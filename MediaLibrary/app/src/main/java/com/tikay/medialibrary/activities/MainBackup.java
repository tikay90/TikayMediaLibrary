package com.tikay.medialibrary.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.db.DatabaseHelper;
import com.tikay.medialibrary.db.DbConstants;
import com.tikay.medialibrary.db.PlaylistDb;
import com.tikay.medialibrary.db.TracksDb;
import com.tikay.medialibrary.fragments.Albums;
import com.tikay.medialibrary.fragments.Artists;
import com.tikay.medialibrary.fragments.Folders;
import com.tikay.medialibrary.fragments.Genre;
import com.tikay.medialibrary.fragments.GridAlbum;
import com.tikay.medialibrary.fragments.GridArtist;
import com.tikay.medialibrary.fragments.Playlists;
import com.tikay.medialibrary.fragments.Tracks;
import com.tikay.medialibrary.models.PlaylistModel;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.service.MediaService;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.List;
import com.tikay.medialibrary.fragments.SingleDualFragment;

public class MainBackup extends AppCompatActivity implements TabLayout.OnTabSelectedListener
{
	//This is our tablayout 
	private TabLayout tabLayout;
	//This is our viewPager
	private ViewPager viewPager;

	private List<Fragment> listFragments;
	private int selectedTab;
	private int pos = 0;
	private static final int REQUEST_PERMISSIONS_RESULT = 101;

	private Intent serviceIntent;
	private String TAG = "MainActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			// 8th nov, 2016
			//This structure I am using to check if my app has permission and than request if it does not. 
			//So in my main code from where i want to check write following :
			int MyVersion = Build.VERSION.SDK_INT;
			if(MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
				if(!alreadyhavePermission()) {
					if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
						Utilities.toastLong(this,"External storage permission required to get Songs on device");
					}
					requestForSpecificPermission();
				}else{
					setup();
				}
			} else { // if less than marshmallow
				setup();
			}
		} catch(Exception e) {
			e.printStackTrace();
			Utilities.toastShort(this, e.getMessage());
		}

	}

	private void setup() {
		try {
			setContentView(R.layout.activity_main);
			Utilities.writeLogcatToFile(TAG);		
			initToolBar();
			initCollapsingToolbar();
			setupStuff();
			serviceIntent = new Intent(MainBackup.this, MediaService.class);
			startService(serviceIntent);
			try {
				new DataBaseTask().execute();
				Utilities.getDeviceInfo();
			} catch(Exception e) {
				Utilities.toastLong(getApplicationContext(), e.toString());
				Log.e(TAG, TAG + " -- Error in onCreate() - @CreateDataBaseTask(): " + e.toString());
			}
		} catch(Exception e) {
			Utilities.toastLong(this, e.getMessage());
			Log.e(TAG, TAG + " >>>>> " + e.getMessage()); 
		}
	}


	private boolean alreadyhavePermission() {
    //int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		int[] permissions = 
		{
			ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE),
			ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE),
			ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE),
			ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO),
			ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS),
			ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK),
			ContextCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS)
		};

    if(permissions[0] != PackageManager.PERMISSION_GRANTED ||
			 permissions[1] != PackageManager.PERMISSION_GRANTED ||
			 permissions[2] != PackageManager.PERMISSION_GRANTED ||
			 permissions[3] != PackageManager.PERMISSION_GRANTED ||
			 permissions[4] != PackageManager.PERMISSION_GRANTED ||
			 permissions[5] != PackageManager.PERMISSION_GRANTED ||
			 permissions[6] != PackageManager.PERMISSION_GRANTED) {
			return false;
    } else {
			return true;
    }
	}
	//Module requestForSpecificPermission() is implemented as :
	private void requestForSpecificPermission() {
		String [] perms = 
		{
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.MODIFY_AUDIO_SETTINGS,
			Manifest.permission.WAKE_LOCK,
			Manifest.permission.READ_LOGS
		};
		ActivityCompat.requestPermissions(this, perms, REQUEST_PERMISSIONS_RESULT);
	}

	//and Override in Activity :
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantpermissions) {
    switch(requestCode) {
			case REQUEST_PERMISSIONS_RESULT:
				if(grantpermissions.length > 0 && grantpermissions[0] == PackageManager.PERMISSION_GRANTED) {
					setup();
				} else {
					try {
						Utilities.toastShort(this, "Cannot run application because External storage permission is not granted");
						//finish();
					} catch(Exception e) {
						Utilities.toastShort(this, "In onRequestPermissionsResult\t" + e.getMessage());
					}
				}
				break;

			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantpermissions);
    }
	}




	@Override
	protected void onResume() {
		// TODO: Implement this method
		super.onResume();

	}



	private void setupStuff() {
		listFragments = new ArrayList<Fragment>();
		listFragments.add(new Tracks());
		listFragments.add(new Albums());
		listFragments.add(new Artists());
		listFragments.add(new Folders());
		listFragments.add(new Playlists());
		listFragments.add(new Genre());
		listFragments.add(new GridAlbum());
		listFragments.add(new GridArtist());
		listFragments.add(new SingleDualFragment());

		viewPager = (ViewPager) findViewById(R.id.pager);
		tabLayout = (TabLayout) findViewById(R.id.tabLayout);
		//Adding the tabs using addTab() method
		tabLayout.addTab(tabLayout.newTab().setText("Tracks"));
		tabLayout.addTab(tabLayout.newTab().setText("Albums"));
		tabLayout.addTab(tabLayout.newTab().setText("Artists"));
		tabLayout.addTab(tabLayout.newTab().setText("Folders"));
		tabLayout.addTab(tabLayout.newTab().setText("Playlists"));
		tabLayout.addTab(tabLayout.newTab().setText("Genres"));
		tabLayout.addTab(tabLayout.newTab().setText("Albums"));
		tabLayout.addTab(tabLayout.newTab().setText("Artists"));
		tabLayout.addTab(tabLayout.newTab().setText("SingleDualFragment"));
		tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
		tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
		tabLayout.setSelectedTabIndicatorHeight(2);
		tabLayout.setTabTextColors(Color.WHITE, Color.CYAN);

		Pager adapter = new Pager(getSupportFragmentManager(), listFragments);
		viewPager.setAdapter(adapter);
		//tabLayout.setupWithViewPager(viewPager);
		tabLayout.setOnTabSelectedListener(this);
		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

		restorePrefs();

	}

	@Override
	public void onTabSelected(TabLayout.Tab tab) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(TabLayout.Tab p1) {
		// TODO: Implement this method
	}

	@Override
	public void onTabReselected(TabLayout.Tab p1) {
		// TODO: Implement this method
	}


	private void initToolBar() {
		Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
		if(toolBar != null)
			setSupportActionBar(toolBar);

		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null) {
			actionBar.setTitle("Media Library");
			//getSupportActionBar().setTitle(getString(R.string.app_name));
		}
	}

	/**
	 * Initializing collapsing toolbar
	 * Will show and hide the toolbar title on scroll
	 */
	private void initCollapsingToolbar() {
		final CollapsingToolbarLayout collapsingToolbar =
			(CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitleEnabled(false);
		AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
		appBarLayout.setExpanded(true);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//getSupportFragmentManager().putFragment(outState, "home", fragHome);
		super.onSaveInstanceState(outState);
	}



	@Override
	protected void onStop() {
		Log.i("MainActivity", "IN MainActivity onStop() CALLED");
		SharedPreferences preferences = getSharedPreferences(MainActivity.class.getSimpleName(), MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putInt("selectedTab", tabLayout.getSelectedTabPosition());
		editor.putInt("pos", pos);

		editor.apply();
		super.onStop();
	}

	private void restorePrefs() {
		// Get selected Tab position
    SharedPreferences preferences = getSharedPreferences(MainActivity.class.getSimpleName(), MODE_PRIVATE);
		selectedTab = preferences.getInt("selectedTab", 0);
		pos = preferences.getInt("pos", 0);
		viewPager.setCurrentItem(selectedTab);
	}

	@Override
	protected void onDestroy() {
		Log.i("MainActivity", "IN MainActivity onDestroy() CALLED");
		super.onDestroy();
	}


	long lastPressedTime ;
	@Override
	public void onBackPressed() {
		int count = getSupportFragmentManager().getBackStackEntryCount();

    if (count == 0) {
			int TIME_INTERVAL = 2000;
			if(lastPressedTime + TIME_INTERVAL > System.currentTimeMillis()) {
				Log.i("MainActivity", "In MainActivity   >>>>> onBackPressed CALLED ;)");
				stopService(serviceIntent);
				if(ImageLoader.getInstance().isInited()) {
					Log.i("MainActivity", "In MainActivity   >>>>> ImageLoader is inited ;)");
					//Utilities.toastLong(MainActivity.this, "In MainActivity   >>>>> ImageLoader is inited ;)" );
					ImageLoader.getInstance().stop();
				}
				super.onBackPressed();
			} else {
				Utilities.toastShort(this, "Press again to exit");
			}

			lastPressedTime = System.currentTimeMillis();
    } else {
			getSupportFragmentManager().popBackStack();
    }

	}


	/*
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	 getMenuInflater().inflate(R.menu.main_menu, menu);
	 return true;
	 }
	 */

	private void createDefaultPlaylist() {
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		PlaylistDb playlistDb = PlaylistDb.getInstance(dbHelper);
		String[] defaultPlaylist = DbConstants.DEFAULT_PLAYLIST;
		List<PlaylistModel> playlists = playlistDb.getAllPlaylist();
		Log.e("DataBase Task", "default playlist size: " + playlists.size());
		if(playlists.size() < defaultPlaylist.length) {
			Log.e(TAG, " adding playlist to - " + DbConstants.DEFAULT_PLAYLIST_TABLE + " ...  ");
			for(int i = 0;i < defaultPlaylist.length;i++) {
				PlaylistModel playlist = new PlaylistModel();
				playlist.setID(i);
				playlist.setName(defaultPlaylist[i]);
				playlist.setType(Constants.FIRST_ROW);
				playlistDb.addPlaylist(playlist);
			}

		} else if(playlists.size() > defaultPlaylist.length) {
			playlistDb.deleteDb(getApplicationContext());
			playlistDb = PlaylistDb.getInstance(dbHelper);
			Log.e(TAG, " adding playlist to - " + DbConstants.DEFAULT_PLAYLIST_TABLE + " ...  ");
			for(int i = 0;i < defaultPlaylist.length;i++) {
				PlaylistModel pl = new PlaylistModel();
				pl.setID(i);
				pl.setName(defaultPlaylist[i]);
				pl.setType(Constants.FIRST_ROW);
				playlistDb.addPlaylist(pl);
			}
		}
		dbHelper.close();
	}

	private void addTracksToDataBase(ArrayList<TracksModel> input) {
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(getApplicationContext());
		TracksDb tracksDb = TracksDb.getInstance(dbHelper);
		List<TracksModel> dbTracks = tracksDb.getAllTracks();
		//Log.e("DataBase Task", "dbTracks size: " + dbTracks.size());
		int tt = dbTracks == null ? 0: dbTracks.size();
		if(input != null) {
			if(tt < input.size() || tt > input.size()) {
				tracksDb.deleteDb(getApplicationContext());
				tracksDb = TracksDb.getInstance(dbHelper);
				Log.e(TAG, " adding tracks to " + DbConstants.PLAYLIST_TRACKS_TABLE + " ...  ");
				for(TracksModel track : input) {
					tracksDb.addTracks(track);
				}
			} else {
				Log.i("DataBase Task", "db tracks equals device tracks... ");
			}
		}
		dbHelper.close();
	}


	private final class DataBaseTask extends AsyncTask<Void,Integer,ArrayList<TracksModel>>
	{
		@Override
		protected ArrayList<TracksModel> doInBackground(Void... p1) {
			return MyMediaQuery.getAllTracks(getApplicationContext());
		}

		@Override
		protected void onPostExecute(ArrayList<TracksModel> result) {
			super.onPostExecute(result);
			Log.e(TAG, "RESULT = " + result.size());
			new CreateDataBaseTask().execute(result);
		}
	}

	private final class CreateDataBaseTask extends AsyncTask<ArrayList<TracksModel> ,Void,String>
	{
		@Override
		protected String doInBackground(ArrayList<TracksModel>[] input) {
			addTracksToDataBase(input[0]);
			createDefaultPlaylist();
			return "Default Playlists Added to Database";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.e(TAG, TAG+": " + result);
			//Utilities.toastLong(getApplicationContext(),result);
		}

	}


	public class Pager extends FragmentStatePagerAdapter
	{
		List<Fragment> listFrags;

		/** my adapter **/
		Pager(FragmentManager fm, List<Fragment> list) {
			super(fm);
			this.listFrags = list;
		}

		@Override
		public int getCount() {
			// TODO: Implement this method
			return listFrags.size();
		}

		@Override
		public Fragment getItem(int position) {
			return listFrags.get(position);
		}

	}

}

// removed from initCollapsingToolbar
// hiding & showing the title when toolbar expanded & collapsed
		/*appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
		 boolean isShow = false;
		 int scrollRange = -1;

		 @Override
		 public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
		 if(scrollRange == -1) {
		 scrollRange = appBarLayout.getTotalScrollRange();
		 }
		 if(scrollRange + verticalOffset == 0) {
		 collapsingToolbar.setTitle(getString(R.string.app_name));
		 isShow = true;
		 } else if(isShow) {
		 collapsingToolbar.setTitle(" ");
		 isShow = false;
		 }
		 }
		 });*/



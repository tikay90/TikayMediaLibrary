package com.tikay.medialibrary.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.RelativeLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.fragments.Albums;
import com.tikay.medialibrary.fragments.Artists;
//import com.tikay.medialibrary.fragments.Folders;
import com.tikay.medialibrary.fragments.FragmentHome;
import com.tikay.medialibrary.fragments.Playlists;
import com.tikay.medialibrary.fragments.Tracks;
import com.tikay.medialibrary.service.MediaService;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.List;
import com.tikay.medialibrary.fragments.AlbumRecycler;
import com.tikay.medialibrary.fragments.Genre;

public class NewActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener
{
	//This is our tablayout 
	private TabLayout tabLayout;
	//This is our viewPager
	private ViewPager viewPager;

	private FragmentManager fm ;
	private List<Fragment> listFragments;
	private DrawerLayout drawerLayout;
	private RelativeLayout drawerPane;
	private int selectedTab;
	private int pos = 0;

	private Intent serviceIntent;
	private FragmentHome fragHome;
	private String TAG = "MainActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
		setContentView(R.layout.main);
		Utilities.writeLogcatToFile(TAG,TAG);		
			//initToolBar();
			//initHomeFragment(savedInstanceState);
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			getSupportActionBar().setTitle(getString(R.string.app_name));
			initCollapsingToolbar();
			setupStuff();
			serviceIntent = new Intent(NewActivity.this, MediaService.class);
			startService(serviceIntent);
		} catch(Exception e) {
			Utilities.toastLong(this, e.getMessage());
			Log.e(TAG, TAG + " >>>>> " + e.getMessage()); 
		}

	}

	private void setupStuff() {
		listFragments = new ArrayList<Fragment>();
		listFragments.add(new Tracks());
		listFragments.add(new Albums());
		listFragments.add(new Artists());
		//listFragments.add(new Folders());
		listFragments.add(new Playlists());
		listFragments.add(new Genre());
		listFragments.add(new AlbumRecycler());
		
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
		tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
		tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
		tabLayout.setSelectedTabIndicatorHeight(5);
		tabLayout.setTabTextColors(Color.WHITE,Color.YELLOW);
		
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
		Toolbar toolBar = (Toolbar) findViewById(R.id.tool_bar);
		if(toolBar != null)
			setSupportActionBar(toolBar);

		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null) {
			actionBar.setTitle("Media Library");
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
	}

	private void initHomeFragment(Bundle savedInstanceState) {
		try {
			fm = getSupportFragmentManager();
			/*	fragHome = (Home)fm.findFragmentByTag("home");
			 if(fragHome == null) {
			 //Utilities.toastShort(this, "if clause ==> myhomefrag is null");
			 fragHome = new Home();
			 fm.beginTransaction().add(fragHome, "home").commit();
			 }//else{
			 //Utilities.toastShort(this, "else clause ==> myhomefrag not null");
			 fragHome = new Home();
			 fm.beginTransaction().replace(R.id.main_content, new Home()).commit();
			 //}
			 */

			if(savedInstanceState == null) {
				fragHome = new FragmentHome();
				//fm.beginTransaction().replace(R.id.main_content, fragHome,"home").commit();
				//fm.beginTransaction().add(R.id.main_content, fragHome).commit();
				//Utilities.toastLong(this, "MainActivity    >>>>   savedInstanceState is null");
			} else {
				//Utilities.toastLong(this, "MainActivity    >>>>   savedInstanceState not null");
				//fragHome = (Home)fm.findFragmentByTag("home");
				fragHome = (FragmentHome) fm.getFragment(savedInstanceState, "home");
			}
		//	fm.beginTransaction().replace(R.id.main_content, fragHome).commit();
		} catch(Exception e) {
			Utilities.toastLong(this, e.getMessage());
			Log.e("Parent Activity - init() ", "init() " + e.getMessage());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//getSupportFragmentManager().putFragment(outState, "home", fragHome);
		super.onSaveInstanceState(outState);
	}



	@Override
	protected void onStop() {
		Log.i("MainActivity", "IN MainActivity onStop() CALLED");
		SharedPreferences preferences = getSharedPreferences(NewActivity.class.getSimpleName(), MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putInt("selectedTab", tabLayout.getSelectedTabPosition());
		editor.putInt("pos", pos);

		editor.apply();
		super.onStop();
	}
	
	private void restorePrefs() {
		// Get selected Tab position
    SharedPreferences preferences = getSharedPreferences(NewActivity.class.getSimpleName(), MODE_PRIVATE);
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
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
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

package com.tikay.medialibrary.fragments;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.AlbumModel;
import com.tikay.medialibrary.recycler_adapter.GridAlbumsAdapter;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.GridSpacingItemDecoration;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale;

public class GridAlbum extends Fragment implements OnQueryTextListener
{
	public static final String BROADCAST_ALBUMLIST = "com.tikay.tkplayer.BROADCAST_ALBUMLIST";
	private RecyclerView recyclerView;
	private ArrayList<AlbumModel> albums = new ArrayList<AlbumModel>();
	private SearchView searchView;
	private	View v;
	private	GridAlbumsAdapter adapter;
	private Intent broadcastIntent;
	private boolean serviceBroadcastRegistered;
	private String TAG = GridAlbum.class.getSimpleName();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utilities.writeLogcatToFile(TAG);
		Log.i(TAG, "IN Albums onCreate() CALLED");
		//Utilities.toastShort(getContext(), "Albums ==> onCreate() CALLED");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, TAG + " onSaveInstanceState CALLED");
		outState.putParcelableArrayList("albums", albums);
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Utilities.toastShort(getContext(), "Albums ==> onCreateView() CALLED");
		Log.i(TAG, "IN Albums onCreateView() CALLED");
		v = inflater.inflate(R.layout.album_recycler, container, false);
		initPrimary();

		return v;
	}

	private void initPrimary() {
		recyclerView = (RecyclerView) v.findViewById(R.id.album_recycler_view);
		//recyclerView.addOnItemTouchListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
		registerServiceBroadcast();

		try {
			if(savedInstanceState == null) {
				new AlbumsTask().execute();
				//new AlbumsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				albums = savedInstanceState.getParcelableArrayList("albums");
				initRecyclerView(albums);
			}
		} catch(Exception e) {
			Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e(TAG, "ERROR In Albums --- " + e.getMessage());
		}
	}

	private void initRecyclerView(ArrayList<AlbumModel> albums) {
		if(albums != null) {
			adapter = new GridAlbumsAdapter(getActivity().getApplicationContext(), albums);
			RecyclerView.LayoutManager mLayoutManager = null;
			boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
			int dpToPx = Utilities.dpToPx(getActivity(),2);
			if(tabletSize) { // tablet
				//Utilities.toastShort(getContext(), "Tablet");
				switch(Utilities.getScreenOrientation(getActivity())){
					case 1:
						//Utilities.toastShort(getContext(), "Portrait Mode");
						mLayoutManager = new GridLayoutManager(getActivity(), 4);
						recyclerView.setLayoutManager(mLayoutManager);
						recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx, true));
						break;
					case 2:
						//Utilities.toastShort(getContext(), "Landscape mode");
						mLayoutManager = new GridLayoutManager(getActivity(), 5);
						recyclerView.setLayoutManager(mLayoutManager);
						recyclerView.addItemDecoration(new GridSpacingItemDecoration(5, dpToPx, true));
						break;
				}
			} else { // phone
				//Utilities.toastShort(getContext(), "Phone");
				switch(Utilities.getScreenOrientation(getActivity())){
					case 1:
						//Utilities.toastShort(getContext(), "Portrait Mode");
						mLayoutManager = new GridLayoutManager(getActivity(), 3);
						recyclerView.setLayoutManager(mLayoutManager);
						recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx, true));
						break;
					case 2:
						//Utilities.toastShort(getContext(), "Landscape mode");
						mLayoutManager = new GridLayoutManager(getActivity(), 4);
						recyclerView.setLayoutManager(mLayoutManager);
						recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx, true));
						break;
				}
			}
			recyclerView.setItemAnimator(new DefaultItemAnimator());
			recyclerView.setAdapter(adapter);
			
		} else {
			Utilities.toastShort(getContext(), "In OCV albums = null");

		}
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search_menu, menu);

		searchView = (SearchView) menu.findItem(R.id.search_view_action).getActionView();  
		//Sets searchable configuration defined in searchable.xml for this SearchView
		SearchManager searchManager =  (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));  

		searchView.setOnQueryTextListener(GridAlbum.this);
	}

	@Override
	public void onStop() {
		//Utilities.toastShort(getContext(), "Albums ==> onStop() CALLED");
		Log.i("Albums", "IN Albums onStop() CALLED");
		// Save scroll position
		SharedPreferences preferences = getActivity().getSharedPreferences(Constants.ALBUMS, getActivity().MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		//int index = recyclerView.g;
		//View v = lv.getChildAt(0);
		//int top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());

		//editor.putInt("albumPosition", index);
		//editor.putInt("up", top);
		//editor.putString("MURI", mUri);
		//editor.putString("SONG_URI", song.getSongUri().toString());
		//editor.apply();
		super.onStop();

	}

	private BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context ctx, Intent intent) {
			String action = intent.getStringExtra(Constants.RECEIVE);
			switch(action) {
				case "tracks":
					Utilities.toastLong(getActivity(), " In TRACKS --- received");
					break;

				case "albums":
					Utilities.toastLong(getActivity(), "In ALBUMS --- received");
					break;

				case "playlists":

					break;

				case "folders": 

					break;
			}

		}
  };

	// register noti broadcast
  private void registerServiceBroadcast() {
		if(!serviceBroadcastRegistered) {
			getActivity().registerReceiver(serviceBroadcastReceiver, new IntentFilter(Constants.BROADCAST_RECEIVED));
			serviceBroadcastRegistered = true;
		}
  }

  // unregister noti broadcast receiver
  private void unregisterServiceBroadcast() {
		if(serviceBroadcastRegistered) {
			getActivity().unregisterReceiver(serviceBroadcastReceiver);
			serviceBroadcastRegistered = false;
		}
  }

	/*
	 @Override
	 public void onStart() {
	 //Utilities.toastShort(getContext(), "Albums ==> onStart() CALLED");
	 super.onStart();


	 Utilities.toastLong(getActivity(), "In Service " + e.getMessage());
	 Log.e(TAG, "In Service " + e.getMessage());
	 }
	 }
	 */



	@Override
	public void onResume() {
		//Utilities.toastShort(getContext(), "Albums ==> onResume() CALLED");
		Log.i("ALBUMS", "IN ALBUMS onResume() CALLED");
		super.onResume();
	}



	public void destroySelf() {
		getActivity().finish();
		Log.i(Constants.ALBUMS, "IN Albums destroySelf CALLED");
	}

	@Override
	public void onDestroyView() {
		Log.i("Albums", "IN Albums onDestroyView() CALLED");
		unregisterServiceBroadcast();
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		//Utilities.toastShort(getContext(), "Albums ==> onDestroy() CALLED");
		Log.i(Constants.ALBUMS, "IN Albums -- Albums DESTROYED");
		super.onDestroy();
	}

	@Override
	public boolean onQueryTextSubmit(String text) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onQueryTextChange(String query) {
		String input = query.toString().toLowerCase(Locale.getDefault());
		adapter.filter(input);
		adapter.setSongsList(albums);
		return false;
	}



	private class AlbumsTask extends AsyncTask<Void,Integer,ArrayList<AlbumModel>>
	{
		@Override
		protected ArrayList<AlbumModel> doInBackground(Void... p1) {
			return MyMediaQuery.getAlbums(getContext());
		}


		@Override
		protected void onPostExecute(ArrayList<AlbumModel> result) {
			super.onPostExecute(result);

			try {
				albums = result;
				//tracks_No.setText(albums.size() + " Albums");
				initRecyclerView(albums);
			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}

	}


}

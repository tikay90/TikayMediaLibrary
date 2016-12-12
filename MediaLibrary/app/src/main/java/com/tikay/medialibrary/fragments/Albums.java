package com.tikay.medialibrary.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.activities.AlbumTracks;
import com.tikay.medialibrary.fragments.Albums;
import com.tikay.medialibrary.models.AlbumModel;
import com.tikay.medialibrary.recycler_adapter.AlbumAdapter;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class Albums extends Fragment implements /* OnItemClickListener,*/OnQueryTextListener,RecyclerView.OnItemTouchListener
{
	public static final String BROADCAST_ALBUMLIST = "com.tikay.tkplayer.BROADCAST_ALBUMLIST";
	private RecyclerView recyclerView;
	private ArrayList<AlbumModel> albums = new ArrayList<AlbumModel>();
	private SearchView searchView;
	private	View v;
	//private TextView tracks_No;
	private	AlbumAdapter adapter;
	private Intent broadcastIntent;
	private String TAG = "ALBUMS";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		writeLogcatToFile();
		Log.i("Albums", "IN Albums onCreate() CALLED");
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
		Log.i("Albums", "IN Albums onCreateView() CALLED");
		v = inflater.inflate(R.layout.album_recycler, container, false);
		initPrimary();

		return v;
	}
	
	GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

			@Override 
			public boolean onSingleTapUp(MotionEvent e) {
				return true;
			}

		});

	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

		View child = rv.findChildViewUnder(e.getX(), e.getY());
		if(child != null && gestureDetector.onTouchEvent(e)) {
			int position = rv.getChildAdapterPosition(child);
			//Utilities.toastShort(getActivity(), albums.get(position).getAlbumName());

			AlbumModel item = albums.get(position);
			long albumId = item.getAlbumId();
			String albumName = item.getAlbumName();

			Intent intent = new Intent(getActivity(), AlbumTracks.class);
			intent.putExtra("name", albumName).putExtra("id", albumId);

			//broadcastIntent.putExtra(Constants.SEND, "albums").putExtra("name", albumName);
			//getActivity().sendBroadcast(broadcastIntent);

			startActivity(intent);
		}
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {
		// TODO: Implement this method
	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disAllowIntercept) {
		// TODO: Implement this method
	}
	
	private void initPrimary() {
		recyclerView = (RecyclerView) v.findViewById(R.id.album_recycler_view);
		recyclerView.addOnItemTouchListener(this);
	}
	
	private void initRecyclerView(ArrayList<AlbumModel> albums) {
		if(albums != null) {
			adapter = new AlbumAdapter(getActivity().getApplicationContext(), albums);
			RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
			recyclerView.setLayoutManager(mLayoutManager);
			//recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
			recyclerView.setItemAnimator(new DefaultItemAnimator());
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);
			//searchView.setOnQueryTextListener(Albums.this);
		} else {
			Utilities.toastShort(getContext(), "In OCV albums = null");

		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
		broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);

		try {
			if(savedInstanceState == null) {
				new AlbumsTask().execute();
				//new AlbumsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				albums = savedInstanceState.getParcelableArrayList("albums");
				if(albums != null) {
					//tracks_No.setText(albums.size() + " Albums");
					initRecyclerView(albums);
					//searchView.setOnQueryTextListener(Albums.this);
				} else {
					Utilities.toastShort(getContext(), "In OCV albums = null");

				}
			}
		} catch(Exception e) {
			Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("ERROR", "In Albums --- " + e.getMessage());
		}
	}



/*
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		try{
		AlbumModel item = albums.get(position);
		long albumId = item.getAlbumId();
		String albumName = item.getAlbumName();

		Intent intent = new Intent(getActivity(), AlbumTracks.class);
		intent.putExtra("name", albumName).putExtra("id", albumId);

		//broadcastIntent.putExtra(Constants.SEND, "albums").putExtra("name", albumName);
		//getActivity().sendBroadcast(broadcastIntent);

		startActivity(intent);
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
			Utilities.toastShort(getContext(),e.getMessage());
		}

		//Utilities.toastShort(getActivity(),"Id = " + albumId + " Name: "+albumName);

	}
	*/

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search_menu,menu);

		searchView = (SearchView) menu.findItem(R.id.search_view_action).getActionView();  
		//Sets searchable configuration defined in searchable.xml for this SearchView
		SearchManager searchManager =  (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));  

		searchView.setOnQueryTextListener(Albums.this);
	}
	
	private void writeLogcatToFile() {
		Log.e(TAG, ">>>>>>>  printLogcat() <<<<<<<");
		String root_sd = Environment.getExternalStorageDirectory().toString();
		File mylogs = new File(root_sd, ".TikayLogs");
		String name = "Albums.log";
		if(!mylogs.exists()) {
			if(Utilities.isSdCardPresent()) {
				mylogs.mkdir();
			}
		}
		final long maxFileSize = 1024 * 100;  // 100KB
		File logFile = new File(mylogs, name);
		if(logFile.length() >= maxFileSize) {
			logFile.delete();
			Log.e(TAG, logFile.getName() + " deleted");
			//Utilities.toastShort(this, logFile.getName()+" deleted");
			logFile = new File(mylogs, name);
		}
		try {
			Process process = Runtime.getRuntime().exec("logcat -c");
			process = Runtime.getRuntime().exec("logcat -v time -f "  + /*mylogs.getAbsolutePath()+"/tikay_log.log"*/logFile.getAbsolutePath());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStop() {
		//Utilities.toastShort(getContext(), "Albums ==> onStop() CALLED");
		Log.i("Albums", "IN Albums onStop() CALLED");
		// Save scroll position
		//SharedPreferences preferences = getActivity().getSharedPreferences(Constants.ALBUMS, getActivity().MODE_PRIVATE);
		//SharedPreferences.Editor editor = preferences.edit();
		
		/*
		int index = lv.getFirstVisiblePosition();
		View v = lv.getChildAt(0);
		int top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());

		editor.putInt("albumPosition", index);
		editor.putInt("up", top);
		editor.putString("MURI", mUri);
		//editor.putString("SONG_URI", song.getSongUri().toString());
		editor.apply();
		*/
		super.onStop();

	}

	

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
				SharedPreferences preferences = getActivity().getSharedPreferences(Constants.ALBUMS, getActivity().MODE_PRIVATE);
				int index = preferences.getInt("albumPosition", 0);
				int top = preferences.getInt("up", 0);

				initRecyclerView(albums);
				//searchView.setOnQueryTextListener(Albums.this);
			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}

	}


}


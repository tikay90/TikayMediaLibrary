package com.tikay.medialibrary.fragments;

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
import android.widget.Toast;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.fragments.Tracks;
import com.tikay.medialibrary.models.AlbumModel;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.recycler_adapter.TracksAdapter;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class Tracks extends Fragment implements /*OnItemClickListener,*/OnQueryTextListener,RecyclerView.OnItemTouchListener
{
	public static final String BROADCAST_PLAYLIST = "com.tikay.tkplayer.BROADCAST_PLAYLIST";
	private ArrayList<TracksModel> tracks = new ArrayList<TracksModel>();
	private RecyclerView recyclerView;

	//private TextView tracks_No;
	private TracksAdapter adapter;
	private boolean serviceBroadcastRegistered = false;
	private SearchView searchView;
	private View v;
	private Intent broadcastIntent;

	private String TAG = "TRACKS";
	private TracksTask myTask;
	//private final String TRACKS_GSON = "TRACKS_GSON";
	//private String tracksGson = "";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		writeLogcatToFile();
		Log.i(Constants.Tracks, "IN Tracks onCreate() CALLED");
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(Constants.Tracks, "IN Tracks onCreateView() CALLED");
		v = inflater.inflate(R.layout.universal_recycler_view, container, false);
		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		try {
			broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
			initPrimary();
			myTask = new TracksTask();
			if(savedInstanceState == null) {
				//Utilities.toastShort(getContext(), "In OCV --- savedInstanceState is null ");
				myTask.execute();
			} else { //-- savedInstanceState not null
				tracks = savedInstanceState.getParcelableArrayList("tracks");
				if(tracks != null) {
					initRecyclerView(tracks);
				} else {
					Utilities.toastShort(getContext(), "In OCV tracks = null");
					//myTask.execute();
				}
			}
		} catch(Exception e) {
			Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("ERROR", "File Reading Error", e);
		}
	}

	private void initRecyclerView(ArrayList<TracksModel> albums) {
		if(albums != null) {
			//tracks_No.setText(albums.size() + " Albums");
			adapter = new TracksAdapter(getActivity().getApplicationContext(), tracks);
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
	public void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, TAG + " onSaveInstanceState CALLED");
		//Utilities.toastShort(getContext(), "Tracks ==> onSaveInstanceState() CALLED");
		outState.putParcelableArrayList("tracks", tracks);
		super.onSaveInstanceState(outState);
	}

	

	private void initPrimary() {
		//tracks_No = (TextView)v.findViewById(R.id.tvAlbums);
		//searchView = (SearchView)v.findViewById(R.id.svA);
		recyclerView = (RecyclerView) v.findViewById(R.id.universal_rv);
		recyclerView.addOnItemTouchListener(this);
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

			TracksModel item = tracks.get(position);
			String name = item.getSongName();
			int pos = item.getRealPosition();

			broadcastIntent.putExtra(Constants.SEND, "tracks").putExtra("pos", pos);
			getActivity().sendBroadcast(broadcastIntent);
			Constants.CURRENT_TRACKS.clear();
			Constants.CURRENT_TRACKS.addAll(Constants.TRACKS);
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search_menu,menu);
		
		searchView = (SearchView) menu.findItem(R.id.search_view_action).getActionView();  
		//Sets searchable configuration defined in searchable.xml for this SearchView
		SearchManager searchManager =  (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));  

		searchView.setOnQueryTextListener(Tracks.this);
	}

	private void writeLogcatToFile() {
		Log.e(TAG, ">>>>>>>  printLogcat() <<<<<<<");
		String root_sd = Environment.getExternalStorageDirectory().toString();
		File mylogs = new File(root_sd, ".TikayLogs");
		String name = "Tracks.log";
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
		Log.i(Constants.Tracks, "IN Tracks - onStop() CALLED");
		try {
			/*Gson mgson = new Gson();
			 Type type = new TypeToken<ArrayList<TracksModel>>() {}.getType();
			 tracksGson = mgson.toJson(tracks, type);
			 Utilities.toastShort(getContext(), "Tracks ==> onStop() CALLED");*/
			//Log.i(Constants.Tracks, "IN Tracks onStop() CALLED"); 
			// Save scroll position
			/*SharedPreferences preferences = getActivity().getSharedPreferences(Constants.Tracks, getActivity().MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			int index = lv.getFirstVisiblePosition();
			View v = lv.getChildAt(0);
			int top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());

			editor.putInt("trackPosition", index);
			editor.putInt("top", top);
			//editor.putString("MURI", mUri);
			//editor.putString(TRACKS_GSON, tracksGson);
			editor.apply(); */

		} catch(Exception e) {
			Log.e(TAG, "In Tracks onStop() -- error: " + e.getMessage());
		}
		super.onStop();
	}


	
	/*
	 @Override
	 public void onStart() {
	 //Utilities.toastShort(getContext(), "Tracks ==> onStart() CALLED");
	 super.onStart();

	 //broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
	 try {
	 registerServiceBroadcast();
	 } catch(Exception e) {
	 Utilities.toastLong(getActivity(), "In Tracks  onStart() --  " + e.getMessage());
	 Log.e(TAG, "In Service " + e.getMessage());
	 }

	 }
	 */

	
	@Override
	public void onResume() {
		//Utilities.toastShort(getContext(), "Tracks ==> onResume() CALLED");
		Log.i(Constants.Tracks, "IN Tracks - onResume() CALLED");
		super.onResume();

		/* //new TracksTask().execute();
		 // Get thewer scroll position
		 SharedPreferences preferences = getActivity().getSharedPreferences(Constants.Tracks, getActivity().MODE_PRIVATE);
		 int index = preferences.getInt("trackPosition", 0);
		 int top = preferences.getInt("top", 0);
		 lv.setSelectionFromTop(index, top);*/
	}



	public void destroySelf() {
		getActivity().finish();
		Log.i(Constants.Tracks, "IN Tracks - destroySelf CALLED");
	}

	@Override
	public void onDestroyView() {
		Log.i(Constants.Tracks, "IN Tracks - onDestroyView() CALLED");
		super.onDestroyView();
	}



	@Override
	public void onDestroy() {
		Log.i(Constants.Tracks, "IN Tracks - Tracks DESTROYED");
		super.onDestroy();
	}



	//*****************************///*****************//***
	@Override
	public boolean onQueryTextSubmit(String text) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onQueryTextChange(String query) {
		String input = query.toString().toLowerCase(Locale.getDefault());
		adapter.filter(input);
		adapter.setSongsList(tracks);
		return false;
	}


	private class TracksTask extends AsyncTask<Void,Integer,ArrayList<TracksModel>>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}


		@Override
		protected ArrayList<TracksModel> doInBackground(Void... p1) {
			ArrayList<TracksModel> listOfSongs = MyMediaQuery.getTrack(getContext());

			return listOfSongs;
		}

		@Override
		protected void onPostExecute(ArrayList<TracksModel> result) {
			super.onPostExecute(result);
			try {
				tracks = result;
				//tracks_No.setText(tracks.size() + " TRACKS");
				/*adapter = new TracksAdapter(getActivity().getApplicationContext(), tracks);

				SharedPreferences preferences = getActivity().getSharedPreferences(Constants.Tracks, getActivity().MODE_PRIVATE);
				int index = preferences.getInt("trackPosition", 0);
				int top = preferences.getInt("top", 0);

				lv.setAdapter(adapter);
				lv.setSelectionFromTop(index, top);

				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(Tracks.this);
				//searchView.setOnQueryTextListener(Tracks.this);
				*/
				initRecyclerView(tracks);
			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}

	}



}



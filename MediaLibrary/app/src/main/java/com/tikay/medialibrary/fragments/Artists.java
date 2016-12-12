package com.tikay.medialibrary.fragments;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.activities.ArtistTracks;
import com.tikay.medialibrary.models.ArtistModel;
import com.tikay.medialibrary.recycler_adapter.ArtistAdapter;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class Artists extends Fragment implements /*OnItemClickListener,*/OnQueryTextListener,RecyclerView.OnItemTouchListener
{
	public static final String BROADCAST_ALBUMLIST = "com.tikay.tkplayer.BROADCAST_ALBUMLIST";
	//private ListView lv;
	private RecyclerView recyclerView;
	private ArrayList<ArtistModel> artistList = new ArrayList<ArtistModel>();
	private SearchView searchView;
	private View v;
	//private TextView tvTotalArtists;
	private	ArtistAdapter adapter;
	private String TAG = "Artists";
	private String KEY = "key";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		writeLogcatToFile();
		Log.i(TAG, "IN PLAYLIST onCreate() CALLED");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, TAG + " onSaveInstanceState CALLED");
		outState.putParcelableArrayList(KEY, artistList);
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("PLAYLIST", "IN PLAYLIST onCreateView() CALLED");
		v = inflater.inflate(R.layout.universal_recycler_view, container, false);
		initPrimary();

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		try {
			if(savedInstanceState == null) {
				new ArtistTask().execute();
			} else {
				artistList = savedInstanceState.getParcelableArrayList(KEY);
				if(artistList != null)
					initRecyclerView(artistList);
			}
		} catch(Exception e) {
			Log.e(TAG, " ERROR ----  " + e.getMessage());
		}

	}


	private void restoreState(Bundle savedInstanceState) {
		artistList = savedInstanceState.getParcelableArrayList(KEY);
		if(artistList != null) {
			adapter = new ArtistAdapter(getActivity().getApplicationContext(), artistList);
			/*lv.setAdapter(adapter);
			 lv.setTextFilterEnabled(true);
			 lv.setOnItemClickListener(Artists.this);*/
		}
	}

	private void initPrimary() {
		recyclerView = (RecyclerView) v.findViewById(R.id.universal_rv);
		recyclerView.addOnItemTouchListener(this);
	}

	private void initRecyclerView(ArrayList<ArtistModel> artistsList) {
		if(artistsList != null) {
			adapter = new ArtistAdapter(getActivity(), artistList);
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
			long ID = artistList.get(position).getArtistID();
			String name = artistList.get(position).getArtistName();
			//Utilities.toastShort(getContext(), name + "\n" + ID);
			Intent intent = new Intent(getActivity(), ArtistTracks.class);
			intent.putExtra("id", ID).putExtra("name", name);

			//broadcastIntent.putExtra(Constants.SEND, "playlist").putExtra("id", ID);
			//getActivity().sendBroadcast(broadcastIntent);

			getActivity().startActivity(intent);
			//getActivity().finish();

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

	/*
	 @Override
	 public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	 //-- 6th sept
	 long ID = artistList.get(position).getArtistID();
	 String name = artistList.get(position).getArtistName();
	 //Utilities.toastShort(getContext(), name + "\n" + ID);
	 Intent intent = new Intent(getActivity(), ArtistTracks.class);
	 intent.putExtra("id", ID).putExtra("name", name);

	 //broadcastIntent.putExtra(Constants.SEND, "playlist").putExtra("id", ID);
	 //getActivity().sendBroadcast(broadcastIntent);

	 getActivity().startActivity(intent);
	 //getActivity().finish();
	 }
	 */


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search_menu, menu);

		searchView = (SearchView) menu.findItem(R.id.search_view_action).getActionView();  
		//Sets searchable configuration defined in searchable.xml for this SearchView
		SearchManager searchManager =  (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));  

		searchView.setOnQueryTextListener(this);
	}

	private void writeLogcatToFile() {
		Log.e(TAG, ">>>>>>>  printLogcat() <<<<<<<");
		String root_sd = Environment.getExternalStorageDirectory().toString();
		File mylogs = new File(root_sd, ".TikayLogs");
		String name = "Artists.log";
		if(!mylogs.exists()) {
			if(Utilities.isSdCardPresent()) {
				mylogs.mkdir();
			}
		}
		final long maxFileSize = 1024 * 100;  // 500KB
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
		//Utilities.toastShort(getContext(), "Playlist ==> onStop() CALLED");
		Log.i(TAG, "IN ARTISTS onStop() CALLED");
		/*// Save scroll position
		 SharedPreferences preferences = getActivity().getSharedPreferences(Constants.ArtistTracks, getActivity().MODE_PRIVATE);
		 SharedPreferences.Editor editor = preferences.edit();

		 int index = lv.getFirstVisiblePosition();
		 View v = lv.getChildAt(0);
		 int top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());

		 editor.putInt("artistPosition", index);
		 editor.putInt("topSpace", top);
		 //editor.putString("MURI", mUri);
		 //editor.putString("SONG_URI", song.getSongUri().toString());
		 editor.apply();*/
		super.onStop();

	}
	@Override
	public void onDestroyView() {
		Log.i(TAG, "IN ARTISTS onDestroyView() CALLED");
		super.onDestroyView();
	}



	@Override
	public void onDestroy() {
		Log.i(TAG, "IN ARTISTS onDestroy() CALLED");

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
		adapter.setSongsList(artistList);
		return false;
	}




	//---- async class
	private class ArtistTask extends AsyncTask<Void,Integer,ArrayList<ArtistModel>>
	{
		@Override
		protected ArrayList<ArtistModel> doInBackground(Void... p1) {
			return MyMediaQuery.getArtist(getContext());
		}

		@Override
		protected void onPostExecute(ArrayList<ArtistModel> result) {
			super.onPostExecute(result);
			
			try {
				artistList = result;
				if(artistList != null)
					initRecyclerView(artistList);
			} catch(Exception e) {
				Log.e(TAG, " ERROR ----  " + e.getMessage());
			}
		}

	}

}

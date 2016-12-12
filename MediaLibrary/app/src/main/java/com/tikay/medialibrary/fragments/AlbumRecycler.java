package com.tikay.medialibrary.fragments;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.activities.AlbumTracks;
import com.tikay.medialibrary.models.AlbumModel;
import com.tikay.medialibrary.recycler_adapter.AlbumsAdapter;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale;

public class AlbumRecycler extends Fragment implements OnQueryTextListener,RecyclerView.OnItemTouchListener
{
	public static final String BROADCAST_ALBUMLIST = "com.tikay.tkplayer.BROADCAST_ALBUMLIST";
	private RecyclerView recyclerView;
	private ArrayList<AlbumModel> albums = new ArrayList<AlbumModel>();
	private SearchView searchView;
	private	View v;
	private	AlbumsAdapter adapter;
	private Intent broadcastIntent;
	private boolean serviceBroadcastRegistered;
	private String TAG = "ALBUMS_RECYCLER";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utilities.writeLogcatToFile("AlbumRecycler", TAG);
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
		//tracks_No = (TextView)v.findViewById(R.id.tvAlbums);
		//searchView = (SearchView)v.findViewById(R.id.svA);
		recyclerView = (RecyclerView) v.findViewById(R.id.album_recycler_view);
		recyclerView.addOnItemTouchListener(this);
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
			//tracks_No.setText(albums.size() + " Albums");
			adapter = new AlbumsAdapter(getActivity().getApplicationContext(), albums);
			RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
			recyclerView.setLayoutManager(mLayoutManager);
			recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
			recyclerView.setItemAnimator(new DefaultItemAnimator());
			recyclerView.setAdapter(adapter);
			//searchView.setOnQueryTextListener(Albums.this);
		} else {
			Utilities.toastShort(getContext(), "In OCV albums = null");

		}
	}
	
	private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration
	{

		private int spanCount;
		private int spacing;
		private boolean includeEdge;

		public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
			this.spanCount = spanCount;
			this.spacing = spacing;
			this.includeEdge = includeEdge;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			int position = parent.getChildAdapterPosition(view); // item position
			int column = position % spanCount; // item column

			if(includeEdge) {
				outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
				outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

				if(position < spanCount) { // top edge
					outRect.top = spacing;
				}
				outRect.bottom = spacing; // item bottom
			} else {
				outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
				outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
				if(position >= spanCount) {
					outRect.top = spacing; // item top
				}
			}
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


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search_menu, menu);

		searchView = (SearchView) menu.findItem(R.id.search_view_action).getActionView();  
		//Sets searchable configuration defined in searchable.xml for this SearchView
		SearchManager searchManager =  (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));  

		searchView.setOnQueryTextListener(AlbumRecycler.this);
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
	

	/**
	 * Converting dp to pixel
	 */
	private int dpToPx(int dp) {
		Resources r = getResources();
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
	}


}

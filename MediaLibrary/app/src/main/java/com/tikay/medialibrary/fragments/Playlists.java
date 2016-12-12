package com.tikay.medialibrary.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.activities.PlaylistTracks;
import com.tikay.medialibrary.db.DbConstants;
import com.tikay.medialibrary.db.PlaylistDb;
import com.tikay.medialibrary.models.PlaylistModel;
import com.tikay.medialibrary.recycler_adapter.PlaylistAdapter;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.PlaylistUtils;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale;
import android.database.sqlite.SQLiteOpenHelper;
import com.tikay.medialibrary.db.DatabaseHelper;
import com.tikay.medialibrary.utils.PlaylistCRUD;
import android.content.ContentResolver;

public class Playlists extends Fragment implements OnQueryTextListener,RecyclerView.OnItemTouchListener,
MenuItem.OnMenuItemClickListener
{
	public static final String BROADCAST_ALBUMLIST = "com.tikay.tkplayer.BROADCAST_ALBUMLIST";
	private	Intent albumlistIntent;
	private Intent broadcastIntent;

	private RecyclerView recyclerView;
	private ArrayList<PlaylistModel> playlists = new ArrayList<PlaylistModel>();
	private SearchView searchView;
	private View v;
	private TextView tvCreatePlaylist;
	private int duration;
	private	PlaylistAdapter adapter;
	private String TAG = "Playlists";
	private String KEY = "key";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utilities.writeLogcatToFile(TAG, TAG);
		Log.i(TAG, "IN PLAYLIST onCreate() CALLED");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		albumlistIntent = new Intent(BROADCAST_ALBUMLIST);
		broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, TAG + " onSaveInstanceState CALLED");
		outState.putParcelableArrayList(KEY, playlists);
		super.onSaveInstanceState(outState);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "IN PLAYLIST onCreateView() CALLED");
		v = inflater.inflate(R.layout.universal_recycler_view, container, false);
		initPrimary();

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		if(savedInstanceState == null) {
			//new PlaylistDataBaseTask().execute();
			new PlaylistTask().execute();
		} else {
			restoreState(savedInstanceState);
		}
		//useBoltTask();

	}

	private void restoreState(Bundle savedInstanceState) {
		playlists = savedInstanceState.getParcelableArrayList(KEY);
		if(playlists != null) {
			initRecyclerView(playlists);
		}
	}

	private void initPrimary() {
		recyclerView = (RecyclerView) v.findViewById(R.id.universal_rv);
		recyclerView.addOnItemTouchListener(this);
	}

	private void initRecyclerView(ArrayList<PlaylistModel> playlist) {
		if(playlist != null) {
			//tracks_No.setText(albums.size() + " Albums");
			adapter = new PlaylistAdapter(getActivity().getApplicationContext(), playlist, this);
			RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
			recyclerView.setLayoutManager(mLayoutManager);
			//recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
			recyclerView.setItemAnimator(new DefaultItemAnimator());
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);
			//registerForContextMenu(recyclerView);
			//searchView.setOnQueryTextListener(Albums.this);
		} else {
			Utilities.toastShort(getContext(), "In OCV albums = null");

		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.playlist_menu, menu);

		searchView = (SearchView) menu.findItem(R.id.pl_search_view_action).getActionView();  
		//Sets searchable configuration defined in searchable.xml for this SearchView
		SearchManager searchManager =  (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));  
		searchView.setOnQueryTextListener(this);


	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.create_playlist:
				//PlaylistCRUD.createPlaylistDialog(getActivity());
				createPlaylistDialog();
				//Utilities.toastShort(getActivity().getApplicationContext(), "Create Playlist selected");
				break;
			case R.id.delete:
				Utilities.toastShort(getActivity().getApplicationContext(), "Delete selected");
				break;
			case R.id.select:
				Utilities.toastShort(getActivity().getApplicationContext(), "Select selected");
				break;
			case R.id.settings:
				Utilities.toastShort(getActivity().getApplicationContext(), "Settings selected");
				break;
		}
		return false;
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
			Intent intent;
			PlaylistModel item = playlists.get(position);
			long ID = item.getID();
			String name = item.getName();
			//Utilities.toastShort(getActivity(), "type: " + item.getType()+"\nId: "+item.getID());
			intent = new Intent(getActivity(), PlaylistTracks.class);
			intent.putExtra("id", ID).putExtra("name", name);

			broadcastIntent.putExtra(Constants.SEND, "playlist").putExtra("id", ID);
			getActivity().startActivity(intent);
			//Utilities.toastShort(getActivity(), ""+position);
			//Utilities.toastShort(getActivity(), albums.get(position).getAlbumName());


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
	public boolean onMenuItemClick(MenuItem item) {
		String name = "";
		String pName = adapter.PLAYLIST_NAME;

		switch(item.getOrder()) {
			case 0: // Rename
				name = item.getTitle().toString();
				renamePlaylistDialog();
				break;

			case 1: // Delete
				name = item.getTitle().toString();
				deletePlaylistDialog();
				break;

			case 2: // Details
				name = item.getTitle().toString();
				break;
		}
		//Utilities.toastShort(getActivity(), PlaylistUtils.getPlaylistID(getActivity(),pName)+"");
		//Utilities.toastLong(getActivity(), pName);
		return true;
	}


	//*****************************///*****************//***
	//---- async class
	public class PlaylistTask extends AsyncTask<Void,Integer,ArrayList<PlaylistModel>>
	{
		@Override
		protected ArrayList<PlaylistModel> doInBackground(Void... p1) {
			try {
				SQLiteOpenHelper sql = DatabaseHelper.getInstance(getActivity().getApplicationContext());
				ArrayList<PlaylistModel> userPlaylist = MyMediaQuery.getPlaylists(getContext().getApplicationContext());
				ArrayList<PlaylistModel> defaultPlaylist = PlaylistDb.getInstance(sql).getAllPlaylist();
				sql.close();
				Constants.ALL_PLAYLIST.clear();
				/**Adding userPlaylist to defaultPlaylist  */
				if(userPlaylist != null) {
					for(PlaylistModel pl:userPlaylist) {
						defaultPlaylist.add(pl);
					}
				}
				Constants.ALL_PLAYLIST.addAll(defaultPlaylist);
				return defaultPlaylist;
			} catch(Exception e) {
				Log.e(TAG, e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<PlaylistModel> result) {
			super.onPostExecute(result);
			try {
				playlists = result;
				initRecyclerView(playlists);
			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}

	}

	@Override
	public void onStart() {
		//Utilities.toastShort(getContext(), "Playlist ==> onStart() CALLED");
		super.onStart();
	}



	@Override
	public void onResume() {
		Log.i(TAG, "IN Playlist onResume() CALLED");
		super.onResume();
	}



	public void destroySelf() {
		getActivity().finish();
		Log.i(Constants.ALBUMS, "IN Playlist destroySelf CALLED");
	}

	@Override
	public void onDestroyView() {
		Log.i("PLAYLIST", "IN PLAYLIST onDestroyView() CALLED");
		super.onDestroyView();
	}



	@Override
	public void onDestroy() {
		//Utilities.toastShort(getContext(), "Playlist ==> onDestroy() CALLED");
		Log.i(TAG, "IN  Playlist -- Playlist DESTROYED");

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
		adapter.setSongsList(playlists);
		return false;
	}


	private void createPlaylistDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("New Playlist");
		//builder.setMessage("Enter Playlist Name");
		final EditText etInput = new EditText(getActivity());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.MATCH_PARENT);
		etInput.setLayoutParams(lp);
		etInput.setHint("Enter Playlist Name");
		etInput.setHintTextColor(Color.BLUE);
		builder.setView(etInput);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface p1, int p2) {
					try {
						String playlistName = etInput.getText().toString().trim();
						//createPlaylist(playlistName);
						if(playlistName.trim().length() > 0) {
							PlaylistUtils.createPlaylists(getActivity().getApplicationContext(), playlistName);
							Log.i(TAG, "added " + playlistName);

							new PlaylistTask().execute();
						} else {

						}
					} catch(Exception e) {
						Log.e(TAG, e.toString());
						Utilities.toastLong(getActivity().getApplicationContext(), e.toString());
						e.printStackTrace();
					}
				}
			});

		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2) {
					// TODO: Implement this method
				}
			});

		builder.show();
	}

	private void deletePlaylistDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Delete " + adapter.PLAYLIST_NAME + "?");

		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
				String pName = adapter.PLAYLIST_NAME;
				long id = PlaylistUtils.getPlaylistID(getActivity(), pName);

				@Override
				public void onClick(DialogInterface p1, int p2) {
					try {
						PlaylistUtils.deletePlaylist(getActivity(), id);
						new PlaylistTask().execute();
						Utilities.toastShort(getActivity(), pName + " deleted");
					} catch(Exception e) {
						Log.e(TAG, e.toString());
						Utilities.toastLong(getActivity().getApplicationContext(), e.toString());
						e.printStackTrace();
					}
				}
			});

		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2) {
					// TODO: Implement this method
				}
			});

		builder.show();
	}

	private void renamePlaylistDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final String pName = adapter.PLAYLIST_NAME;
		final long id = PlaylistUtils.getPlaylistID(getActivity(), pName);
		builder.setTitle("Rename " + pName);

		//builder.setMessage("Enter Playlist Name");
		final EditText etInput = new EditText(getActivity());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.MATCH_PARENT);
		etInput.setLayoutParams(lp);
		etInput.setHint("Enter new Playlist Name");
		etInput.setHintTextColor(Color.BLUE);
		builder.setView(etInput);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("RENAME", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface p1, int p2) {
					try {
						String newPlaylistName = etInput.getText().toString().trim();
						//createPlaylist(playlistName);
						if(newPlaylistName.trim().length() > 0) {
							PlaylistUtils.renamePlaylist(getActivity().getApplicationContext(), id, newPlaylistName);
							Log.i(TAG, pName + " renamed to " + newPlaylistName);

							new PlaylistTask().execute();
						} else {

						}
					} catch(Exception e) {
						Log.e(TAG, e.toString());
						Utilities.toastLong(getActivity().getApplicationContext(), e.toString());
						e.printStackTrace();
					}
				}
			});

		builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2) {
					// TODO: Implement this method
				}
			});

		builder.show();
	}

}


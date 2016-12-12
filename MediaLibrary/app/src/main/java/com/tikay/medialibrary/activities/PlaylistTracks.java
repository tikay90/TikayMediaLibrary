package com.tikay.medialibrary.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.activities.PlaylistTracks;
import com.tikay.medialibrary.adapters.PlaylistTracksAdapter;
import com.tikay.medialibrary.models.PlaylistTrackModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale;
import com.tikay.medialibrary.db.TracksDb;
import com.tikay.medialibrary.db.DbConstants;
import com.tikay.medialibrary.db.DatabaseHelper;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaylistTracks extends AppCompatActivity implements OnItemClickListener, SearchView.OnQueryTextListener
{
	private SearchView searchView;
	private TextView tracks_No;
	private PlaylistTracksAdapter adapter;
	private ListView lv;
	private ArrayList<PlaylistTrackModel> playlists ;

	private Intent broadcastIntent;
	private String KEY = "key";

	private String TAG = "PlaylistTracks";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist_tracks);
		broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
		init();
		//initToolBar();
		Utilities.initActionBar(this, getPlaylistName(),"");
		try {
			if(savedInstanceState == null) {
				new PlaylistTracksTask().execute();
			} else {
				restoreState(savedInstanceState);
			}
		} catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}
		//new PlaylistTracksTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(KEY, playlists);
		super.onSaveInstanceState(outState);
	}

	private void restoreState(Bundle savedInstanceState) {
		playlists = savedInstanceState.getParcelableArrayList(KEY);
		if(playlists != null) {
			//tracks_No.setText(playlists.size() + " TRACKS");
			tracks_No.setText(getTracksNumber());
			adapter = new PlaylistTracksAdapter(getApplicationContext(), playlists);

			lv.setAdapter(adapter);
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(PlaylistTracks.this);
		}
	}

	private String getTracksNumber() {
		String s = "";//a>1?"Tracks":"Track";
		int a = playlists.size();

		if(a > 0) {
			s = a > 1 ? a + " TRACKS": a + " TRACK";
		} else {
			s = "NO TRACKS ";
		}
		return s;
	}



	private String getPlaylistName() {
		Intent i = getIntent();
		String name = i.getStringExtra("name");

		return name;
	}

	private long getPlaylistId() {
		Intent i = getIntent();
		long	id= i.getLongExtra("id", 0);

		return id;
	}

	private void init() {
		try {
			searchView = (SearchView)findViewById(R.id.sv_pt);
			tracks_No = (TextView)findViewById(R.id.tv_pt);
			searchView.setOnQueryTextListener(PlaylistTracks.this);
			lv = (ListView)findViewById(R.id.lv_pt);
		} catch(Exception e) {
			Toast.makeText(this, "Playlists --- init()  " +  e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("ERROR", "Playlists --- init()  " + e.getMessage());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		PlaylistTrackModel item = playlists.get(position);
		int pos = item.getPosition();
		//Toast.makeText(this, item.getID() + "   " + item.getName() + "\n" + item.getPath(), Toast.LENGTH_SHORT).show();
		broadcastIntent.putExtra(Constants.SEND, "playlist_tracks").putExtra("pos", pos);
		sendBroadcast(broadcastIntent);
		Constants.CURRENT_PLAYLIST_TRACKS.clear();
		Constants.CURRENT_PLAYLIST_TRACKS.addAll(Constants.PLAYLIST_TRACKS);

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




	private class PlaylistTracksTask extends AsyncTask<Void,Integer,ArrayList<PlaylistTrackModel>>
	{
		@Override
		protected ArrayList<PlaylistTrackModel> doInBackground(Void... p1) {
			ArrayList<PlaylistTrackModel> listOfTracks = null;
			int id = (int) getPlaylistId();

			switch(id) {
				case 0:
					SQLiteOpenHelper sql = DatabaseHelper.getInstance(getApplicationContext());
					listOfTracks = TracksDb.getInstance(sql).getPlaylistTracks();
					sql.close();
					break;
				case 1:

					break;
				case 2:

					break;
				case 3:

					break;
				default:
					listOfTracks = MyMediaQuery.displayPlaylistDetails(PlaylistTracks.this, id);
			}
			return listOfTracks;
		}

		@Override
		protected void onPostExecute(ArrayList<PlaylistTrackModel> result) {
			super.onPostExecute(result);
			try {
				playlists = result;
				tracks_No.setText(getTracksNumber());
				adapter = new PlaylistTracksAdapter(getApplicationContext(), playlists);

				lv.setAdapter(adapter);
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(PlaylistTracks.this);
			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}
	}




}

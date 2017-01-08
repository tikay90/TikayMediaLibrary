package com.tikay.medialibrary.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.tikay.medialibrary.db.DatabaseHelper;
import com.tikay.medialibrary.db.DbConstants;
import com.tikay.medialibrary.db.PlaylistDb;
import com.tikay.medialibrary.db.TracksDb;
import com.tikay.medialibrary.models.PlaylistModel;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import java.util.ArrayList;
import java.util.List;

public class MyDataBaseTask extends AsyncTask<Void,Integer,ArrayList<TracksModel>>
{
	private Context context;
	private String TAG = "MyDataBaseTask";

	public MyDataBaseTask(Context context) {
		this.context = context;
	}

	@Override
	protected ArrayList<TracksModel> doInBackground(Void... p1) {
		return MyMediaQuery.getAllTracks(context);
	}

	@Override
	protected void onPostExecute(ArrayList<TracksModel> result) {
		super.onPostExecute(result);
		Log.e(TAG, "RESULT = " + result.size());
		new CreateDataBaseTask().execute(result);

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
			Log.e(TAG, TAG + ": " + result);
			//Utilities.toastLong(getApplicationContext(),result);
		}

	}

	private void createDefaultPlaylist() {
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
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
			playlistDb.deleteDb(context);
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
		DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
		TracksDb tracksDb = TracksDb.getInstance(dbHelper);
		List<TracksModel> dbTracks = tracksDb.getAllTracks();
		//Log.e("DataBase Task", "dbTracks size: " + dbTracks.size());
		int tt = dbTracks == null ? 0: dbTracks.size();
		if(input != null) {
			if(tt < input.size() || tt > input.size()) {
				tracksDb.deleteDb(context);
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

}

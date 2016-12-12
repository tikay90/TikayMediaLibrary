package com.tikay.medialibrary.activities;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.adapters.TracksAdapter;
import com.tikay.medialibrary.models.AlbumTracksModel;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale;

public class GenreTracks extends AppCompatActivity implements OnItemClickListener,SearchView.OnQueryTextListener
{
	private ListView lv;
	private ArrayList<TracksModel> genreTracks = new ArrayList<TracksModel>();

	//private TextView tracks_No;
	private TracksAdapter adapter;
	private boolean serviceBroadcastRegistered = false;
	private SearchView searchView;
	private Intent broadcastIntent;

	private String TAG = "GENRETRACKS";
	//private TracksTask myTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.genre_tracks);

		try {
			Utilities.initActionBar(this, "Genre", getGenreName());
			broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
			initPrimary();

			new GenreTracksTask().execute();
		} catch(Exception e) {
			Log.e(TAG, TAG + " error @  " + e.getMessage());
		}

	}



	private void initPrimary() { 
		//tracks_No = (TextView)v.findViewById(R.id.tvTracks);
		//searchView = (SearchView)v.findViewById(R.id.sv_Tracks);
		lv = (ListView) findViewById(R.id.lv_genre_tt);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		TracksModel item = genreTracks.get(position);
		//String name = item.getSongName();
		int pos = item.getRealPosition();

		broadcastIntent.putExtra(Constants.SEND, "genre_tracks").putExtra("genre_pos", pos);
		sendBroadcast(broadcastIntent);
		Constants.CURRENT_GENRE_TRACKS.clear();
		Constants.CURRENT_GENRE_TRACKS.addAll(Constants.GENRE_TRACKS);
		//Utilities.toastShort(getActivity(), name + " pos = " + pos);
	}

	/*
	 public boolean onCreateOptionsMenu(Menu menu) {
	 super.onCreateOptionsMenu(menu);
	 getMenuInflater().inflate(R.menu.search_menu, menu);

	 searchView = (SearchView) menu.findItem(R.id.search_view_action).getActionView();  
	 //Sets searchable configuration defined in searchable.xml for this SearchView
	 SearchManager searchManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	 searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));  

	 searchView.setOnQueryTextListener(GenreTracks.this);

	 return true;
	 }
	 */

	@Override
	public boolean onQueryTextSubmit(String text) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onQueryTextChange(String query) {
		String input = query.toString().toLowerCase(Locale.getDefault());
		adapter.filter(input);
		adapter.setSongsList(genreTracks);
		return false;
	}

	private ArrayList<TracksModel> displayGenreDetails() {
		ArrayList<TracksModel> tracksList = new ArrayList<TracksModel>();
		Cursor cursor = null;
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Uri uri2 = MediaStore.Audio.Genres.Members.getContentUri("external", getId());
		Constants.GENRE_TRACKS.clear();
		long id = 0;
		String where = MediaStore.Audio.Media.TRACK + "=?";
		String whereVal[] = { getGenreName() };
		String orderBy = MediaStore.Audio.Media.TITLE;
		String[] column = {
			/*0*/	MediaStore.Audio.Media._ID,
			/*1*/	MediaStore.Audio.Media.DATA, 
			/*2*/ MediaStore.Audio.Media.TITLE,
			/*3*/	MediaStore.Audio.Media.DISPLAY_NAME,
			/*4*/	MediaStore.Audio.Media.ARTIST,
			/*5*/	MediaStore.Audio.Media.DURATION,
			/*6*/	MediaStore.Audio.Media.ALBUM,
			/*7*/	MediaStore.Audio.Media.DATE_ADDED,
			/*8*/	MediaStore.Audio.Media.ALBUM_ID,
			/*9*/ MediaStore.Audio.Media.SIZE
		};

		try {
			cursor = getContentResolver().query(uri2, column, null, null, orderBy);
			if(Utilities.isSdCardPresent()) {
				if(cursor != null && cursor.moveToFirst()) {
					do{
						String artist = cursor.getString(4);
						String title = cursor.getString(2);
						int time = cursor.getInt(5);
						String album = cursor.getString(6);
						id = cursor.getLong(0);
						String data = cursor.getString(1);
						//dateAdded = cursor.getString(7);
						long size = cursor.getLong(9);
						int albumArtId = cursor.getInt(8);
						Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
						Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumArtId);
						String albumArt = albumArtUri.toString();

						String duration = Utilities.miliSecondsToTimer(time);

						TracksModel item = new TracksModel();
						item.setSongName(title);
						item.setSongArtist(artist);
						item.setSongAlbumName(album);
						item.setSongId((int)id);
						item.setSongFullPath(data);
						item.setSongDuration(duration);
						item.setSongSize(size);
						//item.setDateAdded(dateAdded);
						//item.setDateModified(dataModified);
						item.setSongAlbumArtPath(albumArt);

						int k = 0;
						for(int i =0; i < tracksList.size(); i++) {
							k++;
						}
						item.setRealPosition(k);

						tracksList.add(item);
						Constants.GENRE_TRACKS.add(item);
					}while(cursor.moveToNext());
				}
			}

			if(cursor != null) {
				cursor.close();
				cursor = null;
			}
		} catch(Exception e) {
			Log.e(TAG, TAG + " query() >>> error: " + e.getMessage());
		}


		return tracksList;
	}

	private long getId() {
		Intent i = getIntent();
		long id = i.getLongExtra("id", 0);
		return id;
	}

	private String getGenreName() {
		Intent i = getIntent();
		String name = i.getStringExtra("name");

		//Utilities.toastShort(this,""+id);
		//String aid = String.valueOf(id);

		return name;
	}



	private class GenreTracksTask extends AsyncTask<Void,Integer,ArrayList<TracksModel>>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected ArrayList<TracksModel> doInBackground(Void... p1) {
			//ArrayList<TracksModel> listOfSongs = displayGenreDetails();//MyMediaQuery.getTrack(GenreTracks.this);

			return displayGenreDetails();
		}

		@Override
		protected void onProgressUpdate(Integer[] values) {
			// TODO: Implement this method
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(ArrayList<TracksModel> result) {
			super.onPostExecute(result);
			try {
				genreTracks = result;
				adapter = new TracksAdapter(getApplicationContext(), genreTracks);

				lv.setAdapter(adapter);

				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(GenreTracks.this);
			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}

	}

}

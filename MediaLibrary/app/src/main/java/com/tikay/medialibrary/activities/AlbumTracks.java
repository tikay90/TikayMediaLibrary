package com.tikay.medialibrary.activities;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
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
import com.tikay.medialibrary.activities.AlbumTracks;
import com.tikay.medialibrary.adapters.PlaylistTracksAdapter;
import com.tikay.medialibrary.models.PlaylistTrackModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale;
import com.tikay.medialibrary.adapters.AlbumTracksAdapter;
import com.tikay.medialibrary.models.AlbumTracksModel;

public class AlbumTracks extends AppCompatActivity implements /*LoaderManager.LoaderCallbacks<Cursor>*/ OnItemClickListener, SearchView.OnQueryTextListener
{
	private SearchView searchView;
	private TextView tracks_No;
	private AlbumTracksAdapter adapter;
	private ListView lv;
	private ArrayList<AlbumTracksModel> albumTracks = new ArrayList<AlbumTracksModel>()  ;
	private	ProgressDialog progressDialog;
	private	Intent broadcastIntent;

	private String KEY="key";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_tracks);

		broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
		Utilities.initActionBar(this, "Album", getAlbumName());
		init();
		if(savedInstanceState == null) {
			new AlbumTracksTask().execute();
		} else {
			restoreState(savedInstanceState);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(KEY, albumTracks);
		super.onSaveInstanceState(outState);
	}

	private String getTracksNumber() {
		String s = "";
		int a = albumTracks.size();

		if(a > 0) {
			s = a > 1 ? a + " TRACKS": a + " TRACK";
		} else {
			s = "NO TRACKS ";
		}
		return s;
	}

	private String getAlbumName() {
		Intent i = getIntent();
		String name = i.getStringExtra("name");

		return name;
	}

	private long getAlbumId() {
		Intent i = getIntent();
		long id = i.getLongExtra("id", 0);

		return id;
	}

	private void restoreState(Bundle savedInstanceState) {
		albumTracks = savedInstanceState.getParcelableArrayList(KEY);
		if(albumTracks != null) {
			tracks_No.setText(getTracksNumber());
			adapter = new AlbumTracksAdapter(getApplicationContext(), albumTracks);

			lv.setAdapter(adapter);
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(AlbumTracks.this);

		}
	}

	private void init() {
		searchView = (SearchView)findViewById(R.id.sv_album_tt);
		searchView.setOnQueryTextListener(AlbumTracks.this);
		tracks_No = (TextView)findViewById(R.id.tv_album_tt);
		lv = (ListView)findViewById(R.id.lv_album_tt);
	}



	private ArrayList<AlbumTracksModel> displayAlbumDetails() {
		ArrayList<AlbumTracksModel> tracksList = new ArrayList<AlbumTracksModel>();
		Cursor cursor = null;
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Constants.ALBUM_TRACKS.clear();
		long id = getAlbumId();
		String selection = "is_music != 0";
    if(id > 0) {
			selection = selection + " and album_id = " + id;
    }
		String whereVal[] = { getAlbumName() };
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
			/*8*/	MediaStore.Audio.Media.ALBUM_ID
		};
		if(Utilities.isSdCardPresent()) {
			try {
				cursor = getContentResolver().query(uri, column, selection, null, orderBy);
				if(cursor != null && cursor.moveToFirst()) {
					do{
						String artist = cursor.getString(4);
						String title = cursor.getString(2);
						int time = cursor.getInt(5);
						String album = cursor.getString(6);
						id = cursor.getLong(0);
						String data = cursor.getString(1);
						int albumArtId = cursor.getInt(8);
						Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
						Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumArtId);
						String albumArt = albumArtUri.toString();

						String duration = Utilities.miliSecondsToTimer(time);

						AlbumTracksModel item = new AlbumTracksModel();
						item.setName(title);
						item.setArtist(artist);
						item.setAlbum(album);
						item.setID(id);
						item.setPath(data);
						item.setDuration(duration);
						//item.setDateAdded(dateAdded);
						//item.setDateModified(dataModified);
						item.setAlbumArt(albumArt);

						int k = 0;
						for(int i =0; i < tracksList.size(); i++) {
							k++;
						}
						item.setPosition(k);

						tracksList.add(item);
						Constants.ALBUM_TRACKS.add(item);
					}while(cursor.moveToNext());
				}
			} catch(Exception e) {
				Log.e("AlbumTracks", e.getMessage());
				Utilities.toastLong(this,e.getMessage());
			}


			if(cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return tracksList;
	}

	@Override
	public boolean onQueryTextSubmit(String p1) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onQueryTextChange(String query) {
		String input = query.toString().toLowerCase(Locale.getDefault());
		adapter.filter(input);
		adapter.setSongsList(albumTracks);
		return false;
	}


	/** onclick */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AlbumTracksModel item = albumTracks.get(position);
		int pos = item.getPosition();

		//Toast.makeText(this, pos + "   " + item.getName() + "\n" + item.getPath(), Toast.LENGTH_SHORT).show();
		broadcastIntent.putExtra(Constants.SEND, "album_tracks").putExtra("pos", pos);
		sendBroadcast(broadcastIntent);
		Constants.CURRENT_ALBUM_TRACKS.clear();
		Constants.CURRENT_ALBUM_TRACKS.addAll(Constants.ALBUM_TRACKS);

	}




	private class AlbumTracksTask extends AsyncTask<Void,Integer,ArrayList<AlbumTracksModel>>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Utilities.showProgressDialog(AlbumTracks.this, "Loading Tracks...", "Please wait !!!");
		}

		@Override
		protected ArrayList<AlbumTracksModel> doInBackground(Void... p1) {
			ArrayList<AlbumTracksModel> listOfTracks = displayAlbumDetails();
			//publishProgress(queryTime);

			return listOfTracks;
		}

		@Override
		protected void onProgressUpdate(Integer[] values) {
			// TODO: Implement this method
			super.onProgressUpdate(values);
			/*progressBar.setProgress(values[0]);
			 tvLoading.setText("Loading...  " + values[0] + " %");
			 tvPer.setText(values[0] + " %");
			 */

			Toast.makeText(getApplicationContext(), "In onProgressUpdate() - " + values[0], Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(ArrayList<AlbumTracksModel> result) {
			super.onPostExecute(result);
			//Toast.makeText(getApplicationContext(), "In onPostExecute() " + result.size(), Toast.LENGTH_SHORT).show();

			try {
				albumTracks = result;
				tracks_No.setText(getTracksNumber());
				adapter = new AlbumTracksAdapter(getApplicationContext(), albumTracks);

				lv.setAdapter(adapter);
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(AlbumTracks.this);

				//Utilities.dismissProgressDialog();
				//Utilities.killHandler();

			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}
	}

}

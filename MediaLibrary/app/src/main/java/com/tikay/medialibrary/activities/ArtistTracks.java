package com.tikay.medialibrary.activities;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.tikay.medialibrary.adapters.AlbumTracksAdapter;
import com.tikay.medialibrary.adapters.ArtistTracksAdapter;
import com.tikay.medialibrary.models.ArtistTracksModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.Utilities;
import java.util.ArrayList;
import java.util.Locale;

public class ArtistTracks extends AppCompatActivity implements /*LoaderManager.LoaderCallbacks<Cursor>*/ OnItemClickListener, SearchView.OnQueryTextListener
{
	private SearchView searchView;
	private TextView tracks_No;
	private ArtistTracksAdapter adapter;
	private ListView lv;
	private ArrayList<ArtistTracksModel> artistTracks = new ArrayList<ArtistTracksModel>()  ;
	private	ProgressDialog progressDialog;
	private	Intent broadcastIntent;

	private String KEY="key";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_tracks);

		broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
		Utilities.initActionBar(this,"Artist", getArtistName());
		init();
		if(savedInstanceState == null) {
			new AlbumTracksTask().execute();
		} else {
			restoreState(savedInstanceState);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList(KEY, artistTracks);
		super.onSaveInstanceState(outState);
	}

	private String getTracksNumber() {
		String s = "";
		int a = artistTracks.size();

		if(a > 0) {
			s = a > 1 ? a + " TRACKS": a + " TRACK";
		} else {
			s = "NO TRACKS ";
		}
		return s;
	}

	private String getArtistName() {
		Intent i = getIntent();
		long id = i.getLongExtra("id", 0);
		String name = i.getStringExtra("name");

		//String aid = String.valueOf(id);

		return name;
	}

	private void restoreState(Bundle savedInstanceState) {
		artistTracks = savedInstanceState.getParcelableArrayList(KEY);
		if(artistTracks != null) {
			tracks_No.setText(getTracksNumber());
			adapter = new ArtistTracksAdapter(getApplicationContext(), artistTracks);

			lv.setAdapter(adapter);
			lv.setTextFilterEnabled(true);
			lv.setOnItemClickListener(ArtistTracks.this);

		}
	}

	private void init() {
		searchView = (SearchView)findViewById(R.id.sv_artist_tt);
		searchView.setOnQueryTextListener(ArtistTracks.this);
		tracks_No = (TextView)findViewById(R.id.tv_artist_tt);
		lv = (ListView)findViewById(R.id.lv_artist_tt);
	}

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

	private ArrayList<ArtistTracksModel> displayArtistDetails() {
		ArrayList<ArtistTracksModel> tracksList = new ArrayList<ArtistTracksModel>();
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Constants.ARTIST_TRACKS.clear();
		long id = 0;
		String where = MediaStore.Audio.Media.ARTIST + "=?";
		String whereVal[] = { getArtistName() };
		String orderBy = MediaStore.Audio.Media.TITLE;

		Cursor cursor = getContentResolver().query(uri, column, where, whereVal, orderBy);
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
					//dataModified = cursor.getString(9);
					int albumArtId = cursor.getInt(8);
					Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
					Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumArtId);
					String albumArt = albumArtUri.toString();

					String duration = Utilities.miliSecondsToTimer(time);

					ArtistTracksModel item = new ArtistTracksModel();
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
					Constants.ARTIST_TRACKS.add(item);
				}while(cursor.moveToNext());
			}
		}

		if(cursor != null) {
			cursor.close();
			cursor = null;
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
		adapter.setSongsList(artistTracks);
		return false;
	}


	/** onclick */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ArtistTracksModel item = artistTracks.get(position);
		int pos = item.getPosition();

		//Toast.makeText(this, pos + "   " + item.getName() + "\n" + item.getPath(), Toast.LENGTH_SHORT).show();
		Constants.CURRENT_ARTIST_TRACKS.clear();
		Constants.CURRENT_ARTIST_TRACKS.addAll(Constants.ARTIST_TRACKS);
		broadcastIntent.putExtra(Constants.SEND, "artist_tracks").putExtra("artist_pos", pos);
		sendBroadcast(broadcastIntent);
	}




	private class AlbumTracksTask extends AsyncTask<Void,Integer,ArrayList<ArtistTracksModel>>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//Utilities.showProgressDialog(AlbumTracks.this, "Loading Tracks...", "Please wait !!!");
		}

		@Override
		protected ArrayList<ArtistTracksModel> doInBackground(Void... p1) {
			ArrayList<ArtistTracksModel> listOfTracks = displayArtistDetails();
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
		protected void onPostExecute(ArrayList<ArtistTracksModel> result) {
			super.onPostExecute(result);
			//Toast.makeText(getApplicationContext(), "In onPostExecute() " + result.size(), Toast.LENGTH_SHORT).show();

			try {
				artistTracks = result;
				tracks_No.setText(getTracksNumber());
				adapter = new ArtistTracksAdapter(getApplicationContext(), artistTracks);

				lv.setAdapter(adapter);
				lv.setTextFilterEnabled(true);
				lv.setOnItemClickListener(ArtistTracks.this);

				//Utilities.dismissProgressDialog();
				//Utilities.killHandler();

			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}
	}

}

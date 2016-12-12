package com.tikay.medialibrary.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.tikay.medialibrary.models.AlbumTracksModel;
import com.tikay.medialibrary.models.ArtistTracksModel;
import com.tikay.medialibrary.models.FolderModel;
import com.tikay.medialibrary.models.PlaylistTrackModel;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.Utilities;
import java.io.IOException;
import java.util.ArrayList;

public class MediaService extends Service implements MediaPlayer.OnCompletionListener,
MediaPlayer.OnErrorListener,MediaPlayer.OnSeekCompleteListener,MediaPlayer.OnPreparedListener
{
	private String TAG = "MediaService";
	private boolean notiBroadcastIsRegistered = false;
	private Intent broadcastIntent;
	private int pos = 0;
	private static MediaPlayer mp;
  private static int audioSessionId;
  private String pathToTrack,title,artist,trackAlbum,trackName;
	private ArrayList<FolderModel> FOLDER_TRACKS;
	private ArrayList<AlbumTracksModel> ALBUM_TRACKS;
	private ArrayList<ArtistTracksModel> ARTIST_TRACKS;
	private ArrayList<PlaylistTrackModel> PLAYLIST_TRACKS;
	private ArrayList<TracksModel> TRACKS = new ArrayList<TracksModel>();
	private ArrayList<TracksModel> GENRE_TRACKS = new ArrayList<TracksModel>();
	private static int TRACK_ORIGIN ;

	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "onBind() CALLED");
		Utilities.toastShort(this, "onBind() CALLED");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "SERVICE ==> onCreate() CALLED");

		initMediaPlayer();
		broadcastIntent = new Intent(Constants.BROADCAST_RECEIVED);
		registerGeneralBroadcast();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "SERVICE ==> onStartCommand() CALLED");

		return START_STICKY;
	}


	private void initMediaPlayer() {
		mp = new MediaPlayer();
		mp.setAudioSessionId(Constants.audioSessionId);
		audioSessionId =	mp.getAudioSessionId();
		mp.setOnCompletionListener(this);
		mp.setOnErrorListener(this);
		mp.setOnPreparedListener(this);
		mp.setOnSeekCompleteListener(this);
	}

	private void setMediaPlayerPath() {
		try {
			mp.reset();
			mp.setDataSource(pathToTrack);
			mp.prepareAsync();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalStateException e) {
			e.printStackTrace();
		}
	}


	// Tracks method
	private void getTrackPosition(int position) {
		try {
			pathToTrack = TRACKS.get(position).getSongFullPath();
			trackName = TRACKS.get(position).getSongName();
			trackAlbum = TRACKS.get(position).getSongAlbumName();

			setMediaPlayerPath();
		} catch(Exception e) {
			Log.e(TAG, "getTrackPosition() <<>> " + e.getMessage());
		}
	}

	private void getAlbumTrackPosition(int position) {
		try {
			pathToTrack = ALBUM_TRACKS.get(position).getPath();
			trackName = ALBUM_TRACKS.get(position).getName();
			trackAlbum = ALBUM_TRACKS.get(position).getAlbum();

			setMediaPlayerPath();
		} catch(Exception e) {
			Log.e(TAG, "getAlbumTrackPosition() <<>> " + e.getMessage());
		}
	}

	private void getArtistTrackPosition(int position) {
		try {
			pathToTrack = ARTIST_TRACKS.get(position).getPath();
			trackName = ARTIST_TRACKS.get(position).getName();
			trackAlbum = ARTIST_TRACKS.get(position).getAlbum();

			setMediaPlayerPath();
		} catch(Exception e) {

		}
	}

	private void getPlaylistTrackPosition(int position) {
		try {
			pathToTrack = PLAYLIST_TRACKS.get(position).getPath();
			trackName = PLAYLIST_TRACKS.get(position).getName();
			trackAlbum = PLAYLIST_TRACKS.get(position).getAlbum();

			setMediaPlayerPath();
		} catch(Exception e) {
			Log.e(TAG, "getArtistTrackPosition() <<>> " + e.getMessage());
		}
	}

	private void getFolderTrackPosition(int position) {
		try {
			String songPath = FOLDER_TRACKS.get(position).getFolderPath();
			pathToTrack = songPath;
			Utilities.toastShort(MediaService.this, "" + songPath);
			setMediaPlayerPath();
		} catch(Exception e) {
			Log.e(TAG, "getFolderTrackPosition() <<>> " + e.getMessage());
		}
	}
	
	private void getGenreTrackPosition(int position) {
		try {
			String songPath = GENRE_TRACKS.get(position).getSongFullPath();
			pathToTrack = songPath;
			trackName = GENRE_TRACKS.get(position).getSongName();
			trackAlbum = GENRE_TRACKS.get(position).getSongAlbumName();

			setMediaPlayerPath();
		} catch(Exception e) {
			Log.e(TAG, "getFolderTrackPosition() <<>> " + e.getMessage());
		}
	}


	private BroadcastReceiver universalBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context ctx, Intent intent) {
			String action = intent.getStringExtra(Constants.SEND);

			switch(action) {
				case "tracks":
					TRACK_ORIGIN = 0;
					TRACKS = Constants.CURRENT_TRACKS;
					pos = intent.getIntExtra("pos", 0);
					getTrackPosition(pos);
					broadcastIntent.putExtra(Constants.RECEIVE, "tracks");
					break;

				case "album_tracks":
					TRACK_ORIGIN = 1;
					ALBUM_TRACKS = Constants.CURRENT_ALBUM_TRACKS;
					pos = intent.getIntExtra("pos", 0);
					getAlbumTrackPosition(pos);
					break;

				case "playlist_tracks": 
					TRACK_ORIGIN = 2;
					PLAYLIST_TRACKS = Constants.CURRENT_PLAYLIST_TRACKS;
					pos = intent.getIntExtra("pos", 0);
					getPlaylistTrackPosition(pos);
					break;

				case "folder_track_position":
					TRACK_ORIGIN = 3;
					FOLDER_TRACKS = Constants.CURRENT_FOLDER_TRACKS;
					pos = intent.getIntExtra("folder_position", 0);
					//Utilities.toastShort(MediaService.this, "" + pos);
					getFolderTrackPosition(pos);
					break;

				case "artist_tracks":
					TRACK_ORIGIN = 4;
					ARTIST_TRACKS = Constants.CURRENT_ARTIST_TRACKS;
					pos = intent.getIntExtra("artist_pos", 0);
					//Utilities.toastShort(MediaService.this, "" + pos);
					getArtistTrackPosition(pos);
					break;

				case "genre_tracks":
					TRACK_ORIGIN = 5;
					GENRE_TRACKS = Constants.CURRENT_GENRE_TRACKS;
					pos = intent.getIntExtra("genre_pos", 0);
					//Utilities.toastShort(MediaService.this, "" + pos);
					getGenreTrackPosition(pos);
					break;
			}
			try {

			} catch(Exception e) {
				Utilities.toastLong(MediaService.this, "In Service " + e.getMessage());
				Log.e(TAG, "In Service " + e.getMessage());
			}
			//Utilities.toastLong(MediaService.this, "In Service -->  SWITCH = " + TRACK_ORIGIN);
		}
  };

	// register gen broadcast
  private void registerGeneralBroadcast() {
		if(!notiBroadcastIsRegistered) {
			registerReceiver(universalBroadcastReceiver, new IntentFilter(Constants.BROADCAST_UNIVERSAL));
			notiBroadcastIsRegistered = true;
		}
  }

  // unregister gen broadcast receiver
  private void unregisterGeneralBroadcast() {
		if(notiBroadcastIsRegistered) {
			unregisterReceiver(universalBroadcastReceiver);
			notiBroadcastIsRegistered = false;
		}
  }

	@Override
	public void onCompletion(MediaPlayer p1) {
		try {
			pos += 1;
			switch(TRACK_ORIGIN) {
				case 0: // tracks
					if(pos > TRACKS.size() - 1) {
						pos = 0;
					}
					getTrackPosition(pos);
					break;

				case 1: // album tracks
					if(pos > ALBUM_TRACKS.size() - 1) {
						pos = 0;
					}
					getAlbumTrackPosition(pos);
					break;

				case 2: // playlist tracks
					if(pos > PLAYLIST_TRACKS.size() - 1) {
						pos = 0;
					}
					getPlaylistTrackPosition(pos);
					break;

				case 3: // folder tracks
					if(pos > FOLDER_TRACKS.size() - 1) {
						pos = 0;
					}
					getFolderTrackPosition(pos);
					//Utilities.toastLong(MediaService.this, "Completed pos = " + pos);
					break;

				case 4: // artist tracks
					if(pos > ARTIST_TRACKS.size() - 1) {
						pos = 0;
					}
					getArtistTrackPosition(pos);
					//Utilities.toastLong(MediaService.this, "Completed pos = " + pos);
					break;
					
				case 5: // artist tracks
					if(pos > GENRE_TRACKS.size() - 1) {
						pos = 0;
					}
					getGenreTrackPosition(pos);
					//Utilities.toastLong(MediaService.this, "Completed pos = " + pos);
					break;
			}
			//Utilities.toastLong(MediaService.this, "TRACK_ORIGIN = " + TRACK_ORIGIN + " \n Completed pos = " + pos);
		} catch(Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			Utilities.toastLong(MediaService.this, "In SERVICE >>>> ERROR = " + e.getMessage());
		}
	}

	@Override
	public void onPrepared(MediaPlayer p1) {
		playMedia();
	}

	@Override
	public void onSeekComplete(MediaPlayer p1) {
		// TODO: Implement this method
	}


	@Override
  public boolean onError(MediaPlayer mplayer, int what, int extra) {
		switch(what) {
			case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
				Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK " + extra, Toast.LENGTH_LONG).show();
				break;
			case MediaPlayer.MEDIA_ERROR_UNKNOWN:
				Toast.makeText(this, "MEDIA_ERROR_UNKNOWN " + extra, Toast.LENGTH_LONG).show();
				break;
			case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED " + extra, Toast.LENGTH_LONG).show();
				break;
			case MediaPlayer.MEDIA_ERROR_IO:
				Toast.makeText(this, "MEDIA_ERROR_IO " + extra, Toast.LENGTH_LONG).show();
				break;
			case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
				Toast.makeText(this, "MEDIA_ERROR_UNSUPPORTED " + extra, Toast.LENGTH_LONG).show();
				break;
			case MediaPlayer.MEDIA_ERROR_MALFORMED:
				Toast.makeText(this, "MEDIA_ERROR_MALFORMED " + extra, Toast.LENGTH_LONG).show();
				break;

		}

		return false;
  }

	private void playMedia() {
		//if(!mp.isPlaying()){
		mp.start();
		//}
	}


	private void stopAudio() {
		if(mp.isPlaying()) {
			mp.stop();
			//mp.reset();
			killMediaPlayer();
		}
  }

	private void killMediaPlayer() {
		if(mp != null) {
			if(mp.isPlaying()) {
				mp.stop();
				mp.reset();

			}
			mp.release();
			mp = null;
		}
	}

	


	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy() CALLED");
		//Utilities.toastShort(this,"onDestroy() CALLED");
		unregisterGeneralBroadcast();
		killMediaPlayer();

		super.onDestroy();
	}


}

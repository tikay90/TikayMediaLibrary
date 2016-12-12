package com.tikay.medialibrary.utils;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.provider.MediaStore.Audio.Genres;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.util.Log;
import com.tikay.medialibrary.models.AlbumModel;
import com.tikay.medialibrary.models.ArtistModel;
import com.tikay.medialibrary.models.GenreModel;
import com.tikay.medialibrary.models.PlaylistModel;
import com.tikay.medialibrary.models.PlaylistTrackModel;
import com.tikay.medialibrary.models.TracksModel;
import java.util.ArrayList;

public class MyMediaQuery
{
	/**************************************************************************************************
	 TRACKS QUERY METHOD
	 **************************************************************************************************/
	public static ArrayList<TracksModel> getTrack(Context context) {
		ArrayList<TracksModel> listOfSongs = new ArrayList<TracksModel>();
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = null;
		final String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
		String projection[] = 
		{
			MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.SIZE,
			MediaStore.Audio.Media.IS_MUSIC,
			MediaStore.Audio.Media.ALBUM_ID
		};
		/**---- checking if sdcard is present -----|||--*******************/
		if(Utilities.isSdCardPresent()) {
			cursor = context.getContentResolver().query(uri, projection, null,  null, sortOrder);
			while(cursor.moveToNext()) {
				String songName = cursor.getString(0);
				long size =cursor.getInt(6);
				String albumName = cursor.getString(2);
				String artist = cursor.getString(3);
				String fullPath = cursor.getString(4);
				int albumArtId = cursor.getInt(8);

				int time = cursor.getInt(5);
				String duration = Utilities.miliSecondsToTimer(time);

				Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
				Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumArtId);

				TracksModel track = new TracksModel();
				track.setSongName(songName);
				track.setSongSize(size);
				track.setSongDuration(duration);
				track.setSongArtist(artist);
				if(albumArtUri != null) {
					track.setSongAlbumArtPath(albumArtUri.toString());
				}
				track.setSongFullPath(fullPath);
				track.setSongAlbumName(albumName);
				track.setType(Constants.OTHER_ROW);

				int k = 0;
				//-- 30th jan, 16
				for(int i =0; i < listOfSongs.size(); i++) {
					k++;
				}
				track.setRealPosition(k);
				Constants.TRACKS.add(track);
				listOfSongs.add(track);
			}
		}
		if(cursor != null) {
			cursor.close();
			cursor = null;
		}
		return listOfSongs;
	}



	/**************************************************************************************************
	 ALBUMS QUERY METHOD
	 **************************************************************************************************/
	public static ArrayList<AlbumModel> getAlbums(Context context) {
		ArrayList<AlbumModel> listOfAlbums = new ArrayList<AlbumModel>();
		Cursor cursor = null;
		Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
		String where = MediaStore.Audio.Albums.ALBUM + "!=0";
		String sortOrder = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER + " COLLATE LOCALIZED ASC";
		String projection[] = 
		{
			/* 0 */ BaseColumns._ID,
			/* 1 */ AlbumColumns.ALBUM,
			/* 2 */ AlbumColumns.ARTIST,
			/* 3 */ AlbumColumns.NUMBER_OF_SONGS,
			/* 4 */ AlbumColumns.ALBUM_ART,
			/* 5 */ AlbumColumns.FIRST_YEAR
		};
		if(Utilities.isSdCardPresent()) {
			try {
				cursor = context.getContentResolver().query(uri, projection, where, null, sortOrder);
				while(cursor.moveToNext()) {
					long id = cursor.getLong(0);
					String albumName = cursor.getString(1);
					String albumArtist = cursor.getString(2);
					String albumSongs = cursor.getString(3);
					String albumArt = cursor.getString(4);

					AlbumModel album = new AlbumModel();
					album.setAlbumId(id);
					album.setNub_of_songs(Integer.parseInt(albumSongs));
					//album.setAlbumId(k);
					album.setAlbumName(albumName);
					album.setAlbumArtist(albumArtist);
					album.setArtist_songs(albumSongs);
					album.setArtPath(albumArt);

					listOfAlbums.add(album);
				}
			} catch(Exception e) {
				Log.e("ALBUM_QUERY", e.getMessage());
			}
			if(cursor != null) {
				cursor.close();
				cursor = null;
				//Log.i(Constants.Albums, "IN getAlbums -- cursor closed");
			}
		}
		return listOfAlbums;
	}


	/**************************************************************************************************
	 PLAYLIST QUERY METHOD
	 ***************************************************************************************************/
	private final static Cursor makePlaylistCursor(Context context) {
		Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		String[] selection = {
			/* 0 */ MediaStore.Audio.Playlists._ID,
			/* 1 */ MediaStore.Audio.Playlists.NAME,
			/* 3 */ PlaylistsColumns.DATE_ADDED,
			/* 4 */ PlaylistsColumns.DATE_MODIFIED
		};
		String sortOrder = MediaStore.Audio.Playlists.NAME + " COLLATE LOCALIZED ASC";
		Cursor cursor = context.getContentResolver().query(uri, selection, null, null, sortOrder);
		if(cursor != null) {
			return cursor;
		}

		return null;
	}

	public static ArrayList<PlaylistModel> getPlaylists(Context context) {
		ArrayList<PlaylistModel> mPlaylistList = new ArrayList<PlaylistModel>();
		
		Cursor cursor = makePlaylistCursor(context);
		long id= 0;
		String playlistName = null;
		String playlistCount = null;
		try {
			if(Utilities.isSdCardPresent()) {
				if(cursor != null) {
					while(cursor.moveToNext()) {
						id = cursor.getLong(0);
						playlistName = cursor.getString(1);
						playlistCount = getPlaylistCount(id, context);

						PlaylistModel playlist = new PlaylistModel();
						playlist.setType(Constants.OTHER_ROW);
						playlist.setID(id);
						playlist.setName(playlistName);
						playlist.setTracks(playlistCount);

						mPlaylistList.add(playlist);
						//mPlaylistList.size();
						//Log.i("PLAYLIST", "IN getPlaylists() -- " + playlistName + "\n");
					}
				} else if(cursor.getCount() < 1) {
					Utilities.toastShort(context, " No playlist Available");
					Log.e("PLAYLIST", "IN getPlaylists() --  No playlist Available");
				}
			}

		} catch(Exception e) {
			Log.e("PLAYLIST", "IN getPlaylists() Exception -- " + e.getMessage());
		}	

		if(cursor != null) {
			cursor.close();
			cursor = null;
		}

		return mPlaylistList;
	}

	public static String getPlaylistCount(long playlistId, Context context) {
		String count = null;
		Uri u = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
		Cursor c = context.getContentResolver().query(u, null, null, null, null);
		if(c != null && c.moveToFirst()) {
			do{
				count = String.valueOf(c.getCount());
			}while(c.moveToNext());
		}

		if(c != null) {
			c.close();
			c = null;
		}

		return count;
	} 


	/**************************************************************************************************
	 ARTIST QUERY METHOD
	 **************************************************************************************************/
	public static ArrayList<ArtistModel> getArtist(Context context) {
		ArrayList<ArtistModel> listOfArtists = new ArrayList<ArtistModel>();
		Cursor cursor = null;
		Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
		String sortOrder = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER + " COLLATE LOCALIZED ASC";
		String projection[] = 
		{
			/* 0 */ BaseColumns._ID,
			/* 1 */ ArtistColumns.ARTIST,
			/* 2 */ ArtistColumns.NUMBER_OF_ALBUMS,
			/* 3 */ ArtistColumns.NUMBER_OF_TRACKS,
			///* 4 */ MediaStore.Audio.Artists.NUMBER_OF_TRACKS,

		};

		/**---- checking if sdcard is present -----|||--*/
		if(Utilities.isSdCardPresent()) {
			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, sortOrder);
				while(cursor.moveToNext()) {
					long id = cursor.getLong(0);
					String artistName = cursor.getString(1);
					String artistAlbums = cursor.getString(2);
					String artistTracks = cursor.getString(3);
					//String albumArt = cursor.getString(4);

					ArtistModel artist = new ArtistModel();
					artist.setArtistID(id);
					artist.setArtistName(artistName);
					artist.setArtistAlbums(artistAlbums);
					artist.setArtistTracks(artistTracks);

					listOfArtists.add(artist);
				}
				if(cursor != null) {
					cursor.close();
					cursor = null;
				}
			} catch(Exception e) {
				Log.e("Artist query", " GET-ARTIST <<>>  " + e.getMessage());
			}
		}


		return listOfArtists;
	}



	/** ************************************************************************************************
	 * GENRY QUERY METHOD --- 9th nov,2016
	 * @field context: the context from which this method is called
	 * @field cursor:  queries all genres 
	 * @field filterCursor: filters queries by cursor yo remove empty genres(e.i without dongs)
	 **************************************************************************************************/
	public static ArrayList<GenreModel> getGenry(Context context) {
		ArrayList<GenreModel> listsOfGenry = new ArrayList<GenreModel>();

		Cursor cursor = null;
		Cursor filterCursor = null;
		Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
		String sortOrder = MediaStore.Audio.Genres.DEFAULT_SORT_ORDER + " COLLATE LOCALIZED ASC";
		String projection[] = 
		{
			/* 0 */ Genres._ID,
			/* 1 */ Genres.NAME,
		};

		if(Utilities.isSdCardPresent()) {
			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, sortOrder);
				while(cursor.moveToNext()) {
					long id = cursor.getLong(0);

					Uri uri2 = MediaStore.Audio.Genres.Members.getContentUri("external", id);
					filterCursor = context.getContentResolver().query(uri2, null, null, null, null);
					if(filterCursor != null) {
						if(filterCursor.getCount() > 0) {
							while(filterCursor.moveToNext()) {
								GenreModel genre = new GenreModel();
								genre.setGenreID(id);
								String genreName = cursor.getString(1);
								genre.setGenreName(genreName);
								listsOfGenry.add(genre);
							}
						}
					}
					if(filterCursor != null) {
						filterCursor.close();
						filterCursor = null;
					}
				}
				if(cursor != null) {
					cursor.close();
					cursor = null;
				}

			} catch(Exception e) {
				Log.e("Genry query", " GET-GENRY <<>>  error @ " + e.getMessage());
			}
		}


		return listsOfGenry;
	}

	
	
	public static ArrayList<PlaylistTrackModel> displayPlaylistDetails(Context context,int playlistId) {
		ArrayList<PlaylistTrackModel> mPlaylistList = new ArrayList<PlaylistTrackModel>();
		Cursor cursor = null; 
		Constants.PLAYLIST_TRACKS.clear();
		String[] projection = 
		{ 
			/*0*/ MediaStore.Audio.Playlists.Members.TRACK, 
			/*1*/ MediaStore.Audio.Playlists.Members.ARTIST,
			/*2*/ MediaStore.Audio.Playlists.Members.TITLE, 
			/*3*/ MediaStore.Audio.Playlists.Members.DURATION, 
			/*4*/ MediaStore.Audio.Playlists.Members.ALBUM, 
			/*5*/ MediaStore.Audio.Playlists.Members._ID,
			/*6*/ MediaStore.Audio.Playlists.Members.DATA, 
			/*7*/ MediaStore.Audio.Playlists.Members.PLAY_ORDER ,
			/*8*/ MediaStore.Audio.Playlists.Members.DATE_ADDED ,
			/*9*/ MediaStore.Audio.Playlists.Members.DATE_MODIFIED,
			/*10*/MediaStore.Audio.Playlists.Members.ALBUM_ID
		};

		Uri u = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
		String sortOrder = MediaStore.Audio.Playlists.Members.TITLE + " COLLATE LOCALIZED ASC";
		if(Utilities.isSdCardPresent()) {
			cursor = context.getContentResolver().query(u, projection, null, null, sortOrder);
			if(cursor != null && cursor.moveToFirst()) {
				do{
					String track = cursor.getString(0);
					String artist = cursor.getString(1);
					String title = cursor.getString(2);
					int time = cursor.getInt(3);
					String album = cursor.getString(4);
					long id = cursor.getLong(5);
					String data = cursor.getString(6);
					String dateAdded = cursor.getString(8);
					String dataModified = cursor.getString(9);
					int albumArtId = cursor.getInt(10);
					Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
					Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumArtId);
					String albumArt = albumArtUri.toString();

					String duration = Utilities.miliSecondsToTimer(time);

					PlaylistTrackModel item = new PlaylistTrackModel();
					item.setName(title);
					item.setArtist(artist);
					item.setAlbum(album);
					item.setID(id);
					item.setPath(data);
					item.setDuration(duration);
					item.setDateAdded(dateAdded);
					item.setDateModified(dataModified);
					item.setAlbumArt(albumArt);

					int k = 0;
					for(int i =0; i < mPlaylistList.size(); i++) {
						k++;
					}
					item.setPosition(k);

					mPlaylistList.add(item);
					Constants.PLAYLIST_TRACKS.add(item);
				}while(cursor.moveToNext());
			}
		}
		if(cursor != null) {
			cursor.close();
			cursor = null;
		}

		return mPlaylistList;
	}
	
}

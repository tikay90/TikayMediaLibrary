package com.tikay.medialibrary.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Provides various playlist-related utility functions.
 */
public class PlaylistUtils
{

	private static String TAG = "Playlists";
	/**
	 * Queries all the playlists known to the MediaStore.
	 *
	 * @param resolver A ContentResolver to use.
	 * @return The queried cursor.
	 */
	public static Cursor queryPlaylists(ContentResolver resolver) {
		Uri media = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		String[] projection = { MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME };
		String sort = MediaStore.Audio.Playlists.NAME;
		return resolver.query(media, projection, null, null, sort);
	}


	public static long getPlaylistID(Context context, String name) {
		long id = -1;

		Cursor cursor = context.getContentResolver()
			.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
						 new String[] { MediaStore.Audio.Playlists._ID },
						 MediaStore.Audio.Playlists.NAME + "=?",
						 new String[] { name }, null);

		if(cursor != null) {
			if(cursor.moveToNext())
				id = cursor.getLong(0);
			cursor.close();
		}

		return id;
	}


	public static void createPlaylists(Context context, String pname) {
    Uri playlistsUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
    Cursor cursor = context.getContentResolver().query(playlistsUri, new String[] { "*" }, null, null, null);
    long playlistId = -1;
		if(cursor != null && cursor.moveToFirst()) {
			do {
				String plname = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
				if(plname.equalsIgnoreCase(pname)) {
					playlistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
					break;
				}
			} while (cursor.moveToNext());
		}
    cursor.close();

    if(playlistId != -1) {
			Uri deleteUri = ContentUris.withAppendedId(playlistsUri, playlistId);
			Log.i(TAG, "REMOVING Existing Playlist: " + playlistId + ",  " + pname);

			// delete the playlist
			context.getContentResolver().delete(deleteUri, null, null);
			//deletePlaylist(context.getContentResolver(), playlistId);
    }

    Log.i(TAG, "CREATING PLAYLIST: " + pname);
    ContentValues v1 = new ContentValues();
    v1.put(MediaStore.Audio.Playlists.NAME, pname);
    v1.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
		//  Uri newpl = context.getContentResolver().insert(playlistsUri, v1);
		context.getContentResolver().insert(playlistsUri, v1);
	}

	/**
	 * Run the given query and add the results to the given playlist. Should be
	 * run on a background thread.
	 *
	 * @param resolver A ContentResolver to use.
	 * @param playlistId The MediaStore.Audio.Playlist id of the playlist to
	 * modify.
	 * @param query The query to run. The audio id should be the first column.
	 * @return The number of songs that were added to the playlist.
	 */

	/*
	 public static int addToPlaylist(ContentResolver resolver, long playlistId, QueryTask query) {
	 if(playlistId == -1)
	 return 0;

	 // Find the greatest PLAY_ORDER in the playlist
	 Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
	 String[] projection = new String[] { MediaStore.Audio.Playlists.Members.PLAY_ORDER };
	 Cursor cursor = resolver.query(uri, projection, null, null, null);
	 int base = 0;
	 if(cursor.moveToLast())
	 base = cursor.getInt(0) + 1;
	 cursor.close();

	 Cursor from = query.runQuery(resolver);
	 if(from == null)
	 return 0;

	 int count = from.getCount();
	 if(count > 0) {
	 ContentValues[] values = new ContentValues[count];
	 for(int i = 0; i != count; ++i) {
	 from.moveToPosition(i);
	 ContentValues value = new ContentValues(2);
	 value.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + i));
	 value.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, from.getLong(0));
	 values[i] = value;
	 }
	 resolver.bulkInsert(uri, values);
	 }

	 from.close();

	 return count;
	 }
	 */


	/**
	 * Delete the playlist with the given id.
	 *
	 * @param resolver A ContentResolver to use.
	 * @param id The Media.Audio.Playlists id of the playlist.
	 */
	public static void deletePlaylist(Context context, long id) {
		Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
		context.getContentResolver().delete(uri, null, null);
	}

	/**
	 * Rename the playlist with the given id.
	 *
	 * @param resolver A ContentResolver to use.
	 * @param id The Media.Audio.Playlists id of the playlist.
	 * @param newName The new name for the playlist.
	 */
	public static void renamePlaylist(Context context, long id, String newName) {
		long existingId = getPlaylistID(context, newName);
		// We are already called the requested name; nothing to do.
		if(existingId == id) {
			Utilities.toastShort(context, "same name");
			return;
		}
		// There is already a playlist with this name. Kill it.
		if(existingId != -1) {
			deletePlaylist(context, existingId);
			Utilities.toastShort(context,"playlist deleted");
		}
		ContentValues values = new ContentValues(1);
		values.put(MediaStore.Audio.Playlists.NAME, newName);
		context.getContentResolver().update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, "_id=" + id, null);
	}


}

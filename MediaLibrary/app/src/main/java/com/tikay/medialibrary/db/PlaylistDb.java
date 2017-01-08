package com.tikay.medialibrary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.tikay.medialibrary.models.PlaylistModel;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.utils.Constants;
import java.io.File;
import java.util.ArrayList;

public class PlaylistDb
{
	private SQLiteOpenHelper sql;
	private static PlaylistDb INSTANCE;
	
	private static final String DEFAULT_PLAYLIST_TABLE = DbConstants.DEFAULT_PLAYLIST_TABLE;

	// ❤😉😜😊😘😍😒😂👌👍 Table Columns names
	private static final String COLUMN_ID = DbConstants.COLUMN_ID;
	private static final String COLUMN_NAME = DbConstants.COLUMN_NAME;
	private static final String COLUMN_TYPE = DbConstants.COLUMN_TYPE;
	private static final String TAG = "PlaylistDb";
	
	public static synchronized PlaylistDb getInstance(SQLiteOpenHelper sql) {
		if(INSTANCE == null) {
			Log.e(TAG,"getInstance(): First time getInstance was invoked!");
			INSTANCE = new PlaylistDb(sql);
		}
		return INSTANCE;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone instance of this class");
	}
	
	// constructor
	private PlaylistDb(SQLiteOpenHelper sql){
		this.sql = sql;
	}
	
	public void addPlaylist(PlaylistModel playlist) {
		SQLiteDatabase db = sql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, playlist.getID());
		values.put(COLUMN_NAME, playlist.getName()); // Track Name
		values.put(COLUMN_TYPE, playlist.getType());

		// Inserting Row
		
		db.insert(DEFAULT_PLAYLIST_TABLE, null, values);
		db.close(); // Closing database connection
	}
	
	public PlaylistModel getPlaylist(PlaylistModel playlist) {
		SQLiteDatabase db = sql.getReadableDatabase();

		String[] selection =  { COLUMN_ID,COLUMN_NAME,COLUMN_TYPE };
		String whereClause = COLUMN_ID + "=?";
		String[] where = { String.valueOf(playlist.getID()) };
		Cursor cursor = db.query(DEFAULT_PLAYLIST_TABLE, selection, whereClause, where, null, null, null, null);
		if(cursor != null)
			cursor.moveToFirst();

		PlaylistModel tracks = new PlaylistModel();
		tracks.setID(Long.parseLong(cursor.getString(0)));
		tracks.setName(cursor.getString(1));
		tracks.setType(Integer.parseInt(cursor.getString(2)));

		cursor.close();
		db.close();

		return tracks;
	}
	
	
	// Getting All Playlist
	public ArrayList<PlaylistModel> getAllPlaylist() { 
		ArrayList<PlaylistModel> playList = new ArrayList<PlaylistModel>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + DEFAULT_PLAYLIST_TABLE;

		SQLiteDatabase db = sql.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if(cursor.moveToFirst()) {
			do {
				long id = Long.parseLong(cursor.getString(0));
				String count = getCount(id);
				PlaylistModel playlist = new PlaylistModel();
				playlist.setID(id);
				playlist.setName(cursor.getString(1));
				playlist.setType(Constants.FIRST_ROW);
				if(id == 0) {
					playlist.setTracks("" + TracksDb.getInstance(sql).getTracksCount());
				}

				// Adding TracksModel to list
				playList.add(playlist);
			} while (cursor.moveToNext());
		}

		if(!cursor.isClosed())
			cursor.close();
		db.close();
		// return Tracks list
		return playList;
	}

	// Updating single Playlist
	public int updatePlaylist(PlaylistModel playlist) {
		SQLiteDatabase db = sql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, playlist.getName());
		values.put(COLUMN_TYPE, playlist.getTracks());

		// updating row
		int row = db.update(DEFAULT_PLAYLIST_TABLE, values, COLUMN_ID + " = ?",
												new String[] { String.valueOf(playlist.getID()) });
		db.close();
		return row;
	}

	// Deleting single TracksModel
	public void deletePlaylist(TracksModel TracksModel) {
		SQLiteDatabase db = sql.getWritableDatabase();
		db.delete(DEFAULT_PLAYLIST_TABLE, COLUMN_ID + " = ?",
							new String[] { String.valueOf(TracksModel.getSongId()) });
		db.close();
	}


	public void deleteDb(Context context) {
		SQLiteDatabase db = sql.getWritableDatabase();
		File file = new File(db.getPath());
		Log.e(TAG, "deleting database - "+DbConstants.PLAYLIST_DB+" ...");
		db.deleteDatabase(file);
		db.close();
		Log.e("DataBase ", "Database deleted");
		//Utilities.toastLong(context, "Database deleted");
	}


	// Getting TracksModels Count
	public int getTracksCount() {
		String countQuery = "SELECT * FROM " + DEFAULT_PLAYLIST_TABLE;
		SQLiteDatabase db = sql.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	public int getTracksCount(long id) {
		String countQuery = "SELECT  " + COLUMN_ID + " FROM " + DEFAULT_PLAYLIST_TABLE;
		SQLiteDatabase db = sql.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	public String getCount(long id) {
		String query = "SELECT * FROM " + DEFAULT_PLAYLIST_TABLE + " WHERE "
			+ COLUMN_ID + "='" + id + "'";
		SQLiteDatabase db = sql.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		int count = cursor.getCount();
		if(count > 0)
			return String.valueOf(count);

		return "";
	}
	
	
	
	
}

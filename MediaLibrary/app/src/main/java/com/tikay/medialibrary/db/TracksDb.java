package com.tikay.medialibrary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.tikay.medialibrary.models.PlaylistTrackModel;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TracksDb
{
	private SQLiteOpenHelper sql;

	private static final String TRACKS_TABLE = DbConstants.PLAYLIST_TRACKS_TABLE;

	// Tracks Table Columns names
	private static final String COLUMN_ID = DbConstants.COLUMN_ID;
	private static final String COLUMN_NAME = DbConstants.COLUMN_NAME;
	private static final String COLUMN_PATH = DbConstants.COLUMN_PATH;
	private static final String COLUMN_NO = DbConstants.COLUMN_TRACK_NO;
	private static final String COLUMN_ALBUM_ART = DbConstants.COLUMN_ALBUM_ART;
	private static final String COLUMN_ALBUM = DbConstants.COLUMN_ALBUM;
	private static final String COLUMN_DURATION = DbConstants.COLUMN_DURATION;
	private static final String COLUMN_ARTIST = DbConstants.COLUMN_ARTIST;
	private static final String COLUMN_TYPE = DbConstants.COLUMN_TYPE;

	private static TracksDb INSTANCE;
	private final String TAG = "TracksDb";


	public static synchronized TracksDb getInstance(SQLiteOpenHelper sql) {
		if(INSTANCE == null) {
			INSTANCE = new TracksDb(sql);
		}
		return INSTANCE;
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone instance of this class");
	}

	private TracksDb(SQLiteOpenHelper sql) {
		this.sql = sql;
	}

	// Adding new Track
	public void addTracks(TracksModel track) {
		SQLiteDatabase db = sql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, track.getRealPosition());
		values.put(COLUMN_NAME, track.getSongName()); 
		values.put(COLUMN_PATH, track.getSongFullPath()); 
		values.put(COLUMN_NO, track.getRealPosition());
		values.put(COLUMN_ALBUM_ART, track.getSongAlbumArtPath());
		values.put(COLUMN_ALBUM, track.getSongAlbumName());
		values.put(COLUMN_DURATION, track.getSongDuration());
		values.put(COLUMN_ARTIST, track.getSongArtist());
		values.put(COLUMN_TYPE, track.getType());

		// Inserting Row
		db.insert(TRACKS_TABLE, null, values);
		db.close(); // Closing database connection
	}

	// Getting single track
	public TracksModel getTrack(TracksModel track) {
		SQLiteDatabase db = sql.getReadableDatabase();

		String[] selection =  { COLUMN_ID,COLUMN_NAME, COLUMN_PATH,COLUMN_NO };
		String whereClause = COLUMN_ID + "=?";
		String[] where = { String.valueOf(track.getSongId()) };
		Cursor cursor = db.query(TRACKS_TABLE, selection, whereClause,
														 where, null, null, null, null);
		if(cursor != null)
			cursor.moveToFirst();

		TracksModel tracks = new TracksModel();
		tracks.setSongId(Integer.parseInt(cursor.getString(0)));
		tracks.setSongName(cursor.getString(1));
		tracks.setSongFullPath(cursor.getString(2));

		cursor.close();
		db.close();

		return tracks;
	}


	// Getting All Tracks
	public List<TracksModel> getAllTracks() {
		List<TracksModel> tracksList = new ArrayList<TracksModel>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TRACKS_TABLE;

		SQLiteDatabase db = sql.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if(cursor.moveToFirst()) {
			do {
				TracksModel tracksModel = new TracksModel();
				tracksModel.setSongId(Integer.parseInt(cursor.getString(0)));
				tracksModel.setSongName(cursor.getString(1));
				tracksModel.setSongFullPath(cursor.getString(2));
				tracksModel.setRealPosition(Integer.parseInt(cursor.getString(3)));
				// Adding TracksModel to list
				tracksList.add(tracksModel);
			} while (cursor.moveToNext());
		}

		if(!cursor.isClosed())
			cursor.close();
		db.close();
		// return Tracks list
		return tracksList;
	}

	public ArrayList<PlaylistTrackModel> getPlaylistTracks() {
		ArrayList<PlaylistTrackModel> tracksList = new ArrayList<PlaylistTrackModel>();
		Constants.PLAYLIST_TRACKS.clear();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TRACKS_TABLE;

		SQLiteDatabase db = sql.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if(cursor.moveToFirst()) {
			do {
				PlaylistTrackModel item = new PlaylistTrackModel();
				item.setID(Integer.parseInt(cursor.getString(0)));
				item.setName(cursor.getString(1));
				item.setPath(cursor.getString(2));
				item.setPosition(Integer.parseInt(cursor.getString(3)));
				item.setAlbumArt(cursor.getString(4));
				item.setAlbum(cursor.getString(5));
				item.setDuration(cursor.getString(6));
				item.setArtist(cursor.getString(7));
				item.setType(Integer.parseInt(cursor.getString(8)));
				// Adding TracksModel to list
				tracksList.add(item);
				Constants.PLAYLIST_TRACKS.add(item);
			} while (cursor.moveToNext());
		}

		if(!cursor.isClosed())
			cursor.close();
		db.close();
		// return Tracks list
		return tracksList;
	}

	// Updating single Track
	public int updateTracks(TracksModel TracksModel) {
		SQLiteDatabase db = sql.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, TracksModel.getSongName());
		values.put(COLUMN_PATH, TracksModel.getSongFullPath());

		// updating row
		int row = db.update(TRACKS_TABLE, values, COLUMN_ID + " = ?",
												new String[] { String.valueOf(TracksModel.getSongId()) });
		db.close();
		return row;
	}

	// Deleting single TracksModel
	public void deleteTracks(TracksModel TracksModel) {
		SQLiteDatabase db = sql.getWritableDatabase();
		db.delete(TRACKS_TABLE, COLUMN_ID + " = ?",
							new String[] { String.valueOf(TracksModel.getSongId()) });
		db.close();
	}


	public void deleteDb(Context context) {
		SQLiteDatabase db = sql.getWritableDatabase();
		File file = new File(db.getPath());
		Log.e(TAG, "deleting database - "+DbConstants.PLAYLIST_DB+" ...");
		db.deleteDatabase(file);
		db.close();
		Log.e(TAG, "Database deleted");
		//Utilities.toastLong(context, "Database deleted");
	}


	// Getting TracksModels Count
	public int getTracksCount() {
		String countQuery = "SELECT  * FROM " + TRACKS_TABLE;
		SQLiteDatabase db = sql.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}



}

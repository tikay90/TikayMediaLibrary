package com.tikay.medialibrary.db;
import android.database.sqlite.SQLiteOpenHelper;

public class DbConstants
{
	public static final String PLAYLIST_DB = "Playlist.db";

	public static final String PLAYLIST_TRACKS_DB = "Playlist_Tracks.db";
	public static final String PLAYLIST_TRACKS_TABLE = "Playlist_Tracks_Table";

	public static final String DEFAULT_PLAYLIST_DB = "Default_Playlist.db";
	public static final String DEFAULT_PLAYLIST_TABLE = "Default_Playlist_Table";

	public static final int DATABASE_VERSION = 1;

	// Table Columns names
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_PATH = "path";
	public static final String COLUMN_TRACK_NO = "no_";
	public static final String COLUMN_ALBUM_ART = "album_art";
	public static final String COLUMN_ALBUM = "album";
	public static final String COLUMN_DURATION = "duration";
	public static final String COLUMN_ARTIST = "artist";

	public static final String[] DEFAULT_PLAYLIST = {
		/* 0 */ "Recently Added",
		/* 1 */ "Favourite Tracks",
		/* 3 */ "Most Played",
		/* 4 */ "Recently Played"
	};
	

	public static final String CREATE_DEFAULT_PLAYLIST_TABLE = "CREATE TABLE " + DEFAULT_PLAYLIST_TABLE 
	+ "("
	+ COLUMN_ID + " INTEGER PRIMARY KEY," 
	+ COLUMN_NAME + " TEXT," + COLUMN_TYPE 
	+ " INTEGER" 
	+ ")";



	public static final String CREATE_TRACKS_TABLE = "CREATE TABLE " +  PLAYLIST_TRACKS_TABLE
	+ "("
	+ COLUMN_ID + " INTEGER PRIMARY KEY," 
	+ COLUMN_NAME + " TEXT,"
	+ COLUMN_PATH + " TEXT," 
	+ COLUMN_TRACK_NO + " INTEGER," 
	+ COLUMN_ALBUM_ART + " TEXT," 
	+ COLUMN_ALBUM + " TEXT," 
	+ COLUMN_DURATION + " TEXT," 
	+ COLUMN_ARTIST + " TEXT," 
	+ COLUMN_TYPE + " TEXT"
	+ ")";

}

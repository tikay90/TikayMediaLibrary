package com.tikay.medialibrary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.tikay.medialibrary.utils.Utilities;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = DbConstants.PLAYLIST_DB;
	private static final int DATABASE_VERSION = DbConstants.DATABASE_VERSION;
	private Context context;
	private static DatabaseHelper INSTANCE;

	private String TAG ="DatabaseHelper";
	
	public static synchronized DatabaseHelper getInstance(Context context) {
		if(INSTANCE == null) {
			INSTANCE = new DatabaseHelper(context);
		}

		return INSTANCE;
	}

	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone instance of this class");
	}

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		this.context = context;
		Log.e(TAG, "Database Created");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbConstants.CREATE_TRACKS_TABLE);
		db.execSQL(DbConstants.CREATE_DEFAULT_PLAYLIST_TABLE);
		Log.e(TAG, "Tables Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + DbConstants.PLAYLIST_TRACKS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + DbConstants.DEFAULT_PLAYLIST_TABLE);
		// Create tables again
		onCreate(db);
		Log.e(TAG, "Table upgraded");
		//Utilities.toastShort(context, "Table upgraded");
	}
	
}

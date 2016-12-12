package com.tikay.medialibrary.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.tikay.medialibrary.models.FolderModel;
import java.io.File;
import java.io.FileFilter;

public class ServiceQueryHelper
{
	public static Cursor tracksCursor(Context context) {
		final String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = null;
		if(Utilities.isSdCardPresent()) {
			cursor = context.getContentResolver().query(uri, null, null,  null, sortOrder);
		}
		return cursor;
	}


	public static Cursor albumTracksCursor(Context context, String whereVar) {
		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String where = MediaStore.Audio.Media.ALBUM + "=?";
		String whereVal[] = { whereVar };
		String orderBy = MediaStore.Audio.Media.TITLE;

		Cursor cursor = context.getContentResolver().query(uri, null, where, whereVal, orderBy);

		return  cursor;
	}


	public static Cursor playlistTracksCursor(Context context, long playlistId) {
		Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
		String sortOrder = MediaStore.Audio.Playlists.Members.TITLE + " COLLATE LOCALIZED ASC";

		Cursor cursor = context.getContentResolver().query(uri, null, null, null, sortOrder);

		return cursor;
	}

	public static String folderQuery(Context context, String path) {
		File musicFolder = new File(path);  
		FolderModel model = null;
		File[] list = null;
		String name = null;
		// checking if tempFile is a directory
		if(musicFolder.isDirectory()) { 
			//Utilities.toastShort(context,"In service >< "+ musicFolder.getAbsolutePath());
			list = musicFolder.listFiles(new ServiceQueryHelper().new NoMediaFilter());
			for(int i=0; i < list.length; i++) {
				int count = getAudioFileCount(context,list[i].getAbsolutePath());
				if(count != 0) {
					name = list[i].getName();
					String n = name.substring(0, name.lastIndexOf('.'));
					Log.w("ServiceQueryHelper", n);
					model = new FolderModel();
					model.setFolderName(n);
					model.setFolderPath(list[i].getAbsolutePath());
					//listOfFolders.add(model);
					//listOfSongs.add(model);
				}
			}
		}
		return path;
	}
	
	
	public static int getAudioFileCount(Context c,String dirPath) {
		String selection =MediaStore.Audio.Media.DATA + " like ?";
		String[] projection = {MediaStore.Audio.Media.IS_MUSIC};    
		String[] selectionArgs={dirPath + "%"};
		Cursor cursor = c.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
		int count = cursor.getCount();
		//Log.i(TAG, "Count = "+count);
		cursor.close();
		return count;
  }

	public class NoMediaFilter implements FileFilter
	{
    @Override
    public boolean accept(File fileName) {
			// if we are looking at a directory/file that's not hidden we want to see it so return TRUE
			if(!fileName.getName().startsWith(".")) {
				return true;
			}
			return false;
    }      
	}
	
}

package com.tikay.medialibrary.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.tikay.medialibrary.models.FolderModel;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

public class DirectoriesTask extends AsyncTask<Void,Void,ArrayList<FolderModel>>
{
	private static ArrayList<File> MUSIC_FILES = new ArrayList<File>();
	private static ArrayList<File> UNIQUE_MUSIC_FILES;
	private static String TAG = DirectoriesTask.class.getSimpleName();
	private static Context context;

	public DirectoriesTask(Context context) {
		this.context = context;
	}

	private static ArrayList<File> getFolders() {
		ArrayList<File> tempFile = new ArrayList<File>();
		ArrayList<TracksModel> allTracks = MyMediaQuery.getAllTracks(context);
		
		for(int i=0;i < allTracks.size();i++) {
			TracksModel tm = allTracks.get(i);
			String path = tm.getSongFullPath();
			File file = new File(path);
			File parent = file.getParentFile();
			if(filterFiles(parent)) {
				tempFile.add(parent);
			}

		}
		return tempFile;
	}

	private static boolean filterFiles(File file) {
		if(file.isDirectory() && !file.getName().equalsIgnoreCase("Notifications") && !file.getName().equalsIgnoreCase("Ringtones") && !file.isHidden()) {
			return true;
		}		
		return false;
	}


	private static ArrayList<FolderModel> filterMusicFolders() {
		/* passing folders/directories containing music to linkedhashset 
		 to remove duplicates and maintain entry position
		 */
		ArrayList<FolderModel> folderList = new ArrayList<FolderModel>();
		MUSIC_FILES = getFolders();
		LinkedHashSet<File> linkedHashsets = new LinkedHashSet<File>(MUSIC_FILES);
		UNIQUE_MUSIC_FILES  = new ArrayList<File>(linkedHashsets);
		ArrayList<File> sortedList = new ArrayList<File>(UNIQUE_MUSIC_FILES);
		Collections.sort(sortedList);
		for(File folder : sortedList) {
			FolderModel folderModel = new FolderModel();
			folderModel.setFolderName(folder.getName());
			folderModel.setFolderPath(folder.getAbsolutePath());
			//folderModel.setFolderImg(decodeResource);
			folderList.add(folderModel);
			Log.w(TAG, "DirectoriesTask: <<<<<>>> PARENT FOLDER <<<<<>>>>  " + folder);
		}
		return folderList;
	}


	@Override
	protected ArrayList<FolderModel> doInBackground(Void[] p1) {
		return filterMusicFolders();
	}

	@Override
	protected void onPostExecute(ArrayList<FolderModel> result) {
		super.onPostExecute(result);
		try {
			if(result != null) {
				Constants.F_MUSIC_FOLDERS = result;
				Constants.S_MUSIC_FOLDERS = result;
				Log.e(TAG, TAG + " <<<<<<>>>>> RESULTS not NULL");
				Log.e(TAG, TAG + " <<<<<<>>>>> RESULTS = " + result.size());
			} else {
				Log.e(TAG, TAG + " <<<<<<>>>>> RESULTS = NULL");
			}

		} catch(Exception e) {
			Utilities.toastLong(context, "Error in MainDirLoadTask: " + e.toString());
			Log.e(TAG, "Error In DirectoriesTask: " + e.toString());
		}
	}


}

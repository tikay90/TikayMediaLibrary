package com.tikay.medialibrary.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.tikay.medialibrary.models.FolderModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.StorageUtil;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class MainDirLoadTask extends AsyncTask<Void,Void,ArrayList<FolderModel>>
{
	private static ArrayList<File> MUSIC_FILES = new ArrayList<File>();
	private static ArrayList<File> UNIQUE_MUSIC_FILES;
	private String TAG = "MainDirLoadTask";
	private Context context;
	ProgressDialog progressDialog;

	public MainDirLoadTask(Context context) {
		this.context = context;
	}


	private void listMusicFolders(String path) {
		Log.e(TAG, "MainDirLoadTask - PARENT STORAGE:   ==>  " + path);
		File file = new File(path);
		if(file.exists()) {
			File[] listFiles = file.listFiles(new AudioFilter());
			for(File file2 : listFiles) {
				if(Utilities.getAudioFileCount(context, file2.getAbsolutePath()) != 0) {
					getFilesRecursive(file2);
				}
			}
		}
	}

	private void getFilesRecursive(File file) {
		File[] listFiles = file.listFiles(new AudioFilter());
		for(File file2 : listFiles) {
			if(file2.isDirectory()) {
				getFilesRecursive(file2);
			} else {
				MUSIC_FILES.add(file2.getParentFile());
			}
		}
	}


	private void filterMusicFolders() {
		/* passing folders/directories containing music to linkedhashset 
		 to remove duplicates and maintain entry position
		 */
		LinkedHashSet<File> linkedHashsets = new LinkedHashSet<File>(MUSIC_FILES);
		UNIQUE_MUSIC_FILES  = new ArrayList<File>(linkedHashsets);
		for(File folders : UNIQUE_MUSIC_FILES) {
			Log.w(TAG, "MainDirLoadTask - FolderList  >>>>>  " + folders);
		}
	}

	private ArrayList<FolderModel> queryMusicFolder() {
		ArrayList<FolderModel> folderList = new ArrayList<FolderModel>();
		try {
			for(String dir: StorageUtil.getExtStorageDirectories(context)) {
				listMusicFolders(dir);
				Log.e(TAG, TAG + " DIRECTORIES: ==> " + dir);
			}
			filterMusicFolders();

			for(File file : UNIQUE_MUSIC_FILES) {
				FolderModel folderModel = new FolderModel();
				folderModel.setFolderName(file.getName());
				folderModel.setFolderPath(file.getAbsolutePath());
				//folderModel.setFolderImg(decodeResource);
				folderList.add(folderModel);
			}

			//getFolderList(folderList);
		} catch(Exception e) {
			Log.e(TAG, "In MYAPP queryMusicFolder(): " + e.toString());
		}
		//ArrayList<FolderModel> folderList = new DirectoriesTask(context).filterMusicFolders();
		return folderList;
	}



	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //Set style
		progressDialog.setTitle("Searching Music Folders");
		progressDialog.setMessage("Please wait ......"); //Message
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		//progressDialog.show();#

	}

	@Override
	protected ArrayList<FolderModel> doInBackground(Void[] p1) {
		return queryMusicFolder();
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


			if(progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

		} catch(Exception e) {
			Utilities.toastLong(context, "Error in MainDirLoadTask: " + e.toString());
			Log.e(TAG, "Error In MainDirLoadTask: " + e.toString());
		}
	}





	private class AudioFilter implements FileFilter
	{
		private String[] extension = new String[]{".mp3", ".m4a", ".acc"};

		@Override
		public boolean accept(File file) {
			if(file.isDirectory() && !file.getName().equalsIgnoreCase("Notifications") && !file.getName().equalsIgnoreCase("Ringtones") && !file.isHidden()) {
				return true;
			}
			for(String endsWith : extension) {
				if(file.getName().toLowerCase().endsWith(endsWith)) {
					return true;
				}
			}
			return false;
		}

	}


	private class NoMediaFilter implements FileFilter
	{
		@Override
		public boolean accept(File file) {
			if(file.getName().startsWith(".")) {
				return false;
			}
			return true;
		}
	}



}

package com.tikay.medialibrary.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import com.google.gson.Gson;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.FolderModel;
import com.tikay.medialibrary.recycler_adapter.FolderAdapter;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class FolderDetailActivity extends AppCompatActivity 
{
	private String TAG = FolderDetailActivity.class.getSimpleName();
	private FolderAdapter adapter;
	private String[] array = new String[]{"Edit", "Delete", "Send"};
	private Intent broadcastIntent;

	private ArrayList<FolderModel> listOfFiles = new ArrayList<FolderModel>();
	private RecyclerView recyclerView;
	String directoryPath;
	private String json;
	private	Gson gson = new Gson();
	private	SharedPreferences sharedPreferences;
	private int pos = 0;
	private  ArrayList<File> MUSIC_FILES = new ArrayList<File>();
	private final String KEY_LIST = "key_list";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folder_single_pane);
		
		setUpWidgets();
		
		try {
			broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
			if(savedInstanceState == null) {
			directoryPath = getFolderPath();
				new MusicLoaderTask().execute();
			} else {
				restoreState(savedInstanceState);
			}

		} catch(Exception e) {
			Log.e(TAG, e.toString());
			Utilities.toastLong(this, "FolderPort <<<>>>  " + e.toString());
		}
		
	}
	
	private String getFolderPath() {
		Intent i = getIntent();
		String path = i.getStringExtra("folderPath");

		return path;
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		//Log.i(Constants.Folders, "IN Folders - onSaveInstanceState() CALLED");
		savedInstanceState.putParcelableArrayList(KEY_LIST, listOfFiles);
		super.onSaveInstanceState(savedInstanceState);
		Log.e(TAG, "In folderList onSaveInstanceState() called");
		Utilities.toastShort(this, "In folderDetails onSaveInstanceState() called");
	}

	private void restoreState(Bundle bundle) {
		Utilities.toastShort(this, "In folderPort restoreState() called");
		Log.e(TAG, "In folderList restoreState() called");
		try {
			listOfFiles = bundle.getParcelableArrayList(KEY_LIST);
			initRecyclerView(listOfFiles);
		} catch(Exception e) {
			Log.e(TAG, TAG + " >>>  error restoring state:  " + e.toString());
		}
	}


	private void setUpWidgets() {
		try {
			recyclerView = (RecyclerView) findViewById(R.id.rv_fsp);
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			Utilities.toastLong(this, e.toString());
		}
	}

	private void initRecyclerView(ArrayList<FolderModel> folderList) {
		if(folderList != null) {
			adapter = new FolderAdapter(this, folderList);
			recyclerView.setLayoutManager(new LinearLayoutManager(this));
			recyclerView.setItemAnimator(new DefaultItemAnimator());
			//recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);
		}
	}
	
	
	private ArrayList<File> listMusicFiles(String path) {
		ArrayList<File> fileList = new ArrayList<File>();
		//Log.e(TAG, "FolderPort - PARENT STORAGE:   ==>  " + path);
		File file = new File(path);
		if(file.exists()) {
			File[] listFiles = file.listFiles(new AudioFilter());
			for(File file2 : listFiles) {
				//Log.e(TAG, "FolderPort - FILE NAME:   ==>  " + file2.getName());
				if(Utilities.getAudioFileCount(this, file2.getAbsolutePath()) != 0) {
					fileList.add(file2);
				}
			}
		}
		return fileList;
	}
	
	private ArrayList<FolderModel> queryMusicFiles() {
		ArrayList<FolderModel> folderList = new ArrayList<FolderModel>();
		try {
			MUSIC_FILES = listMusicFiles(directoryPath);
			Constants.FOLDER_TRACKS.clear();
			for(File file : MUSIC_FILES) {
				String namePath = file.getAbsolutePath();
				FolderModel folderModel = new FolderModel();
				String name = file.getName();
				String fileName = name.substring(0, name.lastIndexOf('.'));
				folderModel.setFolderName(fileName);
				folderModel.setFolderPath(file.getAbsolutePath());
				folderModel.setFolderShortPath(file.getParentFile().getName()+"/"+name);
				folderModel.setAlbumArt(Utilities.getArtFromMusicFile(this, namePath));
				folderModel.setType(1);

				int k = 0;
				for(int j=0; j < folderList.size();j++) {
					k++;
				}
				folderModel.setFolderPosition(k);

				folderList.add(folderModel);
			}

		} catch(Exception e) {
			Log.e(TAG, "In FolderPort queryMusicFolder(): " + e.toString());
		}
		return folderList;
	}
	
	private class MusicLoaderTask extends AsyncTask<Void,Void,ArrayList<FolderModel>>
	{

		@Override
		protected ArrayList<FolderModel> doInBackground(Void[] p1) {
			return queryMusicFiles();
		}

		@Override
		protected void onPostExecute(ArrayList<FolderModel> result) {
			super.onPostExecute(result);
			if(result != null) {
				listOfFiles = result;
				Constants.FOLDER_TRACKS.clear();
				Constants.FOLDER_TRACKS = result;
				initRecyclerView(listOfFiles);
				//Constants.F_MUSIC_FOLDERS = result;
				//Constants.S_MUSIC_FOLDERS = result;
				Log.e(TAG, TAG + " <<<<<<>>>>> RESULTS not NULL");
				Log.e(TAG, TAG + " <<<<<<>>>>> RESULTS = " + result.size());
			} else {
				Log.e(TAG, TAG + " <<<<<<>>>>> RESULTS = NULL");
			}
		}
	}
	
	
	private class AudioFilter implements FileFilter
	{
		private String[] extension = new String[]{".mp3", ".m4a", ".acc",".mid"};

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

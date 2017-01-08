package com.tikay.medialibrary.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import com.google.gson.Gson;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.models.FolderModel;
import com.tikay.medialibrary.recycler_adapter.FolderAdapter;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class FolderDetail extends Fragment implements OnItemTouchListener
{
	private String TAG = FolderDetail.class.getSimpleName();
	private FolderAdapter adapter;
	private String[] array = new String[]{"Edit", "Delete", "Send"};
	private Intent broadcastIntent;

	private ArrayList<FolderModel> listOfFiles = new ArrayList<FolderModel>();
	private RecyclerView recyclerView;
	private View v;
	String directoryPath;
	private String json;
	private	Gson gson = new Gson();
	private	SharedPreferences sharedPreferences;
	private int pos = 0;
	private  ArrayList<File> MUSIC_FILES = new ArrayList<File>();

	private final String KEY_LIST = "key_list";


	public static FolderDetail newInstance(String path) {
		FolderDetail fp = new FolderDetail();
		// Supply path input as an argument.
		Bundle args = new Bundle();
		args.putString("path", path);
		fp.setArguments(args);

		return fp;	
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		//Utilities.toastShort(getActivity(),"In FolderPort: onCreate() CALLED");
		setRetainInstance(true);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.universal_folder, container, false);
		setUpWidgets();
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
			//directoryPath = getArguments().getString("path", null);
			//new DirLoadTask().execute();
			if(savedInstanceState == null) {
				directoryPath = getArguments().getString("path", null);
				new MusicLoaderTask().execute();
			} else {
				restoreState(savedInstanceState);
			}
			
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			Utilities.toastLong(getContext(), "FolderPort <<<>>>  " + e.toString());
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		//Log.i(Constants.Folders, "IN Folders - onSaveInstanceState() CALLED");
		savedInstanceState.putParcelableArrayList(KEY_LIST, listOfFiles);
		super.onSaveInstanceState(savedInstanceState);
		Log.e(TAG, "In folderList onSaveInstanceState() called");
		Utilities.toastShort(getActivity(), "In folderDetails onSaveInstanceState() called");
	}

	private void restoreState(Bundle bundle) {
		Utilities.toastShort(getActivity(), "In folderPort restoreState() called");
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
			recyclerView = (RecyclerView) v.findViewById(R.id.universal_frv);
			recyclerView.addOnItemTouchListener(this);
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			Utilities.toastLong(getActivity(), e.toString());
		}
	}

	private void initRecyclerView(ArrayList<FolderModel> folderList) {
		if(folderList != null) {
			adapter = new FolderAdapter(getActivity(), folderList);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
				if(Utilities.getAudioFileCount(getActivity(), file2.getAbsolutePath()) != 0) {
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
				folderModel.setAlbumArt(Utilities.getArtFromMusicFile(getActivity(), namePath));
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


	GestureDetector gestureDetector = new GestureDetector(getActivity(), new SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent motionEvent) {
				return true;
			}
    });

	@Override
	public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
		View findChildViewUnder = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
		if(findChildViewUnder != null && gestureDetector.onTouchEvent(motionEvent)) {
			try {
				pos = recyclerView.getChildAdapterPosition(findChildViewUnder);
				File musicFile = new File(listOfFiles.get(pos).getFolderPath());  
				//Utilities.toastShort(getActivity(), musicFile.getAbsolutePath().toString());

				try {
					Constants.CURRENT_FOLDER_TRACKS.clear();
					Constants.CURRENT_FOLDER_TRACKS.addAll(Constants.FOLDER_TRACKS);
					//Log.e("Folder", "recentFolderTracks --- number : " + Constants.CURRENT_FOLDER_TRACKS.size());
					broadcastIntent.putExtra(Constants.SEND, "folder_track_position")
						.putExtra("folder_position", Constants.CURRENT_FOLDER_TRACKS.get(pos).getFolderPosition());
					//Utilities.toastShort(getActivity(), "position: " + pos);
					String name1 = musicFile.getName();
					String name = name1.substring(0, name1.lastIndexOf('.'));
					//Log.i("In onItemClick - Name : " + "File Playing", name1);
					Utilities.toastShort(getActivity(),  name);
					getActivity().sendBroadcast(broadcastIntent);
					//String bitrate = Utilities.getBitrate(pathToSong);
					//Utilities.toastShort(getActivity(), "" + bitrate + " KBPS  mp3");
				} catch(Exception e) {
					Utilities.toastShort(getActivity(), "Invalid file  " + e.toString());
				}

			} catch(Exception e2) {
				Utilities.toastShort(getActivity(), new StringBuffer().append("In onItemClick()  ").append(motionEvent.toString()).toString());
				Log.e("In FolderPort: onItemClick - Exception : Error message", e2.getMessage());
			}
		}
		return false;
	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean z) {
	}

	@Override
	public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
	}

	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo) {
		if(view.getId() == R.id.pathlist) {
			try {
				contextMenu.setHeaderTitle(listOfFiles.get(((AdapterContextMenuInfo) contextMenuInfo).position).getFolderName());
				String[] strArr = array;
				for(int i = 0; i < strArr.length; i++) {
					contextMenu.add(0, i, i, strArr[i]);
				}
			} catch(Exception e) {
				e.printStackTrace();
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

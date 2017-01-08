package com.tikay.medialibrary.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import com.tikay.medialibrary.db.DbConstants;
import com.tikay.medialibrary.models.FolderModel;
import com.tikay.medialibrary.recycler_adapter.FolderAdapter;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.StorageUtil;
import com.tikay.medialibrary.utils.Utilities;
import de.hdodenhof.circleimageview.R;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Folders extends Fragment implements OnItemTouchListener, OnClickListener
{
	private static ArrayList<File> MUSIC_FILES = new ArrayList<File>();
	private String TAG = Folders.class.getSimpleName();
	private static ArrayList<File> UNIQUE_MUSIC_FILES;
	private FolderAdapter adapter;
	private String[] array = new String[]{"Edit", "Delete", "Send"};
	private Intent broadcastIntent;
	private ImageButton btnBack;
	private int currentPosition = 0;

	private ArrayList<FolderModel> listOfFolders = new ArrayList<FolderModel>();
	private String path = "";
	private TextView pathTextView;
	private int pos = 0;
	private RecyclerView recyclerView;
	private boolean showButton = false;
	private View v;
	private String PATH = "path";


	@Override
	public void onCreate(Bundle bundle) {
		Utilities.writeLogcatToFile(TAG);
		Log.i(Constants.Folders, "IN Folders - onCreate() CALLED");
		super.onCreate(bundle);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
		Log.i(Constants.Folders, "IN Folders - onCreateView() CALLED");
		v = layoutInflater.inflate(R.layout.folder_recycler_view, viewGroup, false);
		setUpWidgets();
		return v;
	}

	@Override
	public void onActivityCreated(Bundle bundle) {
		super.onActivityCreated(bundle);
		try {
			broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
			if(bundle == null) {
				new DirLoadTask().execute();
				//StorageUtil.getExtStorageDirectories(getActivity());
			} else {
				restoreState(bundle);
			}
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			Utilities.toastLong(getContext(), "Folders <<<>>>  " + e.toString());
		}
	}

	private void setUpWidgets() {
		try {
			recyclerView = (RecyclerView) v.findViewById(R.id.universal_frv);
			recyclerView.addOnItemTouchListener(this);
			pathTextView = (TextView) v.findViewById(R.id.path);
			pathTextView.setTextColor(Color.BLUE);
			btnBack = (ImageButton) v.findViewById(R.id.btnBack);
			btnBack.setOnClickListener(this);
			showButton = false;
			btnBack.setVisibility(View.GONE);

			String str = "storage/";
			String simpleName = Folders.class.getSimpleName();
			SharedPreferences prefs = getActivity().getSharedPreferences(simpleName, Context.MODE_PRIVATE);
			path = prefs.getString("folder_path", str);
			//Bitmap decodeResource = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.fb_sdcard);
			pathTextView.setText(path);
		} catch(Exception e) {
			Log.e(TAG, e.toString());
			Utilities.toastLong(getActivity(), e.toString());
		}
	}

	/*
	 private void setUp() {
	 if(Utilities.isSdCardPresent()) {
	 try {
	 /*	if(TextUtils.isEmpty(root_sd)) {
	 root_sd = System.getenv("EXTERNAL_STORAGE");
	 } else {
	 Log.e(TAG, root_sd);
	 }
	 listMusicFolders(root_sd);
	 *
	 try {
	 /*secStore = FolderUtil.getMemoryCardPath();
	 if(TextUtils.isEmpty(secStore)) {
	 Log.i(TAG, "extSdCard not present");
	 } else {
	 Log.e(TAG, secStore);
	 if(new File(secStore).length() > ((long) 0)) {
	 Log.i(TAG, "Files available");
	 listMusicFolders(secStore);
	 } else {
	 Log.e(TAG, "No files available");
	 }

	 }

	 } catch(Exception e) {
	 Utilities.toastLong(getContext(), "Error Accessing Tracks on ExtSdCard:  "+e.toString());
	 Log.e(TAG, "Error Accessing Tracks on ExtSdCard:  " +e.toString());
	 }
	 *
	 for(String dir: StorageUtil.getExtStorageDirectories(getActivity())){
	 listMusicFolders(dir);
	 }
	 filterMusicFolders();
	 init();
	 } catch(Exception e2) {
	 Utilities.toastLong(getContext(), "Folders <<<>>> error @ " + e2.toString());
	 Log.e(TAG, TAG + " <<<>>> error @ " + e2.toString());
	 }
	 }
	 }
	 */

	private void listMusicFolders(String path) {
		Log.e(TAG, "PARENT STORAGE  ==>  " + path);
		File file = new File(path);
		if(file.exists()) {
			File[] listFiles = file.listFiles(new AudioFilter());
			for(File file2 : listFiles) {
				if(Utilities.getAudioFileCount(getContext(), file2.getAbsolutePath()) != 0) {
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
			Log.w("MAIN", "Folders  >>>>>  " + folders);
		}
	}

	private void initRecyclerView(ArrayList<FolderModel> listOfFolders) {
		if(listOfFolders != null) {
			adapter = new FolderAdapter(getActivity(), listOfFolders);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
			recyclerView.setItemAnimator(new DefaultItemAnimator());
			recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);
		}
	}


	private void init() {
		try {
			listOfFolders.clear();
			for(File file : UNIQUE_MUSIC_FILES) {
				FolderModel folderModel = new FolderModel();
				folderModel.setFolderName(file.getName());
				folderModel.setFolderPath(file.getAbsolutePath());
				//folderModel.setFolderImg(decodeResource);
				listOfFolders.add(folderModel);
				Constants.FOLDER_TRACKS.add(folderModel);
			}
			initRecyclerView(listOfFolders);

		} catch(Exception e) {
			Log.e("In init()  ", "In init()  " + e.toString());
		}
	}

	private class DirLoadTask extends AsyncTask<Void,Void,Void>
	{
		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //Set style
			progressDialog.setTitle("Searching Music Folders");
			progressDialog.setMessage("Please wait ......"); //Message
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			//progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void[] p1) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			listOfFolders = Constants.F_MUSIC_FOLDERS;
			initRecyclerView(listOfFolders);
			if(progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		Log.i(Constants.Folders, "IN Folders - onSaveInstanceState() CALLED");
		bundle.putParcelableArrayList("folders", listOfFolders);
		bundle.putString(PATH, path);
		bundle.putBoolean("isShowing", showButton);
		super.onSaveInstanceState(bundle);
	}

	private void restoreState(Bundle bundle) {
		try {
			listOfFolders = bundle.getParcelableArrayList("folders");
			path = bundle.getString(PATH);
			showButton = bundle.getBoolean("isShowing");
			btnBack.setVisibility(!showButton ? View.GONE : View.VISIBLE);
			initRecyclerView(listOfFolders);
			pathTextView.setText(path);
		} catch(Exception e) {
			Log.e(TAG, TAG + " >>>  error restoring state:  " + e.toString());
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
				File musicFile = new File(listOfFolders.get(pos).getFolderPath());  
				FolderModel model = null;
				File[] list = null;
				String name = null;
				// checking if tempFile is a directory
				if(musicFile.isDirectory()) { 
					Utilities.toastShort(getActivity(), musicFile.getAbsolutePath().toString());
					//Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.music_m_logo);
					list = musicFile.listFiles(new NoMediaFilter());
					listOfFolders.clear();
					Constants.FOLDER_TRACKS.clear();
					for(int i=0; i < list.length; i++) {
						int count = Utilities.getAudioFileCount(getContext(), list[i].getAbsolutePath());
						if(count != 0) {
							name = list[i].getName();
							String newName = name.substring(0, name.lastIndexOf('.'));
							String namePath = list[i].getAbsolutePath();
							model = new FolderModel();
							model.setFolderName(newName);
							model.setFolderPath(namePath);
							model.setAlbumArt(Utilities.getArtFromMusicFile(getActivity(), namePath));
							//model.setFolderImg(Utilities.getEmbeddedSongArt(getContext(), namePath));
							//model.setFolderImg(bmp);
							//new FoldersBitmapFactory(getContext(), list[i].getAbsolutePath(), model).execute();
							int k = 0;
							for(int j=0; j < listOfFolders.size();j++) {
								k++;
							}
							listOfFolders.add(model);
							model.setFolderPosition(k);
							Constants.FOLDER_TRACKS.add(model);
						}
					}
					//Log.e(Constants.Folders, "onItemClick --- number of files: " + Constants.FOLDER_TRACKS.size());
					path = musicFile.getAbsolutePath();
					pathTextView.setText(path);
					initRecyclerView(listOfFolders);
				} else {
					try {
						Constants.CURRENT_FOLDER_TRACKS.clear();
						Constants.CURRENT_FOLDER_TRACKS.addAll(Constants.FOLDER_TRACKS);
						//Log.e("Folder", "recentFolderTracks --- number : " + Constants.CURRENT_FOLDER_TRACKS.size());
						currentPosition = 0;
						broadcastIntent.putExtra(Constants.SEND, "folder_track_position").putExtra("folder_position", Constants.FOLDER_TRACKS.get(pos).getFolderPosition());
						//Utilities.toastShort(getActivity(), "position: " + pos);
						String name1 = musicFile.getName();
						String n = name1.substring(0, name1.lastIndexOf('.'));
						//Log.i("In onItemClick - Name : " + "File Playing", name1);
						Utilities.toastShort(getActivity(), "Song name: " + n);
						getActivity().sendBroadcast(broadcastIntent);
						//String bitrate = Utilities.getBitrate(pathToSong);
						//Utilities.toastShort(getActivity(), "" + bitrate + " KBPS  mp3");
					} catch(Exception e) {
						Utilities.toastShort(getActivity(), "Invalid file  " + e.toString());
					}
				}
				btnBack.setVisibility(View.VISIBLE);
				showButton = true;
				registerForContextMenu(recyclerView);
			} catch(Exception e2) {
				Utilities.toastShort(getActivity(), new StringBuffer().append("In onItemClick()  ").append(motionEvent.toString()).toString());
				Log.e("In onItemClick - Exception : Error message", e2.getMessage());
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
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btnBack /*2131099816*/:
				try {
					showButton = false;
					btnBack.setVisibility(View.GONE);
					Bitmap decodeResource = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.fb_sdcard);
					listOfFolders.clear();
					Constants.FOLDER_TRACKS.clear();
					for(File file : UNIQUE_MUSIC_FILES) {
						FolderModel folderModel = new FolderModel();
						folderModel.setFolderName(file.getName());
						folderModel.setFolderPath(file.getAbsolutePath());
						//folderModel.setFolderImg(decodeResource);
						listOfFolders.add(folderModel);
						Constants.FOLDER_TRACKS.add(folderModel);
					}
					path = "storage/ ";
					pathTextView.setText(path);
					recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
					recyclerView.setItemAnimator(new DefaultItemAnimator());
					recyclerView.setHasFixedSize(true);
					recyclerView.setAdapter(adapter);
				} catch(Exception e) {
					Utilities.toastShort(getActivity(), "Can't go beyond root directory ;(  ");
				}
				break;
			default:
				return;
		}
	}


	@Override
	public void onDestroyView() {
		Log.i(TAG, "IN Folders onDestoyView() CALLED");
		super.onDestroyView();
	}


	// onDestroy
	@Override
	public void onDestroy() {
		try {
			Log.i(TAG, "IN Folders onDestoy() CALLED");
			//Debug.stopMethodTracing();
		} catch(Exception e) {
			Utilities.toastLong(getActivity(), "In onDestroy()  " + e.toString());
			Log.e(TAG, "IN Folders onDestoy() " + e.getMessage());
			e.printStackTrace();
		}

		super.onDestroy();
	}


	@Override
	public void onResume() {
		Log.i(Constants.Folders, "IN Folders - onResume() CALLED");
		super.onResume();
	}

	@Override
	public void onStop() {
		Log.i(Constants.Folders, "IN Folders - onStop() CALLED");
		super.onStop();
	}




	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo) {
		if(view.getId() == R.id.pathlist) {
			try {
				contextMenu.setHeaderTitle(listOfFolders.get(((AdapterContextMenuInfo) contextMenuInfo).position).getFolderName());
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

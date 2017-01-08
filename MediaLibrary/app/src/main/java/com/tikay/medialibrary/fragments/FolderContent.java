package com.tikay.medialibrary.fragments;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.tikay.medialibrary.activities.FolderSinglePane;
import com.tikay.medialibrary.models.FolderModel;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.recycler_adapter.FolderContentAdapter;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class FolderContent extends Fragment implements OnItemTouchListener
{
	private String TAG = FolderContent.class.getSimpleName();
	private FolderContentAdapter adapter;
	private String[] array = new String[]{"Edit", "Delete", "Send"};
	View dualPane,singlePane;
	private ArrayList<FolderModel> listOfFolders = new ArrayList<FolderModel>();
	private int pos = 0;
	private RecyclerView recyclerView;
	private View v;
	private boolean isDualPane;
	private boolean isSinglePane;
	private final String SAVED_STATE_KEY = "saved_state_key";
	private final String LIST_KEY = "list_key";
	private String json;
	private	Gson gson = new Gson();
	private	SharedPreferences sharedPreferences;
	private int previousPos = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Utilities.writeLogcatToFile(TAG);
		Log.i(TAG, "IN FolderList - onCreate() CALLED");
		super.onCreate(savedInstanceState);
		//setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.universal_folder, container, false);
		//dual_pane = v.findViewById(R.id.dual_pane);
		setUpWidgets();

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		try {
			//isDual = getActivity().getResources().getBoolean(R.bool.isDual);
			//Utilities.toastLong(getContext(), "" + getParentFragment());

			dualPane = getParentFragment().getView().findViewById(R.id.dual_pane);
			singlePane = getParentFragment().getView().findViewById(R.id.single_pane);
			isDualPane = dualPane != null && dualPane.getVisibility() == View.VISIBLE;
			isSinglePane = singlePane != null && singlePane.getVisibility() == View.VISIBLE;

			if(savedInstanceState == null) {
				Utilities.toastShort(getContext(), "In folderList: savedInstanceState = null");
				new MusicDirectoriesTask(getContext()).execute();
			} else {
				Utilities.toastShort(getContext(), "In folderList: savedInstanceState not null");
				restoreState(savedInstanceState);
			}



		} catch(Exception e) {
			Log.e(TAG, e.toString());
			Utilities.toastLong(getContext(), "FolderList <<<>>>  " + e.toString());
		}
	}



	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		//Log.i(Constants.Folders, "IN Folders - onSaveInstanceState() CALLED");
		savedInstanceState.putParcelableArrayList(LIST_KEY, listOfFolders);
		savedInstanceState.putInt("pos", pos);
		//savedInstanceState.putInt("pos", previousPos);
		super.onSaveInstanceState(savedInstanceState);
		Log.e(TAG, "In folderList onSaveInstanceState() called");
		Utilities.toastShort(getActivity(), "In folderList onSaveInstanceState() called");
	}

	private void restoreState(Bundle bundle) {
		Utilities.toastShort(getActivity(), "In folderList restoreState() called");
		Log.e(TAG, "In folderList restoreState() called");
		try {
			listOfFolders = bundle.getParcelableArrayList(LIST_KEY);
			pos = bundle.getInt("pos", 0);
			initRecyclerView(listOfFolders);
			if(isDualPane) {
				getPrefs();
				listMusicFiles(pos);
			}

		} catch(Exception e) {
			Log.e(TAG, TAG + " >>>  error restoring state:  " + e.toString());
		}
	}


	private void savePrefs() {
		sharedPreferences = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("pos", pos);
		editor.putInt("prev", previousPos);
		editor.commit();
	}

	private void getPrefs() {
		sharedPreferences = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
		pos = sharedPreferences.getInt("pos", 0);
		previousPos = sharedPreferences.getInt("prev", 0);
	}


	@Override
	public void onPause() {
		savePrefs();
    super.onPause();
		//SavedState savedState = getFragmentManager().saveFragmentInstanceState(FolderList.this);
		//MyApp ma = (MyApp) getActivity().getApplication();
		//ma.setFragmentSavedState(SAVED_STATE_KEY, savedState);
    //((MyApp) getActivity().getApplication()).setFragmentSavedState(SAVED_STATE_KEY, getFragmentManager().saveFragmentInstanceState(FolderList.this));
	}


	private void setUpWidgets() {
		recyclerView = (RecyclerView) v.findViewById(R.id.universal_frv);
		recyclerView.addOnItemTouchListener(this);
	}

	private void initRecyclerView(ArrayList<FolderModel> folderList) {
		if(folderList != null) {
			adapter = new FolderContentAdapter(getActivity(), folderList);
			recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
			recyclerView.setItemAnimator(new DefaultItemAnimator());
			//recyclerView.setHasFixedSize(true);
			recyclerView.setAdapter(adapter);
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
				isDualPane = dualPane != null && dualPane.getVisibility() == View.VISIBLE;
				isSinglePane = singlePane != null && singlePane.getVisibility() == View.VISIBLE;
				// preventing reload of fragment if positon in the adapter is the same
				if(isDualPane) {
					if(pos != previousPos)
						listMusicFiles(pos);

					previousPos = pos;
				} else {
					listMusicFiles(pos);
				}
				registerForContextMenu(recyclerView);
			} catch(Exception e) {
				Utilities.toastShort(getActivity(), "In onItemClick()  " + e.toString());
				Log.e("In FolderList: onItemClick - Exception : Error message", e.getMessage());
			}
		}
		return false;
	}


	private void listMusicFiles(int position) {
		File musicFile = new File(listOfFolders.get(position).getFolderPath());  
		String folderPath = musicFile.getAbsolutePath().toString();
		String folderName = musicFile.getName();

		try {

			/** 
			 Checking whether parent Fragment (i.e MasterFragment) layout is dual pane (isDualPane)
			 or single pane (isSinglePane) and decide how to launch/display FolderDetail
			 */
			dualPane = getParentFragment().getView().findViewById(R.id.dual_pane);
			singlePane = getParentFragment().getView().findViewById(R.id.single_pane);
			if(isDualPane) {
				FolderDetail folderPort = FolderDetail.newInstance(folderPath);
				// using getChildFragmentManager() crashed the app
				FragmentManager fm = getActivity().getSupportFragmentManager(); //FragmentManager fm = getChildFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				//Utilities.toastShort(getActivity(), "isDualPane = " + isDualPane);
				//Utilities.toastShort(getActivity(), "isSinglePane = " + isSinglePane);
				ft.replace(R.id.fragment_detail, folderPort);
				ft.commit();
			} else  {
				//Utilities.toastShort(getActivity(), "isSinglePane = " + isSinglePane);
				//Utilities.toastShort(getActivity(), "isDualPane = " + isDualPane);
				/*ft.replace(R.id.fragment_content, folderPort);
				 ft.hide(this);
				 ft.addToBackStack(null);*/
				Intent intent = new Intent(getContext(), FolderSinglePane.class);
				intent.putExtra("folderPath", folderPath).putExtra("name",folderName);
				getContext().startActivity(intent);
			}


		} catch(Exception e) {
			Utilities.toastShort(getActivity(), e.toString());
			Log.e(TAG, e.toString());
		}
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



	public class MusicDirectoriesTask extends AsyncTask<Void,Void,ArrayList<FolderModel>>
	{
		private ArrayList<File> MUSIC_FOLDERS = new ArrayList<File>();
		private ArrayList<File> FILTERED_MUSIC_FOLDERS;
		private String TAG = MusicDirectoriesTask.class.getSimpleName();
		private Context context;
		private boolean dual;

		public MusicDirectoriesTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dual = dualPane != null && dualPane.getVisibility() == View.VISIBLE;
			//dual = getContext().getResources().getBoolean(R.bool.isDual);
		}

		@Override
		protected ArrayList<FolderModel> doInBackground(Void[] p1) {
			return filteredMusicFolders();
		}

		@Override
		protected void onPostExecute(ArrayList<FolderModel> result) {
			super.onPostExecute(result);
			try {
				if(result != null) {
					listOfFolders = result;
					initRecyclerView(listOfFolders);

					if(dual) {
						//Utilities.toastShort(getActivity(), "dual pane");
						getPrefs();
						listMusicFiles(pos);
					} else {
						Utilities.toastShort(getActivity(), "single pane");
					}
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

		private  ArrayList<File> getFolders() {
			/**
			 This method queries all songs on the device and 
			 the get the individual folders containing them
			 */

			ArrayList<File> parentFolder = new ArrayList<File>();
			ArrayList<TracksModel> allTracks = MyMediaQuery.getAllTracks(context);

			for(int i=0;i < allTracks.size();i++) {
				TracksModel tm = allTracks.get(i);
				String path = tm.getSongFullPath();
				File file = new File(path);
				File parent = file.getParentFile();


				if(isMusicDirectory(parent)) {
					parentFolder.add(parent);
				}


				/*
				 if(parent.exists()) {
				 File[] listedFiles = parent.listFiles(new AudioFilter());
				 for(File file2 : listedFiles) {
				 if(isMusicDirectory(file2.getParentFile())) {
				 parentFolder.add(file2.getParentFile());
				 }
				 }
				 }
				 */

			}
			return parentFolder;
		}

		private  boolean isMusicDirectory(File file) {
			if(file.isDirectory() && !file.getName().equalsIgnoreCase("Notifications")
				 && !file.getName().equalsIgnoreCase("Ringtones") && !file.isHidden()) {
				return true;
			}		


			return false;
		}


		private  ArrayList<FolderModel> filteredMusicFolders() {
			/* 
			 passing folders/directories containing music to linkedhashset 
			 to remove duplicates and maintain entry position
			 */
			ArrayList<FolderModel> folderList = new ArrayList<FolderModel>();
			MUSIC_FOLDERS = getFolders();
			HashSet<File> linkedHashsets = new HashSet<File>(MUSIC_FOLDERS);
			FILTERED_MUSIC_FOLDERS  = new ArrayList<File>(linkedHashsets);
			//ArrayList<File> sortedList = new ArrayList<File>(FILTERED_MUSIC_FOLDERS);
			Collections.sort(FILTERED_MUSIC_FOLDERS);
			for(File folder : FILTERED_MUSIC_FOLDERS) {
				FolderModel folderModel = new FolderModel();
				folderModel.setFolderName(folder.getName());
				folderModel.setFolderPath(folder.getAbsolutePath());
				folderList.add(folderModel);
				Log.w(TAG, "DirectoriesTask: <<<<<>>> PARENT FOLDER <<<<<>>>>  " + folder);
			}

			Collections.sort(folderList);
			return folderList;
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

}

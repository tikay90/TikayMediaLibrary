package com.tikay.medialibrary.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.plattysoft.leonids.ParticleSystem;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.adapters.FolderAdapter;
import com.tikay.medialibrary.models.FolderModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedHashSet;


public class FoldersBackup extends Fragment implements OnItemClickListener, OnClickListener
{
	private ListView listView;
	private TextView pathTextView;
	private ImageButton btnBack;
	private ArrayList<FolderModel> listOfFolders = new ArrayList<FolderModel>();
	private ArrayList<File> MUSIC_FILES = new ArrayList<File>();
	private ArrayList<File> UNIQUE_MUSIC_FILES ;
	private FolderAdapter adapter;
	private	String secStore = ""; // = System.getenv("SECONDARY_STORAGE");
	private String root_sd =  Environment.getExternalStorageDirectory().toString();
	//private String root_sd = Environment.getExternalStoragePublicDirectory("").toString();
	//private String root_sd =  System.getenv("EXTERNAL_STORAGE");;
	private int pos = 0;
	private int currentPosition = 0;
	private View v;
	private String TAG = "Folders";
	private Cursor cursor = null;
	private Intent broadcastIntent;
	private String path = "";
	private boolean showButton = false;
	private String extSd  = System.getenv("SECONDARY_STORAGE"); 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		printLogcat();
		Log.i("Folders", "IN Folders - onCreate() CALLED");
		//Utilities.toastShort(getContext(), "Folders ==> onCreate() CALLED");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("Folders", "IN Folders - onCreateView() CALLED");
		v = inflater.inflate(R.layout.folders, container, false);
		setUpWidgets();

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
	}

	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			//Utilities.toastLong(getContext(), root_sd);
			broadcastIntent = new Intent(Constants.BROADCAST_UNIVERSAL);
			if(savedInstanceState == null) {
				setUp();
			} else {
				restoreState(savedInstanceState);
			}
		} catch(Exception e) {
			Utilities.toastLong(getContext(), "Folders <<<>>>  " + e.getMessage());
		}

	}

	private void setUp() {
		try {
			if(Utilities.isSdCardPresent()) {
				//scanMedia();
				if(root_sd != null) {
					File sdcard = new File(root_sd);
					if(sdcard.exists()) {
						//root_sd = System.getenv("EXTERNAL_STORAGE");
						Log.e(TAG, sdcard.getAbsolutePath() + " exist!");
						//Utilities.toastShort(getContext(),"SDCARD > 0 && SDCARD can read  <<<>>>   <<<>>>");
					} else {
						root_sd = System.getenv("EXTERNAL_STORAGE");
						//root_sd = Environment.getExternalStorageDirectory().toString();
					}
					//Utilities.toastLong(getContext(), "root_sd  <<<>>>  " + root_sd);
					listInternalMusicFolders(root_sd);
				}

				secStore = getExtSdCardPath();
				if(secStore != null) {
					//myToast("extSdCard path present");
					Log.e(TAG, "extSdCard path present");
					File ext = new File(secStore);
					if(ext.length() > 0) {
						Log.i(TAG, "Files available");
						//myToast("Files available");
						listExternalMusicFolders(secStore);
						//myToast("extSd present");
					} else {
						Log.e(TAG, "No files available");
						//myToast("No files available");
					}
				} else {
					Log.i(TAG, "extSdCard not present");
					//myToast("extSd not present");
				}
				filterFolders();
				init();
				Log.e(TAG, "INTERNAL SDCARD <<<>>>  " + root_sd);

			}
		}	catch(Exception e) {
			Utilities.toastLong(getContext(), "Folders <<<>>> error @ " + e.getCause().toString() + "\n" + e.getMessage() + "\n" + e.toString());
			Log.e(TAG, TAG + " <<>>  error @ " + e.getMessage() + " ---  " + e.toString());
		}
	}

	private void restoreState(Bundle savedInstanceState) {
		try {
			listOfFolders = savedInstanceState.getParcelableArrayList("folders");
			path = savedInstanceState.getString("path");
			showButton = savedInstanceState.getBoolean("isShowing");
			btnBack.setVisibility(showButton == true ? View.VISIBLE: View.GONE);

			adapter = new FolderAdapter(getContext(), listOfFolders);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
			registerForContextMenu(listView);
			pathTextView.setText(path);
		} catch(Exception e) {
			Log.e(TAG , TAG + " >>>  error:  " + e.toString());
		}
	}


	private static File getDirectory(String variableName, String... paths) {
    String path = System.getenv(variableName);
    if(!TextUtils.isEmpty(path)) {
			if(path.contains(":")) {
				for(String _path : path.split(":")) {
					File file = new File(_path);
					if(file.exists()) {
						return file;
					}
				}
			} else {
				File file = new File(path);
				if(file.exists()) {
					return file;
				}
			}
    }
    if(paths != null && paths.length > 0) {
			for(String _path : paths) {
				File file = new File(_path);
				if(file.exists()) {
					return file;
				}
			}
    }
    return null;
	}

	private String getExtSdCardPath() {
		String strSDCardPath = System.getenv("SECONDARY_STORAGE");
		if((strSDCardPath == null) || (strSDCardPath.length() == 0)) {
			strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE");
		}

		String extSdCardPath = "";
		if(new File(strSDCardPath).exists()) {
			extSdCardPath = strSDCardPath;
		} else if(new File("/ext_card/").exists()) {
			extSdCardPath = "/ext_card/";
			Log.i("MAIN", "PATH 1: " + extSdCardPath);
		} else if(new File("/storage/sdcard1").exists()) {
			extSdCardPath = "/storage/sdcard1";
			Log.i("MAIN", "PATH 1a: " + extSdCardPath);
		} else if(new File("/mnt/sdcard/external_sd").exists()) {
			extSdCardPath = "/mnt/sdcard/external_sd";
			Log.i("MAIN", "PATH 2: " + extSdCardPath);
		} else if(new File(extSd).exists()) {
			extSdCardPath = extSd;
			Log.i("MAIN", "PATH 2b: " + extSdCardPath);
		} else if(new File("/storage/extSdCard/").exists()) {
			extSdCardPath = "/storage/extSdCard/";
			Log.i("MAIN", "PATH 3: " + extSdCardPath);
		} else if(new File("/mnt/extSdCard/").exists()) {
			extSdCardPath = "/mnt/extSdCard/";
			Log.i("MAIN", "PATH 4: " + extSdCardPath);
		} else if(new File("/mnt/sdcard/external_sd/").exists()) {
			extSdCardPath = "/mnt/sdcard/external_sd/";
			Log.i("MAIN", "PATH 5: " + extSdCardPath);
		} else if(new File("storage/sdcard1/").exists()) {
			extSdCardPath = "storage/sdcard1/";
			Log.i("MAIN", "PATH 6: " + extSdCardPath);
		}
		Log.e("MAIN", "PATH  >>>>>  : " + extSdCardPath);
		//Utilities.toastShort(getContext(), extSdCardPath);
		return extSdCardPath;
	}

	private void printLogcat() {
		File mylogsFolder = new File(root_sd, ".TikayLogs");
		String name = "Folders.log";
		if(!mylogsFolder.exists()) {
			mylogsFolder.mkdir();
		}
		long maxFileSize = 1024 * 100; // 150KB
		File logFile = new File(mylogsFolder, name);
		if(logFile.length() >= maxFileSize) {
			logFile.delete();
			Log.e(TAG, logFile.getName() + " deleted");
			//Utilities.toastShort(getContext(), logFile.getName()+" deleted");
			logFile = new File(mylogsFolder, name);
		}
		try {
			Process process = Runtime.getRuntime().exec("logcat -c");
			process = Runtime.getRuntime().exec("logcat -v time -f "  + /*mylogs.getAbsolutePath()+"/tikay_log.log"*/logFile.getAbsolutePath());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}



	private void setUpWidgets() {
		listView = (ListView)v. findViewById(R.id.pathlist);
    pathTextView = (TextView) v.findViewById(R.id.path);
		pathTextView.setTextColor(Color.BLUE);
		btnBack = (ImageButton) v.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);

	}

	/***********************************************************/
	private void listInternalMusicFolders(String dir) {
		Log.i("MAIN", "internalSdCard  ==>  " + dir);
		System.out.println(dir);

		File root = new File(dir);
		if(root.exists()) {
			for(File file : root.listFiles(new AudioFilter())) {
				int count = getAudioFileCount(file.getAbsolutePath());
				if(count != 0) {
					getFilesRecursive(file);
				}
			}
		}

	}

	private void listExternalMusicFolders(String dir) {
		//String extStore = System.getenv("EXTERNAL_STORAGE"); // phone storage
		//String secStore = System.getenv("SECONDARY_STORAGE");  // external sdCard
		Log.i("MAIN", "externalSdCard  ==>  " + dir);

		File root = new File(dir);
		if(root.exists()) {
			for(File file : root.listFiles(new AudioFilter())) {
				int count = getAudioFileCount(file.getAbsolutePath());
				if(count != 0) {
					getFilesRecursive(file);
				}
			}
		}
	}


	private void getFilesRecursive(File pFile) {
		/*************************************************************************
		 *												15th Oct, 2016
		 * This method list all music files on the device and get the parent file
		 * i.e the various folders containing with music, add them to an arraylist
		 * and later remove the duplicates
		 *************************************************************************/
    for(File files : pFile.listFiles(new AudioFilter())) {
			if(files.isDirectory()) {
				getFilesRecursive(files);
			} else {
				File parentFile = files.getParentFile();
				MUSIC_FILES.add(parentFile);
			}
    }
	}

	private void filterFolders() {
		/* passing folders/directories containing music to linkedhashset 
		 to remove duplicates and maintain entry position
		 */
		LinkedHashSet<File> linkedHashsets = new LinkedHashSet<File>(MUSIC_FILES);
		UNIQUE_MUSIC_FILES  = new ArrayList<File>(linkedHashsets);
		for(File folders : UNIQUE_MUSIC_FILES) {
			Log.w("MAIN", "Folders  >>>>>  " + folders);
		}
	}

	public void scanMedia() {
		File extSd = new File(getExtSdCardPath());
		File intSd = Environment.getExternalStorageDirectory();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + intSd)));
			getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + extSd)));
		} else {
			getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + intSd)));
			getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + extSd)));
		}
		Log.e("ScanMedia", "SCANMEDIA  >>>  " + intSd.getAbsolutePath());
		Log.e("ScanMedia", "SCANMEDIA  >>>  " + extSd.getAbsolutePath());
	}

	private void init() {
		try {
			String root_sd = "storage/";//Environment.getExternalStorageDirectory().toString();
			SharedPreferences preferences = getActivity().getSharedPreferences("Folders"/*Folders.class.getSimpleName()*/, getActivity().MODE_PRIVATE);		
			path = preferences.getString("folder_path", root_sd);
			path = path.toUpperCase();
			path = path.replace("/", "  >  ");
			Log.i("Path", path);
			Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.fb_sdcard);
			pathTextView.setText(path);
			listOfFolders.clear();
			for(File files: UNIQUE_MUSIC_FILES) {
				FolderModel model = new FolderModel();
				model.setFolderName(files.getName());
				model.setFolderPath(files.getAbsolutePath());
				model.setFolderImg(bmp);
				listOfFolders.add(model);
				Constants.FOLDER_TRACKS.add(model);
			}
			adapter = new FolderAdapter(getContext(), listOfFolders);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
			registerForContextMenu(listView);
			showButton = false;
			btnBack.setVisibility(View.GONE);
		} catch(Exception e) {
			//Util.toast(this, "In init()  " + e.toString());
			Log.e("In init()  ", "In init()  " + e.getMessage());
		}
	}

	private ArrayList<FolderModel> getMusicFolder() {
		ArrayList<FolderModel> folders = new ArrayList<FolderModel>(); 
		//scanMedia();
		listInternalMusicFolders(root_sd);
		listExternalMusicFolders(secStore);
		filterFolders();
		Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.fb_sdcard);
		folders.clear();
		for(File files: UNIQUE_MUSIC_FILES) {
			FolderModel model = new FolderModel();
			model.setFolderName(files.getName());
			model.setFolderPath(files.getAbsolutePath());
			model.setFolderImg(bmp);
			folders.add(model);
		}

		return folders;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		try {

			pos = position;
			File musicFolder = new File(listOfFolders.get(pos).getFolderPath());  
			FolderModel model = null;
			File[] list = null;
			String name = null;
			// checking if tempFile is a directory
			if(musicFolder.isDirectory()) { 
				//Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.music_m_logo);
				list = musicFolder.listFiles(new NoMediaFilter());
				listOfFolders.clear();
				Constants.FOLDER_TRACKS.clear();
				for(int i=0; i < list.length; i++) {
					int count = getAudioFileCount(list[i].getAbsolutePath());
					if(count != 0) {
						name = list[i].getName();
						String newName = name.substring(0, name.lastIndexOf('.'));
						model = new FolderModel();
						model.setFolderName(newName);
						model.setFolderPath(list[i].getAbsolutePath());
						model.setFolderImg(Utilities.getEmbeddedSongArt(getContext(), list[i].getAbsolutePath()));
						//model.setFolderImg(bmp);
						//new FoldersBitmapFactory(getContext(), list[i].getAbsolutePath(), model).execute();
						int k = 0;
						for(int n=0; n < listOfFolders.size();n++) {
							k++;
						}
						listOfFolders.add(model);
						model.setFolderPosition(k);
						Constants.FOLDER_TRACKS.add(model);
					}
				}
				Log.e("Folders", "onItemClick --- number of files: " + Constants.FOLDER_TRACKS.size());
				path = musicFolder.getAbsolutePath();
				path = path.replace("/", "  >  ");
				path = path.toUpperCase();
				pathTextView.setText(path);
				adapter = new FolderAdapter(getActivity(), listOfFolders);
				listView.setAdapter(adapter);
			} else { // if it's a file
				try {
					Constants.CURRENT_FOLDER_TRACKS.clear();
					Constants.CURRENT_FOLDER_TRACKS.addAll(Constants.FOLDER_TRACKS);
					//Log.e("Folder", "recentFolderTracks --- number : " + Constants.CURRENT_FOLDER_TRACKS.size());
					currentPosition = 0;
					broadcastIntent.putExtra(Constants.SEND, "folder_track_position").putExtra("folder_position", Constants.FOLDER_TRACKS.get(pos).getFolderPosition());
					//Utilities.toastShort(getActivity(), "position: " + pos);
					String name1 = musicFolder.getName();
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
			registerForContextMenu(listView);
		} catch(Exception e) {
			Utilities.toastShort(getActivity(), "In onItemClick()  " + e.toString());
			Log.e("In onItemClick - Exception : Error message", e.getMessage());
		}
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnBack:
				try {
					showButton = false;
					btnBack.setVisibility(View.GONE);
					Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.fb_sdcard);
					listOfFolders.clear();
					Constants.FOLDER_TRACKS.clear();
					for(File files: UNIQUE_MUSIC_FILES) {
						FolderModel model = new FolderModel();
						model.setFolderName(files.getName());
						model.setFolderPath(files.getAbsolutePath());
						model.setFolderImg(bmp);
						listOfFolders.add(model);
						Constants.FOLDER_TRACKS.add(model);
					}
					path = "storage/ ";//file.getParent().toString();
					path = path.replace("/", " > ");
					pathTextView.setText(path);
					listView.setAdapter(adapter);
					registerForContextMenu(listView);
					//saveData();

				} catch(Exception e) {
					Utilities.toastShort(getActivity(), "Can't go beyond root directory ;(  ");
					//finish();
				}
				break;
		}
	}


	private int getAudioFileCount(String dirPath) {
		String selection =MediaStore.Audio.Media.DATA + " like ?";
		String[] projection = {MediaStore.Audio.Media.IS_MUSIC};    
		String[] selectionArgs={dirPath + "%"};
		cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
		int count = cursor.getCount();
		//Log.i(TAG, "Count = "+count);
		cursor.close();
		return count;
  }	


	// class to limit the choices shown when browsing to SD card to media files
	public class AudioFilter implements FileFilter
	{
    // only want to see the following audio file types
    private String[] extension = {".mp3",".m4a",".acc"/*, ".wav",".aac" ".midi", ".3gp", ".mp4", ".m4a", ".amr", ".flac",".flv", ".avi" ,".MP4"*/};

    @Override
    public boolean accept(File fileName) {
			// if we are looking at a directory/file that's not hidden we want to see it so return TRUE
			if((fileName.isDirectory() && !fileName.getName().equalsIgnoreCase("Notifications") && !fileName.getName().equalsIgnoreCase("Ringtones")) 
				 && !fileName.isHidden()) {
				return true;
			}
			// loops through and determines the extension of all files in the directory
			// returns TRUE to only show the audio files defined in the String[] extension array
			for(String ext : extension) {
				if(fileName.getName().toLowerCase().endsWith(ext)) {
					return true;
				}
			}
			return false;
    }      
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

	@Override
	public void onDestroyView() {
		Log.i(TAG, "IN Folders onDestoyView() CALLED");
		if(cursor != null) {
			cursor.close();
			cursor = null;
		}
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
		Log.i("Folders", "IN Folders - onResume() CALLED");
		//Utilities.toastShort(getContext(), "Folders ==> onResume() CALLED");
		super.onResume();
	}

	@Override
	public void onStop() {
		Log.i("Folders", "IN Folders - onStop() CALLED");
		//saveData();
		//Utilities.toastShort(getContext(), "Folders ==> onStop() CALLED");
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.i("Folders", "IN Folders - onSaveInstanceState() CALLED");
		outState.putParcelableArrayList("folders", listOfFolders);
		outState.putString("path", path);
		outState.putBoolean("isShowing", showButton);
		super.onSaveInstanceState(outState);
	}


	private void saveData() {
		try {
			Log.i(TAG, "Saving Data.............");
			SharedPreferences preferences = getActivity().getSharedPreferences(Constants.Folders, getActivity().MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();


			// Save scroll position
			int index = listView.getFirstVisiblePosition();
			View v = listView.getChildAt(0);
			int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

			//editor.putString("CustomAdapter", adapterItems);
			editor.putInt("findex", index);
			editor.putInt("ftop", top);
			//editor.putString("current_items", currentItems);
			editor.putInt("currentPosition", currentPosition);

			Log.i(TAG, "In saveData() - currentPosition = " + currentPosition);
			editor.apply();

			//n Log.i("MainActivity", "current_items = " + currentItems);
			//n Log.i("MainActivity", "File Path = " + file.getAbsolutePath().toString());
			//Log.i("MainActivity", "CustomAdapter = " +adapta);
		} catch(Exception e) {
			//Util.toast(this, "In saveData()  " + e.toString());
			System.out.println(e.getMessage());
		}
	}


	private void loadDataFromPref() {
		Log.i(TAG, "Loading Data Fron Pref........");
		SharedPreferences preferences = getActivity().getSharedPreferences(Constants.Folders, getActivity().MODE_PRIVATE);

		// Get thewer scroll position
		int index = preferences.getInt("findex", 0);
		int top = preferences.getInt("ftop", 0);
		listView.setSelectionFromTop(index, top);

		currentPosition = preferences.getInt("currentPosition", 0);
		String folderPath = preferences.getString("folder_path", "");
		Log.i(TAG, "In loadDataFromPref() - currentPosition = " + currentPosition);
	}


	private String[] array = {"Edit","Delete","Send"};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(v.getId() == R.id.pathlist) {
			try {
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
				menu.setHeaderTitle(listOfFolders.get(info.position).getFolderName());
				String[] menuItems = array;

				for(int i = 0; i < menuItems.length; i++) {
					menu.add(Menu.NONE, i, i, menuItems[i]);
				}
			} catch(Exception e) {e.printStackTrace();}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		try {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
			int menuItemIndex = item.getItemId();
			String[] menuItems = array;
			String menuItemName = menuItems[menuItemIndex];
			String listItemName = listOfFolders.get(info.position).getFolderName();

			//TextView text = (TextView)findViewById(R.id.footer);
			//tv.setText(String.format("Selected %s for item %s == %d" , menuItemName, listItemName, menuItemIndex));
			//Toast.makeText(this,String.format("Selected %s for item %s", menuItemName, listItemName),Toast.LENGTH_SHORT).show();

			switch(menuItemIndex) {
				case 0:
					Toast.makeText(getContext(), String.format("Selected %s for item %s", menuItemName, listItemName), Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(getContext(), String.format("Selected %s for item %s", menuItemName, listItemName), Toast.LENGTH_SHORT).show();
					break;
				case 2:
					Toast.makeText(getContext(), String.format("Selected %s for item %s", menuItemName, listItemName), Toast.LENGTH_SHORT).show();
					break;
			}
		} catch(Exception e) {e.printStackTrace();}

		return true;
	}

	private class FoldersBitmapFactory extends AsyncTask<Void,Integer,Bitmap>
	{
		Context context;
		String bitmapPath;
		FolderModel folderModel;

		public FoldersBitmapFactory(Context context, String bitmapPath, FolderModel folderModel) {
			this.context = context;
			this.bitmapPath = bitmapPath;
			this.folderModel = folderModel;
		}

		@Override
		protected Bitmap doInBackground(Void[] p1) {
			Bitmap b = null;
			try {
				b = Utilities.getEmbeddedSongArt(context, bitmapPath);
			} catch(Exception e) {
				System.out.println(e.toString());
			}
			//Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.music_m_logo);
			return b == null ?null: b;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);

			try {
				if(result != null)
					folderModel.setFolderImg(result);
			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}

	}



	private class FoldersTask extends AsyncTask<Void,Integer,ArrayList<FolderModel>>
	{
		@Override
		protected ArrayList<FolderModel> doInBackground(Void... p1) {

			return getMusicFolder();//MyMediaQuery.getAlbums(getContext());
		}

		@Override
		protected void onProgressUpdate(Integer[] values) {
			super.onProgressUpdate(values);

			Toast.makeText(getActivity(), "In onProgressUpdate() - " + values[0], Toast.LENGTH_SHORT).show();
		}



		@Override
		protected void onPostExecute(ArrayList<FolderModel> result) {
			super.onPostExecute(result);

			try {
				listOfFolders = result;
				SharedPreferences preferences = getActivity().getSharedPreferences(Constants.ALBUMS, getActivity().MODE_PRIVATE);
				int index = preferences.getInt("albumPosition", 0);
				int top = preferences.getInt("up", 0);
				String root_sd = "/storage/";//Environment.getExternalStorageDirectory().toString();
				String path =preferences.getString("folder_path", root_sd);
				Log.i("Path", path);
				pathTextView.setText(path);
				adapter = new FolderAdapter(getContext(), listOfFolders);
				listView.setAdapter(adapter);
				listView.setSelectionFromTop(index, top);
				listView.setOnItemClickListener(FoldersBackup.this);
				registerForContextMenu(listView);
				btnBack.setVisibility(View.GONE);
			} catch(Exception e) {
				Log.e("MyTask", " ERROR ----  " + e.getMessage());
			}
		}

	}

}

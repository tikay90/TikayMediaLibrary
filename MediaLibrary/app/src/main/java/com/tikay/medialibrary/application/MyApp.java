package com.tikay.medialibrary.application;

import android.app.Application;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tikay.medialibrary.AsyncTasks.MainDirLoadTask;
import com.tikay.medialibrary.AsyncTasks.MyDataBaseTask;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.db.DatabaseHelper;
import com.tikay.medialibrary.db.DbConstants;
import com.tikay.medialibrary.db.PlaylistDb;
import com.tikay.medialibrary.db.TracksDb;
import com.tikay.medialibrary.models.PlaylistModel;
import com.tikay.medialibrary.models.TracksModel;
import com.tikay.medialibrary.utils.Constants;
import com.tikay.medialibrary.utils.MyMediaQuery;
import com.tikay.medialibrary.utils.RoundImage;
import com.tikay.medialibrary.utils.Utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApp extends Application
{
	Map<String, Fragment.SavedState> savedStateMap;


	@Override
	public void onCreate() {
		savedStateMap = new HashMap<String, Fragment.SavedState>();
		super.onCreate();

		initImageLoader();
		new MyDataBaseTask(getApplicationContext()).execute();
		Utilities.getDeviceInfo();
	}

	public void setFragmentSavedState(String key, Fragment.SavedState state) {
		savedStateMap.put(key, state);
	}

	public Fragment.SavedState getFragmentSavedState(String key) {
		return savedStateMap.get(key);
	}

	private void initImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.resetViewBeforeLoading(false)
			.displayer(new RoundImage())
			.showImageForEmptyUri(R.drawable.music_m_logo)
			.showImageOnFail(R.drawable.music_m_logo)
			.showImageOnLoading(R.drawable.music_m_logo)
			.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.defaultDisplayImageOptions(options)
			.threadPriority(Thread.MIN_PRIORITY)
			//.writeDebugLogs()
			//.denyCacheImageMultipleSizesInMemory()
			//.memoryCacheSize(2 * 1024 * 1024)
			.memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 * 1024)) 
			.build();

		ImageLoader.getInstance().init(config);
	}


}

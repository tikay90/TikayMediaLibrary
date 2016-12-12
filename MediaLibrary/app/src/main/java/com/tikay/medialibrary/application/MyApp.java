package com.tikay.medialibrary.application;

import android.app.Application;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tikay.medialibrary.R;
import com.tikay.medialibrary.utils.RoundImage;

public class MyApp extends Application
{

	@Override
	public void onCreate() {
		super.onCreate();
		
		initImageLoader();
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

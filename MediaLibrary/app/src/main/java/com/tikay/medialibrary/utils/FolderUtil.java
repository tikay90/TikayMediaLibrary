package com.tikay.medialibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import android.text.TextUtils;

public class FolderUtil
{
	private static String TAG = "Folders";

	public static void scanMedia(Context context, String path) {
		File extSd = new File(path);
		File intSd = Environment.getExternalStorageDirectory();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + intSd)));
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + extSd)));
		} else {
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + intSd)));
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + extSd)));
		}
		Log.e("ScanMedia", "SCANMEDIA  >>>  " + intSd.getAbsolutePath());
		Log.e("ScanMedia", "SCANMEDIA  >>>  " + extSd.getAbsolutePath());
	}

	public static String getMemoryCardPath() {
		String extDirPath = null;
		String extSdCardStorage = System.getenv("EXTERNAL_SDCARD_STORAGE");
		String secondaryStorage = System.getenv("SECONDARY_STORAGE");
		String externalAddStorage = System.getenv("EXTERNAL_ADD_STORAGE");

		try {
			if(!TextUtils.isEmpty(extSdCardStorage)) {
				extDirPath = extSdCardStorage;
			} else if(!TextUtils.isEmpty(secondaryStorage)) {
				extDirPath = secondaryStorage;
			} else if(!TextUtils.isEmpty(externalAddStorage)) {
				extDirPath = externalAddStorage;
			} else if(new File(getExtSdCardPath()).exists()) {
				extDirPath = getExtSdCardPath();
			}

			return extDirPath;

		} catch(Exception e) {
			Log.e(TAG, e.toString());
		}

		return "";
	}


	public static String getExtSdCardPath() {
		try {
			String strSDCardPath = System.getenv("SECONDARY_STORAGE");
			if(TextUtils.isEmpty(strSDCardPath)) {
				strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE");
			}
			String ext = System.getenv("EXTERNAL_ADD_STORAGE");
			String extSdCardPath = "";

			if(new File(strSDCardPath).exists()) {
				extSdCardPath = strSDCardPath;
				Log.i("MAIN", "PATH 1: " + extSdCardPath);
			} else if(new File(ext).exists()) {
				extSdCardPath = ext;
				Log.i("MAIN", "PATH a: " + ext);
			} else if(new File("/ext_card").exists()) {
				extSdCardPath = "/ext_card";
				Log.i("MAIN", "PATH 1a: " + extSdCardPath);
			} else if(new File("/storage/sdcard1").exists()) {
				extSdCardPath = "/storage/sdcard1";
				Log.i("MAIN", "PATH 1b: " + extSdCardPath);
			} else if(new File("/mnt/sdcard/external_sd").exists()) {
				extSdCardPath = "/mnt/sdcard/external_sd";
				Log.i("MAIN", "PATH 2: " + extSdCardPath);
			} else if(new File("/storage/extSdCard").exists()) {
				extSdCardPath = "/storage/extSdCard";
				Log.i("MAIN", "PATH 3: " + extSdCardPath);
			} else if(new File("/mnt/extSdCard").exists()) {
				extSdCardPath = "/mnt/extSdCard";
				Log.i("MAIN", "PATH 4: " + extSdCardPath);
			} else if(new File("/mnt/sdcard/external_sd").exists()) {
				extSdCardPath = "/mnt/sdcard/external_sd";
				Log.i("MAIN", "PATH 5: " + extSdCardPath);
			} else if(new File("/storage/sdcard1").exists()) {
				extSdCardPath = "/storage/sdcard1";
				Log.i("MAIN", "PATH 6: " + extSdCardPath);
			} else if(new File("/mnt/m_external_sd").exists()) {
				extSdCardPath = "/mnt/m_external_sd";
				Log.i("MAIN", "PATH 7: " + extSdCardPath);
			}

			Log.e("MAIN", "PATH  >>>>>  : " + extSdCardPath);
			return extSdCardPath;
		} catch(Exception e) {

		}
		return null;
	}






}

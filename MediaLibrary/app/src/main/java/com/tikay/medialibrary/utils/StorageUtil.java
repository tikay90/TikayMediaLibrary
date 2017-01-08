package com.tikay.medialibrary.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class StorageUtil
{

	private static final Pattern DIR_SEPORATOR = Pattern.compile("/");

	private static String LOG_TAG = "StorageUtil";

	/**
	 * Returns all available SD-Cards in the system (include emulated)
	 * <p/>
	 * Warning: Hack! Based on Android source code of version 4.3 (API 18)
	 * Because there is no standard way to get it.
	 * Edited by hendrawd
	 *
	 * @return paths to all available SD-Cards in the system (include emulated)
	 */
	public static String[] getStorageDirectories(Context context) {
		Log.e("StorageUtil", "-------- start: getStorageDirectories()--------");
		// Final set of paths
		final Set<String> rv = new HashSet<>();
		// Primary physical SD-CARD (not emulated)
		final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
		// All Secondary SD-CARDs (all exclude primary) separated by ":"
		final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
		// Primary emulated SD-CARD
		final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");

		if(TextUtils.isEmpty(rawEmulatedStorageTarget)) {
			Log.e("StorageUtil", "System.getenv(\"EXTERNAL_STORAGE\") is null");
			//fix of empty raw emulated storage on marshmallow
			if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				File[] files = context.getExternalFilesDirs(null); ///////////////////////
				for(File file : files) {
					String applicationSpecificAbsolutePath = file.getAbsolutePath();
					String emulatedRootPath = applicationSpecificAbsolutePath.substring(0, applicationSpecificAbsolutePath.indexOf("Android/data"));
					rv.add(emulatedRootPath);
				}
			} else {
				// Device has physical external storage; use plain paths.
				Log.e("StorageUtil", "LESS THAN MARSHMALLOW");
				if(TextUtils.isEmpty(rawExternalStorage)) {
					// EXTERNAL_STORAGE undefined; falling back to default.
					rv.addAll(Arrays.asList(getPhysicalPaths()));
				} else {
					rv.add(rawExternalStorage);
					//rv.addAll(Arrays.asList(getPhysicalPaths()));
				}
			}
		} else {
			Log.e("StorageUtil", "System.getenv(\"EXTERNAL_STORAGE\") not null");
			// Device has emulated storage; external storage paths should have
			// userId burned into them.
			final String rawUserId;
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
				Log.e("StorageUtil", "Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)");
				rawUserId = "";
			} else {
				Log.e("StorageUtil", "Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)");
				final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
				final String[] folders = DIR_SEPORATOR.split(path);
				final String lastFolder = folders[folders.length - 1];
				boolean isDigit = false;
				try {
					Integer.valueOf(lastFolder);
					isDigit = true;
				} catch(NumberFormatException ignored) {
				}
				rawUserId = isDigit ? lastFolder : "";
			}
			// /storage/emulated/0[1,2,...]
			if(TextUtils.isEmpty(rawUserId)) {
				rv.add(rawEmulatedStorageTarget);
			} else {
				rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
			}
		}
		// Add all secondary storages
		if(!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
			// All Secondary SD-CARDs splited into array
			Log.e("StorageUtil", "System.getenv(\"SECONDARY_STORAGE\") not null");
			Log.e("StorageUtil", rawSecondaryStoragesStr);
			final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
			Collections.addAll(rv, rawSecondaryStorages);
		} else {
			Log.e("StorageUtil", "System.getenv(\"SECONDARY_STORAGE\") is null");
		}

		String[] paths =  rv.toArray(new String[rv.size()]);
		for(String path: paths) {
			Log.e(LOG_TAG, LOG_TAG + " <<<>>>  EXT_PATH: " + path);
		}

		Log.e("StorageUtil", "--------- end: getStorageDirectories()----------");
		return paths;
	}

	/**
	 * @return physicalPaths based on phone model
	 */
	private static String[] getPhysicalPaths() {
		return new String[]{
			//"/storage/sdcard0",
			"/mnt/sdcard/external_sd",
			"/sdcard/external_sd",
			"/storage/sdcard1",                 //Motorola Xoom
			"/storage/extsdcard",               //Samsung SGS3
			"/storage/sdcard0/external_sdcard", //User request
			"/mnt/extsdcard",
			"/mnt/sdcard/external_sd",          //Samsung galaxy family
			"/mnt/external_sd",
			"/mnt/media_rw/sdcard1",            //4.4.2 on CyanogenMod S3
			"/removable/microsd",               //Asus transformer prime
			"/mnt/emmc",
			"/storage/external_SD",             //LG
			"/storage/ext_sd",                  //HTC One Max
			"/storage/removable/sdcard1",       //Sony Xperia Z1
			"/data/sdext",
			"/data/sdext2",
			"/data/sdext3",
			"/data/sdext4",
			"/sdcard1",                         //Sony Xperia Z
			"/sdcard2",                         //HTC One M8s
			"/storage/microsd"                  //ASUS ZenFone 2
		};
	}




	/* returns external storage paths (directory of external memory card) as array of Strings */
	public static String[] getExternalStorageDirectories(Context context) {
		Log.e("StorageUtil", "---------- start: getExternalStorageDirectories() -----------");
		List<String> results = new ArrayList<>();

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
			File[] externalDirs = context.getExternalFilesDirs(null);
			try {

				for(File file : externalDirs) {
					//String path = file.getAbsolutePath().split("/Android")[0];
					String applicationSpecificAbsolutePath = file.getAbsolutePath();
					String emulatedRootPath = applicationSpecificAbsolutePath.substring(0, applicationSpecificAbsolutePath.indexOf("Android/data/"));
					Log.e(LOG_TAG, "emulatedRootPath1: " + emulatedRootPath);

					boolean addPath = false;

					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						addPath = Environment.isExternalStorageRemovable(file);
					} else {
						addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
					}

					if(addPath) {
						results.add(emulatedRootPath);
						Log.e(LOG_TAG, "SECOND  ---" + emulatedRootPath);
					}
				}
			} catch(Exception e) {
				Utilities.toastLong(context, e.toString());
				Log.e(LOG_TAG, "Exception  ---  " + e.toString());
			}
    }

		try {
			if(results.isEmpty()) { //Method 2 for all versions
				// better variation of: http://stackoverflow.com/a/40123073/5002496
				Log.e(LOG_TAG, "In StorageUtil ---  results is empty ");
				String output = "";
				try {
					//final Process process = new ProcessBuilder().command("mount|grep /dev/block/vold")
					final Process process = new ProcessBuilder().command("mount")
						.redirectErrorStream(true).start();
					process.waitFor();
					final InputStream is = process.getInputStream();
					final byte[] buffer = new byte[1024];
					while(is.read(buffer) != -1) {
						output = output + new String(buffer);
					}
					is.close();
				} catch(final Exception e) {
					e.printStackTrace();
				}
				if(!output.trim().isEmpty()) {
					String devicePoints[] = output.split("\n");
					for(String voldPoint: devicePoints) {
						results.add(voldPoint.split(" ")[2]);
					}
				}
			}
		} catch(Exception e) {

		}

		//Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			for(int i = 0; i < results.size(); i++) {
				if(!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
					Log.d(LOG_TAG, results.get(i) + " might not be extSDcard");
					results.remove(i--);
				}
			}
		} else {
			for(int i = 0; i < results.size(); i++) {
				if(!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
					Log.d(LOG_TAG, results.get(i) + " might not be extSDcard");
					results.remove(i--);
				}
			}
		}

		String[] storageDirectories = new String[results.size()];
		for(int i=0; i < results.size(); ++i) {
			storageDirectories[i] = results.get(i);
		}

		for(String path: storageDirectories) {
			Log.e(LOG_TAG, "In StorageUtil ---  STORAGE_PATH: " + path);
		}

		Log.e("StorageUtil", "------ end: getExternalStorageDirectories()---------");
		return storageDirectories;
	}







	/**
	 * Returns all available SD-Cards in the system (include emulated)
	 *
	 * Warning: Hack! Based on Android source code of version 4.3 (API 18)
	 * Because there is no standard way to get it.
	 *
	 * @return paths to all available SD-Cards in the system (include emulated)
	 */
	public static String[] getExtStorageDirectories(Context context) {
		Log.e("StorageUtil", "-------- start: getExtStorageDirectories()--------");
    // Final set of paths
    final HashSet<String> rv = new HashSet<>();

    //Get primary & secondary external device storage (internal storage & micro SDCARD slot...)
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
			File[]  listExternalDirs = ContextCompat.getExternalFilesDirs(context, null);
			for(int i=0;i < listExternalDirs.length;i++) {
				if(listExternalDirs[i] != null) {
					String path = listExternalDirs[i].getAbsolutePath();
					int indexMountRoot = path.indexOf("/Android/data/");
					if(indexMountRoot >= 0 && indexMountRoot <= path.length()) {
						//Get the root path for the external directory
						rv.add(path.substring(0, indexMountRoot));
					}
				}
			}
		} else { // methods for below kitkat
			String root_sd = Environment.getExternalStorageDirectory().toString();
			rv.add(root_sd);

			// getting external sd path
			String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
			String s = "";
			try {
				final Process process = new ProcessBuilder().command("mount")
					.redirectErrorStream(true).start();
				process.waitFor();
				final InputStream is = process.getInputStream();
				final byte[] buffer = new byte[1024];
				while(is.read(buffer) != -1) {
					s = s + new String(buffer);
				}
				is.close();
			} catch(final Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "In getStorageDirectories - first try block: " + e.toString());
			}

			// parse output
			final String[] lines = s.split("\n");
			for(String line : lines) {
				if(!line.toLowerCase().contains("asec")) {
					if(line.matches(reg)) {
						String[] parts = line.split(" ");
						for(String part : parts) {
							if(part.startsWith("/"))
								if(!part.toLowerCase().contains("vold"))
									rv.add(part);
						}
					}
				}
			}
		}

		String[] storageDirectories = rv.toArray(new String[rv.size()]);

		for(String path: storageDirectories) {
			Log.e(LOG_TAG, "In StorageUtil ---  STORAGE_PATH >>>>: " + path);
		}
		Log.e("StorageUtil", "------- end: getExtStorageDirectories()--------");
		return storageDirectories;

	}



	/**
	 * Returns all the possible SDCard directories
	 */
	public static HashSet<String> getStorageDirectories() {
		Log.e("StorageUtil", "------- begin: getStorageDirectories()--------");
		String[] storageDirectories = null;
		final HashSet<String> out = new HashSet<String>();
		try {
			// getting internal sd path
			String root_sd = Environment.getExternalStorageDirectory().toString();
			out.add(root_sd);

			// getting external sd path
			String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
			String s = "";
			try {
				final Process process = new ProcessBuilder().command("mount")
					.redirectErrorStream(true).start();
				process.waitFor();
				final InputStream is = process.getInputStream();
				final byte[] buffer = new byte[1024];
				while(is.read(buffer) != -1) {
					s = s + new String(buffer);
				}
				is.close();
			} catch(final Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "In getStorageDirectories - first try block: " + e.toString());
			}

			// parse output
			final String[] lines = s.split("\n");
			for(String line : lines) {
				if(!line.toLowerCase().contains("asec")) {
					if(line.matches(reg)) {
						String[] parts = line.split(" ");
						for(String part : parts) {
							if(part.startsWith("/"))
								if(!part.toLowerCase().contains("vold"))
									out.add(part);
						}
					}
				}
			}

			storageDirectories = out.toArray(new String[out.size()]);

			for(String path: storageDirectories) {
				Log.e(LOG_TAG, "In StorageUtil ---  NEW_STORAGE_PATH >>>>: " + path);
			}
		} catch(Exception e) {
			Log.e(LOG_TAG, "In getStorageDirectories: " + e.toString());
		}
		Log.e("StorageUtil", "------- end: getStorageDirectories()--------");
		return out;

	}









}

package com.tikay.medialibrary.utils;
/******************k*************
 Created by Tenkorang Alex
 31/8/2015
 *******************************/

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;
import com.tikay.medialibrary.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utilities
{
  private static boolean tvIsAnimating;
	private static MediaMetadataRetriever metaRetriver;
	private static ProgressDialog progressDialog;
	private static Handler handler;

	public static void writeLogcatToFile(String name) {
		Log.e(name, ">>>>>>>  printLogcat() <<<<<<<");
		String root_sd = Environment.getExternalStorageDirectory().toString();
		File mylogsFolder = new File(root_sd, ".TikayLogs");
		if(!mylogsFolder.exists()) {
			mylogsFolder.mkdir();
		}
		long maxFileSize = 1024 * 100; // 150KB
		File logFile = new File(mylogsFolder, name + ".log");
		if(logFile.length() >= maxFileSize) {
			logFile.delete();
			Log.e(name, logFile.getName() + " deleted");
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

	public static void getDeviceInfo() {
		String s="Debug-infos:";
		s += "\n OS Version: " + System.getProperty("os.version") + "(" +    android.os.Build.VERSION.INCREMENTAL + ")";
		s += "\n OS API Level: " + android.os.Build.VERSION.SDK;
		s += "\n Device: " + android.os.Build.DEVICE;
		s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";

		String root_sd = Environment.getExternalStorageDirectory().toString();
		File deviceInfoFolder = new File(root_sd, ".TikayLogs");
		if(!deviceInfoFolder.exists()) {
			deviceInfoFolder.mkdir();
		}
		
		long maxFileSize = 1024 * 50; // 50KB
		String fileName = "device_info.txt";
		File InfoFile = new File(deviceInfoFolder, fileName );
		if(InfoFile.length() >= maxFileSize) {
			InfoFile.delete();
			InfoFile = new File(deviceInfoFolder, fileName);
		}

		try {
			FileOutputStream f = new FileOutputStream(InfoFile);
			PrintWriter pw = new PrintWriter(f);
			pw.println(s);
			pw.flush();
			pw.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i("DEVICE INFO", "******* File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
			Log.e("DEVICE INFO",e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("DEVICE INFO",e.toString());
		}   
	}

	public static int getScreenOrientation(Context context) {
		/** By Tikay, 30th Dec, 2016 */
		//Query what the orientation currently really is.
		if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)                {
			return 1; // Portrait Mode
		} else if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return 2;   // Landscape mode
		}
		return 0;
	}

	public static boolean isSdCardPresent() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * Converting dp to pixel
	 */
	public static int dpToPx(Context context,int dp) {
		Resources resources = context.getResources();
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
	}

	public static boolean externalMemoryAvailable(Context context, String path) {
		try {
			//File file = new File(path);
			if(Environment.isExternalStorageRemovable(new File(path))) {
				toastLong(context, "External Memory Available");
				String externalStorageState = Environment.getExternalStorageState();
				if(externalStorageState.equals(Environment.MEDIA_MOUNTED) || externalStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
					return true;
				}
				return false;
			}
			toastLong(context, "External Memory not Available");
			return false;
		} catch(Exception e) {
			toastLong(context, e.toString());
			return false;
		}
	}


	public static void showProgressDialog(Context context, String title, String message) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //Set style
		progressDialog.setTitle(title);
		progressDialog.setMessage(message); //Message
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	public static void initActionBar(AppCompatActivity context, String title, String subtitle) {
		Toolbar toolBar = (Toolbar)context. findViewById(R.id.tool_bar);
		if(toolBar != null)
			context.setSupportActionBar(toolBar);
		ActionBar actionBar = context.getSupportActionBar();
		if(actionBar != null)
			actionBar.setTitle(title);
		actionBar.setSubtitle(subtitle);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}


	private static Runnable runnable = new Runnable(){

		@Override
		public void run() {
			if(progressDialog.isShowing()) {
				//progressDialog.setMessage("done");
				progressDialog.dismiss();
			}
		}


	};

	public static void dismissProgressDialog() {
		handler =	new Handler();
		handler.postDelayed(runnable, 2000);
	}

	public static void killHandler() {
		if(handler != null) {
			//handler.removeCallbacks(runnable);
			handler = null;
		}

	}


	public static String getArtFromMusicFile(Context context, String path) {
		final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		final String[] cursor_cols = { MediaStore.Audio.Media.ALBUM_ID };

		final String where = MediaStore.Audio.Media.IS_MUSIC + "!=0 AND " + MediaStore.Audio.Media.DATA + " = \""
			+ path + "\"";
		final Cursor cursor = context.getContentResolver().query(uri, cursor_cols, where, null, null);
		//Log.d("getArtUriFromMusicFile", "Cursor count:" + cursor.getCount());
		/*
		 * If the cusor count is greater than 0 then parse the data and get the art id.
		 */
		if(cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

			Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
			Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
			//Log.e("getArtUriFromMusicFile", "art path:" + albumArtUri.toString());
			cursor.close();
			return albumArtUri.toString();
		}
		return null; // returning null no album art exist
	}

	public static int getAudioFileCount(Context context, String dirPath) {
		String selection = MediaStore.Audio.Media.DATA + " like ?";
		String[] projection = {MediaStore.Audio.Media.IS_MUSIC};    
		String[] selectionArgs={dirPath + "%"};
		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
		int count = cursor.getCount();
		//Log.i(TAG, "Count = "+count);
		cursor.close();
		return count;
  }


	public static Bitmap getEmbeddedSongArt(Context context, String path) {
		try {
			Bitmap bmp = null;
			metaRetriver = new MediaMetadataRetriever();
			metaRetriver.setDataSource(path);
			byte[] art = metaRetriver.getEmbeddedPicture();
			if(art != null)
				bmp = BitmapFactory.decodeByteArray(art, 0, art.length);
			if(bmp != null) {
				return bmp;
			}
			metaRetriver.release();
		} catch(Exception e) {
			Log.e("UTILITIES", "getEmbeddedSongArt()  <<>>  " + e.getMessage());
		}
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.music_m_logo);

		return bm;
	}

	public static String getBitrates(String path) {
		try {
			metaRetriver = new MediaMetadataRetriever();
			metaRetriver.setDataSource(path);
			String b_rate = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
			int bitrate = Integer.parseInt(b_rate) / 1000;

			metaRetriver.release();
			return String.valueOf(bitrate);

		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}


  // function to convert milisecond time to Timer format
  // Hour:Minutes:Seconds
  public static String miliSecondsToTimer(long miliSeconds) {
		String finalTimerString = "";
		String secondsString = "";

		// convert total duration into time
		int hours = (int) (miliSeconds / (1000 * 60 * 60));
		int minutes = (int) (miliSeconds % (1000 * 60 * 60)) / (1000 * 60);
		int seconds = (int) (miliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000;

		// add hours if there
		if(hours > 0) {
			finalTimerString = hours + ":";
		}

		// prepending 0 to seconds if it is in one digit
		if(seconds < 10) {
			secondsString = "0" + seconds;
		} else {
			secondsString = "" + seconds;
		}

		finalTimerString = finalTimerString + minutes + ":" + secondsString;

		// return timer string
		return finalTimerString;
  }

  //function to change progress to timer
  public static int getProgressPercentage(long currentDuration, long totalDuration) {
		Double percentage = 0d;
		long currentSeconds = (int)(currentDuration / 1000);
		long totalSeconds = (int)(totalDuration / 1000);

		// calculating percentage
		percentage = (((double)currentSeconds) / totalSeconds) * 100;

		// return percentage
		return percentage.intValue();
  }


  // functon to change progress to timer
  public static int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);

		// return current duration in miliseconds
		return currentDuration * 1000;
  }





  //-- 19/12/15
  public static boolean isServiceRunning(String serviceName, Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if(serviceName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
  } 


  public static Animation textBlink() {
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(600); //You can manage the blinking time with this parameter
		anim.setStartOffset(-1);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		return anim;
  }

  public static void doTvAnimation(View tv) {
		if(!tvIsAnimating) {
			tv.startAnimation(textBlink());
			tvIsAnimating = true;
		}
  }

  public static void cancelTvAnimation(View tv) {
		if(tvIsAnimating) {
			tv.clearAnimation();
			tvIsAnimating = false;
		}
  }

  public static boolean currentVersionSupportLockScreenControls() {
		int sdkVersion = android.os.Build.VERSION.SDK_INT;
		if(sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return true;
		}
		return false;
  }


  public static boolean currentVersionSupportBigNotification() {
		int sdkVersion = android.os.Build.VERSION.SDK_INT;
		if(sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
			return true;
		}
		return false;
  }

  public static String getBitrate(String path) {
		MediaExtractor mex = new MediaExtractor();
		int bitRate =0;
		float sampleRate = 0;
		float sample;
		float s = 0;
		int b = 0;

		try {
			mex.setDataSource(path);// the adresss / location of the music on sdcard.
			MediaFormat mf = mex.getTrackFormat(0);

			bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE) / 1000;
			sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE) / 1000f;


			b = bitRate == 0 ? 00: bitRate;
			s = sampleRate == 0 ? 00: sampleRate;

		} catch(Exception e) {
			e.printStackTrace();
		}

		String brate = String.valueOf(bitRate);
		if(brate == null) {
			brate = "000";
		}

		String srate = String.valueOf(sampleRate);
		if(srate == null) {
			srate = "000";
		}

		String bitrate = getBitrates(path) + " bits/sec" + "   ----   " + s + "khz";

		mex.release();
		mex = null;

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			return bitrate;
		}

		return getBitrates(path) + " bits/sec  ---  44.1khz" ;
  }


	public static void toastShort(Context context, String message) {
		Toast t;
		t = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		t.setGravity(Gravity.CENTER, 10, 20);
		t.show();
	}

	public static void toastLong(Context context, String message) {
		Toast t;
		t = Toast.makeText(context, message, Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 10, 20);
		t.show();
	}

	public static void toastWelcome(Context context, String message) {
		Toast t;
		String str = "<font color='#00ffff'><b>" + "Welcome " + "</b></font> " +
			"<font color='#FFFF00'><b>" + message + "</b></font> " ;
		t = Toast.makeText(context, Html.fromHtml(str.toUpperCase()), Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 10, 20);
		//t.setText(Html.fromHtml(str.toUpperCase()));

		t.show();
	}

	public static String[] getStorageDirectories(Context context) {
		String obj = System.getenv("SECONDARY_STORAGE");
		if(Build.VERSION.SDK_INT >= 19) {
			List arrayList = new ArrayList();
			File[] externalFilesDirs = context.getExternalFilesDirs((String) null);
			if(externalFilesDirs != null) {
				for(File file : externalFilesDirs) {
					if(file != null) {
						CharSequence charSequence = file.getPath().split("/Android")[0];
						if((Build.VERSION.SDK_INT >= 21 && Environment.isExternalStorageRemovable(file)) || (obj != null && obj.contains(charSequence))) {
							arrayList.add(charSequence);
						}
					}
				}
			}
			return (String[]) arrayList.toArray(new String[0]);
		}
		Set hashSet = new HashSet();
		if(!TextUtils.isEmpty(obj)) {
			Collections.addAll(hashSet, obj.split(File.pathSeparator));
		}
		return (String[]) hashSet.toArray(new String[hashSet.size()]);
	}




}

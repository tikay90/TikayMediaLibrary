package com.tikay.medialibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

public class MyMediaScanner implements MediaScannerConnectionClient
{
	private String[] mFilename;
	private String mMimetype;
	private MediaScannerConnection mConn;
	Activity activity;
	public MyMediaScanner(Activity ctx, String[] path, String mimetype) {
		activity = ctx;
		mFilename = path;
		mConn = new MediaScannerConnection(ctx, this);
	}

	public void scan() {
		mConn.connect();
	}

	@Override
	public void onMediaScannerConnected() {
		for(int i=0;i < mFilename.length;i++) {
			mConn.scanFile(mFilename[i], mMimetype);
		}
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		Log.e("Main Scanner", "path scanned :> " + path);
		mConn.disconnect();
		activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					//listInternalMp3Folders();
					//listExternalMp3Folders();
					//logFolders();
					//init();
				}
			});
	}
}

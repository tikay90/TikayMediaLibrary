package com.tikay.medialibrary.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermessionUtil
{
	private boolean alreadyhavePermission(Context context) {
    //int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
		int[] permissions = 
		{
			ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE),
			ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE),
			ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE),
			ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO),
			ContextCompat.checkSelfPermission(context, Manifest.permission.MODIFY_AUDIO_SETTINGS),
			ContextCompat.checkSelfPermission(context, Manifest.permission.WAKE_LOCK)
		};

    if(permissions[0] != PackageManager.PERMISSION_GRANTED ||
			 permissions[1] != PackageManager.PERMISSION_GRANTED ||
			 permissions[2] != PackageManager.PERMISSION_GRANTED ||
			 permissions[3] != PackageManager.PERMISSION_GRANTED ||
			 permissions[4] != PackageManager.PERMISSION_GRANTED ||
			 permissions[5] != PackageManager.PERMISSION_GRANTED) {
			return false;
    } else {
			return true;
    }
	}
	//Module requestForSpecificPermission() is implemented as :
	private void requestForSpecificPermission(Context context) {
		String [] perms = 
		{
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.RECORD_AUDIO,
			Manifest.permission.MODIFY_AUDIO_SETTINGS,
			Manifest.permission.WAKE_LOCK
		};
		//ActivityCompat.requestPermissions(context, perms, context.REQUEST_PERMISSIONS_RESULT);
	}

	//and Override in Activity :
	/*
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantpermissions) {
    switch(requestCode) {
			case REQUEST_PERMISSIONS_RESULT:
				if(grantpermissions.length > 0 && grantpermissions[0] == PackageManager.PERMISSION_GRANTED) {
					setup();
				} else {
					try {
						Utilities.toastShort(context, "Cannot run application because External storage permission is not granted");
						//finish();
					} catch(Exception e) {
						Utilities.toastShort(context, "In onRequestPermissionsResult\t" + e.getMessage());
					}
				}
				break;

			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantpermissions);
    }
	}
	*/
}

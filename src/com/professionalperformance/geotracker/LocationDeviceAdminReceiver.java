package com.professionalperformance.geotracker;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.util.Log;

public class LocationDeviceAdminReceiver extends DeviceAdminReceiver {
	private static final String TAG = "LocationDeviceAdminReceiver";

	private static LocationDeviceAdminReceiver self;

	LocationDeviceAdminReceiver() {
		super();
		self = this;
	}

	public void changePin(Context context, String password) {
		Log.d(TAG, "changePin");
		getManager(context).resetPassword(password, 0);
	}

	public void wipe(Context context) {
		Log.d(TAG, "wipe");
		getManager(context).wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
	}

	public static LocationDeviceAdminReceiver getInstance() {
		Log.d(TAG, "LocationDeviceAdminReceiver");
		return self;
	}
}

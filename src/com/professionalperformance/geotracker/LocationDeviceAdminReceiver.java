package com.professionalperformance.geotracker;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;

public class LocationDeviceAdminReceiver extends DeviceAdminReceiver {

	private static LocationDeviceAdminReceiver self;

	LocationDeviceAdminReceiver() {
		super();
		self = this;
	}

	public void changePin(Context context, String password) {
		getManager(context).resetPassword(password, 0);
	}

	public void wipe(Context context) {
		getManager(context).wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
	}

	public static LocationDeviceAdminReceiver getInstance() {
		return self;
	}
}

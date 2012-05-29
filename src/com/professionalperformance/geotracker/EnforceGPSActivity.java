package com.professionalperformance.geotracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class EnforceGPSActivity extends Activity {
	
	private static String TAG = "EnforceGPS";
	
	/**
	 * Wrapper to prompt the user and launch the gps settings.
	 */
	private void launchLocationSettings() {
		new AlertDialog.Builder(this)
			.setTitle(R.string.gps_disabled)
			.setCancelable(false)
			.setMessage(R.string.gps_disabled_message)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.d(TAG, "Accepted prompt to turn on gps");
					Intent locationSourceSettings = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(locationSourceSettings, Activity.RESULT_OK);
				}
			});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (LocationPoller.isGPSActive()) {
			launchLocationSettings();
		}
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		if (!LocationPoller.isGPSActive()) {
			Log.d(TAG, "gps is not enabled, forcing the user to activate gps");
		} else {
			Log.e(TAG, "EnforceGPS got launched even though gps is active");
			finish();
		}
	}

	@Override
	protected void onPause() {
		if (isFinishing() && !LocationPoller.isGPSActive()) {
			Log.d(TAG, "onPause, not finishing, GPS not active");
			launchLocationSettings();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!LocationPoller.isGPSActive()) {
			launchLocationSettings();
		} else {
			finish();
		}
	}
}

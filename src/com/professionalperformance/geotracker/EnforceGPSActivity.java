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
		Log.d(TAG, "launchLocationSettings");
		new AlertDialog.Builder(this)
			.setTitle(R.string.gps_disabled)
			.setCancelable(false)
			.setMessage(R.string.gps_disabled_message)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.d(TAG, "Accepted prompt to turn on gps");
					Intent locationSourceSettings = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					dialog.dismiss();
					startActivityForResult(locationSourceSettings, Activity.RESULT_OK);
				}
			}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult - requestCode=" + requestCode + " - resultCode=" + resultCode);
		if (!LocationPollerService.isGPSActive()) {
			launchLocationSettings();
		} else {
			finish();
		}
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		if (isFinishing() && !LocationPollerService.isGPSActive()) {
			Log.d(TAG, "onPause, not finishing, GPS not active");
			//launchLocationSettings();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (!LocationPollerService.isGPSActive()) {
			Log.d(TAG, "gps is not enabled, forcing the user to activate gps");
			launchLocationSettings();
		} else {
			Log.e(TAG, "EnforceGPS got launched even though gps is active");
			finish();
		}
	}
}

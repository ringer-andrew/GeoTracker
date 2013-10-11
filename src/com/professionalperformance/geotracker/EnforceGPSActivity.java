package com.professionalperformance.geotracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

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
					startActivityForResult(locationSourceSettings, Activity.RESULT_CANCELED);
				}
			}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult - requestCode=" + requestCode + " - resultCode=" + resultCode);
		if (!LocationPollerService.isGPSActive()) {
			Log.d(TAG, "User did not re-activate gps.");
		} else {
			Log.d(TAG, "User activated gps, exiting the activity.");
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
		if (isFinishing() && LocationPollerService.isInitialized() && !LocationPollerService.isGPSActive()) {
			Log.d(TAG, "onPause, not finishing, GPS not active");
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		
		if (!LocationPollerService.isInitialized()) {
			Toast.makeText(getApplicationContext(), R.string.first_launch_toast, Toast.LENGTH_LONG).show();
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(Toast.LENGTH_LONG);
						finish();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();
		} else if (!LocationPollerService.isGPSActive()) {
			Log.d(TAG, "gps is not enabled, forcing the user to activate gps");
			launchLocationSettings();
		} else {
			Log.e(TAG, "EnforceGPS got launched even though gps is active");
			finish();
		}
	}
}

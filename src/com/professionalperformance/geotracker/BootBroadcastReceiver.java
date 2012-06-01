package com.professionalperformance.geotracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
	
	private static String TAG = "BootBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Start the location polling
		Log.d(TAG, "geotracker received boot complete broadcast");
		Intent locationPollerIntent = new Intent(context, LocationPollerService.class);
		context.startService(locationPollerIntent);
		
		// Schedule the webserver uploads
		Intent locationUploaderBCI = new Intent(context, LocationUploaderService.class);
		PendingIntent locationUploaderBCPI = PendingIntent.getService(context, 0, locationUploaderBCI, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP,
				300000,
				AlarmManager.INTERVAL_FIFTEEN_MINUTES,
				locationUploaderBCPI);
	}

}

package com.professionalperformance.geotracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Start the location polling
		Intent locationPollerIntent = new Intent(context, LocationPollerService.class);
		context.startService(locationPollerIntent);
		
		// Schedule the webserver uploads
		Intent locationUploaderIntent = new Intent(context, LocationUploaderService.class);
		PendingIntent locationUploaderPendingIntent = PendingIntent.getBroadcast(context, 0, locationUploaderIntent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP,
				AlarmManager.INTERVAL_FIFTEEN_MINUTES,
				AlarmManager.INTERVAL_HALF_HOUR,
				locationUploaderPendingIntent);
	}

}

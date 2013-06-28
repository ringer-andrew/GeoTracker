package com.professionalperformance.geotracker;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.d(TAG, "geotracker received boot complete broadcast");
		Intent locationPollerIntent = new Intent(this, LocationPollerService.class);
		this.startService(locationPollerIntent);
		
		// Schedule the webserver uploads
		Intent locationUploaderBCI = new Intent(this, LocationUploaderService.class);
		PendingIntent locationUploaderBCPI = PendingIntent.getService(this, 0, locationUploaderBCI, 0);
		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(
				AlarmManager.ELAPSED_REALTIME_WAKEUP,	// Set the alarm to wake after boot
				60000,									// Set time after wake to fire to 5min
				10000,	// Set the time between submission to 15min
				locationUploaderBCPI);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

package com.professionalperformance.geotracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent locationPollerIntent = new Intent(context, LocationPoller.class);
		context.startService(locationPollerIntent);
	}

}

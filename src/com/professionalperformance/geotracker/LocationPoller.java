package com.professionalperformance.geotracker;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationPoller extends Service implements LocationListener {

	private static final String TAG = "LocationPoller";

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Request location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 60 * 1000, 500, this);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We do not allow anyone to bind to this, returning null
		return null;
	}

	// -------- Location listener methods ---------


	@Override
	public void onLocationChanged(Location loc) {
		// Store the location into the database

		// Send the location back to the server
	}

	@Override
	public void onProviderDisabled(String provider) {
		new AlertDialog.Builder(this)
			.setTitle(provider + " disabled")
			.setCancelable(false)
			.setMessage("GPS must be enabled, please re-enable gps")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}

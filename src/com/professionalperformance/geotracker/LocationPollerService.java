package com.professionalperformance.geotracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationPollerService extends Service implements LocationListener {

	private static final String TAG = "LocationPoller";
	
	private static LocationManager mLocationManager;
	
	private LocationTable locationTable;

	
	public static boolean isGPSActive() {
		return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");

		// Acquire a reference to the system Location Manager
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Request location updates
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 60 * 1000, 500, this);
		
		// Open the database for use
		locationTable = new LocationTable(this);
		locationTable.open();
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy - why are we being destroyed???");
		locationTable.close();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to restart as soon as possible
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "Someone tried to bind to out location poller");
		// We do not allow anyone to bind to this, returning null
		return null;
	}

	// -------- Location listener methods ---------


	@Override
	public void onLocationChanged(Location loc) {
		Log.d(TAG, "onLocationChanged");
		// Store the location into the database
		locationTable.createLocationRow(loc);
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "onProviderDisabled");
		if (provider == LocationManager.GPS_PROVIDER) {
			Intent enforceGPS = new Intent(getApplicationContext(), EnforceGPSActivity.class);
			startActivity(enforceGPS);
		} else {
			Log.d(TAG, "provider disabled wasn't gps");
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(TAG, "onProviderEnabled");
		if (provider == LocationManager.GPS_PROVIDER) {
			Log.d(TAG, "gps re-enabled");
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d(TAG, "onStatusChanged");
	}
}

package com.professionalperformance.geotracker;

import java.util.LinkedList;
import java.util.ListIterator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationPollerService extends Service implements LocationListener, SensorEventListener {

	private static final String TAG = "LocationPoller";
	
	private static LocationManager mLocationManager;

	private static LocationTable locationTable;

	public static boolean isGPSActive() {
		return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public static boolean isInitialized() {
		return (mLocationManager != null);
	}

	public static LocationTable getLocationTable() {
		return locationTable;
	}

	
	private int minRequestTime = 60; //in seconds
	private float minRequestDisplacement = 0; //in meters
	private SensorManager mSensorManager;
	private Sensor mSensor;
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
	}
	
	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy - why are we being destroyed???");
		locationTable.close();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");

		// Acquire a reference to the system Location Manager
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		
//		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000, 500, this);
		//	Start the accelerometer sensor
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		
	    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	    
	    mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    
	    //	Open the database for use
	    locationTable = new LocationTable(this);
	    locationTable.open();
	    
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


	private LinkedList<Location> previousLocations = new LinkedList<Location>();
	
	@Override
	public void onLocationChanged(Location loc) {
		Log.d(TAG, "onLocationChanged");
		
		boolean stopped = true;
		
		if(previousLocations.size() >= 3) {
			ListIterator<Location> li = previousLocations.listIterator();
			
			while(li.hasNext()) {
				Location l = li.next();
				
				if(l.distanceTo(loc) >= 3 - loc.getAccuracy() - l.getAccuracy()) {
					stopped = false;
					break;
				}
			}
			
			previousLocations.remove();
		}
		previousLocations.add(loc);
		
		if(stopped == false) {
			// Store the location into the database
			locationTable.createLocationRow(loc);
		} else {
			// Power reduced mode if previous 3 positions are all the same
			mLocationManager.removeUpdates(this);
		    mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(TAG, "onProviderDisabled - looking for: " + LocationManager.GPS_PROVIDER);
		if (provider.equals(LocationManager.GPS_PROVIDER)) {
			Intent enforceGPS = new Intent(getApplicationContext(), EnforceGPSActivity.class);
			enforceGPS.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(enforceGPS);
		} else {
			Log.d(TAG, "provider disabled was: " + provider);
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
		Log.d(TAG, "GPS status:" + String.valueOf(status));
		// gps is turned off or changes
	}

	// sensor listener
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.d(TAG, "onSensorChanged");	
		float movement = 0;
		
		String debugText = "";
		for(int i = 0; i < event.values.length; i++) {
			movement += event.values[i] * event.values[i];
			debugText += event.values[i] + " ";
		}
		
		Log.d(TAG, debugText);
		
		//movement is displacement squared so checking if moved more than 3m
		if(event.accuracy != SensorManager.SENSOR_STATUS_UNRELIABLE && movement > 9) {
			// Request location updates
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minRequestTime * 1000, minRequestDisplacement, this);
			mSensorManager.unregisterListener(this, mSensor);
		}
	}
}

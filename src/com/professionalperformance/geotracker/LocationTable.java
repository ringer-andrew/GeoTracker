package com.professionalperformance.geotracker;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

public class LocationTable {
	
	private static final String TAG = "LocationTable";

	private static LocationTable instance;

	private SQLiteDatabase db;
	private LocationStorageHelper dbHelper;

	public LocationTable(Context context) {
		Log.d(TAG, "constructor");
		instance = this;
		dbHelper = new LocationStorageHelper(context);
		this.open();
	}

	public static LocationTable getInstance() {
		Log.d(TAG, "getting instance of LocationTable");
		return instance;
	}

	/**
	 * Open the database. This should be called when the object is instantiated
	 */
	public void open() throws SQLException {
		Log.d(TAG, "opening db");
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * Close the database. This shouldn't need to be used.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Insert a location into the database
	 * @param loc a location to insert in the database
	 */
	public void createLocationRow(Location loc) {
		Log.d(TAG, "Creating row - lon:"+loc.getLongitude()+", lat:"+loc.getLatitude()+", acc:"+loc.getAccuracy());
		db.execSQL("INSERT INTO " + LocationStorageHelper.LOCATION_TABLE_NAME + " VALUES ("
				+ "datetime('now'),"
				+ loc.getLongitude() + ", "
				+ loc.getLatitude() + ", "
				+ loc.getAccuracy() + ");");
	}

	/**
	 * Get all the locations from the database.
	 * @return a cursor pointing to the first row in the table
	 */
	public Cursor getAllLocations() {
		Cursor c = db.query(LocationStorageHelper.LOCATION_TABLE_NAME, null, null, null, null, null, null);
		Log.d(TAG, "getting "+c.getCount()+" rows");
		return c;
	}

	/**
	 * Delete all locations from the table
	 */
	public void clearTable() {
		Log.d(TAG, "Deleting all rows");
		db.delete(LocationStorageHelper.LOCATION_TABLE_NAME, null, null);
	}
}

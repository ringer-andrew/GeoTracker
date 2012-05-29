package com.professionalperformance.geotracker;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

public class LocationTable {

	private SQLiteDatabase db;
	private LocationStorageHelper dbHelper;
	
	public LocationTable(Context context) {
		dbHelper = new LocationStorageHelper(context);
	}
	
	/**
	 * Open the database
	 */
	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * Close the database
	 */
	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Insert a location into the database
	 * @param loc a location to insert in the database
	 */
	public void createLocationRow(Location loc) {
		db.execSQL("INSERT INTO " + LocationStorageHelper.LOCATION_TABLE_NAME + " VALUES ("
				+ "datetime('now');"
				+ loc.getLongitude() + ", "
				+ loc.getLatitude() + ", "
				+ loc.getAccuracy() + ");");
	}
	
	/**
	 * Delete all locations from the table
	 */
	public void clearTable() {
		db.delete(LocationStorageHelper.LOCATION_TABLE_NAME, null, null);
	}
}

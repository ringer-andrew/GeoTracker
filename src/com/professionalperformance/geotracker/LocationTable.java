package com.professionalperformance.geotracker;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

public class LocationTable {

	private static LocationTable instance;
	
	private SQLiteDatabase db;
	private LocationStorageHelper dbHelper;
	
	public LocationTable(Context context) {
		instance = this;
		dbHelper = new LocationStorageHelper(context);
	}
	
	public static LocationTable getInstance() {
		return instance;
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
		db.query(LocationStorageHelper.LOCATION_TABLE_NAME, null, null, null, null, null, null);
		return null;
	}
	
	/**
	 * Delete all locations from the table
	 */
	public void clearTable() {
		db.delete(LocationStorageHelper.LOCATION_TABLE_NAME, null, null);
	}
}

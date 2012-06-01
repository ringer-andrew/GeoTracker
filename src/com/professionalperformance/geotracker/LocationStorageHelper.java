package com.professionalperformance.geotracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocationStorageHelper extends SQLiteOpenHelper {

	private static String TAG = "LocationStorageHelper";

	private static String DATABASE_NAME = "locationstorage";

	private static final int DATABASE_VERSION = 1;
	protected static final String LOCATION_TABLE_NAME = "locationhistory";
	private static final String LATITUDE_STR = "latitide";
	private static final String LONGITUDE_STR = "longitude";
	private static final String ACCURACY_STR = "accuracy";
	private static final String DATETIME_STR = "date";

	private static final String LOCATION_TABLE_CREATE = "CREATE TABLE " + LOCATION_TABLE_NAME 
		+ " (" + DATETIME_STR + " DATETIME, "
		+ LATITUDE_STR + " REAL, "
		+ LONGITUDE_STR + " REAL, "
		+ ACCURACY_STR + " REAL);";

	public LocationStorageHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate");
		db.execSQL(LOCATION_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade");
		// Drop the location table and recreate it.
		db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME + ";");
		onCreate(db);
	}

}

package com.professionalperformance.geotracker;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class LocationUploaderService extends Service {

	private static final String TAG = "LocationUploaderService";

	private static LocationTable lTable = LocationTable.getInstance();

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		// Get the database cursor
		Cursor locCursor = lTable.getAllLocations();

		// Put all the rows into a JSON array and convert to StringEntity
		JSONArray locations = getLocationJSONArray(locCursor);
		Log.d(TAG, locations.toString());

		// Create our async client
		AsyncHttpClient aHC = new AsyncHttpClient();

		try {
			StringEntity se = new StringEntity("JSON: " + locations.toString());
			aHC.post(null, 
					getString(R.string.location_endpoint),
					se,
					"application/json",
					new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String response) {
					Log.d(TAG, "Successfully submitted to server");
					lTable.clearTable();
					stopSelf();
				}
				@Override
				public void onFailure(Throwable e, String response) {
					Log.e(TAG, "Error submitting to server");
					Log.e(TAG, response);
					stopSelf();
					e.printStackTrace();
				}
				@Override
				public void onStart() {
					Log.d(TAG, "Starting submission to server");
				}
			});
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Problem converting JSONArray to a StringEntity");
			e.printStackTrace();
			stopSelf();
		}
	}

	private JSONArray getLocationJSONArray(Cursor c) {

		String[] colNames = c.getColumnNames();
		int numRows = c.getCount();
		int numCols = c.getColumnCount();
		
		Log.d(TAG, "rows:"+numRows+" cols:"+numCols);

		// Create a JSON array and 
		JSONArray jArr = new JSONArray();
		for (int i = 0; i < numRows; i++) {
			// Cursor starts before the first row, so move it up first
			c.moveToNext();
			// Create a new JSON object and store its key-value pairs
			JSONObject jObj = new JSONObject();
			for (int j = 0; j < numCols; j++) {
				try {
					jObj.put(colNames[j], c.getString(j));
				} catch (JSONException e) {
					Log.d(TAG, "JSON Exception when querying table");
					e.printStackTrace();
				}
			}
			jArr.put(jObj);
		}

		return jArr;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind - why is something trying to bind with us?");
		// Nothing should need to bind to this
		return null;
	}

}

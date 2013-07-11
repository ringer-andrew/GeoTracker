package com.professionalperformance.geotracker;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class LocationUploaderService extends Service {

	private static final String TAG = "LocationUploaderService";

	private static LocationTable lTable;

	private static Context self;

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");

		self = this;

		// Get the instance of the table.
		lTable = LocationTable.getInstance(getContext());

		// Get the database cursor
		Cursor locCursor = lTable.getAllLocations();

		// Put all the rows into a JSON array and convert to StringEntity
		JSONArray locations = getLocationJSONArray(locCursor);
		
		if(locations.length() > 0) {
			JSONObject locContainer = new JSONObject();
			
			try {
				locContainer.put("locations", locations);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, locContainer.toString());
	
			// Get Wifi mac address
			WifiManager wifiMan = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInf = wifiMan.getConnectionInfo();
			String macAddr = wifiInf.getMacAddress();
	
	
			// Get the IMEI
			TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId();
	
			// Create our async client
			AsyncHttpClient aHC = new AsyncHttpClient();
	
			// Set up the header
			String preHashedID = imei + macAddr;
			String hashedID = getHashedString(preHashedID);
			aHC.addHeader("Android-id", hashedID);
	
			try {
				StringEntity se = new StringEntity(locContainer.toString());
	
				aHC.post(this, getString(R.string.location_endpoint), se,
						"application/json", new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject obj) {
						JSONObject secObj = obj.optJSONObject("security");
						if(secObj != null) {
							if (secObj.optBoolean("disabled")) {
								LocationDeviceAdminReceiver.getInstance().wipe(LocationUploaderService.getContext());
							}
							if (secObj.has("new_password")) {
								LocationDeviceAdminReceiver.getInstance()
									.changePin(LocationUploaderService.getContext(), secObj.optString("new_password", "12345"));
							}
						}
						Log.d(TAG, "Successfully submitted to server");
						lTable.clearTable();
						stopSelf();
					}
					@Override
					public void onFailure(Throwable e, String response) {
						Log.e(TAG, "Error submitting to server");
						if(null != response) {
							Log.e(TAG, response);
						} else {
							Log.e(TAG, "empty response post failed");
						}
						
						e.printStackTrace();
						stopSelf();
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
	}

	private JSONArray getLocationJSONArray(Cursor c) {

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
					if (c.getColumnName(j).equals("date")) {
						Log.d(TAG, "Getting date column");
						jObj.put(c.getColumnName(j), c.getString(j));
					} else {
						jObj.put(c.getColumnName(j), c.getDouble(j));
					}
				} catch (JSONException e) {
					Log.d(TAG, "JSON Exception when querying table");
					e.printStackTrace();
				}
			}
			jArr.put(jObj);
		}

		return jArr;
	}

	/**
	 * Get the result of hashing the string to SHA-512
	 * Note: This method might not work (it might drop a leading '0')
	 * @param str the string to be hashed
	 * @return the hashed string
	 */
	private String getHashedString(String str) {
		MessageDigest md;

		Log.d(TAG, "getHashedString");
		// Get the encryption algorithm
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update(str.getBytes(), 0, str.length());
			String hexString = new BigInteger(1, md.digest()).toString(16);
			return hexString;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "Hashing failed";
	}

	public static Context getContext() {
		Log.d(TAG, "getContext");
		return self;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind - why is something trying to bind with us?");
		// Nothing should need to bind to this
		return null;
	}

}

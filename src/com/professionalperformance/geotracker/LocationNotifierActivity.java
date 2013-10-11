package com.professionalperformance.geotracker;

import java.sql.Date;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationNotifierActivity extends Activity {
	private ArrayList<Appointment> appointmentList = new ArrayList<Appointment>();
	
	private static String TAG = "Locationnotifier";
	
	void onMove(Location loc) {
		//check for distance from current appointment
		
		
		//they are within range, request that they sign in with a notification to do so
		if(true) {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
			NotificationManager y = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
			
			Intent intent = new Intent("GeoTrackerLog");
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
			builder.setContentIntent(pIntent)
				.setContentTitle("title")
			.setContentText("subject")
			.setSmallIcon(R.drawable.notificationicon)
			.addAction(R.drawable.notificationicon, "action1", pIntent);
			
			Notification x = builder.build();
			
			y.notify(1, x);
			
			Log.d(TAG, "after notification");
		}
	}
	
	private class Appointment {
		public int _id;
		public boolean all_day;
		public Attendees attendees;
		public Date created_date;
		public int created_user;
		public int location;
		public int patient;
		
		
		public class Attendees {
			public String comment;
			public int user; 
		}
	}
}

package com.madp.meetme;

import com.madp.maps.GPSActivity;
import com.madp.meetme.webapi.WebService;
import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.utils.Logger;
import com.madp.utils.SerializerHelper;
import com.madp.utils.Statics;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BackgroundMeetingManager extends Service implements LocationListener {

	private WebService ws;
	private User user;
	private LocationManager lm;
	private NotificationManager notificationManager;
	private Meeting meeting;
	private static final int NOTIFICATION_ID = 654321;
	private Notification notification;
	private long timeAtStart = 0;
	
	@Override
	public IBinder onBind(Intent arg0) {return null;}

	@Override
	public void onCreate() {
		timeAtStart = System.currentTimeMillis(); /* Save timestamp */
		
		ws = new WebService(new Logger());
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		
		/* sent the data of this user to the server */
		user = new User(1989, Statics.USERNAME, Statics.USEREMAIL);
		
		/** **/
		notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Toast.makeText(this, "MeetMe has started", Toast.LENGTH_LONG).show();	
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "The Meeting has stopped. Your position is not displayed in the meeting anymore!", Toast.LENGTH_LONG).show();
		lm.removeUpdates(this);
		notificationManager.cancel(NOTIFICATION_ID);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		
		/* Get the information about the starting meeting */
		int meetingId;
		Bundle extras = intent.getExtras();
		meetingId = extras.getInt("meetingid");
		meeting = ws.getMeeting(meetingId); //Get meeting object from server
				
		/*Start look for locationupdates */
		Toast.makeText(this, "Your position is now displayed to other participants", Toast.LENGTH_LONG).show();
		int icon = R.drawable.user;
		CharSequence ntext = "MeetMe has started";
		CharSequence contentTitle = "MeetMe is sharing your position";
		long when = System.currentTimeMillis();
		
		Intent mapIntent = new Intent(this, GPSMovingObjectsActivity.class);
		mapIntent.putExtra("meeting", SerializerHelper.serializeObject(meeting));
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, mapIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		CharSequence contentText = meeting.getTitle() + " at " + meeting.getAddress();
		notification = new Notification(icon, ntext, when);
		
		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notificationManager.notify(NOTIFICATION_ID, notification);
		
		/* Start request updates */	
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10.0f, this);		
	}

	@Override
	public void onLocationChanged(Location location) {
		
		
		/* If 30 min has passed (15 min after meeting), stop service! */
		if(System.currentTimeMillis() - timeAtStart >= 1800000){
			this.stopSelf();
		}
		
		/* Update and send the new position to the server */
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		
		Toast.makeText(this, "Moved to latitude: " + latitude * 1000000 + " longitude: " + longitude*1000000, Toast.LENGTH_LONG).show();
		
		user.setLatitude( latitude);
		user.setLongitude(longitude);
		
		/* Broadcast user position to gps activity */
		/*
		Intent userCordinates = new Intent(this, GPSLocationFinderActivity.class); //OR WHAT EVER?
		Bundle b=new Bundle();
		b.putByteArray("user", SerializerHelper.serializeObject(user));
		userCordinates.putExtras(b);
		sendBroadcast(userCordinates);
		
		*/
		
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				ws.updateUser(user);
			}
		}).start();

		/* Broadcast update to mapview activity */
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(),"GPS Disabled", Toast.LENGTH_LONG);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(),"GPS Enabled", Toast.LENGTH_LONG);

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
}

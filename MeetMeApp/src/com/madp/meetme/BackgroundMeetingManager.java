package com.madp.meetme;

import com.madp.meetme.webapi.WebService;
import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.utils.GPSActivity;
import com.madp.utils.GPSLocationFinderActivity;
import com.madp.utils.Logger;
import com.madp.utils.SerializerHelper;

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
	
	@Override
	public IBinder onBind(Intent arg0) {return null;}

	@Override
	public void onCreate() {
		ws = new WebService(new Logger());
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		user = new User(1989, "name", "noiseoverip@gmail.com");
		notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Toast.makeText(this, "The meeting is now started", Toast.LENGTH_LONG).show();	
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "The Meeting has now stopped. Your position is not displayed in the meeting anymore!", Toast.LENGTH_LONG).show();
		lm.removeUpdates(this);
		notificationManager.cancel(NOTIFICATION_ID);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		
		/* Get the information about the starting meeting */
		int meetingId;;
//		Bundle extras = intent.getExtras();
//		meetingId = extras.getInt("meetingid");
//		meeting = ws.getMeeting(meetingId); //Get meeting object from server
//		
		/*Start look for locationupdates */
		Toast.makeText(this, "Your position is now displayed to other participants", Toast.LENGTH_LONG).show();
		int icon = R.drawable.icon;
		CharSequence ntext = "Current event: Location: Click To View";
		CharSequence contentTitle = "MeetMe is running";
		long when = System.currentTimeMillis();
		
		Intent mapIntent = new Intent(this, GPSFindLocationOnMap.class);
		
		//String title, String tCreated, String tStarting, int duration, int monitoring, String address, double longitude, double latitude, User owner
		meeting = new Meeting("Meetingtest", "a", "a", 15, 15, "K�rrv�gen 47A, Stockholm, Sweden", 100.0, 150.0, user);
		mapIntent.putExtra("meeting", SerializerHelper.serializeObject(meeting));
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, mapIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		CharSequence contentText = "Press on this to start";
		Notification notification = new Notification(icon, ntext, when);
		
		notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		notificationManager.notify(NOTIFICATION_ID, notification);
		
		/* Start request updates */
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10.0f, this);		
	}

	public void onLocationChanged(Location location) {
	
		/* Update and send the new position to the server */
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		
		Toast.makeText(this, "Moved to latitude: " + latitude * 1000000 + " longitude: " + longitude*1000000, Toast.LENGTH_LONG).show();
		
		user.setLatitude( latitude);
		user.setLongitude(longitude);
		new Thread(new Runnable(){
			public void run() {
				ws.updateUser(user);
			}
		}).start();

		/* Broadcast update to mapview activity */
	}

	
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(),"GPS Disabled", Toast.LENGTH_LONG);
	}

	
	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(),"GPS Enabled", Toast.LENGTH_LONG);

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {}
}

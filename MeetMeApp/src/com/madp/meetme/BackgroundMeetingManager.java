package com.madp.meetme;

import com.madp.meetme.webapi.WebService;
import com.madp.meetme.common.entities.User;
import com.madp.utils.Logger;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class BackgroundMeetingManager extends Service implements LocationListener {

	private WebService ws;
	private User user;
	private LocationManager lm;

	public IBinder onBind(Intent arg0) {return null;}

	public void onCreate() {
		ws = new WebService(new Logger());
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		user = new User(1989, "name", "noiseoverip@gmail.com");
		Toast.makeText(this, "The meeting is now started", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "The Meeting has now stopped. Your position is not displayed in the meeting anymore!", Toast.LENGTH_LONG).show();
		lm.removeUpdates(this);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		/*Start look for locationupdates */
		Toast.makeText(this, "Your position is now displayed to other participants", Toast.LENGTH_LONG).show();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10.0f, this);		
	}

	public void onLocationChanged(Location location) {
	
		/* Update and send the new position to the server */
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		
		Toast.makeText(this, "Moved to latitude: " + latitude * 1000000 + " longitude: " + longitude*1000000, Toast.LENGTH_LONG).show();
		
		user.setLatitude(latitude * 1000000);
		user.setLongitude(longitude * 1000000);
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

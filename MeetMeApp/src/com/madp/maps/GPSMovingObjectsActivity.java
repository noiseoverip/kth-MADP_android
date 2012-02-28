package com.madp.maps;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.madp.meetme.R;
import com.madp.meetme.common.entities.LatLonPoint;
import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.meetme.webapi.WebService;
import com.madp.utils.Logger;
import com.madp.utils.SerializerHelper;

/**
 * 
 * @author esauali 2012-02-28 Removed usage of Meeting.getCoordinates()
 * 
 */
public class GPSMovingObjectsActivity extends GPSActivity {

	private static final String TAG = "GPSMovingObjectsActivity";
	private Meeting meeting;
	private WebService ws;
	private Context mContext;
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			System.out.println("Finalize failed!");
			e.printStackTrace();
		}
	}	

	public void MoveQuietly() {
		Runnable runnable = new Runnable() {
			public void run() {			
				
				for(;;){
					Log.i(TAG, "Updating...");				
					try {
						if (mContext == null){
							break;
						}
						
						Thread.sleep(1000);
						Meeting meetingTmp = ws.getMeeting(meeting.getId());
						
						if (meetingTmp != null) {
							Log.e(TAG, "Meeting object was null");
						}
						
						List<User> users = meetingTmp.getParticipants();
						
						if (users  != null) {
							Log.e(TAG, "Users array was null");
						}
						
						for (User user : users){
							DrawAtMap(user.getLatitude(), user.getLongitude(),
									user.getEmail(), "Android pos", R.drawable.androidmarker);
							// Mymap.postInvalidate();
						}
						
					} catch (Exception e) {
						Log.e(TAG, "Some error while drawing users",e);
					}
				}
				
			}
		};
		new Thread(runnable).start();
	}

	@Override
	protected void onResume() {		
		super.onResume();		
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_map);
		Init();
		
		mContext = this;
		
		// Create webservice
		ws = new WebService(new Logger());
		
		Bundle b = getIntent().getExtras();
		Meeting meeting = (Meeting) SerializerHelper.deserializeObject(b.getByteArray("meeting"));
		if (meeting == null) {
			Log.e("Meeting", "De-serilialisation error");
		}
		
		/*
		// Show my self on the map
		if (locman.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.i(TAG, "will find coordinates by GPS");
			locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 2, loclis);
			Location location = locman.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			DrawAtMap(new LatLonPoint(location.getLatitude(), location.getLongitude()), "Argyris", "mypos",
					R.drawable.androidmarker);
		} else {
			System.out.println("GPS unavailable");
			Toast.makeText(this, "GPS is disabled.", Toast.LENGTH_LONG).show();
		}
		*/
		
		// Show meeting location
		LatLonPoint coordinates = new LatLonPoint(meeting.getLatitude(), meeting.getLongitude());
		DrawAtMap(coordinates, meeting.getTitle(), "Meeting location", R.drawable.bluedot);
		mapcon.animateTo(new GeoPoint(coordinates.getILatitude(), coordinates.getILongitude()));
		mapcon.setZoom(10);
		
		MoveQuietly();

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
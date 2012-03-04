package com.madp.meetme;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;
import com.madp.maps.GPSActivity;
import com.madp.meetme.R;
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
	private Meeting meeting;//, meetingTMP;
	private WebService ws;
	private Context mContext;
	private List<User> users;
	Thread Net_worker;
	private volatile boolean keep_running = true;
	
	
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			keep_running = false;
			finish();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	Runnable runnable = new Runnable() {
		public void run() {			
			while(keep_running){
				Log.i(TAG, "Updating...");				
				meeting = (Meeting )ws.getMeeting(meeting.getId());
				/*	Log.i("GPSMoving Pollserver","user 0 lat"+ meeting.getParticipants().get(0).getLatitude()+
							"user 0 lon"+ meeting.getParticipants().get(0).getLongitude());
					Log.i("GPSMoving Pollserver","user 1 lat"+ meeting.getParticipants().get(1).getLatitude()+
									"user 1 lon"+ meeting.getParticipants().get(1).getLongitude());
				*/	if (meeting == null) {
						Log.e(TAG, "Meeting object was null");
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					users = meeting.getParticipants();
					if (users  == null) {
						Log.e(TAG, "Users array was null");
					}
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							DrawAtMap(meeting.getLatitude(),meeting.getLongitude(), meeting.getTitle(), "Meeting location", R.drawable.bluedot);								
							for (int i=0;i<meeting.getParticipants().size();i++ ){
								DrawAtMap(meeting.getParticipants().get(i).getLatitude(), meeting.getParticipants().get(i).getLongitude(),
										meeting.getParticipants().get(i).getEmail(), "Android pos", R.drawable.androidmarker);
							}
						}
						
					});
					
					
			}
		}
	};
	

	@Override
	protected void onResume() {		
		super.onResume();
		/*Net_worker = new Thread(runnable);
		Net_worker.start();
		*/
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_map);
		Init();
		
		ImageButton cancel = (ImageButton) findViewById(R.id.ib_map_logout);
		
		
		mContext = this;
		
		// Create webservice
		ws = new WebService(new Logger());
		
		Bundle b = getIntent().getExtras();
		meeting = (Meeting) SerializerHelper.deserializeObject(b.getByteArray("meeting"));
		if (meeting == null) {
			Log.e("Meeting", "De-serilialisation error");
		}
		
		mapcon.animateTo(new GeoPoint((int)meeting.getLatitude()*1000000,(int)meeting.getLongitude()*1000000));
		mapcon.setZoom(10);
		
		Net_worker = new Thread(runnable);
		Net_worker.start();
		
		
		cancel.setOnClickListener( new View.OnClickListener() {
			
		public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					keep_running = false;
					
					finish();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
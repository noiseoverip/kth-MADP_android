package com.madp.maps;


import com.google.android.maps.GeoPoint;
import com.madp.meetme.R;
import com.madp.meetme.common.entities.LatLonPoint;
import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.meetme.webapi.WebService;
import com.madp.utils.Logger;
import com.madp.utils.SerializerHelper;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;



public class GPSMovingObjectsActivity extends GPSActivity {


	WebService ws;
	Meeting t = null;
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

	public void PollServer(){
		Runnable runnable = new Runnable(){
			public void run(){       		
                  Log.i("WEB_SERVICE","will try to fetch meeting "+t.getId());
                  t = (Meeting) ws.getMeeting(t.getId());
			}			
		};
		new Thread(runnable).start();
	}
	
	public void MoveQuietly(){		
		Runnable runnable = new Runnable(){
			public void run(){       		
                  Log.i(LOCATION_SERVICE,"will try to draw them");
					for(int j=0;j<t.getParticipants().size();j++){
							 DrawAtMap(t.getParticipants().get(j).getCoordinates(),
									   t.getParticipants().get(j).getName(),
									   t.getAddress(),R.drawable.androidmarker );
					//		 Mymap.postInvalidate();						 
					}
                }			
		};
		new Thread(runnable).start();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		PollServer();
		//MoveQuietly();
	}

	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_map);
        Init();
        ws = new WebService(new Logger()); 
        
        Bundle b= getIntent().getExtras();
		t = (Meeting) SerializerHelper.deserializeObject(b.getByteArray("meeting"));
		if(t == null){
			Log.e("Meeting", "De-serilialisation error");
		}
        DrawAtMap(t.getCoordinates(),t.getTitle(),"Meeting location",R.drawable.bluedot );       
		for(int i=0;i<t.getParticipants().size();i++){
			DrawAtMap(t.getParticipants().get(i).getCoordinates(),
					t.getParticipants().get(i).getName(),t.getTitle(), R.drawable.androidmarker);
		}
        mapcon.animateTo(new GeoPoint(t.getCoordinates().getILatitude(), t.getCoordinates().getILongitude()));
        mapcon.setZoom(10);
        
    }
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
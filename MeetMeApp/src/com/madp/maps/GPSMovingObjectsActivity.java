package com.madp.maps;


import com.google.android.maps.GeoPoint;
import com.madp.meetme.R;
import com.madp.meetme.common.entities.LatLonPoint;
import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.utils.SerializerHelper;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;



public class GPSMovingObjectsActivity extends GPSActivity {

	
	User[] user_group =null;
	
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
	
	public void MovePersons(){
		for(int i=0;i<user_group.length;i++){
			/*
			user_group[i].MoveTo(user_group[i].getCoordinates().getDLatitude()+0.1,
					user_group[i].getCoordinates().getDLongitude()+0.1);
			Log.i(LOCATION_SERVICE,"Moved "+user_group[i]+"at "+user_group[i].getCurrentPosition().getILatitude()+" - "+user_group[i].getCurrentPosition().getILongitude());
			*/
		}
	}
	
	public void MoveQuietly(){		
		Runnable runnable = new Runnable(){
			public void run(){       		
        		for(int i=0;i<50;i++){
	                Log.i(LOCATION_SERVICE,"will try to draw them");
	                MovePersons();
	                try {
						Thread.sleep(1000);
						for(int j=0;j<user_group.length;j++){
								 DrawAtMap(user_group[j].getCoordinates(),user_group[j].getName(),"Android pos",R.drawable.androidmarker );
						//		 Mymap.postInvalidate();						 
							}
						} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }			
			}
		};
		new Thread(runnable).start();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MoveQuietly();
	}

	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_map);
        Init();
        
        Bundle b= getIntent().getExtras();
		Meeting t = (Meeting) SerializerHelper.deserializeObject(b.getByteArray("meeting"));
		if(t == null){
			Log.e("Meeting", "De-serilialisation error");
		}
		
        //Show my self on the map
        if(locman.isProviderEnabled(LocationManager.GPS_PROVIDER)){ 
            System.out.println("will find coordinates by GPS");
            locman.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2, loclis);
            Location location=locman.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            DrawAtMap(new LatLonPoint(location.getLatitude(), location.getLongitude()),"Argyris","mypos",R.drawable.androidmarker );          
        }
        else{
        	System.out.println("GPS unavailable");
            Toast.makeText(this,"GPS is disabled.", Toast.LENGTH_LONG).show();             
        }
        DrawAtMap(t.getCoordinates(),t.getTitle(),"Meeting location",R.drawable.bluedot );       
        mapcon.animateTo(new GeoPoint(t.getCoordinates().getILatitude(), t.getCoordinates().getILongitude()));
        mapcon.setZoom(10);
        
    }
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
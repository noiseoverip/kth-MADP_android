package com.madp.maps;




import com.madp.meetme.R;
import com.madp.meetme.common.entities.LatLonPoint;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class GPSLocationFinderActivity extends GPSActivity {
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Mymap.invalidate();	
	}
	
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
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_map);
        Init();
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
       
        
    }

}

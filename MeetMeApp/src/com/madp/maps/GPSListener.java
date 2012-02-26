package com.madp.maps;




import com.madp.meetme.R;
import com.madp.meetme.common.entities.LatLonPoint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GPSListener implements  LocationListener 
{
	GPSActivity ActivityUsingMap;
    public void onLocationChanged(Location location) {
    	if(location!=null) {  
	            ActivityUsingMap.DrawAtMap(new LatLonPoint(location.getLatitude(), location.getLongitude()),"Argyris","mypos",R.drawable.androidmarker );
		ActivityUsingMap.getMap().postInvalidate();	
        }else{
            System.out.println("Error receiving Location(received null)");
        }
    }
    public GPSListener(GPSActivity s){
    	ActivityUsingMap=s;
    }
    public void onProviderDisabled(String provider) {}
    public void onProviderEnabled(String provider) {}
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}

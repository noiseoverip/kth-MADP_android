package com.madp.maps;

import com.google.android.maps.GeoPoint;
import com.madp.meetme.R;
import com.madp.meetme.common.entities.LatLonPoint;
import com.madp.meetme.common.entities.Meeting;
import com.madp.utils.SerializerHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

/**
 * 
 * @author esauali 2012-02-28 Removed usage of User.getCoordinates()
 *
 */
public class GPSFindLocationFromStringOnMap extends GPSActivity{

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.gps_map);
		Init();
		
		ImageButton cancel = (ImageButton) findViewById(R.id.ib_map_logout);
		
		
        Bundle b = getIntent().getExtras();
        byte [] d = (byte[]) b.getByteArray("meeting");
        Meeting s= (Meeting) SerializerHelper.deserializeObject(d);
        if(s == null){
        	Log.e("getting meeting from bundle", "Desirialised to null");
        }
 		Mymap.invalidate();
		
 		LatLonPoint coordinates = new LatLonPoint(s.getLatitude(), s.getLongitude());
 		Log.i("cheking bundle throughput ",  "Meeting name = "+s.getTitle()+" owner =" + s.getOwner()+
 			  " address= "+s.getAddress() + " location lat = " + coordinates.getILatitude()+ " loc lon = "+coordinates.getILongitude());
 		
		DrawAtMap(coordinates, s.getAddress(), s.getOwner().getName(),R.drawable.bluedot);
		mapcon.animateTo(new GeoPoint(coordinates.getILatitude(), coordinates.getILongitude()));
        mapcon.setZoom(10);	
		
        cancel.setOnClickListener( new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					finish();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	
	
	
	
	
	
	
	}
	
}

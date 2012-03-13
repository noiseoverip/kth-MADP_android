package com.madp.meetme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.madp.maps.GPSListener;
import com.madp.meetme.common.entities.Meeting;
import com.madp.utils.SerializerHelper;

//TODO: should initially zoom in to current user location
/**
 * Activity where user places a point on the map were meeting is suppose to take place.
 *
 * @author esauali 2012-03-05 Initial version
 *
 */
public class GPSPlaceAMeeting extends MapActivity implements LocationListener {
	
	private static final String TAG = "GPSPlaceAMeeting";
	
	private LocationManager lm;
	private MapView mapView;
	private MapController mapCon;
	private Context mContext;	
	private GeoPoint geoPoint;
    double longitude;
    double latitude;
	
	@Override
	protected boolean isRouteDisplayed() {		
		return false;
	}
  
	public void onLocationChanged(Location location) {		
		/* Update and send the new position to the server */
		latitude = location.getLatitude();
		longitude = location.getLongitude();		
		Toast.makeText(this, "Moved to latitude: " + latitude * 1000000 + " longitude: " + longitude*1000000, Toast.LENGTH_LONG).show();
		mapCon.animateTo(new GeoPoint((int)latitude*1000000,(int)longitude *1000000));
	}
	
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(),"GPS Disabled", Toast.LENGTH_LONG);
	}

	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(),"GPS Enabled", Toast.LENGTH_LONG);

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_map);
		
		mContext = this;
		mapView = (MapView) findViewById(R.id.mapView);		
		mapView.setBuiltInZoomControls(true);    
		
		mapView.getOverlays().add(new AddLocationOverlay());
		mapCon = (MapController) mapView.getController();
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		
		Meeting m = null;
		ImageButton cancel = (ImageButton) findViewById(R.id.ib_map_logout);	
		
		Bundle b = getIntent().getExtras();
		if(b == null){
			Log.e(TAG, "error getting extras from activity");
			/*user has not enter something on the address field*/
			/*get user's current position and animate map to that location*/
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10.0f, this);
			//mapCon.animateTo(new GeoPoint(59404862,17949859)); //kth location
			
		}
		else{
				byte a[] = b.getByteArray("meeting");
				if(a == null){
					Log.e(TAG, "error getting byte array");
				}
				else{
					 m = (Meeting) SerializerHelper.deserializeObject(a);
					 if(m == null){
							lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10.0f, this);
							Log.e(TAG, "error deserializing");

					 }
					 else if(!m.getAddress().equals("")){
							/*user has entered location address*/
						 	Log.d(TAG, "address = "+m.getAddress());
						 	Log.d(TAG, "Coordinates:Lat = "+m.getLatitude());
						 	Log.d(TAG, "Coordinates:Lon = "+m.getLongitude());
						 	
							mapCon.animateTo(new GeoPoint((int)m.getLatitude()*1000000,(int)m.getLongitude() *1000000));
							mapCon.setZoom(15);
					}
					else
						lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10.0f, this);

				}
		}
		
		
		cancel.setOnClickListener(new View.OnClickListener() {
			//TODO: should handle situation when user doesn't select the point on map
			@Override
			public void onClick(View v) {
				if (geoPoint != null){
					Intent intent = new Intent();
					Bundle s = new Bundle();
					s.putInt("latitude", geoPoint.getLatitudeE6());
					s.putInt("longitude", geoPoint.getLongitudeE6());
					intent.putExtras(s);
					setResult(RESULT_OK, intent);		
					finish();
				} else {
					Toast.makeText(v.getContext(), "Please select point on map", Toast.LENGTH_SHORT).show();
				}
			}
		});
			
	}
	
	private class AddLocationOverlay extends Overlay {
		
		
		public boolean onTap(GeoPoint p, MapView mapView){
			Log.d(TAG, "Got a point:"+p.getLatitudeE6()+":"+p.getLongitudeE6());			
			geoPoint = p;
			mapView.invalidate();
			return true;			
		}
		
		protected void drawCircle(Canvas canvas, Point curScreenCoords) {	   
		    // Draw inner info window
		    canvas.drawCircle((float) curScreenCoords.x, (float) curScreenCoords.y, 15, getInnerPaint());		
		    canvas.drawCircle((float) curScreenCoords.x, (float) curScreenCoords.y, 30, getBorderPaint());
		}

		private Paint innerPaint, borderPaint;

		public Paint getInnerPaint() {
		    if (innerPaint == null) {
		        innerPaint = new Paint();
		        innerPaint.setARGB(225, 255, 0, 0); // gray
		        innerPaint.setAntiAlias(true);
		    }
		    return innerPaint;
		}

		public Paint getBorderPaint() {
		    if (borderPaint == null) {
		        borderPaint = new Paint();
		        borderPaint.setARGB(255, 68, 89, 82);
		        borderPaint.setAntiAlias(true);
		        borderPaint.setStyle(Style.STROKE);
		        borderPaint.setStrokeWidth(4);
		    }
		    return borderPaint;
		}
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			if (geoPoint != null){
				Point p = new Point();		   
		    	drawCircle(canvas, mapView.getProjection().toPixels(geoPoint, p));
			}		    
		}
	}
}

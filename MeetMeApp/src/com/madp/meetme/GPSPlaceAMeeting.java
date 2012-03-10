package com.madp.meetme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
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

//TODO: should initially zoom in to current user location
/**
 * Activity where user places a point on the map were meeting is suppose to take place.
 *
 * @author esauali 2012-03-05 Initial version
 *
 */
public class GPSPlaceAMeeting extends MapActivity {
	
	private static final String TAG = "GPSPlaceAMeeting";
	
	private MapView mapView;
	private MapController mapCon;
	private Context mContext;	
	private GeoPoint geoPoint;
	
	@Override
	protected boolean isRouteDisplayed() {		
		return false;
	}
  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_map);
		
		mContext = this;
		mapView = (MapView) findViewById(R.id.mapView);		
		mapView.setBuiltInZoomControls(true);    
		
		mapView.getOverlays().add(new AddLocationOverlay());
		mapCon = (MapController) mapView.getController();
		
		ImageButton cancel = (ImageButton) findViewById(R.id.ib_map_logout);	
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
		
		mapCon.animateTo(new GeoPoint(59404862,17949859)); //kth location
		mapCon.setZoom(16);		
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

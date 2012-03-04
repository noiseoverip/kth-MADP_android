package com.madp.maps;

import java.util.List;

import com.madp.meetme.R;
import com.madp.meetme.common.entities.LatLonPoint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * 
 * @author esauali 2012-02-28 Added DrawAtMap(double latitude, double longitude, String id, String snippet,int drawable_item)
 *
 */
public class GPSActivity extends MapActivity {

	protected MapView Mymap;
	protected List <Overlay> mapOverlays;
	protected LocationManager locman; 
	protected LocationListener loclis;
	protected MapController mapcon;
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
		
	}
	public void Init(){
		Mymap= (MapView) findViewById(R.id.mapView);
        Mymap.setBuiltInZoomControls(true);
        mapOverlays = Mymap.getOverlays();
        
        locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
        loclis= (LocationListener) new GPSListener(this);
        mapcon = (MapController) Mymap.getController();  
	}

	public MapView getMap(){
		return Mymap;
	}
	
	public void DrawAtMap(LatLonPoint g, String id, String snippet,int drawable_item) {
		Log.i(LOCATION_SERVICE, "will draw "+id +"at lon= "+g.getILongitude()+" lat= "+g.getILatitude());
        Drawable drawable = this.getResources().getDrawable(drawable_item);
        MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(drawable);       
    
        Mymap.postInvalidate();
        for(int i=0;i<mapOverlays.size();i++){
        	MyItemizedOverlay s=(MyItemizedOverlay) mapOverlays.get(i);
        	s.DeleteOverlayItem(id);
        }
       
        OverlayItem overlayitem=new OverlayItem(new GeoPoint(g.getILatitude(),g.getILongitude()),id,snippet);
        itemizedoverlay.addOverlay(overlayitem);            
        mapOverlays.add(itemizedoverlay);
        
    }
	
	public void DrawAtMap(double latitude, double longitude, String id, String snippet,int drawable_item) {
		LatLonPoint coordinates = new LatLonPoint(latitude, longitude);
		this.DrawAtMap(coordinates, id, snippet, drawable_item);
	}
}

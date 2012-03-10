package com.madp.meetme;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.madp.maps.GPSActivity;
import com.madp.maps.MyItemizedOverlay;
import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.meetme.webapi.WebService;
import com.madp.utils.Logger;
import com.madp.utils.SerializerHelper;

/**
 * @author Argyris Initial version
 * @author esauali 2012-02-28 Removed usage of Meeting.getCoordinates()
 * @authod esauali 2012-03-06 Added show meeting location, added temp fix for incorrect coordinates sent from server
 * 
 */
public class GPSMovingObjectsActivity2 extends GPSActivity {

	private static final String TAG = "GPSMovingObjectsActivity";
	private Meeting meeting;//, meetingTMP;
	private WebService ws;
	private Context mContext;
	private List<User> users;
	Thread Net_worker;
	private volatile boolean keep_running = true;	
	
	private List<FakeUser> fakeLocations;
	private MyItemizedOverlay usersOverlay;
	private int i = 0;
	private MapView mapView; 
	
	
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
				//meeting = (Meeting )ws.getMeeting(meeting.getId());
				/*	Log.i("GPSMoving Pollserver","user 0 lat"+ meeting.getParticipants().get(0).getLatitude()+
							"user 0 lon"+ meeting.getParticipants().get(0).getLongitude());
					Log.i("GPSMoving Pollserver","user 1 lat"+ meeting.getParticipants().get(1).getLatitude()+
									"user 1 lon"+ meeting.getParticipants().get(1).getLongitude());
				*/	
					/*
					if (meeting == null) {
						Log.e(TAG, "Meeting object was null");
					}
					*/
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					/*
					users = meeting.getParticipants();
					if (users  == null) {
						Log.e(TAG, "Users array was null");
					}
					*/
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							/*
							DrawAtMap(meeting.getLatitude(),meeting.getLongitude(), meeting.getTitle(), "Meeting location", R.drawable.bluedot);								
							for (int i=0;i<meeting.getParticipants().size();i++ ){
								DrawAtMap(meeting.getParticipants().get(i).getLatitude(), meeting.getParticipants().get(i).getLongitude(),
										meeting.getParticipants().get(i).getEmail(), "Android pos", R.drawable.androidmarker);
							}
							*/
							usersOverlay.clear();
							for (FakeUser user : fakeLocations){							
								usersOverlay.addOverlay(new OverlayItem(user.locations.get(i), user.name, "snipet text"));								
							}
							mapView.postInvalidate();
							// start from begining when out of coordinates
							i= ((i+1) == fakeLocations.get(0).locations.size()) ? 0 : i+1;							
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
		
		//Show meeting position on map
		mapView = (MapView) this.findViewById(R.id.mapView);
		List<Overlay> overlays = mapView.getOverlays();
		overlays.add(new AddLocationOverlay());
		
		//Add users overlay
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		usersOverlay = new MyItemizedOverlay(drawable);
		overlays.add(usersOverlay);		
		
		Log.d(TAG, "Animating to: "+meeting.getLatitude() + "::" + meeting.getLongitude());
		//TODO: fix bug on server returning latitude instead of longitude and vise versus
		mapcon.animateTo(new GeoPoint((int)meeting.getLongitude(),(int)meeting.getLatitude()));
		mapcon.setZoom(16);
		
		fakeLocations = createFakeLocations();
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
	
	private class AddLocationOverlay extends Overlay {	
		
		public boolean onTap(GeoPoint p, MapView mapView){			
			return false;			
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
			GeoPoint geoPoint = new GeoPoint((int)meeting.getLongitude(), (int)meeting.getLatitude());
			if (geoPoint != null){
				Point p = new Point();		   
		    	drawCircle(canvas, mapView.getProjection().toPixels(geoPoint, p));
			}		    
		}
	}
	
	private class FakeUser {
		public String name;
		public List<GeoPoint> locations;
	}
	
	private List<FakeUser> createFakeLocations() {
		List<FakeUser> fakeUsers = new ArrayList<FakeUser>();
		FakeUser peter = new FakeUser();
		peter.locations = new ArrayList<GeoPoint>();
		peter.locations.add(new GeoPoint(59411200, 17942600));
		peter.locations.add(new GeoPoint(59409500, 17946900));
		peter.locations.add(new GeoPoint(59408000, 17950400));
		peter.locations.add(new GeoPoint(59406700, 17954000));
		
		FakeUser niklas = new FakeUser();
		niklas.locations = new ArrayList<GeoPoint>();
		niklas.locations.add(new GeoPoint(59407600, 17932300));
		niklas.locations.add(new GeoPoint(59405300, 17939400));
		niklas.locations.add(new GeoPoint(59402800, 17943800));
		niklas.locations.add(new GeoPoint(59403600, 17947700));
		
		FakeUser argyris = new FakeUser();
		argyris.locations = new ArrayList<GeoPoint>();
		argyris.locations.add(new GeoPoint(59407600, 17932300));
		argyris.locations.add(new GeoPoint(59407600, 17932300));
		argyris.locations.add(new GeoPoint(59407600, 17932300));
		argyris.locations.add(new GeoPoint(59407600, 17932300));
		
		fakeUsers.add(peter);
		fakeUsers.add(niklas);
		fakeUsers.add(argyris);
		
		return fakeUsers;		
	}
}
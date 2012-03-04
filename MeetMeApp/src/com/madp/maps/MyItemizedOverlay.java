package com.madp.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> mOverlays = new Vector();
	Context mContext;
	
	public int FindOverlayItem(String id){
		for (int i=0; i<mOverlays.size();i++){
			OverlayItem dummy=mOverlays.get(i);
			if(dummy.getTitle().equals(id))
				return i;
		}
		return -1;
		
	}
	public OverlayItem GetOverlayItem(String id){
		int pos = FindOverlayItem(id);
		if(pos != -1)
			return mOverlays.get(pos);
		else
			return null;
	}
	
	public void DeleteOverlayItem(String id){
		int pos = FindOverlayItem(id);
		if(pos != -1){
			mOverlays.remove(pos);
		}
	}
	
	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub		
	}

	public MyItemizedOverlay(Drawable defaultMarker, Context context) {
		  super(defaultMarker);
		  mContext = context;
	}	
    
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	public  void removeOverlay(OverlayItem overlay) {
	    mOverlays.remove(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}
}

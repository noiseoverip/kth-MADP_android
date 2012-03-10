package com.madp.maps;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * 
 * @author Argyris initial version
 * @author esauali add clear(), add populate to DeleteOverlayItem, modify FindOverlayItem
 *
 */
public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> mOverlays = new Vector<OverlayItem>();
	Context mContext;
	
	public int FindOverlayItem(String id){
		for (OverlayItem item : mOverlays){			
			if(item.getTitle().equals(id)){
				return mOverlays.indexOf(item);
			}
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
		populate();
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
	
	public void removeOverlay(OverlayItem overlay) {
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
	
	public void clear() {
		mOverlays.clear();
	    populate();
	}
}

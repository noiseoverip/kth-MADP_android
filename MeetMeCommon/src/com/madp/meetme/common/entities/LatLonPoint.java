package com.madp.meetme.common.entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.util.Log;


/* need this class to easily handle the coordinates of meetings and persons
 * when they are show in the map*/
public class LatLonPoint implements Serializable {
    int lat;
    int lon;
	public LatLonPoint(double latitude, double longitude) {
        lat = (int)(latitude * 1E6);
        lon = (int) (longitude * 1E6);
       	Log.i(null, "point at lat "+latitude+" lon "+longitude);        
    }
    public int getILongitude(){
    	return lon;
    }
    public int getILatitude(){
		return lat;    	
    }
    
    public double getDLongitude(){
    	return lon/1E6;
    }
    public double getDLatitude(){
		return lat/1E6;    	
    }

    /**
	 * Always treat de-serialization as a full-blown constructor, by validating
	 * the final state of the de-serialized object.
	 */
	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException,
			IOException {
		aInputStream.defaultReadObject();
	}

	/**
	 * This is the default implementation of writeObject. Customise if
	 * necessary.
	 */
	private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
		// perform the default serialization for all non-transient, non-static
		// fields
		aOutputStream.defaultWriteObject();
	}
	
}

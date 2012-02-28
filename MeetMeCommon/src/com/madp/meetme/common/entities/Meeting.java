package com.madp.meetme.common.entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * Meeting object class. Contains all meeting related information.
 * 
 * @author esauali initial version
 * @author esauali 2012-02-28 removed coordinates stuff
 * 
 */
public class Meeting implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5945827205183259723L;
	private int id;
	private String title;
	private String tCreated; // creation date
	private String tStarting; // meetings starting date
	private int duration; // duration in minutes
	private int monitoring; // minuted before starting to start monitoring
	private String address;
	private double latitude;
	private double longitude;		
	private List<User> participants;
	private User owner;	// meeting creator	

	/**
	 * Default constructor
	 */
	public Meeting() {
		participants = new ArrayList<User>();
	}

	public Meeting (String title, String tCreated, String tStarting, int duration, int monitoring, String address, double longitude, double latitude, User owner){
		this.title = title;
		this.tCreated = tCreated;		
		this.tStarting = tStarting;
		this.duration = duration;
		this.monitoring = monitoring;
		this.address = address;
		this.latitude =latitude;
		this.longitude =longitude;		
		this.owner = owner;
		this.participants = new ArrayList<User>();
	}

	public String getAddress() {
		return address;
	}	

	public int getDuration() {
		return duration;
	}

	public int getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public int getMonitoring() {
		return monitoring;
	}

	public User getOwner() {
		return owner;
	}

	public List<User> getParticipants() {
		return participants;
	}

	public String gettCreated() {
		return tCreated;
	}

	public String getTitle() {
		return title;
	}

	public String gettStarting() {
		return tStarting;
	}

	/* WHIS SHOULD NOT BE HERE
	public static JSONObject getLocationInfo(String address) {
		
		Log.d("address info", "address = "+address);
		
	    StringBuilder stringBuilder = new StringBuilder();
	    try {

	    address = address.replaceAll(" ","%20");    

	    HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response;
	    stringBuilder = new StringBuilder();


	        response = client.execute(httppost);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
	        while ((b = stream.read()) != -1) {
	            stringBuilder.append((char) b);
	        }
	    } catch (ClientProtocolException e) {
	    } catch (IOException e) {
	    }

	    JSONObject jsonObject = new JSONObject();
	    try {
	        jsonObject = new JSONObject(stringBuilder.toString());
	    } catch (JSONException e) {	        
	        e.printStackTrace();
	    }

	    return jsonObject;
	}
	public static LatLonPoint  getLatLong(JSONObject jsonObject) {

        Double lon = new Double(0);
        Double lat = new Double(0);

        try {

            lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

        } catch (Exception e) {
            e.printStackTrace();

        }

        Log.i("coordinates::","Lat = "+lat+" Lon = "+lon);
        return new LatLonPoint(lat,lon);
    }
	*/
	/**
	 * Always treat de-serialization as a full-blown constructor, by validating
	 * the final state of the de-serialized object.
	 */
	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException,
			IOException {
		aInputStream.defaultReadObject();
	}

	public void setAddress(String address) {
		this.address = address;		
	}	

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}	

	public void setMonitoring(int monitoring) {
		this.monitoring = monitoring;
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	public void setParticipants(List<User> participants) {
		this.participants = participants;
	}

	public void settCreated(String tCreated) {
		this.tCreated = tCreated;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void settStarting(String tStarting) {
		this.tStarting = tStarting;
	}

	@Override
	public String toString(){
		return "Meeting id:"+this.id+" starting:"+this.tStarting+" participants:"+((participants != null) ? this.participants.size() : 0);
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

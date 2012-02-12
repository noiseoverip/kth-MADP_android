package com.madp.meetme.common.entities;

import java.util.ArrayList;
import java.util.List;


/**
 * Meeting object class. Contains all meeting related information.
 * 
 * @author esauali
 * 
 */
public class Meeting {
	private int id;
	private String title;
	private String tCreated; // creation date
	private String tStarting; // meetings starting date
	private int duration; // duration in minutes
	private int monitoring; // minuted before starting to start monitoring
	private String address;
	private double longitude;
	private double latitude;	
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
		this.longitude = longitude;
		this.latitude = latitude;
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

	
}

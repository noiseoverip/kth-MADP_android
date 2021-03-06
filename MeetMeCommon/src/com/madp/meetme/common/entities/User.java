package com.madp.meetme.common.entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * User object. Contain all user related information like: usename, user email, user location...
 * 
 * @author esauali initial version
 * @author esauali 2012-02-28 removed LatLonpoint
 * 
 */
public class User implements Serializable {
	public enum Status {
		OK, OK_NOLOC, NO, WAITING, FAKE
	}

	private static final long serialVersionUID = 2061673643035123774L;

	int id;
	private String name;
	private String email;
	private String time; // last location update time
	private double latitude;
	private double longitude;;
	private Status currentStatus;

	/**
	 * Default constructor
	 */
	public User() {

	}

	/**
	 * Convenience constructor
	 * 
	 * @param id
	 * @param name
	 * @param email
	 */
	public User(int id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
	}

	/**
	 * Convenience constructor
	 * 
	 * @param email
	 */
	public User(String email) {
		this(-1, null, email);
	}

	public Status getCurrentStatus() {
		return currentStatus;
	}

	public String getEmail() {
		return email;
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
	
	
	public String getName() {
		return name;
	}

	public String getTime() {
		return time;
	}

	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException {
		aInputStream.defaultReadObject();
	}

	public void setCurrentStatus(Status currentStauts) {
		this.currentStatus = currentStauts;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * This is the default implementation of writeObject. Customize if necessary.
	 */
	private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
		// perform the default serialization for all non-transient, non-static
		// fields
		aOutputStream.defaultWriteObject();
	}

	@Override
	public String toString() {
		return "User id:" + id + " name:" + name + " email:" + email + " long" + longitude + " lat:" + latitude
				+ " status:" + currentStatus;
	}

}

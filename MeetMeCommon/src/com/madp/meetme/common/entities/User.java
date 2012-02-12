package com.madp.meetme.common.entities;

/**
 * User object. Contain all user related information like: usename, user email, user location...
 * 
 * @author esauali
 * 
 */
public class User {
	int id;
	String name;
	String email;
	String time; // last location update time
	Double longitude;
	Double latitude;

	/**
	 * Default constuctor
	 */
	public User() {

	}

	/**
	 * Convinience constructor
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
	
	public String getEmail() {
		return email;
	}

	public int getId() {
		return id;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String getName() {
		return name;
	}

	public String getTime() {
		return time;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTime(String time) {
		this.time = time;
	}
}

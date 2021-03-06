package com.madp.entities;

import static com.madp.utils.ServiceUtils.FAILED_CLOSE_MYSQL;
import static com.madp.utils.ServiceUtils.FAILED_CONNECT_MYSQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.madp.utils.SqlUtils;

/**
 * User object. Contain all user related information like: usename, user email, user location...
 * 
 * @author esauali
 * 
 */
public class User {
	public enum Status {
		OK, OK_NOLOC, NO, WAITING
	};

	public static User getUserByEmail(String email, boolean create) throws Exception {

		User user = new User();
		user.setEmail(email);

		Connection con = SqlUtils.getConnection();
		if (con == null) {
			throw new Exception(FAILED_CONNECT_MYSQL);
		}

		PreparedStatement stm = null;
		ResultSet rs = null;

		stm = con.prepareStatement(SQL_GET_USER_BY_EMAIL);
		stm.setString(1, user.getEmail());
		rs = stm.executeQuery();
		if (rs.next()) {
			user.setId(rs.getInt(1));
		} else if (create) { // create new User
			stm = con.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS);
			stm.setString(1, user.getEmail());
			if (stm.executeUpdate() == 1) {
				rs = stm.getGeneratedKeys();
				if (rs.next()) {
					user.setId(rs.getInt(1));
					logger.debug("Added new user id:" + user.getId() + " email:" + user.getEmail());
				}
			} else {
				throw new Exception("Could create new user " + user.getEmail());
			}
		} else {
			return null;
		}
		return user;
	}

	int id;
	String name;
	String email;
	String time;
	Double longitude;;
	Double latitude;

	private Status currentStatus;
	private static Logger logger = Logger.getLogger(User.class);
	private static final String SQL_GET_USER_BY_EMAIL = "SELECT * FROM `users` WHERE `email`=?";
	private static final String SQL_INSERT_USER = "INSERT INTO `users` (`email`) VALUES (?)";

	private static final String SQL_UPDATE_LOCATION = "UPDATE `users` SET `longitude`=?, `latitude`=? WHERE `user_id`=?";
	private static final String SQL_UPDATE_STATUS = "UPDATE `participants` SET `status`=? WHERE `meeting_id`=? AND `user_id`=?";
	
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
	public User(int id, String email, double longitude, double latitude, Status currentStatus) {
		this.id = id;
		this.email = email;
		this.longitude = longitude;
		this.latitude = latitude;
		this.currentStatus = currentStatus;
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

	public void setCurrentStatus(Status currentStauts) {
		this.currentStatus = currentStauts;
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

	@Override
	public String toString() {
		return "User id:" + id + " name:" + name + " email:" + email + " long" + longitude + " lat:" + latitude
				+ " status:" + currentStatus;
	}
	
	/**
	 * Update user in users table
	 * @throws Exception
	 */
	public void update() throws Exception {
		Connection con = SqlUtils.getConnection();
		if (con == null) {
			throw new Exception(FAILED_CONNECT_MYSQL);
		}

		PreparedStatement stm = null;
		try {
			stm = con.prepareStatement(SQL_UPDATE_LOCATION);
			stm.setDouble(1, longitude);
			stm.setDouble(2, latitude);
			stm.setInt(3, this.id);
			if (stm.executeUpdate() != 1) {
				throw new Exception("Could not update user location:" + this.toString());
			}
		} finally {
			if (null != stm) {
				try {
					stm.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
			if (null != con) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.error(FAILED_CLOSE_MYSQL, e);
				}
			}
		}
	}
	
	/**
	 * Update user status for meeting id
	 * @param meetingId
	 * @throws Exception 
	 */
	public void updateStatus(int meetingId) throws Exception{
		Connection con = SqlUtils.getConnection();
		if (con == null) {
			throw new Exception(FAILED_CONNECT_MYSQL);
		}

		PreparedStatement stm = null;
		try {
			stm = con.prepareStatement(SQL_UPDATE_STATUS);
			stm.setString(1, this.getCurrentStatus().toString());
			stm.setInt(2, meetingId);
			stm.setInt(3, this.id);
			logger.debug(stm.toString());
			if (stm.executeUpdate() != 1) {
				throw new Exception("Could not update user status:" + this.toString());
			}
		} finally {
			if (null != stm) {
				try {
					stm.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
			if (null != con) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.error(FAILED_CLOSE_MYSQL, e);
				}
			}
		}
	}
}

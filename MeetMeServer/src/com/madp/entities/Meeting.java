package com.madp.entities;

import static com.madp.utils.ServiceUtils.FAILED_CLOSE_MYSQL;
import static com.madp.utils.ServiceUtils.FAILED_CONNECT_MYSQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.madp.utils.SqlUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Meeting {
	private static Logger logger = Logger.getLogger(Meeting.class);

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
	private User owner;

	private com.mysql.jdbc.Connection con;

	private static final String SQL_INSERT_MEETING = "INSERT INTO `meetings`(`title`, `starting`, `duration`, `monitoring`, `address`, `longitude`, `latitude`,`owner_id`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_GET_MEETINGS = "SELECT `m`.*, `u`.`email`,`u`.`longitude`,`u`.`latitude` FROM `meetings` AS m LEFT JOIN users AS u on m.owner_id=u.user_id ORDER BY `created` DESC LIMIT ?,?";
	private static final String SQL_GET_MEETINGS_USER = "SELECT `m`.*, `u`.`email`,`u`.`longitude`,`u`.`latitude` FROM `meetings` AS m LEFT JOIN users AS u on m.owner_id=u.user_id WHERE `meeting_id` IN (SELECT DISTINCT `meeting_id` FROM `participants` WHERE `user_id` = ?) ORDER BY `created` DESC LIMIT ?,?";
	private static final String SQL_GET_MEETING = "SELECT `m`.*, `u`.`email`,`u`.`longitude`,`u`.`latitude` FROM `meetings` AS m LEFT JOIN users AS u on m.owner_id=u.user_id WHERE `meeting_id`=?";
	private static final String SQL_GET_MEETING_PARTICIPANTS = "SELECT u.`user_id`, u.`email`, `u`.`longitude`,`u`.`latitude` FROM `participants` p NATURAL JOIN `users` u WHERE p.`meeting_id`=?";
	private static final String SQL_ADD_PARTICIPANT = "INSERT INTO `participants` (meeting_id, user_id) VALUES (?,?)";

	public Meeting() {

	}
	
	private void getMeetingParticipants(int id, List<User> participants) throws SQLException{		
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			// get meeting participants
			stm = con.prepareStatement(SQL_GET_MEETING_PARTICIPANTS);
			stm.setInt(1, id);
			logger.debug(stm.toString());
			rs = stm.executeQuery();
			while (rs.next()) {
				User participant = new User(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4));
				participants.add(participant);
			}

		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
			if (null != stm) {
				try {
					stm.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
			
	}
	/**
	 * Constructor used for creating Meeting object from database
	 * 
	 * @param id
	 * @param title
	 * @param tCreated
	 * @param tStarting
	 * @param duration
	 * @param monitoring
	 * @param address
	 * @param longitude
	 * @param latitude
	 */
	public Meeting(int id, String title, String tCreated, String tStarting, String duration, String monitoring,
			String address, Double longitude, Double latitude, User owner) {
		this.id = id;
		this.title = title;
		this.tCreated = tCreated;
		this.tStarting = tStarting;
		this.duration = Integer.parseInt(duration);
		this.monitoring = Integer.parseInt(monitoring);
		this.address = address;
		this.longitude = longitude;
		this.latitude = latitude;
		this.owner = owner;
		this.participants = new ArrayList<User>();
	}

	/**
	 * Constructor for creating Meeting object from HTTP request
	 * 
	 * @param title
	 * @param tCreated
	 * @param tStarting
	 * @param duration
	 * @param monitoring
	 * @param address
	 * @param longitude
	 * @param latitude
	 */
	public Meeting(String title, String tCreated, String tStarting, String duration, String monitoring, String address,
			String longitude, String latitude) {
		this.title = title;
		this.tCreated = tCreated;
		this.tStarting = tStarting;
		this.duration = Integer.parseInt(duration);
		this.monitoring = Integer.parseInt(monitoring);
		this.address = address;
		this.longitude = Double.parseDouble(longitude);
		this.latitude = Double.parseDouble(latitude);
	}

	// TODO: should be in MeetingDao
	private void addParticipant(int meetingId, int userId, PreparedStatement stm) throws SQLException, Exception {
		if (con == null) {
			logger.error("Tried to add participant withou mysql connetion");
		}

		stm = con.prepareStatement(SQL_ADD_PARTICIPANT);
		stm.setInt(1, meetingId);
		stm.setInt(2, userId);

		logger.debug(stm.toString());

		if (stm.executeUpdate() != 1) {
			throw new Exception("Could not add participant meetingId:" + meetingId + " userId:" + userId);
		}
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

	// TODO: should be in MeetingDao
	public Meeting getMeeting(int id) throws Exception {
		con = SqlUtils.getConnection();
		if (con == null) {
			throw new Exception(FAILED_CONNECT_MYSQL);
		}

		Meeting m = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			// create meeting object
			stm = con.prepareStatement(SQL_GET_MEETING);
			stm.setInt(1, id);
			logger.debug(stm.toString());
			rs = stm.executeQuery();
			if (rs.next()) {
				m = new Meeting(rs.getInt(1), rs.getString(3), rs.getString(2), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getString(7), rs.getDouble(8), rs.getDouble(9), new User(rs.getInt(10),
								rs.getString(11), rs.getDouble(12), rs.getDouble(13)));
			} else {
				throw new Exception("Could not find meeting with id:" + id);
			}

			// get meeting participants
			getMeetingParticipants(id, m.getParticipants());

		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
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

		return m;
	}

	// TODO: should be in MeetingDao
	/**
	 * Method returns Meetings, optionally filtered by participation of user identified by userEmail
	 * 
	 * @param userEmail
	 * @param limit_from
	 * @param limit_to
	 * @return
	 * @throws Exception
	 */
	public Meeting[] getMeetings(String userEmail, int limit_from, int limit_to) throws Exception {
		List<Meeting> array = new ArrayList<Meeting>();

		con = SqlUtils.getConnection();
		if (con == null) {
			throw new Exception(FAILED_CONNECT_MYSQL);
		}

		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			if (userEmail != null) {
				User user = User.getUserByEmail(userEmail, false);
				if (user == null) {
					logger.error("Cannot get user meetings, user doesn't exist with email:" + userEmail);
					throw new Exception("User doesn't exist:" + userEmail);
				}
				stm = con.prepareStatement(SQL_GET_MEETINGS_USER);
				stm.setInt(1, user.getId());
				stm.setInt(2, limit_from);
				stm.setInt(3, limit_to);
			} else {
				stm = con.prepareStatement(SQL_GET_MEETINGS);
				stm.setInt(1, limit_from);
				stm.setInt(2, limit_to);
			}
			logger.debug(stm.toString());

			rs = stm.executeQuery();
			while (rs.next()) {
				Meeting m = new Meeting(rs.getInt(1), rs.getString(3), rs.getString(2), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7), rs.getDouble(8), rs.getDouble(9), new User(
								rs.getInt(10), rs.getString(11), rs.getDouble(12), rs.getDouble(13)));
				array.add(m);
				
				// get meeting participants
				getMeetingParticipants(m.getId(), m.getParticipants());
			}
			
			logger.debug("Found "+array.size()+" meetings");

		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
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

		return array.toArray(new Meeting[array.size()]);
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

	/**
	 * Stores Meeting object into database
	 * 
	 * @throws Exception
	 */
	public void persist() throws Exception {

		con = SqlUtils.getConnection();
		if (con == null) {
			throw new Exception(FAILED_CONNECT_MYSQL);
		}

		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			// update owner object with id
			owner = User.getUserByEmail(owner.getEmail(), true);

			// create meeting object
			stm = con.prepareStatement(SQL_INSERT_MEETING, Statement.RETURN_GENERATED_KEYS);
			stm.setString(1, title);
			stm.setString(2, tStarting);
			stm.setInt(3, duration);
			stm.setInt(4, monitoring);
			stm.setString(5, address);
			stm.setDouble(6, longitude);
			stm.setDouble(7, latitude);
			stm.setInt(8, owner.getId());
			logger.debug(stm.toString());

			if (stm.executeUpdate() == 1) {
				rs = stm.getGeneratedKeys();
				if (rs.next()) {
					this.id = rs.getInt(1);
					logger.debug("Meeting persisted, generated id:" + this.id);
				}
			} else {
				throw new Exception("Could not add meeting");
			}

			// add meeting participants
			for (User user : participants) {
				logger.debug("Adding participant:" + user.getEmail() + " to meeting:" + this.id);
				User user_tmp = User.getUserByEmail(user.getEmail(), true);
				addParticipant(this.id, user_tmp.getId(), stm);
			}

		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
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
	public String toString() {
		return "Meeting id:" + this.id + " " + " title:" + title + " owner:" + owner.toString() + " date:" + tStarting
				+ " longitude:" + longitude + " latitude:" + latitude + " address:" + address + " duration:" + duration
				+ " monitoring:" + monitoring;
	}
}

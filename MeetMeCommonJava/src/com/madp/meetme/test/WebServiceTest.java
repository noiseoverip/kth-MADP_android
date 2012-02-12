package com.madp.meetme.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.meetme.webapi.LoggerInterface;
import com.madp.meetme.webapi.WebService;

public class WebServiceTest {	
	
	private LoggerInterface logger = new LoggerJava();
	private final String TAG = "test";	

	@Test
	public void testCreateMeeting() {
		WebService ws = new WebService(logger);
		Meeting meeting = new Meeting("meeting1", "2012-01-01", "2012-01-01", 60, 30, "Kistavage 20", 59.40494,
				17.94974, new User(0, null, "noiseoverip@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip1@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip2@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip3@gmail.com"));
		String response = ws.postMeeting(meeting);
		assertTrue(response != null && response.length() > 0);
	}

	@Test
	public void testGetAllMeetings() {
		WebService ws = new WebService(logger);
		List<Meeting> meetings = ws.getMeetings(0, 100);
		logger.i(TAG, "Received:" + meetings.size() + " meetings");
		assertTrue(meetings.size() > 0);
	}

	@Test
	public void testGetMeeting() {
		WebService ws = new WebService(logger);
		Meeting meeting = ws.getMeeting(30);
		logger.i(TAG, "Received:" + meeting.toString());
		assertTrue(meeting.getId() > 0);
	}

	@Test
	public void testGetAllUserMeetings() {
		WebService ws = new WebService(logger);
		List<Meeting> meetings = ws.getUserMeetings(new User(0, null, "noiseoverip@gmail.com"), 0, 100);
		logger.i(TAG, "Received:" + meetings.size() + " meetings");
		assertTrue(meetings != null && meetings.size() > 0);
	}

	@Test
	public void testUpdateUser() {
		WebService ws = new WebService(logger);
		User user = new User(0, null, "noiseoverip@gmail.com");
		user.setLatitude(2.2);
		user.setLongitude(4.4);
		assertTrue(ws.updateUser(user));
	}
}

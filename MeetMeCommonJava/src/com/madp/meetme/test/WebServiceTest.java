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
		User owner = new User(0, "demo", "demo@gmail.com");
		Meeting meeting = new Meeting("fff", null, "2012-01-19 12:00", 60, 30, "Kistavagen 20", 56.0, 78.0, owner);		
		meeting.getParticipants().add(new User(0, null, "noiseoverip@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip1@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip2@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip3@gmail.com"));	
		meeting.getParticipants().add(owner);	
		String response = ws.postMeeting(meeting);
		assertTrue(response != null && response.startsWith("Meeting id:"));
	}

	@Test
	public void testGetAllMeetings() {
		logger.i(null, "testGetAllMeetings");
		WebService ws = new WebService(logger);
		List<Meeting> meetings = ws.getMeetings(0, 100);
		logger.i(TAG, "Received:" + meetings.size() + " meetings");
		assertTrue(meetings.size() > 0);
	}

	@Test
	public void testGetMeeting() {
		logger.i(null, "testGetMeeting 30");
		WebService ws = new WebService(logger);
		Meeting meeting = ws.getMeeting(30);
		logger.i(TAG, "Received:" + meeting.toString());
		assertTrue(meeting.getId() > 0);
	}
	
	@Test
	public void testCreateAndGetMeeting() {
		//Create meeting
		WebService ws = new WebService(logger);		
		Meeting meeting1 = new Meeting("fff", null, "2012-01-19 12:00", 60, 30, "Kistavagen 20", 54.0, 96.0, new User(0, "demo", "demo@gmail.com"));		
		meeting1.getParticipants().add(new User(0, null, "noiseoverip@gmail.com"));
		meeting1.getParticipants().add(new User(0, null, "noiseoverip1@gmail.com"));
		meeting1.getParticipants().add(new User(0, null, "noiseoverip2@gmail.com"));
		meeting1.getParticipants().add(new User(0, null, "noiseoverip3@gmail.com"));	
		String response = ws.postMeeting(meeting1);		
		
		//Extract meeting id
		int meetingId = Integer.parseInt(response.split(" ")[1].split(":")[1]);
		logger.i(null, "Got meeting id:"+meetingId);
		
		//Set the same meting id for meeting1
		meeting1.setId(meetingId);
		
		//Get meeting for comparing			
		Meeting meeting2 = ws.getMeeting(meetingId);
		logger.i(TAG, "Received:" + meeting2.toString());
		assertEquals(meeting1.getTitle(), meeting2.getTitle());
		assertEquals(meeting1.getDuration(), meeting2.getDuration());
		assertEquals(meeting1.getMonitoring(), meeting2.getMonitoring());
		assertEquals(meeting1.getParticipants().size(), meeting2.getParticipants().size());
		assertEquals(meeting1.getOwner().getEmail(), meeting2.getOwner().getEmail());
		assertTrue(meeting1.getOwner().getLatitude() == meeting2.getOwner().getLatitude());
		assertTrue(meeting1.getOwner().getLongitude() == meeting2.getOwner().getLongitude());
	}

	@Test
	public void testGetAllUserMeetings() {
		logger.i(null, "testGetAllUserMeetings");
		WebService ws = new WebService(logger);
		List<Meeting> meetings = ws.getUserMeetings("demo@gmail.com", 0, 100);
		logger.i(TAG, "Received:" + meetings.size() + " meetings");
		assertTrue(meetings != null && meetings.size() > 0);
	}

	@Test
	public void testUpdateUser() {
		logger.i(null, "testUpdateUser");
		WebService ws = new WebService(logger);
		User user = new User(0, null, "noiseoverip@gmail.com");
		user.setLatitude(2.2);
		user.setLongitude(4.4);
		assertTrue(ws.updateUser(user));
	}
}

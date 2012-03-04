package com.madp.meetme.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.meetme.common.entities.User.Status;
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
		Meeting meeting1 = new Meeting("fff", null, "2012-01-19 12:00", 60, 30, "Kistavagen 20", 54.0, 96.0, new User(0, "demo", "noiseoverip@gmail.com"));		
		meeting1.getParticipants().add(new User(0, null, "saulius@swampyfoot.com"));
		meeting1.getParticipants().add(new User(0, null, "alisauskas.saulius@gmail.com"));		
		String response = ws.postMeeting(meeting1);		
		
		//Extract meeting id
		int meetingId = Integer.parseInt(response.split(" ")[1].split(":")[1]);
		logger.i(null, "Got meeting id:"+meetingId);
		
		//Set the same meting id for meeting1
		meeting1.setId(meetingId);
		
		//Get meeting for comparing			
		Meeting meetingAfter = ws.getMeeting(meetingId);
		logger.i(TAG, "Received:" + meetingAfter.toString());
		assertEquals(meeting1.getTitle(), meetingAfter.getTitle());
		assertEquals(meeting1.getDuration(), meetingAfter.getDuration());
		assertEquals(meeting1.getMonitoring(), meetingAfter.getMonitoring());
		assertEquals(meeting1.getParticipants().size(), meetingAfter.getParticipants().size());
		assertEquals(meeting1.getOwner().getEmail(), meetingAfter.getOwner().getEmail());
		//assertTrue(meeting1.getOwner().getLatitude() == meetingAfter.getOwner().getLatitude());
		//assertTrue(meeting1.getOwner().getLongitude() == meetingAfter.getOwner().getLongitude());
		
		//update user coordinates
		User user = new User(0, null, "saulius@swampyfoot.com");
		user.setLatitude(2.2);
		user.setLongitude(4.4);		
		assertTrue(ws.updateUser(user));
		
		//get meeting info and check user status
		
		//Update user status for this meeting
		user.setCurrentStatus(Status.OK);
		assertTrue(ws.updateUserMeetingStatus(meetingId, user));
		
		//get meeting info and check user status
		meetingAfter = ws.getMeeting(meetingId);
		for (User u : meetingAfter.getParticipants()){
			logger.d(null,"Checking user:"+u);
			if (u.getEmail().equals("saulius@swampyfoot.com")){
				assertEquals(Status.OK, u.getCurrentStatus());
			}
		}
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

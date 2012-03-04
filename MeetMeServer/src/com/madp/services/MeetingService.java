package com.madp.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.madp.entities.Meeting;
import com.madp.utils.EmailUtils;

/**
 * Meeting oject operations
 * 
 * @author esauali 2012-02-20 Initial version
 * @author esauali 2012-02-28 Add attach participants to meeting object, javadoc
 *
 */
@Path("/meeting")
public class MeetingService {

	private static Logger logger = Logger.getLogger(MeetingService.class);

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addMeeting(Meeting meeting) {

		// Persist record in database
		try {
			meeting.persist();
		} catch (Exception e) {
			logger.error("Could not persist meeting:"+meeting.toString(), e);
			return Response.status(Response.Status.BAD_REQUEST).entity(meeting.toString()).build();
		}

		return Response.status(Response.Status.OK).entity(meeting.toString()).build();
	}

	/**
	 * 
	 * @param id - meeting database id or -1 to get all meetings
	 * @param limit_from
	 * @param limit_to
	 * @return
	 */
	@GET
	@Path("all/{limit_from}/{limit_to}")
	@Produces(MediaType.APPLICATION_JSON)
	public Meeting[] getAllMeeting(@PathParam("limit_from") String limit_from_str,
			@PathParam("limit_to") String limit_to_str) {
		Meeting meeting = new Meeting();

		int limit_from = Integer.parseInt(limit_from_str);
		int limit_to = Integer.parseInt(limit_to_str);
		Meeting[] meetings = null;
		try {
			meetings = meeting.getMeetings(null, limit_from, limit_to);
		} catch (Exception e) {
			logger.error("Could get fetch all meetings", e);
		}

		return meetings;
	}

	/**
	 * 
	 * @param id - meeting database id or -1 to get all meetings
	 * @param limit_from
	 * @param limit_to
	 * @return
	 */
	@GET
	@Path("all/{limit_from}/{limit_to}/{userEmail}")
	@Produces(MediaType.APPLICATION_JSON)
	public Meeting[] getAllMeetingUser(@PathParam("limit_from") String limit_from_str,
			@PathParam("limit_to") String limit_to_str, @PathParam("userEmail") String userEmail) {
		Meeting meeting = new Meeting();

		int limit_from = Integer.parseInt(limit_from_str);
		int limit_to = Integer.parseInt(limit_to_str);
		Meeting[] meetings = null;
		try {
			meetings = meeting.getMeetings(userEmail, limit_from, limit_to);
		} catch (Exception e) {
			logger.error("Could get fetch all meetings", e);
		}

		return meetings;
	}

	/**
	 * 
	 * @param id - meeting database id or -1 to get all meetings
	 * @param limit_from
	 * @param limit_to
	 * @return
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Meeting getMeeting(@PathParam("id") int id) {
		Meeting meeting = new Meeting();
		try {
			meeting = meeting.getMeeting(id);
		} catch (Exception e) {
			logger.error("Could get fetch all meetings", e);
		}
		return meeting;
	}
	
	
	/**
	 * A method to test emails
	 * @return
	 */
	@GET
	@Path("message")
	@Produces(MediaType.APPLICATION_JSON)
	public Response message() {
		new EmailUtils().sendEmail("ingblond@gmail.com", new String[] {"noiseoverip@gmail.com", "saulius@swampyfoot.com", "alisauskas.saulius@gmail.com"}, "subject", "some strange message");		
		return Response.status(Response.Status.OK).entity("DONE SOMETHING").build();
	}
}

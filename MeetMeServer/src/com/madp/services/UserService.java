package com.madp.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.madp.entities.User;

@Path("/user")
public class UserService {
	private static Logger logger = Logger.getLogger(UserService.class);

	/**
	 * Update user location
	 * 
	 * @param meeting
	 * @return
	 */
	@PUT
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateLocation(User user) {
		try {
			User user_tmp = User.getUserByEmail(user.getEmail(), false);
			user.setId(user_tmp.getId());
			user.update();
		} catch (Exception e) {
			logger.error("Could not persist meeting", e);
			return Response.status(Response.Status.BAD_REQUEST).entity("error").build();
		}

		return Response.status(Response.Status.OK).build();
	}
}

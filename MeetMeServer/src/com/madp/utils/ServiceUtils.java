package com.madp.utils;

import java.util.List;

import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.sun.jersey.api.representation.Form;

public final class ServiceUtils {
	
	public static final String FAILED_CONNECT_MYSQL = "Failed to connect to mysql";	
	public static final String FAILED_CLOSE_MYSQL = "Could not close database connection";	

	
	public static void logError(Logger logger, Exception e, @Context Form form) {
		if (form != null) {
			StringBuilder params = new StringBuilder("\nParams:");
			for (Form.Entry<String, List<String>> param : form.entrySet()) {
				params.append("\n");
				params.append(param.getKey());
				params.append("=[");
				params.append(param.getValue().get(0));
				params.append("]");
			}
			logger.error(e.getMessage() + params.toString(), e);
		} else {
			logger.error(e.getMessage(), e);
		}
	}
}

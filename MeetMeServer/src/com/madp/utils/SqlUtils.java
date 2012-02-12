package com.madp.utils;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Connection;

public final class SqlUtils {

	private static Logger logger = Logger.getLogger(SqlUtils.class);
	private static final String DBURL = "jdbc:mysql://localhost:3306/meetme?user=user&password=user";
	
	public static Connection getConnection() {
		// initialize SqlUtils
		if (!initialize()) {
			return null;
		}
		// get Connection
		Connection connection = null;
		try {			
			connection = (Connection) DriverManager.getConnection(DBURL);
		} catch (SQLException e) {
			logger.error("Cannot get database connection.", e);
		}		
		logger.debug("Database url:"+DBURL);
		return connection;
	}	
	
	protected static boolean initialize() {		
		logger.info("Initializing sql");
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();			
		} catch (Exception e) {
			logger.error("Failed initializing database driver", e);
			return false;
		}		
		return true;
	}	
	
}

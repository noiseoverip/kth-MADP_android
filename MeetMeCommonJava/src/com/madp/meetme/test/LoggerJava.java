package com.madp.meetme.test;

import org.apache.log4j.Logger;

import com.madp.meetme.webapi.LoggerInterface;

public class LoggerJava implements LoggerInterface {
	private static Logger logger = Logger.getLogger(LoggerJava.class);
	
	@Override
	public void e(String tag, String msg, Throwable tr) {
		logger.error(msg, tr);	
	}

	@Override
	public void e(String tag, String msg) {
		logger.error(msg);		
	}

	@Override
	public void d(String tag, String msg, Throwable tr) {
		logger.debug(msg, tr);	}

	@Override
	public void d(String tag, String msg) {
		logger.debug(msg);		
	}

	@Override
	public void i(String tag, String msg, Throwable tr) {
		logger.info(msg, tr);		
	}

	@Override
	public void i(String tag, String msg) {
		logger.info(msg);		
	}

}

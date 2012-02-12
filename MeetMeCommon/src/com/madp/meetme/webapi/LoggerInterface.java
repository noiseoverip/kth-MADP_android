package com.madp.meetme.webapi;


public interface LoggerInterface {
	
	// Android log methods
	public void e(String tag, String msg, Throwable tr);
	public void e(String tag, String msg);
	public void d(String tag, String msg, Throwable tr);
	public void d(String tag, String msg);
	public void i(String tag, String msg, Throwable tr);
	public void i(String tag, String msg);	
}

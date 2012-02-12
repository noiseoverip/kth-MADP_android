package com.madp.utils;

import android.util.Log;

import com.madp.meetme.webapi.LoggerInterface;

/**
 * This class is required by MeetMeWebApi
 * @author esauali
 *
 */
public class Logger implements LoggerInterface {

	public void d(String tag, String msg) {
		Log.d(tag, msg);
	}	
	
	public void d(String tag, String msg, Throwable tr) {
		Log.d(tag, msg, tr);
	}

	public void e(String tag, String msg) {
		Log.e(tag, msg);
	}

	public void e(String tag, String msg, Throwable tr) {
		Log.e(tag, msg, tr);
	}

	public void i(String tag, String msg) {
		Log.i(tag, msg);
	}

	public void i(String tag, String msg, Throwable tr) {
		Log.i(tag, msg, tr);
	}
}

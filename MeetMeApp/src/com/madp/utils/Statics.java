package com.madp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class for storing default string throughout application
 * @author esauali 2012-02-25
 *
 */
public class Statics {
	public static final String USERNAME = "userName";
	public static final String USEREMAIL = "userEmail";
	
	public static String getUserEmail(Context mContext){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		return prefs.getString(USEREMAIL, "");
	}
	
	public static String getUserName(Context mContext){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		return prefs.getString(USERNAME, "");
	}
}

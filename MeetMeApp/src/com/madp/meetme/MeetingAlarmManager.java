package com.madp.meetme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/*	Class for receiving the broadcast msg sent by the OS and then start the BackgroundMeetingManager.java.
 * 	
 * */
public class MeetingAlarmManager extends BroadcastReceiver{
	
	private int meetingId;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		meetingId = extras.getInt("meetingid");
	
		Intent newIntent = new Intent(context, BackgroundMeetingManager.class);
		
		newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		newIntent.putExtra("meetingid", meetingId);
		/* Start background service */
		context.startService(newIntent);
	}
}

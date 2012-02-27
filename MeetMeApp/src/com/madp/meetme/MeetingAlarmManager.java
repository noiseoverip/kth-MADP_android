package com.madp.meetme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MeetingAlarmManager extends BroadcastReceiver{
	
	private int meetingId;

	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		meetingId = extras.getInt("meetingid");
		
	
		Intent newIntent = new Intent(context, BackgroundMeetingManager.class);
		
		newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		newIntent.putExtra("meetingid", meetingId);
		context.startService(newIntent);
	}
}

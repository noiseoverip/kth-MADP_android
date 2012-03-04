package com.madp.meetme;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.meetme.webapi.WebService;
import com.madp.utils.Logger;
import com.madp.utils.SerializerHelper;
import com.madp.utils.Statics;

/**
 * Activity launched when a link o patter: http:meetme.com/meeting.meeting?id=XX is clicked. User then accepts or
 * declined the meeting invitation Activity send updated to server as well as inform alarm manager
 * 
 * @author esauali 2012-12-25 Initial version (not finished)
 * 
 */
public class InvitationActivity extends Activity {
	private static final String TAG = "InvitationActivity";
	private static final String meetingInfoTmp = "Meeting title:%s\nDate:%s\nParticipants:%d\nOwner:%s\n";
	/*
	 * private TextView meetingInfo; private Button accept; private Button reject; private CheckBox showLocation;
	 */
	private WebService ws;
	private AlarmManager am;
	private long timeLeftToMeetingInMillisec;
	private Meeting meeting;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invitation);

		// Bind views
		//meetingInfo = (TextView) this.findViewById(R.id.meetingInfo);

		String path = getIntent().getData().getEncodedPath();
		String meetingIdStr = getIntent().getData().getQueryParameter("id");
		int meetingId = -1;
		try {
			meetingId = Integer.parseInt(meetingIdStr);
		} catch (NumberFormatException e) {
			Log.e(TAG, "Could not parse meeting id", e);
			Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT);
			return;
		}
		Log.d(TAG, "Meeting id:" + meetingId);

		ws = new WebService(new Logger());

		meeting = ws.getMeeting(meetingId);
		if (meeting == null) {
			Toast.makeText(this, "Error getting meeting info, check you connection", Toast.LENGTH_SHORT);
			return;
		}

		// Set meeting info		
		((TextView) this.findViewById(R.id.meetingInfo)).setText(String.format(
				meetingInfoTmp, 
				meeting.getTitle(), 
				meeting.gettStarting(), 
				meeting.getParticipants().size(), 
				meeting.getOwner().getEmail()));	

		((Button) this.findViewById(R.id.accept)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {		
				// Get "showLocation checkBox value"
				final boolean showLocation = ((CheckBox) ((Activity) v.getContext()).findViewById(R.id.showLocation)).isChecked();				
				Log.d(TAG, "User selected accept, showLocation:"+showLocation);			
				
				// send up date to server
				final User user = new User(Statics.getUserEmail(v.getContext()));
				user.setCurrentStatus(User.Status.OK);				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						ws.updateUserMeetingStatus(meeting.getId(), user);						
					};
				});
				

				// inform alarm manager
				String meetingStartsIn = meeting.gettStarting();
				meetingStartsIn = meetingStartsIn.replaceAll(" ", "-");
				meetingStartsIn = meetingStartsIn.replaceAll(":", "-");
				String[] meetingStart = meetingStartsIn.split("-");

				int year = Integer.parseInt(meetingStart[0]);
				int month = Integer.parseInt(meetingStart[1]);
				int day = Integer.parseInt(meetingStart[2]);
				int hour = Integer.parseInt(meetingStart[3]);
				int min = Integer.parseInt(meetingStart[4]);

				timeLeftToMeetingInMillisec = TimeToMeetingInLong(year, month, day, hour, min);
				setOneTimeAlarm(timeLeftToMeetingInMillisec, meeting.getId());
				
				exit();
			}
		});

		((Button) this.findViewById(R.id.reject)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				Log.d(TAG, "User selected reject");

				// send update to server				
				final User user = new User(Statics.getUserEmail(v.getContext()));
				user.setCurrentStatus(User.Status.NO);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						ws.updateUserMeetingStatus(meeting.getId(), user);						
					};
				});
				
				exit();
			}
		});
		
		((Button) this.findViewById(R.id.showMeetingInfo)).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent a = new Intent(v.getContext(), MeetingInfoActivity.class);
				a.putExtra("meeting", SerializerHelper.serializeObject(meeting));
				startActivity(a);
			}
		});
	}

	/* Turn clock 15 min back */
	private long TimeToMeetingInLong(int newYear, int newMonth, int newDay, int newHour, int newMin) {
		Date d1 = new GregorianCalendar(newYear, newMonth, newDay, newHour, newMin).getTime();
		Date today = new Date();
		System.out.println(d1.getTime() - today.getTime()); // Should be logger
		System.out.println(d1.getTime() + " " + today.getTime()); // Should be logger
		return (d1.getTime() - today.getTime());
	}

	public void setOneTimeAlarm(long timeLeftToMeetingInMillisec, int id) {
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent meetingIntent = new Intent(this, MeetingAlarmManager.class);
		meetingIntent.putExtra("meetingid", id);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, meetingIntent, PendingIntent.FLAG_ONE_SHOT);

		Calendar cal = Calendar.getInstance();
		am.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + timeLeftToMeetingInMillisec), pendingIntent);
	}

	/**
	 * Exit this activity
	 */
	private void exit() {
		this.finish();
	}

}

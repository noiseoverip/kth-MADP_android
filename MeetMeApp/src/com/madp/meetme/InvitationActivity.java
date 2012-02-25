package com.madp.meetme;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.webapi.WebService;
import com.madp.utils.Logger;

/**
 * Activity launched when a link o patter: http:meetme.com/meeting.meeting?id=XX is clicked.
 * User then accepts or declined the meeting invitation
 * Activity send updated to server as well as inform alarm manager
 * @author esauali 2012-12-25 Initial version (not finished)
 *
 */
public class InvitationActivity extends Activity {
	private static final String TAG = "InvitationActivity";
	private static final String meetingInfoTmp = "Meeting title:%s\nDate:%s\nParticipants:%d\nCreator:%s\n";
	/*
	private TextView meetingInfo;
	private Button accept;
	private Button reject;
	private CheckBox showLocation;	
	*/
	private WebService ws;
	
	@Override
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

		Meeting meeting = ws.getMeeting(meetingId);
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
				meeting.getOwner().getName()));	
		
		((Button) this.findViewById(R.id.accept)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {		
				// Get "showLocation checkBox value"
				final boolean showLocation = ((CheckBox) ((Activity) v.getContext()).findViewById(R.id.showLocation)).isChecked();				
				Log.d(TAG, "User selected accept, showLocation:"+showLocation);
				
				// send up date to server
				
				// inform alarm manager
			}
		});
		
		((Button) this.findViewById(R.id.reject)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {				
				Log.d(TAG, "User selected reject");
				
				// send update to server
			}
		});
	}
}

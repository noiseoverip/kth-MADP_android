package com.madp.meetme;

import android.app.Activity;
import android.os.Bundle;

import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.meetme.webapi.WebService;
import com.madp.utils.Logger;

public class MeetingsListActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        WebService ws = new WebService(new Logger());
		Meeting meeting = new Meeting("meeting1", "2012-01-01", "2012-01-01", 60, 30, "Kistavage 20", 59.40494,
				17.94974, new User(0, null, "noiseoverip@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip1@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip2@gmail.com"));
		meeting.getParticipants().add(new User(0, null, "noiseoverip3@gmail.com"));
		ws.postMeeting(meeting);
    }
}
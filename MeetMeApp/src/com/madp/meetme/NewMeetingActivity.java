package com.madp.meetme;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.madp.meetme.common.entities.LatLonPoint;
import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.meetme.webapi.WebService;
import com.madp.utils.Logger;
import com.madp.utils.ParticipantsAdapter;
import com.madp.utils.SerializerHelper;

//TODO: we should probably have one activity for creating new meeting, updating it and displaying it's info
/**
 * Activity for creating new Meeting
 * 
 * @author Niklas and Peter initial version
 * @author Saulius 2012-02-19 integrated WebService, added TODO elements, remove obsolete stuff
 * 
 */
public class NewMeetingActivity extends ListActivity {
	/** Called when the activity is first created. */

	EditText nameOfMeeting, nameOfPlace;
	String newMeetingName, newMeetingPlace, newMeetingTime, newMeetingDate, smin;
	ImageButton createMeeting, meetingTime, addParticipants, meetingDate;
	TextView infolabel;
	private ParticipantsAdapter p_adapter;
	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;
	private int hour, min, year, month, day;
	private static final int CONTACT_PICKER_RESULT = 12345;
	private ListView lv;
	private Meeting meeting;
	private WebService ws;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newmeeting);
		
		ws = new WebService(new Logger());
		meeting = new Meeting();		
		nameOfMeeting = (EditText) findViewById(R.id.meetingName);
		nameOfPlace = (EditText) findViewById(R.id.meetingPlace);
		createMeeting = (ImageButton) findViewById(R.id.createMeeting);
		meetingTime = (ImageButton) findViewById(R.id.meetingTime);
		meetingDate = (ImageButton) findViewById(R.id.meetingDate);
		addParticipants = (ImageButton) findViewById(R.id.addParticipants);
		infolabel = (TextView) findViewById(R.id.infolabel);

		final Calendar c = Calendar.getInstance();

		hour = c.get(Calendar.HOUR_OF_DAY);
		min = c.get(Calendar.MINUTE);
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		this.p_adapter = new ParticipantsAdapter(this, R.layout.meetingrow, meeting.getParticipants());
		this.getListView().setAdapter(p_adapter);
		infolabel.setText("New Meeting");

		/* Add participants */
		addParticipants.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				@SuppressWarnings("deprecation")
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, CONTACT_PICKER_RESULT);
			}
		});

		/* If all data is inserted create meeting ! */
		createMeeting.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				/**
				 * Currently you must send all the fields
				 */
				meeting.setTitle(nameOfMeeting.getText().toString());
				meeting.setAddress(nameOfPlace.getText().toString());
				meeting.setDuration(60); //TODO: to be implemented
				meeting.setMonitoring(20); //TODO: to be implemented				
				meeting.settStarting(year + "-" + month + "-" + day + " " + hour + ":" + min);
				meeting.setOwner(new User(0, "", "demo@gmail.com"));
				
				Intent intent = new Intent(view.getContext(), MeetingsListActivity.class);
				Bundle s = new Bundle();
				s.putByteArray("meeting", SerializerHelper.serializeObject(meeting));
				intent.putExtras(s);
				setResult(RESULT_OK, intent);
				ws.postMeeting(meeting);
				finish();
			}
		});

		// Buttonlisteners for the date and time buttons
		meetingTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});

		meetingDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
	}

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			hour = hourOfDay;

			if (minute < 10) {
				smin = ("0" + Integer.toString(minute));
			} else {
				smin = Integer.toString(minute);
			}
		}
	};
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int thisYear, int monthOfYear, int dayOfMonth) {
			year = thisYear;
			month = monthOfYear;
			day = dayOfMonth;

		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, hour, min, true);
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, year, month, day);
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == CONTACT_PICKER_RESULT) {
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					// TODO: should be email here
					String contactEmail = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					meeting.getParticipants().add(new User(0, "", contactEmail));
					p_adapter.notifyDataSetChanged();
				}
			}
		}
	}
}
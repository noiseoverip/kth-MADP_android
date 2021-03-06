package com.madp.meetme;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.common.entities.User;
import com.madp.meetme.webapi.WebService;
import com.madp.utils.Logger;
import com.madp.utils.ParticipantsAdapter;
import com.madp.utils.SerializerHelper;
import com.madp.utils.Statics;

//TODO: we should probably have one activity for creating new meeting, updating it and displaying it's info
/**
 * Activity for creating new Meeting
 * 
 * @author Niklas and Peter initial version
 * @author Saulius 2012-02-19 integrated WebService, added TODO elements, remove obsolete stuff
 * @author Saulius 2012-02-28 fixed parsing of server response to get meeting id
 * 
 */
public class NewMeetingActivity extends ListActivity {

	private static final int REQUEST_CODE_POINTONMAP = 100;

	class LatLonPoint {
		/* a convinient class to use when using coordinates and maps */
		private static final long serialVersionUID = -4119420152413060192L;
		int lat;
		int lon;

		public LatLonPoint(double latitude, double longitude) {
			lat = (int) (latitude * 1E6);
			lon = (int) (longitude * 1E6);
			Log.i(null, "point at lat " + latitude + " lon " + longitude);
		}

		public int getILongitude() {
			return lon;
		}

		public int getILatitude() {
			return lat;
		}

		public double getDLongitude() {
			return lon / 1E6;
		}

		public double getDLatitude() {
			return lat / 1E6;
		}
	}

	public LatLonPoint getLatLong(JSONObject jsonObject) {
		/* extract a LatLonPoint from JSON object received from google server */
		Double lon = new Double(0);
		Double lat = new Double(0);

		try {

			lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lng");

			lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location").getDouble("lat");

		} catch (Exception e) {
			e.printStackTrace();

		}

		Log.i("coordinates::", "Lat = " + lat + " Lon = " + lon);
		return new LatLonPoint(lat, lon);
	}

	public static JSONObject getLocationInfo(String address) {
		/* pull from google server, longitude and latitude of a String address */
		Log.i("address info", "address = " + address);

		StringBuilder stringBuilder = new StringBuilder();
		try {

			address = address.replaceAll(" ", "%20");

			HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address
					+ "&sensor=false");
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			stringBuilder = new StringBuilder();
			response = client.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonObject;
	}

	private static final String TAG = "NewMeetingActivity";
	private EditText nameOfMeeting, nameOfPlace;
	private ImageButton createMeeting, meetingTime, addParticipants, meetingDate, map;
	private TextView infolabel, currentDateSet, currentTimeSet;
	private ParticipantsAdapter p_adapter;
	private static final int TIME_DIALOG_ID = 0;
	private static final int DATE_DIALOG_ID = 1;
	private int hour, min, year, month, day;
	private long timeLeftToMeetingInMillisec;
	private static final int CONTACT_PICKER_RESULT = 12345;
	private Meeting meeting;
	private WebService ws;
	private AlarmManager am;

	@Override
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
		currentDateSet = (TextView) findViewById(R.id.currentDateSet);
		currentTimeSet = (TextView) findViewById(R.id.currentTimeSet);
		map = (ImageButton) findViewById(R.id.setlocationonmap);

		final Calendar c = Calendar.getInstance();

		hour = c.get(Calendar.HOUR_OF_DAY);
		min = c.get(Calendar.MINUTE);
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		this.p_adapter = new ParticipantsAdapter(this, R.layout.meetingrow, meeting.getParticipants());
		this.getListView().setAdapter(p_adapter);
		infolabel.setText("New Meeting");
		
		map.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), GPSPlaceAMeeting.class);	
				meeting.setAddress(nameOfPlace.getText().toString());
				LatLonPoint coordinates = getLatLong(getLocationInfo(nameOfPlace.getText().toString()));
				Log.i("Click CreateMeeting NewMeeting.java", "lat = "+coordinates.getDLatitude());
				meeting.setLatitude(coordinates.getDLatitude());
				meeting.setLongitude(coordinates.getDLongitude());

				Bundle s = new Bundle();
				s.putByteArray("meeting", SerializerHelper.serializeObject(meeting));
				intent.putExtras(s);
				startActivityForResult(intent, REQUEST_CODE_POINTONMAP);				
			}
			
		});
		
		/* Add participants */
		addParticipants.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, CONTACT_PICKER_RESULT);
			}
		});

		/* If all data is inserted create meeting ! */
		createMeeting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				final ProgressDialog dialog = ProgressDialog.show(view.getContext(), "", "Saving meeting... ", true);
				
				/**
				 * Currently you must send all the fields
				 */
				meeting.setTitle(nameOfMeeting.getText().toString());
				meeting.setAddress(nameOfPlace.getText().toString());
				meeting.setDuration(60); // TODO: to be implemented
				meeting.setMonitoring(20); // TODO: to be implemented
				meeting.settStarting(year + "-" + month + "-" + day + " " + hour + ":" + min);				

				// Set owner
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				User owner = new User(0, prefs.getString(Statics.USERNAME, null), prefs.getString(Statics.USEREMAIL,
						null));
				meeting.setOwner(owner);

				// Add owner to participants list
				meeting.getParticipants().add(owner);
				
				
				Intent intent = new Intent(view.getContext(), MeetingsListActivity.class);
				Bundle s = new Bundle();
				s.putByteArray("meeting", SerializerHelper.serializeObject(meeting));
				intent.putExtras(s);
				setResult(RESULT_OK, intent);
				
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						/* Post the meeting to server, receive generated id*/
						String response = ws.postMeeting(meeting);
						int id = -1;
						try {
							id = Integer.parseInt(response.split(" ")[1].split(":")[1]);
						} catch (Exception e) {
							Log.e(TAG, "Could not parse meeting id from:" + response);							
						}
						Log.d(TAG, "Parsed meeting id:" + id);
						
						// Set alarm 
						timeLeftToMeetingInMillisec = TimeToMeetingInLong(year, month, day, hour, min);
						setOneTimeAlarm(timeLeftToMeetingInMillisec, id);
						
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								dialog.dismiss();
								finish();
								
							}
						});
					}
				}).start();	
				
			}
		});

		// Buttonlisteners for the date and time buttons
		meetingTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});

		meetingDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
	}

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			hour = hourOfDay;
			min = minute;
			/* LAYOUTPROBLEM? */
			currentTimeSet.setText(hour + ":" + min);
		}
	};
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int thisYear, int monthOfYear, int dayOfMonth) {
			year = thisYear;
			month = monthOfYear;
			day = dayOfMonth;

			currentDateSet.setText(day + "/" + month + "-" + year);

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

	/* Turn clock 15 min back or smth */
	private long TimeToMeetingInLong(int newYear, int newMonth, int newDay, int newHour, int newMin) {

		Date d1 = new GregorianCalendar(newYear, newMonth, newDay, newHour, newMin).getTime();
		Date today = new Date();
		System.out.println(d1.getTime() - today.getTime());
		System.out.println(d1.getTime() + " " + today.getTime());
		return (d1.getTime() - today.getTime());
	}

	public void setOneTimeAlarm(long timeLeftToMeetingInMillisec, int id) {
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent meetingIntent = new Intent(this, MeetingAlarmManager.class);
		meetingIntent.putExtra("meetingid", id);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, meetingIntent, PendingIntent.FLAG_ONE_SHOT);

		Calendar cal = Calendar.getInstance();
		am.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + timeLeftToMeetingInMillisec)-900000, pendingIntent);//Start 15min before meeting
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "OnActivityResult r:"+requestCode+" res"+resultCode);
		if (resultCode == RESULT_OK) {
			if (requestCode == CONTACT_PICKER_RESULT) {
				final User user = new User(-1, null, null);
				List<String> emails = new ArrayList<String>();
				Uri contactData = data.getData();
				Cursor userCursor = managedQuery(contactData, null, null, null, null);
				if (userCursor.moveToFirst()) {
					// Get contact id
					String contactId = userCursor.getString(userCursor.getColumnIndex(BaseColumns._ID));
					// Get user name
					user.setName(userCursor.getString(userCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
					// Get contact emails
					Cursor emailsCursor = getContentResolver().query(
							ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
					while (emailsCursor.moveToNext()) {
						String e = emailsCursor.getString(emailsCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						if (e.length() > 0) {
							emails.add(e);
						}
					}
					emailsCursor.close();
					userCursor.close();
					Log.d(TAG, "Emails found:" + emails);
				}

				// Set email
				if (emails.size() == 1) { // only one email
					Log.d(TAG, "One email found");
					user.setEmail(emails.get(0));
				} else if (emails.size() > 1) { // multiple email per contact
					Log.d(TAG, "More then one email found");
					final CharSequence[] items = emails.toArray(new CharSequence[emails.size()]);
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Choose email to use");
					builder.setItems(items, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int item) {
							user.setEmail((String) items[item]);
							p_adapter.notifyDataSetChanged();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				} else { // no emails set for the contact
					Log.d(TAG, "No emails found");
					Toast.makeText(this, "This contact has no emails set", Toast.LENGTH_SHORT).show();
					return;
				}
				meeting.getParticipants().add(user);
				p_adapter.notifyDataSetChanged();
			}
			else if (requestCode == REQUEST_CODE_POINTONMAP) {
				Log.d(TAG, "Returned from GPSPlaceAMetting");
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					int longitude = bundle.getInt("longitude");
					int latitude = bundle.getInt("latitude");
					Log.d(TAG, "Received coordinates from gps activity: " + latitude + "::" + longitude);
					meeting.setLongitude((double)longitude/1000000);
					meeting.setLatitude((double)latitude/1000000);
				} else {
					Log.e(TAG, "GSP activity didn't return any coordinates");
				}
			}
		} 
	}
}

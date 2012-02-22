package com.madp.meetme;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import com.madp.meetme.common.entities.Meeting;
import com.madp.meetme.webapi.WebService;
import com.madp.utils.Logger;
import com.madp.utils.MeetingsListAdapter;
import com.madp.utils.SerializerHelper;

//TODO: add logs for debugging
//TODO: refreshList() should be done on a separate thread and should display progress
//TODO: implement refresh meeting list button
//TODO: Delete meetings should take place in MeetingInfo activity
/**
 * Display meetings where user is participant or creator. *
 * 
 * @author Niklas and Peter initial version without server integration
 * @author Saulius 2012-02-19 integrated WebService and removed some obsolete stuff
 * 
 */
public class MeetingsListActivity extends ListActivity {	
	private final String TAG = "MeetingListActivity";
	private List<Meeting> meetings  = new ArrayList();;
	private MeetingsListAdapter listAdapter;
	private ImageButton newMeetingButton, exitButton;
	private static final int CREATE_NEW_MEETING_RESULT = 891030;
	private static final int CLICK_ON_MEETING_LIST_ELEMENT_RESULT = 860604;
	private WebService ws;
	
	
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meetinglist);

		
		ws = new WebService(new Logger());
		
		newMeetingButton = (ImageButton) findViewById(R.id.new_meeting_button);
		exitButton = (ImageButton) findViewById(R.id.logout);

		
		if(meetings == null)
			refreshList();
		if (meetings != null) {
			listAdapter = new MeetingsListAdapter(this, R.layout.meetingrow, meetings);
			setListAdapter(listAdapter);
		}

		/* Implement listers for buttons and a Item in the list */
		exitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		/* Click on a listitem */
		this.getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(arg1.getContext(), MeetingInfoActivity.class);
				Bundle b=new Bundle();
				b.putByteArray("meeting", SerializerHelper.serializeObject(meetings.get(position)));				
				startActivityForResult(intent, CLICK_ON_MEETING_LIST_ELEMENT_RESULT);
			}
		});

		/* Create a new Meeting by going to the Activity */
		newMeetingButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), NewMeetingActivity.class);
				startActivityForResult(intent, CREATE_NEW_MEETING_RESULT);
			}
		});
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "Result code received:"+resultCode);
		// TODO: should be two situations: new meeting was created or not, update only if created
		refreshList();
		if (requestCode == CREATE_NEW_MEETING_RESULT) {
			if (resultCode == RESULT_OK) { 
				Bundle b = data.getExtras();
				if(b != null){
					Meeting s = (Meeting) SerializerHelper.deserializeObject(b.getByteArray("meeting"));
					meetings.add(s);
					listAdapter = new MeetingsListAdapter(this, R.layout.meetingrow, meetings);
					setListAdapter(listAdapter);
					listAdapter.setItems(meetings);
					listAdapter.notifyDataSetChanged();
					

				}
			}
		} else if (requestCode == CLICK_ON_MEETING_LIST_ELEMENT_RESULT) {
			// Meeting deletion and update should be performed in MeetingInfo
		}
	}

	/**
	 * Fetch meetings from server
	 */
	
	
	private void refreshList() {
		List<Meeting> meetingsTmp = ws.getMeetings(0, 100);
		if (meetingsTmp != null) {
			meetings = meetingsTmp;
			if (listAdapter != null) {
				listAdapter.setItems(meetings);
				listAdapter.notifyDataSetChanged();
			}
		}
		
	}
}
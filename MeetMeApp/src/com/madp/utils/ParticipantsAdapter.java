package com.madp.utils;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;


import com.madp.meetme.common.entities.User;
import com.madp.meetme.common.*;
public class ParticipantsAdapter extends ArrayAdapter<User>{
	private final String TAG = "participantAdapter";
	private List<User> participants;
	Context mcontext;
	ImageButton deleteButton;	

	public ParticipantsAdapter(Context context, int textViewResourceId, List<User> participants) {
		super(context, textViewResourceId, participants);
		this.mcontext = context;
		this.participants = participants;
	}	

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		Log.d(TAG,"Position i getView " + position);
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.participantrow, null);
		}
		String participantName = participants.get(position).getEmail();
		if (participantName != null) {
			TextView tf = (TextView) v.findViewById(R.id.username);
			tf.setText(participantName);                
			deleteButton = (ImageButton) v.findViewById(R.id.deleteparticipant);
			deleteButton.setFocusableInTouchMode(false);
			deleteButton.setFocusable(false);
			deleteButton.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					participants.remove(position);
					notifyDataSetChanged();
				}
			});	
		}
		return v;
	}

}

package com.madp.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.madp.meetme.R;
import com.madp.meetme.common.entities.Meeting;


/* Code built on http://www.softwarepassion.com/android-series-custom-listview-items-and-adapters/ */
public class MeetingsListAdapter extends ArrayAdapter<Meeting> {
	private List<Meeting> items;
	private Context mcontext;

    public MeetingsListAdapter(Context context, int textViewResourceId, List<Meeting> items) {
            super(context, textViewResourceId, items);
            this.mcontext = context;
            this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
            	LayoutInflater vi = (LayoutInflater)mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.meetingrow, null);
            }
            Meeting meeting = items.get(position);
            
            if (meeting != null) {
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView mt = (TextView)v.findViewById(R.id.middletext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    if (tt != null) {
                          tt.setText(meeting.getTitle());
                    }
                    if(mt != null){
                    	mt.setText("Date: " + meeting.gettStarting());
                    }
                    if(bt != null){
                          bt.setText("Location: " + meeting.getAddress());
                    }
            }
            return v;
    }
    
    public void setItems(List<Meeting> items){
    	this.items = items;
    }
}

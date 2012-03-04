package com.madp.meetme;


import android.app.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/*	Class for just testing the background service
 * 
 * */

public class MeetMeBackgroundActivity extends Activity implements OnClickListener   { 
  private static final String TAG = "ServicesDemo";
  Button buttonStart, buttonStop;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.backgroundservicecontroller);

  
    buttonStart = (Button) findViewById(R.id.start);
    buttonStop = (Button) findViewById(R.id.stop);
    
    buttonStart.setOnClickListener(this);
    buttonStop.setOnClickListener(this);
  }

  @Override
public void onClick(View src) {
    switch (src.getId()) {
    case R.id.start:
      //Log.d(TAG, "onClick: starting srvice");
      startService(new Intent(this, BackgroundMeetingManager.class));
      System.out.println("I start");
      break;
    case R.id.stop:
      Log.d(TAG, "onClick: stopping srvice");
      stopService(new Intent(this, BackgroundMeetingManager.class));
      break;
    }
  }
}
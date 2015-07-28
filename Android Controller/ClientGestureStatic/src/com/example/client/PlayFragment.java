package com.example.client;

import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

public class PlayFragment extends Fragment {

	private View v = null;
	private Button buttonHold = null;
	private String filename = null;
	BaseActivity baseActivity = null;
	Date sDate = null;
	Date nDate = null;
	long oTime, nTime;
	long oSeconds, nSeconds;
	boolean timeSet = true;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		baseActivity = (BaseActivity) activity;
		Log.d("In Attach", "Balu");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("In on create bundle", " Picture Preview Created ");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		v = inflater.inflate(R.layout.play_game, container, false);
		buttonHold = (Button) v.findViewById(R.id.holdButton);
		buttonHold.setOnTouchListener(buttonPressListener);
		buttonHold.setOnClickListener(buttonPressClickListener);
		return v;

	}

	public OnTouchListener buttonPressListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (timeSet) {
				baseActivity.sensorManager.registerListener(
						baseActivity.sListener, baseActivity.sensor,
						SensorManager.SENSOR_DELAY_NORMAL);
				sDate = new Date();
				oTime = sDate.getTime();
				oSeconds = oTime ;
				Log.d("PlayFragment", "SDATE : " + sDate);
				Log.d("PlayFragment", "oSeconds : " + oSeconds);
				timeSet = false;
			}
			// Log.d("PlayFragment", "In Button Touch Event");

			return false;
		}
	};
	public OnClickListener buttonPressClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			Log.d("PlayFragment", "IN Button Click Event");
			baseActivity.sensorManager.unregisterListener(
					baseActivity.sListener, baseActivity.sensor);
			try {
				Log.d("PlayFragment",
						"IN Button Click Event and callin gesture function");
				nDate = new Date();
				Log.d("PlayFragment", "NDATE : " + nDate);
				nTime = nDate.getTime();
				nSeconds = nTime ;
				Log.d("PlayFragment", "nSeconds : " + nSeconds);
				long tDiff = nSeconds - oSeconds;
				Log.d("PlayFragment", "Time Difference : " + tDiff);
				baseActivity.checkGesture(tDiff);
				nSeconds = 0;
				oSeconds = 0;
				timeSet = true;
				Log.d("PlayFragment", "After Button Click Event");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

}

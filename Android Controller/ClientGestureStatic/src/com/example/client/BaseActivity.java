package com.example.client;

import android.app.Activity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class BaseActivity extends Activity implements SensorEventListener {

	TextView textResponse, gestureText;
	Button buttonConnect, buttonPress, buttonClear, buttonSubmit,
			buttonGesture;
	List<Float> xPoints;
	List<Float> yPoints;
	List<Float> zPoints;
	Sensor sensor = null;
	SensorManager sensorManager = null;
	SensorEventListener sListener;
	static int checkUpdate = 0;
	static String resultGesture = null;
	MyClientTask connectServer = null;
	MainActivity mainActivity = null;
	SelectGesture selectGesture = null;
	PlayFragment playFragment = null;
	float x1, y1, z1;
	int check = 0;
	float speed = 0;
	float[] gravity = new float[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mainActivity = new MainActivity();
		playFragment = new PlayFragment();
		selectGesture = new SelectGesture();
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.linearFrag, mainActivity).commit();
		}
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sListener = this;
		sensorManager.unregisterListener(sListener, sensor);
		xPoints = new ArrayList<Float>();
		yPoints = new ArrayList<Float>();
		zPoints = new ArrayList<Float>();
	}

	public void playGameSubmit(View v) {
		// TODO Auto-generated method stub
		getFragmentManager().beginTransaction()
				.replace(R.id.linearFrag, playFragment).commit();
	}

	public void goToGestures(View v) {
		// TODO Auto-generated method stub
		getFragmentManager().beginTransaction()
				.replace(R.id.linearFrag, selectGesture).commit();
	}

	public void clearClick(View v) {
		// TODO Auto-generated method stub
		if (connectServer != null) {
			connectServer = null;
			textResponse.setText("Connection closed");
		}
	}

	public void connectToServer(View arg0) {

		if (mainActivity.editTextAddress != null) {
			Log.d("Server IP", mainActivity.editTextAddress.getText()
					.toString());
			connectServer = new MyClientTask(mainActivity.editTextAddress
					.getText().toString(), 8080);
			Log.d("Server IP", mainActivity.editTextAddress.getText()
					.toString());
			connectServer.execute();
		} else {
			Log.d("Server IP", "NULL");
		}
	}

	public class MyClientTask extends AsyncTask<Void, Void, Void> {

		String dstAddress;
		int dstPort;
		String response = "";
		MainActivity mainActivity = new MainActivity();

		MyClientTask(String addr, int port) {
			dstAddress = addr;
			dstPort = port;
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			OutputStream outputStream;
			Socket socket = null;
			try {
				socket = new Socket(dstAddress, dstPort);
				Log.d("MyClient Task", "Destination Address : " + dstAddress);
				Log.d("MyClient Task", "Destination Port : " + dstPort + "");
				outputStream = socket.getOutputStream();
				PrintStream printStream = new PrintStream(outputStream);
				while (true) {
					if (BaseActivity.checkUpdate == 1) {
						Log.d("Gesture Sent", BaseActivity.resultGesture);
						System.out.println("Gesture Sent : "
								+ BaseActivity.resultGesture);
						printStream.print(BaseActivity.resultGesture);
						printStream.flush();
						BaseActivity.checkUpdate = 0;
						BaseActivity.resultGesture = null;
					}
				}

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response = "UnknownHostException: " + e.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response = "IOException: " + e.toString();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// textResponse.setText(response);
			super.onPostExecute(result);
		}

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor == sensor) {
			final float alpha = (float) 0.8;

			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
			xPoints.add(event.values[0] - gravity[0]);
			yPoints.add(event.values[1] - gravity[1]);
			zPoints.add(event.values[2] - gravity[2]);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void checkGesture(long time) throws Exception {
		gestureText = (TextView) findViewById(R.id.gestureText);
		ArrayList<String> dataPoints = new ArrayList<String>();
		resultGesture = getGesture(time, xPoints, yPoints, zPoints);
		// checkUpdate = 1;
		Log.d("Result Gesture", resultGesture);
		gestureText.setText(resultGesture + " with speed : " + speed + " m/msec");
		xPoints = new ArrayList<Float>();
		yPoints = new ArrayList<Float>();
		zPoints = new ArrayList<Float>();
	}

	public String getGesture(long time, List<Float> x, List<Float> y, List<Float> z) {
		String result = "others";
		float[][] change = new float[3][x.size() - 1];
		int i = 0;
		while (i < x.size() - 1) {
			change[0][i] = getAbs(x.get(i + 1)) - getAbs(x.get(i));
			change[1][i] = getAbs(y.get(i + 1)) - getAbs(y.get(i));
			change[2][i] = getAbs(z.get(i + 1)) - getAbs(z.get(i));
			i++;
		}
		float xAvg, yAvg, zAvg;
		speed=0;
		xAvg = getRange(x);
		yAvg = getRange(y);
		zAvg = getRange(z);
		int[] count = new int[3];
		i = 0;
		count[0] = 0;
		count[1] = 0;
		count[2] = 0;
		while (i < x.size() - 1) {
			if (change[0][i] > change[1][i] && change[0][i] > change[2][i]) {
				count[0]++;
			} else if (change[1][i] > change[0][i]
					&& change[1][i] > change[2][i]) {
				count[1]++;
			} else if (change[2][i] > change[0][i]
					&& change[2][i] > change[1][i]) {
				count[2]++;
			} else {
				// DO Nothing
			}
			i++;
		}
		Log.d("BaseActivity", "Right/Left : " + (getAbs(xAvg) + count[0]));
		Log.d("BaseActivity", "Up/Down : " + (getAbs(yAvg) + count[1]));
		Log.d("BaseActivity", "Push/Pull : " + (getAbs(zAvg) + count[2]));
		if ((getAbs(xAvg) + count[0]) > (getAbs(yAvg) + count[1])
				&& (getAbs(xAvg) + count[0]) > (getAbs(zAvg) + count[2])) {
			
			speed = getAbs(xAvg)/time;
			
			if (xAvg < 0) {
				result = "right";
			} else {
				result = "left";
			}

		} else if ((getAbs(yAvg) + count[1]) > (getAbs(xAvg) + count[0])
				&& (getAbs(yAvg) + count[1]) > (getAbs(zAvg) + count[2])) {
			speed = getAbs(yAvg)/time;
			if (zAvg < 0) {
				result = "up";
			} else {
				result = "down";
			}

		} else if ((getAbs(zAvg) + count[2]) > (getAbs(xAvg) + count[0])
				&& (getAbs(zAvg) + count[2]) > (getAbs(yAvg) + count[1])) {
			speed = getAbs(zAvg)/time;
			if (zAvg < 0) {
				result = "pull";
			} else {
				result = "push";
			}

		} else {
			result = "others";
		}
		
		
		return result;
	}

	public float getRange(List<Float> temp) {
		return (temp.get(temp.size() - 1) - temp.get(0));
	}

	public float getAbs(float temp) {
		if (temp < 0) {
			return temp * (-1);
		} else {
			return temp;
		}
	}
}

package com.client.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.client.activity.utils.AutoRegression;
import com.client.activity.utils.Autocorrelation;
import com.client.activity.utils.FastFourierTransform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends Activity implements SensorEventListener {

	private Button buttonStart, buttonStop = null;
	TextView result = null;
	List<Float> xAPoints;
	List<Float> yAPoints;
	List<Float> zAPoints;
	List<Float> xGPoints;
	List<Float> yGPoints;
	List<Float> zGPoints;
	List<Float> xGYPoints;
	List<Float> yGYPoints;
	List<Float> zGYPoints;
	List<Float> xfAPoints;
	List<Float> yfAPoints;
	List<Float> zfAPoints;
	List<Float> xfGYPoints;
	List<Float> yfGYPoints;
	List<Float> zfGYPoints;
	List<Float> xfGPoints;
	List<Float> yfGPoints;
	List<Float> zfGPoints;
	Date startTime, endTime;
	Sensor sensorA, sensorG = null;
	SensorManager sensorManager = null;
	SensorEventListener sListener;
	float[] gravity = new float[3];
	final float alpha = (float) 0.3;
	public static final float EPSILON = 0.000000001f;
	private static final float NS2S = 1.0f / 1000000000.0f;
	private final float[] deltaRotationVector = new float[4];
	private float timestamp;
	private Spinner spinner = null;
	private Context context = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_game);
		result = (TextView) findViewById(R.id.result);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorA = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorG = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sListener = this;
		context = this;
		sensorManager.unregisterListener(sListener, sensorA);
		sensorManager.unregisterListener(sListener, sensorG);
		buttonStart = (Button) findViewById(R.id.start);
		buttonStart.setOnClickListener(buttonStartClickListener);
		buttonStop = (Button) findViewById(R.id.stop);
		buttonStop.setOnClickListener(buttonStopClickListener);
		spinner = (Spinner) findViewById(R.id.spinner1);

		xAPoints = new ArrayList<Float>();
		yAPoints = new ArrayList<Float>();
		zAPoints = new ArrayList<Float>();
		xGPoints = new ArrayList<Float>();
		yGPoints = new ArrayList<Float>();
		zGPoints = new ArrayList<Float>();
		xGYPoints = new ArrayList<Float>();
		yGYPoints = new ArrayList<Float>();
		zGYPoints = new ArrayList<Float>();

		xfAPoints = new ArrayList<Float>();
		yfAPoints = new ArrayList<Float>();
		zfAPoints = new ArrayList<Float>();
		xfGPoints = new ArrayList<Float>();
		yfGPoints = new ArrayList<Float>();
		zfGPoints = new ArrayList<Float>();
		xfGYPoints = new ArrayList<Float>();
		yfGYPoints = new ArrayList<Float>();
		zfGYPoints = new ArrayList<Float>();
	}

	public OnClickListener buttonStartClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			sensorManager.registerListener(sListener, sensorA,
					SensorManager.SENSOR_DELAY_FASTEST);
			sensorManager.registerListener(sListener, sensorG,
					SensorManager.SENSOR_DELAY_FASTEST);
			startTime = new Date();
			try {
				result.setText("Activity Tracking Started");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	public OnClickListener buttonStopClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			stopActivity();
		}
	};

	public void updateTracking() {
		int i = 0, j = 0;
		sensorManager.unregisterListener(sListener, sensorA);
		sensorManager.unregisterListener(sListener, sensorG);

		try {
			result.setText("Activity Tracking Stopped");
			float[][] temp;
			temp = fft(xAPoints, yAPoints, zAPoints);
			xfAPoints = new ArrayList<Float>();
			yfAPoints = new ArrayList<Float>();
			zfAPoints = new ArrayList<Float>();
			for (i = 0; i < temp.length; i++) {
				for (j = 0; j < temp[0].length; j++) {
					if (i == 0) {
						xfAPoints.add(temp[i][j]);
					} else if (i == 1) {
						yfAPoints.add(temp[i][j]);
					} else if (i == 2) {
						zfAPoints.add(temp[i][j]);
					} else {
						// Do Nothing
					}
				}
			}

			temp = fft(xGPoints, yGPoints, zGPoints);
			xfGPoints = new ArrayList<Float>();
			yfGPoints = new ArrayList<Float>();
			zfGPoints = new ArrayList<Float>();
			for (i = 0; i < temp.length; i++) {
				for (j = 0; j < temp[0].length; j++) {
					if (i == 0) {
						xfGPoints.add(temp[i][j]);
					} else if (i == 1) {
						yfGPoints.add(temp[i][j]);
					} else if (i == 2) {
						zfGPoints.add(temp[i][j]);
					} else {
						// Do Nothing
					}
				}
			}

			temp = fft(xGYPoints, yGYPoints, zGYPoints);
			xfGYPoints = new ArrayList<Float>();
			yfGYPoints = new ArrayList<Float>();
			zfGYPoints = new ArrayList<Float>();
			for (i = 0; i < temp.length; i++) {
				for (j = 0; j < temp[0].length; j++) {
					if (i == 0) {
						xfGYPoints.add(temp[i][j]);
					} else if (i == 1) {
						yfGYPoints.add(temp[i][j]);
					} else if (i == 2) {
						zfGYPoints.add(temp[i][j]);
					} else {
						// Do Nothing
					}
				}
			}
			String label = "";
			if (spinner.getSelectedItem() != null) {
				label = spinner.getSelectedItemPosition() + "";
			} else {
				label = "9";
			}
			CollectData data = new CollectData(context, label);
			data.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("Exparam", e.toString());
			e.printStackTrace();
		}
	}

	private void stopActivity() {
		sensorManager.unregisterListener(sListener, sensorA);
		sensorManager.unregisterListener(sListener, sensorG);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		endTime = new Date();
		if ((endTime.getTime() - startTime.getTime()) / 1000 > 2
				&& (endTime.getTime() - startTime.getTime()) / 1000 < 300) {
			updateTracking();
		} else if ((endTime.getTime() - startTime.getTime()) / 1000 > 300) {
			stopActivity();
		}

		if (event.sensor == sensorA) {
			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
			xGPoints.add(gravity[0]);
			yGPoints.add(gravity[1]);
			zGPoints.add(gravity[2]);
			xAPoints.add(event.values[0] - gravity[0]);
			yAPoints.add(event.values[1] - gravity[1]);
			zAPoints.add(event.values[2] - gravity[2]);
		} else if (event.sensor == sensorG) {

			// Axis of the rotation sample, not normalized yet.
			float axisX = event.values[0];
			float axisY = event.values[1];
			float axisZ = event.values[2];

			// Calculate the angular speed of the sample
			float omegaMagnitude = (float) Math.sqrt(axisX * axisX + axisY
					* axisY + axisZ * axisZ);

			// Normalize the rotation vector if it's big enough to get the
			// axis
			// (that is, EPSILON should represent your maximum allowable
			// margin of error)
			if (omegaMagnitude > EPSILON) {
				axisX /= omegaMagnitude;
				axisY /= omegaMagnitude;
				axisZ /= omegaMagnitude;
			}

			xGYPoints.add(axisX);
			yGYPoints.add(axisY);
			zGYPoints.add(axisZ);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	class CollectData extends AsyncTask {

		private List<Float> attributes;
		ProgressDialog mprocess;
		String label;

		public CollectData(Context context, String label) {
			attributes = new ArrayList<Float>();
			mprocess = new ProgressDialog(context);
			mprocess.setTitle("Preparing Data");
			this.label = label;
		}

		@Override
		protected void onPreExecute() {
			mprocess.setMessage("Test Data File Generation in Progress");
			mprocess.show();
		}

		@Override
		protected Object doInBackground(Object... params) {

			Log.d("xAPoints", "Array : " + xAPoints.toArray());
			// TODO Auto-generated method stub
			float[] tempARF;
			int i = 0, j = 0;
			// Mean of Accelerometer Data
			attributes.add(mean(xAPoints));
			attributes.add(mean(yAPoints));
			attributes.add(mean(zAPoints));

			// Mean of Gyroscope Data
			attributes.add(mean(xGYPoints));
			attributes.add(mean(yGYPoints));
			attributes.add(mean(zGYPoints));

			// Mean of Gravity Data
			attributes.add(mean(xGPoints));
			attributes.add(mean(yGPoints));
			attributes.add(mean(zGPoints));

			// Standard Deviation of Accelerometer Data
			attributes.add(std(xAPoints));
			attributes.add(std(yAPoints));
			attributes.add(std(zAPoints));

			// Standard Deviation of Gyroscope Data
			attributes.add(std(xGYPoints));
			attributes.add(std(yGYPoints));
			attributes.add(std(zGYPoints));

			// Standard Deviation of Gravity Data
			attributes.add(std(xGPoints));
			attributes.add(std(yGPoints));
			attributes.add(std(zGPoints));

			Log.d("param", "Calling MAD");
			// Median Deviation of Accelerometer Data
			attributes.add(mad(xAPoints));
			attributes.add(mad(yAPoints));
			attributes.add(mad(zAPoints));
			Log.d("param", "Call Ended");

			// Median Deviation of Gyroscope Data
			attributes.add(mad(xGYPoints));
			attributes.add(mad(yGYPoints));
			attributes.add(mad(zGYPoints));

			// Median Deviation of Gravity Data
			attributes.add(mad(xGPoints));
			attributes.add(mad(yGPoints));
			attributes.add(mad(zGPoints));

			// Maximum of Accelerometer Data
			attributes.add(max(xAPoints));
			attributes.add(max(yAPoints));
			attributes.add(max(zAPoints));

			// Maximum of Gyroscope Data
			attributes.add(max(xGYPoints));
			attributes.add(max(yGYPoints));
			attributes.add(max(zGYPoints));

			// Maximum of Gravity Data
			attributes.add(max(xGPoints));
			attributes.add(max(yGPoints));
			attributes.add(max(zGPoints));

			// Minimum of Accelerometer Data
			attributes.add(min(xAPoints));
			attributes.add(min(yAPoints));
			attributes.add(min(zAPoints));

			// Minimum of Gyroscope Data
			attributes.add(min(xGYPoints));
			attributes.add(min(yGYPoints));
			attributes.add(min(zGYPoints));

			// Minimum of Gravity Data
			attributes.add(min(xGPoints));
			attributes.add(min(yGPoints));
			attributes.add(min(zGPoints));

			// Signal Magnitude Area of Accelerometer Data
			attributes.add(sma(xAPoints, yAPoints, zAPoints));

			// Signal Magnitude Area of Gyroscope Data
			attributes.add(sma(xGYPoints, yGYPoints, zGYPoints));

			// Signal Magnitude Area of Gravity Data
			attributes.add(sma(xGPoints, yGPoints, zGPoints));

			// Energy of Accelerometer Data
			attributes.add(energy(xAPoints));
			attributes.add(energy(yAPoints));
			attributes.add(energy(zAPoints));

			// Energy of Gyroscope Data
			attributes.add(energy(xGYPoints));
			attributes.add(energy(yGYPoints));
			attributes.add(energy(zGYPoints));

			// Energy of Gravity Data
			attributes.add(energy(xGPoints));
			attributes.add(energy(yGPoints));
			attributes.add(energy(zGPoints));

			// InterQuatile Range of Accelerometer Data
			Log.d("param", "IQR: SIZE " + xAPoints.size());
			attributes.add(iqr(xAPoints));
			Log.d("param", "IQR: SIZE " + yAPoints.size());
			attributes.add(iqr(yAPoints));
			Log.d("param", "IQR: SIZE " + zAPoints.size());
			attributes.add(iqr(zAPoints));

			// InterQuatile Range of Gyroscope Data
			Log.d("param", "IQR: GYX SIZE " + xAPoints.size());
			attributes.add(iqr(xGYPoints));
			Log.d("param", "IQR: GYY SIZE " + xAPoints.size());
			attributes.add(iqr(yGYPoints));
			Log.d("param", "IQR: GYZ SIZE " + xAPoints.size());
			attributes.add(iqr(zGYPoints));

			// InterQuatile Range of Gravity Data
			attributes.add(iqr(xGPoints));
			attributes.add(iqr(yGPoints));
			attributes.add(iqr(zGPoints));

			// Entropy Range of Accelerometer Data
			attributes.add(entropy(xAPoints));
			attributes.add(entropy(yAPoints));
			attributes.add(entropy(zAPoints));

			// Entropy Range of Gyroscope Data
			attributes.add(entropy(xGYPoints));
			attributes.add(entropy(yGYPoints));
			attributes.add(entropy(zGYPoints));

			// Entropy Range of Gravity Data
			attributes.add(entropy(xGPoints));
			attributes.add(entropy(yGPoints));
			attributes.add(entropy(zGPoints));

			// Skewness of Accelerometer Data
			attributes.add(skewness(xAPoints));
			attributes.add(skewness(yAPoints));
			attributes.add(skewness(zAPoints));

			// Skewness of Gyroscope Data
			attributes.add(skewness(xGYPoints));
			attributes.add(skewness(yGYPoints));
			attributes.add(skewness(zGYPoints));

			// Skewness of Gravity Data
			attributes.add(skewness(xGPoints));
			attributes.add(skewness(yGPoints));
			attributes.add(skewness(zGPoints));

			// Kurtosis of Accelerometer Data
			attributes.add(kurtosis(xAPoints));
			attributes.add(kurtosis(yAPoints));
			attributes.add(kurtosis(zAPoints));

			// Kurtosis of Gyroscope Data
			attributes.add(kurtosis(xGYPoints));
			attributes.add(kurtosis(yGYPoints));
			attributes.add(kurtosis(zGYPoints));

			// Kurtosis of Gravity Data
			attributes.add(kurtosis(xGPoints));
			attributes.add(kurtosis(yGPoints));
			attributes.add(kurtosis(zGPoints));

			// ARF of Accelerometer Data
			try {
				tempARF = arf(xAPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(yAPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(zAPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(xGPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(yGPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(zGPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(xGYPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(yGYPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(zGYPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Correlation of Accelerometer Data
			attributes.add(correlation(xAPoints, yAPoints));
			attributes.add(correlation(yAPoints, zAPoints));
			attributes.add(correlation(xAPoints, zAPoints));

			// Correlation of Gyroscope Data
			attributes.add(correlation(xGYPoints, yGYPoints));
			attributes.add(correlation(yGYPoints, zGYPoints));
			attributes.add(correlation(xGYPoints, zGYPoints));

			// Correlation of Gravity Data
			attributes.add(correlation(xGPoints, yGPoints));
			attributes.add(correlation(yGPoints, zGPoints));
			attributes.add(correlation(xGPoints, zGPoints));

			/*** After Fast fourier Tranform ***/

			Log.d("params", "FFTzfGYPoints Size : " + zfGYPoints.size());
			// Mean of Accelerometer Data
			attributes.add(mean(xfAPoints));
			attributes.add(mean(yfAPoints));
			attributes.add(mean(zfAPoints));

			// Mean of Gyroscope Data
			attributes.add(mean(xfGYPoints));
			attributes.add(mean(yfGYPoints));
			attributes.add(mean(zfGYPoints));

			// Mean of Gravity Data
			attributes.add(mean(xfGPoints));
			attributes.add(mean(yfGPoints));
			attributes.add(mean(zfGPoints));

			// Standard Deviation of Accelerometer Data
			attributes.add(std(xfAPoints));
			attributes.add(std(yfAPoints));
			attributes.add(std(zfAPoints));

			// Standard Deviation of Gyroscope Data
			attributes.add(std(xfGYPoints));
			attributes.add(std(yfGYPoints));
			attributes.add(std(zfGYPoints));

			// Standard Deviation of Gravity Data
			attributes.add(std(xfGPoints));
			attributes.add(std(yfGPoints));
			attributes.add(std(zfGPoints));

			// Median Deviation of Accelerometer Data
			attributes.add(mad(xfAPoints));
			attributes.add(mad(yfAPoints));
			attributes.add(mad(zfAPoints));

			// Median Deviation of Gyroscope Data
			attributes.add(mad(xfGYPoints));
			attributes.add(mad(yfGYPoints));
			Log.d("params", "AfterFFTzfGYPoints Size : " + zfGYPoints.size());
			attributes.add(mad(zfGYPoints));

			// Median Deviation of Gravity Data
			attributes.add(mad(xfGPoints));
			attributes.add(mad(yfGPoints));
			attributes.add(mad(zfGPoints));

			// Maximum of Accelerometer Data
			attributes.add(max(xfAPoints));
			attributes.add(max(yfAPoints));
			attributes.add(max(zfAPoints));

			// Maximum of Gyroscope Data
			attributes.add(max(xfGYPoints));
			attributes.add(max(yfGYPoints));
			attributes.add(max(zfGYPoints));

			// Maximum of Gravity Data
			attributes.add(max(xfGPoints));
			attributes.add(max(yfGPoints));
			attributes.add(max(zfGPoints));

			// Minimum of Accelerometer Data
			attributes.add(min(xfAPoints));
			attributes.add(min(yfAPoints));
			attributes.add(min(zfAPoints));

			// Minimum of Gyroscope Data
			attributes.add(min(xfGYPoints));
			attributes.add(min(yfGYPoints));
			attributes.add(min(zfGYPoints));

			// Minimum of Gravity Data
			attributes.add(min(xfGPoints));
			attributes.add(min(yfGPoints));
			attributes.add(min(zfGPoints));

			// Signal Magnitude Area of Accelerometer Data
			attributes.add(sma(xfAPoints, yfAPoints, zfAPoints));

			// Signal Magnitude Area of Gyroscope Data
			attributes.add(sma(xfGYPoints, yfGYPoints, zfGYPoints));

			// Signal Magnitude Area of Gravity Data
			attributes.add(sma(xfGPoints, yfGPoints, zfGPoints));

			// Energy of Accelerometer Data
			attributes.add(energy(xfAPoints));
			attributes.add(energy(yfAPoints));
			attributes.add(energy(zfAPoints));

			// Energy of Gyroscope Data
			attributes.add(energy(xfGYPoints));
			attributes.add(energy(yfGYPoints));
			attributes.add(energy(zfGYPoints));

			// Energy of Gravity Data
			attributes.add(energy(xfGPoints));
			attributes.add(energy(yfGPoints));
			attributes.add(energy(zfGPoints));

			// InterQuatile Range of Accelerometer Data
			attributes.add(iqr(xfAPoints));
			attributes.add(iqr(yfAPoints));
			attributes.add(iqr(zfAPoints));

			// InterQuatile Range of Gyroscope Data
			attributes.add(iqr(xfGYPoints));
			attributes.add(iqr(yfGYPoints));
			attributes.add(iqr(zfGYPoints));

			// InterQuatile Range of Gravity Data
			attributes.add(iqr(xfGPoints));
			attributes.add(iqr(yfGPoints));
			attributes.add(iqr(zfGPoints));

			// Entropy Range of Accelerometer Data
			attributes.add(entropy(xfAPoints));
			attributes.add(entropy(yfAPoints));
			attributes.add(entropy(zfAPoints));

			// Entropy Range of Gyroscope Data
			attributes.add(entropy(xfGYPoints));
			attributes.add(entropy(yfGYPoints));
			attributes.add(entropy(zfGYPoints));

			// Entropy Range of Gravity Data
			attributes.add(entropy(xfGPoints));
			attributes.add(entropy(yfGPoints));
			attributes.add(entropy(zfGPoints));

			// Skewness of Accelerometer Data
			attributes.add(skewness(xfAPoints));
			attributes.add(skewness(yfAPoints));
			attributes.add(skewness(zfAPoints));

			// Skewness of Gyroscope Data
			attributes.add(skewness(xfGYPoints));
			attributes.add(skewness(yfGYPoints));
			attributes.add(skewness(zfGYPoints));

			// Skewness of Gravity Data
			attributes.add(skewness(xfGPoints));
			attributes.add(skewness(yfGPoints));
			attributes.add(skewness(zfGPoints));

			// Kurtosis of Accelerometer Data
			attributes.add(kurtosis(xfAPoints));
			attributes.add(kurtosis(yfAPoints));
			attributes.add(kurtosis(zfAPoints));

			// Kurtosis of Gyroscope Data
			attributes.add(kurtosis(xfGYPoints));
			attributes.add(kurtosis(yfGYPoints));
			attributes.add(kurtosis(zfGYPoints));

			// Kurtosis of Gravity Data
			attributes.add(kurtosis(xfGPoints));
			attributes.add(kurtosis(yfGPoints));
			attributes.add(kurtosis(zfGPoints));

			// ARF of Accelerometer Data
			try {
				tempARF = arf(xfAPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(yfAPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(zfAPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(xfGPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(yfGPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(zfGPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(xfGYPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(yfGYPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}
				tempARF = arf(zfGYPoints);
				for (i = 0; i < tempARF.length; i++) {
					attributes.add(tempARF[i]);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Correlation of Accelerometer Data
			attributes.add(correlation(xfAPoints, yfAPoints));
			attributes.add(correlation(yfAPoints, zfAPoints));
			attributes.add(correlation(xfAPoints, zfAPoints));

			// Correlation of Gyroscope Data
			attributes.add(correlation(xfGYPoints, yfGYPoints));
			attributes.add(correlation(yfGYPoints, zfGYPoints));
			attributes.add(correlation(xfGYPoints, zfGYPoints));

			// Correlation of Gravity Data
			attributes.add(correlation(xfGPoints, yfGPoints));
			attributes.add(correlation(yfGPoints, zfGPoints));
			attributes.add(correlation(xfGPoints, zfGPoints));

			Log.d("ParamLength", attributes.size() + "");
			// Log.d("ParamList", attributes.toArray().toString());

			Log.d("param", "Before Creatin Directory");
			// Copy Data to a file
			File mediaStorageDir = new File("/sdcard/", "activity");
			boolean forward = false;
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdir()) {
					Log.d("param", "Failed to create Directory");
				} else {
					forward = true;
				}
			} else {
				forward = true;
				Log.d("param", "Directory Exists!");
			}
			if (forward) {
				Log.d("param", "Created activity directory");
				OutputStream outputStream = null;
				File file = new File(mediaStorageDir.getPath() + File.separator
						+ "activity.data");
				if (!file.exists()) {
					Log.d("param", "File: " + file.getAbsolutePath());
					try {
						if (file.createNewFile()) {
							outputStream = new FileOutputStream(file);
							// outputStream.write(imageData.toString().getBytes());
							String line = label + " ";
							for (i = 0; i < attributes.size(); i++) {
								line = line + attributes.get(i) + ":";
							}
							line = line + 0 + "\n";
							outputStream.write(line.getBytes());
							outputStream.close();
							// Toast toast =
							// Toast.makeText(context,"Succesfully Inserted line",Toast.LENGTH_LONG);
							// toast.show();
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						Log.d("param", "In old file");
						outputStream = new FileOutputStream(file, true);
						String line = label + " ";
						for (i = 0; i < attributes.size(); i++) {
							line = line + attributes.get(i) + ":";
						}
						line = line + 0 + "\n";
						outputStream.write(line.getBytes());
						outputStream.close();
						// Toast toast =
						// Toast.makeText(context,"Succesfully Inserted line",Toast.LENGTH_LONG);
						// toast.show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object object) {
			mprocess.dismiss();
			sensorManager.registerListener(sListener, sensorA,
					SensorManager.SENSOR_DELAY_FASTEST);
			sensorManager.registerListener(sListener, sensorG,
					SensorManager.SENSOR_DELAY_FASTEST);
		}
	}

	private float mean(List<Float> data) {
		float data_sum = 0.0f;
		for (float value : data) {
			// Log.d("param", "MeanData : "+ value);
			data_sum = data_sum + value;
		}
		return data_sum / data.size();
	}

	private float std(List<Float> data) {
		float square_sum = 0.0f;
		float meanOfData;
		meanOfData = mean(data);
		for (float value : data) {
			square_sum = square_sum + (value - meanOfData)
					* (value - meanOfData);
		}
		return (float) Math.sqrt(square_sum / data.size());
	}

	private float median(List<Float> data) {
		Object[] sortData;
		float[] sData;
		sortData = data.toArray();
		sData = new float[sortData.length];
		for (int i = 0; i < sortData.length; i++) {
			sData[i] = Float.parseFloat(sortData[i].toString());
		}
		Arrays.sort(sData);
		Log.d("param", "InMedianSize : " + sData.length);
		if (sData.length % 2 == 0) {
			// returns average of n/2 and n/2+1 element
			return ((sData[sData.length / 2] + sData[(sData.length / 2) - 1]) / 2);
		} else {
			return (sData[sData.length / 2]); // return n/2+1 element
		}
	}

	private List<Float> abs_deviation(List<Float> data) {
		List<Float> deviated_data = new ArrayList<Float>();
		Iterator<Float> it = data.iterator();
		// Log.d("param ", "Data Size : "+data.size());
		while (it.hasNext()) {
			deviated_data.add(it.next() - mean(data));
			// Log.d("param Values", it.next() - mean(data)+"");
		}
		// Log.d("param", "AfterDeviation");
		return deviated_data;
	}

	private float mad(List<Float> data) {
		// Log.d("param","IN MAD SIZE : " + data.size());
		return median(abs_deviation(data));
	}

	private float sma(List<Float> X, List<Float> Y, List<Float> Z) {
		float x_sum = 0.0f, y_sum = 0.0f, z_sum = 0.0f;
		float mean_x, mean_y, mean_z;
		for (float x : X) {
			mean_x = mean(X);
			x_sum = x_sum + Math.abs(x - mean_x);
		}
		for (float y : Y) {
			mean_y = mean(Y);
			y_sum = y_sum + Math.abs(y - mean_y);
		}
		for (float z : Z) {
			mean_z = mean(Z);
			z_sum = z_sum + Math.abs(z - mean_z);
		}
		return (x_sum + y_sum + z_sum) / X.size();

	}

	private float skewness(List<Float> data) {
		float cube_sum = 0;
		float mean_data = mean(data);
		for (float value : data) {
			cube_sum = cube_sum + (value - mean_data) * (value - mean_data)
					* (value - mean_data);
		}
		return (cube_sum) / (data.size() * (std(data) * std(data) * std(data)));
	}

	private float kurtosis(List<Float> data) {
		float sum = 0;
		float mean_data = mean(data);
		for (float value : data) {
			sum = sum + (value - mean_data) * (value - mean_data)
					* (value - mean_data) * (value - mean_data);
		}
		return (sum)
				/ (data.size() * (std(data) * std(data) * std(data) * std(data)));
	}

	private float energy(List<Float> data) {
		float sum = 0.0f;
		for (float value : data) {
			sum = sum + value * value;
		}
		return sum / data.size();
	}

	private float min(List<Float> data) {
		Object[] sortData;
		float[] sData;
		sortData = data.toArray();
		sData = new float[sortData.length];
		for (int i = 0; i < sortData.length; i++) {
			sData[i] = Float.parseFloat(sortData[i].toString());
		}
		Arrays.sort(sData);
		return (sData[0]);
	}

	private float max(List<Float> data) {
		Object[] sortData;
		float[] sData;
		sortData = data.toArray();
		sData = new float[sortData.length];
		for (int i = 0; i < sortData.length; i++) {
			sData[i] = Float.parseFloat(sortData[i].toString());
		}
		Arrays.sort(sData);
		return (sData[sData.length - 1]);
	}

	private float iqr(List<Float> data) {
		float temp = 0.0f;
		List<Float> leftList = new ArrayList<Float>();
		List<Float> rightList = new ArrayList<Float>();
		Collections.sort(data);
		Log.d("param", "iqrdatalength : " + data.size());
		float median = median(data);
		Log.d("param", "iqrMedian : " + median);
		Log.d("param", "iqrdatalengthaftermedian : " + data.size());
		Iterator<Float> iterate = data.iterator();
		while (iterate.hasNext()) {
			temp = iterate.next().floatValue();
			if (temp < median) {
				leftList.add(temp);

			} else if (temp > median) {
				rightList.add(temp);
			} else {
				// Do Nothing
			}
		}
		Log.d("param", "iqrRightListLength : " + rightList.size());
		Log.d("param", "iqrLeftListLength : " + leftList.size());
		return (median(rightList) - median(leftList));
	}

	private float[][] fft(List<Float> xData, List<Float> yData,
			List<Float> zData) {
		double[][] realArray, imagArray;
		float[][] result = null;
		int i = 0;
		// Log.d("param", "In FFT");
		// Log.d("paramSize", xData.size()+"");
		if (xData.size() % 2 == 0) {
			realArray = new double[3][xData.size()];
			imagArray = new double[3][xData.size()];
			for (i = 0; i < xData.size(); i++) {
				// Log.d("paramLength", xData.get(i)+"");
				realArray[0][i] = xData.get(i);
				realArray[1][i] = yData.get(i);
				realArray[2][i] = zData.get(i);
			}
		} else {
			realArray = new double[3][xData.size() - 1];
			imagArray = new double[3][xData.size() - 1];
			for (i = 0; i < xData.size() - 1; i++) {
				realArray[0][i] = xData.get(i);
				realArray[1][i] = yData.get(i);
				realArray[2][i] = zData.get(i);
			}
		}
		FastFourierTransform.fastFT(realArray, imagArray, true);
		// Log.d("realArray", realArray.length+"");
		result = new float[3][xData.size()];
		for (i = 0; i < realArray[0].length; i++) {
			result[0][i] = (float) realArray[0][i];
			// Log.d("param", result[0][i]+"");
			result[1][i] = (float) realArray[1][i];
			result[2][i] = (float) realArray[2][i];
		}

		return result;
	}

	private float entropy(List<Float> data) {
		float result = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();
		// count the occurrences of each value
		for (float sequence : data) {
			if (!map.containsKey(sequence)) {
				map.put(sequence + "", 0);
			}
			map.put(sequence + "", map.get(sequence + "") + 1);
		}

		// calculate the entropy
		for (String sequence : map.keySet()) {
			Double frequency = (double) map.get(sequence) / data.size();
			result -= frequency * (Math.log(frequency) / Math.log(2));
		}

		return result;
	}

	private float[] arf(List<Float> data) throws Exception {
		int order = 4;
		boolean removeMean = false;
		int i = 0;
		double[] result;
		float[] arc;
		double[] arrayData = new double[data.size()];
		for (i = 0; i < data.size(); i++) {
			arrayData[i] = data.get(i);
		}
		result = AutoRegression.calculateARCoefficients(arrayData, order,
				removeMean);
		arc = new float[result.length];
		for (i = 0; i < result.length; i++) {
			arc[i] = (float) result[i];
		}
		return arc;
	}

	private float correlation(List<Float> xData, List<Float> yData) {
		float result = 0;
		int i = 0;
		Double[] xArray = new Double[xData.size()];
		Double[] yArray = new Double[yData.size()];
		double temp = 0;
		for (i = 0; i < xData.size(); i++) {
			temp = xData.get(i);
			xArray[i] = new Double(temp);
		}
		for (i = 0; i < yData.size(); i++) {
			temp = yData.get(i);
			yArray[i] = new Double(temp);
		}

		result = (float) Autocorrelation.calcAutocorrelation(xArray, yArray);
		return result;
	}

	private float angleRadians(float x1, float y1, float z1, float x2,
			float y2, float z2) {
		float result = 0;
		float dot = x1 * x2 + y1 * y2 + z1 * z2;
		float mag1 = (float) Math.sqrt((x1 * x1 + y1 * y1 + z1 * z1))
				+ (float) Math.sqrt((x2 * x2 + y2 * y2 + z2 * z2));
		result = (float) (Math.acos(mag1) / Math.PI);
		return result;
	}
}

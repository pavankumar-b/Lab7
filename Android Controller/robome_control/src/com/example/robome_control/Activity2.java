package com.example.robome_control;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View.OnClickListener;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
 

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
public class Activity2 extends Activity implements OnClickListener {

	static int checkUpdate = 0;
	static String resultGesture = "";
	Socket socket = null;
	List<Socket> sList;
	PrintStream printStream;
	Intent intent = null;
	 	private TextView txtSpeechInput;
	    private ImageButton btnSpeak;
	    private Button leftB, rightB, upB, downB, stopB, sStream, stStream, autoStart, autoStop,sing;
	    String ip = null;
	    MyClientTask connectServer = null;
	    Context context = null;
	    private final int REQ_CODE_SPEECH_INPUT = 100;
	 
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.client);
	        intent = getIntent();
	        leftB = (Button)findViewById(R.id.button1);
	        rightB = (Button)findViewById(R.id.button2);
	        upB = (Button)findViewById(R.id.button3);
	        downB = (Button)findViewById(R.id.button4);
	        stopB = (Button)findViewById(R.id.button5);
	        sStream = (Button)findViewById(R.id.button6);
	        stStream = (Button)findViewById(R.id.button7);
	        autoStart = (Button)findViewById(R.id.button8);
	        autoStop = (Button)findViewById(R.id.button9);
	        sing = (Button)findViewById(R.id.button10);
	        leftB.setOnClickListener(this);
	        rightB.setOnClickListener(this);
	        upB.setOnClickListener(this);
	        downB.setOnClickListener(this);
	        stopB.setOnClickListener(this);
	        sStream.setOnClickListener(this);
	        stStream.setOnClickListener(this);
	        autoStart.setOnClickListener(this);
	        autoStop.setOnClickListener(this);
	        sing.setOnClickListener(this);
	        sList = new ArrayList<Socket>();
	        ip = intent.getStringExtra("ip");
	        Log.d("Actvity2", "After IP");
			connectServer = new MyClientTask(ip, 1234);
			Log.d("Server IP", ip);
			connectServer.execute();
	 
	    }
	    
	    /** socket logic **/
	    public class MyClientTask extends AsyncTask<Void, Void, Void> {

			String dstAddress;
			int dstPort;
			String response = "";

			MyClientTask(String addr, int port) {
				dstAddress = addr;
				dstPort = port;
			}

			@Override
			protected Void doInBackground(Void... arg0) {

				OutputStream outputStream;

				try {
					Log.d("Creating Socket", dstAddress);
					socket = new Socket(dstAddress, dstPort);
					outputStream = socket.getOutputStream();
					Log.d("MyClienet Task", "Destination Address : " + dstAddress);
					Log.d("MyClient Task", "Destination Port : " + dstPort + "");
					printStream = new PrintStream(outputStream);

				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					Log.d("ConectServer", "Unknown Host");
					Log.d("ConnectService", e.toString());
					response = "UnknownHostException: " + e.toString();
				} catch (IOException e) {
					Log.d("CoonectServer", "IO Excpetion");
					// TODO Auto-generated catch block
					/*Intent intent = new Intent(context, MainActivity.class);
					startActivity(intent);*/
					
					Log.d("ConnectService", e.toString());
					response = "IOException: " + e.toString();
				} finally {
					if (socket != null) {
//						try {
//							//socket.close();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
					}
					Log.d("ConnectServer", "In Final Clinet");
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Log.d("ConnectServer", "Socket closed: " + socket.isClosed());
				super.onPostExecute(result);
			}

		}
	 
	    /**
	     * Showing google speech input dialog
	     * */
	    private void promptSpeechInput() {
	        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
	        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
	                getString(R.string.speech_prompt));
	        try {
	            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
	        } catch (ActivityNotFoundException a) {
	            Toast.makeText(getApplicationContext(),
	                    getString(R.string.speech_not_supported),
	                    Toast.LENGTH_SHORT).show();
	        }
	    }
	 
	    /**
	     * Receiving speech input
	     * */
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	 
	        switch (requestCode) {
	        case REQ_CODE_SPEECH_INPUT: {
	            if (resultCode == RESULT_OK && null != data) {
	 
	                ArrayList<String> result = data
	                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	                txtSpeechInput.setText(result.get(0));
	                Activity2.resultGesture = result.get(0);
	                Activity2.checkUpdate = 1;
	            }
	            break;
	        }
	 
	        }
	    }

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1:
				Activity2.resultGesture = "left";
				break;
			case R.id.button2:
				Activity2.resultGesture = "right";
				break;
			case R.id.button3:
				Activity2.resultGesture = "up";
				break;
			case R.id.button4:
				Activity2.resultGesture = "down";
				break;
			case R.id.button5:
				Activity2.resultGesture = "stop";
				break;
			case R.id.button6:
				Activity2.resultGesture = "streamstart";
				break;
			case R.id.button7:
				Activity2.resultGesture = "streamstop";
				break;
			case R.id.button8:
				Activity2.resultGesture = "autostart";
				break;
			case R.id.button9:
				Activity2.resultGesture = "autostop";
				break;
			case R.id.button10:
				Activity2.resultGesture = "sing";
				break;
			default:
				Activity2.resultGesture = "others";
				break;
			}
			Log.i("Activity2", "Gesture: " + Activity2.resultGesture);
//			if(!socket.isClosed())
//			{
//				
//			}
			printStream.print(Activity2.resultGesture);
			printStream.flush();
//			Activity2.checkUpdate = 1;
		}
	 
//	    @Override
//	    public boolean onCreateOptionsMenu(Menu menu) {
//	        // Inflate the menu; this adds items to the action bar if it is present.
//	        getMenuInflater().inflate(R.menu.main, menu);
//	        return true;
//	    }
}

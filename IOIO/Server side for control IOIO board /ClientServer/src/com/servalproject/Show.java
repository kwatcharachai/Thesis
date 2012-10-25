package com.servalproject;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Show extends Activity {
	protected static final int MAX_UDP_DATAGRAM_LEN = 15000;
	private static final int UDP_SERVER_PORT = 11111;
	private String Status;
	private TextView txtDisplay;
	private Boolean StartStopServer; 
	private AsyncTask<Void, String, Void> task;
	private ioio objIO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		
		
		StartStopServer = true;
		
		TextView txtStatus = (TextView) findViewById(R.id.ShowSC); 
		final Button btnStart = (Button) findViewById(R.id.Start);
		final Button btnStop = (Button) findViewById(R.id.Stop);
		txtDisplay = (TextView) findViewById(R.id.text1);
		
		Status = getIntent().getStringExtra("Showradio");
		txtStatus.setText(Status);
		
		
		
		btnStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(Status.equals("Server")){
					
					Log.d(ACCESSIBILITY_SERVICE, "Server");
					btnStart.setEnabled(false);
					btnStop.setEnabled(true);
					StartStopServer = true;
					
					objIO = new ioio();
					objIO.startIO();
					
					runUdpServer();
					
				}
					else if(Status.equals("Client")){
						
						Log.d(ACCESSIBILITY_SERVICE, "Client");
						Intent goClientShow = new Intent(Show.this,ClientShow.class);				      
				        startActivity(goClientShow);	
						
				}
								
			}
		});
		
		btnStop.setEnabled(false);
		btnStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartStopServer = false;
				objIO.stopIO();
				stopServer();
				finish();
			}
		});
		
	}
	
			
	
	
	private void runUdpServer() {		
		
		task =  new AsyncTask<Void, String, Void>(){

			@Override
			protected void onProgressUpdate(String... values) {
		       txtDisplay.setText(values[0]);
	
			}

			@Override
			protected Void doInBackground(Void... arg0) {
			    //String lText;
			    byte[] lMsg = new byte[MAX_UDP_DATAGRAM_LEN];
			    DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
			    DatagramSocket ds = null;
			   while(StartStopServer){
			    try {
			    	final String lText;
			        ds = new DatagramSocket(UDP_SERVER_PORT);
			        ds.receive(dp);
			        lText = new String(lMsg, 0, dp.getLength());
			        Log.i("UDP packet received", lText);
			        Log.d("Receive", lText);
			        
			        this.publishProgress(lText); // Show on txtDisplay
			        
			        // TODO Auto-generated method stub
			       objIO.setPinData(lText);
			    
			    } catch (SocketException e) {
			        e.printStackTrace();
			    
			    } catch (IOException e) {
			        e.printStackTrace();
			    
			    } finally {
			        if (ds != null) {
			            ds.close();
			        }
			       
			    }
			    
			}
			   return null;
		  }

		}.execute((Void[])null);
		
		
	}
	
	private void stopServer()
	{
		try {
			task.cancel(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}

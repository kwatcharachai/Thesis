package com.servalproject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ClientShow extends Activity  {

	private static final int UDP_SERVER_PORT = 11111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cientshow); 
		
		Intent intent = new Intent(ClientShow.this, Service1.class);
		startService(intent);
		
		final EditText InputText = (EditText) findViewById(R.id.editText1);
		
		Button Send = (Button) findViewById(R.id.send);
		Send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				runUdpClient(InputText.getText().toString());			
				finish();			
			}
		});
		
			
	}
	
	public static void runUdpClient(String a)  {
	    String udpMsg = a;
	    DatagramSocket ds = null;
	    try {
	        ds = new DatagramSocket();
	        ds.setBroadcast(true);
	        
	        InetAddress serverAddr = InetAddress.getByName("28.184.3.85");
	        //28.168.172.220
	        DatagramPacket dp;
	        dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), serverAddr, UDP_SERVER_PORT);
	        ds.send(dp);
	    } catch (SocketException e) {
	        e.printStackTrace();
	   
	    }catch (UnknownHostException e) {
	        e.printStackTrace();
	    
	    } catch (IOException e) {
	        e.printStackTrace();
	    
	    } catch (Exception e) {
	        e.printStackTrace();
	    
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }
	    }
	
	}
	
}





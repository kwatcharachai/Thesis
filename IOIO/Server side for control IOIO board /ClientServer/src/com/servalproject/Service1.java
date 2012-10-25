package com.servalproject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


public class Service1 extends Service {
	private static final String LOG_TAG = "SERVICE1";
	private Handler serviceHandler = null; 
	  
    @Override
    public void onStart( Intent intent, int startId ) {
	  super.onStart( intent, startId );
	  serviceHandler = new Handler();

	  //set time millisecond
	  serviceHandler.postDelayed( new RunTask(),6000L );
    }

    @Override
    public void onCreate() {
      super.onCreate();
	  Log.d( LOG_TAG,"onCreate" );
	  
	  BroadcastReceiver instrumentReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				
				String action = intent.getAction();
				if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
					
					int CurrentBatt = intent.getIntExtra("level", 0);
					Log.v("Batt", CurrentBatt+"");
				
				}
			}
		};
		
		IntentFilter instrumentFilter = new IntentFilter();
		instrumentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		this.registerReceiver(instrumentReceiver, instrumentFilter);
		
	  
    }

    @Override
    public void onDestroy() {
	  super.onDestroy();
	  Log.d( LOG_TAG,"onDestroy" );
    }
    
   
    
    class RunTask implements Runnable {

	public void run() {

		serviceHandler.postDelayed( this, 6000L );
	  }
	}


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}




package com.servalproject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;


public class ChargeService extends Service {
	private static final String LOG_TAG = "ChargeService";
	private Handler serviceHandler = null; 
	protected PowerManager.WakeLock mWakeLock;
	  
    @Override
    public void onStart( Intent intent, int startId ) {
	  super.onStart( intent, startId );
	  serviceHandler = new Handler();

	  //set time millisecond
	  serviceHandler.postDelayed( new RunTask(),10000L );
    }

    @Override
    public void onCreate() {
      super.onCreate();
	  Log.d( LOG_TAG,"onCreate" );
	  
	  final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	  this.mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "FULL_WAKE_LOCK");
	  this.mWakeLock.acquire();
	  
	  BroadcastReceiver instrumentReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				
				String action = intent.getAction();
				if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
					
					int CurrentBatt = intent.getIntExtra("level", 0);
					Log.v("Batt", CurrentBatt+"");
					if (CurrentBatt > StartStop.Min){
						Toast.makeText(getApplicationContext(), "Battery> Min  ChargeService", Toast.LENGTH_SHORT).show();
						stopSelf();
					
					}
					
				}
			}
		};
		
		IntentFilter instrumentFilter = new IntentFilter();
		instrumentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		this.registerReceiver(instrumentReceiver, instrumentFilter);
		
	  
    }

    @Override
    public void onDestroy() {
      this.mWakeLock.release();
	  super.onDestroy();
	  Log.d( LOG_TAG,"onDestroy" );
    }
    
   
    
    class RunTask implements Runnable {

	public void run() {
		serviceHandler.postDelayed( this, 10000L );
	  }
	}


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}

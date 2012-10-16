package com.servalproject;

import java.util.Calendar;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class LogService extends Service {
	private static final String LOG_TAG = "LOGSERVICE";
	public static final String WIFI_MODE_ACTION = "org.servalproject.WIFI_MODE";
	public static final String EXTRA_NEW_MODE = "new_mode";
	public static final String EXTRA_CHANGING = "changing";
	public static final String EXTRA_CHANGE_PENDING = "change_pending";
	public static final String EXTRA_CONNECTED_SSID = "wifi_ssid";
	public static int MaxLevel = Integer.parseInt(StartStop.PercentMax); 
	public static int MinLevel = Integer.parseInt(StartStop.PercentMin);
	public static int TopLevel = MaxLevel + 1;
	public static int Max = MaxLevel;
	public static int CurrentBatt;
	public static long FirstStartTime;
	protected PowerManager.WakeLock mWakeLock;
	
	private Handler serviceHandler = null; 
	  
    @Override
    public void onStart( Intent intent, int startId ) {
	  super.onStart( intent, startId );
	  serviceHandler = new Handler();

	  //set time millisecond
	  serviceHandler.postDelayed( new RunTask(),60000L );
    }

    @Override
    public void onCreate() {
    	
      super.onCreate();
	  Log.d( LOG_TAG,"onCreate" );
	  
	  final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	  this.mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "FULL_WAKE_LOCK");
	  this.mWakeLock.acquire();
	  
	   BroadcastReceiver receiver1 = new Receiver();

		   IntentFilter filter = new IntentFilter();
		   filter.addAction(WIFI_MODE_ACTION);
		   this.registerReceiver(receiver1, filter);

	
	   BroadcastReceiver instrumentReceiver = new BroadcastReceiver() {
	
			@Override
			public void onReceive(Context context, Intent intent) {
				
				String action = intent.getAction();
				if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
					
					String logLine = "" + intent.getIntExtra("level", 0) + " ; "
							+ intent.getIntExtra("scale", 0) + " ; "
							+ intent.getIntExtra("voltage", 0) + " ; "
							+ intent.getIntExtra("temperature", 0) + " ; "
							+ intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) + " ; "
							+ intent.getIntExtra("plugged", 0) + " ; "
							+ intent.getIntExtra("health", 0) + "\r\n\n";
					Log.e(logLine, logLine);
					
					
			//date		
					Calendar c = Calendar.getInstance();

					String sDate = c.get(Calendar.YEAR) + "-" 
					+ c.get(Calendar.MONTH)
					+ "-" + c.get(Calendar.DAY_OF_MONTH) 
					+ " at " + c.get(Calendar.HOUR_OF_DAY) 
					+ ":" + c.get(Calendar.MINUTE) + "\r\n";
					Log.e(sDate, sDate);
							
					
				if (MaxLevel == intent.getIntExtra("level", 0))
					{
					CurrentBatt = intent.getIntExtra("level", 0);
					
					Toast.makeText(getApplicationContext(), "Batt" + CurrentBatt +"", Toast.LENGTH_SHORT).show();
					
						if (CurrentBatt == Max){
							Toast.makeText(getApplicationContext(), "Batt == Max", Toast.LENGTH_SHORT).show();
							FirstStartTime = System.currentTimeMillis();
							Log.v ("FirstTime", FirstStartTime + "");
						}
						
					 if (TopLevel - CurrentBatt == 1)
					   {
						  TopLevel = CurrentBatt;
						  --MaxLevel;	  
						   
						  Write LogLevel = new Write();
						  LogLevel.writefile(sDate, logLine);
						 
						  // Batt is less than or equal to MinLevel
						  		if (CurrentBatt <= MinLevel)
						  		{		
						  			Toast.makeText(getApplicationContext(), "Batt <= Min", Toast.LENGTH_SHORT).show();
						  			
						  			Receiver.AllTotalTime = Receiver.TotalUpTime + Receiver.TotalRampUpTime 
						  					+ Receiver.TotalDownTime + Receiver.TotalRampDownTime;
						  			Receiver.RawlogResults();
						  			
						  			
						  				if (Receiver.TotalStartCount > Receiver.TotalStopCount)
						  				{
						  					long TotalStopTime1 = Receiver.TotalStopCount * StartStop.Stop;
						  					long TotalStopTime2 = Receiver.TotalRampDownTime + Receiver.TotalDownTime;
						  						  				
						  					if(TotalStopTime1 > TotalStopTime2){
						  						long Compare1 = TotalStopTime1 - TotalStopTime2;
						  						Receiver.TotalDownTime = Receiver.TotalDownTime + Compare1;
			
						  					}else{
						  						long Compare2 = TotalStopTime2 - TotalStopTime1;
						  						Receiver.TotalDownTime = Receiver.TotalDownTime - Compare2;
						  					}
			
						  				}
						  				 else
						  				{
						  					long TotalStartTime1 = Receiver.TotalStartCount * StartStop.Start;
						  					long TotalStartTime2 = Receiver.TotalUpTime + Receiver.TotalRampUpTime;
						  					 
						  					if(TotalStartTime1 > TotalStartTime2){
						  						long Compare3 = TotalStartTime1 - TotalStartTime2;
						  						Receiver.TotalUpTime = Receiver.TotalUpTime + Compare3;
						  					}else{
						  						long Compare4 = TotalStartTime2 - TotalStartTime1;
						  						Receiver.TotalUpTime = Receiver.TotalUpTime - Compare4;
						  					}
						  				
						  				}
						  				
						  			Receiver.AllTotalTime = Receiver.TotalUpTime + Receiver.TotalRampUpTime 
						  					+ Receiver.TotalDownTime + Receiver.TotalRampDownTime;
						  				
						  				if(Receiver.TotalStartCount > Receiver.TotalStopCount){
						  					long UpTime; 
						  					UpTime = Write.AllCountSum - Receiver.AllTotalTime;
						  					Receiver.TotalUpTime += UpTime;
						  				}else{
						  					long DownTime; 
						  					DownTime = Write.AllCountSum - Receiver.AllTotalTime;
						  					Receiver.TotalDownTime += DownTime;
						  				}
						  				
						  			
						  			Receiver.AllTotalTime = Receiver.TotalUpTime + Receiver.TotalRampUpTime 
						  					+ Receiver.TotalDownTime + Receiver.TotalRampDownTime;	
						  			
						  			Receiver.logResults();
									Receiver.reset();
															  		
						  			MaxLevel = Max;
									MinLevel = StartStop.Min;
									TopLevel = MaxLevel + 1;
									
									stopSelf();	  	
						  			
						  		}
					    }
					}
					
				}
			}
		}; 
		
		IntentFilter instrumentFilter = new IntentFilter();
		instrumentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		this.registerReceiver(instrumentReceiver, instrumentFilter);	

    } 
    
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
    	return START_STICKY;
	}
    

	@Override
    public void onDestroy() {
      this.mWakeLock.release();
	  super.onDestroy();
	  Log.d( LOG_TAG,"onDestroy" );
    }
    
   
    
  class RunTask implements Runnable {

	public void run() {
		//set time millisecond
		serviceHandler.postDelayed( this, 60000L );
	  }
	}


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}





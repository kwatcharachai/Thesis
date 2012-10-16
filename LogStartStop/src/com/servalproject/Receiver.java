package com.servalproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
	final String NOSUCHMODE = "no such mode as this";
	String lastMode = NOSUCHMODE;

	long lastModeChangeTime=0;

	public static long TotalUpTime = 0; 
	public static long TotalDownTime = 0;
	public static long TotalRampUpTime = 0; 
	public static long TotalRampDownTime = 0;
	public static long TotalStartCount =0;
	public static long TotalStopCount =0;
	public static long AllTotalTime =0;
	public static long ExperimentStartTime = 0;

	public static void logResults() {
		String PowerName = "powerstats.txt";
		File root = null;
		
		try{
			// check for SDcard   
            root = Environment.getExternalStorageDirectory();  
            
            if (root.canWrite()){  
                File fileDir = new File(root.getAbsolutePath()+"/PowerLog/");  
                fileDir.mkdirs();  
               
                File file= new File(fileDir, PowerName); 
			
			//FileWriter fstream = new FileWriter("powerstats.log");
            FileWriter fstream = new FileWriter(file,true);  
			BufferedWriter out = new BufferedWriter(fstream);
			/*out.write(String.format("%ld;%ld;%ld;%ld;%ld;%ld;%ld,%ld\n",
					System.currentTimeMillis(),					
					TotalUpTime,
					TotalDownTime,
					TotalRampUpTime,
					TotalRampDownTime,
					TotalStartCount,
					TotalStopCount,
					AllTotalTime));*/
			out.write("\n" + StartStop.Start +"\t"+ StartStop.Stop +"\t"+ TotalRampUpTime +"\t"+ TotalUpTime
					+"\t"+ TotalRampDownTime +"\t"+ TotalDownTime +"\t"+ AllTotalTime +"\t" + TotalStartCount +"\t"+
					TotalStopCount + "\n" );
			
			out.close();
			
		}
            
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void RawlogResults() {
		String PowerName = "Rawpowerstats.txt";
		File root = null;
		
		try{
			// check for SDcard   
            root = Environment.getExternalStorageDirectory();  
            
            if (root.canWrite()){  
                File fileDir = new File(root.getAbsolutePath()+"/PowerLog/");  
                fileDir.mkdirs();  
               
            File file= new File(fileDir, PowerName); 
			
            FileWriter fstream = new FileWriter(file,true);  
			BufferedWriter out = new BufferedWriter(fstream);
			
			out.write("\n" + StartStop.Start +"\t"+ StartStop.Stop +"\t"+ TotalRampUpTime +"\t"+ TotalUpTime
					+"\t"+ TotalRampDownTime +"\t"+ TotalDownTime +"\t"+ AllTotalTime +"\t" + TotalStartCount +"\t"+
					TotalStopCount + "\n" );
			
			out.close();
			
		}
            
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void reset() {
		TotalUpTime=0;
		TotalDownTime=0;
		TotalRampUpTime=0;
		TotalRampDownTime=0;
		TotalStartCount=0;
		TotalStopCount=0;
		AllTotalTime = 0;
		ExperimentStartTime = System.currentTimeMillis();
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		if (action.equals(LogService.WIFI_MODE_ACTION)) {
			String mode = intent.getStringExtra(LogService.EXTRA_NEW_MODE);
			Log.v("Mode", mode);

			boolean changing = intent.getBooleanExtra(
					LogService.EXTRA_CHANGING, false);
			Log.v("Change", changing + "");

			if (!changing) {
				// finished changing mode so record ramp up/down time
				if (!mode.equals(lastMode))
				{
					Log.v("WifiStatus", "Changed to: "+mode);

					// Work out how long we spent and in which mode.
					// Add that duration to the appropriate timer.
					if (lastModeChangeTime!=0) {
						long duration = System.currentTimeMillis()-lastModeChangeTime;
						if (mode.equals("Adhoc")){
							TotalRampUpTime+=duration;
							TotalStartCount++;
							Log.v("RampUp", ""+TotalRampUpTime);
							Log.v("StartCount", ""+TotalStartCount);
							
						} else if (mode.equals("Off")) {
							TotalRampDownTime+=duration;
							TotalStopCount++;
							Log.v("RampDown", ""+TotalRampDownTime);
							Log.v("StopCount", ""+TotalStopCount);
						}	
					}	
				}
			} else {
				// changing mode, so record time spent in previous mode
				Log.v("WifiStatus", "Changing to: "+mode);

				// Work out how long we spent and in which mode.
				// Add that duration to the appropriate timer.
				if (lastModeChangeTime!=0) {
					long duration = System.currentTimeMillis()-lastModeChangeTime;
					if (lastMode.equals("Off")){
						TotalDownTime+=duration;
						Log.v("TotalDownTime", ""+TotalDownTime);
					}else if (lastMode.equals("Adhoc")) {
						TotalUpTime+=duration;
						Log.v("TotalUpTime", ""+TotalUpTime);
					}
				}
			}
			// Remember state for next time around
			lastMode=mode;
			lastModeChangeTime=System.currentTimeMillis();
		}
	}
}

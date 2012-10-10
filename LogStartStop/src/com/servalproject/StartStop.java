package com.servalproject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StartStop extends Activity {
	
	private static final int UDP_SERVER_PORT = 11111;
	
	public static String ReceptStartCount, ReceptStopCount,ReceptLoopCount;
	public static String PercentMax, PercentMin;
	public static String LogName;
	public static int  Min, Battery; 
	public static long CountStartSec, CountStopSec, Start, Stop;
	public static String GetStartCount, GetStopCount,GetLoopCount;
	int CountLoop = 0;
	int LoopCountSet;
	private Boolean LoopCheck;
	private Context context;
	public static long FirstStartTime1;
	protected PowerManager.WakeLock mWakeLock;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startstop);
		context = this;
		
		GetStartCount = getIntent().getStringExtra("TextStartCountInput");
		GetStopCount = getIntent().getStringExtra("TextStopCountInput");
		GetLoopCount = getIntent().getStringExtra("TextLoopCountInput");
		
		long CountStartSec = Long.valueOf(GetStartCount);
		long CountStopSec = Long.valueOf(GetStopCount);
		LoopCountSet = Integer.valueOf(GetLoopCount);

	    Start = (CountStartSec * 1000); 
	    Stop = (CountStopSec * 1000);
	    Log.v("Batt",Start+"");
	    Log.v("Batt",Stop+"");
		
		isStart = false;
        isStop = false;
        LoopCheck = false;
		
        PercentMax = CountSet.ReceptMax;
        PercentMin = CountSet.ReceptMin;
    	
        String min = PercentMin;
        Min = Integer.parseInt(min);
        
        ReceptStartCount = getIntent().getStringExtra("TextStartCountInput");
        ReceptStopCount = getIntent().getStringExtra("TextStopCountInput");
        ReceptLoopCount = getIntent().getStringExtra("TextLoopCountInput");
        
        TextView txtMinMaxShow = (TextView) findViewById(R.id.textResultPercent); 
        txtMinMaxShow.setText(PercentMax + "%" + "-" + PercentMin + "%");
        TextView txtStartStopCountShow = (TextView) findViewById(R.id.textResultCount); 
        txtStartStopCountShow.setText(ReceptStartCount + "Sec" + "-" + ReceptStopCount + "Sec");
        
        TextView txtLoopShow = (TextView) findViewById(R.id.loopCollect); 
        txtLoopShow.setText("Loop Count Set= " + ReceptLoopCount);
          
        final EditText logName= (EditText) findViewById(R.id.editLogName);
      
		final Button startButton = (Button) findViewById(R.id.StartButton);
		final Button stopButton = (Button) findViewById(R.id.StopButton);	
		
		
		startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				LogName = logName.getText().toString();
				FirstStartTime1 = System.currentTimeMillis();
				Intent intent = new Intent(context, LogService.class);
				startService(intent);
				start();
				//doWifiScanSet();
				doWifiScan();
				
			}
		});
		
		stopButton.setEnabled(false);
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				Receiver.reset();
				CountLoop = 0;
				LogService.MaxLevel = LogService.Max;
				LogService.MinLevel = Min;
				LogService.TopLevel = LogService.MaxLevel + 1;
				stopScan();
				stopScan1();
				//stopScanSet();
				Intent intent = new Intent(StartStop.this, LogService.class);
				stopService(intent);
				Intent intentNew = new Intent(context, ChargeService.class);
				stopService(intentNew);
				
			}
		});
		
		Button s_BackButton = (Button) findViewById(R.id.S_BackButton);
		s_BackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();			
			}
		});

		// For Check loop of battery charging
		BroadcastReceiver instrumentReceiver1 = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				
				String action = intent.getAction();
				if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
					
					Battery = intent.getIntExtra("level", 0);
					Log.v("Batt",Battery+"");	
					
			        TextView txtLoopCheckShow = (TextView) findViewById(R.id.textLoopCheck); 
				    txtLoopCheckShow.setText("Loop = " + CountLoop);
				    				   
					if (Battery <= Min){
						Toast.makeText(getApplicationContext(), "Batt < Min Loop Count "+ CountLoop, Toast.LENGTH_SHORT).show();
						Intent intentNew = new Intent(context, ChargeService.class);
						startService(intentNew);
					}
				
					if(LoopCheck){
					 if (Battery == LogService.Max  && CountLoop < LoopCountSet) {
						 
						 chargeOver();
							
					  }	
					} 
				
				}
			}
						
		};
				
		IntentFilter instrumentFilter1 = new IntentFilter();
		instrumentFilter1.addAction(Intent.ACTION_BATTERY_CHANGED);
		this.registerReceiver(instrumentReceiver1, instrumentFilter1);		

	}
	

	private boolean isStart;
    private boolean isStop;
    
    
    private TimerTask scanTask;
    final Handler handler = new Handler(Looper.getMainLooper());
    private Timer timer = new Timer();
    
    public void doWifiScan()
    {
    	if(!isStop)
    	{
		    scanTask = new TimerTask() {
		    	public void run() { 
		    		handler.post(new Runnable() {
		    			public void run() {
		    				// isStart = true
		    				if(!isStart){
		    					start();
		    					}
		    				else {
		    					stop();
		    					Log.v("Time1",isStart + "");
		    				}
		    				boolean z = (Battery <= Min);
		    				
		    				//isStart = False
		    				if (!isStart && z){
		    					
		    					// Set loop collect data repeatedly, in case of remained stop
		    					boolean loop = (CountLoop < LoopCountSet);
		    					if(!isStart && loop) {	
		    						Toast.makeText(getApplicationContext(), "startServal Loop", Toast.LENGTH_SHORT).show();
		    						startServal();
		    					
		    					}else{
		    						stopScan();
		    						stopScan1();
		    					} 
		    					
		    					
		    				}else { 
		    					Boolean loop01 = (CountLoop < LoopCountSet);
		    					if (true && loop01)
		    					{
		    						// Count down time for Stop Serval and then Start Serval
		    						doWifiScan1();
		    					}
		    				}
		    				   				
		                }

		        });
		    }};
	        timer.schedule(scanTask, Start); 
	     
    	}

     }

    public void stopScan()
      {
       if(scanTask!=null){
          scanTask.cancel();
          scanTask = null;
      }

    }
    
    
    public void doWifiScan1()
    {	
    	if(!isStop)
    	{
		    scanTask = new TimerTask() {
		    	public void run() {
		    		handler.post(new Runnable() {
		    			public void run() {
		    				//isStart = False
		    				if(!isStart){
		    					start();
		    					Log.v("Time2",isStart + "");
		    				}
		    				else {
		    					stop();
		    				}
		  
		    				boolean y = (Battery <= Min);
		    				
		    				//isStart = true
		    				if (isStart && y){
		    					
		    					// Set loop collect data repeatedly, in case of remained start
		    					boolean loop1 = (CountLoop < LoopCountSet);
		    					if(isStart && loop1) {
		    						Toast.makeText(getApplicationContext(), "stopServal Loop", Toast.LENGTH_SHORT).show();
		    
		    						//stopServal();
		    						doStartServalCharge();
		    						
		    					}else{
		    						stopScan1();
		    						stopScan();
		    					  } 
		    					
		    					
		    				}else{ 
		    					Boolean loop11 = (CountLoop < LoopCountSet);
		    					if (true && loop11)
		    					{
		    						// Count down time for Start Serval and then Stop Serval
		    						doWifiScan();
		    					}
		    				}
		                }
		    	
		        });
		    		
		    }};
	        timer.schedule(scanTask,Stop); 
	     
    	}

     }

    public void stopScan1()
       {
       if(scanTask!=null){
          scanTask.cancel();
          scanTask = null;
       }
       
      }
    
    
	public void start() {
		// TODO Auto-generated method stub
		
    	Intent mIntent = new Intent("org.servalproject.ACTION_START_SERVAL");
		startService(mIntent);
	
		isStart = true;
	}

	public void stop() {

    	Intent mIntent = new Intent("org.servalproject.ACTION_STOP_SERVAL");
		startService(mIntent);
		
		isStart = false;
	}


public void chargeOver()
{
	if(true)
	{
	    scanTask = new TimerTask() {
	    	public void run() {
	    		handler.post(new Runnable() {
	    			public void run(){
	    				startFromMax();
	               }
	        });
	    		
	    }};
        timer.schedule(scanTask,120000); 
     
	}

 }
	

public void startFromMax() {
	Intent mIntent = new Intent("org.servalproject.ACTION_START_SERVAL");
	startService(mIntent);
	
	CountLoop++;
	Toast.makeText(getApplicationContext(), "Loop Count = "+ CountLoop, Toast.LENGTH_SHORT).show();
	
	StartServalLoop();
}

public void startServal() {
		// TODO Auto-generated method stub
    		doStopServalChargeStart();
	}
    
    public void doStopServalChargeStart()
    {
    	if(true)
    	{
		    scanTask = new TimerTask() {
		    	public void run() {
		    		handler.post(new Runnable() {
		    			public void run(){
		    				Intent mIntentStart = new Intent("org.servalproject.ACTION_START_SERVAL");
		    	    		startService(mIntentStart);	
		    	    		doStartServalCharge();
		               }
		        });
		    		
		    }};
	        timer.schedule(scanTask,5000); 
	     
    	}

     }
    
/*public void stopServal() {
		
    		doStopServalChargeStop();		

	}
    
    
    public void doStopServalChargeStop()
    {
    	if(true)
    	{
		    scanTask = new TimerTask() {
		    	public void run() {
		    		handler.post(new Runnable() {
		    			public void run(){
		    				Intent mIntent = new Intent("org.servalproject.ACTION_STOP_SERVAL");
		    	    		startService(mIntent);
		    	    		doStopServalChargeRest();
		               }
		        });
		    		
		    }};
	        timer.schedule(scanTask,5000); 
	     
    	}

     }
    
    public void doStopServalChargeRest()
    {
    	if(true)
    	{
		    scanTask = new TimerTask() {
		    	public void run() {
		    		handler.post(new Runnable() {
		    			public void run(){
		    				StartChargeService();
		               }
		        });
		    		
		    }};
	        timer.schedule(scanTask,5000); 
	     
    	}

     }
    
    public void StartChargeService(){
		
			Intent mIntent = new Intent("org.servalproject.ACTION_START_SERVAL");
			startService(mIntent);
			doStartServalCharge();
		
	}*/
    
  // Start and Stop Serval  
    public void doStartServalCharge()
    {
    	if(true)
    	{
		    scanTask = new TimerTask() {
		    	public void run() {
		    		handler.post(new Runnable() {
		    			public void run(){
		    				String Charge = "4";
		    				//String Charge = "3";
    						runUdpClient(Charge); 
    						Toast.makeText(getApplicationContext(), Charge +"", Toast.LENGTH_SHORT).show();
		    				stopServal0();
		               }
		        });
		    		
		    }};
	        timer.schedule(scanTask,30000); 
	     
    	}

     }   
    
    
public void stopServal0() {
	 	doStopServalCharge();

	}
    
 
    // Stop, Start and Stop Serval
    public void doStopServalCharge()
    {
    	if(true)
    	{
		    scanTask = new TimerTask() {
		    	public void run() {
		    		handler.post(new Runnable() {
		    			public void run(){
		    				Intent mIntent = new Intent("org.servalproject.ACTION_STOP_SERVAL");
		    				startService(mIntent);
		    				
		    				LoopCheck = true;
		    				Toast.makeText(getApplicationContext(), "StopServalCharge", Toast.LENGTH_SHORT).show(); 
		    				
		               }
		        });
		    		
		    }};
	        timer.schedule(scanTask,10000); 
	     
    	}

     }
    
    
    
public void stopServal1() {
		
		doStopServalCharge1();

	}
 
    public void doStopServalCharge1()
    {
    	if(true)
    	{
		    scanTask = new TimerTask() {
		    	public void run() {
		    		handler.post(new Runnable() {
		    			public void run(){
		    				Intent mIntent = new Intent("org.servalproject.ACTION_STOP_SERVAL");
		    				startService(mIntent);
		    				
		    				if (CountLoop < LoopCountSet)
		    				{
		    					Toast.makeText(getApplicationContext(), "CountLoop after checking If = " + CountLoop , Toast.LENGTH_SHORT).show();
		    					
		    					if (LoopCountSet - CountLoop == 1){
		    						 Start = 240 * 1000; 
		    						  Stop = 240 * 1000;
		    					}else if (LoopCountSet - CountLoop == 2) {
		    						 Start = 240 * 1000; 
		    						  Stop = 240 * 1000;
		    					}else if (LoopCountSet - CountLoop == 3) {
		    						 Start = 240 * 1000; 
		    						  Stop = 240 * 1000;
		    					}else if (LoopCountSet - CountLoop == 4) {
		    						 Start = 120 * 1000; 
		    						  Stop = 120 * 1000;
		    					}else if (LoopCountSet - CountLoop == 5) {
		    						 Start = 120 * 1000; 
		    						  Stop = 120 * 1000;
		    					}else if (LoopCountSet - CountLoop == 6) {
		    						 Start = 120 * 1000; 
		    						  Stop = 120 * 1000;
		    					}else if (LoopCountSet - CountLoop == 7) {
		    						 Start = 60 * 1000; 
		    						  Stop = 60 * 1000;
		    					}else if (LoopCountSet - CountLoop == 8) {
		    						 Start = 60 * 1000; 
		    						  Stop = 60 * 1000;
		    					}else if (LoopCountSet - CountLoop == 9) {
		    						 Start = 60 * 1000; 
		    						  Stop = 60 * 1000;
		    					}else if (LoopCountSet - CountLoop == 10) {
		    						 Start = 30 * 1000; 
		    						  Stop = 30 * 1000;
		    					}else if (LoopCountSet - CountLoop == 11) {
		    						 Start = 30 * 1000; 
		    						  Stop = 30 * 1000;
		    					}
		    					
		    						isStart = false;
		    						isStop = false;
		    			        
		    						RestTime();
		    				}
		    				
		               }
		        });
		    		
		    }};
	        timer.schedule(scanTask,3000); 
	     
    	}

     }
     
    
    // Rest Time before go to doWiFiSet
     public void RestTime()
     {
     	if(true)
     	{
 		    scanTask = new TimerTask() {
 		    	public void run() {
 		    		handler.post(new Runnable() {
 		    			public void run(){
 		    				Receiver.reset();
 		    				Intent intentLoop = new Intent(context, LogService.class);
    						startService(intentLoop);
 		    				//doWifiScanSet();
    						start();
    						doWifiScan();
 		               }
 		        });
 		    		
 		    }};
 	        timer.schedule(scanTask,3000); 
 	     
     	}

      }
  
       
 // Start and Stop Serval  
    public void StartServalLoop()
    {
    	if(true)
    	{
		    scanTask = new TimerTask() {
		    	public void run() {
		    		handler.post(new Runnable() {
		    			public void run(){
		    				String StopCharge = "4s";
		    				//String StopCharge = "3s";
    						runUdpClient(StopCharge); 
    						LoopCheck = false;
    						Toast.makeText(getApplicationContext(), StopCharge+"", Toast.LENGTH_SHORT).show();
		    				stopServal1();
		               }
		        });
		    		
		    }};
	        timer.schedule(scanTask,7000); 
	     
    	}

     }

    
    public static void runUdpClient(String a)  {
	    String udpMsg = a;
	    DatagramSocket ds = null;
	    try {
	        ds = new DatagramSocket();
	        ds.setBroadcast(true);
	        
	        InetAddress serverAddr = InetAddress.getByName("28.184.3.85");
	        // 28.168.172.220
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


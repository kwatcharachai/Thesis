package org.servalproject.watcharachai.servalenergyanalysis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.PowerManager;
import java.util.Calendar;
import android.app.Service;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class IntentMonitorService extends Service {
	private WakeLock mWakeLock;

	public void onCreate() {
    	
	      final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		  this.mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "FULL_WAKE_LOCK");
		  this.mWakeLock.acquire();
		  
		   BroadcastReceiver receiver1 = new IntentMonitor();

			   IntentFilter filter = new IntentFilter();
			   filter.addAction(IntentMonitor.WIFI_MODE_ACTION);
			   this.registerReceiver(receiver1, filter);
		}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}

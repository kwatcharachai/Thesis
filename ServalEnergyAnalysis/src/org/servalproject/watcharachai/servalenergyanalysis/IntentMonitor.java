package org.servalproject.watcharachai.servalenergyanalysis;

import com.servalproject.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.Log;

// Watch for Serval Mesh WiFi change events, and also battery level status

public class IntentMonitor extends BroadcastReceiver {

	public static final String WIFI_MODE_ACTION = "org.servalproject.WIFI_MODE";
	public static final String EXTRA_NEW_MODE = "new_mode";	
	public static final String EXTRA_CHANGING = "changing";
		
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(WIFI_MODE_ACTION)) {
			String mode = intent.getStringExtra(EXTRA_NEW_MODE);
			Log.v("Mode", mode);

			boolean changing = intent.getBooleanExtra(
					EXTRA_CHANGING, false);
			Log.v("Change", changing + "");

			if (!changing) {			
				if (mode.equals("On")) log.logWiFiTurnedOn();
				if (mode.equals("Off")) log.logWiFiTurnedOff();
			}
		}
	}
}

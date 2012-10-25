package com.servalproject;

import android.util.Log;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IOIOFactory;
import ioio.lib.api.exception.ConnectionLostException;

public class ioio {
	Boolean PinData3, PinData4;
	private IOIOThread ioio_thread_;
	
	public ioio() {
		PinData3 = false;
		PinData4 = false;
	}
	
	public void setPinData(String pin)
	{
		if(pin.equals("3")){
			PinData3 = true;
		}
		else if (pin.equals("4")){
			PinData4 = true;
		}
		else if (pin.equals("3s")){
			PinData3 = false;
		}else if (pin.equals("4s")){
			PinData4 = false;
		}
	}

	public void startIO()
	{
		ioio_thread_ = new IOIOThread();
		ioio_thread_.start();
	}


	public void stopIO()
	{
		ioio_thread_.abort();
		try {
			ioio_thread_.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	class IOIOThread extends Thread {
		private IOIO ioio_;
		private boolean abort_ = false;
		private boolean ledState = false;
		
		@Override
		public void run() {
			super.run();
			while (true) {
				synchronized (this) {
					if (abort_) {
						break;
					}
					ioio_ = IOIOFactory.create();
				}
				try {
					
					ioio_.waitForConnect();
					
					DigitalOutput led = ioio_.openDigitalOutput(0,true);
					DigitalOutput led3 = ioio_.openDigitalOutput(3,true);
					DigitalOutput led4 = ioio_.openDigitalOutput(4,true);
					
					while (true) {
						Log.d("Layout","send");
						led3.write(PinData3);
						led4.write(PinData4);
						led.write(ledState);
						ledState=!ledState;
						
					sleep(1);
					}
					
				} catch (ConnectionLostException e) {
					e.printStackTrace();
				} catch (Exception e) {
					Log.e("HelloIOIOPower", "Unexpected exception caught", e);
					ioio_.disconnect();
					break;
				} finally {
					try {
						ioio_.waitForDisconnect();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		synchronized public void abort() {
			abort_ = true;
			if (ioio_ != null) {
				ioio_.disconnect();
			}
		}
		
	}
}

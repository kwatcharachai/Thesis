import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.os.Environment;

import com.servalproject.StartStop;


public class log {

	// The time span covered by the current logging episode
	private static long logStartTime = 0;
	private static long logEndTime = 0;
	private static long logTotalTime = 0;
	
	private final static int STATE_INVALID = -1; // used to mark end of experiment
	private final static int STATE_OFF = 0;
	private final static int STATE_RAMPUP = 1;
	private final static int STATE_ON = 2;
	private final static int STATE_RAMPDOWN = 3;
	// The time spent in each of the above states
	private static long stateTimes[] = {0,0,0,0};
	// Count how many times we enter each state
	private static long stateCounts[] = {0,0,0,0};
	private static int currentState=-1;
	private static long currentStateBegan=0;
	
	public static void logReset()
	{
		logStartTime=0;
		logEndTime=0;
		currentState=-1;
		currentStateBegan=0;
		for(int i=0;i<4;i++) stateTimes[i]=0;
	}
	
	public static void logBegin() 
	{	
		logStartTime=System.currentTimeMillis();
	}
	
	private static void logStateTime(int newState)
	{
		long now=System.currentTimeMillis();
		if (currentState>-1&&logStartTime!=0&&currentStateBegan!=0)
		{
			long currentStateInterval=now-currentStateBegan;
			stateTimes[currentState]+=currentStateInterval;
		}
		currentStateBegan=now;
		currentState=newState;
		stateCounts[newState]++;
	}
	
	public static void logWiFiTurnedOn() 
	{
		logStateTime(STATE_ON);
	}
	
	public static void logWiFiTurnedOff()
	{
		logStateTime(STATE_OFF);
	}
	
	public static void logWiFiStartCalled()
	{
		logStateTime(STATE_RAMPUP);
	}
	
	public static void logWiFiStopCalled()
	{
		logStateTime(STATE_RAMPDOWN);
	}
	
	public static void logEnd() {
		// Record the time in the current state until logEnd was called.
		logStateTime(STATE_INVALID);
		logEndTime=System.currentTimeMillis();
		logTotalTime=logEndTime-logStartTime;
		// And then mark experiment as over, so that we don't log anything more.
		logStartTime=0;
	}
	
	public static void logWrite() {
		try{
			// check for SDcard   
            File root = Environment.getExternalStorageDirectory();  
            
            if (root.canWrite()){  
                File fileDir = new File(root.getAbsolutePath()+"/PowerLog/");  
                fileDir.mkdirs();  
               
                File file= new File(fileDir, "runtimes.log");
                boolean alreadyExists = file.exists();
			
                FileWriter fstream = new FileWriter(file,true);  
                BufferedWriter out = new BufferedWriter(fstream);
			
                if (!alreadyExists) {
                	out.write("totaltime\ttime.off\ttime.rampup\ttime.on\ttime.rampdown\tcount.off\tcount.rampup\tcount.on\tcount.rampdown\n");
                }
            	out.write(logTotalTime+"\t"
            			+stateTimes[STATE_OFF]+"\t"
            			+stateTimes[STATE_RAMPUP]+"\t"
            			+stateTimes[STATE_ON]+"\t"
            			+stateTimes[STATE_RAMPDOWN]+"\t"
            			+stateCounts[STATE_OFF]+"\t"
            			+stateCounts[STATE_RAMPUP]+"\t"
            			+stateCounts[STATE_ON]+"\t"
            			+stateCounts[STATE_RAMPDOWN]
            			+"\n");
            	out.close();
            }
            
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}

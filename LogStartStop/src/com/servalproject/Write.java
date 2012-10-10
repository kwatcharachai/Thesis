package com.servalproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class Write {
	
	public static long AllCountSum;
	
	public void writefile(String sDate, String logLine) {
		//double FirstStartTime;
		String Name = StartStop.LogName + "_TotalTime.txt";
		File root = null;
			
        try {  
  
            // check for SDcard   
            root = Environment.getExternalStorageDirectory();  
  
            //check sdcard permission  
            if (root.canWrite()){  
                File fileDir = new File(root.getAbsolutePath()+"/Result/");  
                fileDir.mkdirs();  
 
                File file= new File(fileDir, Name);  
                Log.v("test",Name);
                FileWriter filewriter = new FileWriter(file,true);  
                BufferedWriter out = new BufferedWriter(filewriter);
                
                	out.write(sDate);
                	out.write("Level Scale Volt Temp1 Temp2 Plug Health \n");
                	out.write(logLine);
                	out.write("\n");
                		if (LogService.CurrentBatt <= LogService.MinLevel)
                		{		
                			long EndStoptTime = System.currentTimeMillis();
                			AllCountSum = ((EndStoptTime - LogService.FirstStartTime)) ;
                			
                			// For Check TotalTime
                			out.write("Total Time = " + (AllCountSum) + " Sec \n");
                			out.write("\n \n");
                		}
                	out.close();  
            }  
            
        	} catch (IOException e) {  
        		Log.e("ERROR:---", "Could not write file to SDCard" + e.getMessage());  
        	} 
        	
}	

}

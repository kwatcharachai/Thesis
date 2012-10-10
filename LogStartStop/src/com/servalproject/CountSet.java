package com.servalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class CountSet extends Activity {
	
	public static String ReceptMax, ReceptMin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.countset);
		
		final EditText StartCount = (EditText) findViewById(R.id.editStartCount);
        final EditText StopCount = (EditText) findViewById(R.id.editStopCount);
        
        ReceptMax = getIntent().getStringExtra("TextMaxInput");
        ReceptMin = getIntent().getStringExtra("TextMinInput");
        
        final EditText loopCount = (EditText) findViewById(R.id.editLoop);
		
		Button countButton = (Button) findViewById(R.id.CountButton);
		countButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent goStartStop = new Intent(CountSet.this,StartStop.class);
				goStartStop.putExtra("TextStartCountInput",StartCount.getText().toString());
				goStartStop.putExtra("TextStopCountInput",StopCount.getText().toString());
				goStartStop.putExtra("TextLoopCountInput",loopCount.getText().toString());
		        startActivity(goStartStop);		
				
			}
		});
		
		Button c_BackButton = (Button) findViewById(R.id.C_BackButton);
		c_BackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				finish();	
			}
		});
			
	}
	
}

package com.servalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Main extends Activity {
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final EditText TextMax = (EditText) findViewById(R.id.editMax);
        final EditText TextMin = (EditText) findViewById(R.id.editMin);
		
		Button percentButton = (Button) findViewById(R.id.PercentButton);
		percentButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent goCountSet = new Intent(Main.this,CountSet.class);
				goCountSet.putExtra("TextMaxInput",TextMax.getText().toString());
				goCountSet.putExtra("TextMinInput",TextMin.getText().toString());
		        startActivity(goCountSet);		
				
			}
		});
    }
    

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.runFinalizersOnExit(true);
		System.exit(0);
	}
    
}
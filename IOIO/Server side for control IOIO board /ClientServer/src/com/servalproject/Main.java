package com.servalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Main extends Activity {
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        addListenerOnButton();        
     }
    
    public void addListenerOnButton() {
    	final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
    	Button ok = (Button) findViewById(R.id.button1);
     
    	ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int selectedId = radioGroup.getCheckedRadioButtonId();
					Log.d("test",selectedId + "");
				
			        RadioButton radioButton = (RadioButton) findViewById(selectedId);
	 
			        // Show results of radioButton 
			        // Toast.makeText(Main.this,
				   // radioButton.getText(), Toast.LENGTH_SHORT).show();
				
			        Intent goShow = new Intent(Main.this,Show.class);
			        goShow.putExtra("Showradio",(String) radioButton.getText());
			        startActivity(goShow);			        
			        
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
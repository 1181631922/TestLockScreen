package com.example.testlockscreen;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class MainActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
    }  
  
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //停止监听screen状态  
    }  
    
    public void doDrawPasssword(View view){
    	Intent intent = new Intent();
    	intent.setClass(MainActivity.this, GestureLockAty.class);
    	intent.putExtra("isSetting", true);
    	startActivity(intent);
    }
    
    public void IntentActivity(View view){
    	Intent intent = new Intent();
    	intent.setClass(MainActivity.this, BAty.class);
    	startActivity(intent);
    }
    
}

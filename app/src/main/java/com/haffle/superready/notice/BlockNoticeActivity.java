package com.haffle.superready.notice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.TextView;

import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.R;

public class BlockNoticeActivity extends FragmentActivity {
	
	ActivityManager activityManager = ActivityManager.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_block_notice);
		
		activityManager.addActivity(this);
		
		Intent intent = getIntent();
		String message = intent.getStringExtra("MESSAGE");
		
		TextView textView_notice = (TextView)findViewById(R.id.blockNotice_text);
		textView_notice.setText(message);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityManager.deleteTopActivity();
	}
}

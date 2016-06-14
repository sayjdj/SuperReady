package com.haffle.superready.notice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.R;

public class UpdateNoticeActivity extends FragmentActivity {
	
	ActivityManager activityManager = ActivityManager.getInstance();
	Button button_upDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_update_notice);
		
		activityManager.addActivity(this);

		setView();
		setClickListener();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityManager.deleteTopActivity();
	}

	void setView() {
		button_upDate = (Button)findViewById(R.id.updateNotice_button);
	}

	void setClickListener() {
		button_upDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.haffle.superready")));
			}
		});
	}
}

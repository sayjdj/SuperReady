package com.haffle.superready.intro;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.haffle.superready.manager.ActivityManager;
import com.haffle.superready.manager.InfoManager;
import com.haffle.superready.R;
import com.haffle.superready.user.UserVisit;

import io.fabric.sdk.android.Fabric;

public class Intro extends FragmentActivity {

    ActivityManager activityManager = ActivityManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(InfoManager.flagCrashLytics) {
            Fabric.with(this, new Crashlytics());
        }
        setContentView(R.layout.activity_intro);

        activityManager.addActivity(this);

        InfoManager.SetData(getApplicationContext());   // url, sharedPreference key, user 정보 등을 설정

        // user가 방문했음을 알린다
        UserVisit userVisit = new UserVisit(this, getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityManager.deleteTopActivity();
    }

    @Override
    public void onBackPressed() {
    }
}
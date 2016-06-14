package com.haffle.superready.manager;

import java.util.ArrayList;

import android.app.Activity;

public class ActivityManager {
	
	private static ActivityManager activityManager = null;
	private ArrayList<Activity> activityList = null;
	
	private ActivityManager() {
		activityList = new ArrayList<Activity>();
	}
	
	public static ActivityManager getInstance() {
		
		if(ActivityManager.activityManager == null) {
			activityManager = new ActivityManager();
		}
		return activityManager;
	}
	
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	
	public void finishAllActivityExceptOne() {
		for(int i=0; i < (activityList.size() - 1); i++) {
			activityList.get(i).finish();
		}
	}
	
	public void finishAllActivity() {
		for(int i=0; i < (activityList.size()); i++) {
			activityList.get(i).finish();
		}
	}
	
	public void deleteTopActivity() {
		activityList.remove(activityList.size() - 1);
	}
	
	public int getActivitySize() {
		return activityList.size();
	}
}

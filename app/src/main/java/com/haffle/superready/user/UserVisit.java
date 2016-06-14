package com.haffle.superready.user;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.haffle.superready.manager.InfoManager;

public class UserVisit {

	Activity activity;
	Context context;

	public UserVisit(Activity activity, Context context) {
		this.activity = activity;
		this.context = context;

		visitCount();
	}

	public void visitCount() {
		SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);

		// 사용자 등록을 했는지 확인
		Boolean isRegister = mPref.getBoolean(InfoManager.SHARED_PREFERENCE_USERREGISTRATION, false);
		// user의 방문 횟수
		int count = Integer.parseInt(mPref.getString(InfoManager.SHARED_PREFERENCE_USERVISITCOUNT, "0"));

		// 사용자 등록을 하지 않았을 경우 사용자 등록을 한다.
		if(!isRegister) {
			UserRegistration userRegistration = new UserRegistration(context);
		}

		// 앱 실행시 방문 사실을 server에 전송
		UserEnter userEnter = new UserEnter(activity, context);
		userEnter.execute();

		// 방문 횟수를 증가하여 저장
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString(InfoManager.SHARED_PREFERENCE_USERVISITCOUNT, ++count + "");
		editor.commit();
	}
}
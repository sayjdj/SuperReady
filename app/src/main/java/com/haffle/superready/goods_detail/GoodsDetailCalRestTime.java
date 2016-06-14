package com.haffle.superready.goods_detail;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

public class GoodsDetailCalRestTime {

	Context context;
	Handler handler;

	Calendar calendar;
	TextView textView;

	GoodsDetailCalRestTime(Context context, TextView textView, Calendar calendar) {
		this.context = context;
		this.textView = textView;
		this.calendar = calendar;
		
		setRestTime();
	}

	void setRestTime() {

		handler = new Handler();

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Calendar curCalendar = Calendar.getInstance();
				Calendar restCalendar = calendar;

				final String restTime = calRestTime((restCalendar.getTimeInMillis()
						- curCalendar.getTimeInMillis()) / 1000);

				final TextView textView_restTime = textView;

				handler.post(new Runnable() {
					public void run() {
						textView_restTime.setText(restTime);								
					}
				});
			}
		};

		Timer timer = new Timer();
		timer.schedule(timerTask, 0, 1000);
	}

	String calRestTime(long restTime) {
		
		if(restTime < 0) {
			return "행사가 마감되었습니다";
		}
		
		String restTime_string = "";

		restTime_string += restTime/(60*60*24) + "일 ";
		restTime %= (60*60*24);
		restTime_string += restTime/(60*60) + "시간 ";
		restTime %= (60*60);
		restTime_string += restTime/(60) + "분 ";
		restTime %= 60;
		restTime_string += restTime + "초 남음";

		return restTime_string;
	}
}
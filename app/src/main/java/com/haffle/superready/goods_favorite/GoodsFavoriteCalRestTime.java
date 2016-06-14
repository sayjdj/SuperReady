package com.haffle.superready.goods_favorite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

public class GoodsFavoriteCalRestTime {

	Context context;
	Handler handler;

	ArrayList<Calendar> calendar = new ArrayList<Calendar>();
	ArrayList<TextView> textView = new ArrayList<TextView>();

	GoodsFavoriteCalRestTime(Context context, ArrayList<TextView> textView, ArrayList<Calendar> calendar) {
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
				for(int i=0; i<textView.size(); i++) {
					Calendar curCalendar = Calendar.getInstance();
					Calendar restCalendar = calendar.get(i);

					final String restTime = calRestTime((restCalendar.getTimeInMillis()
							- curCalendar.getTimeInMillis()) / 1000);
					
					final TextView textView_restTime = textView.get(i);

					handler.post(new Runnable() {
						public void run() {
							textView_restTime.setText(restTime);								
						}
					});
				}
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
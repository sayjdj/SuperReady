package com.haffle.superready.goods_list;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.haffle.superready.item.Goods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class GoodsCalRestTime {

    Context context;
    Handler handler;
    RestTime restTime;

    GoodsCalRestTime(Context context) {
        this.context = context;

        restTime = new RestTime();
        setRestTime();
    }

    void setRestTime() {

        handler = new Handler(Looper.getMainLooper());

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        for (int i = 0; i < restTime.calendars.size(); i++) {

                            Calendar curCalendar = Calendar.getInstance();
                            Calendar restCalendar = restTime.calendars.get(i);

                            String restMilliTime = calRestTime((restCalendar.getTimeInMillis()
                                    - curCalendar.getTimeInMillis()) / 1000);

                            restTime.textView_restTime.get(i).setText(restMilliTime);
                        }
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    void addRestTime(Goods goods, TextView textView_restTime) {
        boolean overlap = false;

        // goods이 중복되있는지 확인
        for (int i = 0; i < restTime.calendars.size(); i++) {
            if (restTime.goodses.get(i).equals(goods)) {  // 중복일 경우
                overlap = true;
                restTime.textView_restTime.set(i, textView_restTime);
                break;
            }
        }

        // 중복이 아닐 경우에만 추가
        if (!overlap) {
            restTime.goodses.add(goods);
            restTime.calendars.add(dateParsing(goods.getCampaign().getEnd()));
            restTime.textView_restTime.add(textView_restTime);
        }
    }

    private String calRestTime(long restTime) {
        String restTime_string = "";

        if (restTime / (60 * 60 * 24) == 0) {
            restTime %= (60 * 60 * 24);
            restTime_string += restTime / (60 * 60) + "시간 ";
            restTime %= (60 * 60);
            restTime_string += restTime / (60) + "분 ";
            restTime %= 60;
            restTime_string += restTime + "초 남음";
        } else {
            restTime_string += restTime / (60 * 60 * 24) + "일 남음";
        }

        return restTime_string;
    }

    private Calendar dateParsing(String string) {
        StringTokenizer str = new StringTokenizer(string, "- T : .");
        String[] date = new String[7];

        for (int i = 0; str.hasMoreTokens(); i++) {
            date[i] = str.nextToken();
        }

        Calendar cal;
        cal = Calendar.getInstance();
        cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1,
                Integer.parseInt(date[2]), Integer.parseInt(date[3]),
                Integer.parseInt(date[4]), Integer.parseInt(date[5]));    // 연, 월(-1해야함), 일, 시간, 분, 초
        cal.add(Calendar.HOUR, 9);

        return cal;
    }

    class RestTime {
        ArrayList<Goods> goodses = new ArrayList<Goods>();
        ArrayList<Calendar> calendars = new ArrayList<Calendar>();
        ArrayList<TextView> textView_restTime = new ArrayList<TextView>();
    }
}
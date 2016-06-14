package com.haffle.superready.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.haffle.superready.db.AlarmDBManager;
import com.haffle.superready.item.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public class BootReceiver extends BroadcastReceiver {

    Context context;
    AlarmDBManager alarmDBManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        alarmDBManager = new AlarmDBManager(context, "ALARM.db", null, 1);

        ArrayList<Alarm> list = alarmDBManager.select();

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            for (int i = 0; i < list.size(); i++) {
                setAlarm(list.get(i).getGoodsId(), list.get(i).getGoodsName(), list.get(i).getRestTime());
            }
        }
    }

    void setAlarm(String goodsId, String goodsName, String time) {

        Calendar restTime = dateParsing(time);

        restTime.set(restTime.get(Calendar.YEAR),
                restTime.get(Calendar.MONTH),
                restTime.get(Calendar.DATE) - 1,
                restTime.get(Calendar.HOUR_OF_DAY),
                restTime.get(Calendar.MINUTE),
                restTime.get(Calendar.SECOND));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);

        intent.putExtra("TITLE", goodsName);
        intent.putExtra("TEXT", "특가 세일기간이 하루 남았습니다!!!");
        intent.putExtra("GOODSID", goodsId);

        PendingIntent pIntent = PendingIntent.getBroadcast(context, Integer.parseInt(goodsId), intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, restTime.getTimeInMillis(), pIntent);
    }

    Calendar dateParsing(String string) {
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

        return cal;
    }
}

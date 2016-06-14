package com.haffle.superready.alarm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.haffle.superready.db.AlarmDBManager;
import com.haffle.superready.market.MainActivity;
import com.haffle.superready.R;

public class AlarmReceiver extends BroadcastReceiver {
	
	Context context;
	String title;
	String text;
	String id;
	AlarmDBManager alarmDBManager;
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	this.context = context;
		alarmDBManager = new AlarmDBManager(context, "ALARM.db", null, 1);
    	
        title = intent.getStringExtra("TITLE");
        text = intent.getStringExtra("TEXT");
    	id = intent.getStringExtra("GOODSID");
    	
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			notificationJellyBean();	
		} else {
			notificationIceCreamSandwich();	
		}
		
		alarmDBManager.deleteWithGoodsId(Integer.parseInt(id));
    }
    
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	void notificationJellyBean() {
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification.Builder mBuilder = new Notification.Builder(context);
		mBuilder.setSmallIcon(R.drawable.superready_notification);
		mBuilder.setTicker("슈퍼레디 특가가 얼마 하루 남았습니다!!!");
		mBuilder.setWhen(System.currentTimeMillis());
		mBuilder.setContentTitle(title);
		mBuilder.setContentText(text);
		mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		mBuilder.setContentIntent(pendingIntent);
		mBuilder.setAutoCancel(true);
		 
		nm.notify(Integer.parseInt(id), mBuilder.build());
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	void notificationIceCreamSandwich() {
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		 
		NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(context);
		mCompatBuilder.setSmallIcon(R.drawable.superready_notification);
		mCompatBuilder.setTicker("NotificationCompat.Builder");
		mCompatBuilder.setWhen(System.currentTimeMillis());
		mCompatBuilder.setContentTitle(title);
		mCompatBuilder.setContentText(text);
		mCompatBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
		mCompatBuilder.setContentIntent(pendingIntent);
		mCompatBuilder.setAutoCancel(true);
		 
		nm.notify(Integer.parseInt(id), mCompatBuilder.build());
	}
}

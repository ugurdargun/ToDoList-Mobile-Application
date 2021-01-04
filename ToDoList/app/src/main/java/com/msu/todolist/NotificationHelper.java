package com.msu.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

public class NotificationHelper {

    public static Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void newNotification(long id, Calendar cal){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, (int) id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }

    public void cancelNotification(long id){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, (int) id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(broadcast);
    }

}

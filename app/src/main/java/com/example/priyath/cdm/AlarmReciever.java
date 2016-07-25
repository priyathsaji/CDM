package com.example.priyath.cdm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/*
 * Created by PRIYATH SAJI on 23-07-2016.
 *
 * This is a reciever which is activates when the serAlarm() function of the this class is
 * called.once the setAlarm fuction is called the onReciver function of the reciever is called
 * once a day
 */
public class AlarmReciever extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,wakeUpService.class);
        startWakefulService(context,intent1);
    }


    public AlarmManager alarmManager;
    public PendingIntent alarmIntent;

    public void setAlarm(Context context){
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AlarmReciever.class);
        alarmIntent = PendingIntent.getBroadcast(context,0,intent,0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,20);
        calendar.set(Calendar.MINUTE,47);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,alarmIntent);

    }

    public void cancelAlarm(Context context){
        if(alarmManager != null){
            alarmManager.cancel(alarmIntent);
        }
    }
}

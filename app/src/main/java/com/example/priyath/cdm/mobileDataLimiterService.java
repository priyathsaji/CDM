package com.example.priyath.cdm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;


/*
 * Created by priyath on 22/4/16.
 */
public class mobileDataLimiterService extends Service {
    private gMobileData data = new gMobileData();
    private Handler handler = new Handler();
    private long limitReached;
    int checker;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        getData();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "MobileData notification Disabled", Toast.LENGTH_LONG).show();
    }

    void getData(){
        MainActivity mainActivityObject = new MainActivity();
        data = mainActivityObject.getMobileLimiterData();
    }


    private Runnable looper = new Runnable() {
        @Override
        public void run() {
            try{
                if(checker < 4) {
                    checker = checking();
                }else if(checker == 1) {
                    limitOverNotification();
                }else if(checker == 2){
                    limitReached = TrafficStats.getMobileRxBytes();
                    limitReachedNotification();
                }else if(checker == 3){
                    limitReached = TrafficStats.getMobileRxBytes();
                    limitGoingToBeOverNotification();
                }
            }catch(Exception e){
                Log.i("Some error in checking"," error");

            }finally{

                handler.postDelayed(looper,1000);
            }

        }
    };


    private void limitOverNotification(){
        long exededData;
        int b = 0;
        String[] unitArray = new String[]{
                "B", "KB", "MB", "GB"
        };



        exededData = TrafficStats.getMobileRxBytes() - limitReached;

        if(exededData < 1024){

            b=0;
        } else if (exededData > 1073741824){
            exededData /= 1073741824;
            b=3;
        } else if (exededData > (1048576)) {
            exededData /=  1048576;
            b=2;
        } else if (exededData > 1024) {
            exededData /= 1024;
            b=1;
        }


        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Details.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.InboxStyle(new Notification.Builder(this)
                .setContentTitle("Your Mobile Data excede the limit ")
                .setSmallIcon(R.drawable.mobiledata)
                .setContentText("Data Exceded  : " + String.valueOf(exededData) + unitArray[b] )
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(resultPendingIntent))
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(2, notification);


    }

    private void limitReachedNotification(){

    }

    private void limitGoingToBeOverNotification(){

    }



    int checking(){
        long dataUsed,limit;
        limit = Long.parseLong(data.limit);
        dataUsed = TrafficStats.getMobileRxBytes()-data.startMobileData;

        if(dataUsed > limit){
            return 1;
        }else if(dataUsed == limit){
            return 2;

        }else if(dataUsed > (limit*0.9)){
            return 3;
        }

        return 0;
    }


}


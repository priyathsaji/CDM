package com.example.priyath.cdm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;



/*
 * Created by priyath on 17/4/16.
 */
public class MyService extends Service {
    public String unitArray[];
    public Handler mHandler;
    public static long wifiD,wifiS;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mHandler = new Handler();
        wifiD = TrafficStats.getTotalRxBytes()- TrafficStats.getMobileRxBytes();
        startRepeatingTask();

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {


              checking();


            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                int mInterval = 1000;
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    /*void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }*/

    void checking(){
        WifiManager wifi = (WifiManager)getSystemService(WIFI_SERVICE);

        if(wifi.isWifiEnabled()) {
            displayNotification();
        }else{
            wifiD = TrafficStats.getTotalRxBytes()- TrafficStats.getMobileRxBytes();
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager)getSystemService(ns);
            nMgr.cancel(0);
        }

    }


    void displayNotification(){


            unitArray = new String[]{
                    "B", "KB", "MB", "GB"
            };

            int k1 =1,k2=1;
            int a =0,b=0;
            double wifiData;
            long wifiDataDownloaded = (TrafficStats.getTotalRxBytes()-TrafficStats.getMobileRxBytes())-wifiD;

            if(wifiDataDownloaded < 1024){
                k1 = 1;
                a=0;
            } else if (wifiDataDownloaded > 1073741824){
                k1 = 1073741824;
                a=3;
            } else if (wifiDataDownloaded > (1048576)) {
                k1 =  1048576;
                a=2;
            } else if (wifiDataDownloaded > 1024) {
                k1 = 1024;
                a=1;
            }

            wifiData = wifiDataDownloaded / k1;
            wifiS = wifiDataDownloaded - wifiS;

            //wifiData = Math.round((wifiData*100)/100);

        if(wifiS < 1024){
            k2 = 1;
            b=0;
        } else if (wifiS > 1073741824){
            k2 = 1073741824;
            b=3;
        } else if (wifiS > (1048576)) {
            k2 =  1048576;
            b=2;
        } else if (wifiS > 1024) {
            k2 = 1024;
            b=1;
        }


            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(Details.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.InboxStyle(new Notification.Builder(this)
                    .setContentTitle("Wifi Details")
                    .setSmallIcon(R.drawable.wifidata)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(resultPendingIntent))
                    .addLine("Data Downloaded : " + wifiData + unitArray[a]  )
                    .addLine("Download Speed  : " + String.valueOf(wifiS/k2) + unitArray[b] + "/s")
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
            wifiS = wifiDataDownloaded;
            // Adds the Intent that starts the Activity to the top of the stack
    }



}

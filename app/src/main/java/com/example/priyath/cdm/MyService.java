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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;


/*
 * Created by priyath on 17/4/16.
 */
public class MyService extends Service {
    public String unitArray[];
    public Handler mHandler;
    public long wifiD,wifiS;
    boolean wifiEnabled;
    int flag =0;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mHandler = new Handler();
        wifiD = TrafficStats.getTotalRxBytes()-TrafficStats.getMobileRxBytes();
        startRepeatingTask();

        Toast.makeText(this, "wifi notification Enabled", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(getBaseContext(), MyMobileService.class));

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        settings set = new settings();
        if(set.serviceEndWifiStatus()){
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager)getSystemService(ns);
            nMgr.cancel(0);
            stopRepeatingTask();
            Toast.makeText(this, "wifi notification Disabled", Toast.LENGTH_SHORT).show();
            stopService(new Intent(getApplicationContext(),MyService.class));
        }
        else{
            startService(new Intent(getApplicationContext(),MyService.class));
        }



    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                wifiEnabled = false;
                checking();
                if(wifiEnabled) {
                    if(flag == 1)
                        wifiD = TrafficStats.getTotalRxBytes()-TrafficStats.getMobileRxBytes();
                    Log.i("something is wrong here",String.valueOf(TrafficStats.getMobileRxBytes()));
                    displayNotification();
                }else {
                    flag=0;
                    String ns = Context.NOTIFICATION_SERVICE;
                    NotificationManager nMgr = (NotificationManager)getSystemService(ns);
                    nMgr.cancel(0);



                }

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

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    void checking() {
        WifiManager manager = (WifiManager)getSystemService(WIFI_SERVICE);
        wifiEnabled = manager.isWifiEnabled();


    }







    void displayNotification(){


        unitArray = new String[]{
                "B", "KB", "MB", "GB"
        };

        int k1 =1,k2=1;
        int a =0,b=0;
        double wifi;
        long wifiDownloaded = (TrafficStats.getTotalRxBytes()-TrafficStats.getMobileRxBytes())-wifiD;

        if(wifiDownloaded < 1024){
            k1 = 1;
            a=0;
        } else if (wifiDownloaded > 1073741824){
            k1 = 1073741824;
            a=3;
        } else if (wifiDownloaded > (1048576)) {
            k1 =  1048576;
            a=2;
        } else if (wifiDownloaded > 1024) {
            k1 = 1024;
            a=1;
        }

        wifi = wifiDownloaded / k1;
        if(flag != 1 && flag != 0)
            wifiS = wifiDownloaded - wifiS;

        //wifi = Math.round((wifi*100)/100);

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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("wifi Data Details")
                .setSmallIcon(R.drawable.wifidata)
                .setContentText("Downloaded : " + wifi + unitArray[a] + "        Speed  : " + String.valueOf(wifiS/k2) + unitArray[b] + "/s")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
        wifiS = wifiDownloaded;
        flag++;

    }



}

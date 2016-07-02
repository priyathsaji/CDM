package com.example.priyath.cdm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/*
 * Created by PRIYATH SAJI on 18-06-2016.
 */
public class wifiService extends Service {

    public Handler handler = new Handler();
    long wPrevious,wNow,wSpeed,wpData,wpUploaded;
    static long wnData,wnUploaded;
    public static boolean refreshed;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId){

        updatewpData();
        wPrevious = TrafficStats.getTotalRxBytes()-TrafficStats.getMobileRxBytes();
        wNow = wPrevious;
        startRun();

        return START_STICKY;
    }



    Runnable run = new Runnable() {
        @Override
        public void run() {
            if(refreshed)
                updatewpData();
            wnData = (TrafficStats.getTotalRxBytes()-TrafficStats.getMobileRxBytes())-wpData;
            wnUploaded =(TrafficStats.getTotalTxBytes()-TrafficStats.getMobileTxBytes())-wpUploaded;
            updatewnData();
            wSpeed = wNow - wPrevious;
            wPrevious = wNow;
            wNow = TrafficStats.getTotalRxBytes()-TrafficStats.getMobileRxBytes();
            updateWifiNotification();


            handler.postDelayed(run,1000);

        }
    };
    public void updatewpData(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if(refreshed){
            wpData = TrafficStats.getTotalRxBytes()-TrafficStats.getMobileRxBytes();
            wpUploaded = TrafficStats.getTotalTxBytes()-TrafficStats.getMobileTxBytes();
            editor.putLong("wpUploaded",wpUploaded);
            editor.putLong("wpData",wpData);
            editor.apply();
            refreshed = false;
        }else{

            wpData = preferences.getLong("wpData",0);
        }
    }
    public void updatewnData(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("wnData",wnData);

        editor.apply();

    }

    void stopRun(){
        handler.removeCallbacks(run);
    }

    void startRun(){
        run.run();
    }


    void updateWifiNotification(){
        int k=1,b=0,k1=1,b1=0;

        String [] unitArray = new String[]{
                "B", "KB", "MB", "GB"
        };

        if(wSpeed < 1024){
            k = 1;
            b=0;
        } else if (wSpeed > 1073741824){
            k = 1073741824;
            b=3;
        } else if (wSpeed > (1048576)) {
            k =  1048576;
            b=2;
        } else if (wSpeed > 1024) {
            k = 1024;
            b=1;
        }

        if(wnData < 1024){
            k1 = 1;
            b1=0;
        } else if (wnData > 1073741824){
            k1 = 1073741824;
            b1=3;
        } else if (wnData > (1048576)) {
            k1 =  1048576;
            b1=2;
        } else if (wnData > 1024) {
            k1 = 1024;
            b1=1;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Wifi Notification")
                .setOngoing(true)
                .setPriority(5)
                .setSmallIcon(R.drawable.ic_network_wifi_white_24dp)
                .setContentText("Download Speed: " + wSpeed/k +" " + unitArray[b] +"/s"+"  "+"Downloaded: " + wnData/k1 +" "+unitArray[b1]);

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        stackBuilder.addParentStack(MainActivity.class);


        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);


        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1,builder.build());
    }

    public long getWifiDataDownloaded(){
        return wnData;
    }
    public long getWifiDataUploaded(){
        return wnUploaded;
    }

    public void onDestroy(){
        stopRun();
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);

    }


    public void refresh(){

        refreshed = true;


    }
}

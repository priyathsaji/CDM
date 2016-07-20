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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/*
 * Created by PRIYATH SAJI on 19-06-2016.
 */
public class mobileDataService extends Service {
    public Handler handler = new Handler();
    long mdPrevious,mdNow,mdSpeed,mdpData,mpUploaded;
    static long mdnData,mnUploaded;
    public static boolean refreshed;
    public boolean update = false;
    public limiterdata ldata;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        updatemdpData();
        mdPrevious = TrafficStats.getMobileRxBytes();
        mdNow = mdPrevious;
        getLimiterDataObject();
        startRun();

        return START_STICKY;
    }



    Runnable run = new Runnable() {
        @Override
        public void run() {
            if(refreshed)
                updatemdpData();
            mdnData = TrafficStats.getMobileRxBytes()-mdpData;
            mnUploaded = TrafficStats.getMobileTxBytes() - mpUploaded;
            updatemdnData();
            mdSpeed = mdNow - mdPrevious;
            mdPrevious = mdNow;
            mdNow = TrafficStats.getMobileRxBytes();
            updateWifiNotification();
            if(ldata.isLimiterSet)
                checkinglimiterData();


            handler.postDelayed(run,1000);

        }
    };
    public void updatemdpData(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if(refreshed){
            mdpData = TrafficStats.getMobileRxBytes();
            mpUploaded = TrafficStats.getMobileTxBytes();
            editor.putLong("mpUploaded",mpUploaded);
            editor.putLong("mdpData",mdpData);
            editor.apply();
            refreshed = false;
        }else{

            mdpData = preferences.getLong("mdpData",0);
            mpUploaded = preferences.getLong("mpUploaded",0);
        }
    }
    public void updatemdnData(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("mdnData",mdnData);

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

        if(mdSpeed < 1024){
            k = 1;
            b=0;
        } else if (mdSpeed > 1073741824){
            k = 1073741824;
            b=3;
        } else if (mdSpeed > (1048576)) {
            k =  1048576;
            b=2;
        } else if (mdSpeed > 1024) {
            k = 1024;
            b=1;
        }

        if(mdnData < 1024){
            k1 = 1;
            b1=0;
        } else if (mdnData > 1073741824){
            k1 = 1073741824;
            b1=3;
        } else if (mdnData > (1048576)) {
            k1 =  1048576;
            b1=2;
        } else if (mdnData > 1024) {
            k1 = 1024;
            b1=1;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Mobile Data ")
                .setOngoing(true)
                .setPriority(5)
                .setSmallIcon(R.drawable.ic_network_cell_white_24dp)
                .setContentText("Download Speed: " + mdSpeed/k +" " + unitArray[b] +"/s"+"  "+"Downloaded: " + mdnData/k1 +" "+unitArray[b1]);

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        stackBuilder.addParentStack(MainActivity.class);


        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);


        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1,builder.build());


    }

    public void onDestroy(){
        stopRun();
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1);
        manager.cancel(2);

    }
    public long getmobileDataDownloaded(){
        return mdnData;
    }
    public long getmobileDataUPloaded(){ return mnUploaded; }

    public void refresh(){

        refreshed = true;


    }

    public void checkinglimiterData(){
        long dataLeft;
        long limit=0;
        if(ldata.unit.equals("GB")){
            limit = ldata.limit*1073741824;
        }else if(ldata.unit.equals("MB")){
            limit = ldata.limit*1048576;
        }

        dataLeft = limit - (TrafficStats.getMobileRxBytes()-ldata.dataDownloaded);


        if(dataLeft <= 0){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle("Data Usage Warning!!!")
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_network_cell_white_24dp)
                    .setContentText("The mobile data limit is exceeded");

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(intent);
            stackBuilder.addParentStack(MainActivity.class);


            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);


            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.notify(2,builder.build());
            ldata.isLimiterSet =false;
            if(!update){
                updateLimiterDataObject();
                update = true;
            }

        }else if(dataLeft<=(100*1048576)) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle("Data Usage Warning!!!")
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_network_cell_white_24dp)
                    .setContentText("The limit is going to exceed");

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(intent);
            stackBuilder.addParentStack(MainActivity.class);


            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);


            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(2, builder.build());
        }
    }


    public void getLimiterDataObject(){

        try {
            FileInputStream in = openFileInput("limiterdata");
            ObjectInputStream ois = new ObjectInputStream(in);

            ldata = (limiterdata)ois.readObject();

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    public void updateLimiterDataObject(){
        try {
            FileOutputStream out = openFileOutput("limiterdata",MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(ldata);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

package com.example.priyath.cdm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Created by PRIYATH SAJI on 23-07-2016.
 *
 * This is a service which is called by the AlarmReciever when it is the time to check
 * This is called once a day at 12 am
 * This service check whether the end date of the mobile data limiter is equal to todays date
 */
public class wakeUpService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flag, int startId){


        //reading today's date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd / MMM / yyyy");
        String todaysDate =  dateFormat.format(calendar.getTime());
        limiterdata ldata = new limiterdata();



        //reading the Mobile data limiter data from a file limiter data which is
        //included while setting the limiter
        FileInputStream in = null;
        try {
            in = openFileInput("limiterdata");
            ObjectInputStream ois = new ObjectInputStream(in);

            ldata = (limiterdata)ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        Log.i("eData", ldata.eDate);
        Log.i("todays data", todaysDate);



        //checking whether today's date is equal to the end date of the mobile data limiter
        if(ldata.eDate.equals(todaysDate))
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle("Data Usage Warning!!!")
                    .setSmallIcon(R.drawable.ic_network_cell_white_24dp)
                    .setContentText("The limit date is reached!!");

            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(intent1);
            stackBuilder.addParentStack(MainActivity.class);


            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);


            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(3, builder.build());


            //to stop repeating the alarm service using the fuction in the AlarmReciever class
            AlarmReciever ar = new AlarmReciever();
            ar.cancelAlarm(this);
        }

        return START_STICKY;

    }
}

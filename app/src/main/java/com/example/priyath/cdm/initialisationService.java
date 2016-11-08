package com.example.priyath.cdm;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/*
 * Created by priyath on 26/7/16.
 */
public class initialisationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    public List<ApplicationInfo> installedApplications;
    public List<appDetails> appdetails;
    public int onStartCommand(Intent intent,int flags, int startId){
        Log.i("status of the service","is started");
        PackageManager packageManager;
        packageManager = getPackageManager();

        installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        appDetails app = new appDetails();
        for(ApplicationInfo appinfo: installedApplications){
            if(appinfo.permission.equals("INTERNET")) {

                Log.i("the app name is :", appinfo.className);
                app.uid = appinfo.uid;
                app.dataused = TrafficStats.getUidRxBytes(app.uid);
                appdetails.add(app);
            }
        }


        try {
            FileOutputStream fos = openFileOutput("app initialisation details",MODE_PRIVATE);
            ObjectOutputStream oos  = new ObjectOutputStream(fos);
            oos.writeObject(appdetails);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("the service is over", "Press the details button!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return START_STICKY;
    }
}

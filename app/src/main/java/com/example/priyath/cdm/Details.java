package com.example.priyath.cdm;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Details extends AppCompatActivity {

    public List<ApplicationInfo> InstalledApplication;
    RecyclerView rv;
    LinearLayoutManager llm;
    List<usage> sortedAppDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rv = (RecyclerView) findViewById(R.id.relativeLayout);
        getAppList();

        initialise();

        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        Log.i("The size is  :", String.valueOf(sortedAppDetails.size()));


        rv.setHasFixedSize(true);

        if(!appDetails.isEmpty()) {
            RVadapter adapter = new RVadapter(appDetails);
            rv.setAdapter(adapter);
        }else{
            Toast.makeText(this, "Nothing to show in Details",
                    Toast.LENGTH_LONG).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataUsage();






            }
        });
    }

    PackageManager packageManager;

    void getAppList() {

        packageManager = getPackageManager();
        InstalledApplication = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

    }


    void getDataUsage() {
        int UID;
        long DataUsage;
        SharedPreferences preferences;
        SharedPreferences.Editor editor;
        for (ApplicationInfo appinfo : InstalledApplication) {
            UID = appinfo.uid;
            DataUsage = TrafficStats.getUidRxBytes(UID);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            editor = preferences.edit();
            editor.putString(String.valueOf(UID), String.valueOf(DataUsage));
            editor.apply();
        }

        makingZero();


    }

    void makingZero(){
        usage temp;
        appDetails.clear();

        if(!appDetails.isEmpty()) {
            RVadapter adapter = new RVadapter(appDetails);
            rv.setAdapter(adapter);
        }else{
            Toast.makeText(this, "The Details is cleared",
                    Toast.LENGTH_LONG).show();
        }
    }


    List<usage> appDetails;

    void initialise() {
        SharedPreferences preferences;
        long dataused;
        int UID;
        int i = 0;
        Drawable drawable;
        String appname, datausageString;


        appDetails = new ArrayList<>();

        for (ApplicationInfo appinfo : InstalledApplication) {
            appname = (String) appinfo.loadLabel(packageManager);
            drawable = appinfo.loadIcon(packageManager);
            UID = appinfo.uid;


            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            datausageString = preferences.getString(String.valueOf(UID), "0");
            dataused = TrafficStats.getUidRxBytes(UID)-Long.parseLong(datausageString);
            if ((dataused != 0)) {
                i++;

                Log.i(i + "th appname is  ", appname);
                appDetails.add(new usage(dataused, appname, drawable, i));
            }

        }
        usage temp;

        for(int k=0;k<appDetails.size();k++){
            for(int j=k;j<appDetails.size();j++){
                if(Long.compare(appDetails.get(k).usage,appDetails.get(j).usage)<=0){
                   temp=appDetails.get(k);
                    appDetails.set(k,appDetails.get(j));
                    appDetails.set(j,temp);

                }

            }
        }


    }
}

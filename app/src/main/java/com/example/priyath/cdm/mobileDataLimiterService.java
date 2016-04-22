package com.example.priyath.cdm;

import android.app.Service;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.IBinder;
import android.widget.Toast;

/*
 * Created by priyath on 22/4/16.
 */
public class mobileDataLimiterService extends Service {
    private gMobileData data = new gMobileData();
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



    int checking(){
        long dataUsed,limit;
        limit = Long.parseLong(data.limit);
        dataUsed = TrafficStats.getMobileRxBytes()-data.startMobileData;

        if(dataUsed > (limit*0.9)){
         return 1;
        }else if(dataUsed == limit){
            return 2;

        }else if(dataUsed > limit){
            return 3;
        }

        return 0;
    }


}


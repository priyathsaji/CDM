package com.example.priyath.cdm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/*
 * Created by PRIYATH SAJI on 18-06-2016.
 */
public class wifiReciever  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            Intent intent1 = new Intent(context, wifiService.class);
            context.startService(intent1);
        }else{
            Intent intent1 = new Intent(context,wifiService.class);
            context.stopService(intent1);
        }
    }
}

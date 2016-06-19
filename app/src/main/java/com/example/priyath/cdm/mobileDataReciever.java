package com.example.priyath.cdm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/*
 * Created by PRIYATH SAJI on 19-06-2016.
 */
public class mobileDataReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((info!=null)&&(info.isConnected())){


            Intent intent1 = new Intent(context,mobileDataService.class);
            context.startService(intent1);

        }else{
            Intent intent1 = new Intent(context,mobileDataService.class);
            context.stopService(intent1);
        }
    }
}

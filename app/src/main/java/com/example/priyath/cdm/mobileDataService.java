package com.example.priyath.cdm;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.ImageView;

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
    int kbps_array[]={R.drawable.ic_1,R.drawable.ic_2,R.drawable.ic_3,R.drawable.ic_4,R.drawable.ic_5,R.drawable.ic_6,R.drawable.ic_7,R.drawable.ic_8,R.drawable.ic_9,R.drawable.ic_10,R.drawable.ic_11,R.drawable.ic_12,R.drawable.ic_13,R.drawable.ic_14,R.drawable.ic_15,R.drawable.ic_16,R.drawable.ic_16,R.drawable.ic_17,R.drawable.ic_18,R.drawable.ic_19,R.drawable.ic_20,R.drawable.ic_21,R.drawable.ic_22,R.drawable.ic_23,R.drawable.ic_23,R.drawable.ic_24,R.drawable.ic_25,R.drawable.ic_26,R.drawable.ic_27,R.drawable.ic_28,R.drawable.ic_29,R.drawable.ic_30,R.drawable.ic_31,R.drawable.ic_32,R.drawable.ic_33,R.drawable.ic_34,R.drawable.ic_35,R.drawable.ic_36,R.drawable.ic_37,R.drawable.ic_38,R.drawable.ic_39,R.drawable.ic_40,R.drawable.ic_41,R.drawable.ic_42,R.drawable.ic_43, R.drawable.ic_44,R.drawable.ic_45,R.drawable.ic_46,R.drawable.ic_47,R.drawable.ic_48,R.drawable.ic_49,R.drawable.ic_50,
            R.drawable.ic_51,R.drawable.ic_52,R.drawable.ic_53,R.drawable.ic_54,R.drawable.ic_55,R.drawable.ic_56,R.drawable.ic_57,R.drawable.ic_58,R.drawable.ic_59,R.drawable.ic_60,R.drawable.ic_61,R.drawable.ic_62,R.drawable.ic_63,R.drawable.ic_64,R.drawable.ic_65,R.drawable.ic_66,R.drawable.ic_67,R.drawable.ic_68,R.drawable.ic_69,R.drawable.ic_70,R.drawable.ic_71,R.drawable.ic_72,R.drawable.ic_73,R.drawable.ic_74,R.drawable.ic_75,R.drawable.ic_76,R.drawable.ic_77,R.drawable.ic_78,R.drawable.ic_79,R.drawable.ic_80,R.drawable.ic_81,R.drawable.ic_82,R.drawable.ic_83,R.drawable.ic_84,R.drawable.ic_85,R.drawable.ic_86,R.drawable.ic_87,R.drawable.ic_88,R.drawable.ic_89,R.drawable.ic_90,R.drawable.ic_91,R.drawable.ic_92,R.drawable.ic_93,R.drawable.ic_94,R.drawable.ic_95,R.drawable.ic_96,R.drawable.ic_97,R.drawable.ic_98,R.drawable.ic_99,R.drawable.ic_100,
            R.drawable.ic_101,R.drawable.ic_102,R.drawable.ic_103,R.drawable.ic_104,R.drawable.ic_105,R.drawable.ic_106,R.drawable.ic_107,R.drawable.ic_108,R.drawable.ic_109,R.drawable.ic_110,R.drawable.ic_111,R.drawable.ic_112,R.drawable.ic_113,R.drawable.ic_114,R.drawable.ic_115,R.drawable.ic_116,R.drawable.ic_117,R.drawable.ic_118,R.drawable.ic_119,R.drawable.ic_120,R.drawable.ic_121,R.drawable.ic_122,R.drawable.ic_123,R.drawable.ic_124,R.drawable.ic_125,R.drawable.ic_126,R.drawable.ic_127,R.drawable.ic_128,R.drawable.ic_129,R.drawable.ic_130,R.drawable.ic_131,R.drawable.ic_132,R.drawable.ic_133,R.drawable.ic_134,R.drawable.ic_135,R.drawable.ic_136,R.drawable.ic_137,R.drawable.ic_138,R.drawable.ic_139,R.drawable.ic_140,R.drawable.ic_141,R.drawable.ic_142,R.drawable.ic_143,R.drawable.ic_144,R.drawable.ic_145,R.drawable.ic_146,R.drawable.ic_147,R.drawable.ic_148,R.drawable.ic_149,R.drawable.ic_150,
            R.drawable.ic_151,R.drawable.ic_152,R.drawable.ic_153,R.drawable.ic_154,R.drawable.ic_155,R.drawable.ic_156,R.drawable.ic_157,R.drawable.ic_158,R.drawable.ic_159,R.drawable.ic_160,R.drawable.ic_161,R.drawable.ic_162,R.drawable.ic_163,R.drawable.ic_164,R.drawable.ic_165,R.drawable.ic_166,R.drawable.ic_167,R.drawable.ic_168,R.drawable.ic_169,R.drawable.ic_170,R.drawable.ic_171,R.drawable.ic_172,R.drawable.ic_173,R.drawable.ic_174,R.drawable.ic_175,R.drawable.ic_176,R.drawable.ic_177,R.drawable.ic_178,R.drawable.ic_179,R.drawable.ic_180,R.drawable.ic_181,R.drawable.ic_182,R.drawable.ic_183,R.drawable.ic_184,R.drawable.ic_185,R.drawable.ic_186,R.drawable.ic_187,R.drawable.ic_188,R.drawable.ic_189,R.drawable.ic_190,R.drawable.ic_191,R.drawable.ic_192,R.drawable.ic_193,R.drawable.ic_194,R.drawable.ic_195,R.drawable.ic_196,R.drawable.ic_197,R.drawable.ic_198,R.drawable.ic_199,R.drawable.ic_200,
            R.drawable.ic_201,R.drawable.ic_202,R.drawable.ic_203,R.drawable.ic_204,R.drawable.ic_205,R.drawable.ic_206,R.drawable.ic_207,R.drawable.ic_208,R.drawable.ic_209,R.drawable.ic_210,R.drawable.ic_211,R.drawable.ic_212,R.drawable.ic_213,R.drawable.ic_214,R.drawable.ic_215,R.drawable.ic_216,R.drawable.ic_217,R.drawable.ic_218,R.drawable.ic_219,R.drawable.ic_220,R.drawable.ic_221,R.drawable.ic_222,R.drawable.ic_223,R.drawable.ic_224,R.drawable.ic_225,R.drawable.ic_226,R.drawable.ic_227,R.drawable.ic_228,R.drawable.ic_229,R.drawable.ic_230,R.drawable.ic_231,R.drawable.ic_232,R.drawable.ic_233,R.drawable.ic_234,R.drawable.ic_235,R.drawable.ic_236,R.drawable.ic_237,R.drawable.ic_238,R.drawable.ic_239,R.drawable.ic_240,R.drawable.ic_241,R.drawable.ic_242,R.drawable.ic_243,R.drawable.ic_244,R.drawable.ic_245,R.drawable.ic_246,R.drawable.ic_247,R.drawable.ic_248,R.drawable.ic_249,R.drawable.ic_250,
            R.drawable.ic_251,R.drawable.ic_252,R.drawable.ic_253,R.drawable.ic_254,R.drawable.ic_255,R.drawable.ic_256,R.drawable.ic_257,R.drawable.ic_258,R.drawable.ic_259,R.drawable.ic_260,R.drawable.ic_261,R.drawable.ic_262,R.drawable.ic_263,R.drawable.ic_264,R.drawable.ic_265,R.drawable.ic_265,R.drawable.ic_267,R.drawable.ic_268,R.drawable.ic_269,R.drawable.ic_270,R.drawable.ic_271,R.drawable.ic_272,R.drawable.ic_273,R.drawable.ic_274,R.drawable.ic_275,R.drawable.ic_276,R.drawable.ic_277,R.drawable.ic_278,R.drawable.ic_279,R.drawable.ic_280,R.drawable.ic_281,R.drawable.ic_282,R.drawable.ic_283,R.drawable.ic_284,R.drawable.ic_285,R.drawable.ic_286,R.drawable.ic_287,R.drawable.ic_288,R.drawable.ic_289,R.drawable.ic_290,R.drawable.ic_291,R.drawable.ic_292,R.drawable.ic_293,R.drawable.ic_294,R.drawable.ic_295,R.drawable.ic_296,R.drawable.ic_297,R.drawable.ic_298,R.drawable.ic_299,R.drawable.ic_300};



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
            if(ldata!=null)
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



    void updateWifiNotification() {
        int k = 1, b = 0, k1 = 1, b1 = 0;

        String[] unitArray = new String[]{
                "B", "KB", "MB", "GB"
        };

        if (mdSpeed < 1024) {
            k = 1;
            b = 0;
        } else if (mdSpeed > 1073741824) {
            k = 1073741824;
            b = 3;
        } else if (mdSpeed > (1048576)) {
            k = 1048576;
            b = 2;
        } else if (mdSpeed > 1024) {
            k = 1024;
            b = 1;
        }

        if (mdnData < 1024) {
            k1 = 1;
            b1 = 0;
        } else if (mdnData > 1073741824) {
            k1 = 1073741824;
            b1 = 3;
        } else if (mdnData > (1048576)) {
            k1 = 1048576;
            b1 = 2;
        } else if (mdnData > 1024) {
            k1 = 1024;
            b1 = 1;
        }

        if(mdSpeed < 0){
            Intent intent = new Intent(this,mobileDataService.class);
            stopService(intent);
            WifiManager manager1 = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
            if(manager1.isWifiEnabled()){
                Intent intent1 = new Intent(this,wifiService.class);
                startService(intent1);
            }
        }
        int kbps;
        if (b == 0) {
            kbps = 0;
        }else if(b == 1){
            kbps = (int) mdSpeed / k;
        }else if(b == 2) {
            kbps = 300;

        }else{
            kbps = 300;
        }

        NotificationCompat.Builder builder = null;

            builder = new NotificationCompat.Builder(this)
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

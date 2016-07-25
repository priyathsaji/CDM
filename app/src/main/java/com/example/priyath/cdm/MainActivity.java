package com.example.priyath.cdm;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {



    private String[] unitArray;
    private Handler mHandler;
    private  boolean mobileDataEnabled = false;
    Button button,button1,button2,button3,button4;
    private int a1 = 1, a2 = 1;
    private EditText days,limit;
    private Spinner unitSpinner;
    private TextView unitTview,sDate,eDate,dDownloaded,dUploaded;
    private CardView cardView;
    private boolean isLimiterSet;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        days = (EditText)findViewById(R.id.eDay);
        limit = (EditText)findViewById(R.id.eLimit);

        unitArray = new String[]{
                "B", "KB", "MB", "GB"
        };

        String[] spinnerArray = new String[]{
                "MB", "GB"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);

        limiterdata ldata = null;
        try {
            FileInputStream in = openFileInput("limiterdata");
            ObjectInputStream ois = new ObjectInputStream(in);

            ldata = (limiterdata)ois.readObject();

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }


        if(ldata != null)
        if(ldata.isLimiterSet){
            CardView cardView = (CardView)findViewById(R.id.cardView);
            CardView cardView2 = (CardView)findViewById(R.id.cardView3);
            assert cardView != null;
            cardView.setVisibility(View.VISIBLE);
            assert cardView2 != null;
            cardView2.setVisibility(View.INVISIBLE);
        }

        // unit spinner inside the mobile data notification
        unitSpinner = (Spinner) findViewById(R.id.unitSpinner);
        assert unitSpinner != null;
        unitSpinner.setAdapter(adapter);
        unitTview = (TextView)findViewById(R.id.unit);


        initialisation();
        updateLimiter();
        mHandler = new Handler();
        startRepeatingTask();

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button4 = (Button)findViewById(R.id.setlimiter);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardView cview = (CardView)findViewById(R.id.cardView);
                assert cview != null;
                cview.setVisibility(View.VISIBLE);
                CardView cview2 = (CardView)findViewById(R.id.cardView3);
                assert cview2 != null;
                cview2.setVisibility(View.INVISIBLE);
                isLimiterSet =true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLimiterSet",true);

            }
        });

        //to be removed

        button = (Button)findViewById(R.id.details);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,Details.class);
                startActivity(intent);
            }
        });


        //button to refresh the notification counter for mobile data
        button1= (Button)findViewById(R.id.mobile);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileDataService mService = new mobileDataService();
                mService.refresh();
                onDisplayMobileData(0,0);


            }
        });


        //button to refresh the notification counter for the wifi
        button2 = (Button) findViewById(R.id.wifi);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiService wService = new wifiService();
                wService.refresh();
                onDisplayWifiData(0,0);
            }
        });




        //onclick listener for the button change to set the mobile data limiter
        button3 = (Button)findViewById(R.id.change);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changedatalimiter();
            }
        });


        //on click listener to the mobile data limiter to ask whether the user to edit the limiter or not
        cardView = (CardView)findViewById(R.id.cardView);
        assert cardView != null;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limiterDialog();
            }
        });


    }



    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

                try {
                    Class cmClass = Class.forName(cm.getClass().getName());
                    Method method1 = cmClass.getDeclaredMethod("getMobileDataEnabled");
                    method1.setAccessible(true); // Make the method callable
                    // get the setting for "mobile data"
                    mobileDataEnabled = (Boolean) method1.invoke(cm);
                } catch (Exception e) {
                    // Some problem accessible private API
                }

                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                if((!mobileDataEnabled) && (!wifi.isWifiEnabled())) {
                    stopRepeatingTask();
                    initialisation();
                    mHandler.removeCallbacks(mStatusChecker);
                }

                if(mobileDataEnabled) {
                    mobileDataService mService = new mobileDataService();
                    long mData = mService.getmobileDataDownloaded();
                    long mUPloaded = mService.getmobileDataUPloaded();
                    onDisplayMobileData(mData,mUPloaded);
                    Log.i("mobile data is  :" ,String.valueOf(mData));
                    if(isLimiterSet){
                        updateLimiter();
                    }
                }

                if (wifi.isWifiEnabled()){
                    wifiService wService = new wifiService();
                    long wUploaded = wService.getWifiDataUploaded();
                    long wData = wService.getWifiDataDownloaded();
                    onDisplayWifiData(wData,wUploaded);
                    Log.i("wifi Data is  :",String.valueOf(wData));
                }


                 //this function can change value of mInterval.

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

    void initialisation(){
        long mData,wData,wUploaded,mUploaded;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mData = preferences.getLong("mobileData",0);
        wData = preferences.getLong("wifiData",0);
        wUploaded = preferences.getLong("wifiUploaded",0);
        mUploaded = preferences.getLong("mobileUploded",0);
        onDisplayMobileData(mData,mUploaded);
        onDisplayWifiData(wData,wUploaded);
    }


    void changedatalimiter(){
        String nodays;
        String limitData;
        String unit = "MB";
        String feDate;
        long downloaded;
        long uploaded;



        dDownloaded = (TextView)findViewById(R.id.DataDownload);
        dUploaded = (TextView)findViewById(R.id.DataUploaded);

        downloaded = TrafficStats.getMobileRxBytes();
        uploaded = TrafficStats.getMobileTxBytes();

        nodays = days.getText().toString();
        limitData = limit.getText().toString();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = preferences.edit();

        if(pos == 1)
            unit = "GB";


        unitTview.setText(unit);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd / MMM / yyyy");
        String formattedDate = df.format(c.getTime());
        sDate = (TextView)findViewById(R.id.sDate);
        assert sDate != null;
        sDate.setText(formattedDate);
        eDate = (TextView)findViewById(R.id.eDate);

        c.add(Calendar.DATE, Integer.parseInt(nodays));
        SimpleDateFormat sdf = new SimpleDateFormat("dd / MMM / yyyy");
        Date resultdate = new Date(c.getTimeInMillis());
        feDate = sdf.format(resultdate);

        eDate.setText(feDate);

        editor.putString("Days",nodays);
        editor.putString("limit",limitData);
        editor.putString("unit", unit);
        editor.putString("sDate",formattedDate);
        editor.putString("eDate",feDate);
        editor.putLong("dataDownloaded",downloaded);
        editor.putLong("dataUploaded",uploaded);
        editor.putBoolean("isLimiterSet",true);
        editor.apply();


        // storing the limiter data to an object of class limiterdata

        limiterdata ldata = new limiterdata();

        ldata.dataDownloaded = downloaded;
        ldata.dataUploaded = uploaded;
        ldata.days = Long.parseLong(nodays);
        ldata.unit = unit;
        ldata.sdate = formattedDate;
        ldata.eDate = feDate;
        ldata.isLimiterSet = true;
        ldata.limit = Long.parseLong(limitData);

        //storing the data to internal storage
        try {
            FileOutputStream out = openFileOutput("limiterdata",Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(ldata);
            oos.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        dDownloaded.setText("0.0");
        dUploaded.setText("0.0");


        days.setFocusableInTouchMode(false);
        limit.setFocusableInTouchMode(false);
        days.setFocusable(false);
        limit.setFocusable(false);
        unitTview.setVisibility(View.VISIBLE);
        unitSpinner.setVisibility(View.INVISIBLE);
        unitSpinner.setFocusable(false);
        unitTview.setFocusableInTouchMode(true);
        unitTview.setFocusable(true);
        cardView.setFocusable(true);
        cardView.setClickable(true);
        button3.setVisibility(View.INVISIBLE);

        //starting the alarm Servie

        AlarmReciever alarmReciever = new AlarmReciever();
        alarmReciever.setAlarm(this);

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.setMobileDataLimiter){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!isFinishing()){
                        limiterDialog();
                    }
                }
            });


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void limiterDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Alert")
                .setMessage("Do you want to change the number of Days and Data Limit ?")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        button3.setVisibility(View.VISIBLE);
                        assert days != null;
                        days.setFocusable(true);
                        days.setFocusableInTouchMode(true);
                        limit.setFocusable(true);
                        limit.setFocusableInTouchMode(true);
                        unitTview.setVisibility(View.INVISIBLE);
                        unitSpinner.setVisibility(View.VISIBLE);
                        unitSpinner.setFocusable(true);
                        cardView.setFocusable(false);
                        cardView.setClickable(false);
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }


    boolean doubleBackToExitPressedOnce = false;





    public void updateLimiter(){
        String nodays;
        String limitData;
        String unit;
        String formattedsDate,formattedeDate;

        long datadownloaded,datauploaded,k1,k2;
        int a,b;
        double downloaded,uploaded;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        nodays = preferences.getString("Days","0");
        limitData = preferences.getString("limit","0");
        unit = preferences.getString("unit","MB");
        formattedsDate = preferences.getString("sDate"," not used ");
        formattedeDate = preferences.getString("eDate"," not used ");

        days = (EditText)findViewById(R.id.eDay);
        limit = (EditText)findViewById(R.id.eLimit);

        sDate = (TextView)findViewById(R.id.sDate);
        eDate = (TextView)findViewById(R.id.eDate);

        dDownloaded = (TextView)findViewById(R.id.DataDownload);
        dUploaded = (TextView)findViewById(R.id.DataUploaded);

        TextView dUnit = (TextView)findViewById(R.id.DownloadUnit);
        TextView uUnit = (TextView)findViewById(R.id.UploadUnit);


        datadownloaded = (TrafficStats.getMobileRxBytes()-preferences.getLong("dataDownloaded",0));
        datauploaded = (TrafficStats.getMobileTxBytes()-preferences.getLong("dataUploaded",0));




            if (datadownloaded > 1073741824){
                k1 = 1073741824;
                a=3;
            } else if (datadownloaded > (1048576)) {
                k1 =  1048576;
                a=2;
            } else if (datadownloaded > 1024) {
                k1 = 1024;
                a=1;
            }else{
                k1 = 1;
                a=0;
            }


        if (datauploaded > 1073741824){
            k2 = 1073741824;
            b=3;
        } else if (datauploaded > (1048576)) {
            k2 =  1048576;
            b=2;
        } else if (datauploaded > 1024) {
            k2 = 1024;
            b=1;
        } else{
                k2 = 1;
                b=0;
        }

        downloaded = datadownloaded / k1;
        uploaded = datauploaded / k2;

        downloaded = Math.round((downloaded*100)/100);
        uploaded = Math.round((uploaded*100)/100);


        dDownloaded.setText(String.valueOf(downloaded));
        dUploaded.setText(String.valueOf(uploaded));
        assert dUnit != null;
        dUnit.setText(unitArray[a]);
        assert uUnit != null;
        uUnit.setText(unitArray[b]);


        assert eDate != null;
        eDate.setText(formattedeDate);
        assert sDate != null;
        sDate.setText(formattedsDate);
        unitTview .setText(unit);
        days.setText(nodays);
        limit.setText(limitData);

        long limit = Long.parseLong(limitData);

        if(unit.equals("GB")) {
            if (((limit * 1073741824) - datadownloaded) <= 0) {
                CardView cardView = (CardView)findViewById(R.id.cardView);
                CardView cardView2 = (CardView)findViewById(R.id.cardView3);
                assert cardView != null;
                cardView.setVisibility(View.INVISIBLE);
                assert cardView2 != null;
                cardView2.setVisibility(View.VISIBLE);
                isLimiterSet = false;
            }
        }else{
               if (((limit*1073741824)-datadownloaded) <= 0){

                   CardView cardView = (CardView)findViewById(R.id.cardView);
                   CardView cardView2 = (CardView)findViewById(R.id.cardView3);
                   assert cardView != null;
                   cardView.setVisibility(View.INVISIBLE);
                   assert cardView2 != null;
                   cardView2.setVisibility(View.VISIBLE);
               }
                isLimiterSet = false;


        }



    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
        //updateLimiter();
    }


    // The code below is the code for updating the notification counter

    void onDisplayMobileData(long mData ,long mdUploaded) {
        double mobileData;
        double mUploaded;
        int k = 0,k1=0,a = 1 ;
        mobileData = mData;
        mUploaded = mdUploaded;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("mobileData",mData);
        editor.putLong("mobileUploded",mdUploaded);
        editor.apply();

        if(mobileData < 1024){
            k=0;

        } else if (mobileData > 1073741824){
            a1 = 1073741824;
            k=3;
        } else if (mobileData > (1048576)) {
            a1 =  1048576;
            k=2;
        } else if (mobileData > 1024) {
            a1 = 1024;
            k=1;
        }


        if(mUploaded < 1024){
            k1=0;
            a=1;

        } else if (mUploaded > 1073741824){
            a = 1073741824;
            k1=3;
        } else if (mUploaded > (1048576)) {
            a =  1048576;
            k1=2;
        } else if (mUploaded > 1024) {
            a = 1024;
            k1=1;
        }

        mobileData= (mobileData/a1);//*0.0009765625);
        mUploaded = (mUploaded/a);
        mobileData=Math.round(mobileData * 100.0)/100.0;
        mUploaded =Math.round(mUploaded * 100.0)/100.0;

        TextView mobileDataView = (TextView)findViewById(R.id.textView2);
        TextView unit1 = (TextView)findViewById(R.id.unit1);
        TextView mobileUploaded = (TextView)findViewById(R.id.MUploadedData);
        TextView unit3 = (TextView)findViewById(R.id.unit3);


        assert mobileUploaded != null;
        mobileUploaded.setText(String.valueOf(mUploaded));
        assert mobileDataView != null;
        mobileDataView.setText(String.valueOf(mobileData));

        assert unit1 != null;
        unit1.setText(unitArray[k]);

        assert unit3 != null;
        unit3.setText(unitArray[k1]);
    }


    void onDisplayWifiData(long wData,long wUploaded){
        double wifiData,wifiUploaded;
        int  j = 0,k=0,a3 =1;
        wifiData = wData;
        wifiUploaded = wUploaded;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("wifiData",wData);
        editor.putLong("wifiUploaded",wUploaded);
        editor.apply();

        if(wifiData  < 1024 ) {
            j = 0;
            a2 = 1;
        }else if(wifiData > (1073741824)) {
            a2 = 1073741824;
            j=3;
        }else if(wifiData > (1048576) ) {
            a2 = 1048576;
            j=2;
        }else if(wifiData > 1024) {
            a2 = 1024;
            j = 1;
        }

        if(wifiUploaded  < 1024 ) {
            k = 0;
            a3 = 1;
        }else if(wifiUploaded > (1073741824)) {
            a3 = 1073741824;
            k=3;
        }else if(wifiUploaded > (1048576) ) {
            a3 = 1048576;
            k=2;
        }else if(wifiUploaded > 1024) {
            a3 = 1024;
            k = 1;
        }


        wifiData=  (wifiData/a2);
        wifiUploaded = (wifiUploaded/a3);
        wifiData = Math.round(wifiData * 100.0) / 100.0;
        wifiUploaded = Math.round(wifiUploaded * 100.0) / 100.0;

        TextView wifiDataView = (TextView)findViewById(R.id.textView4);
        TextView unit2 = (TextView)findViewById(R.id.unit2);
        TextView wifiUploadeView = (TextView)findViewById(R.id.WUploadedData);
        TextView unit4 = (TextView)findViewById(R.id.unit4);
        assert wifiDataView != null;
        wifiDataView.setText(String.valueOf(wifiData));
        assert unit2 != null;
        unit2.setText(unitArray[j]);
        assert wifiUploadeView != null;
        wifiUploadeView.setText(String.valueOf(wifiUploaded));
        assert unit4 != null;
        unit4.setText(unitArray[k]);
    }

    //funtion for the dual press exit on the back key

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}


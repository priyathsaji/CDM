package com.example.priyath.cdm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {


    public HashMap<String,String> dataRecieved = null;
    public ArrayList<HashMap<String,String>> dataRecievedList = null;
    private String[] unitArray,spinnerArray;
    private int mInterval = 1000;
    private Handler mHandler;
    private WifiManager wifi;
    private Method method1;
    private Class cmClass;
    private  boolean mobileDataEnabled = false;
    Button button,button1,button2,button3;
    private int a1 = 1, a2 = 1;
    private EditText days,limit;
    private Spinner unitSpinner;
    private TextView unitTview;
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

        spinnerArray = new String[]{
                "MB", "GB"
        };




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,spinnerArray);

        unitSpinner = (Spinner) findViewById(R.id.unitSpinner);

        assert unitSpinner != null;
        unitSpinner.setAdapter(adapter);

        unitTview = (TextView)findViewById(R.id.unit);


        onDisplayWifiData();
        onDisplayMobileData();
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

        button = (Button)findViewById(R.id.details);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,Details.class);
                startActivity(intent);


            }
        });

        button1= (Button)findViewById(R.id.mobile);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateMobileData();
                onDisplayMobileData();


            }
        });


        button2 = (Button) findViewById(R.id.wifi);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWifiData();
                onDisplayWifiData();
            }
        });


        button3 = (Button)findViewById(R.id.change);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nodays;
                String limitData;
                String unit = "MB";


                nodays = days.getText().toString();
                limitData = limit.getText().toString();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = preferences.edit();

                if(pos == 1)
                    unit = "GB";


                unitTview.setText(unit);
                editor.putString("Days",nodays);
                editor.putString("limit",limitData);
                editor.putString("unit", unit);
                editor.apply();

                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                days.setFocusableInTouchMode(false);
                limit.setFocusableInTouchMode(false);
                days.setFocusable(false);
                limit.setFocusable(false);
                unitTview.setVisibility(View.VISIBLE);
                unitSpinner.setVisibility(View.INVISIBLE);
                unitSpinner.setFocusable(false);
                unitTview.setFocusableInTouchMode(true);
                unitTview.setFocusable(true);


                button3.setVisibility(View.INVISIBLE);

            }
        });


    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                try {
                    cmClass = Class.forName(cm.getClass().getName());
                    method1 = cmClass.getDeclaredMethod("getMobileDataEnabled");
                    method1.setAccessible(true); // Make the method callable
                    // get the setting for "mobile data"
                    mobileDataEnabled = (Boolean)method1.invoke(cm);
                } catch (Exception e) {
                    // Some problem accessible private API
                    // TODO do whatever error handling you want here
                }

                wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);

                if((!mobileDataEnabled) && (!wifi.isWifiEnabled()))
                    stopRepeatingTask();

                if(mobileDataEnabled)
                    onDisplayMobileData();

                if (wifi.isWifiEnabled()){
                    onDisplayWifiData();
                }


                 //this function can change value of mInterval.

            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
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
            button3.setVisibility(View.VISIBLE);

            assert days != null;
            days.setFocusable(true);
            days.setFocusableInTouchMode(true);
            limit.setFocusable(true);
            limit.setFocusableInTouchMode(true);
            unitTview.setVisibility(View.INVISIBLE);
            unitSpinner.setVisibility(View.VISIBLE);
            unitSpinner.setFocusable(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void updateLimiter(){
        String nodays;
        String limitData;
        String unit;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        nodays = preferences.getString("Days","0");
        limitData = preferences.getString("limit","0");
        unit = preferences.getString("unit","MB");

        days = (EditText)findViewById(R.id.eDay);
        limit = (EditText)findViewById(R.id.eLimit);
        unitTview .setText(unit);

        days.setText(nodays);
        limit.setText(limitData);







    }

    public void updateMobileData(){
        long totalDataRecieved = TrafficStats.getTotalRxBytes();
        long mobileDataRecieved = TrafficStats.getMobileRxBytes();
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("MobileDataRecieved",String.valueOf(mobileDataRecieved));
        editor.putString("TotalDataRecieved",String.valueOf(totalDataRecieved));

    }


    public void updateWifiData(){

        long totalDataRecieved = TrafficStats.getTotalRxBytes();
        long mobileDataRecieved = TrafficStats.getMobileRxBytes();
        long wifiDataRecieved = totalDataRecieved - mobileDataRecieved;
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("WifiDataRecieved",String.valueOf(wifiDataRecieved));
        editor.putString("TotalDataRecieved",String.valueOf(totalDataRecieved));
        editor.apply();

    }

    void onDisplayMobileData() {

        long mobileDataRecieved = TrafficStats.getMobileRxBytes();

        double mobileData;
        int k = 0 ;


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String mD = preferences.getString("MobileDataRecieved", "0");
        mobileData = mobileDataRecieved - Long.parseLong(mD);


        if(mobileData < 1024){
            k=0;
            a1 = 1;
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






      mobileData= (mobileData/a1);//*0.0009765625);


        mobileData=Math.round(mobileData * 100.0)/100.0;
        TextView mobileDataView = (TextView)findViewById(R.id.textView2);

        TextView unit1 = (TextView)findViewById(R.id.unit1);

        assert mobileDataView != null;
        mobileDataView.setText(String.valueOf(mobileData));

        assert unit1 != null;
        unit1.setText(unitArray[k]);
        ;




    }

    void onDisplayWifiData(){

        long totalDataRecieved = TrafficStats.getTotalRxBytes();
        long mobileDataRecieved = TrafficStats.getMobileRxBytes();
        long wifiDataRecieved = totalDataRecieved - mobileDataRecieved;

        double wifiData;
        int  j = 0;


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String wD = preferences.getString("WifiDataRecieved", "0");
        wifiData = wifiDataRecieved - Long.parseLong(wD);


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


        wifiData=  (wifiData/a2);

        wifiData = Math.round(wifiData * 100.0) / 100.0;
        TextView wifiDataView = (TextView)findViewById(R.id.textView4);
        TextView unit2 = (TextView)findViewById(R.id.unit2);
        assert wifiDataView != null;
        wifiDataView.setText(String.valueOf(wifiData));
        assert unit2 != null;
        unit2.setText(unitArray[j]);

    }


}


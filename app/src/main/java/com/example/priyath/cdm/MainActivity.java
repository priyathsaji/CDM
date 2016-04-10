package com.example.priyath.cdm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {


    public HashMap<String,String> dataRecieved = null;
    public ArrayList<HashMap<String,String>> dataRecievedList = null;
    private String[] unitArray;
    private int mInterval = 1000;
    private Handler mHandler;
    Button button;
    private int a1 = 1, a2 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        unitArray = new String[]{
                "B", "KB", "MB", "GB"
        };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,unitArray);

        Spinner unitSpinner = (Spinner) findViewById(R.id.unitSpinner);

        assert unitSpinner != null;
        unitSpinner.setAdapter(adapter);


        onDisplay();
        mHandler = new Handler();
        startRepeatingTask();


        button = (Button)findViewById(R.id.details);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this,Details.class);
                startActivity(intent);


            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                update();
                onDisplay();
            }
        });
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                onDisplay(); //this function can change value of mInterval.
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

        return super.onOptionsItemSelected(item);
    }




    public void update(){

        long totalDataRecieved = TrafficStats.getTotalRxBytes();
        long mobileDataRecieved = TrafficStats.getMobileRxBytes();
        long wifiDataRecieved = totalDataRecieved - mobileDataRecieved;
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("MobileDataRecieved",String.valueOf(mobileDataRecieved));
        editor.putString("WifiDataRecieved",String.valueOf(wifiDataRecieved));
        editor.putString("TotalDataRecieved",String.valueOf(totalDataRecieved));
        editor.apply();




    }

    void onDisplay() {
        long totalDataRecieved = TrafficStats.getTotalRxBytes();
        long mobileDataRecieved = TrafficStats.getMobileRxBytes();
        long wifiDataRecieved = totalDataRecieved - mobileDataRecieved;
        double mobileData;
        double wifiData;
        int k = 0 , j = 0;


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String wD = preferences.getString("WifiDataRecieved", "0");
        String mD = preferences.getString("MobileDataRecieved", "0");
        mobileData = mobileDataRecieved - Long.parseLong(mD);
        wifiData = wifiDataRecieved - Long.parseLong(wD);

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





      mobileData= (mobileData/a1);//*0.0009765625);
        wifiData=  (wifiData/a2);

        wifiData = Math.round(wifiData * 100.0) / 100.0;
        mobileData=Math.round(mobileData * 100.0)/100.0;
        TextView mobileDataView = (TextView)findViewById(R.id.textView2);
        TextView wifiDataView = (TextView)findViewById(R.id.textView4);
        TextView unit1 = (TextView)findViewById(R.id.unit1);
        TextView unit2 = (TextView)findViewById(R.id.unit2);
        assert mobileDataView != null;
        mobileDataView.setText(String.valueOf(mobileData));
        assert wifiDataView != null;
        wifiDataView.setText(String.valueOf(wifiData));
        assert unit1 != null;
        unit1.setText(unitArray[k]);
        assert unit2 != null;
        unit2.setText(unitArray[j]);




    }


}


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
    private String[] arraySpinner;
    private int mInterval = 1000;
    private Handler mHandler;
    private int a1=1048576,a2=1048576;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Spinner spinner1=(Spinner)findViewById(R.id.spinner);
        Spinner spinner2=(Spinner)findViewById(R.id.spinner2);

        this.arraySpinner = new String[] {
                "MB", "KB", "GB"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        assert spinner1 != null;
        spinner1.setAdapter(adapter);
        assert spinner2 != null;
        spinner2.setAdapter(adapter);


        onDisplay();
        mHandler = new Handler();
        startRepeatingTask();

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 1)
                    a2 = 1024;
                else if (position == 0)
                    a2 = 1048576;
                else if (position == 2)
                    a2 = 1073741824;
                onDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 1)
                    a1 = 1024;
                else if (position == 0)
                    a1 = 1048576;
                else if (position == 2)
                    a1 = 1073741824;
                onDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        button = (Button)findViewById(R.id.button);

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

    void onDisplay(){
        long totalDataRecieved = TrafficStats.getTotalRxBytes();
        long mobileDataRecieved = TrafficStats.getMobileRxBytes();
        long wifiDataRecieved = totalDataRecieved - mobileDataRecieved;
        double mobileData ;
        double wifiData;


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String wD = preferences.getString("WifiDataRecieved", "0");
        String mD = preferences.getString("MobileDataRecieved","0");
        mobileData = mobileDataRecieved - Long.parseLong(mD);
        wifiData = wifiDataRecieved - Long.parseLong(wD);
        mobileData= (mobileData/a1);//*0.0009765625);
        wifiData=  (wifiData/a2);

        wifiData = Math.round(wifiData * 100.0) / 100.0;
        mobileData=Math.round(mobileData * 100.0)/100.0;

        TextView mobileDataView = (TextView)findViewById(R.id.textView2);
        TextView wifiDataView = (TextView)findViewById(R.id.textView4);
        mobileDataView.setText(String.valueOf(mobileData));
        wifiDataView.setText(String.valueOf(wifiData));



    }
}


package com.example.priyath.cdm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class settings extends AppCompatActivity {

    private Switch wifiSwitch,mobileDataSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        wifiSwitch = (Switch)findViewById(R.id.wifiSwitch);
        mobileDataSwitch = (Switch)findViewById(R.id.mobileDataSwitch);
        assert wifiSwitch != null;
        wifiSwitch.setChecked(false);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(settings.this);
        String status = preference.getString("wifiSwitch","");
        String status1 = preference.getString("mobileDataSwitch","");
        if(status.equals("Off")){
            wifiSwitch.setChecked(false);
            wifiSwitch.setText(status);
        }else if(status.equals("On")){
            wifiSwitch.setChecked(true);
            wifiSwitch.setText(status);
            startService(new Intent(getBaseContext(),MyService.class));
        }
        if(status1.equals("On")){
            mobileDataSwitch.setChecked(true);
            mobileDataSwitch.setText(status1);
            startService(new Intent(getBaseContext(),MyMobileService.class));
        }else{
            mobileDataSwitch.setChecked(false);
            mobileDataSwitch.setText(status1);
        }


        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(settings.this);
                SharedPreferences.Editor editor = preferences.edit();
                String status;
                if(isChecked){
                    status = "On";
                    wifiSwitch.setText(status);
                    startService(new Intent(getBaseContext(), MyService.class));
                    editor.putString("wifiSwitch","On");
                    editor.apply();

                }else{
                    status = "Off";
                    wifiSwitch.setText(status);
                    stopService(new Intent(getBaseContext(),MyService.class));
                    editor.putString("wifiSwitch","Off");
                    editor.apply();

                }
            }
        });

        mobileDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(settings.this);
                SharedPreferences.Editor editor = preferences.edit();
                String status;
                if(isChecked){
                    status = "On";
                    mobileDataSwitch.setText(status);
                    startService(new Intent(getBaseContext(), MyMobileService.class));
                    editor.putString("mobileDataSwitch","On");
                    editor.apply();
                }else{
                    status = "Off";
                    mobileDataSwitch.setText(status);
                    stopService(new Intent(getBaseContext(),MyMobileService.class));
                    editor.putString("mobileDataSwitch","Off");
                    editor.apply();

                }

            }
        });
    }
}

package com.example.priyath.cdm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class settings extends AppCompatActivity {

    private Switch wifiSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        wifiSwitch = (Switch)findViewById(R.id.wifiSwitch);
        assert wifiSwitch != null;
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(settings.this);
        final String status = preference.getString("wifiSwitch","");
        if(status.equals("Off")){
            wifiSwitch.setChecked(false);
            wifiSwitch.setText(status);
        }else{
            wifiSwitch.setChecked(true);
            wifiSwitch.setText(status);
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
    }
}

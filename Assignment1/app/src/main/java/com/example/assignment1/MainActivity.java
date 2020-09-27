package com.example.assignment1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements value_sender {

    //Button
    public Button start_button;
    public Button stop_button;
    public Button setting_button;

    //Key
    public static String longtitude_key = "long_key";
    public static String latitude_key = "lat_key";

    //User input declaration
    public String userinput_distance;
    public String userinput_time;

    //Receiver
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set button and click listener
        button_init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    //Call back of permission result;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // User permission granted fail;
            finish();
        } else {
            permission_checking();
        }
    }

    //Return true if pass checking false if not
    public boolean permission_checking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void get_message(String distance, String time) {
        userinput_distance = distance;
        userinput_time = time;

        System.out.println("x = " + userinput_time);
        System.out.println("y = " + userinput_distance);
    }

    public void button_init() {

        start_button = (Button) findViewById(R.id.start_button);
        stop_button = (Button) findViewById(R.id.stop_button);
        setting_button = (Button) findViewById(R.id.setting_button);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Location_Service.class);
                intent.putExtra("distance", userinput_distance);
                intent.putExtra("time", userinput_time);


                System.out.println("distance = " + userinput_distance);
                System.out.println();
                System.out.println("time = " + userinput_time);
                System.out.println();

                startService(intent);

            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Location_Service.class);
                stopService(intent);

            }
        });

        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //start with fragment transaction
                setting_fragement_transaction();

            }
        });
    }

    public void setting_fragement_transaction() {

        //Basic Fragment Manager setup
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //create a new setting fragment
        SettingFragment settingFragment = SettingFragment.newInstance(null, null);
        fragmentTransaction.replace(R.id.fragment_container, settingFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    System.out.println("longtitude is  " + intent.getExtras().get(longtitude_key));
                    System.out.println("latitude is  " + intent.getExtras().get(latitude_key));
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

}
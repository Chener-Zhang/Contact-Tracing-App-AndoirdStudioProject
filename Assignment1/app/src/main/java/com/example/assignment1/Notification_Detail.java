package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Notification_Detail extends AppCompatActivity {


    //longtitude and latitude declaration

    //Intent keys
    public static String long_key = "longtitude";
    public static String la_key = "latitude";
    public TextView my_longtitude;
    public TextView my_latitude;
    public double get_long;
    public double get_lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification__detail);
        System.out.println("code goes here");

        //retrive from the main activities
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            get_long = bundle.getDouble(long_key);
            get_lat = bundle.getDouble(la_key);
            System.out.printf("get the value %f    %f ", get_long, get_lat);
        } else {
            System.out.println("bundle == null!!!!");
        }

    }


}
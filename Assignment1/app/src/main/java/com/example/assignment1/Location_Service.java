package com.example.assignment1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Location_Service extends Service {


    public static String longtitude_key = "long_key";
    public static String latitude_key = "lat_key";
    //Location component
    public LocationManager locationManager;
    public LocationListener locationListener;
    public double longtitude;
    public double latitude;

    //input distance and time


    //Notification component
    public static String CHANNEL_ID = "channel_id";
    public static String CHANNEL_NAME = "channel_name";
    public static String CHANNEL_DES = "channel_description";


    public String Tracing_distance;
    public String sedentary_time;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Tracing_distance = intent.getStringExtra("distance");
        sedentary_time = intent.getStringExtra("time");

        long time = Long.parseLong(sedentary_time);
        float distance = Float.parseFloat(Tracing_distance);
        System.out.println(" long time : " + time + " float distance : " + distance);

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, locationListener);
        return START_STICKY;

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                Intent i = new Intent("location_update");

                longtitude = location.getLongitude();
                latitude = location.getLatitude();
                i.putExtra(longtitude_key, latitude);
                i.putExtra(latitude_key, latitude);

                sendBroadcast(i);

                //notification builder
                Notification_builder();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };


        //init notification
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(CHANNEL_DES);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public void Notification_builder() {
        Intent detail_intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, detail_intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder N_builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.messages)
                .setContentTitle("Notification Ttile")
                .setContentText("Notification contentext")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, N_builder.build());

    }
}

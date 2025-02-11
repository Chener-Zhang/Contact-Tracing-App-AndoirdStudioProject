package edu.temple.contacttracer;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Location_Service extends Service {

    //Location component
    public LocationManager locationManager;
    public LocationListener locationListener;

    // longtitude and latitude
    public double longtitude;
    public double latitude;

    //Notification component
    public static String CHANNEL_ID = "channel_id";
    public static String CHANNEL_NAME = "channel_name";
    public static String CHANNEL_DES = "channel_description";

    //Tracing_Distance and Sedentary_time
    public String Tracing_distance;
    public String Sedentary_time;

    //Location time
    public long sedentary_begin;
    public long sedentary_end;

    //Location change
    Location last_location;

    //isMoving
    public boolean stop_moving = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Tracing_distance = intent.getStringExtra("distance");
        Sedentary_time = intent.getStringExtra("time");

        long time = Long.parseLong(Sedentary_time);
        float distance = Float.parseFloat(Tracing_distance);

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

                sedentary_begin = location.getTime();

                if (last_location != null) {
                    sedentary_end = last_location.getTime();
                } else {
                    last_location = location;
                }

                Intent i = new Intent("location_update");

                longtitude = location.getLongitude();
                latitude = location.getLatitude();

                i.putExtra(CONSTANT.LONGTITUDE_KEY, longtitude);
                i.putExtra(CONSTANT.LATITUDE_KEY, latitude);
                i.putExtra(CONSTANT.SENDENTARY_BEGIN_KEY, sedentary_begin);
                i.putExtra(CONSTANT.SENDENTARY_END_KEY, sedentary_end);


                if (location.getTime() - last_location.getTime() >= (Integer.parseInt(String.valueOf(Sedentary_time))) && last_location.getLatitude() == location.getLatitude() && last_location.getLongitude() == location.getLongitude()) {
                    stop_moving = true;
                }

                i.putExtra(CONSTANT.STOP_MOVING, stop_moving);

                //send broadcast
                sendBroadcast(i);
                stop_moving = false;
                last_location = location;

                //build notification
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


        //Init notification
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(CHANNEL_DES);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
        //Init Completed
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

        //Notification Builder
        NotificationCompat.Builder N_builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.messages)
                .setContentTitle("Tracing App")
                .setContentText("You are moving ! ! !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(1, N_builder.build());

    }
}

package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    //Button
    public Button start_button;
    public Button stop_button;
    public Button setting_button;

    //Notification component
    public static String CHANNEL_ID = "channel_id";
    public static String CHANNEL_NAME = "channel_name";
    public static String CHANNEL_DES = "channel_description";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Buttons
        button_init();

        //Check SKD

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription(CHANNEL_DES);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);


    }

    public void Notification_builder() {
        Intent detail_intent = new Intent(this, Notification_Detail.class);
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


    public void button_init() {
        start_button = (Button) findViewById(R.id.start_button);
        stop_button = (Button) findViewById(R.id.stop_button);
        setting_button = (Button) findViewById(R.id.setting_button);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("start button trigger");
                Notification_builder();

            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("stop button trigger");
            }
        });

        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("setting trigger");
                //start with fragment transaction
                setting_fragement_transaction();

            }
        });
    }

    public void setting_fragement_transaction() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //create a new setting fragment
        SettingFragment settingFragment = SettingFragment.newInstance(null, null);
        fragmentTransaction.replace(R.id.fragment_container, settingFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

}
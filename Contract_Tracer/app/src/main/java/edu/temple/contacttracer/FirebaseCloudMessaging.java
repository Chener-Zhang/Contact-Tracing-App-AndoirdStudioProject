package edu.temple.contacttracer;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseCloudMessaging extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String s) {
        Log.d("FCM Notification", s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String json = remoteMessage.getData().get("payload");


        if (remoteMessage.getNotification() != null) {
            Log.d("RemoteMessage not equal to null FCM Notification", remoteMessage.getNotification().getBody());
        } else {
            try {
                Log.d("FCM Data: ", json);
                JSONObject jsonObject = new JSONObject(json);
                System.out.println(jsonObject.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}

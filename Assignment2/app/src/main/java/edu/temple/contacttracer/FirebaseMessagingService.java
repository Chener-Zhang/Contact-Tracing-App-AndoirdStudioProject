package edu.temple.contacttracer;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        System.out.println("FirebaseMessagingService got the token : " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String message = remoteMessage.getData().get("payload");
        if (remoteMessage.getNotification() != null) {
            System.out.println("FirebaseMessagingService FCM Notification" + remoteMessage.getNotification().getBody());
        } else {
            System.out.println("FirebaseMessagingService FCM Data" + message);
        }

        Intent messageIntent = new Intent(getPackageName() + "CHAT_MESSAGE");
        messageIntent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

    }
}

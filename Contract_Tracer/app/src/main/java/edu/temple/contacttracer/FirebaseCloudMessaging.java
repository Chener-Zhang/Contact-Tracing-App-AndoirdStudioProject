package edu.temple.contacttracer;


import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseCloudMessaging extends FirebaseMessagingService {


    public String mylocation;
    public String tracking_json;
    public String tracing_json;


    @Override
    public void onNewToken(@NonNull String s) {
        Log.d("FCM Notification", s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Get datas
        Intent message_from_FCM = new Intent(getPackageName() + ".CHAT_MESSAGE");
        tracking_json = remoteMessage.getData().get("payload");
        tracing_json = remoteMessage.getData().get("payload");


        if (remoteMessage.getFrom().equals("/topics/TRACING")) {

            try {
                JSONObject jsonObject = new JSONObject(tracing_json);
                Log.d("FROM TRACING", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            message_from_FCM.putExtra(CONSTANT.JSON_FROM_BROADCAST_TRACING, tracing_json);

        } else {
            if (remoteMessage.getNotification() != null) {
                Log.d("RemoteMessage not equal to null FCM Notification", remoteMessage.getNotification().getBody());

            } else {
                try {
                    Log.d("FCM Data From Tracking: ", tracking_json);
                    JSONObject jsonObject = new JSONObject(tracking_json);

                    try {
                        String other_uuid = jsonObject.getString(CONSTANT.UUID);
                        //Check with if uuid is my from the server
                        if (other_uuid == CONSTANT.MY_UUID) {
                            mylocation = tracking_json;
                            Log.d("other_uuid == CONSTANT.MY_UUID", mylocation);
                        } else {
                            mylocation = "mylocation not list here";
                        }
                    } catch (Exception e) {
                        Log.d("Error  ", "Somebody sending something else than REGULAR imformation");
                        Log.d("Error  ", e.toString());
                    }

                    System.out.println(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //PASS THE DATA TO INTENT
            message_from_FCM.putExtra(CONSTANT.JSON_FROM_BROADCAST_TRACKING, tracking_json);
            message_from_FCM.putExtra(CONSTANT.MYLOCATION, mylocation);

        }
        //SEND THE BROAD CASE
        LocalBroadcastManager.getInstance(this).sendBroadcast(message_from_FCM);

    }

}

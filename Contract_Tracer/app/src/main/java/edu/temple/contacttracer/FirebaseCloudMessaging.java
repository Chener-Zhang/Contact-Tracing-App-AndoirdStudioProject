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

                try {
                    String other_uuid = jsonObject.getString(CONSTANT.UUID);
                    double other_latitude = Double.parseDouble(jsonObject.getString(CONSTANT.LATITUDE));
                    double other_longtitude = Double.parseDouble(jsonObject.getString(CONSTANT.LONGTITUDE));
                    long other_sedentary_begin = Long.parseLong(jsonObject.getString(CONSTANT.SEDENTARY_BEGIN));
                    long other_sedentary_end = Long.parseLong(jsonObject.getString(CONSTANT.SEDENTARY_END));

                    if (other_uuid == CONSTANT.MY_UUID) {
                        mylocation = json;
                        Log.d("Get the location ", mylocation);
                    } else {
                        mylocation = "mylocation not list here";
//                        Log.d("Mylocation", mylocation);
                    }
                } catch (Exception e) {
                    Log.d("Error  ", "somebody sending something else than regular information");
                    Log.d("Error  ", e.toString());
                }


                System.out.println(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent message_from_FCM = new Intent(getPackageName() + ".CHAT_MESSAGE");
        message_from_FCM.putExtra(CONSTANT.JSON_FROM_BROADCAST, json);
        message_from_FCM.putExtra(CONSTANT.MYLOCATION, mylocation);

        LocalBroadcastManager.getInstance(this).sendBroadcast(message_from_FCM);

    }

}

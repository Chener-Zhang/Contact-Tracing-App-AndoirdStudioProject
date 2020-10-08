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
        if (remoteMessage.getFrom().equals("/topics/TRACING")) {
            Log.d("Tracing ", "Data Received");
            String json = remoteMessage.getData().get("payload");
//            Log.d("FCM Data From Tracing: ", json);

            try {
                JSONObject jsonObject = new JSONObject(json);


                Log.d("FROM TRACING", jsonObject.toString());

                Log.d("GET THE UUIDS:", jsonObject.getJSONArray("uuids").toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            //Get datas
            String json = remoteMessage.getData().get("payload");

            if (remoteMessage.getNotification() != null) {
                Log.d("RemoteMessage not equal to null FCM Notification", remoteMessage.getNotification().getBody());

            } else {
                try {
                    Log.d("FCM Data From Tracking: ", json);
                    JSONObject jsonObject = new JSONObject(json);

                    try {
                        String other_uuid = jsonObject.getString(CONSTANT.UUID);
                        double other_latitude = Double.parseDouble(jsonObject.getString(CONSTANT.LATITUDE));
                        double other_longtitude = Double.parseDouble(jsonObject.getString(CONSTANT.LONGTITUDE));
                        long other_sedentary_begin = Long.parseLong(jsonObject.getString(CONSTANT.SEDENTARY_BEGIN));
                        long other_sedentary_end = Long.parseLong(jsonObject.getString(CONSTANT.SEDENTARY_END));

                        //Check with if uuid is my from the server
                        if (other_uuid == CONSTANT.MY_UUID) {
                            mylocation = json;
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


            Intent message_from_FCM = new Intent(getPackageName() + ".CHAT_MESSAGE");


            //PASS THE DATA TO INTENT
            message_from_FCM.putExtra(CONSTANT.JSON_FROM_BROADCAST, json);
            message_from_FCM.putExtra(CONSTANT.MYLOCATION, mylocation);

            //SEND THE BROAD CASE
            LocalBroadcastManager.getInstance(this).sendBroadcast(message_from_FCM);
        }


    }

}

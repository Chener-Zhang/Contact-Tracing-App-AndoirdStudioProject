package edu.temple.contacttracer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements value_sender {

    //Button
    public Button start_button;
    public Button stop_button;
    public Button token_generator;
    public Button clear_button;


    //User input declaration
    public String userinput_distance;
    public String userinput_time;


    //Tool bar
    public Toolbar toolbar;
    public MenuItem menuItem;


    //URL
    public static String tracking_url = "https://kamorris.com/lab/ct_tracking.php";
    //Dynamic Variable;
    public UUID uuid;
    public double longtitude;
    public double latitude;
    public long sedentary_begin;
    public long sedentary_end;
    public static String tracing_url = "https://kamorris.com/lab/ct_tracing.php";
    public MenuItem date_picker;
    public Button get_sick_button;


    //Firebase intent
    Intent firebase_intent;
    public Button Get_token;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    //Token
    Token_Container ALL_token_container;
    Token_Container list_retrieve_token_container;

    //Gson
    Gson gson;

    //Spinner
    public boolean stop_moving;


    //My location
    String mylocation;

    //JsonArray UUIDS and long DATE
    JSONArray jsonArray;
    long date_long;


    //Receiver
    public BroadcastReceiver broadcastReceiver;

    //FCM Broad Cast Receiver
    IntentFilter FCM_IntentFilter;


    BroadcastReceiver FCM_BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String tracking_json = intent.getStringExtra(CONSTANT.JSON_FROM_BROADCAST_TRACKING);
            String tracing_json = intent.getStringExtra(CONSTANT.JSON_FROM_BROADCAST_TRACING);

            mylocation = intent.getStringExtra(CONSTANT.MYLOCATION);

            try {
                if (tracking_json != null) {
                    JSONObject jsonObject = new JSONObject(tracking_json);

                    String uuid_in_string = (String) jsonObject.get(CONSTANT.UUID);
                    double latitude = jsonObject.getDouble(CONSTANT.LATITUDE);
                    double longtitude = jsonObject.getDouble(CONSTANT.LONGTITUDE);
                    long sedentary_begin = jsonObject.getLong(CONSTANT.SEDENTARY_BEGIN);
                    long sedentary_end = jsonObject.getLong(CONSTANT.SEDENTARY_END);

                    UUID uuid = UUID.fromString(uuid_in_string);

                    //Token(double latitude, double longtitude, long sedentary_begin, long sedentary_end, LocalDate date)
                    Token other_tocken = new Token(uuid, latitude, longtitude, sedentary_begin, sedentary_end);
                    ALL_token_container.others_add(other_tocken);
                    ALL_token_container.discard_repeate();

                    String other_json = gson.toJson(ALL_token_container);
                    editor.putString(CONSTANT.TO_JSON, other_json);
                    editor.commit();

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (tracing_json != null) {
                    Log.d("BRAOD CAST FROM TRACING TO MAIN ACTIVITIES", "RECEIVED");
                    list_retrieve();
                    //-test--------->
                    JSONObject object = new JSONObject(tracing_json);
                    String all_uuids = object.get(CONSTANT.UUIDS).toString();
                    String my_uuids = ALL_token_container.get_all_my_uuid().toString();

//                    System.out.println("------------------------------");
//                    System.out.println(all_uuids);
//                    System.out.println("------------------------------");
//                    System.out.println(my_uuids);
//                    System.out.println("------------------------------");

                    //others
                    if (!all_uuids.equals(my_uuids)) {


                        Log.d("Detected", "someone get sick");
                        Log.d("Receive Report", tracing_json);

                        JSONObject other_uuids_json_object = new JSONObject(tracing_json);
                        JsonArray other_uuids = (JsonArray) other_uuids_json_object.get(CONSTANT.UUIDS);

                        for (JsonElement id : other_uuids) {
                            Log.d("ID", id.toString());
                        }


                    }
                    //ignore my uuid
                    else {

                        System.out.println("\n\n you are fine \n\n");
                    }
                    //-test--------->
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INITIALIZATION :

        //init the share preference
        sharedpreferences = getSharedPreferences(CONSTANT.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        //init Token container
        ALL_token_container = new Token_Container();
        list_retrieve_token_container = new Token_Container();

        //init toolbar init
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //init Gson
        gson = new Gson();

        //init FCM Broadcast receiver
        FCM_IntentFilter = new IntentFilter(getPackageName() + ".CHAT_MESSAGE");

        //init Firebase Cloud Messaging
        firebase_intent = new Intent(this, FirebaseCloudMessaging.class);


        //Set button and click listener
        button_init();

        //Permission check
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                Log.d("FCM Token f/Activities", task.getResult().getToken());
                Log.d("FCM Id f/Activities", task.getResult().getId());
            }
        });
        //Subscribe_Traking
        subscribe_Tracking();

        //Subscribe_Tracing
        subscribe_Tracing();


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    uuid = (UUID) intent.getExtras().get(CONSTANT.UUID);
                    longtitude = (double) intent.getExtras().get(CONSTANT.LONGTITUDE_KEY);
                    latitude = (double) intent.getExtras().get(CONSTANT.LATITUDE_KEY);
                    sedentary_begin = (long) intent.getExtras().get(CONSTANT.SENDENTARY_BEGIN_KEY);
                    sedentary_end = (long) intent.getExtras().get(CONSTANT.SENDENTARY_END_KEY);
                    stop_moving = (boolean) intent.getExtras().get(CONSTANT.STOP_MOVING);


                    Log.d(CONSTANT.STOP_MOVING, stop_moving + "");

                    //If stop moving -> send the a post request
                    if (stop_moving) {
                        Token token = new Token(null, latitude, longtitude, sedentary_begin, sedentary_end);
                        ALL_token_container.mine_add(token);
                        uuid = token.uuid;
                        String json = gson.toJson(ALL_token_container);
                        editor.putString(CONSTANT.TO_JSON, json);
                        editor.commit();

                        send_tracking_post_request();
                    }

                }
            };
        }

        //register receiver
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
        LocalBroadcastManager.getInstance(this).registerReceiver(FCM_BroadcastReceiver, FCM_IntentFilter);
    }

    //Menu Fragment Passing
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);

        //init the button0
        menuItem = menu.findItem(R.id.setting_menu);
        date_picker = menu.findItem(R.id.date_picker);


        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                setting_fragement_transaction();
                return true;
            }
        });

        date_picker.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.d("Date Picker Button", "Clicked");
                setDate_picker_fragment_transaction();
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(FCM_BroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }


    /*
    Callback for permission check
    Call back of permission result;
    Return true if pass checking false if not
    */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // User permission granted fail;
            Log.d("USER GPD PERMISSION :  ", " FAIL ");
            finish();
        } else {
            permission_checking();
        }
    }

    public boolean permission_checking() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    //Get the data from the menu fragment

    @Override
    public void get_message(String distance, String time) {
        userinput_distance = distance;
        userinput_time = time;
    }

    //Rebot the emulator retrive the list from the share preference

    public void list_retrieve() {
        String json = sharedpreferences.getString(CONSTANT.TO_JSON, null);
        list_retrieve_token_container = gson.fromJson(json, Token_Container.class);

        try {
            list_retrieve_token_container.expire_days_checker();
            System.out.println("list retrieve Mine: \n" + list_retrieve_token_container.print_mine_tokens());
            System.out.println("list retrieve Others: \n" + list_retrieve_token_container.print_others_tokens());

        } catch (Exception e) {
            System.out.println("it is empty");
        }


    }

    public void subscribe_Tracking() {
        FirebaseMessaging.getInstance().subscribeToTopic("TRACKING")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Subscribe Tracking", "Fail");
                        }
                        Log.d("Subscribe : Tracking", "Success");
                    }
                });
    }

    public void subscribe_Tracing() {
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/TRACING")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Subscribe Tracing", "Fail");
                        }
                        Log.d("Subscribe : Tracing", "Success");
                    }
                });
    }

    public void button_init() {
        start_button = findViewById(R.id.start_button);
        stop_button = findViewById(R.id.stop_button);
        token_generator = findViewById(R.id.token_generator);
        clear_button = findViewById(R.id.clear);
        Get_token = findViewById(R.id.get_tocken);
        get_sick_button = findViewById(R.id.send_report);

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.commit();
                ALL_token_container.clear_mine();
                ALL_token_container.clear_others();
                System.out.println("Tokens have been all clear");
            }
        });

        Get_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_retrieve();
            }
        });

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Location_Service.class);
                intent.putExtra("distance", userinput_distance);
                intent.putExtra("time", userinput_time);
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

        get_sick_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Sick Button", "Clicked");
                send_tracing_post_request();
            }
        });

        token_generator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Token token = new Token(null, latitude, longtitude, sedentary_begin, sedentary_end);
                ALL_token_container.mine_add(token);

                String json = gson.toJson(ALL_token_container);
                editor.putString(CONSTANT.TO_JSON, json);
                editor.commit();
            }
        });

    }
    //Fragment connection

    public void setting_fragement_transaction() {

        //Basic Fragment Manager setup
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //create a new setting fragment
        SettingFragment settingFragment = SettingFragment.newInstance(null, null);
        fragmentTransaction.replace(R.id.fragment_container, settingFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void setDate_picker_fragment_transaction() {
        //Basic Fragment Manager setup
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //create a new Date time picker fragment
        Date_time_Picker_VIEW date_time_picker_view = Date_time_Picker_VIEW.newInstance(null, null);
        fragmentTransaction.replace(R.id.fragment_container, date_time_picker_view).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void send_tracking_post_request() {
        //POST REQUEST BEGIN
        RequestQueue postqueue = Volley.newRequestQueue(this);
        StringRequest postquest = new StringRequest(Request.Method.POST, tracking_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response from post request " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("get the VolleyError " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put(CONSTANT.UUID, String.valueOf(uuid));
                params.put(CONSTANT.LATITUDE, String.valueOf(latitude));
                params.put(CONSTANT.LONGTITUDE, String.valueOf(longtitude));
                params.put(CONSTANT.SEDENTARY_BEGIN, String.valueOf(sedentary_begin));
                params.put(CONSTANT.SEDENTARY_END, String.valueOf(sedentary_end));


                Log.d("TRACKING MESSAGE", "SEND");
                return params;

            }
        };
        postqueue.add(postquest);
        //POST REQUEST END
    }

    public void send_tracing_post_request() {
        //POST REQUEST BEGIN
        RequestQueue postqueue = Volley.newRequestQueue(this);
        StringRequest postquest = new StringRequest(Request.Method.POST, tracing_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response from post request " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("get the VolleyError " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                list_retrieve();

                //send all my uuids
                jsonArray = list_retrieve_token_container.get_all_my_uuid();
                date_long = Instant.now().toEpochMilli();


                params.put(CONSTANT.UUIDS, jsonArray.toString());
                params.put(CONSTANT.DATE, String.valueOf(date_long));


                Log.d("TRACING MESSAGE", "SEND");

                return params;

            }
        };
        postqueue.add(postquest);
        //POST REQUEST END

    }


}


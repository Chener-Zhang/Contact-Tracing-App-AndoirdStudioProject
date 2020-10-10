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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements value_sender {

    //Button
    public Button start_button;
    public Button stop_button;
    public Button token_generator;
    public Button clear_button;
    //Receive the message from the FCM class . Include Tracking and Tracing message
    final BroadcastReceiver FCM_BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String tracking_json = intent.getStringExtra(CONSTANT.JSON_FROM_BROADCAST_TRACKING);
            String tracing_json = intent.getStringExtra(CONSTANT.JSON_FROM_BROADCAST_TRACING);

            mylocation = intent.getStringExtra(CONSTANT.MYLOCATION);

            //Retrieve tracking FCM message;
            try {
                if (tracking_json != null) {
                    JSONObject jsonObject = new JSONObject(tracking_json);

                    String uuid_in_string = (String) jsonObject.get(CONSTANT.UUID);
                    double latitude = jsonObject.getDouble(CONSTANT.LATITUDE);
                    double longtitude = jsonObject.getDouble(CONSTANT.LONGTITUDE);
                    long sedentary_begin = jsonObject.getLong(CONSTANT.SEDENTARY_BEGIN);
                    long sedentary_end = jsonObject.getLong(CONSTANT.SEDENTARY_END);

                    try {
                        UUID uuid = UUID.fromString(uuid_in_string);
                        //Token(double latitude, double longtitude, long sedentary_begin, long sedentary_end, LocalDate date)
                        Token other_tocken = new Token(uuid, latitude, longtitude, sedentary_begin, sedentary_end);

                        temporary_token_container.others_add(other_tocken);
                        temporary_token_container.discard_repeate();

                        String json = gson.toJson(temporary_token_container);
                        editor.putString(CONSTANT.TO_JSON, json);
                        editor.commit();

                    } catch (Exception e) {
                        Log.d("Error", "Someone send something else");
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Retrieve tracing FCM message
            try {
                if (tracing_json != null) {
                    Log.d("BRAOD CAST FROM TRACING TO MAIN ACTIVITIES", "RECEIVED");

                    list_retrieve();

                    JSONObject object = new JSONObject(tracing_json);
                    JSONArray all_uuids_jsonAray = object.getJSONArray(CONSTANT.UUIDS);

                    others_tracing_uuids_jsonArray = filter_uuids_return_other(my_uuids_jsonArray, all_uuids_jsonAray);

                    //add the positive date to the calendar fragment
                    positive_report_date.add((Long) object.get(CONSTANT.DATE));

                    Log.d("POSITIVE_REPORT_DATE", positive_report_date.toString());


                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    };


    //User input declaration
    public String userinput_distance;
    public String userinput_time;


    //Tool bar
    public Toolbar toolbar;
    public MenuItem menuItem;


    //URL
    public static String tracking_url = "https://kamorris.com/lab/ct_tracking.php";
    public static String tracing_url = "https://kamorris.com/lab/ct_tracing.php";

    //Dynamic Variable;
    public UUID uuid;
    public double longtitude;
    public double latitude;
    public long sedentary_begin;
    public long sedentary_end;
    public MenuItem date_picker;
    public Button get_sick_button;


    //Firebase intent
    Intent firebase_intent;
    public Button Get_token;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    //Token
    Token_Container temporary_token_container;
    Token_Container list_retrieve_token_container;

    //Gson
    Gson gson;

    //Spinner
    public boolean stop_moving;


    //My location
    String mylocation;

    //JsonArray UUIDS and long DATE
    JSONArray others_tracing_uuids_jsonArray;
    JSONArray my_uuids_jsonArray;
    long date_long;


    //Receiver
    public BroadcastReceiver broadcastReceiver;

    //FCM Broad Cast Receiver
    IntentFilter FCM_IntentFilter;


    //Date for positive report
    List<Long> positive_report_date;
    public Button test_map;
    //save boolean;
    public boolean issave = false;

    public ArrayList<Token> matching(JSONArray others_filtered_uuids) throws JSONException {
        ArrayList<Token> suspecious_token = new ArrayList<Token>();

        for (Token token : list_retrieve_token_container.Other_tokenArrayList) {
            for (int i = 0; i < others_filtered_uuids.length(); i++) {
                if (token.uuid.equals(others_filtered_uuids.get(i))) {
                    suspecious_token.add(token);
                }
            }
        }
        return suspecious_token;
    }

    public JSONArray filter_uuids_return_other(JSONArray mine_uuids, JSONArray others_uuids) throws JSONException {
        JSONArray return_values = new JSONArray();


        ArrayList<String> mine_list = new ArrayList<String>();
        ArrayList<String> other_list = new ArrayList<String>();

        for (int i = 0; i < mine_uuids.length(); i++) {
            mine_list.add(mine_uuids.get(i).toString());
        }
        for (int i = 0; i < others_uuids.length(); i++) {
            other_list.add(others_uuids.get(i).toString());
        }
        System.out.println("\n\n------------------------------------------------\n\n");
        Log.d("ALL UUIDS", other_list.toString());
        Log.d("Mine UUIDS", mine_list.toString());
        System.out.println("\n\n------------------------------------------------\n\n");
        //remove repetition
        other_list.removeAll(mine_list);

        //maybe require convert string to uuid
        for (String s : other_list) {
            return_values.put(UUID.fromString(s));
        }
        Log.d("RETURN VALUE AFTER FILTER", return_values.toString());
        return return_values;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INITIALIZATION :

        //init the share preference
        sharedpreferences = getSharedPreferences(CONSTANT.MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        //init Token container
        temporary_token_container = new Token_Container();
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

        //init others dates
        positive_report_date = new ArrayList<Long>();

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
                        temporary_token_container.mine_add(token);
                        uuid = token.uuid;

                        String json = gson.toJson(temporary_token_container);
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

        if (temporary_token_container.My_tokenArrayList.isEmpty()) {
            Log.d("ERROR", "YOU DOES NOT SAVE THE FILE");
        } else {
            String save_file = gson.toJson(temporary_token_container);
            editor.putString(CONSTANT.TO_JSON, save_file);
            editor.commit();
            Log.d("SUCCESS", "YOU SAVE THE FILE");
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        if (temporary_token_container.My_tokenArrayList.isEmpty()) {
            Log.d("ERROR", "YOU DOES NOT SAVE THE FILE");
        } else {
            String save_file = gson.toJson(temporary_token_container);
            editor.putString(CONSTANT.TO_JSON, save_file);
            editor.commit();
            Log.d("SUCCESS", "YOU SAVE THE FILE");
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
        String restore_data = sharedpreferences.getString(CONSTANT.TO_JSON, null);

        try {
            list_retrieve_token_container = gson.fromJson(restore_data, Token_Container.class);
            list_retrieve_token_container.expire_days_checker();
            temporary_token_container = list_retrieve_token_container;
        } catch (Exception e) {
            System.out.println("CONTAINER EMPTY");
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
        test_map = findViewById(R.id.test_map);


        test_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map_fragement_transaction();
            }
        });
        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.commit();
                try {
                    temporary_token_container.clear_mine();
                    temporary_token_container.clear_others();
                    issave = false;
                    System.out.println("Tokens have been all clear");
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });

        Get_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_retrieve();
                try {
                    System.out.println("list retrieve Mine: \n -------------------------------------------\n" + list_retrieve_token_container.print_mine_tokens());
                    System.out.println("list retrieve Others: \n-----------------------------------------\n" + list_retrieve_token_container.print_others_tokens());
                } catch (Exception e) {
                    Log.d("EXCEPTION: ", "LIST ARE EMPTY");
                }
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

                temporary_token_container.mine_add(token);
                String json = gson.toJson(temporary_token_container);
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

    public void map_fragement_transaction() {

        //Basic Fragment Manager setup
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //create a new setting fragment
        TracingFragment tracingFragment = TracingFragment.newInstance(null, null);
        fragmentTransaction.replace(R.id.fragment_container, tracingFragment).addToBackStack(null);
        fragmentTransaction.commit();
    }


    public void setDate_picker_fragment_transaction() {
        //Basic Fragment Manager setup
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        long[] arr = new long[positive_report_date.size()];
        int i = 0;
        for (Long value : positive_report_date) {
            arr[i++] = value;
        }

        //create a new Date time picker fragment
        Date_time_Picker_VIEW date_time_picker_view = Date_time_Picker_VIEW.newInstance(arr);
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

                try {
                    my_uuids_jsonArray = list_retrieve_token_container.get_all_my_uuid();
                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                }
                //send all my uuids
                date_long = Instant.now().toEpochMilli();

                params.put(CONSTANT.UUIDS, my_uuids_jsonArray.toString());
                params.put(CONSTANT.DATE, String.valueOf(date_long));


                Log.d("TRACING MESSAGE", "SEND");

                return params;

            }
        };
        postqueue.add(postquest);
        //POST REQUEST END

    }


}


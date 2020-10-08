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

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements value_sender {

    //Button
    public Button start_button;
    public Button stop_button;
    public Button token_generator;
    public Button clear_button;
    public static String tracking_url = "https://kamorris.com/lab/ct_tracking.php";


    //User input declaration
    public String userinput_distance;
    public String userinput_time;


    //Tool bar
    public Toolbar toolbar;
    public MenuItem menuItem;


    //Dynamic Variable;
    public double longtitude;
    public double latitude;
    public long sedentary_begin;
    public long sedentary_end;

    //URL
    public static String tracing_url = "https://kamorris.com/lab/ct_tracing.php";
    public Button get_sick_button;
    //Firebase intent
    Intent firebase_intent;
    public Button Get_token;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    //Token
    Token_Container temporary_token_container;
    Token_Container current_token_container;

    //Gson
    Gson gson;

    //Spinner
    public boolean stop_moving;


    //My location
    String mylocation;


    //Receiver
    public BroadcastReceiver broadcastReceiver;
    BroadcastReceiver FCM_BroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra("json_file");
            mylocation = intent.getStringExtra(CONSTANT.MYLOCATION);
            Log.d("Main Activity json receive", json);

        }
    };
    //FCM Broad Cast Receiver
    IntentFilter FCM_IntentFilter;

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
        current_token_container = new Token_Container();

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
                    longtitude = (double) intent.getExtras().get(CONSTANT.LONGTITUDE_KEY);
                    latitude = (double) intent.getExtras().get(CONSTANT.LATITUDE_KEY);
                    sedentary_begin = (long) intent.getExtras().get(CONSTANT.SENDENTARY_BEGIN_KEY);
                    sedentary_end = (long) intent.getExtras().get(CONSTANT.SENDENTARY_END_KEY);
                    stop_moving = (boolean) intent.getExtras().get(CONSTANT.STOP_MOVING);
                    Log.d(CONSTANT.STOP_MOVING, stop_moving + "");

                    //If stop moving -> send the a post request
                    if (stop_moving) {
                        send_post_request(tracking_url);
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
        menuItem = menu.findItem(R.id.setting_menu);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                setting_fragement_transaction();
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
        String json = sharedpreferences.getString("tojson", null);
        current_token_container = gson.fromJson(json, Token_Container.class);

        try {
            current_token_container.expire_days_checker();
            System.out.println("list retrieve : " + current_token_container.print());
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
        start_button = (Button) findViewById(R.id.start_button);
        stop_button = (Button) findViewById(R.id.stop_button);
        token_generator = (Button) findViewById(R.id.token_generator);
        clear_button = (Button) findViewById(R.id.clear);
        Get_token = (Button) findViewById(R.id.get_tocken);
        get_sick_button = (Button) findViewById(R.id.get_sicks);

        clear_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.commit();
                temporary_token_container.clear();
                System.out.println("Tokens have been all clear");
            }
        });
        token_generator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("button", "trigger");
                Log.d("Long", String.valueOf(longtitude));
                Log.d("La", String.valueOf(longtitude));

                Token token = new Token(latitude, longtitude, sedentary_begin, sedentary_end);
                temporary_token_container.add(token);

                String json = gson.toJson(temporary_token_container);
                editor.putString(CONSTANT.TO_JSON, json);
                editor.commit();
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
                send_post_request(tracing_url);
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


    public void send_post_request(String url) {

        //POST REQUEST BEGIN
        RequestQueue postqueue = Volley.newRequestQueue(this);
        StringRequest postquest = new StringRequest(Request.Method.POST, url,
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
                params.put(CONSTANT.UUID, "tuh12085");
                params.put(CONSTANT.LATITUDE, String.valueOf(latitude));
                params.put(CONSTANT.LONGTITUDE, String.valueOf(longtitude));
                params.put(CONSTANT.SEDENTARY_BEGIN, String.valueOf(sedentary_begin));
                params.put(CONSTANT.SEDENTARY_END, String.valueOf(sedentary_end));
                return params;

            }
        };
        postqueue.add(postquest);
        //POST REQUEST END
    }


}


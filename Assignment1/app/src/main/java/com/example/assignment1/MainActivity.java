package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {


    public Button start_button;
    public Button stop_button;
    public Button setting_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Buttons
        button_init();

    }

    public void button_init() {
        start_button = (Button) findViewById(R.id.start_button);
        stop_button = (Button) findViewById(R.id.stop_button);
        setting_button = (Button) findViewById(R.id.setting_button);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("start button trigger");
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
        fragmentTransaction.replace(R.id.fragment_container,settingFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

}
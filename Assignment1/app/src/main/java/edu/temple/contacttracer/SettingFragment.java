package edu.temple.contacttracer;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.contacttracer.R;


interface value_sender {
    public void get_message(String distance, String time);
}

public class SettingFragment extends Fragment {


    //Text input and button;
    public EditText TRACING_DISCANTE;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public EditText SEDEMTARY_TIME;
    public Button SUBMIT_BUTTON;

    public value_sender value_sender;

    public SettingFragment() {
    }


    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof value_sender) {
            value_sender = (value_sender) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);


        //edit text and button setup
        TRACING_DISCANTE = view.findViewById(R.id.tracing_distance);
        SEDEMTARY_TIME = view.findViewById(R.id.sedentary_time);
        SUBMIT_BUTTON = view.findViewById(R.id.submit_button);


        //edit text and button setup complete
        SUBMIT_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("button clicked");
                String x = TRACING_DISCANTE.getText().toString();
                String y = SEDEMTARY_TIME.getText().toString();
                value_sender.get_message(x, y);
            }
        });

        return view;
    }

}
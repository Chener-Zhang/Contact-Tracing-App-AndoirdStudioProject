package edu.temple.contacttracer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class Date_time_Picker_VIEW extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Date_time_Picker_VIEW() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Date_time_Picker_VIEW newInstance(String param1, String param2) {
        Date_time_Picker_VIEW fragment = new Date_time_Picker_VIEW();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View this_view = inflater.inflate(R.layout.fragment_time_picker_dialog, container, false);


        return this_view;
    }
}
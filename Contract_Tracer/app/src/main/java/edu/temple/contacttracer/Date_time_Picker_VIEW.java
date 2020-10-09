package edu.temple.contacttracer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.Arrays;


public class Date_time_Picker_VIEW extends Fragment {
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private long[] positive_dates;


    public Date_time_Picker_VIEW() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Date_time_Picker_VIEW newInstance(long[] param1) {
        Date_time_Picker_VIEW fragment = new Date_time_Picker_VIEW();
        Bundle args = new Bundle();
        args.putLongArray(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            positive_dates = getArguments().getLongArray(ARG_PARAM1);
            Log.d("REICEVE long[]", Arrays.toString(positive_dates));
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View this_view = inflater.inflate(R.layout.fragment_time_picker_dialog, container, false);


        return this_view;
    }
}
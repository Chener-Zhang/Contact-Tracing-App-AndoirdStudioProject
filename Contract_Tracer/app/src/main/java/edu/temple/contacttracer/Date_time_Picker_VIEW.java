package edu.temple.contacttracer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.Calendar;


public class Date_time_Picker_VIEW extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private long[] positive_dates;


    DatePicker calendar;
    TextView textView;
    View view;

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

        view = inflater.inflate(R.layout.fragment_time_picker_dialog, container, false);

        textView = view.findViewById(R.id.test_result);
        calendar = view.findViewById(R.id.my_calendar);


        calendar.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                System.out.println("date changed");
                if (show_Positive_dates(year, monthOfYear, dayOfMonth)) {
                    textView.setText("SOME ONE GET SICK");
                }
            }
        });
        return view;
    }

    public boolean show_Positive_dates(int the_year, int the_month, int the_date) {
        Calendar calendar = Calendar.getInstance();
        int date;
        int month;
        int year;

        for (long l : positive_dates) {
            long milliSeconds = l;
            calendar.setTimeInMillis(milliSeconds);
            date = calendar.get(Calendar.DATE);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
            if (date == the_date && the_year == year && the_month == month) {
                return true;
            }
        }
        return false;
    }


}
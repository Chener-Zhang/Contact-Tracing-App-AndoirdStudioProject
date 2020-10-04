package edu.temple.contacttracer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StartupFragment extends Fragment {

    FragmentInteractionInterface parent;
    Button startButton, stopButton;

    public StartupFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This is how we let the activity know that we have an ActionBar
        // item that we would like to have displayed
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionInterface) {
            parent = (FragmentInteractionInterface) context;
        } else {
            throw new RuntimeException("Please implement FragmentInteractionInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_startup, container, false);
        startButton = v.findViewById(R.id.startServiceButton);
        stopButton = v.findViewById(R.id.stopServiceButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.startService();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.stopService();
            }
        });


        return v;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            parent.settingsMenu();
            return true;
        } else {
            return false;
        }
    }

    interface FragmentInteractionInterface {
        void startService();

        void stopService();

        void settingsMenu();

    }
}
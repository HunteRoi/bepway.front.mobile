package com.henallux.bepway.features.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.henallux.bepway.R;
import com.henallux.bepway.features.activities.MainActivity;

public class LogFragment extends Fragment {
    public LogFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        Button logButton = view.findViewById(R.id.logButton);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mettre en place la connection
                Intent intent = new Intent(getActivity(), MainActivity.class);
                Log.i("PhoneCrash", "intent created");
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}

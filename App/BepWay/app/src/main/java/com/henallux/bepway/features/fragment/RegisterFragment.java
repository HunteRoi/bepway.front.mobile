package com.henallux.bepway.features.fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.henallux.bepway.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterFragment extends Fragment {

    private TextView displayDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Date birthDate;
    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordCheckInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button createButton = view.findViewById(R.id.createAccountButton);
        Button cancelButton = view.findViewById(R.id.cancelAccountButton);
        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        passwordCheckInput = view.findViewById(R.id.passwordCheckInput);
        emailInput = view.findViewById(R.id.emailInput);
        displayDate = view.findViewById(R.id.registerBirthdate);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // + creation et insertion BD
                checkForm();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameInput.getText().clear();
                usernameInput.setHint(R.string.username_hint);
                passwordInput.getText().clear();
                passwordInput.setHint(R.string.password_hint);
                passwordCheckInput.getText().clear();
                passwordCheckInput.setHint(R.string.check_password_hint);
                emailInput.getText().clear();
                emailInput.setHint(R.string.email_hint);
                displayDate.setText("");
                displayDate.setHint(R.string.birthdate_hint);
            }
        });

        displayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener, year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth+"/"+(month+1)+"/"+year;
                SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
                try{
                  birthDate = formater.parse(date);
                  displayDate.setText(formater.format(birthDate));
                }
                catch(Exception ex) {

                }
            }
        };

        return view;
    }

    private void checkForm(){
        if(usernameInput.getText().toString().isEmpty()) usernameInput.setError(getString(R.string.empty_field_error));
        if(passwordInput.getText().toString().isEmpty())passwordInput.setError(getString(R.string.empty_field_error));
        if(passwordCheckInput.getText().toString().isEmpty())passwordCheckInput.setError(getString(R.string.empty_field_error));
        if(!passwordInput.getText().toString().equals(passwordCheckInput.getText().toString())) passwordCheckInput.setError(getString(R.string.passwords_dont_match_error));
        if(emailInput.getText().toString().isEmpty()) emailInput.setError(getString(R.string.empty_field_error));
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput.getText().toString()).matches()) emailInput.setError(getString(R.string.invalid_email_error));
    }
}

package com.henallux.bepway.features.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.henallux.bepway.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

    private EditText dateDayInput;
    private EditText dateMonthInput;
    private EditText dateYearInput;
    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordCheckInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        Button createButton = view.findViewById(R.id.createAccountButton);
        Button resetButton = view.findViewById(R.id.cancelAccountButton);
        usernameInput = view.findViewById(R.id.usernameInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        passwordCheckInput = view.findViewById(R.id.passwordCheckInput);
        emailInput = view.findViewById(R.id.emailInput);
        dateDayInput = view.findViewById(R.id.registerBirthdateDay);
        dateMonthInput = view.findViewById(R.id.registerBirthdateMonth);
        dateYearInput = view.findViewById(R.id.registerBirthdateYear);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkForm()){
                    // + creation et insertion BD
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameInput.getText().clear();
                usernameInput.setHint(R.string.username_hint);
                usernameInput.setError(null);
                passwordInput.getText().clear();
                passwordInput.setHint(R.string.password_hint);
                passwordInput.setError(null);
                passwordCheckInput.getText().clear();
                passwordCheckInput.setHint(R.string.check_password_hint);
                passwordCheckInput.setError(null);
                emailInput.getText().clear();
                emailInput.setHint(R.string.email_hint);
                emailInput.setError(null);
                dateDayInput.setHint(getString(R.string.birthdate_day_hint));
                dateDayInput.getText().clear();
                dateDayInput.setError(null);
                dateMonthInput.setHint(getString(R.string.birthdate_month_hint));
                dateMonthInput.getText().clear();
                dateMonthInput.setError(null);
                dateYearInput.setHint(getString(R.string.birthdate_year_hint));
                dateYearInput.getText().clear();
                dateYearInput.setError(null);
            }
        });

        return view;
    }

    private boolean checkForm(){
        Boolean valid = true;


        if(usernameInput.getText().toString().isEmpty()){
            usernameInput.setError(getString(R.string.empty_field_error));
            valid = false;
        }
        if(passwordInput.getText().toString().isEmpty()){
            passwordInput.setError(getString(R.string.empty_field_error));
            valid = false;
        }
        if(passwordCheckInput.getText().toString().isEmpty()){
            passwordCheckInput.setError(getString(R.string.empty_field_error));
            valid = false;
        }
        if(!passwordInput.getText().toString().equals(passwordCheckInput.getText().toString())){
            passwordCheckInput.setError(getString(R.string.passwords_dont_match_error));
            valid = false;
        }
        if(emailInput.getText().toString().isEmpty()){
            emailInput.setError(getString(R.string.empty_field_error));
            valid = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput.getText().toString()).matches()){
            emailInput.setError(getString(R.string.invalid_email_error));
            valid = false;
        }
        /*if(formatDate(dateInput.getText().toString()).after(Calendar.getInstance().getTime())){
            dateInput.setError(getString(R.string.wrong_date_error));
            valid = false;
        }*/
        return valid;
    }

    private Date formatDate(String stringDate){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        try {
            date = formater.parse(stringDate);
        }
        catch (Exception ex){}
        return date;
    }
}

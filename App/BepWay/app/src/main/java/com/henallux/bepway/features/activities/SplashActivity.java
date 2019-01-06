package com.henallux.bepway.features.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.henallux.bepway.Exception.TokenException;
import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.TokenDAO;
import com.henallux.bepway.features.fragment.GuestFragment;
import com.henallux.bepway.model.LoginModel;
import com.henallux.bepway.model.Token;

public class SplashActivity extends AppCompatActivity {

    private GetToken getToken;
    private String login;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        login = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getString("Login",null);

        password = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("Password",null);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(login != null && password != null){
                    LoginModel loginModel = new LoginModel(login, password);
                    getToken = new GetToken();
                    getToken.execute(loginModel);
                }
                else{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
        }, 3000);


    }

    public class GetToken extends AsyncTask<LoginModel, Void, Token> {
        @Override
        protected Token doInBackground(LoginModel... params) {
            Token token = new Token();
            TokenDAO tokenDAO = new TokenDAO();
            if(isCancelled()){

            }
            try {
                token = tokenDAO.getToken(params[0]);
            }
            catch (TokenException e) {
                token.setException(e.getErrorMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(Token token) {
            if(token.getToken() != null){
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("Token",token.getToken())
                        .apply();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
            if(token.getException() != null){
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}

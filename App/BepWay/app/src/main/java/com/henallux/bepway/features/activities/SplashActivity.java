package com.henallux.bepway.features.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.henallux.bepway.Exception.TokenException;
import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.TokenDAO;
import com.henallux.bepway.model.LoginModel;
import com.henallux.bepway.model.Token;

public class SplashActivity extends AppCompatActivity {

    private GetToken getToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String login = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getString("Login",null);

        String password = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("Password",null);

        if(login != null && password != null){
            LoginModel loginModel = new LoginModel(login, password);
            getToken = new GetToken();
            getToken.execute(loginModel);
        }
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
            } catch (TokenException e) {
                //token.setException(e.getErrorMessage());
            }
            catch (Exception ex){
                //token.setException(ex.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(Token token) {
            if(token.getToken() != null){
                /*PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("Token",token.getToken())
                        .putString("Login",loginModel.getLogin())
                        .putString("Password",loginModel.getPassword())
                        .apply();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);*/
            }
            //if(token.getException() != null)
                //Toast.makeText(getActivity(), token.getException(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}

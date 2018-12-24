package com.henallux.bepway.dataAccess;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.henallux.bepway.Exception.TokenException;
import com.henallux.bepway.features.activities.MainActivity;
import com.henallux.bepway.model.LoginModel;
import com.henallux.bepway.model.Token;

public class GetToken /*extends AsyncTask<LoginModel, Void, Token>*/ {

    /*private Context applicationContext;

    public GetToken(Context applicationContext){
        this.applicationContext = applicationContext;
    }

    @Override
    protected Token doInBackground(LoginModel... params) {
        Token token = new Token();
        TokenDAO tokenDAO = new TokenDAO();
        if(isCancelled()){

        }
        try {
            token = tokenDAO.getToken(params[0]);
        } catch (TokenException e) {
            token.setException(e.getErrorMessage());
        }
        catch (Exception ex){
            token.setException(ex.getMessage());
        }
        return token;
    }

    @Override
    protected void onPostExecute(Token token) {
        if(token.getToken() != null){
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    .edit()
                    .putString("Token",token.getToken())
                    .putString("Login",loginModel.getLogin())
                    .putString("Password",loginModel.getPassword())
                    .apply();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            getActivity().startActivity(intent);
        }
        if(token.getException() != null)
            Toast.makeText(getActivity(), token.getException(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }*/
}
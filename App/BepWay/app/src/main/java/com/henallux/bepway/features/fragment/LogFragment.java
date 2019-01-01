package com.henallux.bepway.features.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.henallux.bepway.Exception.TokenException;
import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.TokenDAO;
import com.henallux.bepway.features.activities.MainActivity;
import com.henallux.bepway.model.LoginModel;
import com.henallux.bepway.model.Token;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class LogFragment extends Fragment {

    private EditText username;
    private EditText password;
    private LoginModel loginModel;
    private GetToken getToken;

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
        username = view.findViewById(R.id.usernameLogin);
        password = view.findViewById(R.id.passwordLogin);
        Button logButton = view.findViewById(R.id.logButton);
        final ImageView appIcon =  view.findViewById(R.id.appIcon);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager conMan = ((ConnectivityManager)getActivity().getApplicationContext().getSystemService(CONNECTIVITY_SERVICE));
                boolean isWifiEnabled = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
                boolean is3GEnabled = !(conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                        && conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getReason().equals("dataDisabled"));
                if(isWifiEnabled || is3GEnabled){
                    getToken = new GetToken();
                    loginModel = new LoginModel(username.getText().toString(), password.getText().toString());
                    getToken.execute(loginModel);
                }
                else{
                    Toast.makeText(getActivity(), getString(R.string.no_connection_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //region Surprise
        appIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                appIcon.setImageResource(R.drawable.titi);
                return false;
            }
        });
        //endregion

        return view;
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
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
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
        }
    }

}

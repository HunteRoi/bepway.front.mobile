package com.henallux.bepway.features.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.henallux.bepway.Exception.TokenException;
import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.TokenDAO;
import com.henallux.bepway.features.activities.MainActivity;
import com.henallux.bepway.model.LoginModel;
import com.henallux.bepway.model.Token;

public class GuestFragment extends Fragment {

    private final String LOGIN_GUEST = "guest";
    private final String PASSWORD_GUEST = "guest";
    private final LoginModel GUEST_ACCOUNT = new LoginModel(LOGIN_GUEST, PASSWORD_GUEST);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guest, container, false);
        final ImageView appIcon = view.findViewById(R.id.appIconGuest);
        Button loginGuest = view.findViewById(R.id.guestLogin);

        loginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetToken getToken = new GetToken();
                getToken.execute(GUEST_ACCOUNT);
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
            if(token.getException() != null)
                Toast.makeText(getActivity(), token.getException(), Toast.LENGTH_SHORT).show();
            else{
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}

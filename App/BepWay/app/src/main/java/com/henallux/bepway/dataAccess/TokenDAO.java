package com.henallux.bepway.dataAccess;

import com.google.gson.Gson;
import com.henallux.bepway.Exception.TokenException;
import com.henallux.bepway.model.LoginModel;
import com.henallux.bepway.model.Token;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class TokenDAO implements ITokenDAO{


    public Token getToken(LoginModel login)throws TokenException, Exception {
        URL url = new URL("https://bepway.azurewebsites.net/api/jwt");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type","application/json");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        OutputStream outputStream = connection.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        connection.connect();

        outputStreamWriter.write(new Gson().toJson(login));

        outputStreamWriter.flush();
        outputStreamWriter.close();
        outputStream.close();



        if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST){
            String test = connection.getResponseMessage();
            throw new TokenException("Informations manquantes ou incorrectes");
        }
        if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) throw new TokenException("Utilisateur introuvable");

        BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String stringJSON = "",line;
        while((line = buffer.readLine()) != null){
            stringBuilder.append(line);
        }
        buffer.close();
        connection.disconnect();
        stringJSON = stringBuilder.toString();
        JSONObject JSONToken = new JSONObject(stringJSON);
        long expires = (new Date().getTime())/1000 + JSONToken.getInt("expires_in");
        return new Token(JSONToken.getString("access_token"),expires);
    }
}

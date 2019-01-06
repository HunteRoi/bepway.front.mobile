package com.henallux.bepway.dataAccess;

import android.util.Log;
import com.henallux.bepway.Exception.ApiErrorException;
import com.henallux.bepway.model.Coordinate;
import com.henallux.bepway.model.Zoning;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ZoningDAO {

    public ArrayList<Zoning> getAllZoningsAPI(String token, int pageIndex, String filterKey, String filterValue) throws ApiErrorException, Exception{
        URL url;
        if(filterKey == null || filterValue == null)url = new URL(String.format("https://bepway.azurewebsites.net/api/Zoning?pageIndex=%2d", pageIndex));
        else url = new URL(String.format("https://bepway.azurewebsites.net/api/Zoning?pageIndex=%2d&%s=%s", pageIndex, filterKey, filterValue));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        int test = connection.getResponseCode();
        int test2 = HttpURLConnection.HTTP_INTERNAL_ERROR;

        //if(connection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) throw new ApiErrorException("Error coming from the API");

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer "+token);

        BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String stringJSON = "",line;
        while((line = buffer.readLine()) != null){
            stringBuilder.append(line);
        }
        buffer.close();
        stringJSON = stringBuilder.toString();
        return jsonToAPIZonings(stringJSON);
    }

    public ArrayList<Zoning> jsonToAPIZonings(String stringJSON) throws Exception{
        ArrayList<Zoning> zonings = new ArrayList<>();
        try {
            JSONArray records = new JSONArray(stringJSON);
            for (int i = 0; i < records.length(); i++) {
                Zoning zoning = new Zoning();
                JSONObject zoningJson = records.getJSONObject(i);
                JSONObject coordinates = zoningJson.getJSONObject("coordinates");
                zoning.setId(zoningJson.getInt("id"));
                zoning.setName(zoningJson.getString("name"));
                zoning.setUrl(zoningJson.getString("url"));
                zoning.setZoningCenter(new Coordinate(Double.parseDouble(coordinates.getString("latitude")),Double.parseDouble(coordinates.getString("longitude"))));
                double superficie = zoningJson.getDouble("surface");
                zoning.setSuperficie((int)(Math.round(superficie)));
                zoning.setCommune(zoningJson.getString("township"));
                zoning.setCity(zoningJson.getString("localisation"));
                zoning.setNbImplantations(zoningJson.getInt("nbImplantations"));
                zonings.add(zoning);
            }
        } catch (Exception ex) {Log.i("Errors", ex.getClass() +" - "+ ex.getMessage());}
        return zonings;
    }
}

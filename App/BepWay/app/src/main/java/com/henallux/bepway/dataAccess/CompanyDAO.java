package com.henallux.bepway.dataAccess;

import android.util.Log;
import com.henallux.bepway.model.Company;
import com.henallux.bepway.model.Coordinate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CompanyDAO {
    public ArrayList<Company> getAllCompanies(String token, int pageIndex) throws Exception{
        URL url = new URL("https://bepway.azurewebsites.net/api/Company");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer "+token);
        connection.setRequestProperty("pageIndex", Integer.toString(pageIndex));

        BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String stringJSON = "",line;
        while((line = buffer.readLine()) != null){
            stringBuilder.append(line);
        }
        buffer.close();
        stringJSON = stringBuilder.toString();
        Log.i("Company", stringJSON);
        return jsonToCompanies(stringJSON);
    }

    public ArrayList<Company> getCompaniesByZoning(String token, int zoningId, int pageIndex) throws Exception{
        URL url = new URL("https://bepway.azurewebsites.net/api/Company?idZoning="+zoningId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("idZoning",Integer.toString(zoningId));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer "+token);
        //connection.setRequestProperty("pageIndex", Integer.toString(pageIndex));

        BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String stringJSON = "",line;
        while((line = buffer.readLine()) != null){
            stringBuilder.append(line);
        }
        buffer.close();
        stringJSON = stringBuilder.toString();
        Log.i("Company", stringJSON);
        return jsonToCompanies(stringJSON);
    }

    public ArrayList<Company>jsonToCompanies(String stringJSON) throws Exception{
        ArrayList<Company> companies = new ArrayList<>();
        try{
            JSONArray records = new JSONArray(stringJSON);
            for(int i = 0; i < records.length(); i++){
                Company company = new Company();
                JSONObject companyJson = records.getJSONObject(i);
                JSONObject coordinate = companyJson.getJSONObject("coordinates");
                JSONObject sector = companyJson.getJSONObject("activitySector");
                company.setId(companyJson.getInt("id"));
                company.setName(companyJson.getString("name"));
                company.setImageUrl(companyJson.getString("imageUrl"));
                company.setSiteUrl(companyJson.getString("siteUrl"));
                company.setDescription(companyJson.getString("description"));
                company.setStatus(companyJson.getString("status"));
                company.setAddress(companyJson.getString("address"));
                company.setPremium(companyJson.getBoolean("isPremium"));
                company.setSector(sector.getString("name"));
                company.setLocation(new Coordinate(Double.parseDouble(coordinate.getString("latitude")), Double.parseDouble(coordinate.getString("longitude"))));
                companies.add(company);
            }
        }
        catch (Exception ex){
            Log.i("ezfkezo", ex.getMessage());
        }
        return companies;
    }
}

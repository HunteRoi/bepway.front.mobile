package com.henallux.bepway.dataAccess;

import android.util.Log;

import com.henallux.bepway.Exception.ApiErrorException;
import com.henallux.bepway.Exception.JSONException;
import com.henallux.bepway.model.Company;
import com.henallux.bepway.model.Coordinate;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CompanyDAO implements ICompanyDAO{

    private final int DEFAULT_PAGE_SIZE = 15;

    public ArrayList<Company> getAllCompanies(String token, int pageIndex, String filterKey, String filterValue) throws Exception{
        URL url;
        if(filterKey == null || filterValue == null) url = new URL(String.format("https://bepway.azurewebsites.net/api/Company?pageIndex=%2d",pageIndex));
        else url =new URL(String.format("https://bepway.azurewebsites.net/api/Company?pageIndex=%2d&%s=%s",pageIndex,filterKey, filterValue));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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
        Log.i("Company", stringJSON);
        return jsonToCompanies(stringJSON);
    }

    public ArrayList<Company> getCompaniesByZoning(String token, int zoningId, int pageIndex, int pageSize, String filterKey, String filterValue) throws Exception{
        URL url;
        if(filterKey == null | filterValue == null) url = new URL(String.format("https://bepway.azurewebsites.net/api/Company?zoningId=%s&pageIndex=%2d&pageSize=%2d",zoningId, pageIndex, pageSize));
        else url = new URL(String.format("https://bepway.azurewebsites.net/api/Company?idZoning=%s&pageIndex=%2d&pageSize=%2d&%s=%s",zoningId, pageIndex, pageSize,filterKey,filterValue));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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
        Log.i("Company", stringJSON);
        return jsonToCompanies(stringJSON);
    }

    public ArrayList<Company> getCompaniesByZoning(String token, int zoningId, int pageIndex, String filterKey, String filterValue) throws Exception{
        return getCompaniesByZoning(token, zoningId, pageIndex, DEFAULT_PAGE_SIZE, filterKey, filterValue);
    }

    public ArrayList<Company> jsonToCompanies(String stringJSON) throws Exception, JSONException{
        ArrayList<Company> companies = new ArrayList<>();
        try{
            JSONArray records = new JSONArray(stringJSON);
            for(int i = 0; i < records.length(); i++){
                Company company = new Company();
                JSONObject companyJson = records.getJSONObject(i);
                JSONObject coordinate = companyJson.getJSONObject("coordinates");
                JSONObject sector = null;
                if(!companyJson.isNull("activitySector"))sector = companyJson.getJSONObject("activitySector");
                company.setId(companyJson.getInt("id"));
                company.setName(companyJson.getString("name"));
                company.setImageUrl(companyJson.getString("imageUrl").equals("null")? null : companyJson.getString("imageUrl"));
                company.setSiteUrl(companyJson.getString("siteUrl").equals("null")? null : companyJson.getString("siteUrl"));
                company.setDescription(companyJson.getString("description").equals("null")? null : companyJson.getString("description"));
                company.setStatus(companyJson.getString("status"));
                company.setAddress(companyJson.getString("address"));
                company.setPremium(companyJson.getBoolean("isPremium"));
                company.setSector(sector == null? "Secteur d'activité non spécifié" : sector.getString("name"));
                company.setLocation(new Coordinate(Double.parseDouble(coordinate.getString("latitude")), Double.parseDouble(coordinate.getString("longitude"))));
                companies.add(company);
            }
        }
        catch (Exception ex){
            throw new JSONException("Error while reading data");
        }
        return companies;
    }
}

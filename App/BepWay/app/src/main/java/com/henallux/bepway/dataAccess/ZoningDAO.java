package com.henallux.bepway.dataAccess;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.henallux.bepway.model.Coordinate;
import com.henallux.bepway.model.Token;
import com.henallux.bepway.model.Zoning;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ZoningDAO {

    public ArrayList<Zoning> getAllZoningsAPI(String token) throws Exception{
        //URL url = new URL("https://data.bep.be/api/records/1.0/search/?dataset=parcs-dactivite-economique&facet=bep_services_dbo_gestparc_pae_equipements_hallrelais&facet=bep_services_dbo_gestparc_pae_equipements_gaz&facet=bep_services_dbo_gestparc_pae_equipements_fibreoptique&facet=bep_services_dbo_gestparc_pae_equipements_adsl&facet=bep_services_dbo_gestparc_pae_equipements_coaxial&facet=bep_services_dbo_gestparc_pae_equipements_assainissementcollectif&facet=bep_services_dbo_gestparc_pae_caracteristiques_type&facet=bep_services_dbo_gestparc_pae_caracteristiques_localisation&facet=bep_services_dbo_gestparc_pae_caracteristiques_commune&facet=bep_services_dbo_gestparc_pae_caracteristiques_prixvente");
        URL url = new URL("https://bepway.azurewebsites.net/api/Zoning");
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

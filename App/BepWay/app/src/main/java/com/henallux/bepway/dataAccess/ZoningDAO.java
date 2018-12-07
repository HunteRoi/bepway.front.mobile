package com.henallux.bepway.dataAccess;

import android.util.Log;
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

    public ArrayList<Zoning> getAllZonings() throws Exception{
        Log.i("Zoning", "getAllZonings");
        URL url = new URL("https://data.bep.be/api/records/1.0/search/?dataset=parcs-dactivite-economique&facet=bep_services_dbo_gestparc_pae_equipements_hallrelais&facet=bep_services_dbo_gestparc_pae_equipements_gaz&facet=bep_services_dbo_gestparc_pae_equipements_fibreoptique&facet=bep_services_dbo_gestparc_pae_equipements_adsl&facet=bep_services_dbo_gestparc_pae_equipements_coaxial&facet=bep_services_dbo_gestparc_pae_equipements_assainissementcollectif&facet=bep_services_dbo_gestparc_pae_caracteristiques_type&facet=bep_services_dbo_gestparc_pae_caracteristiques_localisation&facet=bep_services_dbo_gestparc_pae_caracteristiques_commune&facet=bep_services_dbo_gestparc_pae_caracteristiques_prixvente");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String stringJSON = "",line;
        while((line = buffer.readLine()) != null){
            stringBuilder.append(line);
        }
        buffer.close();
        stringJSON = stringBuilder.toString();
        return jsonToZonings(stringJSON);
    }

    public ArrayList<Zoning>jsonToZonings(String stringJSON) throws Exception{
        ArrayList<Zoning> zonings = new ArrayList<>();
        try {
            ArrayList<Coordinate> coordinates;
            JSONObject json = new JSONObject(stringJSON);
            JSONArray jsonRecords = new JSONArray(json.getString("records"));
            for (int i = 0; i < jsonRecords.length(); i++) {
                coordinates = new ArrayList<>();
                JSONObject record = jsonRecords.getJSONObject(i);
                JSONObject fields = record.getJSONObject("fields");
                JSONObject geoShape = fields.getJSONObject("geo_shape");
                JSONArray coordinatesJSON = geoShape.getJSONArray("coordinates");
                if(geoShape.getString("type").equals("MultiPolygon")){
                    for (int y = 0; y < coordinatesJSON.length(); y++) {
                        JSONArray arrayY = coordinatesJSON.getJSONArray(y);
                        for (int z = 0; z < arrayY.length(); z++) {
                            JSONArray arrayZ = arrayY.getJSONArray(z);
                            for (int c = 0; c < arrayZ.length(); c++) {
                                JSONArray arrayC = arrayZ.getJSONArray(c);
                                for (int x = 0; x < arrayC.length(); x++) {
                                    coordinates.add(new Coordinate(Float.parseFloat(arrayC.getString(0)), Float.parseFloat(arrayC.getString(1))));
                                }
                            }
                        }
                    }
                }
                else{
                    JSONArray uniqueArray = coordinatesJSON.getJSONArray(0);
                    for(int y = 0; y < uniqueArray.length(); y++){
                        JSONArray coordArray = uniqueArray.getJSONArray(y);
                        coordinates.add(new Coordinate(Float.parseFloat(coordArray.getString(0)), Float.parseFloat(coordArray.getString(1))));
                    }
                }
                String nom = fields.getString("bep_services_dbo_gestparc_pae_caracteristiques_nomparc");
                String city = fields.getString("bep_services_dbo_gestparc_pae_caracteristiques_localisation");
                String commune = fields.getString("bep_services_dbo_gestparc_pae_caracteristiques_commune");
                String url = fields.getString("bep_services_dbo_gestparc_pae_caracteristiques_url_pae");
                String supef = fields.getString("bep_services_dbo_gestparc_parcs_superficie");
                String superfi = supef.split(",")[0]+"."+(supef.split(",")[1]).charAt(0);
                int superficie = (int)(Math.round(Double.parseDouble(superfi)/100));
                Zoning zoning = new Zoning(nom, city, commune, url, superficie, coordinates);
                zonings.add(zoning);
            }
        } catch (Exception ex) {Log.i("Errors", ex.getClass() +" - "+ ex.getMessage());}





      /*  Log.i("Zoning", "Records");
        for(int i = 0; i < jsonArray.length(); i++){
            Log.i("Zoning", "Fields");
            coordinates = new Coordinate[200];
            JSONObject jsonCompany2 = jsonArray.getJSONObject(i);
            JSONObject jsonZoning = new JSONObject(jsonCompany2.getString("fields"));
            JSONObject geoShape = new JSONObject(jsonZoning.getString("geo_shape"));
            JSONArray coordinatesArray = new JSONArray(geoShape.getString("coordinates"));
            for(int z = 0; i < coordinatesArray.length(); z++){
                JSONArray coordinatesOne = new JSONArray(coordinatesArray.getString(z));
                Log.i("Zoning", coordinatesOne.toString());
                JSONArray coordinatesOneOne = new JSONArray(coordinatesOne.getString(0));
                Log.i("Zoning", "array length = " + coordinatesOneOne.length());
                for(int y = 0; y < coordinatesOneOne.length(); y++){
                    JSONArray coords = new JSONArray(coordinatesOneOne.getString(y));
                    coordinates[y] = new Coordinate(Float.parseFloat(coords.getString(0)), Float.parseFloat(coords.getString(1)));
                }
                Log.i("Zoning", jsonZoning.getString("bep_services_dbo_gestparc_pae_caracteristiques_nomparc"));
                Zoning zoning = new Zoning(jsonZoning.getString("bep_services_dbo_gestparc_pae_caracteristiques_nomparc"),coordinates);
                zonings.add(zoning);
            }
        }*/
        return zonings;
    }
}

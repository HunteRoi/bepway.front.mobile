package com.henallux.bepway.dataAccess;

import android.util.Log;
import com.henallux.bepway.model.Company;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CompanyDAO {
    public ArrayList<Company> getAllCompanies() throws Exception{
        URL url = new URL("https://data.bep.be/api/records/1.0/search/?dataset=societes-de-nos-parcs-dactivite&facet=secteuractivite&facet=nomparc&facet=adresseville&facet=adressearrondissement&facet=new_secteur_d_activites");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
        JSONObject json = new JSONObject(stringJSON);
        JSONArray jsonArray =   new JSONArray(json.getString("records"));
        String nomEntreprise, nomParc, adresseVille, adresseRueNumero, secteurActivites;

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonCompany2 = jsonArray.getJSONObject(i);
            JSONObject jsonCompany = new JSONObject(jsonCompany2.getString("fields"));

            nomEntreprise = jsonCompany.getString("nomentreprise");
            nomParc = jsonCompany.getString("nomparc");
            try {
                adresseVille = jsonCompany.getString("adresseville");
            }
            catch (JSONException exception){
                adresseVille = null;
            }
            try{
                adresseRueNumero = jsonCompany.getString("adresseruenumero");
            }
            catch (JSONException exception){
                adresseRueNumero = null;
            }
            secteurActivites = jsonCompany.getString("new_secteur_d_activites");

            Company company = new Company(nomEntreprise,nomParc,adresseVille,adresseRueNumero, secteurActivites);
            companies.add(company);
        }
        return companies;
    }
}

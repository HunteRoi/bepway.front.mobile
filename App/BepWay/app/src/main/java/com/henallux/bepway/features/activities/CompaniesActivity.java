package com.henallux.bepway.features.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.CompanyDAO;
import com.henallux.bepway.features.adapters.AllCompaniesAdapter;
import com.henallux.bepway.features.recyclerView.RecyclerItemClickListener;
import com.henallux.bepway.model.Company;

import java.util.ArrayList;

public class CompaniesActivity extends AppCompatActivity {

    private RecyclerView companiesToDisplay;
    private AllCompaniesAdapter adapter;
    private LoadCompanies loadCompanies;
    private ArrayList<Company> allCompanies;
    private ArrayList<Company> searchedCompanies;
    private Dialog dialog;
    private MenuItem nameFilter;
    private MenuItem sectorFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout_companies);

        SearchView searchView = findViewById(R.id.searchViewCompanies);

        companiesToDisplay = findViewById(R.id.recyclerView);
        companiesToDisplay.addOnItemTouchListener(new RecyclerItemClickListener(this.getApplicationContext(), companiesToDisplay, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showPopup(searchedCompanies.get(position));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        dialog = new Dialog(this);
        allCompanies = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        companiesToDisplay.addItemDecoration(decoration);
        companiesToDisplay.setLayoutManager(layoutManager);
        loadCompanies = new LoadCompanies();
        loadCompanies.execute();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchedCompanies.clear();
                for(Company company : allCompanies){
                    if(nameFilter.isChecked()) {
                        if(company.getName().toLowerCase().contains(newText.toLowerCase())) searchedCompanies.add(company);
                    }
                    else {
                        if(sectorFilter.isChecked()){
                            if(company.getSector().toLowerCase().contains(newText.toLowerCase())) searchedCompanies.add(company);
                        }
                        else{
                            if(company.getSector().toLowerCase().contains(newText.toLowerCase()) || company.getName().toLowerCase().contains(newText.toLowerCase()))searchedCompanies.add(company);
                        }
                    }
                }
                adapter.setCompanies(searchedCompanies);
                companiesToDisplay.setAdapter(adapter);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.checkName:
                if(item.isChecked()) item.setChecked(false);
                else {
                    item.setChecked(true);
                    sectorFilter.setChecked(false);
                }
                return true;
            case R.id.checkSector:
                if(item.isChecked())item.setChecked(false);
                else {
                    item.setChecked(true);
                    nameFilter.setChecked(false);
                }
            default : return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        nameFilter = menu.findItem(R.id.checkName);
        sectorFilter = menu.findItem(R.id.checkSector);
        return super.onPrepareOptionsMenu(menu);
    }

    public void showPopup(Company company){
        dialog.setContentView(R.layout.company_popup);
        TextView textClose = (TextView)dialog.findViewById(R.id.close_popup_company);
        TextView companyName = dialog.findViewById(R.id.companyName);
        companyName.setText(company.getName());
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.filter_search_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loadCompanies != null) loadCompanies.cancel(true);
    }

    public class LoadCompanies extends AsyncTask<Void, Void, ArrayList<Company>>{
        @Override
        protected ArrayList<Company> doInBackground(Void... voids) {
            ArrayList<Company> companies = new ArrayList<>();
            CompanyDAO companyDAO = new CompanyDAO();
            if(isCancelled()){
                Log.i("Company","Is cancelled");
            }
            try {
                companies = companyDAO.getAllCompanies();
            } catch (Exception e) {
                Log.i("Company", e.getMessage());
            }
            return companies;
        }

        @Override
        protected void onPostExecute(ArrayList<Company> companies) {
            for(Company company : companies){
                allCompanies.add(company);
            }
            searchedCompanies = (ArrayList<Company>) allCompanies.clone();
            adapter = new AllCompaniesAdapter(allCompanies);
            companiesToDisplay.setAdapter(adapter);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}

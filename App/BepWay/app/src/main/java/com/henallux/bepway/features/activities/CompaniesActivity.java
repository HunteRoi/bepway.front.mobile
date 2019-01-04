package com.henallux.bepway.features.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.CompanyDAO;
import com.henallux.bepway.features.adapters.AllCompaniesAdapter;
import com.henallux.bepway.features.recyclerView.RecyclerItemClickListener;
import com.henallux.bepway.model.Company;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CompaniesActivity extends AppCompatActivity {

    private RecyclerView companiesToDisplay;
    private AllCompaniesAdapter adapter;
    private LoadCompanies loadCompanies;
    private ArrayList<Company> allCompanies;
    private ArrayList<Company> searchedCompanies;
    private Dialog dialogCompanyInfo;
    private Dialog dialogFilter;
    private CheckBox nameFilter;
    private CheckBox sectorFilter;
    private CheckBox cityFilter;
    private Button filterValidation;
    private SearchView searchView;
    private int pageNumber;
    private int zoningId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout_companies);

        pageNumber = 0;
        searchedCompanies = new ArrayList<>();
        companiesToDisplay = findViewById(R.id.recyclerView);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);

        companiesToDisplay.addOnItemTouchListener(new RecyclerItemClickListener(this.getApplicationContext(), companiesToDisplay, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showInformationPopup(searchedCompanies.get(position));
            }
        }));
        companiesToDisplay.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Toast.makeText(CompaniesActivity.this, "Last", Toast.LENGTH_LONG).show();
                    LoadCompanies addCompanies = new LoadCompanies();
                    addCompanies.execute();
                }
            }
            /*@Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = layoutManager.getChildCount();
                totalItems = layoutManager.getItemCount();
                scrollOutItems = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems)){
                    isScrolling = false;
                    //loadCompanies.execute();
                }
            }*/
        });

        zoningId = getIntent().getIntExtra("zoningId", -5);

        dialogFilter = new Dialog(this);
        if(zoningId == -5) dialogFilter.setContentView(R.layout.filter_all_companies_popup);
        else dialogFilter.setContentView(R.layout.filter_popup);


        nameFilter = dialogFilter.findViewById(R.id.checkName);
        sectorFilter = dialogFilter.findViewById(R.id.checkSector);
        cityFilter = dialogFilter.findViewById(R.id.checkCity);
        filterValidation = dialogFilter.findViewById(R.id.filterButton);

        nameFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameFilter.isChecked()){
                    if(sectorFilter.isChecked())sectorFilter.setChecked(false);
                    if(cityFilter.isChecked())cityFilter.setChecked(false);
                }
            }
        });

        sectorFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sectorFilter.isChecked()){
                    if(nameFilter.isChecked())nameFilter.setChecked(false);
                    if(cityFilter.isChecked())cityFilter.setChecked(false);
                }
            }
        });

        cityFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cityFilter.isChecked()){
                    if(nameFilter.isChecked())nameFilter.setChecked(false);
                    if(sectorFilter.isChecked())sectorFilter.setChecked(false);
                }
            }
        });

        filterValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterCompanies(searchView.getQuery().toString());
                dialogFilter.dismiss();
            }
        });

        dialogCompanyInfo = new Dialog(this);

        allCompanies = new ArrayList<>();
        adapter = new AllCompaniesAdapter(allCompanies);

        companiesToDisplay.addItemDecoration(decoration);
        companiesToDisplay.setLayoutManager(layoutManager);

        loadCompanies = new LoadCompanies();
        loadCompanies.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.filter_search_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.filterOption:
                showFilterPopup();
                return true;
            default : return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        searchView = (SearchView)menu.findItem(R.id.researchOption).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCompanies(newText);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void filterCompanies(String newText){
        searchedCompanies.clear();
        for(Company company : allCompanies){
            if(nameFilter.isChecked()) {
                if(company.getName().toLowerCase().contains(newText.toLowerCase())) searchedCompanies.add(company);
            }
            else {
                if(sectorFilter.isChecked()){
                    if(company.getSector().toLowerCase().contains(newText.toLowerCase())) searchedCompanies.add(company);
                }
                else {
                    if (cityFilter.isChecked()) {
                        if(company.getAddress() != null && company.getAddress().toLowerCase().contains(newText.toLowerCase())) searchedCompanies.add(company);
                    } else {
                        if(company.getName().toLowerCase().contains(newText.toLowerCase()) || company.getSector().toLowerCase().contains(newText.toLowerCase()) || company.getAddress().toLowerCase().contains(newText.toLowerCase()))
                            searchedCompanies.add(company);
                    }
                }
            }
        }
        adapter.setCompanies(searchedCompanies);
        companiesToDisplay.setAdapter(adapter);
    }

    public void showFilterPopup(){
        TextView textClose = dialogFilter.findViewById(R.id.close_filterPopup);
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFilter.dismiss();
            }
        });
        dialogFilter.show();
    }

    public void showInformationPopup(final Company company){
        if(company.isPremium()){
            dialogCompanyInfo.setContentView(R.layout.premium_company_popup);
            TextView description = dialogCompanyInfo.findViewById(R.id.companyDescription);
            ImageView image = dialogCompanyInfo.findViewById(R.id.companyImage);
            ImageView website = dialogCompanyInfo.findViewById(R.id.companyWebsite);
            description.setText(company.getDescription() == null? "" : company.getDescription());
            if(company.getImageUrl() != null){
                try{
                    URL url = new URL(company.getImageUrl());
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    image.setImageBitmap(bmp);
                }
                catch (Exception exception){
                    Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(company.getSiteUrl() != null){
                        dialogCompanyInfo.dismiss();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(company.getSiteUrl()));
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(CompaniesActivity.this, getString(R.string.no_url_provided), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            dialogCompanyInfo.setContentView(R.layout.company_popup);
        }
        TextView textClose = (TextView) dialogCompanyInfo.findViewById(R.id.close_popup_company);
        TextView companyName = dialogCompanyInfo.findViewById(R.id.companyName);
        ImageView map = dialogCompanyInfo.findViewById(R.id.companyMap);
        companyName.setText(company.getName());
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCompanyInfo.dismiss();
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompaniesActivity.this, OSMActivity.class);
                ArrayList<Company> companies = new ArrayList<>();
                companies.add(company);
                intent.putExtra("type","Company");
                intent.putExtra("center", company.getLocation());
                intent.putExtra("companies",companies);
                startActivity(intent);
            }
        });
        dialogCompanyInfo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCompanyInfo.show();
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
                String token = PreferenceManager.getDefaultSharedPreferences(CompaniesActivity.this).getString("Token",null);

                if(zoningId != -5) {
                    companies = companyDAO.getCompaniesByZoning(token, zoningId, pageNumber);
                }
                else{
                    companies = companyDAO.getAllCompanies(token, pageNumber);
                }
                pageNumber++;
            } catch (Exception e) {
                Log.i("Company", e.getMessage());
            }
            return companies;
        }

        @Override
        protected void onPostExecute(ArrayList<Company> companies) {
            allCompanies.addAll(companies);
            Collections.sort(searchedCompanies, new Comparator<Company>() {
                @Override
                public int compare(Company o1, Company o2) {
                    return Boolean.compare(o1.isPremium(), o2.isPremium());
                }
            });
            searchedCompanies.addAll(companies);
            companiesToDisplay.setAdapter(adapter);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}

package com.henallux.bepway.features.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import com.henallux.bepway.Exception.CompanyException;
import com.henallux.bepway.Exception.TokenException;
import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.CompanyDAO;
import com.henallux.bepway.features.adapters.AllCompaniesAdapter;
import com.henallux.bepway.features.recyclerView.RecyclerItemClickListener;
import com.henallux.bepway.features.util.CheckConnection;
import com.henallux.bepway.features.util.LoadImage;
import com.henallux.bepway.model.Company;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class CompaniesActivity extends AppCompatActivity {

    private final int ZONING_ID_MISSING = -1;
    private RecyclerView companiesToDisplay;
    private AllCompaniesAdapter adapter;
    private LoadCompanies loadCompanies;
    private ArrayList<Company> allCompanies;
    private ArrayList<Company> searchedCompanies;
    private Dialog dialogCompanyInfo;
    private Dialog dialogFilter;
    private CheckBox nameFilter;
    private CheckBox sectorFilter;
    private CheckBox addressFilter;
    private Button filterValidation;
    private SearchView searchView;
    private int pageNumber;
    private int zoningId;
    private String filterKey;
    private String filterValue;
    private String lastFilterValue;
    private boolean firstResearchDone;
    private int lastFirstVisiblePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout_companies);

        firstResearchDone = false;
        filterKey = "companyName";
        filterValue = null;
        lastFilterValue = null;
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
                    lastFirstVisiblePosition = ((LinearLayoutManager)companiesToDisplay.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    LoadCompanies addCompanies = new LoadCompanies();
                    addCompanies.execute();
                }
            }
        });

        zoningId = getIntent().getIntExtra("zoningId", ZONING_ID_MISSING);

        dialogFilter = new Dialog(this);
        if(zoningId == ZONING_ID_MISSING) dialogFilter.setContentView(R.layout.filter_all_companies_popup);
        else dialogFilter.setContentView(R.layout.filter_popup);


        nameFilter = dialogFilter.findViewById(R.id.checkName);
        sectorFilter = dialogFilter.findViewById(R.id.checkSector);
        addressFilter = dialogFilter.findViewById(R.id.checkCity);
        filterValidation = dialogFilter.findViewById(R.id.filterButton);

        nameFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uncheckEverythingAndResetPageNumber();
                nameFilter.setChecked(true);
                filterKey = "companyName";
            }
        });

        sectorFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uncheckEverythingAndResetPageNumber();
                sectorFilter.setChecked(true);
                filterKey = "activityName";
            }
        });

        if(zoningId == ZONING_ID_MISSING){
            addressFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uncheckEverythingAndResetPageNumber();
                    addressFilter.setChecked(true);
                    filterKey = "address";
                }
            });
        }

        filterValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //filterCompanies(searchView.getQuery().toString());
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

    public void uncheckEverythingAndResetPageNumber(){
        pageNumber = 0;
        nameFilter.setChecked(false);
        sectorFilter.setChecked(false);
        if(zoningId == ZONING_ID_MISSING) addressFilter.setChecked(false);
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
                if(!firstResearchDone){
                    firstResearchDone = true;
                    pageNumber = 0;
                }
                lastFilterValue = filterValue;
                filterValue = query;
                if(!filterValue.equals(lastFilterValue)) pageNumber = 0;
                allCompanies = new ArrayList<>();
                searchedCompanies = new ArrayList<>();
                LoadCompanies loadCompanies = new LoadCompanies();
                loadCompanies.execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filterCompanies(newText);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                filterValue = null;
                searchedCompanies = new ArrayList<>();
                pageNumber = 0;
                LoadCompanies loadCompanies = new LoadCompanies();
                loadCompanies.execute();
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
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
        ImageView image;
        if(company.isPremium()){
            dialogCompanyInfo.setContentView(R.layout.premium_company_popup);
            image = dialogCompanyInfo.findViewById(R.id.companyImage);
            TextView description = dialogCompanyInfo.findViewById(R.id.companyDescription);
            ImageView website = dialogCompanyInfo.findViewById(R.id.companyWebsite);
            description.setText(company.getDescription() == null? "" : company.getDescription());
            if(company.getImageUrl() != null){
                LoadImage loadImageURL = new LoadImage(getApplicationContext(),company.getImageUrl(), image);
                loadImageURL.execute();
            }else{
                image.setImageDrawable(getDrawable(R.drawable.company_example));
            }
            website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(company.getSiteUrl() != null){
                        if(CheckConnection.haveConnection(CompaniesActivity.this)){
                            dialogCompanyInfo.dismiss();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(company.getSiteUrl()));
                            startActivity(i);
                        }
                        else Toast.makeText(CompaniesActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(CompaniesActivity.this, getString(R.string.no_url_provided), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            dialogCompanyInfo.setContentView(R.layout.company_popup);
            image = dialogCompanyInfo.findViewById(R.id.companyImage);
            image.setImageDrawable(getDrawable(R.drawable.company_example));
        }

        TextView textClose = dialogCompanyInfo.findViewById(R.id.close_popup_company);
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
                if(CheckConnection.haveConnection(CompaniesActivity.this)){
                    Intent intent = new Intent(CompaniesActivity.this, OSMActivity.class);
                    ArrayList<Company> companies = new ArrayList<>();
                    companies.add(company);
                    intent.putExtra("type","Company");
                    intent.putExtra("center", company.getLocation());
                    intent.putExtra("companies",companies);
                    startActivity(intent);
                }
                else Toast.makeText(CompaniesActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
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

                if(zoningId == ZONING_ID_MISSING) {
                    companies = companyDAO.getAllCompanies(token, pageNumber,filterKey,filterValue);
                }
                else{
                    companies = companyDAO.getCompaniesByZoning(token, zoningId, pageNumber, filterKey, filterValue);
                }
                pageNumber++;
            }
            catch (TokenException exception) {
                Toast.makeText(CompaniesActivity.this, getString(exception.getMessageCode()), Toast.LENGTH_SHORT).show();
            }
            catch (CompanyException exception) {
                Toast.makeText(CompaniesActivity.this, getString(exception.getMessageCode()), Toast.LENGTH_SHORT).show();
            }
            return companies;
        }

        @Override
        protected void onPostExecute(ArrayList<Company> companies) {
            allCompanies.addAll(companies);
            searchedCompanies.addAll(companies);
            adapter.setCompanies(searchedCompanies);
            companiesToDisplay.setAdapter(adapter);
            (companiesToDisplay.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}

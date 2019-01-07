package com.henallux.bepway.features.activities;

import android.app.Dialog;
import android.content.Intent;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.henallux.bepway.Exception.TokenException;
import com.henallux.bepway.Exception.ZoningException;
import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.CompanyDAO;
import com.henallux.bepway.dataAccess.ZoningDAO;
import com.henallux.bepway.features.adapters.AllZoningsAdapter;
import com.henallux.bepway.features.recyclerView.RecyclerItemClickListener;
import com.henallux.bepway.features.util.CheckConnection;
import com.henallux.bepway.model.Company;
import com.henallux.bepway.model.Zoning;

import java.io.Serializable;
import java.util.ArrayList;

public class ZoningsActivity extends AppCompatActivity implements Serializable {

    private RecyclerView zoningsToDisplay;
    private AllZoningsAdapter adapter;
    private LoadZonings loadZonings;
    private ArrayList<Zoning> allZonings;
    private ArrayList<Zoning> searchedZonings;
    private Dialog dialog;
    private int pageNumber;
    private boolean firstResearchDone;
    private String filterKey;
    private String filterValue;
    private String lastFilterValue;
    private int lastFirstVisiblePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout_zonings);

        pageNumber = 0;
        firstResearchDone = false;
        filterValue = null;
        lastFilterValue = null;
        searchedZonings = new ArrayList<>();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.zoning_popup);

        zoningsToDisplay = findViewById(R.id.recyclerView);
        zoningsToDisplay.addOnItemTouchListener(new RecyclerItemClickListener(this.getApplicationContext(), zoningsToDisplay, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showPopup(searchedZonings.get(position));
            }
        }));
        zoningsToDisplay.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    lastFirstVisiblePosition = ((LinearLayoutManager)zoningsToDisplay.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    LoadZonings addZonig = new LoadZonings();
                    addZonig.execute();
                }
            }
        });


        allZonings = new ArrayList<>();
        adapter = new AllZoningsAdapter(allZonings);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);

        zoningsToDisplay.addItemDecoration(decoration);
        zoningsToDisplay.setLayoutManager(layoutManager);

        loadZonings = new LoadZonings();
        loadZonings.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.zoning_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchView searchView = (SearchView)menu.findItem(R.id.researchZoning).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!firstResearchDone){
                    firstResearchDone = true;
                    filterKey = "zoningName";
                    pageNumber = 0;
                }
                lastFilterValue = filterValue;
                filterValue = query;
                if(!filterValue.equals(lastFilterValue)) pageNumber = 0;
                allZonings = new ArrayList<>();
                searchedZonings = new ArrayList<>();
                LoadZonings loadZonings = new LoadZonings();
                loadZonings.execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchedZonings.clear();
                for(Zoning zoning : allZonings){
                    if(zoning.getName().toLowerCase().contains(newText.toLowerCase())) searchedZonings.add(zoning);
                }
                adapter.setZonings(searchedZonings);
                zoningsToDisplay.setAdapter(adapter);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void showPopup(final Zoning zoning){
        TextView textClose = dialog.findViewById(R.id.close_popup_zoning);
        TextView superficie = dialog.findViewById(R.id.superficieZoning);
        TextView nbImplantations = dialog.findViewById(R.id.implentationsZoning);
        TextView nomZoning = dialog.findViewById(R.id.zoningTitle);
        TextView localite = dialog.findViewById(R.id.localiteZoning);
        TextView commune = dialog.findViewById(R.id.communeZoning);
        ImageView companies = dialog.findViewById(R.id.listCompaniesPopup);
        ImageView website = dialog.findViewById(R.id.webZoning);
        ImageView map = dialog.findViewById(R.id.mapViewZoning);

        localite.setText(zoning.getCity());
        commune.setText(zoning.getCommune());
        superficie.setText(zoning.getSuperficie()+" " + getString(R.string.size_unity));
        nbImplantations.setText(Integer.toString(zoning.getNbImplantations()));
        nomZoning.setText(zoning.getName());
        companies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckConnection.haveConnection(ZoningsActivity.this)){
                    dialog.dismiss();
                    Intent intentList = new Intent(ZoningsActivity.this, CompaniesActivity.class);
                    intentList.putExtra("zoningId", zoning.getId());
                    startActivity(intentList);
                }
                else Toast.makeText(ZoningsActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
            }
        });
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckConnection.haveConnection(ZoningsActivity.this)){
                    dialog.dismiss();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(zoning.getUrl()));
                    startActivity(i);
                }
                else Toast.makeText(ZoningsActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
            }
        });
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckConnection.haveConnection(ZoningsActivity.this)){
                    dialog.dismiss();
                    LoadCompanies loadCompanies = new LoadCompanies();
                    loadCompanies.execute(zoning);
                }
                else Toast.makeText(ZoningsActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        if(loadZonings != null) loadZonings.cancel(true);
    }

    public class LoadZonings extends AsyncTask<Void, Void, ArrayList<Zoning>> {
        @Override
        protected ArrayList<Zoning> doInBackground(Void... voids) {
            ArrayList<Zoning> zonings = new ArrayList<>();
            ZoningDAO zoningDAO = new ZoningDAO();
            if(isCancelled()){
                Log.i("Zoning","Is cancelled");
            }
            try {
                String token = PreferenceManager.getDefaultSharedPreferences(ZoningsActivity.this).getString("Token",null);
                zonings = zoningDAO.getAllZoningsAPI(token, pageNumber, filterKey, filterValue);
                pageNumber++;
            }
            catch (TokenException exception){
                Toast.makeText(ZoningsActivity.this, getString(exception.getMessageCode()), Toast.LENGTH_SHORT).show();
            }
            catch (ZoningException exception) {
                Toast.makeText(ZoningsActivity.this, getString(exception.getMessageCode()), Toast.LENGTH_SHORT).show();
            }
            return zonings;
        }

        @Override
        protected void onPostExecute(ArrayList<Zoning> zonings) {
            for(Zoning zoning : zonings){
                allZonings.add(zoning);
            }
            searchedZonings.addAll(zonings);
            adapter.setZonings(searchedZonings);
            zoningsToDisplay.setAdapter(adapter);
            ((LinearLayoutManager) zoningsToDisplay.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public class LoadCompanies extends AsyncTask<Zoning, Void, ArrayList<Company>> {
        private Zoning zoning;
        @Override
        protected ArrayList<Company> doInBackground(Zoning... params) {
            zoning = params[0];
            ArrayList<Company> companies = new ArrayList<>();
            CompanyDAO companyDAO = new CompanyDAO();
            if(isCancelled()){
                Log.i("Zoning","Is cancelled");
            }
            try {
                String token = PreferenceManager.getDefaultSharedPreferences(ZoningsActivity.this).getString("Token",null);
                Zoning zoning = params[0];
                companies = companyDAO.getCompaniesByZoning(token, zoning.getId(),0, zoning.getNbImplantations(), null, null);
            } catch (Exception e) {
                Toast.makeText(ZoningsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return companies;
        }

        @Override
        protected void onPostExecute(ArrayList<Company> companies) {
            Intent intent = new Intent(ZoningsActivity.this, OSMActivity.class);
            intent.putExtra("type","Zoning");
            intent.putExtra("center",zoning.getZoningCenter());
            intent.putExtra("companies", companies);
            startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}

package com.henallux.bepway.features.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.ZoningDAO;
import com.henallux.bepway.features.adapters.AllZoningsAdapter;
import com.henallux.bepway.features.recyclerView.RecyclerItemClickListener;
import com.henallux.bepway.model.Zoning;

import java.util.ArrayList;

public class ZoningsActivity extends AppCompatActivity {

    private RecyclerView zoningsToDisplay;
    private AllZoningsAdapter adapter;
    private LoadZonings loadZonings;
    private ArrayList<Zoning> allZonings;
    private ArrayList<Zoning> searchedZonings;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout_zonings);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.zoning_popup);

        zoningsToDisplay = findViewById(R.id.recyclerView);
        zoningsToDisplay.addOnItemTouchListener(new RecyclerItemClickListener(this.getApplicationContext(), zoningsToDisplay, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showPopup(searchedZonings.get(position));
            }
        }));


        allZonings = new ArrayList<>();

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
        TextView nomZoning = dialog.findViewById(R.id.zoningTitle);
        TextView localite = dialog.findViewById(R.id.localiteZoning);
        TextView commune = dialog.findViewById(R.id.communeZoning);
        ImageView companies = dialog.findViewById(R.id.listCompaniesPopup);
        ImageView website = dialog.findViewById(R.id.webZoning);

        localite.setText(zoning.getCity());
        commune.setText(zoning.getCommune());
        superficie.setText(zoning.getSuperficie()+" " + getString(R.string.size_unity));
        nomZoning.setText(zoning.getName());
        companies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intentList = new Intent(ZoningsActivity.this, CompaniesActivity.class);
                startActivity(intentList);
            }
        });
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(zoning.getUrl()));
                startActivity(i);
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
                Log.i("Company","Is cancelled");
            }
            try {
                zonings = zoningDAO.getAllZonings();
            } catch (Exception e) {
                Log.i("Company", e.getMessage());
            }
            return zonings;
        }

        @Override
        protected void onPostExecute(ArrayList<Zoning> zonings) {
            for(Zoning zoning : zonings){
                allZonings.add(zoning);
            }
            searchedZonings = (ArrayList<Zoning>)allZonings.clone();
            adapter = new AllZoningsAdapter(allZonings);
            zoningsToDisplay.setAdapter(adapter);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}

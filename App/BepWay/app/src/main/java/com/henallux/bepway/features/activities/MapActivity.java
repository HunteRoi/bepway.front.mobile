package com.henallux.bepway.features.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.henallux.bepway.R;
import com.henallux.bepway.dataAccess.ZoningDAO;
import com.henallux.bepway.features.map.SurfaceMap;
import com.henallux.bepway.model.Coordinate;
import com.henallux.bepway.model.Zoning;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity {

    private LoadZonings loadZoning;
    private ArrayList<Zoning>allZonings;
    private LocationManager manager;
    private SurfaceMap surfaceMap;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        surfaceMap = (SurfaceMap) findViewById(R.id.SurfaceMap);
        //surfaceMap.draw();

        allZonings = new ArrayList<>();
        loadZoning = new LoadZonings();
        loadZoning.execute();

        manager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) { ;
                Log.i("lcoation",location.getLatitude()+ " - " + location.getLongitude());
        }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(Build.VERSION.SDK_INT < 23){
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        }
        else{
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                return;
            }else {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
            }
        }
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
            ArrayList<PointF> points = new ArrayList<>();
            for(Coordinate coordinate: allZonings.get(0).getRoads()){
                points.add(new PointF(coordinate.getX(), coordinate.getY()));
            }
            surfaceMap.draw(points);
            Log.i("map", allZonings.size()+"");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}

package com.henallux.bepway.features.activities;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.henallux.bepway.R;
import com.henallux.bepway.model.Company;
import com.henallux.bepway.model.Coordinate;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class OSMActivity extends AppCompatActivity {

    private MapView map = null;
    private RoadManager roadManager;
    private Road road;
    private RoadTask roadTask;
    private LocationManager manager;
    private LocationListener listener;
    private Dialog dialog;
    private MyLocationNewOverlay myLocationNewOverlay;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osm);


        dialog = new Dialog(this);
        dialog.setContentView(R.layout.zoning_popup);
        //handle permissions first, before map is created. not depicted here
        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setZoomRounding(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();

        CompassOverlay compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this),map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        Coordinate center = new Coordinate();

        ItemizedIconOverlay.OnItemGestureListener<OverlayItem> itemListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                showPopup();
                return false;
            }
        };

        if(getIntent().getStringExtra("type").equals("Zoning")) mapController.setZoom(16.0);
        else mapController.setZoom(19.0);

        if(getIntent().getSerializableExtra("center") != null) {
            //Get zoning center + its companies and put marker on them
            center = (Coordinate) getIntent().getSerializableExtra("center");
            GeoPoint centerCoord = new GeoPoint(center.getLatitude(),center.getLongitude());
            mapController.setCenter(centerCoord);
        }

        ArrayList<Company> companies = new ArrayList<>();

        if(getIntent().getSerializableExtra("companies") != null){
            companies = (ArrayList<Company>) getIntent().getSerializableExtra("companies");
            //Drawable markerCompany = this.getResources().getDrawable(R.drawable.ic_place_map, null);
            ArrayList<OverlayItem> items = new ArrayList<>();
            for(Company company : companies){
                OverlayItem item = new OverlayItem(company.getName(), company.getSector(), new GeoPoint(company.getLocation().getLatitude(),company.getLocation().getLongitude()));
                //item.setMarker(markerCompany);
                items.add(item);
            }

            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<>(getApplicationContext(), items, itemListener);
            mOverlay.setFocusItemsOnTap(true);
            map.getOverlays().add(mOverlay);
        }


        myLocationNewOverlay = new MyLocationNewOverlay(map);
        map.getOverlays().add(myLocationNewOverlay);
        myLocationNewOverlay.enableMyLocation();

        //items.add(new OverlayItem("Zoning de ciney", "c un pt randon", new GeoPoint(50.309,5.108)));


        /*ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(50.30925, 5.10866);
        waypoints.add(endPoint);*/
        //roadTask = new RoadTask();
        //roadTask.execute(waypoints);

        //map.getOverlayManager().add(mOverlay);


        /*RoadManager roadManager = new OSRMRoadManager(this);
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(50.308, 5.108);
        waypoints.add(endPoint);
        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = roadManager.buildRoadOverlay(road);*/

        //map.getOverlays().add(roadOverlay);
        //map.invalidate();

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

    public void showPopup(){
        TextView textClose = dialog.findViewById(R.id.close_popup_zoning);
        TextView superficie = dialog.findViewById(R.id.superficieZoning);
        TextView nbImplantations = dialog.findViewById(R.id.implentationsZoning);
        TextView nomZoning = dialog.findViewById(R.id.zoningTitle);
        TextView localite = dialog.findViewById(R.id.localiteZoning);
        TextView commune = dialog.findViewById(R.id.communeZoning);
        ImageView companies = dialog.findViewById(R.id.listCompaniesPopup);
        ImageView website = dialog.findViewById(R.id.webZoning);
        ImageView map = dialog.findViewById(R.id.mapViewZoning);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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

    private class RoadTask extends AsyncTask<ArrayList<GeoPoint>, Void, Road> {
        protected Road doInBackground(ArrayList<GeoPoint>... params) {
            ArrayList<GeoPoint> waypoints = params[0];
            RoadManager roadManager = new MapQuestRoadManager(getResources().getString(R.string.mapQuest_apiKey));
            return roadManager.getRoad(waypoints);
        }

        @Override
        protected void onPostExecute(Road result) {
            road = result;
            // showing distance and duration of the road
            Toast.makeText(OSMActivity.this, "distance="+road.mLength, Toast.LENGTH_LONG).show();
            Toast.makeText(OSMActivity.this, "durée="+road.mDuration, Toast.LENGTH_LONG).show();

            if(road.mStatus != Road.STATUS_OK)
                Toast.makeText(OSMActivity.this, "Error when loading the road - status="+road.mStatus, Toast.LENGTH_SHORT).show();
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

            //Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
            for (int i=0; i<road.mNodes.size(); i++){
                RoadNode node = road.mNodes.get(i);
                Marker nodeMarker = new Marker(map);
                nodeMarker.setPosition(node.mLocation);
                //nodeMarker.setIcon(nodeIcon);
                nodeMarker.setSnippet(node.mInstructions);
                nodeMarker.setTitle("Step "+i);
                map.getOverlays().add(nodeMarker);
            }

            map.getOverlays().add(roadOverlay);
            map.invalidate();
            //updateUIWithRoad(result);
        }
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}
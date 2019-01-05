package com.henallux.bepway.features.activities;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.henallux.bepway.R;
import com.henallux.bepway.model.Company;
import com.henallux.bepway.model.Coordinate;
import com.karan.churi.PermissionManager.PermissionManager;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.IOrientationProvider;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class OSMActivity extends AppCompatActivity implements MapEventsReceiver {

    private final double ZOOM_COMPANY = 19.0;
    private final double ZOOM_ZONING =  16.0;
    private final double ZOOM_ROUTING = 20.0;
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
    private final int MY_PERMISSIONS_REQUEST_WRITE_DATA = 2;
    private MapView map = null;
    private IMapController mapController;
    private Road road;
    private Dialog dialog;
    private MyLocationNewOverlay myLocationNewOverlay;
    private ItemizedOverlayWithFocus<OverlayItem> mOverlayMarkers;
    private ImageButton getMyLocation;
    private ImageButton centerMap;
    private PermissionManager permissionManager;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osm);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setZoomRounding(true);
        map.setMultiTouchControls(true);

        RotationGestureOverlay gestureOverlay = new RotationGestureOverlay(map);
        gestureOverlay.setEnabled(true);
        map.getOverlays().add(gestureOverlay);

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(mapEventsOverlay);

        myLocationNewOverlay = new MyLocationNewOverlay(map);

        checkPermissions();
        /*myLocationNewOverlay.enableMyLocation();
        map.getOverlays().add(myLocationNewOverlay);*/

        mapController = map.getController();

        CompassOverlay compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this),map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        getMyLocation = findViewById(R.id.ic_follow_me);
        getMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!myLocationNewOverlay.isMyLocationEnabled()){
                   centerMap.performClick();
                   getMyLocation.setImageResource(R.drawable.ic_follow_me_on);
               }
               else{
                   myLocationNewOverlay.disableFollowLocation();
                   myLocationNewOverlay.disableMyLocation();
                   getMyLocation.setImageResource(R.drawable.ic_follow_me);
               }
            }
        });

        centerMap = findViewById(R.id.ic_center_map);
        centerMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocationNewOverlay.enableMyLocation();
                myLocationNewOverlay.enableFollowLocation();
                mapController.setZoom(20.0);
                mapController.setCenter(myLocationNewOverlay.getMyLocation());

            }
        });

        ItemizedIconOverlay.OnItemGestureListener<OverlayItem> itemListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                //checkPermissions();
                if (ContextCompat.checkSelfPermission(OSMActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(OSMActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    myLocationNewOverlay.enableMyLocation();
                    map.getOverlays().add(myLocationNewOverlay);
                    RoadTask task = new RoadTask();
                    ArrayList<GeoPoint> waypoints = new ArrayList<>();
                    waypoints.add(myLocationNewOverlay.getMyLocation());
                    waypoints.add(new GeoPoint(item.getPoint().getLatitude(), item.getPoint().getLongitude()));
                    mapController.setCenter(myLocationNewOverlay.getMyLocation());
                    map.getOverlays().remove(mOverlayMarkers);
                    mapController.setZoom(ZOOM_ROUTING);
                    task.execute(waypoints);
                }
                else{
                    Toast.makeText(OSMActivity.this, getString(R.string.location_permission_required), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        };

        if(getIntent().getSerializableExtra("center") != null){
            if(getIntent().getStringExtra("type").equals("Zoning")) mapController.setZoom(ZOOM_ZONING);
            else mapController.setZoom(ZOOM_COMPANY);
        }

        Coordinate center;
        if(getIntent().getSerializableExtra("center") != null) {
            //Get zoning center + its companies and put marker on them
            center = (Coordinate) getIntent().getSerializableExtra("center");
            GeoPoint centerCoord = new GeoPoint(center.getLatitude(),center.getLongitude());
            mapController.setCenter(centerCoord);
        }

        ArrayList<Company> companies = new ArrayList<>();

        if(getIntent().getSerializableExtra("companies") != null) {
            companies = (ArrayList<Company>) getIntent().getSerializableExtra("companies");
            //Drawable markerCompany = this.getResources().getDrawable(R.drawable.ic_place_map, null);
            ArrayList<OverlayItem> items = new ArrayList<>();
            for (Company company : companies) {
                OverlayItem item = new OverlayItem(company.getName(), company.getSector(), new GeoPoint(company.getLocation().getLatitude(), company.getLocation().getLongitude()));
                items.add(item);
            }

            mOverlayMarkers = new ItemizedOverlayWithFocus<>(getApplicationContext(), items, itemListener);
            mOverlayMarkers.setFocusItemsOnTap(true);
            map.getOverlays().add(mOverlayMarkers);
        }
    }

    public void checkPermissions(){
        //StringBuilder message = new StringBuilder();
        if (ContextCompat.checkSelfPermission(OSMActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OSMActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(OSMActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OSMActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(map);
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
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
                nodeMarker.setSubDescription(Road.getLengthDurationText(OSMActivity.this, node.mLength, node.mDuration));
                nodeMarker.setTitle("Step "+i);
                map.setMapOrientation((float)myLocationNewOverlay.getMyLocation().bearingTo(node.mLocation));
                map.getOverlays().add(nodeMarker);
            }
            myLocationNewOverlay.enableFollowLocation();
            map.getOverlays().add(roadOverlay);
            map.invalidate();
            //updateUIWithRoad(result);
        }
    }

    public void onResume(){
        super.onResume();
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.enableMyLocation();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        myLocationNewOverlay.disableFollowLocation();
        myLocationNewOverlay.disableMyLocation();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}
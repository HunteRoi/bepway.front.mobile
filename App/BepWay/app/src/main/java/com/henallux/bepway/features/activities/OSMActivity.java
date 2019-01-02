package com.henallux.bepway.features.activities;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.henallux.bepway.R;
import com.henallux.bepway.model.Coordinate;

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

import java.util.ArrayList;
import java.util.List;

public class OSMActivity extends AppCompatActivity {

    private MapView map = null;
    private RoadManager roadManager;
    private Road road;
    private RoadTask roadTask;
    private LocationManager manager;
    private LocationListener listener;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_osm);

        /*org.osmdroid.config.IConfigurationProvider osmConf = org.osmdroid.config.Configuration.getInstance();
        File basePath = new File(getCacheDir().getAbsolutePath(), "osmdroid");
        osmConf.setOsmdroidBasePath(basePath);
        File tileCache = new File(osmConf.getOsmdroidBasePath().getAbsolutePath(), "tile");
        osmConf.setOsmdroidTileCache(tileCache);*/

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
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(50.30553, 5.10502);
        mapController.setCenter(startPoint);

        CompassOverlay compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this),map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        ArrayList<OverlayItem> items = new ArrayList<>();
        //items.add(new OverlayItem("Zoning de ciney", "c un pt randon", new GeoPoint(50.309,5.108)));
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(getApplicationContext(), items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                return false;
            }
        });
        mOverlay.setFocusItemsOnTap(true);

        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(50.30925, 5.10866);
        waypoints.add(endPoint);
        //roadTask = new RoadTask();
        //roadTask.execute(waypoints);

        //map.getOverlayManager().add(mOverlay);
        Coordinate center = new Coordinate(89,89);
        if(getIntent().getSerializableExtra("center") != null) center = (Coordinate) getIntent().getSerializableExtra("center");
        else Log.i("klerkg","ITS NULL");

        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(center.getLatitude(),center.getLongitude()));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlayManager().add(marker);

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
            RoadManager roadManager = new OSRMRoadManager(OSMActivity.this);
            roadManager.addRequestOption("key=W9jJpleg3DK5A2gnGfCQmfTtZF1MaAvH");
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
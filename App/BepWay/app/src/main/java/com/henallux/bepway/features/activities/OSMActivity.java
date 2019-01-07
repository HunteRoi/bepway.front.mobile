package com.henallux.bepway.features.activities;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.henallux.bepway.R;
import com.henallux.bepway.features.map.MyOwnItemizedOverlay;
import com.henallux.bepway.features.util.CheckConnection;
import com.henallux.bepway.model.Company;
import com.henallux.bepway.model.Coordinate;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OSMActivity extends AppCompatActivity implements MapEventsReceiver {

    private final double ZOOM_COMPANY = 19.0;
    private final double ZOOM_ZONING =  16.0;
    private final double ZOOM_ROUTING = 17;
    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
    private GeoPoint mapCenter;
    private IMapController mapController;
    private Road road;
    private MyLocationNewOverlay myLocationNewOverlay;
    private MyOwnItemizedOverlay mOverlayMarkers;
    @BindView(R.id.map) public  MapView map = null;
    @BindView(R.id.ic_follow_me) public ImageButton getMyLocation;
    @BindView(R.id.ic_center_map) public ImageButton centerMap;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osm);
        ButterKnife.bind(this);

        centerMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!myLocationNewOverlay.isMyLocationEnabled()) getMyLocation.performClick();
            List<Overlay> overlays = map.getOverlays();

            if (!overlays.contains(myLocationNewOverlay) || !myLocationNewOverlay.isMyLocationEnabled()) {
                myLocationNewOverlay.enableMyLocation();
                overlays.add(myLocationNewOverlay);
            }

            mapController.setCenter(myLocationNewOverlay.getMyLocation());
            mapController.zoomTo(ZOOM_ROUTING);
            // possible adds here to change camera angle on map so it's axed with designed route
            myLocationNewOverlay.enableFollowLocation();
            }
        });

        getMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            List<Overlay> overlays = map.getOverlays();
            if(!myLocationNewOverlay.isMyLocationEnabled()){
                if (!overlays.contains(myLocationNewOverlay) || !myLocationNewOverlay.isMyLocationEnabled()) {
                    myLocationNewOverlay.enableMyLocation();
                    overlays.add(myLocationNewOverlay);
                }
                getMyLocation.setImageResource(R.drawable.ic_follow_me_on);
            } else {
                myLocationNewOverlay.disableFollowLocation();
                myLocationNewOverlay.disableMyLocation();
                getMyLocation.setImageResource(R.drawable.ic_follow_me);
            }
            }
        });

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setZoomRounding(true);
        map.setMultiTouchControls(true);

        RotationGestureOverlay gestureOverlay = new RotationGestureOverlay(map);
        gestureOverlay.setEnabled(true);
        map.getOverlays().add(gestureOverlay);

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(mapEventsOverlay);

        myLocationNewOverlay = new MyLocationNewOverlay(map);
        //For fun remove this comment
        //myLocationNewOverlay.setPersonIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_me));

        checkPermissions();
        if (hasPermissionToTrackUser()) {
            getMyLocation.performClick();
        }
        mapController = map.getController();

        CompassOverlay compassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this),map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        ItemizedIconOverlay.OnItemGestureListener<OverlayItem> itemListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                return false;
            }

            @Override
            public boolean onItemLongPress(int index, OverlayItem item) {
                //region Second surprise
                Drawable markerCompany = getDrawable(R.drawable.ic_me);
                //endregion
                item.setMarker(markerCompany);
                //drawRouteAndRecenterMapView(item);
                return false;
            }
        };

        if(getIntent().getSerializableExtra("center") != null){
            if(getIntent().getStringExtra("type").equals("Zoning")) mapController.setZoom(ZOOM_ZONING);
            else mapController.setZoom(ZOOM_COMPANY);
        }
        mapController.setCenter(mapCenter);

        Coordinate center;
        if(getIntent().getSerializableExtra("center") != null) {
            //Get zoning center + its companies and put marker on them
            center = (Coordinate) getIntent().getSerializableExtra("center");
            mapCenter = new GeoPoint(center.getLatitude(),center.getLongitude());
            mapController.setCenter(mapCenter);
        }

        ArrayList<Company> companies = new ArrayList<>();

        if(getIntent().getSerializableExtra("companies") != null) {
            companies = (ArrayList<Company>) getIntent().getSerializableExtra("companies");
            Drawable markerCompany = this.getResources().getDrawable(R.drawable.ic_place_map, null);
            ArrayList<OverlayItem> items = new ArrayList<>();
            for (Company company : companies) {
                OverlayItem item = new OverlayItem(company.getName(), company.getSector(), new GeoPoint(company.getLocation().getLatitude(), company.getLocation().getLongitude()));
                item.setMarker(markerCompany);
                items.add(item);
            }

            mOverlayMarkers = new MyOwnItemizedOverlay(getApplicationContext(), items, itemListener, companies, OSMActivity.this);
            map.getOverlays().add(mOverlayMarkers);
        }
    }

    public void drawRouteAndRecenterMapView(OverlayItem item) {
        if (hasPermissionToTrackUser() && CheckConnection.haveConnection(OSMActivity.this)) {
            if (!myLocationNewOverlay.isMyLocationEnabled()) myLocationNewOverlay.enableMyLocation();
            RoadTask task = new RoadTask();
            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            waypoints.add(myLocationNewOverlay.getMyLocation());
            waypoints.add(new GeoPoint(item.getPoint().getLatitude(), item.getPoint().getLongitude()));
            map.getOverlays().remove(mOverlayMarkers);
            task.execute(waypoints);

            centerMap.performClick();
        } else {
            if(!hasPermissionToTrackUser()) Toast.makeText(OSMActivity.this, R.string.location_permission_required, Toast.LENGTH_SHORT).show();
            else Toast.makeText(OSMActivity.this, R.string.no_connection_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermissions(){
        if (!hasFineLocationPermission()) {
            ActivityCompat.requestPermissions(OSMActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        if (!hasCoarseLocationPermission()) {
            ActivityCompat.requestPermissions(OSMActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        }
    }

    private boolean hasPermissionToTrackUser() {
        return hasFineLocationPermission() && hasCoarseLocationPermission();
    }

    private boolean hasFineLocationPermission () {
        return ContextCompat.checkSelfPermission(OSMActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasCoarseLocationPermission () {
        return ContextCompat.checkSelfPermission(OSMActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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

            if(road.mStatus != Road.STATUS_OK) Toast.makeText(OSMActivity.this, R.string.error_loading_road+" "+road.mStatus, Toast.LENGTH_SHORT).show();
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

            //Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
            for (int i=0; i<road.mNodes.size(); i++){
                RoadNode node = road.mNodes.get(i);
                Marker nodeMarker = new Marker(map);
                nodeMarker.setPosition(node.mLocation);
                //nodeMarker.setIcon(nodeIcon);
                nodeMarker.setSnippet(node.mInstructions);
                nodeMarker.setSubDescription(Road.getLengthDurationText(OSMActivity.this, node.mLength, node.mDuration));
                nodeMarker.setTitle(R.string.step+" "+(i+1));
                map.setMapOrientation((float)myLocationNewOverlay.getMyLocation().bearingTo(node.mLocation));
                map.getOverlays().add(nodeMarker);
            }
            myLocationNewOverlay.enableFollowLocation();
            map.getOverlays().add(roadOverlay);
            map.invalidate();
        }
    }

    public void onResume(){
        super.onResume();
        //myLocationNewOverlay.enableFollowLocation();
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

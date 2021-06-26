package com.example.otp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int LOCATION_REQUEST_CODE = 10002;
    private int CIRCLE_RADIUS;
    private GeofenceHelper geofenceHelper;
    private String GEOFENCE_ID = "ID";
    private static final String TAG = "MapsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Intent intent = getIntent();
        CIRCLE_RADIUS = intent.getIntExtra("radius",0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng mn = new LatLng(28.8344, 77.5699);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mn, 16));
        enableUserLocation();

        mMap.setOnMapLongClickListener(this);

    }

    public void enableUserLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //sjow a dialogue to display why permission needed and then ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //Permission not granted
            }
        }


        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //Permission not granted
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if(Build.VERSION.SDK_INT >= 29){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_GRANTED){
                mMap.clear();
                addMarker(latLng);
                createCircle(latLng, CIRCLE_RADIUS);
                addGeofence(latLng, CIRCLE_RADIUS);
            }
            else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},LOCATION_REQUEST_CODE);
                }
                else{
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},LOCATION_REQUEST_CODE);
                }
            }
        }
        else{
            mMap.clear();
            addMarker(latLng);
            createCircle(latLng, CIRCLE_RADIUS);
            addGeofence(latLng, CIRCLE_RADIUS);
        }

    }

    private void addGeofence(LatLng latLng, float radius) {
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER
                |GeofencingRequest.INITIAL_TRIGGER_DWELL|GeofencingRequest.INITIAL_TRIGGER_EXIT);
        GeofencingRequest geofencingRequest = GeofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid){
                        Log.d(TAG,"On Success : Geofence added");

                    }
                })
                .addOnFailureListener(new OnFailureListener(){

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String eMsg = geofenceHelper.getErrorCode(e);
                        Log.d(TAG,"On Failure"+eMsg);

                    }
                });

    }


    private void addMarker(LatLng latLng){

        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);

    }
    private void createCircle(LatLng latLng,float radius){

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(65,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }
}
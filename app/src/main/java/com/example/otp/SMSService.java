package com.example.otp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;


public class SMSService extends JobIntentService {
    private static final String TAG = "SENDSMS Task";
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 50;
    private static final int PERMISSION_FINE_LOCATION = 22;
    String address;
    public double lat;
    public double lng;
    public String finalMessage = "";




    private boolean isLocationGet = false;



    // Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    // Location request
    LocationRequest locationRequest;

    // Location CallBack
    LocationCallback locationCallBack;

    Context mcontext;


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    public SMSService(Context context) {
        mcontext = context;
    }

    public SMSService() {
        super();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        // Will send sms to contacts selected by user
        mcontext=this;

        // set all properties of locationRequest
        locationRequest = new LocationRequest();

        // how often does the default location check occurs?
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);

        // how often does the location check occurs when using most frequent update?
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // Called when whenever the update interval is met
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                address = fetchAddress(locationResult.getLastLocation());
            }
        };

        startLocationUpdates();
        updateGPS();



//        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage("8770103066", null, address, null, null);
//        Toast.makeText(getApplicationContext(), "SMS SENT", Toast.LENGTH_LONG).show();








        Log.i("info", "This is the SMS Service");
        stopSelf();

        return START_NOT_STICKY;
    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mcontext);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();

    }



    private void updateGPS() {
        // get permissions from the user to track gps
        // get the current location from the fused client
        // update the UI

        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mcontext);
        if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    // put the values of location into UI components
                    fetchAddress(location);

                }
            });
        } else {
            // permission not granted.


        }
    }

    private String fetchAddress(Location location) {
        Geocoder geocoder = new Geocoder(mcontext);

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);


            if(!isLocationGet) {
                isLocationGet = true;
                lat = location.getLatitude();
                lng = location.getLongitude();
                String mapURL = "https://maps.google.com/?q="+lat+","+lng;
                finalMessage = "Status!!, My Current location is:" + address + "\n \n" + "Open on google maps" + "\n" + mapURL + "\n \n" + "By HumanAid | 2021" + "\n \n";
                SmsManager smsManager = SmsManager.getDefault();
                for(Contact contact: SensorService.tempList) {
                    String number = contact.getPhonenumber();

                    String message = "Hi " + contact.getName() + ", " + contact.getMessage() + "\n \n" + finalMessage;
                    ArrayList<String> parts = smsManager.divideMessage(message);
                    smsManager.sendMultipartTextMessage(number, null, parts, null, null);
                }
                if(SensorService.tempList.size()>0) {
                    Toast.makeText(getApplicationContext(), "SMS SENT to " + SensorService.tempList.size() + " Contacts", Toast.LENGTH_LONG).show();
                }

            }





        } catch (Exception e) {

            Log.d("HumanAid App:", "fetching address failed");
            address = "";
        }

        return (address);


    }
}

package com.example.otp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {


    public static final String TAG = "GeofenceHelper";
    PendingIntent pendingIntent;

    public GeofenceHelper(Context base) {
        super(base);
    }

    public static GeofencingRequest getGeofencingRequest(Geofence geofence){

        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    public Geofence getGeofence(String ID, LatLng latLng,float radius,int transitionType){
        return new Geofence.Builder().setCircularRegion(latLng.latitude,latLng.longitude,radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionType)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

    }

    public PendingIntent getPendingIntent(){
        if(pendingIntent != null)
            return pendingIntent;
        else{
            Intent intent = new Intent(this,GeofenceBroadcastReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this,607,intent,pendingIntent.FLAG_UPDATE_CURRENT);
            return pendingIntent;
        }
    }
    public String getErrorCode(Exception e){
        if(e instanceof ApiException){
            ApiException exception = (ApiException) e;
            switch(exception.getStatusCode()){
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE NOT AVAILABLE";

                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "TOO MANY GEOFENCES";

                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "TOO MANY PENDING REQUEST";
            }
        }
        return e.getLocalizedMessage();
    }
}

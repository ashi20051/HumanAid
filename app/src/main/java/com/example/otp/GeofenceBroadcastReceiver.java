package com.example.otp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";

    @Override
    public void onReceive(Context context, Intent intent) {
        PendingResult pendingResult=goAsync();
        Toast.makeText(context,"Boot ACtion Recieved",Toast.LENGTH_LONG).show();
        new Task(pendingResult, intent).execute();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        Toast.makeText(context,"Geofence Triggered",Toast.LENGTH_SHORT).show();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            Log.d(TAG,"On Receive : Error Triggered");
            return;
        }

        List<Geofence> geofencingList= geofencingEvent.getTriggeringGeofences();

        for(Geofence geofence : geofencingList) {
            Log.d(TAG,"On Receive"+geofence.getRequestId());
        }
        //  Location loc = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "ENTERED Geofence!", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER","",MapsActivity.class);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "EXITED Geofence!", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT","",MapsActivity.class);
                Intent smsIntent = new Intent(context,SMSService.class);
                context.startService(smsIntent);
                break;

            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL","", MapsActivity.class);
                break;
        }
    }

    private static class Task extends AsyncTask<Void, Void,Void> {

        PendingResult pendingResult;
        Intent intent;
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pendingResult.finish();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
//                Thread.sleep(150000);
                while(SensorService.IS_SERVICE_RUNNING){

                    Thread.sleep(5000);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        public Task(PendingResult pendingResult,Intent intent){

            this.pendingResult=pendingResult;
            this.intent=intent;


        }
    }


}

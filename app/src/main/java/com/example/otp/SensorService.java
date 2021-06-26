package com.example.otp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SensorService extends JobIntentService implements SensorEventListener {
    //Initialization:
    private TextView xTextView,yTextView,zTextView;//X y And Z Possitions heres

    //Step1: Declare the SensorManager And Sensor
    public static SensorManager sensorManager;//X y And Z Possitions heres
    public static Sensor accelerometer;
    public static boolean isAccelerometer;
    private float crrX,crrY,crrZ;
    private float lastX,lastY,lastZ;
    private boolean itIsNotFirstTime;
    private float xDiff, yDiff, zDiff;
    private float shakehreshold=5f;
    public static Vibrator vibrator;
    public boolean isToggleStarts;
    private Context currentContext;
    public static int counter=0;
    private static int threshols=7;
    private static final String LOG_TAG = "ForegroundService";
    public static boolean IS_SERVICE_RUNNING = false;

    public static boolean IS_TOGGLE_ENABLED = false;
    public static List<Contact> tempList;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isAccelerometer){
            sensorManager.unregisterListener(this);
            IS_SERVICE_RUNNING=false;
            counter=0;
        }

        Log.i("info","SensorService|-------------Inside onDestry||Stop Service");
        Toast.makeText(this,"Sensor Service Stopped",Toast.LENGTH_LONG);

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("Hello==========================");
        //Step3: Get The Services For The Sensors After Adding the Permissions[Done in MainActivity]

        currentContext=this;
        //Step4: Check If Sensor Is Available in the Phs and Set it to booleansss[Done in MainActivity]

        if(isAccelerometer) {

            sensorManager.registerListener((SensorEventListener) currentContext, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            IS_SERVICE_RUNNING=true;



        }
        Log.i("info", "SensorService|-------------Inside onStartCommand||Registering the Notification Foreground");

//===============================Build The Noification==================================
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel("Channelid1","Song Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);

        }
        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class); // Here pass your activity where you want to redirect.
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT);

        Notification notification =  new NotificationCompat.Builder(this,"Channelid1")
                .setContentTitle("HumanAid App is executing in the background")
                .setSmallIcon(R.drawable.logo_demo)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Shake Sensor is active"))
                .addAction(getResources().getIdentifier("com.example.humanaid:drawable/cd", null, null), "Action Button", pendingIntent)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();

        startForeground(1,notification);

        Log.i("info", "SensorService|-------------Provided the Notification");

        Toast.makeText(this, "SensorService", Toast.LENGTH_LONG);

        return  START_STICKY;
//=============================End Of Function=========================================================
//        ===================================





//        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
//            Log.i(LOG_TAG, "Received Start Foreground Intent ");
//            showNotification();
//            Toast.makeText(this, "Service Started!", Toast.LENGTH_SHORT).show();
//
//        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
//            Log.i(LOG_TAG, "Sensor Play");
//
//            Toast.makeText(this, "Sensor Play!", Toast.LENGTH_SHORT).show();
//        } else if (intent.getAction().equals(
//                Constants.ACTION.STOPFOREGROUND_ACTION)) {
//            Log.i(LOG_TAG, "Received Stop Foreground Intent");
//            stopForeground(true);
//            stopSelf();
//        }





//            ======================================================================

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel notificationChannel = new NotificationChannel("Channelid1", "Sensor Notification", NotificationManager.IMPORTANCE_DEFAULT);
//                NotificationManager manager = getSystemService(NotificationManager.class);
//                manager.createNotificationChannel(notificationChannel);
//
//            }

//        int position=intent.getIntExtra("possition",0);
//        arrayList=intent.getStringArrayListExtra("list");


//            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class); // Here pass your activity where you want to redirect.
//            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT);
//
//
////
//            Notification notification = new NotificationCompat.Builder(this, "Channelid1")
//                    .setContentTitle("Music App IIIT")
//                    .setContentText("")
//                    .setSmallIcon(R.drawable.ic_launcher_background)
//                    .setStyle(new NotificationCompat.BigTextStyle()
//                            .bigText("song playing"))
//                    .addAction(getResources().getIdentifier("com.mc.myapplication2:drawable/cd", null, null), "Action Button", pendingIntent)
//                    .setContentIntent(pendingIntent)
//                    .setOngoing(true).build();




//       startForeground(1,notification);
//            startForeground(1, notification);//notificationBuilder.getNotification());
//            return START_NOT_STICKY;

    }


    @Override
    public void onCreate () {
        super.onCreate();
    }




    @Override
    public void onSensorChanged(SensorEvent event) {
        //Step6: This Does the Reqires Task Heres on Sensor Activation


//        xTextView.setText(event.values[0] + "m/s2");
//        yTextView.setText(event.values[1] + "m/s2");
//        zTextView.setText(event.values[2] + "m/s2");
        Log.i("","event  value is==================="+event);
        crrX = event.values[0];
        crrY = event.values[1];
        crrZ = event.values[2];

        if (itIsNotFirstTime) {
            xDiff = Math.abs(lastX - crrX);
            yDiff = Math.abs(lastY - crrY);
            zDiff = Math.abs(lastZ - crrZ);
            Log.i("info", "The Counter is:            " + counter);
            if (((xDiff > shakehreshold && yDiff > shakehreshold) ||
                    (xDiff > shakehreshold && zDiff > shakehreshold) ||
                    (yDiff > shakehreshold && zDiff > shakehreshold))
            ) {

                counter++;
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.i("info","-----------------------The Counter Reset----------");
//                        counter=0;
//                    }
//                }, 3000);
                new Timer().schedule(new TimerTask() {
                    int prev = counter;

                    @Override
                    public void run() {


                        if (prev == counter) {
                            Log.i("info", "-----------------------The Counter Reset----------");
                            counter = 0;
                        }
                        // this code will be executed after 2 seconds
                    }
                }, 3000);//Withinsss 3sec Otherwise Reset The Counter

                //Definately Its a Shake Heres

                if (counter % threshols == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Random rnd = new Random();
//                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//                        getWindow().getDecorView().setBackgroundColor(color);
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                        Log.i("info","This is The Sensor Start Event");
//                       -----------------Message Code By Apporv---------------------

//                        new SendSMS(currentContext).execute();

                        Intent smsIntent =new Intent(currentContext,SMSService.class);
                        currentContext.startService(smsIntent);


//                        --------------------End------------------------------------



                        Toast.makeText(currentContext,"!!!It's Danger Now, Contacts Alerted",Toast.LENGTH_LONG);
                        counter = 0;
                    } else {

                        vibrator.vibrate(500);
                        //Its Deprecated In API Nos 26
                    }
                }
            }

        }


        lastX = crrX;
        lastY = crrY;
        lastZ = crrZ;

        itIsNotFirstTime = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}

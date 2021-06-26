package com.example.otp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import java.util.Locale;

public class TimerService extends JobIntentService {

    private long timer;
    public CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;
    Context context;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        timer =  intent.getLongExtra("timerValue", 0);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel("Channelid1","Duration Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

        setTime(timer);
        startTimer();
        context = this;



        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class); // Here pass your activity where you want to redirect.
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT);

        Notification notification =  new NotificationCompat.Builder(this,"Channelid1")
                .setContentTitle("HumanAid App is executing in the background")
                .setSmallIcon(R.drawable.logo_demo)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Shake Sensor + Timer ON!"))
                .addAction(getResources().getIdentifier("com.example.humanaid:drawable/cd", null, null), "Action Button", pendingIntent)
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();

        startForeground(1,notification);
        return  START_NOT_STICKY;

    }


    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
    }

    private void updateCountDown() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeft;
        if(hours > 0) {
            timeLeft = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes, seconds);
        } else {
            timeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d",hours,minutes, seconds);
        }
        //mTextViewCountDown.setText(timeLeft);


        if(hours == 0 && minutes == 0 && seconds == 1) {
            Log.d("HumanAid:", "Timer ended");
            Intent smsIntent = new Intent(context,SMSService.class);
            context.startService(smsIntent);
        }

    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDown();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                //updateUI();
            }
        }.start();
        mTimerRunning = true;
        //updateUI();

    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        //updateUI();
    }


    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDown();
        //updateUI();
    }


    @Override
    public boolean isStopped() {
        return super.isStopped();

    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Timer Services is Stopped!", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}

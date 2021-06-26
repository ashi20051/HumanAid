package com.example.otp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Toolbar toolbar;
    private Context currentContext;
    public  Switch mySwitch;
    public Button setTimerButton;
    public ImageButton locationBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        currentContext=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

//        -------------------Lokesh Code For Sensor--------------------------
        //        ----------------------------Sensor Code----------------------------------------


        //Step3: Get The Services For The Sensors After Adding the Permissions
        SensorService.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        SensorService.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

//        //Step4: Check If Sensor Is Available in the Phone and Set it to boolean
        if (SensorService.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            //Sensor Is present in  the phonesss
            SensorService.accelerometer = SensorService.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            SensorService.isAccelerometer = true;
            Toast.makeText(this,"Sensor Service is Active Now Shake 3-4 times to alert",Toast.LENGTH_LONG);
        } else {

//            xTextView.setText("Accelerometer Not Heres");
            Toast.makeText(this,"Sorry sensors not present to work",Toast.LENGTH_LONG);

            SensorService.isAccelerometer = false;
        }

        mySwitch = findViewById(R.id.customSwitch);
        if(SensorService.IS_SERVICE_RUNNING){
            mySwitch.setChecked(true);

        }

        //Step5: If Sensors Are Available Regester Or nregister Those
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SensorService.isAccelerometer) {
                    SensorService.counter = 0;
                    Intent intent=new Intent(currentContext,SensorService.class);
//                    Intent intent=new Intent(currentContext,DurationService.class);
                    setTimerButton = findViewById(R.id.setDurationbtn);
                    locationBtn = findViewById(R.id.locationBtn);
                    if (isChecked) {
                        SensorService.IS_TOGGLE_ENABLED = true;
                        setTimerButton.setVisibility(View.VISIBLE);
                        locationBtn.setVisibility(View.VISIBLE);
                        if (!SensorService.IS_SERVICE_RUNNING) {
                            intent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//                            Toast.makeText(currentContext,"!!!It's  danger, Contacts Alerted",Toast.LENGTH_LONG);
                            SensorService.IS_SERVICE_RUNNING = true;
                        } else {

                            intent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                            SensorService.IS_SERVICE_RUNNING = false;
                            Toast.makeText(currentContext,"!!!It's Safe Now, Contacts Alerted",Toast.LENGTH_LONG);

                        }
                        currentContext.startService(intent);
//                        sensorManager.registerListener((SensorEventListener) currentContext, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

                    } else {
                        SensorService.IS_TOGGLE_ENABLED = false;
                        setTimerButton.setVisibility(View.INVISIBLE);
                        locationBtn.setVisibility(View.INVISIBLE);
                        currentContext.stopService(intent);
                        SensorService.IS_SERVICE_RUNNING=false;
                        Intent intentSMSService=new Intent(currentContext,SMSService.class);
                        Intent intentDuration=new Intent(currentContext,TimerService.class);

                        currentContext.stopService(intentSMSService);
                        currentContext.stopService(intentDuration);

//                        Intent intentDuration=new Intent(currentContext,SMSService.class);
                    }
                }
            }
        });

//        setTimerButton = findViewById(R.id.setDurationbtn);
//        locationBtn = findViewById(R.id.locationBtn);
//        if(mySwitch.isChecked()) {
//            setTimerButton.setVisibility(View.VISIBLE);
//            locationBtn.setVisibility(View.VISIBLE);
//        } else {
//            setTimerButton.setVisibility(View.INVISIBLE);
//            locationBtn.setVisibility(View.INVISIBLE);
//        }


//        --------------------------------End------------------------------------------------------






//        ------------------------End-----------------------------------------

        //Adding fragment
        adapter.AddFragment(new ContactFragment(),"Contacts");
        adapter.AddFragment(new LocationFragment(),"Shield");
        adapter.AddFragment(new EmergencyFragment(),"Emergency");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkPermissions() {
        // Code for checking All permissions needed for the App
    }
}
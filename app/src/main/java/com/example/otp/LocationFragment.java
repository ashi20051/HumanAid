package com.example.otp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public Button setTimerBtn, startStopTimerBtn;
    public EditText setTimeret, radiuset;
    public TextView mTextViewCountDown;
    public CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private Context context;
    private boolean isSetBtnClicked = false;

    private static final String TAG = "LocationFragment";
    ImageButton locationButton;
    EditText radiusTxt;
    int radius;







    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationFragment.
     */


    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_location, container, false);

        context = getActivity();


        // Set Timer Btn
        setTimerBtn = view.findViewById(R.id.setDurationbtn);

        // EditText Input
        setTimeret = view.findViewById(R.id.enterDurationev);

        // startStop Timer Btn
        startStopTimerBtn = view.findViewById(R.id.startStopTimerBtn);

        // Timer TextView
        mTextViewCountDown = view.findViewById(R.id.countDownText);

        // radius edit text
        radiuset = view.findViewById(R.id.radiusTxt);


//        mySwitch = getActivity().findViewById(R.id.customSwitch);

//        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    Toast.makeText(getContext(), "Switch on!", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getContext(), "Switch off!", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        locationButton = view.findViewById(R.id.locationBtn);





        radiusTxt = view.findViewById(R.id.radiusTxt);

        locationButton.setOnClickListener(v -> {
            String inputRadius = radiuset.getText().toString();
            if(inputRadius.length() == 0) {
                Toast.makeText(getContext(), " Radius Field cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            long radiusInput = Long.parseLong(inputRadius);
            if (radiusInput <= 0) {
                Toast.makeText(getContext(), "Enter a positive number for radius", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d(TAG,"Edit Button Clicked");
                radius = Integer.parseInt( radiusTxt.getText().toString() );
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("radius",radius);
                startActivity(intent);
            }


        });


        setTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!SensorService.IS_SERVICE_RUNNING){
                    Toast.makeText(getContext(),"Please Start The Toggle Service",Toast.LENGTH_LONG).show();
                    return;
                }
                isSetBtnClicked = true;
                String input = setTimeret.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(getContext(), " Duration Field cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(input) * 60000;

                if (millisInput <= 0) {
                    Toast.makeText(getContext(), "Enter a positive number", Toast.LENGTH_SHORT).show();
                }
                else {
                    setTime(millisInput);
                    setTimeret.setText("");
                    Intent startTimerServiceintent = new Intent(getContext(), TimerService.class);
                    startTimerServiceintent.putExtra("timerValue", millisInput);
                    getActivity().startService(startTimerServiceintent);
                    startTimer();
                }


            }

        });


        startStopTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    Intent stopTimerServiceintent = new Intent(getContext(), TimerService.class);
                    getActivity().stopService(stopTimerServiceintent);
                    stopTimer();
                } else {
                    if(SensorService.IS_TOGGLE_ENABLED == false) {
                        Toast.makeText(getContext(), "Toggle is OFF! please ON the Toggle", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        if(setTimeret.getText().toString().length() == 0) {
                            Toast.makeText(getContext(), "Enter the value for timer! and click SET", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            if(isSetBtnClicked == true) {
                                startTimer();
                            }
                            else {
                                Toast.makeText(getContext(), "Please Click SET to set the timer!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }


                    }

                }
            }
        });





        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SensorService.IS_TOGGLE_ENABLED)  {

        } else {
            setTimerBtn.setVisibility(View.INVISIBLE);
            locationButton.setVisibility(View.INVISIBLE);
            if(mTimerRunning) {
                mTextViewCountDown.setText("00:00:00");
                mTimerRunning = false;
            }

        }

    }

    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateUI();
            }
        }.start();
        mTimerRunning = true;
        updateUI();

    }

    private void stopTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mTextViewCountDown.setText("00:00:00");
        updateUI();
    }


    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        updateUI();
    }

    private void updateCountDownText() {

        if(SensorService.IS_TOGGLE_ENABLED == false || SensorService.IS_SERVICE_RUNNING==false) {
            stopTimer();//Changes For Timer Rest On Toggle
            mTextViewCountDown.setText("00:00:00");
            startStopTimerBtn.setText("START");
            return;
        }


        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeft;
        if(hours > 0) {
            timeLeft = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,minutes, seconds);
        } else {
            timeLeft = String.format(Locale.getDefault(),"%02d:%02d:%02d",hours,minutes, seconds);
        }
        mTextViewCountDown.setText(timeLeft);


        if(hours == 0 && minutes == 0 && seconds == 1) {
            Toast.makeText(context, "Timer finished", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUI() {
        if(mTimerRunning) {
            setTimerBtn.setVisibility(View.INVISIBLE);
            startStopTimerBtn.setText("Stop");
        } else {
            setTimerBtn.setVisibility(View.VISIBLE);
            startStopTimerBtn.setText("Start");

            if (mTimeLeftInMillis < 1000) {
                startStopTimerBtn.setVisibility(View.INVISIBLE);
            } else {
                startStopTimerBtn.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft",mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();
        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }


    }


    @Override
    public void onStart() {
        super.onStart();
        if(SensorService.IS_TOGGLE_ENABLED == false) {
            mTextViewCountDown.setText("00:00:00");
            mTimerRunning = false;
            return;
        }
        SharedPreferences preferences = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        mStartTimeInMillis = preferences.getLong("startTimeInMillis", 0);
        mTimeLeftInMillis = preferences.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = preferences.getBoolean("timerRunning", false);
        updateCountDownText();
        updateUI();
        if(mTimerRunning) {
            mEndTime = preferences.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if(mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateUI();
            } else {
                startTimer();
            }
        }
    }



}


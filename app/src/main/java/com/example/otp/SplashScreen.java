package com.example.otp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

//        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.flipping);
//        anim.setTarget(R.id.imgLogo);
//        anim.setDuration(3000);
//        anim.start();
        ImageView imageView = (ImageView) findViewById(R.id.imgLogo);
        imageView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse));


//
//        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
//        findViewById(R.id.imgLogo).startAnimation(rotate);
//        rotate.reset();
//        rotate.start();

        new Handler().postDelayed(new Runnable() {



            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, RegisterActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}

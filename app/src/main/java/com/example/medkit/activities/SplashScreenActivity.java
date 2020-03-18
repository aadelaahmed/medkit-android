package com.example.medkit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.medkit.R;

public class SplashScreenActivity extends AppCompatActivity {
    private Intent intent;
    private SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedpreferences = this.getSharedPreferences("sign",Context.MODE_PRIVATE);
        displaySplashScreen();

    }

        private void displaySplashScreen() {
            int SPLASH_TIME_OUT = 1000;
            final boolean isFirstTime = sharedpreferences.getBoolean("isFirstTime",true);
            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                        finish();
                        //TODO: Change later to isFirstTime
                        //if first time open slideshow, else open home
                        if (true) {
                            overridePendingTransition(0, android.R.anim.fade_out);
                            //Intent intent = new Intent(SplashScreenActivity.this, SignHomeActivity.class);
                            Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }else{
                            //TODO
                        }

                }
            }, SPLASH_TIME_OUT);
        }
}

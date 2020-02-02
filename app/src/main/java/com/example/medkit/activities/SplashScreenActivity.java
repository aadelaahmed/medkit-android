package com.example.medkit.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.medkit.R;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;
    private Intent intent;
    private SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        displaySplashScreen();
    }

        private void displaySplashScreen() {
            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {

                        finish();
                        overridePendingTransition(0, android.R.anim.fade_out);
                        Intent dialogIntent = new Intent(SplashScreenActivity.this, SlideShowActivity.class);
                        startActivity(dialogIntent);

                }
            }, SPLASH_TIME_OUT);
        }
}

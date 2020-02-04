package com.example.medkit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.medkit.R;

public class sign_in_first_activity extends AppCompatActivity {
    TextView textViewLogin, textViewTouch;
    Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_first_activity);

        textViewTouch = findViewById(R.id.text_view_touch);
        textViewLogin = findViewById(R.id.text_view_login);
        buttonLogin = findViewById(R.id.button_login);
        setFontType();

    }


    void setFontType() {
        Typeface robotoMediumType = ResourcesCompat.getFont(this, R.font.roboto_medium);
        textViewTouch.setTypeface(robotoMediumType);


        Typeface robotoBoldType = ResourcesCompat.getFont(this, R.font.roboto_bold2);
        textViewLogin.setTypeface(robotoBoldType);


        buttonLogin.setTypeface(robotoBoldType);
    }
}

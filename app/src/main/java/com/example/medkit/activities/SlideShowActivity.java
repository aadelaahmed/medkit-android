package com.example.medkit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.medkit.R;

public class SlideShowActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);
        viewPager = findViewById(R.id.view_pager_slide_show);
        dotsLinearLayout = findViewById(R.id.dots_linear_layout);
    }
}

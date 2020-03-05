package com.example.medkit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medkit.R;
import com.example.medkit.model.CustomViewPager;
import com.example.medkit.utils.SliderAdapter;

public class DoctorRegistrationActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;
    private Intent intent;
    private CustomViewPager viewPager;
    private LinearLayout dotsLinearLayout;
    private Button nextBtn;
    private Button backBtn;
    private int nCurrentPage;
    CustomViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {
            addDots(position);
            nCurrentPage = position;
            if (position == 3) {
                nextBtn.setText("Finish");
            } else {
                nextBtn.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    ViewPager.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,android.R.anim.fade_out);
        setContentView(R.layout.activity_doctor_registration);

        sharedpreferences = this.getSharedPreferences("sign",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("isFirstTime", false);
        editor.apply();

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.medkit_text);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);


        viewPager = findViewById(R.id.view_pager_slide_show);
        dotsLinearLayout = findViewById(R.id.dots_linear_layout);
        SliderAdapter sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        addDots(0);
        viewPager.addOnPageChangeListener(viewlistener);
        nextBtn = findViewById(R.id.slider_next_btn);
        backBtn = findViewById(R.id.slider_skip_btn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(nCurrentPage + 1);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent = new Intent(SlideShowActivity.this, SignUpActivity.class);
//                startActivity(intent);
                viewPager.setCurrentItem(nCurrentPage - 1);
            }
        });
        viewPager.setOnTouchListener( touchListener);
    }

    public void addDots(int position) {
        TextView[] nDots = new TextView[5];
        dotsLinearLayout.removeAllViews();
        for (int i = 0; i < 4; i++) {
            nDots[i] = new TextView(this);
            nDots[i].setText(Html.fromHtml("&#8212;")+" ");
            nDots[i].setTextSize(30);
            nDots[i].setTextColor(getResources().getColor(R.color.lightBackGroundColor));
            nDots[i].setShadowLayer(3,0,0,getResources().getColor(R.color.colorPrimaryDark));
            dotsLinearLayout.addView(nDots[i]);
        }
        nDots[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }
}

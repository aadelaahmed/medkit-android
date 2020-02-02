package com.example.medkit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.medkit.R;
import com.example.medkit.utils.SliderAdapter;

public class SlideShowActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLinearLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] nDots;
    private Button nextBtn;
    private Button skipBtn;
    private int nCurrentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);
        viewPager = findViewById(R.id.view_pager_slide_show);
        dotsLinearLayout = findViewById(R.id.dots_linear_layout);
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        addDots(0);
        viewPager.addOnPageChangeListener(viewlistener);
        nextBtn = findViewById(R.id.slider_next_btn);
        skipBtn = findViewById(R.id.slider_skip_btn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(nCurrentPage+1);
            }
        });
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implement
            }
        });
    }
    public void addDots(int position){
        nDots = new TextView[5];
        dotsLinearLayout.removeAllViews();
        for (int i = 0;i<5;i++){
            nDots[i] = new TextView(this);
            nDots[i].setText(Html.fromHtml("&#8226;"));
            nDots[i].setTextSize(40);
            nDots[i].setTextColor(getResources().getColor(R.color.dotsNotActiveColor));
            dotsLinearLayout.addView(nDots[i]);
        }
        nDots[position].setTextColor(getResources().getColor(R.color.buttonMainColor));
    }
    ViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {
            addDots(position);
            nCurrentPage = position;
            if(position == 4){
                nextBtn.setVisibility(View.GONE);
                skipBtn.setVisibility(View.GONE);
            }else{
                nextBtn.setVisibility(View.VISIBLE);
                skipBtn.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}

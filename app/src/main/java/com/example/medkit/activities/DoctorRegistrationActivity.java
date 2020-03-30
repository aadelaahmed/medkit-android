package com.example.medkit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.model.CustomViewPager;
import com.example.medkit.model.User;
import com.example.medkit.utils.SliderAdapter;

public class DoctorRegistrationActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;
    private Intent intent;
    private CustomViewPager viewPager;
    private LinearLayout dotsLinearLayout;
    private Button nextBtn;
    private Button backBtn;
    private int nCurrentPage;
    EditText firstEdText;
    EditText secondEdText;
    View view;
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

        sharedpreferences = this.getSharedPreferences(UserTypeActivity.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("isFirstTime", false);
        editor.apply();

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.medkit_text);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);


        viewPager = findViewById(R.id.view_pager_slide_show);
        dotsLinearLayout = findViewById(R.id.dots_linear_layout);
        final SliderAdapter sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        addDots(0);
        viewPager.addOnPageChangeListener(viewlistener);
        nextBtn = findViewById(R.id.slider_next_btn);
        backBtn = findViewById(R.id.slider_skip_btn);

        nextBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                view = viewPager.getChildAt(nCurrentPage);
                //get text from view item
                firstEdText = view.findViewById(R.id.first_edit_text);
                secondEdText = view.findViewById(R.id.second_edit_text);
                String tempFirstStr = firstEdText.getText().toString();
                String tempSecondStr = secondEdText.getText().toString();
                Log.e("first", tempFirstStr);
                showMessage(tempFirstStr);
                if (nCurrentPage < 3) {
                    if (!tempFirstStr.trim().isEmpty()) {

                        switch (nCurrentPage) {
                            case 0:
                                editor.putString(User.SPECIALITY, tempFirstStr);
                                editor.apply();
                                break;

                            case 1:
                                editor.putString("BIO_KEY", tempFirstStr);
                                editor.apply();
                                break;

                            case 2:
                                editor.putString(User.LOCATION, tempFirstStr);
                                editor.apply();
                                break;

                        }
                        viewPager.setCurrentItem(nCurrentPage + 1);
                    } else
                        showMessage("please fill all input fields");

                } else if (nCurrentPage == 3) {
                    if (!tempFirstStr.trim().isEmpty() || !tempSecondStr.trim().isEmpty()) {
                        editor.putString(User.G_FACULTY, tempFirstStr);
                        editor.putString(User.G_YEAR, tempSecondStr);
                        editor.apply();
                        viewPager.setCurrentItem(nCurrentPage + 1);
                    } else
                        showMessage("please fill all input fields");
                } else

                    startActivity(new Intent(DoctorRegistrationActivity.this, SignInActivity.class));
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
//                intent = new Intent(SlideShowActivity.this, SignUpActivity.class);
//                startActivity(intent);
                viewPager.setCurrentItem(nCurrentPage - 1);
            }
        });
        viewPager.setOnTouchListener( touchListener);
    }

    private void showMessage(String message) {
        Toast.makeText(DoctorRegistrationActivity.this, message, Toast.LENGTH_LONG).show();
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

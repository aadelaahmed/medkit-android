package com.example.medkit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.medkit.R;
import com.example.medkit.model.CustomViewPager;
import com.example.medkit.model.User;
import com.example.medkit.utils.SliderAdapter;
import com.reginald.editspinner.EditSpinner;

public class DoctorRegistrationActivity extends AppCompatActivity {


    EditSpinner spinner;
    CustomViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {
            addDots(position);

            if (position == 3) {
                nextBtn.setText("Finish");
            } else {
                nextBtn.setText("Next");
            }
            viewPager.setCurrentItem(nCurrentPage, true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    EditText firstEdText;
    EditText secondEdText;
    View view;
    String emailUser = null;
    ViewPager.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }

    };
    private SharedPreferences sharedpreferences;
    private Intent intent;
    private CustomViewPager viewPager;
    private LinearLayout dotsLinearLayout;
    private Button nextBtn;
    private Button backBtn;
    private int nCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,android.R.anim.fade_out);
        setContentView(R.layout.activity_doctor_registration);

        sharedpreferences = this.getSharedPreferences(SignHomeActivity.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("isFirstTime", false);
        editor.apply();
        emailUser = sharedpreferences.getString(User.EMAIL, null);
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
        viewPager.setOffscreenPageLimit(3);
        nCurrentPage = viewPager.getCurrentItem();



        nextBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //get the view for the current page
                view = viewPager.getChildAt(nCurrentPage);

                //get text from view item
                firstEdText = view.findViewById(R.id.first_edit_text);
                secondEdText = view.findViewById(R.id.second_edit_text);
                spinner = view.findViewById(R.id.speciality_list);
                String tempFirstStr = firstEdText.getText().toString();
                String tempSecondStr = secondEdText.getText().toString();
                String valueFromSpinner = spinner.getText().toString();

                switch (nCurrentPage) {
                    case 0:
                        if (!tempFirstStr.trim().isEmpty()) {
                            showMessage(tempFirstStr);
                            editor.putString(User.LOCATION, tempFirstStr);
                            editor.apply();
                            nCurrentPage++;
                            viewPager.setCurrentItem(nCurrentPage, true);
                        } else
                            showMessage("please type your location");
                        break;
                    case 1:
                        if (!valueFromSpinner.isEmpty()) {
                            showMessage(valueFromSpinner);
                            editor.putString(User.SPECIALITY, valueFromSpinner);
                            editor.apply();
                            nCurrentPage++;
                            viewPager.setCurrentItem(nCurrentPage, true);
                        } else
                            showMessage("please choose your speciality");
                        break;
                    case 2:
                        if (!tempFirstStr.isEmpty()) {
                            showMessage(tempFirstStr);
                            editor.putString(User.BIO, tempFirstStr);
                            editor.apply();
                            nCurrentPage++;
                            viewPager.setCurrentItem(nCurrentPage, true);
                        } else
                            showMessage("please type your bio");
                        break;
                    case 3:
                        if (!tempFirstStr.trim().isEmpty() && !tempSecondStr.trim().isEmpty()) {
                            editor.putString(User.G_FACULTY, tempFirstStr);
                            editor.putString(User.G_YEAR, tempSecondStr);
                            editor.apply();
                            showMessage(tempFirstStr.trim() + ",    " + tempSecondStr.trim());
                            //TODO intent
                            //startActivity(new Intent(DoctorRegistrationActivity.this, SignInActivity.class));
                        } else
                            showMessage("please type your faculty and graduation year");
                        break;
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick (View v){
                    //if user in first page and clicked back send him to the previous activity
                    //else take him to previous page
                    if(nCurrentPage == 0){
                        intent = new Intent(DoctorRegistrationActivity.this, UserTypeActivity.class);
//                      startActivity(intent);
                    }else{
                        nCurrentPage--;
                        viewPager.setCurrentItem(nCurrentPage,true);
                        Log.e("currentPage", String.valueOf(nCurrentPage));
                    }
                }
        });
        viewPager.setOnTouchListener( touchListener);
    }

    private void showMessage(String message) {
        Toast.makeText(DoctorRegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
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

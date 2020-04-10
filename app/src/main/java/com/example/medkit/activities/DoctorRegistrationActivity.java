package com.example.medkit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.model.CustomViewPager;
import com.example.medkit.model.User;
import com.example.medkit.utils.SliderAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.reginald.editspinner.EditSpinner;

import java.util.Arrays;

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
    //private String apiKey = "AIzaSyCdJi2I6U7tS8dBf9mv_AfvgogOmOcWLeU";
    private String apiKey = "AIzaSyAhmfZ3nferjgL8nNI7CJJSp-4NXO6-zII";


    EditText firstEdText;
    EditText secondEdText;
    private PlacesClient placesClient;
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
    private Fragment fragment;
    private Button backBtn;
    private int nCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,android.R.anim.fade_out);
        setContentView(R.layout.activity_doctor_registration);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);


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
        final SliderAdapter sliderAdapter = new SliderAdapter(getApplicationContext(), this, this.getSupportFragmentManager());
        viewPager.setAdapter(sliderAdapter);
        addDots(0);
        viewPager.addOnPageChangeListener(viewlistener);
        nextBtn = findViewById(R.id.slider_next_btn);
        backBtn = findViewById(R.id.slider_skip_btn);
        viewPager.setOffscreenPageLimit(3);
        nCurrentPage = viewPager.getCurrentItem();

//
//        fragment = getSupportFragmentManager().findFragmentById(R.id.location_fragment2);
//        getSupportFragmentManager().beginTransaction().hide(fragment);
//        if(nCurrentPage == 0) {
//            getSupportFragmentManager().beginTransaction().show(fragment);
//            Toast.makeText(getApplicationContext(), "ttt", Toast.LENGTH_SHORT).show();
//            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                    getSupportFragmentManager().findFragmentById(R.id.location_fragment2);
//
//            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
//
//            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                @Override
//                public void onPlaceSelected(Place place) {
//                    final LatLng latLng = place.getLatLng();                            // TODO: Get info about the selected place.
//                    Toast.makeText(getApplicationContext(), "" + latLng.latitude, Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onError(Status status) {
//                    // TODO: Handle the error.
//                    Toast.makeText(getApplicationContext(), "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }else
//            getSupportFragmentManager().beginTransaction().hide(fragment);




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

                if (nCurrentPage == 0) {


//                    showMessage(valueFromSpinner);
//                    editor.putString(User.SPECIALITY, valueFromSpinner);
//                    editor.apply();
//                    nCurrentPage++;
//                    viewPager.setCurrentItem(nCurrentPage,true);
                } else if (nCurrentPage > 0 && nCurrentPage < 3) {
                    showMessage(tempFirstStr);
                    if (!tempFirstStr.trim().isEmpty()) {
                        switch (nCurrentPage) {
                            case 1:
                                editor.putString("BIO_KEY", tempFirstStr);
                                editor.apply();
                                break;

                            case 2:
                                editor.putString(User.LOCATION, tempFirstStr);
                                editor.apply();
                                break;
                        }
                        nCurrentPage++;
                        viewPager.setCurrentItem(nCurrentPage,true);
                    } else
                        showMessage("please fill all input fields");

                } else if (nCurrentPage == 3) {
                    if (!tempFirstStr.trim().isEmpty() && !tempSecondStr.trim().isEmpty()) {
                        editor.putString(User.G_FACULTY, tempFirstStr);
                        editor.putString(User.G_YEAR, tempSecondStr);
                        editor.apply();
                        showMessage(tempFirstStr.trim() + ",    " + tempSecondStr.trim());
                    } else
                        showMessage("please fill all input fields");
                } else
                    startActivity(new Intent(DoctorRegistrationActivity.this, SignInActivity.class));
                nCurrentPage++;
                viewPager.setCurrentItem(nCurrentPage, true);
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

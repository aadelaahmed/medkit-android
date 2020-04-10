package com.example.medkit.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import com.example.medkit.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.reginald.editspinner.EditSpinner;

import java.util.Arrays;

public class SliderAdapter extends PagerAdapter{
    private Context context;
    private FragmentManager fragmentManager;
    private Context appContext;
    private int[] firstArrText = {R.string.location, R.string.specialty, R.string.bio, R.string.graduation};
    private int[] secondArrText = {R.string.enter_speciality, R.string.enter_location, R.string.enter_bio, R.string.enter_graduation};
    private int[] hintArrText = {R.string.hint_location, R.string.hint_speciality, R.string.hint_bio, R.string.hint_graduation_faculty, R.string.hint_graduation_year};
    private EditText firstEditText;
    private EditText secondEditText;
    private EditSpinner spinner;
    //private String apiKey = "AIzaSyCdJi2I6U7tS8dBf9mv_AfvgogOmOcWLeU";
    private String apiKey = "AIzaSyDJm7xflEeJCaHSLMXtDYvlqoCnF2E39f0";
    private PlacesClient placesClient;
    private Fragment fragment;

    public SliderAdapter(Context appContext, Context context, FragmentManager fragmentManager)
    {
        this.appContext = appContext;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        return firstArrText.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
        if (!Places.isInitialized()) {
            Places.initialize(appContext, apiKey);
        }
        placesClient = Places.createClient(context);



        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.doctor_details_layout,container,false);
        TextView firstTextView = view.findViewById(R.id.first_text_view);
        TextView secondTextView = view.findViewById(R.id.second_text_view);
        firstEditText = view.findViewById(R.id.first_edit_text);
        secondEditText = view.findViewById(R.id.second_edit_text);
        firstTextView.setText(view.getResources().getString(firstArrText[position]));
        secondTextView.setText(view.getResources().getString(secondArrText[position]));
        firstEditText.setHint(view.getResources().getString(hintArrText[position]));
        secondEditText.setVisibility(View.INVISIBLE);
        spinner = view.findViewById(R.id.speciality_list);
        ListAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
                context.getResources().getStringArray(R.array.speciality_spinner));
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.INVISIBLE);
        spinner.setHint(view.getResources().getString(hintArrText[position]));
        fragment = fragmentManager.findFragmentById(R.id.location_fragment);
        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) fragmentManager.findFragmentById(R.id.location_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteSupportFragment.setOnPlaceSelectedListener(
                new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        final LatLng latLng = place.getLatLng();

                        Toast.makeText(context, "" + place.getName(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(Status status) {
                        Toast.makeText(context, "" + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        switch (position) {
            case 0:
                firstEditText.setVisibility(View.INVISIBLE);
                break;
            case 1:
                spinner.setVisibility(View.VISIBLE);
                firstEditText.setVisibility(View.INVISIBLE);
//                fragment.getView().setVisibility(View.INVISIBLE);
                break;
            case 2:
//                fragment.getView().setVisibility(View.INVISIBLE);
                break;
            case 3:
//                fragment.getView().setVisibility(View.INVISIBLE);
                secondEditText.setHint(hintArrText[position - 3]);
                secondEditText.setVisibility(View.VISIBLE);
                break;
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}


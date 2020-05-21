package com.example.medkit.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.example.medkit.R;
import com.reginald.editspinner.EditSpinner;

public class SliderAdapter extends PagerAdapter{
    private Context context;
    private int[] firstArrText = {R.string.location, R.string.specialty, R.string.bio, R.string.graduation};
    private int[] secondArrText = {R.string.enter_speciality, R.string.enter_location, R.string.enter_bio, R.string.enter_graduation};
    private int[] hintArrText = {R.string.hint_location, R.string.hint_speciality, R.string.hint_bio, R.string.hint_graduation_faculty, R.string.hint_graduation_year};
    private EditText firstEditText;
    private EditText secondEditText;
    private EditSpinner spinner;

    public SliderAdapter(Context context)
    {
        this.context = context;
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

        switch (position) {
            case 0:
                break;
            case 1:
                spinner.setVisibility(View.VISIBLE);
                firstEditText.setVisibility(View.INVISIBLE);
                break;
            case 2:
                break;
            case 3:
                secondEditText.setHint("Graduation Year");
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


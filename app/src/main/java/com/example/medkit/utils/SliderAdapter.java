package com.example.medkit.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.example.medkit.R;

public class SliderAdapter extends PagerAdapter{
    private Context context;
    private int[] firstArrText = {R.string.specialty,R.string.bio,R.string.location,R.string.graduation};
    private int[] secondArrText = {R.string.enter_speciality,R.string.enter_bio,R.string.enter_location,R.string.enter_graduation};
    private int[] hintArrText = {R.string.hint_speciality,R.string.hint_bio,R.string.hint_location,R.string.hint_graduation_faculty,R.string.hint_graduation_year};
    public static EditText firstEditText;
    public static EditText secondEditText;
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
        //return super.instantiateItem(container, position);
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
        if(position == 3)
        {
            secondEditText.setHint(hintArrText[position]);
            secondEditText.setVisibility(View.VISIBLE);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}


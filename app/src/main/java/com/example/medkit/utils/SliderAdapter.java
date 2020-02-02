package com.example.medkit.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.medkit.R;

public class SliderAdapter extends PagerAdapter {
    private Context context;

    public SliderAdapter(Context context) {
        this.context = context;
    }
    private int[] images = {
            R.drawable.communicate,
            R.drawable.multi,
            R.drawable.notification,
            R.drawable.donate,
            R.drawable.start

    };

    private String[] heads = {
            "Share & Communicate!",
            "Allow Multitasking for you!",
            "Allow Notification!",
            "Donation Service!",
            "It's time to START NOW!"
    };

    private String[] description = {
            "Find your doctor, get better communication and\n" +
                    "create impactful change to better health",
            "Make your profile, set up your information\n" +
                    "and chatting with doctors, make\n" +
                    "your post and live call.",
            "You can receive notifications from doctors\n" +
                    "to respond to your problem",
            "We provide donation for all people that\n" +
                    "look forward better health.",
            "Join to our community and enjoy with\n" +
                    "MEDKIT services and a lot!"
    };

    private String[] buttons = {
            "LEARN MORE!!",
            "JOIN NOW!"
    };

    @Override
    public int getCount() {
        return heads.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_show_layout,container,false);

        ImageView imageView = view.findViewById(R.id.slide_img);
        TextView headtv = view.findViewById(R.id.slide_head);
        TextView desctv = view.findViewById(R.id.slide_desc);

        imageView.setImageResource(images[position]);
        headtv.setText(heads[position]);
        desctv.setText(description[position]);
        if (position >=3){
            Button btn = view.findViewById(R.id.slide_btn);
            btn.setVisibility(View.VISIBLE);
            btn.setText(buttons[position-3]);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}

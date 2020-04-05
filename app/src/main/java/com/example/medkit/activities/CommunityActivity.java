package com.example.medkit.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivityCommunityBinding;
import com.example.medkit.fragments.HomeFragment;
import com.example.medkit.fragments.MessageFragment;
import com.example.medkit.fragments.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CommunityActivity extends AppCompatActivity {

    private ActivityCommunityBinding binding;
    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFreagment = null;
                    switch (item.getItemId()) {
                        case R.id.home_item:
                            selectedFreagment = new HomeFragment();
                            break;
                        case R.id.notify_item:
                            selectedFreagment = new NotificationFragment();
                            break;
                        case R.id.message_item:
                            selectedFreagment = new MessageFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFreagment).commit();
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityCommunityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        //TODO --> add badge to notification and backlight on item click
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }
}

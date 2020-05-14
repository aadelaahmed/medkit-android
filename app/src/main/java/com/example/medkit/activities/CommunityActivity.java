package com.example.medkit.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivityCommunityBinding;
import com.example.medkit.fragments.HomeFragment;
import com.example.medkit.fragments.NotificationFragment;
import com.example.medkit.utils.GlideApp;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class CommunityActivity extends AppCompatActivity {

    private ActivityCommunityBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Uri userPhoto;
    FirebaseStorage storageInstance;
    StorageReference storageRef;
    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFreagment = null;
                    switch (item.getItemId()) {
                        case R.id.home_item:
                            selectedFreagment = new HomeFragment(CommunityActivity.this);
                            break;
                        case R.id.notify_item:
                            selectedFreagment = new NotificationFragment();
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
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        storageInstance = FirebaseStorage.getInstance();
        storageRef = storageInstance.getReference("userPhoto/" + currentUser.getUid());
        iniActionBar();
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        binding.viewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CommunityActivity.this, ProfileActivity.class));
            }
        });
        //TODO --> add badge to notification and backlight on item click
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(this)).commit();
    }


    private void iniActionBar() {
        if (currentUser != null) {
            //userPhoto = currentUser.getPhotoUrl();
            //Log.d("TAG", "iniActionBar: " + userPhoto.toString());
            // binding.imgUserCommunity.setImageURI(userPhoto);
            GlideApp.with(this).load(storageRef).into(binding.imgUserCommunity);
            String[] tempArr = currentUser.getEmail().split("@");
            String email = tempArr[0];
            binding.txtEmailCommunity.setText(email);
            String userName = currentUser.getDisplayName();
            binding.txtNameCommunity.setText(userName);
        } else {
            //currentUser.reload();
            currentUser = mAuth.getCurrentUser();
            iniActionBar();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlideApp.with(this).load(storageRef).into(binding.imgUserCommunity);
    }
}

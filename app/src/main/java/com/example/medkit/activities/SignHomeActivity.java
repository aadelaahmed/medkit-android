package com.example.medkit.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivitySignHomeBinding;

public class SignHomeActivity extends AppCompatActivity {
    private ActivitySignHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.emailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignHomeActivity.this,UserTypeActivity.class));
            }
        });
        binding.facebookSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignHomeActivity.this,UserTypeActivity.class));
            }
        });
        binding.googleSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignHomeActivity.this,UserTypeActivity.class));
            }
        });

        binding.signinTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignHomeActivity.this,SignInActivity.class));
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
        super.onBackPressed();
    }
}

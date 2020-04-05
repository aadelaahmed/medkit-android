package com.example.medkit.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivityUserTypeBinding;
import com.example.medkit.model.User;

public class UserTypeActivity extends AppCompatActivity {
    private ActivityUserTypeBinding binding;
    SharedPreferences sharedPreferences;
    String emailUser = null;

    // public static final String ISDOCTOR_KEY = "ISDOCTOR";
    //public static final String SHARED_PREFERENCE_NAME = "USER_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,android.R.anim.fade_out);
        binding = ActivityUserTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        ActionBar actionBar= getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        emailUser = sharedPreferences.getString(User.EMAIL, null);
        binding.doctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(User.IS_DOCTOR, true);
                editor.commit();
                if (emailUser != null)
                    startActivity(new Intent(UserTypeActivity.this, DoctorRegistrationActivity.class));
                else
                    startActivity(new Intent(UserTypeActivity.this, SignUpActivity.class));
            }
        });
        binding.userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(User.IS_DOCTOR, false);
                editor.commit();
                if (emailUser != null)
                    startActivity(new Intent(UserTypeActivity.this, GetStartedActivity.class));
                else
                    startActivity(new Intent(UserTypeActivity.this, SignUpActivity.class));
            }
        });
    }

 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(0, android.R.anim.fade_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
        super.onBackPressed();
    }*/
}

package com.example.medkit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.medkit.databinding.ActivityUserTypeBinding;
import com.example.medkit.model.User;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class UserTypeActivity extends AppCompatActivity {
    private ActivityUserTypeBinding binding;
    SharedPreferences sharedPreferences;
    String normalReg = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,android.R.anim.fade_out);
        binding = ActivityUserTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(SignHomeActivity.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        ActionBar actionBar= getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        normalReg = sharedPreferences.getString(User.NORMAL_REGISTER, null);
        Log.d("TAG", "onCreate: " + normalReg);
        binding.doctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean(User.IS_DOCTOR, true);
                editor.commit();
                if (normalReg.equals("custom register"))
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
                if (normalReg.equals("custom register"))
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

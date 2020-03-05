package com.example.medkit.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ActivitySignUpBinding binding;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,android.R.anim.fade_out);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar= getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        sharedPreferences = this.getSharedPreferences("sign", Context.MODE_PRIVATE);
        boolean isDoctor = sharedPreferences.getBoolean("isDoctor",false);

        binding.maleRadio.setOnCheckedChangeListener(this);
        binding.femaleRadio.setOnCheckedChangeListener(this);

        if(isDoctor){
            binding.continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SignUpActivity.this,DoctorRegistrationActivity.class));
                }
            });
        }else{
            binding.continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startActivity(new Intent(SignUpActivity.this,DoctorRegistrationActivity.class));
                    Toast.makeText(SignUpActivity.this, "go to home", Toast.LENGTH_SHORT).show();
                    //TODO
                }
            });
        }

    }

    @Override
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
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            if(buttonView.getId() == R.id.male_radio)
                binding.femaleRadio.setChecked(false);
            else if(buttonView.getId() == R.id.female_radio)
                binding.maleRadio.setChecked(false);
        }
    }
}

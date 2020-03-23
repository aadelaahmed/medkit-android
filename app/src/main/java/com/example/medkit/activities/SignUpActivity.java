package com.example.medkit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-zA-Z])" +
                    "(?=\\S+$)" +
                    ".{4,}$");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[a-zA-Z])" +
                    ".{4,}$");
    private ActivitySignUpBinding binding;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    String name, email, password, age;
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

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.continueBtn.setVisibility(View.INVISIBLE);
                binding.progressSignUp.setVisibility(View.VISIBLE);
                email = binding.emailEd.getEditText().getText().toString().trim();
                password = binding.passwordEd.getEditText().getText().toString().trim();
                name = binding.nameEd.getEditText().getText().toString().trim();
                age = binding.ageEd.getEditText().getText().toString().trim();
                signUp(email, password, name, age);
            }
        });

    }

   /* private void updateUI()
    {

    }*/

    private void signUp(String email, String password, String name, String age) {

        //firebaseAuth = FirebaseAuth.getInstance();
        if (validateName() && validateEmail() && validatePassword() && validateAge()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    binding.progressSignUp.setVisibility(View.INVISIBLE);
                    binding.continueBtn.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()) {
                        sendEmailVerification();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException weakPassword) {
                            binding.passwordEd.setError("weak password");
                        } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                            binding.emailEd.setError("malformed email");
                        } catch (FirebaseAuthUserCollisionException existedEmail) {
                            binding.emailEd.setError("existed Email");
                        } catch (Exception e) {
                            showMessage(e.getMessage());
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    binding.progressSignUp.setVisibility(View.INVISIBLE);
                    binding.continueBtn.setVisibility(View.VISIBLE);
                    showMessage(e.getMessage());
                }
            });
        } else {
            binding.progressSignUp.setVisibility(View.INVISIBLE);
            binding.continueBtn.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Invalid", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validatePassword() {

        if (password.isEmpty()) {
            binding.passwordEd.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            binding.passwordEd.setError("*must contain at least one number and one letter, and at least 8 or more characters");
            return false;
        } else {
            binding.passwordEd.setError(null);
            binding.passwordEd.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateName() {

        if (name.isEmpty()) {
            binding.nameEd.setError("*Required");
            return false;
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            binding.nameEd.setError("invalid name");
            return false;
        } else {
            binding.nameEd.setError(null);
            binding.nameEd.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAge() {

        if (age.isEmpty()) {
            binding.ageEd.setError("*Required");
            return false;
        } else {
            binding.ageEd.setError(null);
            binding.ageEd.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        if (email.isEmpty()) {
            binding.emailEd.setError("*Required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEd.setError("*Incorrect email");
            return false;
        } else {
            binding.emailEd.setError(null);
            binding.emailEd.setErrorEnabled(false);
            return true;
        }
    }
    private void sendEmailVerification() {
        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showMessage("please verify your account");
                if (task.isSuccessful()) {
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    startActivity(intent);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
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

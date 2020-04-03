package com.example.medkit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivitySignUpBinding;
import com.example.medkit.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ActivitySignUpBinding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String USER_KEY = "USER_KEY";
    public FirebaseAuth mAuth;
    boolean isDoctor = false;
    String email;
    public FirebaseUser currentUser;
    //public static FirebaseUser currentUser;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    public static String timestampToString(long time) {

        SimpleDateFormat sfd = new SimpleDateFormat("ddd, dd MMM yyyy HH':'mm':'ss 'GMT'");
        String date = sfd.format(new Date(time));
        /*
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("ddd, dd MMM yyyy HH':'mm':'ss 'GMT'",calendar).toString();*/
        return date;

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(0, android.R.anim.fade_out);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences(SignHomeActivity.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isDoctor = sharedPreferences.getBoolean(User.IS_DOCTOR, false);
        mAuth = FirebaseAuth.getInstance();
        /* listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
            }
        };
        mAuth.addAuthStateListener(listener);*/

       /* mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null)
                {
                    currentUser = firebaseAuth.getCurrentUser();
                    showMessage("user correct");
                }
                else
                {
                    showMessage("user failed");
                }
            }
        };*/

        binding.maleRadio.setOnCheckedChangeListener(this);
        binding.femaleRadio.setOnCheckedChangeListener(this);
        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.continueBtn.setVisibility(View.INVISIBLE);
                    binding.progressSignUp.setVisibility(View.VISIBLE);
                    email = binding.emailEd.getText().toString();
                    String password = binding.passwordEd.getText().toString();
                    String age = binding.ageEd.getText().toString();
                    RadioButton mRadio = binding.maleRadio;
                    RadioButton fRadio = binding.femaleRadio;
                    signUp(email, password, age, mRadio, fRadio);
                }
        });
    }

    private void signUp(String email, String password, String age, RadioButton mRadio, RadioButton fRadio) {
        //firebaseAuth = FirebaseAuth.getInstance();
        if (!email.isEmpty() && !password.isEmpty() && (mRadio.isChecked() || fRadio.isChecked()) && !age.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    binding.progressSignUp.setVisibility(View.INVISIBLE);
                    binding.continueBtn.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()) {
                        currentUser = mAuth.getCurrentUser();
                        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(binding.fnameEd.getText().toString())
                                .build();
                        currentUser.updateProfile(profleUpdate);
                        sendEmailVerification();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException weakPassword) {
                            showMessage("weak password");
                        } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                            showMessage("malformed exception");
                        } catch (FirebaseAuthUserCollisionException existedEmail) {
                            showMessage("existed Email");
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
            showMessage("please enter your email and password");
        }
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

    private void sendEmailVerification() {

        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    setSharedData();
                    showMessage("Verification email sent to: " + currentUser.getEmail());
                    //startActivity(new Intent(SignUpActivity.this, DoctorRegistrationActivity.class));

                    if (!isDoctor) {
                        Intent intent = new Intent(SignUpActivity.this, GetStartedActivity.class);
                        intent.putExtra(USER_KEY, currentUser);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SignUpActivity.this, DoctorRegistrationActivity.class);
                        intent.putExtra(USER_KEY, currentUser);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
            }
        });
    }

    private void setSharedData() {
       /* String email = binding.emailEd.getText().toString();
        String fullName = binding.fnameEd.getText().toString();
        int age = Integer.parseInt(binding.ageEd.getText().toString());
        String gender = "";
        if (binding.maleRadio.isChecked())
            gender = "Male";
        else
            gender = "Female";
        editor.putString(User.EMAIL,email);
        editor.putString(User.FULLNAME,fullName);
        editor.putInt(User.AGE, age);
        editor.putString(User.GENDER, gender);*/
        String createdTime = timestampToString(currentUser.getMetadata().getCreationTimestamp());
        editor.putString(User.CREATED_TIME, createdTime);
        editor.commit();
    }
}

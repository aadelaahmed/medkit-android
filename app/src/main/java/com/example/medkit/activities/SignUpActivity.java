package com.example.medkit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivitySignUpBinding;
import com.example.medkit.model.User;
import com.example.medkit.utils.LoadingAlertDialog;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ActivitySignUpBinding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    LoadingAlertDialog tempDialog;
    StorageReference rootRef = FirebaseStorage.getInstance().getReference().child("users");
    boolean isDoctor = false;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");
    String email, name, password, age;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, android.R.anim.fade_out);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        tempDialog = new LoadingAlertDialog(this);
        sharedPreferences = this.getSharedPreferences(SignHomeActivity.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isDoctor = sharedPreferences.getBoolean(User.IS_DOCTOR, true);
        firebaseAuth = FirebaseAuth.getInstance();
        //currentUser = firebaseAuth.getCurrentUser();
        binding.maleRadio.setOnCheckedChangeListener(this);
        binding.femaleRadio.setOnCheckedChangeListener(this);
        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.continueBtn.setVisibility(View.INVISIBLE);
                    binding.progressSignUp.setVisibility(View.VISIBLE);
                    tempDialog.startAlertDialog();
                    name = binding.nameEd.getEditText().getText().toString().trim();
                    email = binding.emailEd.getEditText().getText().toString().trim();
                    password = binding.passwordEd.getEditText().getText().toString();
                    age = binding.ageEd.getEditText().getText().toString().trim();
                    RadioButton mRadio = binding.maleRadio;
                    RadioButton fRadio = binding.femaleRadio;
                    signUp(name, email, password, age, mRadio, fRadio);
                }
        });
    }

    private void signUp(final String name, String email, String password, String age, RadioButton mRadio, RadioButton fRadio) {
        //firebaseAuth = FirebaseAuth.getInstance();
        if (validatename() && validateEmail() && validatePassword() && validateAge() && (mRadio.isChecked() || fRadio.isChecked())) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    binding.progressSignUp.setVisibility(View.INVISIBLE);
                    binding.continueBtn.setVisibility(View.VISIBLE);
                    tempDialog.dismissAlertDialog();
                    if (task.isSuccessful()) {
                        currentUser = firebaseAuth.getCurrentUser();
                        updateUserProfile();
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
                    tempDialog.dismissAlertDialog();
                    showMessage(e.getMessage());
                }
            });
        } else {
            binding.progressSignUp.setVisibility(View.INVISIBLE);
            binding.continueBtn.setVisibility(View.VISIBLE);
            tempDialog.dismissAlertDialog();
            showMessage("please check the required fields");
        }
    }

    private void updateUserProfile() {
        //Uri uri = Uri.parse("android.resource://"+this.getPackageName()+"/drawable/man.jpg");
        Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.userphoto);
        //Uri uri=Uri.parse("R.drawable.man.jpg");
        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(uri)
                .build();
        currentUser.updateProfile(profleUpdate).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Something wrong with update user profile");
            }
        });

       /*rootRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .setPhotoUri(uri)
                        .build();
                currentUser.updateProfile(profleUpdate);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Something wrong with update user profile");
            }
        });*/
    }

    private boolean validatePassword() {

        if (password.isEmpty()) {
            binding.passwordEd.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            binding.passwordEd.setError("A minimum 8 characters password contains atleast a letter and number are required.");
            return false;
        } else {
            binding.passwordEd.setError(null);
            return true;
        }
    }

    private boolean validatename() {

        if (name.isEmpty()) {
            binding.nameEd.setError("*Required");
            return false;
        } else {
            binding.nameEd.setError(null);
            return true;
        }
    }

    private boolean validateAge() {

        if (age.isEmpty()) {
            binding.ageEd.setError("*Required");
            return false;
        } else {
            binding.ageEd.setError(null);
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
            binding.emailEd.setErrorEnabled(false);
            return true;
        }
    }
    private void sendEmailVerification() {
        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setSharedData();
                    showMessage("Verification email sent to: " + currentUser.getEmail());
                    if (isDoctor)
                        startActivity(new Intent(SignUpActivity.this, DoctorRegistrationActivity.class));
                    else
                        startActivity(new Intent(SignUpActivity.this, GetStartedActivity.class));
                   /* if(!isDoctor)
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                    else
                        startActivity(new Intent(SignUpActivity.this, DoctorRegistrationActivity.class));*/
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

    private void setSharedData() {

        /*String gender = "";
        if (binding.maleRadio.isChecked())
            gender = "Male";
        else
            gender = "Female";*/
       /* Map<String ,String> generalInfo = new HashMap<>();
        generalInfo.put(User.AGE,);
        generalInfo.put(User.GENDER,gender);*/
        //editor.putString(User.AGE, binding.ageEd.getEditText().getText().toString());
        //editor.putString(User.GENDER, gender);
        if (!isDoctor) {
            /*Map<String, String> userType = new HashMap<>();
            userType.put(User.G_FACULTY, "empty");
            userType.put(User.G_YEAR, "empty");
            userType.put(User.SPECIALITY, "empty");
            userType.put(User.USERTYPE, "empty");*/
            editor.putString(User.G_FACULTY, "empty");
            editor.putString(User.G_YEAR, "empty");
            editor.putString(User.SPECIALITY, "empty");
            editor.putString(User.USERTYPE, "Paient");
        } else
            editor.putString(User.USERTYPE, "Doctor");
        editor.apply();

        /*String userId = currentUser.getUid();
        String lastSignIn = timestampToString(currentUser.getMetadata().getLastSignInTimestamp());
        usersCollection.add(new User(FieldValue.serverTimestamp().toString(),email,generalInfo,lastSignIn,userId,userType));*/
    }


}

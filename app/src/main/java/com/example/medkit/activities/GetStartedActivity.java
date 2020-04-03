package com.example.medkit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivityCommunityBinding;
import com.example.medkit.databinding.ActivityGetStartedBinding;
import com.example.medkit.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GetStartedActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CollectionReference userCollection;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseUser currentUser;
    String emailUser = null;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private ActivityGetStartedBinding binding;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGetStartedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        //mAuth.addAuthStateListener(mAuthStateListener);
        //final FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
            }
        };
        //while(!currentUser.isEmailVerified()){ showMessage("please verify your account");mAuth.getCurrentUser().reload(); }
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("Users");
        sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        emailUser = sharedPreferences.getString(User.EMAIL, null);
        binding.btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = getIntent();
                //FirebaseUser user =  mAuth.getCurrentUser();
                //FirebaseUser currentUser = (FirebaseUser) intent.getExtras().get(SignUpActivity.USER_KEY);
                if (emailUser == null) {
                    if (currentUser.isEmailVerified())
                        uploadIntoFireStore();
                    else {
                        currentUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showMessage("please verify your account");
                            }
                        });
                    }
                } else
                    uploadIntoFireStore();

            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void uploadIntoFireStore() {
      /*  int age = sharedPreferences.getInt(User.AGE,222);
        String createdTime = sharedPreferences.getString(User.CREATED_TIME,"null");
        String email = sharedPreferences.getString(User.EMAIL,"null");
        String fullName = sharedPreferences.getString(User.FULLNAME,"null");
        String gender = sharedPreferences.getString(User.GENDER,"null");
        String userPhoto = sharedPreferences.getString(User.IMGURL,"null");
        String userId = sharedPreferences.getString(User.USER_ID,"null");
        String gFaculty = sharedPreferences.getString(User.G_FACULTY,"null");
        String gYear = sharedPreferences.getString(User.G_YEAR,"null");
        String speciality = sharedPreferences.getString(User.SPECIALITY,"null");
        String userType = sharedPreferences.getString(User.USERTYPE,"null");

        User newUser = new User(age,createdTime,email,fullName,gender,userPhoto,userId,gFaculty,gYear,speciality,userType);*/
        boolean isDoctor = sharedPreferences.getBoolean(User.IS_DOCTOR, false);
        String createdTime = sharedPreferences.getString(User.CREATED_TIME, "null");
        String userType = "";
        User newUser;
        Map<String, Object> mapType = new HashMap<>();
        if (isDoctor) {
            userType = "Doctor";
            String gFaculty = sharedPreferences.getString(User.G_FACULTY, null);
            String gYear = sharedPreferences.getString(User.G_YEAR, null);
            String speciality = sharedPreferences.getString(User.SPECIALITY, null);
            String location = sharedPreferences.getString(User.LOCATION, null);
            mapType.put(User.G_FACULTY, gFaculty);
            mapType.put(User.G_YEAR, gYear);
            mapType.put(User.SPECIALITY, speciality);
            mapType.put(User.LOCATION, location);
            mapType.put(User.USERTYPE, userType);
            newUser = new User(createdTime, mapType);
        } else {
            userType = "Patient";
            mapType.put(User.USERTYPE, userType);
            newUser = new User(createdTime, mapType);
        }
        //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        editor.clear();
        editor.commit();
        userCollection.document(userId).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(GetStartedActivity.this, CommunityActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("something wrong with upload data into firestore");
            }
        });
    }


}

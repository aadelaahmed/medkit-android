package com.example.medkit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    //TextView textViewLogin, textViewTouch;
    //Button buttonLogin;
    FirebaseAuth firebaseAuth = null;
    FirebaseUser currentUser = null;
    private ActivitySignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,android.R.anim.fade_out);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar= getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        /* textViewTouch = findViewById(R.id.text_view_touch);
        textViewLogin = findViewById(R.id.text_view_login);
        buttonLogin = findViewById(R.id.button_login);
        setFontType(); */

        firebaseAuth = FirebaseAuth.getInstance();
        //currentUser = firebaseAuth.getCurrentUser();
        binding.btnLognIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressSignIn.setVisibility(View.VISIBLE);
                binding.btnLognIn.setVisibility(View.INVISIBLE);
                String email = binding.etEmailSignIn.getText().toString();
                String password = binding.etPasswordSignIn.getText().toString();
                logIn(email, password);
            }
        });

    }

    private void updateUI() {
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseUser user = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI();
        }
    }

    private void logIn(String email, String password) {
        if (!email.isEmpty() || !password.isEmpty()) {


            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        binding.btnLognIn.setVisibility(View.VISIBLE);
                        binding.progressSignIn.setVisibility(View.INVISIBLE);
                        verifyEmailAddress();

                    } else {
                        try {
                            throw task.getException();
                        }
                        // if user enters wrong email.
                        catch (FirebaseAuthInvalidUserException invalidEmail) {
                            showMessage(invalidEmail.getMessage());
                        }
                        // if user enters wrong password.
                        catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                            showMessage(wrongPassword.getMessage());
                        } catch (Exception e) {
                            showMessage(e.getMessage());
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    showMessage(e.getMessage());
                }
            });
        } else {
            binding.btnLognIn.setVisibility(View.VISIBLE);
            binding.progressSignIn.setVisibility(View.INVISIBLE);
            showMessage("please enter your email and password");
        }
    }

    private void showMessage(String message) {
        Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void verifyEmailAddress() {
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser.isEmailVerified()) {
            showMessage("login successfully");
            updateUI();
        } else {

            showMessage("please verify your account");
        }
    }

   /* void setFontType() {
        Typeface robotoMediumType = ResourcesCompat.getFont(this, R.font.roboto_medium);
        textViewTouch.setTypeface(robotoMediumType);


        Typeface robotoBoldType = ResourcesCompat.getFont(this, R.font.roboto_bold2);
        textViewLogin.setTypeface(robotoBoldType);


        buttonLogin.setTypeface(robotoBoldType); */
    //}

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
}

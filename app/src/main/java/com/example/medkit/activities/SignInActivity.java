package com.example.medkit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivitySignInBinding;
import com.example.medkit.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {
    //TextView textViewLogin, textViewTouch;
    //Button buttonLogin;
    FirebaseAuth firebaseAuth = null;
    //FirebaseUser currentUser ;
    public static final int RC_SIGN_IN = 8;
    CallbackManager mCallbackManager;
    GoogleSignInClient mGoogleSignInClient;
    private ActivitySignInBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection;
    SharedPreferences userSharedPref;
    boolean isFirstTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,android.R.anim.fade_out);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        usersCollection = db.collection("Users");
        setContentView(binding.getRoot());
        ActionBar actionBar= getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        /* textViewTouch = findViewById(R.id.text_view_touch);
        textViewLogin = findViewById(R.id.text_view_login);
        buttonLogin = findViewById(R.id.button_login);
        setFontType(); */
        firebaseAuth = FirebaseAuth.getInstance();
        userSharedPref = this.getSharedPreferences(SignHomeActivity.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        binding.iconGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestClientGoogle();
            }
        });

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
        mCallbackManager = CallbackManager.Factory.create();
        binding.iconFacebookSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        LoginManager.getInstance().logOut();
                        showMessage("login facebook only cancled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        showMessage(error.getMessage());
                    }
                });
            }
        });


    }

    public void requestClientGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //create googleSignInClient with the previous request(options) gso
        mGoogleSignInClient = GoogleSignIn.getClient(SignInActivity.this, gso);
        signInWithGoogle();
    }


    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            isFirstTime = task.getResult().getAdditionalUserInfo().isNewUser();
                            isEmailAlreadyExisted(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showMessage(task.getException().getMessage());
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void isEmailAlreadyExisted(FirebaseUser currentUser) {
        if (isFirstTime)
            updateUI(currentUser);
        else {
            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //TODO progress bar here
                    startActivity(new Intent(SignInActivity.this, SignHomeActivity.class));
                    finish();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage(e.getMessage());
                        }
                    });
        }
    }


    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(SignInActivity.this, CommunityActivity.class);
            startActivity(intent);
            finish();
        }
    }

 /*   @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            updateUI(user);
        }
    }*/

    private void logIn(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {

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
            binding.etEmailSignIn.setError("Email iccorrect");
        }
    }

    private void showMessage(String message) {
        Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void verifyEmailAddress() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser.isEmailVerified()) {
            showMessage("login successfully");
            updateUI(currentUser);
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

    //performed when we click on sign in button
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                showMessage("Google sign in failed");
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            isEmailAlreadyExisted(user);
                            //create method to get user profile information
                            //getUserInfo();

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            showMessage("signInWithCredential:failure");
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


}

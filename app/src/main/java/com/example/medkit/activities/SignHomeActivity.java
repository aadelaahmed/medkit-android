package com.example.medkit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medkit.databinding.ActivitySignHomeBinding;
import com.example.medkit.model.User;
import com.example.medkit.utils.ExternalAuthProvider;
import com.example.medkit.utils.LoadingAlertDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class SignHomeActivity extends AppCompatActivity {
    public static final String SHARED_PREFERENCE_NAME = "USER_DATA";
    private ActivitySignHomeBinding binding;
    public static final int RC_SIGN_IN = 200;
    //boolean isFirstTime;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth = null;
    FirebaseUser currentUser = null;
    SharedPreferences.Editor editor;
    /*String creationTime;
    CallbackManager mCallbackManager;
    GoogleSignInClient mGoogleSignInClient;*/
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection;
    LoadingAlertDialog tempDialog;
    //String userName = null, userImage = null, userEmail = null;
    private ExternalAuthProvider tempProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //mCallbackManager = CallbackManager.Factory.create();
        usersCollection = db.collection(User.USER_COLLECTION);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        tempDialog = new LoadingAlertDialog(this);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        tempProviders = new ExternalAuthProvider(this, firebaseAuth, currentUser);
        binding.emailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(User.NORMAL_REGISTER, "normal register");
                editor.commit();
                startActivity(new Intent(SignHomeActivity.this, UserTypeActivity.class));
                //TODO enable action back click
                finish();
            }
        });
        binding.facebookSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(User.NORMAL_REGISTER, "custom register");
                editor.commit();
                tempProviders.signInWithFaceBook();
                Log.d("TAG", "onClick:facebook error  : erorororororo");
                /*tempDialog.startAlertDialog();
                LoginManager.getInstance().logInWithReadPermissions(SignHomeActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        tempDialog.dismissAlertDialog();
                        LoginManager.getInstance().logOut();
                        showMessage("login facebook only cancled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        tempDialog.dismissAlertDialog();
                        showMessage(error.getMessage());
                    }
                });*/

                //startActivity(new Intent(SignHomeActivity.this,UserTypeActivity.class));
            }
        });
        binding.googleSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure Google Sign In
                editor.putString(User.NORMAL_REGISTER, "custom register");
                editor.commit();
                tempProviders.requestClientGoogle();
                //startActivity(new Intent(SignHomeActivity.this,UserTypeActivity.class));
            }
        });

        binding.signinTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempProviders.loadingDialog.startAlertDialog();
                startActivity(new Intent(SignHomeActivity.this, SignInActivity.class));

            }
        });
    }

    @Override
    public void onBackPressed() {
        tempProviders.loadingDialog.dismissAlertDialog();
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
        super.onBackPressed();
    }
    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
                startActivity(new Intent(SignHomeActivity.this, CommunityActivity.class));
        }
    }

   /* public void requestClientGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //create googleSignInClient with the previous request(options) gso
        mGoogleSignInClient = GoogleSignIn.getClient(SignHomeActivity.this, gso);
        fireIntentGoogle();
    }

    //performed when we click on sign in button
    private void fireIntentGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                tempProviders.firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                tempProviders.loadingDialog.dismissAlertDialog();
                showMessage("check your internet connection");
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        } else {
            // pass login result to login manager
            // here we can manage callbacks from facebook SDK
            tempProviders.mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

/*
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //currentUser = firebaseAuth.getCurrentUser();
                            //updateProfileUser(userName, Uri.parse(userImage));
                            isFirstTime = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (!isFirstTime) {
                                tempDialog.dismissAlertDialog();
                                showMessage("Email already existed");
                            }
                            userName = acct.getDisplayName();
                            userImage = acct.getPhotoUrl().toString();
                            Log.d("TAG", "onComplete gooogle: " + userImage);
                            userEmail = acct.getEmail();
                            isFirstTime = false;
                            Log.d("TAG", "signInWithCredential:success");
                            //create method to get user profile information
                            //getUserInfo();
                            updateUI();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            tempDialog.dismissAlertDialog();
                            showMessage("signInWithCredential:failure");
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }*/

    /*private void updateProfileUser(String tempName, Uri tempImage) {
        UserProfileChangeRequest tempUpdate =
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(tempName)
                        .setPhotoUri(tempImage)
                        .build();
        currentUser.updateProfile(tempUpdate);
    }*/


   /* private void handleFacebookAccessToken(final AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //currentUser = firebaseAuth.getCurrentUser();
                            //editor.putString(User.EMAIL, token.getUserId());
                            getFacebookData(token);
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            tempDialog.dismissAlertDialog();
                            showMessage(task.getException().toString());
                        }
                    }
                });
    }*/

    /*private void getFacebookData(final AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        userName = json.getString("name");
                        userEmail = json.getString("email");
                        //userImage = "https://graph.facebook.com/" + token.getUserId() + "/picture?type=large";
                        userImage = "https://graph.facebook.com/" + token.getUserId() + "/picture?height=500";
                        //updateProfileUser(userName, Uri.parse(userImage));
                        updateUI();
                        Log.d("TAG", "onCompletedFACEBBOOk: " + userName + userEmail + userImage);
                        // String text = "<b>Name :</b> "+json.getString("name")+"<br><br><b>Email :</b> "+json.getString("email")+"<br><br><b>Profile link :</b> "+json.getString("link");
                        // details_txt.setText(Html.fromHtml(text));
                        //profile.setProfileId(json.getString("id"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAndWait();
        //request.executeAsync();
    }*/


    private void showMessage(String message) {
        Toast.makeText(SignHomeActivity.this, message, Toast.LENGTH_LONG).show();
    }

}

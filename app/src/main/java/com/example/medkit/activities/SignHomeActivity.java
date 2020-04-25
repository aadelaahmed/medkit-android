package com.example.medkit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivitySignHomeBinding;
import com.example.medkit.model.User;
import com.example.medkit.utils.LoadingAlertDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignHomeActivity extends AppCompatActivity {
    public static final String SHARED_PREFERENCE_NAME = "USER_DATA";
    private ActivitySignHomeBinding binding;
    private static final int RC_SIGN_IN = 200;
    boolean isFirstTime;
    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth = null;
    FirebaseUser currentUser = null;
    CallbackManager mCallbackManager;
    SharedPreferences.Editor editor;
    String createdTime;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection;
    LoadingAlertDialog tempDialog;

   /* public ProgressDialog iniProgressBar(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setContentView(R.layout.activity_loading);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        return progressDialog;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mCallbackManager = CallbackManager.Factory.create();
        usersCollection = db.collection("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        tempDialog = new LoadingAlertDialog(this);
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        binding.emailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(User.NORMAL_REGISTER, "normal register");
                editor.commit();
                startActivity(new Intent(SignHomeActivity.this,UserTypeActivity.class));
                //TODO enable action back click
                finish();
            }
        });
        binding.facebookSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempDialog.startAlertDialog();
                LoginManager.getInstance().logInWithReadPermissions(SignHomeActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        //requestFacebookData(loginResult.getAccessToken());
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
                });

                //startActivity(new Intent(SignHomeActivity.this,UserTypeActivity.class));
            }
        });
        binding.googleSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configure Google Sign In
                tempDialog.startAlertDialog();
                requestClientGoogle();
                //startActivity(new Intent(SignHomeActivity.this,UserTypeActivity.class));
            }
        });

        binding.signinTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempDialog.startAlertDialog();
                startActivity(new Intent(SignHomeActivity.this,SignInActivity.class));
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        tempDialog.dismissAlertDialog();
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
        super.onBackPressed();
    }

  /*  @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null)
            startActivity(new Intent(SignHomeActivity.this, CommunityActivity.class));
    }*/

    public void requestClientGoogle() {
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
                tempDialog.dismissAlertDialog();
                showMessage("Google sign in failed");
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        } else {
            // pass login result to login manager
            // here we can manage callbacks from facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest tempUpdate =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(acct.getDisplayName())
                                            .setPhotoUri(acct.getPhotoUrl())
                                            .build();
                            currentUser.updateProfile(tempUpdate);
                            editor.putString(User.USER_ID, acct.getId());
                            editor.putString(User.EMAIL, acct.getEmail());
                            editor.putString(User.FULLNAME, acct.getDisplayName());
                            editor.putString(User.USER_PHOTO, String.valueOf(acct.getPhotoUrl()));
                            editor.commit();
                            isFirstTime = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (!isFirstTime) {
                                tempDialog.dismissAlertDialog();
                                showMessage("Email already existed");
                                return;
                            }
                            Log.d("TAG", "signInWithCredential:success");
                            //create method to get user profile information
                            //getUserInfo();
                            updateUI(currentUser);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            tempDialog.dismissAlertDialog();
                            showMessage("signInWithCredential:failure");
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void handleFacebookAccessToken(final AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            requestFacebookData(token);
                            /*currentUser = firebaseAuth.getCurrentUser();
                            editor.putString(User.USER_ID, token.getUserId());*/
                            updateUI(currentUser);
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            tempDialog.dismissAlertDialog();
                            showMessage("Email already existed");
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
           /* String userId = currentUser.getUid();
            String userName = currentUser.getDisplayName();
            String userPhoto = currentUser.getPhotoUrl().toString();
            String userEmail = currentUser.getEmail();*/
            // createdTime = SignUpActivity.timestampToString(currentUser.getMetadata().getCreationTimestamp());
          /*  for(UserInfo profile : currentUser.getProviderData()) {
                // check if the provider id matches "facebook.com"
                if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    facebookUserId = profile.getUid();
                    // facebookUserPhoto = profile.getPhotoUrl().toString();
                    facebookUserName = profile.getDisplayName();
                }
            }
            String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
           /* editor.putString(User.USER_ID,userId);
            editor.putString(User.FULLNAME,userName);
            editor.putString(User.IMGURL,userPhoto);
            editor.putString(User.EMAIL,userEmail); */
            /*editor.putString(User.FULLNAME,facebookUserName);
            editor.putString(User.USER_PHOTO,photoUrl);
            editor.putString(User.USER_ID,facebookUserId);
            editor.putString(User.CREATED_TIME, createdTime);
            editor.commit();*/
            Intent intent = new Intent(SignHomeActivity.this, UserTypeActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void showMessage(String message) {
        Toast.makeText(SignHomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public void requestFacebookData(final AccessToken tempAccessToken) {

        GraphRequest request = GraphRequest.newMeRequest(tempAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        String fbUserName = null, fbUserPhoto = null, fbUserId = null, fbUserEmail = null;
                        fbUserEmail = json.getString("email");
                        fbUserName = json.getString("name");
                        //fbUserId = tempAccessToken.getUserId();
                        fbUserPhoto = json.getString("picture");
                        //fbUserPhoto = "https://graph.facebook.com/"+fbUserId+"/picture?height=500";

                        currentUser = firebaseAuth.getCurrentUser();
                        UserProfileChangeRequest tempUpdate =
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fbUserName)
                                        .setPhotoUri(Uri.parse(fbUserPhoto))
                                        .build();
                        currentUser.updateProfile(tempUpdate);
                        //editor.putString(User.USER_ID, fbUserId);
                        editor.putString(User.EMAIL, fbUserEmail);
                        editor.putString(User.FULLNAME, fbUserName);
                        editor.putString(User.USER_PHOTO, fbUserPhoto);
                        editor.commit();
                        //String text = "<b>Name :</b> "+json.getString("name")+"<br><br><b>Email :</b> "+json.getString("email")+"<br><br><b>Profile link :</b> "+json.getString("link");
                        /*details_txt.setText(Html.fromHtml(text));
                        profile.setProfileId(json.getString("id"));*/
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

}

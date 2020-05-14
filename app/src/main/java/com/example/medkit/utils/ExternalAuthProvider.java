package com.example.medkit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.activities.CommunityActivity;
import com.example.medkit.activities.SignHomeActivity;
import com.example.medkit.activities.SignInActivity;
import com.example.medkit.activities.UserTypeActivity;
import com.example.medkit.model.User;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import androidx.annotation.NonNull;

public class ExternalAuthProvider {
    private static final int RC_SIGN_IN = 200;
    public CallbackManager mCallbackManager;
    public LoadingAlertDialog loadingDialog;
    public String userName;
    public String userEmail;
    public String userImage;
    public boolean fbFlagData = false;
    public boolean gFlagData = false;
    public boolean isFirstTime = false;
    Activity currentActivity;
    Context mContext;
    CollectionReference rootUsers = FirebaseFirestore.getInstance().collection("Users");
    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser currentUser = null;
    private GoogleSignInClient mGoogleSignInClient;

    public ExternalAuthProvider(Activity currentActivity, FirebaseAuth firebaseAuth, FirebaseUser currentUser) {
        this.firebaseAuth = firebaseAuth;
        this.currentUser = currentUser;
        loadingDialog = new LoadingAlertDialog(currentActivity);
        this.currentActivity = currentActivity;
        this.mCallbackManager = CallbackManager.Factory.create();
    }


    private void showMessage(String message) {
        Toast.makeText(currentActivity, message, Toast.LENGTH_SHORT).show();
    }

    public void signInWithFaceBook() {
        loadingDialog.startAlertDialog();
        LoginManager.getInstance().logInWithReadPermissions(currentActivity, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                loadingDialog.dismissAlertDialog();
                LoginManager.getInstance().logOut();
                showMessage("login opertaion was cancled");
            }

            @Override
            public void onError(FacebookException error) {
                loadingDialog.dismissAlertDialog();
                showMessage(error.getMessage());
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(currentActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //currentUser = firebaseAuth.getCurrentUser();
                            //editor.putString(User.EMAIL, token.getUserId());
                            boolean tempFlag = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d("TAG", "onComplete: " + tempFlag);
                            if (!tempFlag || currentActivity instanceof SignInActivity) {
                                loadingDialog.dismissAlertDialog();
                                checkUserExisted(userEmail);
                                return;
                            }

                            getFacebookData(token);
                            //userExists(userEmail);
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            loadingDialog.dismissAlertDialog();
                            showMessage(task.getException().toString());
                        }
                    }
                });
    }

    public void getFacebookData(final AccessToken token) {
        userName = userEmail = userImage = null;
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
                        //updateUI();
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
        /*Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAndWait();*/
        Log.d("TAG", "getFacebookData: here we are");
        if (userName != null && userEmail != null && userImage != null)
            updateUI();
        else {
            showMessage("check your internet connection and try again");
            loadingDialog.dismissAlertDialog();
        }
        //request.executeAsync();
    }

    //Google LOGIN
    public void requestClientGoogle() {
        loadingDialog.startAlertDialog();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(currentActivity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //create googleSignInClient with the previous request(options) gso
        mGoogleSignInClient = GoogleSignIn.getClient(currentActivity, gso);
        fireIntentGoogle();
    }

    private void fireIntentGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        if (currentActivity instanceof SignHomeActivity)
            currentActivity.startActivityForResult(signInIntent, SignHomeActivity.RC_SIGN_IN);
        else
            currentActivity.startActivityForResult(signInIntent, SignInActivity.RC_SIGN_IN);
    }

    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        userName = userEmail = userImage = null;
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(currentActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userEmail = acct.getEmail();
                            //currentUser = firebaseAuth.getCurrentUser();
                            //updateProfileUser(userName, Uri.parse(userImage));
                            isFirstTime = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (!isFirstTime || currentActivity instanceof SignInActivity) {
                                //loadingDialog.dismissAlertDialog();
                                checkUserExisted(userEmail);
                                Log.d("TAG", "onComplete: google here we are");
                                return;
                            }
                            userName = acct.getDisplayName();
                            userImage = acct.getPhotoUrl().toString();
                            Log.d("TAG", "onComplete gooogle: " + userImage);
                            isFirstTime = false;
                            gFlagData = true;
                            updateUI();
                            Log.d("TAG", "signInWithCredential:success");
                            //create method to get user profile information
                            //getUserInfo();
                            //updateUI();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            loadingDialog.dismissAlertDialog();
                            gFlagData = false;
                            showMessage(task.getException().toString());
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        // ...
                    }
                });
    }

    public void checkUserExisted(String tempEmail) {
        Query query = rootUsers.whereEqualTo("email", tempEmail);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0 && currentActivity instanceof SignHomeActivity) {
                        //case:- user totally register but he/she can't remember
                        showMessage("Email already exists");
                        loadingDialog.dismissAlertDialog();
                        currentActivity.startActivity(new Intent(currentActivity, SignInActivity.class));
                    } else if (task.getResult().size() == 0) {
                        //case:- user register without sending data into firebase
                        currentUser.delete();
                        showMessage("please sign up first");
                        loadingDialog.dismissAlertDialog();
                        currentActivity.startActivity(new Intent(currentActivity, SignHomeActivity.class));
                        currentActivity.finish();
                    } else {
                        loadingDialog.dismissAlertDialog();
                        currentActivity.startActivity(new Intent(currentActivity, CommunityActivity.class));
                        currentActivity.finish();
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

    private void updateUI() {

           /* String userId = currentUser.getUid();
            String userName = currentUser.getDisplayName();
            String userPhoto = currentUser.getPhotoUrl().toString();
            String userEmail = currentUser.getEmail();*/
        //creationTime = SignUpActivity.timestampToString(currentUser.getMetadata().getCreationTimestamp());

           /* editor.putString(User.USER_ID,userId);
            editor.putString(User.FULLNAME,userName);
            editor.putString(User.IMGURL,userPhoto);
            editor.putString(User.EMAIL,userEmail); */
        SharedPreferences sharedPreferences = currentActivity.getSharedPreferences(SignHomeActivity.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.FULLNAME, userName);
        editor.putString(User.EMAIL, userEmail);
        editor.putString(User.USER_PHOTO, userImage);
        Log.d("TAG", "updateUI: " + userName + userEmail + userImage);
        editor.commit();
        Intent intent = new Intent(currentActivity, UserTypeActivity.class);
        currentActivity.startActivity(intent);
        loadingDialog.dismissAlertDialog();
        currentActivity.finish();
    }

}

package com.example.medkit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medkit.databinding.ActivityGetStartedBinding;
import com.example.medkit.model.User;
import com.example.medkit.utils.LoadingAlertDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class GetStartedActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CollectionReference userCollection;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseUser currentUser;
    String emailUser = null;
    FirebaseStorage rootStorage;
    String fullName;
    String imageUser;
    String tempNormalReg = null;
    Uri resImageUri;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    private ActivityGetStartedBinding binding;
    LoadingAlertDialog tempAlertDialog;

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
       /* StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/
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
        userCollection = db.collection(User.USER_COLLECTION);
        sharedPreferences = getSharedPreferences(SignHomeActivity.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        rootStorage = FirebaseStorage.getInstance();
        tempNormalReg = sharedPreferences.getString(User.NORMAL_REGISTER, null);
        tempAlertDialog = new LoadingAlertDialog(this);
        //emailUser = sharedPreferences.getString(User.EMAIL, null);
        binding.btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = getIntent();
                //FirebaseUser user =  mAuth.getCurrentUser();
                //FirebaseUser currentUser = (FirebaseUser) intent.getExtras().get(SignUpActivity.USER_KEY);
                try {
                    checkEmailVerification();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkEmailVerification() throws IOException {
        if (tempNormalReg == "noraml register") {
            if (currentUser.isEmailVerified()) {
                tempAlertDialog.startAlertDialog();
                getSharedData();
            } else {
                showMessage("please verify your account");
                currentUser.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        } else {
            tempAlertDialog.startAlertDialog();
            getSharedData();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void getSharedData() throws IOException {
        sharedPreferences = getSharedPreferences(SignHomeActivity.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        boolean isDoctor = sharedPreferences.getBoolean(User.IS_DOCTOR, false);
        fullName = sharedPreferences.getString(User.FULLNAME, null);
        emailUser = sharedPreferences.getString(User.EMAIL, null);
        imageUser = sharedPreferences.getString(User.USER_PHOTO, null);
        Log.d("TAG", "getSharedData: check image user " + imageUser);
        String uType = "";
        User newUser;
        Map<String, Object> userType = new HashMap<>();
        if (isDoctor) {
            uType = "Doctor";
            String gFaculty = sharedPreferences.getString(User.G_FACULTY, null);
            String gYear = sharedPreferences.getString(User.G_YEAR, null);
            String speciality = sharedPreferences.getString(User.SPECIALITY, null);
            String location = sharedPreferences.getString(User.LOCATION, null);
            userType.put(User.G_FACULTY, gFaculty);
            userType.put(User.G_YEAR, gYear);
            userType.put(User.SPECIALITY, speciality);
            userType.put(User.LOCATION, location);
            userType.put(User.USERTYPE, uType);
            //Photo url =storagerefrence + User ID ,so we pass the the six-th parameter in constructor as uId
            newUser = new User(userType, emailUser, fullName);
        } else {
            uType = "Patient";
            userType.put(User.USERTYPE, uType);
            newUser = new User(userType, emailUser, fullName);
        }
        String tempNormalReg = sharedPreferences.getString(User.NORMAL_REGISTER, null);
        String userKey = currentUser.getUid();
        newUser.setUid(userKey);
        uploadIntoFirebaseStorage(newUser, userKey);
    }

    private void uploadIntoFirebaseStorage(final User tempNewUser, final String tempUserKey) throws IOException {
        //StorageReference storageRef = rootStorage.getReference();
        StorageReference childStorage = rootStorage.getReference(User.USER_IMAGES_STORAGE + "/" + tempUserKey);
        // Uri tempImgUri = Uri.parse(imageUser);
        AsyncTask<String, Void, Uri> x = new ImageFirebaseStorage().execute(imageUser);
        try {
            resImageUri = x.get(3000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Log.d("TAG", "uploadIntoFirebaseStorage: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d("TAG", "uploadIntoFirebaseStorage: " + e.getMessage());
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.d("TAG", "uploadIntoFirebaseStorage: " + e.getMessage());
            e.printStackTrace();
        }
//        Log.d("TAG", "uploadIntoFirebaseStorage: " + resImageUri.toString());
        childStorage.putFile(resImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (fullName != null && resImageUri != null) {
                    updateProfileUser(fullName, Uri.parse(imageUser));
                    uploadIntoFireStore(tempNewUser, tempUserKey);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
                tempAlertDialog.dismissAlertDialog();
            }
        });
    }

    private void updateProfileUser(String tempName, Uri tempImage) {
        UserProfileChangeRequest tempUpdate =
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(tempName)
                        .setPhotoUri(tempImage)
                        .build();
        Log.d("TAG", "updateProfileUser: " + tempUpdate.toString());
        currentUser.updateProfile(tempUpdate);
    }

    private void uploadIntoFireStore(User newUser, String userKey) {
        userCollection.document(userKey)
                .set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                editor.clear();
                editor.commit();
                tempAlertDialog.dismissAlertDialog();
                startActivity(new Intent(GetStartedActivity.this, CommunityActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tempAlertDialog.dismissAlertDialog();
                showMessage(e.getMessage());
            }
        });
    }
   /* private void saveImageFromURL(String tempImageUserURL) throws IOException {
        URL imageUrl = new URL(tempImageUserURL);
        Bitmap bitMap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
        resImageUri = getImageUri(this, bitMap);
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/

    class ImageFirebaseStorage extends AsyncTask<String, Void, Uri> {
        @Override
        protected Uri doInBackground(String... strings) {
            URL imageUrl = null;
            try {
                imageUrl = new URL(strings[0]);
            } catch (MalformedURLException e) {
                Log.d("TAG", "doInBackground: " + e.getMessage());
                e.printStackTrace();
            }
            Bitmap bitMap = null;
            try {
                bitMap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            } catch (IOException e) {
                showMessage(e.getMessage());
                e.printStackTrace();
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitMap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(GetStartedActivity.this.getContentResolver(), bitMap, "Title", null);
            return Uri.parse(path);
        }
    }

}

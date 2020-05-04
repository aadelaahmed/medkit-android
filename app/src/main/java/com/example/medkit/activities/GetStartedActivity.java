package com.example.medkit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
    // String uid;
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
        userCollection = db.collection("Users");
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
        // String createdTime = String.valueOf(FieldValue.serverTimestamp());
        //String createdTime = sharedPreferences.getString(User.CREATED_TIME, null);
        fullName = sharedPreferences.getString(User.FULLNAME, null);
        //uid = sharedPreferences.getString(User.USER_ID, null);
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
            newUser = new User(userType, emailUser, fullName, imageUser);
        } else {
            uType = "Patient";
            userType.put(User.USERTYPE, uType);
            newUser = new User(userType, emailUser, fullName, imageUser);
        }
        //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
      /*  List<String> lstPostKeys = new ArrayList<>();
        lstPostKeys.add("post key 1");
        lstPostKeys.add("post key 3");
        lstPostKeys.add("post key 2");
        lstPostKeys.add("post key 4");
        newUser.setPostKeys(lstPostKeys);*/
        String tempNormalReg = sharedPreferences.getString(User.NORMAL_REGISTER, null);
       /* if(tempNormalReg == null)
            tempImgUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.userphoto);
        else
            tempImgUri = Uri.parse(imageUser);
        Log.d("TAG", "getSharedData: google auth  "+tempImgUri.toString() );*/
        //String userId = currentUser.getUid();
        //DocumentReference tempDoc = userCollection.document();
        //create method for uploading user data into firestore and uploading image into firebase storage
        String userKey = currentUser.getUid();
        newUser.setUid(userKey);
        uploadIntoFirebaseStorage(newUser, userKey);
    }

    private void uploadIntoFirebaseStorage(final User tempNewUser, final String tempUserKey) throws IOException {
        //StorageReference storageRef = rootStorage.getReference();
        StorageReference childStorage = rootStorage.getReference("userPhoto/" + tempUserKey);
        // Uri tempImgUri = Uri.parse(imageUser);
        saveImageFromURL(imageUser);
        Log.d("TAG", "uploadIntoFirebaseStorage: " + resImageUri.toString());
        childStorage.putFile(resImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (fullName != null && resImageUri != null)
                    updateProfileUser(fullName, Uri.parse(imageUser));
                uploadIntoFireStore(tempNewUser, tempUserKey);
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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tempAlertDialog.dismissAlertDialog();
                showMessage(e.getMessage());
            }
        });
    }

    private void saveImageFromURL(String tempImageUserURL) throws IOException {
        URL imageUrl = new URL(tempImageUserURL);
        Bitmap bitMap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
        resImageUri = getImageUri(this, bitMap);
       /* URL url = new URL (tempImageUserURL);
        InputStream input = url.openStream();
        try {
            File storagePath = Environment.getExternalStorageDirectory();
            OutputStream output = new FileOutputStream(storagePath + "/myImage.png");
            try {
                byte[] buffer = new byte[200];
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                }
                //get previous stored image
            } finally {
                output.close();
            }
        } finally {
            input.close();
        }*/
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}

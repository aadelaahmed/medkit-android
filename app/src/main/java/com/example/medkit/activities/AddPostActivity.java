package com.example.medkit.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.medkit.databinding.ActivityAddPostBinding;
import com.example.medkit.model.PostModel;
import com.example.medkit.utils.LoadingAlertDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AddPostActivity extends AppCompatActivity {
    public static final int mRequestCode = 40;
    ActivityAddPostBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference rootPost = db.collection("Posts");
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    StorageReference rootStorage;
    Uri pickedImageUri = null;
    String imageUrlStr = null;
    String title, description, category;
    PostModel addedPost;
    LoadingAlertDialog tempDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        binding.btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermission();
            }
        });

        tempDialog = new LoadingAlertDialog(this);

        binding.btnAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* binding.btnAddPost.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);*/
                tempDialog.startAlertDialog();
                //binding.btnUploadPhoto.setPressed(false);
                title = binding.edtTitle.getText().toString().trim();
                description = binding.edtDescription.getText().toString().trim();
                category = binding.edtCategory.getText().toString().trim();
                if (!title.isEmpty() && !description.isEmpty() && !category.isEmpty()) {
                    /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM");
                    String createdTime = simpleDateFormat.format(new Date());*/
                    addedPost = new PostModel(
                            title,
                            description,
                            imageUrlStr,
                            currentUser.getPhotoUrl().toString(),
                            currentUser.getUid(),
                            category,
                            0,
                            0
                    );
                    addedPost.setUserName(currentUser.getDisplayName());
                    //Log.d("TAG", "onClick: "+ currentUser.getDisplayName());
                    binding.imgPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkAndRequestPermission();
                        }
                    });
                    /*Timestamp temp = (Timestamp) addedPost.getCreatedTime();
                    Date tempDate = temp.toDate();
                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(AddPostActivity.this);
                    Log.d("TAG", "onClick: "+dateFormat.format(tempDate));*/
                    /*Timestamp temp = (Timestamp) addedPost.getCurrentDate();
                    timeStampIntoDate(temp.toDate());*/
                    if (pickedImageUri != null)
                        uploadImageIntoStorage();
                    else
                        uploadPost(addedPost);

                } else {
                   /* binding.btnAddPost.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);*/
                    tempDialog.dismissAlertDialog();
                    binding.btnUploadPhoto.setPressed(true);
                    showMessage("Please enter all fileds");
                }

            }
        });
    }

    private void uploadImageIntoStorage() {
        rootStorage = FirebaseStorage.getInstance().getReference().child("post-images");
        final StorageReference postImageRef = rootStorage.child(pickedImageUri.getLastPathSegment());
        postImageRef.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                postImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        currentUser = mAuth.getCurrentUser();
                        //currentUser.reload();
                        imageUrlStr = uri.toString();
                        addedPost.setPostPhoto(imageUrlStr);
                        uploadPost(addedPost);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                /*binding.btnAddPost.setVisibility(View.VISIBLE);
                                binding.progressBar.setVisibility(View.GONE);*/
                                tempDialog.startAlertDialog();
                                //binding.btnUploadPhoto.setPressed(true);
                                showMessage(e.getMessage());
                            }
                        });
            }
        });
    }

    private void uploadPost(PostModel postModel) {
        currentUser = mAuth.getCurrentUser();
        DocumentReference docRef = rootPost.document();
        String postKey = docRef.getId();
        postModel.setPostKey(postKey);
        db.collection("Users").document(currentUser.getUid()).update("postKeys", FieldValue.arrayUnion(postKey));
        docRef.set(postModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("upload post successfully");
                startActivity(new Intent(AddPostActivity.this, CommunityActivity.class));
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        /*binding.btnAddPost.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);*/
                        tempDialog.dismissAlertDialog();
                        //binding.btnUploadPhoto.setPressed(true);
                        showMessage(e.getMessage());
                    }
                });

    }


    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed because of this and that")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(AddPostActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            mRequestCode
                                    );
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create()
                            .show();
                } else {
                    ActivityCompat.requestPermissions(AddPostActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            mRequestCode
                    );
                }
            } else
                openGallery();
        } else
            openGallery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                openGallery();
            else
                showMessage("PERMISSION WAS DENIED");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, mRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mRequestCode && resultCode == RESULT_OK && data != null) {
            pickedImageUri = data.getData();
            binding.imgPost.setImageURI(pickedImageUri);
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

   /* private void timeStampIntoDate(Date tempDate)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        String dateString = formatter.format(tempDate);
        Log.d("TAG", "timeStampIntoDate: "+dateString);
    }*/
}

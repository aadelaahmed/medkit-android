package com.example.medkit.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.medkit.databinding.ActivityProfileBinding;
import com.example.medkit.model.PostModel;
import com.example.medkit.utils.CustomPostAdapter;
import com.example.medkit.utils.GlideApp;
import com.example.medkit.utils.LoadingAlertDialog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    public static final int mRequestCode = 50;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage rootRef = FirebaseStorage.getInstance();
    CollectionReference rootUsers;
    CollectionReference rootPosts;
    DocumentReference docUser;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    //PostAdapter postAdapter;
    CustomPostAdapter tempAdapter;
    //RecyclerView recyclerPostProfile;
    Uri pickedImageUri;
    //private List<String> resPostKeys;
    LoadingAlertDialog tempDialog = null;
    StorageReference childStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.userProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermission();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        binding.userProfileName.setText(currentUser.getDisplayName());
        tempDialog = new LoadingAlertDialog(this);
        childStorageRef = rootRef.getReference("userPhoto/" + currentUser.getUid());
       /* Bitmap ppbitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_type_user);
        Bitmap ibitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_communicate);
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, true, false));
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, false, true));
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, true, true));
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, false, false));
        PostAdapter postAdapter = new PostAdapter(posts,this);

        binding.profilePostsContainer.setAdapter(postAdapter);*/
       /* binding.profilePostsContainer.setLayoutManager(new LinearLayoutManager(this));
        binding.profilePostsContainer.getItemAnimator().setChangeDuration(0);
        recyclerPostProfile = binding.profilePostsContainer;
        recyclerPostProfile.setHasFixedSize(true);
        recyclerPostProfile.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));*/
        rootUsers = db.collection("Users");
        rootPosts = db.collection("Posts");
        docUser = rootUsers.document(currentUser.getUid());
        //showMessage("create state");
        iniRecyclerView();
        GlideApp.with(this).load(childStorageRef).into(binding.userProfilePicture);
        binding.userProfileName.setText(currentUser.getDisplayName());
        //binding.userProfilePicture.setImageURI(currentUser.getPhotoUrl());
    }

    private void iniRecyclerView() {
        Query query = rootPosts
                .whereEqualTo("userID", currentUser.getUid())
                .orderBy("createdTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostModel> tempOption = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();

        tempAdapter = new CustomPostAdapter(tempOption, this);
        binding.profilePostsContainer.setAdapter(tempAdapter);
        binding.profilePostsContainer.setLayoutManager(new LinearLayoutManager(this));
        //binding.profilePostsContainer.getItemAnimator().setChangeDuration(0);
        //binding.profilePostsContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    @Override
    protected void onStart() {
        super.onStart();
        //showMessage("start state");
        tempAdapter.startListening();
        /*currentUser = mAuth.getCurrentUser();

        binding.userProfilePicture.setImageURI(currentUser.getPhotoUrl());
        mListener = rootPosts.whereEqualTo("userID", currentUser.getUid())
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("TAG", "onEvent: failed with fetching user's posts");
                            return;
                        }
                        for (QueryDocumentSnapshot resDoc : queryDocumentSnapshots) {
                            PostModel resPost = resDoc.toObject(PostModel.class);
                            lstPostModel.add(resPost);
                            Log.d("TAG", "onEvent: " + resPost.toString());
                        }
                        postAdapter = new PostAdapter(lstPostModel, ProfileActivity.this);
                        recyclerPostProfile.setAdapter(postAdapter);
                    }
                });

       /* currentUser = mAuth.getCurrentUser();
        rootUsers
                .whereEqualTo("user id",currentUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e !=null)
                {
                    Log.d("TAG", "onEvent: error in fetching data from user firestore");
                    return;
                }


            }
        });


        rootPosts
                .whereIn("postKey",resPostKeys)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Log.d("TAG", "onEvent: error in fetching data from firestore");
                    return;
                }
                for (QueryDocumentSnapshot tempDoc : queryDocumentSnapshots)
                {
                    PostModel resPost = tempDoc.toObject(PostModel.class);
                    resPostModel.add(resPost);
                }

            }
        });*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        //showMessage("stop state");
        tempAdapter.stopListening();
        //mListener.remove();
    }

    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("This permission is needed because of updating profile picture...")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(ProfileActivity.this,
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
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            mRequestCode
                    );
                }
            } else
                openGallery();
        } else
            openGallery();
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
            tempDialog.startAlertDialog();
            pickedImageUri = data.getData();
            //StorageReference temp  = childStorageRef.child(pickedImageUri.getLastPathSegment());
            childStorageRef.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    /*rootUsers.document(currentUser.getUid()).update("photoUrl", String.valueOf(newPhotoUri))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    tempDialog.dismissAlertDialog();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    tempDialog.dismissAlertDialog();
                                    showMessage(e.getMessage());
                                }
                            });*/
                    //update user profile
                    UserProfileChangeRequest request =
                            new UserProfileChangeRequest
                                    .Builder()
                                    .setPhotoUri(pickedImageUri)
                                    .build();
                    currentUser.updateProfile(request)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    tempDialog.dismissAlertDialog();
                                    showMessage("successful update profile with new photo");
                                    GlideApp.with(ProfileActivity.this).load(childStorageRef).into(binding.userProfilePicture);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            tempDialog.dismissAlertDialog();
                            Log.d("TAG", "onFailure: " + e.toString());
                            showMessage(e.toString());
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

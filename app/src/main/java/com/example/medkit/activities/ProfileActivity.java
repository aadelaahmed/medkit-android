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

import com.bumptech.glide.Glide;
import com.example.medkit.databinding.ActivityProfileBinding;
import com.example.medkit.model.PostModel;
import com.example.medkit.utils.PostAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileActivity extends AppCompatActivity {
    private List<PostModel> posts;
    private ActivityProfileBinding binding;
    public static final int mRequestCode = 50;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference rootUsers;
    CollectionReference rootPosts;
    DocumentReference docUser;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ListenerRegistration mListener;
    PostAdapter postAdapter;
    RecyclerView recyclerPostProfile;
    Uri pickedImageUri;
    //private List<String> resPostKeys;
    private List<PostModel> lstPostModel;

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
        posts = new ArrayList<>();
        binding.userProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermission();
            }
        });

       /* Bitmap ppbitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_type_user);
        Bitmap ibitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_communicate);
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, true, false));
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, false, true));
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, true, true));
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, false, false));
        PostAdapter postAdapter = new PostAdapter(posts,this);

        binding.profilePostsContainer.setAdapter(postAdapter);*/
        binding.profilePostsContainer.setLayoutManager(new LinearLayoutManager(this));
        binding.profilePostsContainer.getItemAnimator().setChangeDuration(0);
        recyclerPostProfile = binding.profilePostsContainer;
        recyclerPostProfile.setHasFixedSize(true);
        recyclerPostProfile.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootUsers = db.collection("Users");
        rootPosts = db.collection("Posts");
        docUser = rootUsers.document(currentUser.getUid());
        lstPostModel = new ArrayList<>();
        showMessage("create state");
        Glide.with(this).load(currentUser.getPhotoUrl()).into(binding.userProfilePicture);
        //binding.userProfilePicture.setImageURI(currentUser.getPhotoUrl());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        showMessage("restart state");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showMessage("Destroy state");

    }

    @Override
    protected void onResume() {
        super.onResume();
        showMessage("resume state");

    }

    @Override
    protected void onStart() {
        super.onStart();
        showMessage("start state");

        currentUser = mAuth.getCurrentUser();
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
        showMessage("stop state");
        //mListener.remove();
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
            pickedImageUri = data.getData();
            Glide.with(this).load(currentUser.getPhotoUrl()).into(binding.userProfilePicture);
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
                            showMessage("successful update profile with new photo");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage(e.toString());
                }
            });
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
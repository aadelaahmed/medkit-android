package com.example.medkit.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivityProfileBinding;
import com.example.medkit.model.PostModel;
import com.example.medkit.model.User;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Field;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class ProfileActivity extends AppCompatActivity implements CustomPostAdapter.OnPostLitener {
    private ActivityProfileBinding binding;
    public static final int mRequestCode = 50;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage rootRef = FirebaseStorage.getInstance();
    StorageReference storagePosts;
    StorageReference storageUsers;
    CollectionReference rootUsers;
    CollectionReference rootPosts;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Uri pickedImageUri;
    LoadingAlertDialog tempDialog = null;
    StorageReference childStorageRef;
    RecyclerView recyclerPosts;
    CustomPostAdapter customPostAdapter;
    String currentUserId, userId, userName;
    Intent intent;
    ListenerRegistration tempListener;
    Intent postDetailIntent;
    Map<String, Integer> newMapValue, postVotes;
    int newVote, tempCurrentVote, userNumClappins, userNumAnswers;
    PostModel tempPost;
    Intent recIntent;
    User userDataPofile;
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
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootUsers = db.collection(User.USER_COLLECTION);
        rootPosts = db.collection(PostModel.POST_COLLECTION);
        currentUserId = currentUser.getUid();

        /*intent = getIntent();
        userId = intent.getStringExtra(User.USER_ID);
        Log.d("TAG", "onCreate: " + userId);
        childStorageRef = rootRef.getReference("userPhoto/" + userId);
        GlideApp.with(this).load(childStorageRef).into(binding.userProfilePicture);*/
        iniUIProfile();
        if (userDataPofile.getUid().equals(userId)) {
            binding.userProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickPhoto();
                }
            });
        }

        if (currentUserId.equals(userId)) {

            binding.userProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkAndRequestPermission();
                }
            });
        } else {
            binding.userProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickPhoto();
                }
            });

        }
        tempDialog = new LoadingAlertDialog(this);
        iniRecyclerView();
    }

    private void iniUIProfile() {
        recIntent = getIntent();
        userDataPofile = recIntent.getParcelableExtra(User.OBJECT_KEY);
        userId = userDataPofile.getUid();
        userNumClappins = userDataPofile.getClappingCounter();
        userNumAnswers = userDataPofile.getCommentCounter();
        binding.txtApplaudse.setText(String.valueOf(userNumClappins) + " Applauds");
        binding.txtAnswers.setText(String.valueOf(userNumAnswers) + " Answers");
        binding.userProfileName.setText(userDataPofile.getFullName());
        storageUsers = rootRef.getReference().child(User.USER_IMAGES_STORAGE + "/" + userDataPofile.getUid());
        GlideApp.with(this).load(storageUsers).into(binding.userProfilePicture);
    }


    private void clickPhoto() {
        Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_photo_post, null);
        ImageView clickedPhoto = view.findViewById(R.id.img_clicked_post);
        GlideApp.with(this).load(childStorageRef).into(clickedPhoto);
        settingsDialog.setContentView(view);
        settingsDialog.show();
    }


   /* private void getCurrentUserData() {
        userName = currentUser.getDisplayName();
        binding.userProfileName.setText(userName);
    }*/

    /*private void getUserData(String tempUserId) {
        tempListener =
                rootUsers.whereEqualTo("userId", tempUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            showMessage(e.getMessage());
                            Log.d("TAG", "onEvent: " + e.getMessage());
                            return;
                        }
                        for (DocumentSnapshot tempDoc : queryDocumentSnapshots) {
                            userName = tempDoc.getString("fullName");
                            binding.userProfileName.setText(userName);
                        }

                    }
                });
    }*/

    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void iniRecyclerView() {
        Query query = rootPosts
                .whereEqualTo(PostModel.USER_ID, userId)
                .orderBy(PostModel.CREATED_TIME_KEY, Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostModel> tempOption = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();

        customPostAdapter = new CustomPostAdapter(tempOption, this, this);
        recyclerPosts = binding.profilePostsContainer;
        recyclerPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerPosts.setAdapter(customPostAdapter);
        ((SimpleItemAnimator) recyclerPosts.getItemAnimator()).setSupportsChangeAnimations(false);
        //binding.profilePostsContainer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userId.equals(currentUserId)) {
            //getOverflowMenu();
            getMenuInflater().inflate(R.menu.profile_menu_items, menu);
            return super.onCreateOptionsMenu(menu);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                mAuth.signOut();
                Intent intent = new Intent(this, SignHomeActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        customPostAdapter.startListening();

        //showMessage("start state");
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
        customPostAdapter.stopListening();
        if (tempListener != null)
            tempListener.remove();
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
                                    GlideApp.with(ProfileActivity.this).load(childStorageRef).into(binding.userProfilePicture);
                                    tempDialog.dismissAlertDialog();
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
                    showMessage(e.getMessage());
                    Log.d("TAG", "onFailure: " + e.getMessage());
                }
            });
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPostClick(PostModel clickedPost) {
        postDetailIntent = new Intent(this, PostDetail.class);
        postDetailIntent.putExtra(PostModel.OBJECT_KEY, clickedPost);
        startActivity(postDetailIntent);
    }

    @Override
    public void onUpVoteClick(DocumentSnapshot documentSnapshot, String currentUserID) {
        tempPost = documentSnapshot.toObject(PostModel.class);
        postVotes = tempPost.getUpVotes();
        tempCurrentVote = 0;
        if (postVotes.containsKey(currentUserID))
            tempCurrentVote = postVotes.get(currentUserID);
        int newVote = (tempCurrentVote == 1) ? 0 : 1;
        Log.d("TAG", "addUpVote: tuesday" + tempCurrentVote);
        //tempCurrentVote = newVote;
        Log.d("TAG", "addUpVote: tuesday" + tempCurrentVote);
        /*newMapValue = new HashMap<>();
        newMapValue.put(currentUserID, newVote);*/
        documentSnapshot.getReference().update(PostModel.UP_VOTES + "." + currentUserID, newVote);
    }

    @Override
    public void onDownVoteClick(DocumentSnapshot documentSnapshot, String currentUserID) {
        tempPost = documentSnapshot.toObject(PostModel.class);
        postVotes = tempPost.getUpVotes();
        tempCurrentVote = 0;
        if (postVotes.containsKey(currentUserID))
            tempCurrentVote = postVotes.get(currentUserID);
        newVote = (tempCurrentVote == -1) ? 0 : -1;
        Log.d("TAG", "addDownVote: tuesday" + tempCurrentVote);
        //currentUserVote = newVote;
        Log.d("TAG", "addDownVote: tuesday" + tempCurrentVote);
        /*newMapValue = new HashMap<>();
        newMapValue.put(currentUserID, newVote);*/
        documentSnapshot.getReference().update(PostModel.UP_VOTES + "." + currentUserID, newVote);
    }

    @Override
    public void onUserNameClick(PostModel clickedPost) {

    }

    @Override
    public void onPostImageClick(String postKey) {
        storagePosts = rootRef.getReference().child(PostModel.POST_IMAGES_STORAGE + "/" + postKey);
        Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_photo_post, null);
        ImageView clickedPhoto = view.findViewById(R.id.img_clicked_post);
        GlideApp.with(this).load(storagePosts).into(clickedPhoto);
        settingsDialog.setContentView(view);
        settingsDialog.show();
    }


}

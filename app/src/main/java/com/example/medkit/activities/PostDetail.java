package com.example.medkit.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.medkit.R;
import com.example.medkit.databinding.ActivityPostDetailBinding;
import com.example.medkit.model.Comment;
import com.example.medkit.model.PostModel;
import com.example.medkit.utils.CommentAdapter;
import com.example.medkit.utils.GlideApp;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PostDetail extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String postImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference rootPost = db.collection("Posts");
    DocumentReference currentDoc;
    CollectionReference rootComment;
    CommentAdapter commentAdapter;
    private ActivityPostDetailBinding binding;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    StorageReference storageUsers;
    StorageReference storagePosts;
    StorageReference storageCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        binding.postDetailComment.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.post_detail_comment) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        iniUI();
        binding.postDetailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPhoto();
            }
        });
        binding.postDetailBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
        iniRecyclerComments();
    }

    private void addComment() {
        String content = binding.postDetailComment.getText().toString().trim();
        if (!content.isEmpty() && currentUser != null) {
            /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM");
            String createdTime = simpleDateFormat.format(new Date());*/
            binding.postDetailComment.setText("");
            String tempUserName = currentUser.getDisplayName();
            String tempUserImage = currentUser.getPhotoUrl().toString();
            String tempUserId = currentUser.getUid();
            Comment comment = new Comment(content, tempUserId, tempUserImage, tempUserName);
            rootComment.document().set(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showMessage("success add comment");
                    binding.postDetailComment.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage("Failed add comment");
                }
            });
        } else
            showMessage("please write your comment");
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void iniUI() {
        Intent recIntent = getIntent();
        boolean imgFlag = recIntent.getBooleanExtra(PostModel.POST_IMAGE_FLAG, false);
        String postKey = recIntent.getStringExtra(PostModel.POST_KEY);
        String userID = recIntent.getStringExtra(PostModel.USER_ID);
        if (!imgFlag) {
            binding.postDetailImg.setVisibility(View.GONE);
        } else {
            //postImage = recIntent.getStringExtra(PostModel.POST_IMAGE_KEY);
            storagePosts = storageRef.getReference().child("postImages/" + postKey);
            GlideApp.with(this).load(storagePosts).into(binding.postDetailImg);
        }
        String title = recIntent.getStringExtra(PostModel.TITLE_KEY);
        String description = recIntent.getStringExtra(PostModel.DESCRIPTION_KEY);
        //String userPhoto = recIntent.getStringExtra(PostModel.USER_IMAGE_KEY);
        String createdTime = recIntent.getStringExtra(PostModel.TIME_KEY);
        String userName = currentUser.getDisplayName();
        String dateWithName = createdTime + " | by " + userName;

        currentDoc = rootPost.document(postKey);
        rootComment = currentDoc.collection("Comments");
        binding.postDetailTitle.setText(title);
        binding.postDetailDescription.setText(description);
        binding.postDetailDateName.setText(dateWithName);
        storageCurrentUser = storageRef.getReference().child("userPhoto/" + currentUser.getUid());
        storageUsers = storageRef.getReference().child("userPhoto/" + userID);
        GlideApp.with(this).load(storageUsers).into(binding.postDetailUserOwnerImg);
        GlideApp.with(this).load(storageCurrentUser).into(binding.postDetailUserOwnerImg);
        //Glide.with(this).load(currentUser.getPhotoUrl()).into(binding.postDetailUserImg);
    }

    private void clickPhoto() {
        Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_photo_post, null);
        ImageView clickedPhoto = view.findViewById(R.id.img_clicked_post);
        Glide.with(this).load(postImage).into(clickedPhoto);
        settingsDialog.setContentView(view);
        settingsDialog.show();
    }

    private void iniRecyclerComments() {
        Query query = rootComment.orderBy("createdTime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();
        commentAdapter = new CommentAdapter(options, this);
        RecyclerView recyclerView = binding.postDetailRecyclerComments;
        LinearLayoutManager tempLayoutManager = new LinearLayoutManager(this);
        tempLayoutManager.setReverseLayout(true);
        tempLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(tempLayoutManager);
        recyclerView.setAdapter(commentAdapter);
        //recyclerView.getItemAnimator().setChangeDuration(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        commentAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        commentAdapter.stopListening();
    }

    /* @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, Context mContext) {
        //initialize UI of activity
       /* clickedPost = documentSnapshot.toObject(PostModel.class);
        //startActivity(new Intent(mContext,PostDetail.class));
        String postImage;
        if(clickedPost.getPostPhoto() == null)
            binding.postDetailImg.setVisibility(View.GONE);
        else
        {
            postImage = clickedPost.getPostPhoto();
            Glide.with(this).load(postImage).into(binding.postDetailImg);
        }
        String title = clickedPost.getTitle();
        String description = clickedPost.getDescription();
        String createdTime = clickedPost.getCreatedTime();
        String userName = clickedPost.getUserName();
        String userPhoto = clickedPost.getUserPhoto();
        String dateWithName = createdTime + " | by "+ userName;
        binding.postDetailTitle.setText(title);
        binding.postDetailDescription.setText(description);
        binding.postDetailDateName.setText(dateWithName);
        Glide.with(this).load(userPhoto).into(binding.postDetailUserOwnerImg);
    }*/
}

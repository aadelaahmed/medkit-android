package com.example.medkit.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.medkit.R;
import com.example.medkit.databinding.ActivityPostDetailBinding;
import com.example.medkit.model.Comment;
import com.example.medkit.model.PostModel;
import com.example.medkit.model.User;
import com.example.medkit.utils.CommentAdapter;
import com.example.medkit.utils.GlideApp;
import com.example.medkit.utils.NotificationHelper;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class PostDetail extends AppCompatActivity {
    public static String target_id;
    public static String post_id;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference rootPost = db.collection(PostModel.POST_COLLECTION);
    DocumentReference currentDoc;
    CollectionReference rootComment;
    CommentAdapter commentAdapter;
    private ActivityPostDetailBinding binding;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    StorageReference storageUsers;
    StorageReference storagePosts;
    StorageReference storageCurrentUser;
    String userId, currentUserId, postKey, postImage, userName, content, currentUserName, title, description, dateWithName, createdTime;
    Intent intent, recIntent;
    Comment newComment;
    PostModel clickPost;
    boolean imgFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        iniUI();
        binding.postDetailUserOwnerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(PostDetail.this, ProfileActivity.class);
                intent.putExtra(User.USER_ID, userId);
                startActivity(intent);
            }
        });

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
        content = binding.postDetailComment.getText().toString().trim();
        binding.postDetailComment.setText("");
        currentUserId = currentUser.getUid();
        currentUserName = currentUser.getDisplayName();
        if (!content.isEmpty() && currentUser != null) {
            newComment = new Comment(content, currentUserId, userName);
            rootComment.document().set(newComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("success add comment");
                        binding.postDetailComment.setText("");
                        // notification
                        Log.d("notification", "from post details: " + post_id);
                        NotificationHelper.SendCommentNotification(content, target_id, post_id);
                        // end notification
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
        recIntent = getIntent();
        clickPost = recIntent.getParcelableExtra(PostModel.OBJECT_KEY);
        imgFlag = recIntent.getBooleanExtra(PostModel.POST_IMAGE_FLAG, false);
        postKey = clickPost.getPostKey();
        userId = clickPost.getUserID();
        if (!imgFlag) {
            binding.postDetailImg.setVisibility(View.GONE);
        } else {
            //postImage = recIntent.getStringExtra(PostModel.POST_IMAGE_KEY);
            storagePosts = storageRef.getReference().child(PostModel.POST_IMAGES_STORAGE + postKey);
            GlideApp.with(this).load(storagePosts).into(binding.postDetailImg);
        }
        title = clickPost.getTitle();
        description = clickPost.getDescription();
        Date clickDate = new Date(clickPost.getCreatedTime() * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        createdTime = dateFormat.format(clickDate);
        userName = clickPost.getUserName();
        dateWithName = createdTime + " | by " + userName;
        currentDoc = rootPost.document(postKey);
        rootComment = currentDoc.collection(Comment.COMMENT_COLLECTION);
        binding.postDetailTitle.setText(title);
        binding.postDetailDescription.setText(description);
        binding.postDetailDateName.setText(dateWithName);
        storageCurrentUser = storageRef.getReference().child(User.USER_IMAGES_STORAGE + "/" + currentUser.getUid());
        storageUsers = storageRef.getReference().child(User.USER_IMAGES_STORAGE + "/" + userId);
        GlideApp.with(this).load(storageUsers).into(binding.postDetailUserOwnerImg);
        GlideApp.with(this).load(storageCurrentUser).into(binding.postDetailUserOwnerImg);
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
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        //recyclerView.getItemAnimator().setChangeDuration(0);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int currentPosition = viewHolder.getAdapterPosition();
                commentAdapter.deleteComment(currentPosition);
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int currentPosition = viewHolder.getAdapterPosition();
                if (!commentAdapter.isOwnerComment(currentPosition))
                    return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        }).attachToRecyclerView(binding.postDetailRecyclerComments);
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

}

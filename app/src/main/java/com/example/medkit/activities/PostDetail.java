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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class PostDetail extends AppCompatActivity implements View.OnClickListener, CommentAdapter.OnCommentClickListener {
    public static String post_id;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference rootPost = db.collection(PostModel.POST_COLLECTION);
    private static final String TAG = "PostDetail";
    DocumentReference currentDoc;
    CollectionReference rootComment;
    CommentAdapter commentAdapter;
    private ActivityPostDetailBinding binding;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    StorageReference storageUsers;
    StorageReference storagePosts;
    StorageReference storageCurrentUser;
    String userId, currentUserId, postKey, postImage, userName, content, currentUserName, title, description, dateWithName, createdTime, category;
    Intent intent, recIntent;
    Comment newComment;
    PostModel clickPost;
    boolean imgFlag;
    CollectionReference rootUsers = db.collection(User.USER_COLLECTION);
    User clickUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        iniUI();
        binding.postDetailUserOwnerImg.setOnClickListener(this);
        binding.postDetailUserImg.setOnClickListener(this);
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

    private void updateUI(User clickUser) {
        intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(User.OBJECT_KEY, clickUser);
        startActivity(intent);
    }

    private void addComment() {
        content = binding.postDetailComment.getText().toString().trim();
        binding.postDetailComment.setText("");
        currentUserId = currentUser.getUid();
        currentUserName = getSharedPreferences("pref", MODE_PRIVATE).getString("user_name", null);
        Log.d(TAG, "addComment: " + currentUserName);
        if (!content.isEmpty() && currentUser != null) {
            newComment = new Comment(content, currentUserId, currentUserName);
            rootComment.document().set(newComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("success add comment");
                        binding.postDetailComment.setText("");
                        // notification
                        Log.d("notification", "from post details: " + post_id);
                        NotificationHelper.SendCommentNotification(currentUserName, content, clickPost.getUserID(), clickPost.getPostKey());
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
            storagePosts = storageRef.getReference().child(PostModel.POST_IMAGES_STORAGE + "/" + postKey);
            GlideApp.with(this).load(storagePosts).into(binding.postDetailImg);
        }
        title = clickPost.getTitle();
        description = clickPost.getDescription();
        if (description.equals(""))
            binding.postDetailDescription.setVisibility(View.GONE);
        else
            binding.postDetailDescription.setText(description);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM h:mm a", Locale.getDefault());
        createdTime = sdf.format(clickPost.getCreatedTime());
        userName = clickPost.getUserName();
        dateWithName = createdTime + " | by " + userName;
        currentDoc = rootPost.document(postKey);
        rootComment = currentDoc.collection(Comment.COMMENT_COLLECTION);
        binding.postDetailTitle.setText(title);
        binding.postDetailDateName.setText(dateWithName);
        storageCurrentUser = storageRef.getReference().child(User.USER_IMAGES_STORAGE + "/" + currentUser.getUid());
        storageUsers = storageRef.getReference().child(User.USER_IMAGES_STORAGE + "/" + userId);
        category = clickPost.getCategory();
        binding.postCategoryTv.setText(category);
        Log.d(TAG, "iniUI: " + userId);
        GlideApp.with(this).load(storageUsers).into(binding.postDetailUserOwnerImg);
        GlideApp.with(this).load(storageCurrentUser).into(binding.postDetailUserOwnerImg);
        GlideApp.with(this).load(storageCurrentUser).into(binding.postDetailUserImg);
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
        commentAdapter = new CommentAdapter(options, this, this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.post_detail_user_owner_img:
                navigateToProfile(userId);
                break;
            case R.id.post_detail_user_img:
                navigateToProfile(currentUserId);
                break;
        }

    }

    private void navigateToProfile(String tempUserId) {
        rootUsers.whereEqualTo(User.USER_ID, tempUserId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot tempDoc : task.getResult()) {
                    clickUser = tempDoc.toObject(User.class);
                }
                Log.d(TAG, "onComplete: " + clickUser.getUid());
                updateUI(clickUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    @Override
    public void onUserClick(String userId) {
        navigateToProfile(userId);
    }

    @Override
    public void onClappingClick(String userId) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

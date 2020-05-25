package com.example.medkit.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.activities.PostDetail;
import com.example.medkit.activities.ProfileActivity;
import com.example.medkit.model.Comment;
import com.example.medkit.model.PostModel;
import com.example.medkit.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewPostAdapter extends FirestorePostAdapter<NewPostAdapter.CustomViewHolder> {

    Context mContext;
    PostModel clickedPost;
    CollectionReference rootComment;
    DocumentReference currentDoc;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    StorageReference storagePosts;
    StorageReference storageUsers;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference rootPosts = db.collection("Posts");
    CollectionReference usersCollection = db.collection("Users");
    CollectionReference rootUpVotes;
    int currentUserVote = 0; //vote of current user for each post
    int cntrVotes = 0; // count for votes
    Map<String, Integer> mapVotes;
    String currentUserID;
    String userName;
    List<PostModel> postsList;

    public NewPostAdapter(Context mContext, Query mQuery) {
        super(mQuery);
        this.mContext = mContext;
    }

   /* public NewPostAdapter(Context mContext,List<PostModel> postsList )
    {
        this.mContext  = mContext;
        this.postsList = postsList;
    }*/

    private void getUserProfile() {
        mContext.startActivity(new Intent(mContext, ProfileActivity.class));
    }

    private void addComment(int tempPostition, String content) {
        if (tempPostition != RecyclerView.NO_POSITION && content != null) {
            rootComment = currentDoc.collection("Comments");
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            String userID = currentUser.getUid();
            //String userImage = currentUser.getPhotoUrl().toString();
            String userName = currentUser.getDisplayName();
            Comment newComment = new Comment(content, userID, userName);
            rootComment.document().set(newComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showMessage("Comment added successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage(e.getMessage());
                }
            });
        } else
            showMessage("Please enter your comment");
    }

    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    private void clickPhoto(int position) {
        Dialog settingsDialog = new Dialog(mContext);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_photo_post, null);
        ImageView clickedPhoto = view.findViewById(R.id.img_clicked_post);
        GlideApp.with(mContext).load(storagePosts).into(clickedPhoto);
        settingsDialog.setContentView(view);
        settingsDialog.show();
    }

    @NonNull
    @Override
    public NewPostAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false);
        return new CustomViewHolder(view);
    }

    private void addUpVote(CustomViewHolder holder) {
        int userVote = 0;
        PostModel tempPost = getSnapshot(holder.getAdapterPosition()).toObject(PostModel.class);
        Map<String, Integer> tempMap = tempPost.getUpVotes();
        if (tempMap.containsKey(currentUserID))
            userVote = mapVotes.get(currentUserID);
        int cntr = Integer.parseInt(holder.countVotes.getText().toString());
        int position = holder.getAdapterPosition();
        DocumentReference tempDoc = getSnapshot(position).getReference();
        int newVote = (userVote == 1) ? 0 : 1;
        Log.d("TAG", "addUpVote: currentUserVote" + userVote);
        Log.d("TAG", "addUpVote: cntrVotes before" + cntr);
        if (newVote == 1 && holder.btnDown.isSelected())
            cntr += 2;
        else if (newVote == 1)
            cntr++;
        else
            cntr--;
        Log.d("TAG", "addUpVote: cntrVotes after" + cntr);
        holder.countVotes.setText(String.valueOf(cntr));
        //userVote = newVote;
        Map<String, Integer> newMapValue = new HashMap<>();
        newMapValue.put(currentUser.getUid(), newVote);
        tempDoc.update("mapVotes", newMapValue);
    }

    private void addDownVote(CustomViewHolder holder) {
        int userVote = 0;
        PostModel tempPost = getSnapshot(holder.getAdapterPosition()).toObject(PostModel.class);
        Map<String, Integer> tempMap = tempPost.getUpVotes();
        if (tempMap.containsKey(currentUserID))
            userVote = mapVotes.get(currentUserID);
        int cntr = Integer.parseInt(holder.countVotes.getText().toString());
        int position = holder.getAdapterPosition();
        DocumentReference tempDoc = getSnapshot(position).getReference();
        int newVote = (userVote == -1) ? 0 : -1;
        Log.d("TAG", "addDownVote: currentUserVote " + userVote);
        Log.d("TAG", "addDownVote: newVote" + newVote);
        Log.d("TAG", "addDownVote: cntrVotes before" + cntr);
        if (newVote == -1 && holder.btnUp.isSelected())
            cntr -= 2;
        else if (newVote == -1)
            cntr--;
        else
            cntr++;
        Log.d("TAG", "addDownVote: cntrVote after" + cntr);
        holder.countVotes.setText(String.valueOf(cntr));
        userVote = newVote;
        Map<String, Integer> newMapValue = new HashMap<>();
        newMapValue.put(currentUser.getUid(), newVote);
        tempDoc.update("mapVotes", newMapValue);
    }

    private void computeVotes(PostModel model, CustomViewHolder holder) {
        cntrVotes = 0;
        for (String tempStrKey : mapVotes.keySet()) {
            cntrVotes += mapVotes.get(tempStrKey);
        }
        holder.countVotes.setText(String.valueOf(cntrVotes));
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        DocumentSnapshot tempDocSnapshot = getSnapshot(holder.getAdapterPosition());
        //currentDoc = tempDocSnapshot.getReference();
        PostModel model = tempDocSnapshot.toObject(PostModel.class);
        holder.txtTitle.setText(model.getTitle());
        holder.txtDescription.setText(model.getDescription());
        holder.txtCategory.setText(model.getCategory());
        storagePosts = storageRef.getReference().child("postImages/" + model.getPostKey());
        storageUsers = storageRef.getReference().child("userPhoto/" + model.getUserID());
        GlideApp.with(mContext).load(storageUsers).into(holder.imgUser);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        mapVotes = model.getUpVotes();
        if (mapVotes.containsKey(currentUserID))
            currentUserVote = mapVotes.get(currentUserID);
        computeVotes(model, holder);
        if (currentUserVote == 1)
            holder.btnUp.setSelected(true);
        else if (currentUserVote == -1)
            holder.btnDown.setSelected(true);
        holder.btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnDown.isSelected())
                    holder.btnDown.setSelected(false);
                holder.btnUp.setSelected(!holder.btnUp.isSelected());
                Log.d("TAG", "onClick: up " + holder.btnDown.isSelected());
                Log.d("TAG", "onClick: up " + holder.btnUp.isSelected());
                addUpVote(holder);
            }
        });

        holder.txtUserName.setText(model.getUserName());
        holder.btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnUp.isSelected())
                    holder.btnUp.setSelected(false);
                holder.btnDown.setSelected(!holder.btnDown.isSelected());
                Log.d("TAG", "onClick: down " + holder.btnDown.isSelected());
                Log.d("TAG", "onClick: down " + holder.btnUp.isSelected());
                addDownVote(holder);
            }
        });


        holder.edtComment.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                // TODO Auto-generated method stub
                if (view.getId() == R.id.edt_comment_post) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                        case MotionEvent.ACTION_DOWN:
                            if (event.getRawX() >= (holder.edtComment.getRight() - holder.edtComment.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                String content = holder.edtComment.getText().toString().trim();
                                addComment(holder.getAdapterPosition(), content);
                                holder.edtComment.setText("");
                            }
                            break;
                    }
                }
                return false;
            }
        });

        holder.imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempPosition = holder.getAdapterPosition();
                String tempPostId = postsList.get(tempPosition).getPostKey();
                storagePosts = storageRef.getReference().child("postImages/" + tempPostId);
                clickPhoto(tempPosition);
            }
        });
        if (model.getPostPhoto() != null)
            GlideApp.with(mContext)
                    .load(storagePosts).into(holder.imgPost);
        else
            holder.imgPost.setVisibility(View.GONE);
        holder.txtUserName.setText(model.getUserName());
        Log.d("TAG", "onBindViewHolder: " + model.getUserName());
        holder.txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO --> set concurrent user profile
                getUserProfile();
            }
        });
        holder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO --> set concurrent user profile
                getUserProfile();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(mContext, PostDetail.class);
                    if (clickedPost.getPostPhoto() == null)
                        intent.putExtra(PostModel.POST_IMAGE_FLAG, false);
                    else {
                        intent.putExtra(PostModel.POST_IMAGE_FLAG, true);
                        intent.putExtra(PostModel.USER_ID, clickedPost.getUserID());
                    }
                    String title = holder.txtTitle.getText().toString();
                    String description = holder.txtDescription.getText().toString();
                    Long temp = clickedPost.getCreatedTime();
                    Date tempDate = new Date(temp * 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
                    String createdTime = dateFormat.format(tempDate);
                    String clickUserName = holder.txtUserName.getText().toString();
                    String postKey = clickedPost.getPostKey();
                    intent.putExtra(PostModel.POST_KEY, postKey);
                    intent.putExtra(PostModel.TITLE_KEY, title);
                    intent.putExtra(PostModel.DESCRIPTION_KEY, description);
                    intent.putExtra(User.FULLNAME, clickUserName);
                    intent.putExtra(PostModel.TIME_KEY, createdTime);
                    mContext.startActivity(intent);
                }
            }
        });
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription, txtCategory, txtUserName, txtTime, txtNumOfComments, countVotes;
        ImageView imgUser;
        ImageView imgPost;
        ImageButton btnUp, btnDown;
        EditText edtComment;
        private View view;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtTitle = itemView.findViewById(R.id.post_title_tv);
            txtDescription = itemView.findViewById(R.id.post_content_tv);
            txtCategory = itemView.findViewById(R.id.post_category_tv);
            imgUser = itemView.findViewById(R.id.comment_user_photo);
            imgPost = itemView.findViewById(R.id.post_image);
            txtUserName = itemView.findViewById(R.id.post_user_name);
            btnUp = itemView.findViewById(R.id.up_vote_btn);
            btnDown = itemView.findViewById(R.id.down_vote_btn);
            txtTime = itemView.findViewById(R.id.post_title_tv);
            edtComment = itemView.findViewById(R.id.edt_comment_post);
            txtNumOfComments = itemView.findViewById(R.id.n_comments_tv);
            countVotes = itemView.findViewById(R.id.txt_counter);
        }

    }


}

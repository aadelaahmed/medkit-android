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
import android.widget.ToggleButton;

import com.example.medkit.R;
import com.example.medkit.activities.PostDetail;
import com.example.medkit.activities.ProfileActivity;
import com.example.medkit.model.Comment;
import com.example.medkit.model.PostModel;
import com.example.medkit.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomPostAdapter extends FirestoreRecyclerAdapter<PostModel, CustomPostAdapter.CustomHolder> {

    Context mContext;
    PostModel clickedPost;
    int cntrUp, cntrDown;
    CollectionReference rootComment;
    DocumentReference currentDoc;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    StorageReference storagePosts;
    StorageReference storageUsers;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection = db.collection("Users");
    int currentUserVote = 0; //vote of current user for each post
    Map<String, Integer> mapVotes;
    String currentUserID, description, content, userName;
    int tempPosition;
    PostModel tempModel;

    public CustomPostAdapter(@NonNull FirestoreRecyclerOptions<PostModel> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected void onBindViewHolder(@NonNull final CustomHolder holder, int position, @NonNull PostModel model) {
        tempPosition = holder.getAdapterPosition();
        tempModel = getSnapshots().getSnapshot(tempPosition).toObject(PostModel.class);
        description = tempModel.getDescription();
        if (description == "")
            holder.txtDescription.setVisibility(View.GONE);
        holder.txtTitle.setText(tempModel.getTitle());
        holder.txtDescription.setText(tempModel.getDescription());
        holder.txtCategory.setText(tempModel.getCategory());
        holder.txtUserName.setText(tempModel.getUserName());
        storagePosts = storageRef.getReference().child("postImages/" + tempModel.getPostKey());
        storageUsers = storageRef.getReference().child("userPhoto/" + tempModel.getUserID());
        GlideApp.with(mContext).load(storageUsers).into(holder.imgUser);
        //mapVotes = model.getMapUpVotes();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        clickedPost = model;
        mapVotes = tempModel.getMapVotes();
        if (mapVotes.containsKey(currentUserID))
            currentUserVote = mapVotes.get(currentUserID);
        //getUserName(holder);
        computeVotes(holder);
        if (currentUserVote == 1)
            holder.btnUp.setSelected(true);
        else if (currentUserVote == -1)
            holder.btnDown.setSelected(true);
        currentDoc = getSnapshots().getSnapshot(position).getReference();
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

        Log.d("TAG", "size of documents: " + String.valueOf(getSnapshots().size()));

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
                                content = holder.edtComment.getText().toString().trim();
                                userName = holder.txtUserName.getText().toString();
                                holder.edtComment.setText("");
                                addComment(holder.getAdapterPosition(), content, userName);
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
                String tempPostId = getItem(holder.getAdapterPosition()).getPostKey();
                storagePosts = storageRef.getReference().child("postImages/" + tempPostId);
                clickPhoto(holder.getAdapterPosition());
            }
        });
        if (tempModel.getPostPhoto() != null)
            GlideApp.with(mContext).load(storagePosts).into(holder.imgPost);
        else
            holder.imgPost.setVisibility(View.GONE);
        holder.txtUserName.setText(tempModel.getUserName());
        Log.d("TAG", "onBindViewHolder: " + tempModel.getUserName());

    }

    /*private void getUserName(final CustomHolder holder) {
        PostModel tempModel = getSnapshots().getSnapshot(holder.getAdapterPosition()).toObject(PostModel.class);
        String userId = tempModel.getUserID();
        if (userId != null) {
            usersCollection.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                String tempUserName = null;
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists())
                    {
                         tempUserName = documentSnapshot.getString("fullName");
                         holder.txtUserName.setText(tempUserName);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage(e.getMessage());
                }
            });
        } else
            showMessage("check user id");
    }*/

    private void addUpVote(CustomHolder tempHolder) {
        int tempPosition = tempHolder.getAdapterPosition();
        PostModel tempPost = getSnapshots().getSnapshot(tempPosition).toObject(PostModel.class);
        Map<String, Integer> tempMap = tempPost.getMapVotes();
        int tempCurrentVote = 0;
        if (tempMap.containsKey(currentUserID))
            tempCurrentVote = tempMap.get(currentUserID);
        int newVote = (tempCurrentVote == 1) ? 0 : 1;
        Log.d("TAG", "addUpVote: tuesday" + tempCurrentVote);
        //tempCurrentVote = newVote;
        Log.d("TAG", "addUpVote: tuesday" + tempCurrentVote);
        Map<String, Integer> newMapValue = new HashMap<>();
        newMapValue.put(currentUser.getUid(), newVote);
        getSnapshots().getSnapshot(tempPosition).getReference().update("mapVotes", newMapValue);
    }

    private void addDownVote(CustomHolder tempHolder) {
        int tempPosition = tempHolder.getAdapterPosition();
        PostModel tempPost = getSnapshots().getSnapshot(tempPosition).toObject(PostModel.class);
        Map<String, Integer> tempMap = tempPost.getMapVotes();
        int tempCurrentVote = 0;
        if (tempMap.containsKey(currentUserID))
            tempCurrentVote = tempMap.get(currentUserID);
        int newVote = (tempCurrentVote == -1) ? 0 : -1;
        Log.d("TAG", "addDownVote: tuesday" + currentUserVote);
        //currentUserVote = newVote;
        Log.d("TAG", "addDownVote: tuesday" + currentUserVote);
        Map<String, Integer> newMapValue = new HashMap<>();
        newMapValue.put(currentUserID, newVote);
        getSnapshots().getSnapshot(tempPosition).getReference().update("mapVotes", newMapValue);
    }

    private void computeVotes(CustomHolder holder) {
        int cntrVotes = 0;
        for (String tempStrKey : mapVotes.keySet()) {
            cntrVotes += mapVotes.get(tempStrKey);
        }
        holder.countVotes.setText(String.valueOf(cntrVotes));
    }
   /* private void getCurrentUserName(String userId, final CustomHolder holder) {
        if (userId != null) {
            usersCollection.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                String tempUserName = null;

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        tempUserName = documentSnapshot.getString("fullname");
                        holder.txtUserName.setText(tempUserName);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage(e.getMessage());
                }
            });
        } else
            showMessage("check user id");
    }*/


    private void getUserProfile() {
        mContext.startActivity(new Intent(mContext, ProfileActivity.class));
    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
        showMessage(e.getMessage());
    }

    private void addComment(int tempPostition, final String content, String userName) {
        if (tempPostition != RecyclerView.NO_POSITION && !content.equals("")) {
            currentDoc = getSnapshots().getSnapshot(tempPostition).getReference();
            rootComment = currentDoc.collection("Comments");
            Comment newComment = new Comment(content, currentUserID, userName);
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            String userID = currentUser.getUid();

            final PostModel clickedPost = getSnapshots().getSnapshot(tempPostition).toObject(PostModel.class);
           /*String userImage = currentUser.getPhotoUrl().toString();
            String userName = currentUser.getDisplayName();*/
            rootComment.document().set(newComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showMessage("Comment added successfully");
                    // notification
                    Log.d("notification", "from adapter addComment: " + currentDoc.getId());
                    NotificationHelper.SendCommentNotification(content, clickedPost.getUserID(), currentDoc.getId());
                    // end notification
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

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new CustomHolder(view);
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



    public class CustomHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtDescription, txtCategory, txtUserName, txtTime, txtNumOfComments, countVotes;
        ImageView imgUser;
        ImageView imgPost;
        ImageButton btnUp, btnDown;
        EditText edtComment;

        public CustomHolder(@NonNull final View itemView) {
            super(itemView);
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(mContext, PostDetail.class);
                        //mListener = new PostDetail();
                        PostModel clickedPost = getSnapshots().getSnapshot(position).toObject(PostModel.class);
                        if (clickedPost.getPostPhoto() == null)
                            intent.putExtra(PostModel.POST_IMAGE_FLAG, false);
                        else {
                            intent.putExtra(PostModel.POST_IMAGE_FLAG, true);
                            intent.putExtra(PostModel.USER_ID, clickedPost.getUserID());

                        }
                        String title = txtTitle.getText().toString();
                        String description = txtDescription.getText().toString();
                        Long temp = clickedPost.getCreatedTime();

                        // notification
                        PostDetail.target_id = clickedPost.getUserID();
                        PostDetail.post_id = getSnapshots().getSnapshot(position).getId();
                        Log.d("notification", "from adapter onclick " + clickedPost.getUserID());
                        // end notification
                        Date tempDate = new Date(temp * 1000);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
                        String createdTime = dateFormat.format(tempDate);
                        String clickUserName = txtUserName.getText().toString();
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

    }

}


   /* public void setOnItemClickListener(OnItemClickListener mListener)
    {
        this.mListener = mListener;
    }*/



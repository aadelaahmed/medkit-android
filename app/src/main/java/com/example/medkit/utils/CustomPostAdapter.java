package com.example.medkit.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.model.PostModel;
import com.example.medkit.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomPostAdapter extends FirestoreRecyclerAdapter<PostModel, CustomPostAdapter.CustomHolder> {

    Context mContext;
    CollectionReference rootComment;
    DocumentReference currentDoc;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    StorageReference storagePosts;
    StorageReference storageUsers;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //CollectionReference usersCollection = db.collection("Users");
    int currentUserVote = 0; //vote of current user for each post
    Map<String, Integer> mapVotes;
    private static final String TAG = "CustomPostAdapter";
    int tempPosition, currentCommentCounter;
    PostModel tempModel;
    String currentUserID, description; //, content, userName, title, clickDesc, createdTime, clickUserName, clickPostKey, clickUserID;
    Intent intent;
    /* Long clickCreatedTime;
     Date clickDate;*/
    OnPostLitener tempPostListener;
    SimpleDateFormat sdf;

    public CustomPostAdapter(@NonNull FirestoreRecyclerOptions<PostModel> options, Context mContext, OnPostLitener tempPostListener) {
        super(options);
        this.mContext = mContext;
        setHasStableIds(true);
        this.tempPostListener = tempPostListener;
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
        currentUserVote = 0;
        tempPosition = holder.getAdapterPosition();
        tempModel = getSnapshots().getSnapshot(tempPosition).toObject(PostModel.class);
        description = tempModel.getDescription();
        if (description.equals(""))
            holder.txtDescription.setVisibility(View.GONE);
        holder.txtTitle.setText(tempModel.getTitle());
        holder.txtDescription.setText(tempModel.getDescription());
        holder.txtCategory.setText(tempModel.getCategory());
        holder.txtUserName.setText(tempModel.getUserName());
        holder.txtNumOfComments.setText(String.valueOf(tempModel.getCommentCounter()));
        sdf = new SimpleDateFormat("d MMM h:mm a", Locale.getDefault());
        holder.txtTime.setText(sdf.format(tempModel.getCreatedTime()));
        storagePosts = storageRef.getReference().child(PostModel.POST_IMAGES_STORAGE + "/" + tempModel.getPostKey());
        storageUsers = storageRef.getReference().child(User.USER_IMAGES_STORAGE + "/" + tempModel.getUserID());
        GlideApp.with(mContext).load(storageUsers).into(holder.imgUser);
        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        mapVotes = tempModel.getUpVotes();
        if (mapVotes.containsKey(currentUserID))
            currentUserVote = mapVotes.get(currentUserID);
        Log.d("TAG", "onClick user vote: " + currentUserVote);
        //holder.countVotes.setText(String.valueOf(tempModel.getUpVotesCounter()));
        //getUserName(holder);
        computeVotes(holder);
        //Log.d("TAG", "onClick: bind up: "+holder.btnUp);
        if (currentUserVote == 1) {
            holder.btnUp.setSelected(true);
            if (holder.btnDown.isSelected())
                holder.btnDown.setSelected(false);
        } else if (currentUserVote == -1) {
            holder.btnDown.setSelected(true);
            if (holder.btnUp.isSelected())
                holder.btnUp.setSelected(false);
        } else if (currentUserVote == 0) {
            holder.btnUp.setSelected(false);
            holder.btnDown.setSelected(false);
        }
        //currentDoc = getSnapshots().getSnapshot(position).getReference();
       /* holder.btnUp.setOnClickListener(new View.OnClickListener() {
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
        });*/

       /* holder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(User.USER_ID, tempModel.getUserID());
                mContext.startActivity(intent);
            }
        });*/


        /*holder.edtComment.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

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
                                addComment(holder.getAdapterPosition(), content, userName,tempModel.getUserID(),tempModel.getPostKey());
                            }
                            break;
                    }
                }
                return false;
            }
        });*/


        if (tempModel.getPostPhoto())
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

   /* private void addUpVote(CustomHolder tempHolder) {
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
    }*/

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

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
        showMessage(e.getMessage());
    }



    /*private void addComment(int tempPostition, final String content, String userName, final String userId, final String postId) {
        if (tempPostition != RecyclerView.NO_POSITION && !content.equals("")) {
            currentDoc = getSnapshots().getSnapshot(tempPostition).getReference();
            rootComment = currentDoc.collection("Comments");
            Comment newComment = new Comment(content, currentUserID, userName);
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            //final PostModel clickedPost = getSnapshots().getSnapshot(tempPostition).toObject(PostModel.class);
            rootComment.document().set(newComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showMessage("Comment added successfully");
                    // notification
                    Log.d("notification", "from adapter addComment: " + currentDoc.getId());
                    NotificationHelper.SendCommentNotification(content, userId, postId);
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
    }*/

    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new CustomHolder(view, tempPostListener);
    }

    /*private void clickPhoto() {
        Dialog settingsDialog = new Dialog(mContext);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_photo_post, null);
        ImageView clickedPhoto = view.findViewById(R.id.img_clicked_post);
        GlideApp.with(mContext).load(storagePosts).into(clickedPhoto);
        settingsDialog.setContentView(view);
        settingsDialog.show();
    }*/


    public interface OnPostLitener {
        void onPostClick(PostModel clickedPost);

        void onUpVoteClick(DocumentSnapshot documentSnapshot, String currentUserID);

        void onDownVoteClick(DocumentSnapshot documentSnapshot, String currentUserID);

        void onUserNameClick(PostModel clickedPost);

        void onPostImageClick(String postKey);

    }

    public class CustomHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtTitle, txtDescription, txtCategory, txtUserName, txtTime, txtNumOfComments, countVotes, edtComment;
        ImageView imgUser;
        ImageView imgPost;
        ImageButton btnUp, btnDown, btnComment;
        //PostModel clickedPost;
        OnPostLitener onPostLitener;
        DocumentSnapshot documentSnapshot;
        PostModel tempPost;

        public CustomHolder(@NonNull final View itemView, OnPostLitener postLitener) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.post_title_tv);
            txtDescription = itemView.findViewById(R.id.post_content_tv);
            txtCategory = itemView.findViewById(R.id.post_category_tv);
            imgUser = itemView.findViewById(R.id.comment_user_photo);
            imgPost = itemView.findViewById(R.id.post_image);
            txtUserName = itemView.findViewById(R.id.post_user_name);
            btnUp = itemView.findViewById(R.id.up_vote_btn);
            btnDown = itemView.findViewById(R.id.down_vote_btn);
            txtTime = itemView.findViewById(R.id.psot_time_tv);
            edtComment = itemView.findViewById(R.id.edt_comment_post);
            txtNumOfComments = itemView.findViewById(R.id.n_comments_tv);
            countVotes = itemView.findViewById(R.id.txt_counter);
            btnComment = itemView.findViewById(R.id.comment_icon);
            this.onPostLitener = postLitener;
            itemView.setOnClickListener(this);
            btnUp.setOnClickListener(this);
            btnDown.setOnClickListener(this);
            imgPost.setOnClickListener(this);
            txtUserName.setOnClickListener(this);
            imgUser.setOnClickListener(this);
            edtComment.setOnClickListener(this);
            btnComment.setOnClickListener(this);
            txtNumOfComments.setOnClickListener(this);
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(mContext, PostDetail.class);
                        //mListener = new PostDetail();
                        clickedPost = getSnapshots().getSnapshot(position).toObject(PostModel.class);
                        if (clickedPost.getPostPhoto() == null)
                            intent.putExtra(PostModel.POST_IMAGE_FLAG, false);
                        else {
                            intent.putExtra(PostModel.POST_IMAGE_FLAG, true);
                        }
                         title = txtTitle.getText().toString();
                         description = txtDescription.getText().toString();
                         clickCreatedTime = clickedPost.getCreatedTime();
                         clickUserID = clickedPost.getUserID();
                        // notification
                        PostDetail.target_id = clickedPost.getUserID();
                        PostDetail.post_id = getSnapshots().getSnapshot(position).getId();
                        Log.d("notification", "from adapter onclick " + clickedPost.getUserID());
                        // end notification
                         clickDate = new Date(clickCreatedTime * 1000);
                         SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
                         createdTime = dateFormat.format(clickDate);
                         clickUserName = txtUserName.getText().toString();
                         clickPostKey = clickedPost.getPostKey();
                         intent.putExtra(User.USER_ID,clickUserID);
                        intent.putExtra(User.USER_ID, clickedPost.getUserID());
                        intent.putExtra(PostModel.POST_KEY, clickPostKey);
                        intent.putExtra(PostModel.TITLE_KEY, title);
                        intent.putExtra(PostModel.DESCRIPTION_KEY, description);
                        intent.putExtra(User.FULLNAME, clickUserName);
                        intent.putExtra(PostModel.TIME_KEY, createdTime);
                        currentUserVote = 0;
                        mContext.startActivity(intent);
                    }
                }
            });*/
        }

        @Override
        public void onClick(View view) {
            documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
            tempPost = documentSnapshot.toObject(PostModel.class);
            currentUserID = currentUser.getUid();
            switch (view.getId()) {
                case R.id.up_vote_btn:
                    if (this.btnDown.isSelected())
                        this.btnDown.setSelected(false);
                    view.setSelected(!view.isSelected());
                    Log.d("TAG", "onClick: prev down " + this.btnDown.isSelected());
                    Log.d("TAG", "onClick: up " + view.isSelected());
                    onPostLitener.onUpVoteClick(documentSnapshot, currentUserID);
                    break;
                case R.id.down_vote_btn:
                    if (this.btnUp.isSelected())
                        this.btnUp.setSelected(false);
                    view.setSelected(!view.isSelected());
                    Log.d("TAG", "onClick: prev up " + this.btnUp.isSelected());
                    Log.d("TAG", "onClick: up " + view.isSelected());
                    onPostLitener.onDownVoteClick(documentSnapshot, currentUserID);
                    break;
                case R.id.comment_user_photo:
                    onPostLitener.onUserNameClick(tempPost);
                    break;
                case R.id.post_user_name:
                    onPostLitener.onUserNameClick(tempPost);
                    break;
                case R.id.post_image:
                    onPostLitener.onPostImageClick(tempPost.getPostKey());
                    break;
                case R.id.edt_comment_post:
                    onPostLitener.onPostClick(tempPost);
                    break;
                case R.id.comment_icon:
                    onPostLitener.onPostClick(tempPost);
                    break;
                case R.id.n_comments_tv:
                    onPostLitener.onPostClick(tempPost);
                    break;
                default:
                    onPostLitener.onPostClick(tempPost);
            }
        }
    }
}




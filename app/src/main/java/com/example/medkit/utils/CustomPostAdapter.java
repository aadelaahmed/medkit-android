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
    int resUp = 0, resDown = 0;
    String currentUserID;
    String userName;

    public CustomPostAdapter(@NonNull FirestoreRecyclerOptions<PostModel> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull final CustomHolder holder, final int position, @NonNull final PostModel model) {
        resUp = resDown = 0;
        holder.txtTitle.setText(model.getTitle());
        holder.txtDescription.setText(model.getDescription());
        holder.txtCategory.setText(model.getCategory());
        storagePosts = storageRef.getReference().child("postImages/" + model.getPostKey());
        storageUsers = storageRef.getReference().child("userPhoto/" + model.getUserID());
        GlideApp.with(mContext).load(storageUsers).into(holder.imgUser);
        //mapVotes = model.getMapUpVotes();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        clickedPost = model;
        Log.d("TAG", "size of documents: " + String.valueOf(getSnapshots().size()));
       /* holder.btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: " + holder.btnUp.isChecked());
                if (holder.btnUp.isChecked()) {
                    if (holder.btnDown.isChecked())
                        holder.btnDown.setChecked(false);
                    holder.btnUp.setTextOn(++resUp + " Up");
                    setUpVotesBtn(holder, position, 1);
                } else {
                    holder.btnUp.setTextOff(--resUp + " Up");
                    setUpVotesBtn(holder, position, 0);
                }
            }
        });

        holder.btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: " + holder.btnDown.isChecked());
                if (holder.btnDown.isChecked()) {
                    if (holder.btnUp.isChecked())
                        holder.btnUp.setChecked(false);
                    holder.btnDown.setTextOn(++resDown + " Down");
                    setDownVotesBtn(holder, position, 1);
                } else {
                    holder.btnDown.setTextOff(--resDown + " Down");
                    setDownVotesBtn(holder, position, 0);
                }
            }
        });
        holder.btnUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    holder.btnDown.setChecked(false);
                    holder.btnUp.setTextOn(++resUp + " Up");
                    setUpVotesBtn(holder, position, 1);
                } else {
                    holder.btnUp.setTextOff(--resUp + " Up");
                    setUpVotesBtn(holder, position, 0);
                }

            }
        });

        holder.btnDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    holder.btnUp.setChecked(false);
                    holder.btnDown.setTextOn(++resDown + " Down");
                    setDownVotesBtn(holder, position, 1);
                } else {
                    holder.btnDown.setTextOff(--resDown + " Down");
                    setDownVotesBtn(holder, position, 0);
                }
            }
        });*/
        usersCollection.document(model.getUserID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userName = task.getResult().getString("fullName");
                    Log.d("TAG", "onComplete: " + userName);
                    holder.txtUserName.setText(userName);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
            }
        });
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
                clickPhoto(position);
            }
        });
        if (model.getPostPhoto() != null)
            GlideApp.with(mContext).load(storagePosts).into(holder.imgPost);
        else
            holder.imgPost.setVisibility(View.GONE);
        holder.txtUserName.setText(model.getUserName());
        Log.d("TAG", "onBindViewHolder: " + model.getUserName());
        /*holder.btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference tempDoc = getSnapshots().getSnapshot(position).getReference();
                PostModel currentPost = getItem(holder.getAdapterPosition());
                cntrUp = currentPost.getUpVotes();
                //cntrUp = model.getUpVotes();
                FieldValue incrementUp = FieldValue.increment(1);
                tempDoc.update("upVotes", incrementUp);
                holder.btnUp.setText(++cntrUp + " UP");

            }
        });

        holder.btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cntrDown = model.getDownVotes();
                DocumentReference tempDownDoc = getSnapshots().getSnapshot(position).getReference();
                //holder.btnUp.setText(cntrDown + " Down");
                //cntrDown = (int) tempDownDoc.get().getResult().get("downVotes");
                PostModel currentPost = getItem(holder.getAdapterPosition());
                cntrDown = currentPost.getDownVotes();
                //cntrDown = model.getDownVotes();
                FieldValue incrementDown = FieldValue.increment(1);
                tempDownDoc.update("downVotes", incrementDown);
                holder.btnDown.setText(++cntrDown + " Down");
            }
        });*/
    }

    private void setButtonStates(CustomHolder holder, int currentUserVote) {
        switch (currentUserVote) {
            case 1:
                holder.btnUp.setChecked(true);
                break;
            case -1:
                holder.btnDown.setChecked(true);
                break;
        }
    }

    private void setUpVotesBtn(CustomHolder holder, int position, int currentVote) {
        //int currentVote = (currentUserVote == 1) ? 0 : 1;
        /*if(mapVotes.containsKey(currentUserID))
            currentUserVote = mapVotes.get(currentUserID);
        currentVote = (currentUserVote == 1) ? 0 : 1;
        if(currentVote == 1)
            holder.btnUp.setText(++resUp + " Up");
        else
            holder.btnUp.setText(--resUp + " Up");*/
       /* Map<String, Integer> newMapValue = new HashMap<>();
        newMapValue.put(currentUser.getUid(), currentVote);
        getSnapshots().getSnapshot(position).getReference().update("mapUpVotes", newMapValue);*/
    }

    private void setDownVotesBtn(CustomHolder holder, int position, int currentVote) {
        //int currentVote = (currentUserVote == -1) ? 0 : -1;
        /*if(mapVotes.containsKey(currentUserID))
            currentUserVote = mapVotes.get(currentUserID);
        currentVote = (currentUserVote == -1) ? 0 : -1;
        if(currentVote == -1)
            holder.btnUp.setText(++resDown + " Down");
        else
            holder.btnUp.setText(--resDown + " Down");*/
        /*Map<String, Integer> newMapValue = new HashMap<>();
        newMapValue.put(currentUser.getUid(), currentVote);
        getSnapshots().getSnapshot(position).getReference().update("mapUpVotes", newMapValue);*/
    }

   /* private void computeVotes(PostModel model, CustomHolder holder) {
        for (String tempStrKey : mapVotes.keySet()) {
            if (mapVotes.get(tempStrKey) > 0)
                resUp++;
            else if (mapVotes.get(tempStrKey) < 0)
                resDown++;
        }
        holder.btnUp.setText(resUp + " UP");
        holder.btnDown.setText(resDown + " Down");
    }*/

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

    private void addComment(int tempPostition, String content) {
        if (tempPostition != RecyclerView.NO_POSITION && content != null) {
            currentDoc = getSnapshots().getSnapshot(tempPostition).getReference();
            rootComment = currentDoc.collection("Comments");
            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            String userID = currentUser.getUid();
           /*String userImage = currentUser.getPhotoUrl().toString();
            String userName = currentUser.getDisplayName();*/
            Comment newComment = new Comment(content, userID);
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

        TextView txtTitle, txtDescription, txtCategory, txtUserName, txtTime, txtNumOfComments;
        ImageView imgUser;
        ImageView imgPost;
        ToggleButton btnUp, btnDown;
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
                        String title = clickedPost.getTitle();
                        String description = clickedPost.getDescription();
                        /*Long temp = clickedPost.getCreatedTime();
                        Date tempDate = new Date(temp * 1000);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");*/
                        String createdTime = "";//dateFormat.format(tempDate);
                        //Log.d("TAG", "onClick: " + dateFormat.format(tempDate));
                        String postKey = clickedPost.getPostKey();
                        intent.putExtra(PostModel.POST_KEY, postKey);
                        intent.putExtra(PostModel.TITLE_KEY, title);
                        intent.putExtra(PostModel.DESCRIPTION_KEY, description);
                        intent.putExtra(User.FULLNAME, userName);
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



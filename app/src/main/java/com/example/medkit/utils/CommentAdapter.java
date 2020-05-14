package com.example.medkit.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.activities.ProfileActivity;
import com.example.medkit.model.Comment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder> {
    Context mContext;
    FirebaseStorage storageInstance = FirebaseStorage.getInstance();
    StorageReference storageRef;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection = db.collection("Users");

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_layout, parent, false);
        currentUser = mAuth.getCurrentUser();
        return new CommentViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
        /*Timestamp temp = (Timestamp) getSnapshots().getSnapshot(position).getData().get("createdTime");
        Date tempDate = temp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        String createdTime = dateFormat.format(tempDate);*/

        /*Date tempDate =new Date(model.getCreatedTime() * 1000);
        String createdTime = new SimpleDateFormat("dd MMMM").format(tempDate);
        Log.d("TAG", "onBindViewHolder comment: " + createdTime);*/
        String content = model.getContent();
        //String userImage = model.getUserImage();
        String userID = model.getUserId();
        storageRef = storageInstance.getReference("userPhoto/" + userID);
        holder.txtContent.setText(content);
        getCurrentUserName(userID, holder);
        //holder.txtDate.setText(createdTime);
        GlideApp.with(mContext).load(storageRef).into(holder.imgUser);
        holder.txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI();
            }
        });
        holder.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI();
            }
        });
    }

    private void getCurrentUserName(String userId, final CommentViewHolder holder) {
        if (userId != null) {
            usersCollection.document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                String tempUserName = null;

                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
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
    }


    private void updateUI() {
        //TODO profile of concurrent user
        mContext.startActivity(new Intent(mContext, ProfileActivity.class));
    }

    public void deleteComment(int adapterPosition) {
        getSnapshots().getSnapshot(adapterPosition).getReference().delete();
    }

    public boolean isOwnerComment(int currentPosition) {
        Map<String, Object> tempRes = getSnapshots().getSnapshot(currentPosition).getData();
        String userId = (String) tempRes.get("userId");
        String userIdAuth = currentUser.getUid();
        if (userIdAuth.equals(userId))
            return true;
        else
            return false;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

    }

    @Override
    public void onError(@NonNull FirebaseFirestoreException e) {
        super.onError(e);
        showMessage(e.getMessage());
    }

    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtContent, txtDate;
        CircleImageView imgUser;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.comment_user_name);
            txtContent = itemView.findViewById(R.id.comment_content);
            txtDate = itemView.findViewById(R.id.comment_date);
            imgUser = itemView.findViewById(R.id.comment_user_photo);
        }
    }
}
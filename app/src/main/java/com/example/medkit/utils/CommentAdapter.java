package com.example.medkit.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.medkit.R;
import com.example.medkit.model.Comment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder> {
    Context mContext;
    FirebaseStorage storageInstance = FirebaseStorage.getInstance();
    StorageReference storageRef;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection = db.collection("Users");
    String content, userName, userID;
    Intent intent;
    Comment tempModel;
    OnCommentClickListener commentClickListener;

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, Context mContext, OnCommentClickListener commentClickListener) {
        super(options);
        this.mContext = mContext;
        this.commentClickListener = commentClickListener;
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
    protected void onBindViewHolder(@NonNull final CommentViewHolder holder, int position, @NonNull Comment model) {
        tempModel = getSnapshots().getSnapshot(holder.getAdapterPosition()).toObject(Comment.class);
        content = tempModel.getContent();
        userName = tempModel.getUserName();
        userID = tempModel.getUserId();
        storageRef = storageInstance.getReference("userPhoto/" + userID);
        RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));
        /*Glide.with(mContext).load(storageRef)
                .apply(requestOptions)
                .into(holder.imgUser);*/
        GlideApp.with(mContext).load(storageRef).into(holder.imgUser);
        holder.txtContent.setText(content);
        holder.txtUserName.setText(userName);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM h:mm a", Locale.getDefault());
        holder.txtDate.setText(sdf.format(tempModel.getCreatedTime()));
       /* usersCollection.document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("fullName");
                    holder.txtUserName.setText(userName);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
                Log.d("TAG", "onFailure: "+e.getMessage());
            }
        });*/
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

    public interface OnCommentClickListener {
        void onUserClick(String userId);

        void onClappingClick(String userId);
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtUserName, txtContent, txtDate;
        CircleImageView imgUser;
        ImageButton clapBtn;
        Comment tempComment;
        DocumentSnapshot documentSnapshot;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.comment_user_name);
            txtContent = itemView.findViewById(R.id.comment_content);
            txtDate = itemView.findViewById(R.id.comment_date);
            imgUser = itemView.findViewById(R.id.comment_user_photo);
            clapBtn = itemView.findViewById(R.id.clapping_btn);
            imgUser.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
            tempComment = documentSnapshot.toObject(Comment.class);
            switch (view.getId()) {
                case R.id.comment_user_photo:
                    commentClickListener.onUserClick(tempComment.getUserId());
                    break;
                case R.id.clapping_btn:
                    if (!view.isSelected())
                        view.setSelected(true);


                    commentClickListener.onClappingClick(tempComment.getUserId());
                    break;
            }
        }
    }


}

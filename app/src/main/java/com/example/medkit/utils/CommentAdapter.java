package com.example.medkit.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    int currentUserClapping = 0;
    Map<String, Integer> mapClapping;
    String currentUserId;
    SimpleDateFormat sdf;
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
        currentUserClapping = 0;
        tempModel = getSnapshots().getSnapshot(holder.getAdapterPosition()).toObject(Comment.class);
        content = tempModel.getContent();
        userName = tempModel.getUserName();
        userID = tempModel.getUserId();
        storageRef = storageInstance.getReference("userPhoto/" + userID);
        mapClapping = tempModel.getClappings();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        if (mapClapping.containsKey(currentUserId))
            currentUserClapping = mapClapping.get(currentUserId);
        if (currentUserClapping == 1)
            holder.clapBtn.setSelected(true);
        else
            holder.clapBtn.setSelected(false);
        computeVotes(holder);
        //RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));
        /*Glide.with(mContext).load(storageRef)
                .apply(requestOptions)
                .into(holder.imgUser);*/
        GlideApp.with(mContext).load(storageRef).into(holder.imgUser);
        holder.txtContent.setText(content);
        holder.txtUserName.setText(userName);
        sdf = new SimpleDateFormat("d MMM h:mm a", Locale.getDefault());
        //holder.txtClappingCounter.setText("+"+String.valueOf(tempModel.getClappingCounter()));
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

    private void computeVotes(CommentViewHolder holder) {
        int cntrClapping = 0;
        for (String tempStrKey : mapClapping.keySet()) {
            if (mapClapping.get(tempStrKey) == 1)
                cntrClapping++;
        }
        holder.txtClappingCounter.setText("+" + String.valueOf(cntrClapping));
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

        void onClappingClick(DocumentSnapshot documentSnapshot, String userId);
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtUserName, txtContent, txtDate, txtClappingCounter;
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
            txtClappingCounter = itemView.findViewById(R.id.clapping_counter);
            imgUser.setOnClickListener(this);
            clapBtn.setOnClickListener(this);
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
                    view.setSelected(!view.isSelected());
                    commentClickListener.onClappingClick(documentSnapshot, tempComment.getUserId());
                    break;
            }
        }
    }


}

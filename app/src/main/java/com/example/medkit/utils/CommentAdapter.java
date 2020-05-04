package com.example.medkit.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.medkit.R;
import com.example.medkit.activities.ProfileActivity;
import com.example.medkit.model.Comment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder> {
    Context mContext;
    FirebaseStorage storageInstance = FirebaseStorage.getInstance();
    StorageReference storageRef;

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
        String userName = model.getUserName();
        /*Timestamp temp = (Timestamp) getSnapshots().getSnapshot(position).getData().get("createdTime");
        Date tempDate = temp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        String createdTime = dateFormat.format(tempDate);*/
        Timestamp tempStamp = model.getCreatedTime();
        Date tempDate = tempStamp.toDate();
        String createdTime = new SimpleDateFormat("dd MMMM").format(tempDate);
        Log.d("TAG", "onBindViewHolder comment: " + createdTime);
        String content = model.getContent();
        //String userImage = model.getUserImage();
        storageRef = storageInstance.getReference("userPhoto/" + model.getUserId());
        holder.txtUserName.setText(userName);
        holder.txtContent.setText(content);
        holder.txtDate.setText(createdTime);
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

    private void updateUI() {
        //TODO profile of concurrent user
        mContext.startActivity(new Intent(mContext, ProfileActivity.class));
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

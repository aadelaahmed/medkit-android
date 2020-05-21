package com.example.medkit.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.medkit.R;
import com.example.medkit.fragments.NotificationFragment;
import com.example.medkit.model.NotificationModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    StorageReference storageUsers;
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    private List<NotificationModel> notificationList;
    private Context context;

    public NotificationAdapter(List<NotificationModel> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }


    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.notification_layout,parent,false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final NotificationModel notification = notificationList.get(position);
        String from = notification.getFrom();
        final String message = notification.getMessage();
        FirebaseFirestore mfirebase = FirebaseFirestore.getInstance();
//        Log.d("notification", "onBindViewHolder: "+notification.getFrom());
        mfirebase.collection("Users").document(from).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String name = (String) documentSnapshot.get("fullName");
                String content = "<b>" + name + "</b> " + message.substring(0, 26) + ": <br> " + message.substring(26);
                holder.contentTV.setText(Html.fromHtml(content));
                String image = (String) documentSnapshot.get("photoUrl");
                holder.timeTv.setText(notification.getTime());
                Log.d("notification", "onBindViewHolder Image: " + image);
                storageUsers = storageRef.getReference().child("userPhoto/" + notification.getFrom());
                Glide.with(context).load(storageUsers).into(holder.userProfilePicture);
            }
        });

        holder.container.setBackgroundResource(R.color.white);
        if (!notification.isRead()) {
//            Log.d("notification", "onBindViewHolder: "+notification.isRead());
            holder.container.setBackgroundResource(R.color.textNotActiveColor);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.setRead(true, notification.getN_id());
                notifyItemChanged(position);
                NotificationFragment.updateUnRead();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout container;
        TextView contentTV, timeTv;
        CircleImageView userProfilePicture;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTV = itemView.findViewById(R.id.notification_content_tv);
            userProfilePicture = itemView.findViewById(R.id.notification_user_photo);
            container = itemView.findViewById(R.id.notification_container);
            timeTv = itemView.findViewById(R.id.notification_time);

        }
    }


}

package com.example.medkit.utils;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.activities.PostDetail;
import com.example.medkit.fragments.NotificationFragment;
import com.example.medkit.model.NotificationModel;
import com.example.medkit.model.PostModel;
import com.example.medkit.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
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
        final FirebaseFirestore mfirebase = FirebaseFirestore.getInstance();
//        Log.d("notification", "onBindViewHolder: "+notification.getFrom());
        mfirebase.collection(User.USER_COLLECTION).document(from).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String name = (String) documentSnapshot.get("fullName");
                String content = "<b>" + name + "</b> " + message;
                holder.contentTV.setText(Html.fromHtml(content));
                String image = (String) documentSnapshot.get("photoUrl");
                SimpleDateFormat sdf = new SimpleDateFormat("d MMM h:mm a", Locale.getDefault());
                holder.timeTv.setText(sdf.format(notification.getCreatedTime()));
                Log.d("notification", "onBindViewHolder Image: " + image);
                storageUsers = storageRef.getReference().child(User.USER_IMAGES_STORAGE + "/" + notification.getFrom());
                GlideApp.with(context).load(storageUsers).into(holder.userProfilePicture);
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
                if (!notification.isRead()) {
                    notification.setRead(true, notification.getN_id());
                    notifyItemChanged(position);
//                notifyDataSetChanged();
                    NotificationFragment.updateUnRead();
//                Toast.makeText(context,notification.getPost_id(),Toast.LENGTH_LONG).show();
                }
                mfirebase.collection(PostModel.POST_COLLECTION).document(notification.getPost_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        PostModel post = documentSnapshot.toObject(PostModel.class);
                        Intent intent = new Intent(context, PostDetail.class);
                        intent.putExtra(PostModel.OBJECT_KEY, post);
                        if (post.getPostPhoto() == null)
                            intent.putExtra(PostModel.POST_IMAGE_FLAG, false);
                        else {
                            intent.putExtra(PostModel.POST_IMAGE_FLAG, true);
                        }
                        context.startActivity(intent);
                    }
                });
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

package com.example.medkit.utils;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medkit.R;
import com.example.medkit.fragments.NotificationFragment;
import com.example.medkit.model.NotificationModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationModel> mNotifications;
    public NotificationAdapter(List<NotificationModel> notifications) {
        this.mNotifications = notifications;
    }
    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.notification_layout,parent,false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        TextView contentTV;
        ImageView userProfilePicture;
        ConstraintLayout container;

        final NotificationModel notification = mNotifications.get(position);
        contentTV = holder.contentTV;
        userProfilePicture = holder.userProfilePicture;
        container = holder.container;
        String content = "<b>" + notification.getUserName() + "</b> " + notification.getNotificationContent();
        contentTV.setText(Html.fromHtml(content));

//        userProfilePicture.setImageBitmap(notification.getUserPhoto());
        if(notification.isRead()){
            container.setBackgroundResource(R.color.colorHintText);
        }else
        {
            container.setBackgroundResource(R.color.white);
        }

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notification.setRead(true);
                notifyItemChanged(position);
                NotificationFragment.updateUnRead();
            }
        });



    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout container;
        TextView contentTV;
        ImageView userProfilePicture;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTV = itemView.findViewById(R.id.notification_content_tv);
            userProfilePicture = itemView.findViewById(R.id.notification_user_photo);
            container = itemView.findViewById(R.id.notification_container);

        }
    }

}

package com.example.medkit.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medkit.R;
import com.example.medkit.model.PostModel;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView userNameTV,titleTV,contentTV,categoryTV;
        ImageView userProfilePicture, image;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTV = itemView.findViewById(R.id.post_user_name);
            titleTV = itemView.findViewById(R.id.post_title_tv);
            contentTV = itemView.findViewById(R.id.post_content_tv);
            categoryTV = itemView.findViewById(R.id.post_category_tv);
            userProfilePicture = itemView.findViewById(R.id.post_user_profile_picture);
            image = itemView.findViewById(R.id.post_image);
        }
    }
    private List<PostModel> mPosts;
    public PostAdapter(List<PostModel> posts) {
        this.mPosts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.post_layout,parent,false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        PostModel post = mPosts.get(position);
        TextView userNameTV,titleTV,contentTV,categoryTV;
        userNameTV = holder.userNameTV;
        userNameTV.setText(post.getUserName());
        titleTV = holder.titleTV;
        titleTV.setText(post.getTitle());
        contentTV = holder.contentTV;
        contentTV.setText(post.getContent());
        categoryTV = holder.categoryTV;
        categoryTV.setText(post.getCategory());

//        ImageView userProfilePicture, image;
//        userProfilePicture = holder.userProfilePicture;
//        userProfilePicture.setImageBitmap(post.getUserProfilePicture());
//        image = holder.image;
//        image.setImageBitmap(post.getImage());
    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }
}

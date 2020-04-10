package com.example.medkit.utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medkit.R;
import com.example.medkit.model.PostModel;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView userNameTV,titleTV,contentTV,categoryTV, n_comments, commentTV;
        ImageView userProfilePicture, image;
        Button upVote,downVote;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTV = itemView.findViewById(R.id.post_user_name);
            titleTV = itemView.findViewById(R.id.post_title_tv);
            contentTV = itemView.findViewById(R.id.post_content_tv);
            categoryTV = itemView.findViewById(R.id.post_category_tv);
            userProfilePicture = itemView.findViewById(R.id.post_user_profile_picture);
            image = itemView.findViewById(R.id.post_image);
            upVote = itemView.findViewById(R.id.up_vote_btn);
            downVote = itemView.findViewById(R.id.down_vote_btn);
            n_comments = itemView.findViewById(R.id.n_comments_tv);
            commentTV = itemView.findViewById(R.id.comment_tv);
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
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, final int position) {
        final PostModel post = mPosts.get(position);
        //TextViews code
        TextView userNameTV,titleTV,contentTV,categoryTV, n_comments, commentTV;
        userNameTV = holder.userNameTV;
        userNameTV.setText(post.getUserName());
        titleTV = holder.titleTV;
        titleTV.setText(post.getTitle());
        contentTV = holder.contentTV;
        contentTV.setText(post.getDescription());
        categoryTV = holder.categoryTV;
        categoryTV.setText(post.getCategory());
        n_comments = holder.n_comments;
        n_comments.setText(post.getnComments()+" comments");
        commentTV = holder.commentTV;
        commentTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(),"clicked",Toast.LENGTH_SHORT).show();
            }
        });


        //Buttons code
        final int[] nUps = {post.getUpVotes()};
        final int[] nDowns = {post.getDownVotes()};
        final boolean isUp = post.isUpVoted();
        final boolean isDown = post.isDownVoted();
        final boolean[] isMin = {false};
        final Button upVote,downVote;
        upVote = holder.upVote;
        downVote = holder.downVote;
        upVote.setText(nUps[0] +" Up");
        downVote.setText(nDowns[0] +" Down");
        if(isUp){
            upVote.setBackgroundResource(R.color.textNotActiveColor);
            downVote.setBackgroundResource(R.color.transparent);
        }else if(isDown){
            downVote.setBackgroundResource(R.color.textNotActiveColor);
            upVote.setBackgroundResource(R.color.transparent);
        }else{
            upVote.setBackgroundResource(R.color.transparent);
            downVote.setBackgroundResource(R.color.transparent);
        }


        upVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUp){
                    post.setUpVoted(false);
                    post.setDownVoted(false);
                    post.setUpVotes(--nUps[0]);
                }else if(isDown){
                    post.setUpVoted(true);
                    post.setDownVoted(false);
                    post.setUpVotes(++nUps[0]);
                    post.setDownVotes(--nDowns[0]);
                }else{
                    post.setUpVoted(true);
                    post.setDownVoted(false);
                    post.setUpVotes(++nUps[0]);
                }
                notifyItemChanged(position);
                Log.e("up_vote", String.valueOf(post.isUpVoted()));
                Log.e("down_vote", String.valueOf(post.isDownVoted()));
            }
        });

        downVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDown){
                    post.setUpVoted(false);
                    post.setDownVoted(false);
                    post.setDownVotes(--nDowns[0]);
                }else if(isUp) {
                    post.setDownVoted(true);
                    post.setUpVoted(false);
                    post.setDownVotes(++nDowns[0]);
                    post.setUpVotes(--nUps[0]);
                }else{
                    post.setDownVoted(true);
                    post.setUpVoted(false);
                    post.setDownVotes(++nDowns[0]);
                }
                notifyItemChanged(position);
                Log.e("up_vote", String.valueOf(post.isUpVoted()));
                Log.e("down_vote", String.valueOf(post.isDownVoted()));
            }
        });

        //ImageViews code
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

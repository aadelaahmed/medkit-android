package com.example.medkit.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.medkit.R;
import com.example.medkit.model.PostModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference rootPosts = db.collection("Posts");
    DocumentReference clickedDocument;
    private Context mContext;
    private List<PostModel> mPosts;
    private PostModel post;

    public PostAdapter(List<PostModel> posts, Context mContext) {
        this.mPosts = posts;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.post_layout, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, final int position) {
        post = mPosts.get(position);
        //TextViews code

        TextView userNameTV, titleTV, contentTV, categoryTV, n_comments, commentTV;
        userNameTV = holder.userNameTV;
        userNameTV.setText(post.getUserName());
        titleTV = holder.titleTV;
        titleTV.setText(post.getTitle());
        contentTV = holder.contentTV;
        contentTV.setText(post.getDescription());
        categoryTV = holder.categoryTV;
        categoryTV.setText(post.getCategory());
        n_comments = holder.n_comments;
        n_comments.setText(post.getnComments() + " comments");
        holder.upVote.setText(post.getUpVotes() + " UP");
        holder.downVote.setText(post.getDownVotes() + " Down");
        if (post.getPostPhoto() != null)
            Glide.with(mContext).load(post.getPostPhoto()).into(holder.image);
        else
            holder.image.setVisibility(View.GONE);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPhoto();
            }
        });
        Glide.with(mContext).load(post.getUserPhoto()).into(holder.userProfilePicture);
        clickedDocument = rootPosts.document(post.getPostKey());
        //Buttons code
        final int[] nUps = {post.getUpVotes()};
        final int[] nDowns = {post.getDownVotes()};
        final boolean isUp = post.isUpVoted();
        final boolean isDown = post.isDownVoted();
        final boolean[] isMin = {false};
        Button upVote, downVote;
        upVote = holder.upVote;
        downVote = holder.downVote;
       /* upVote.setText(nUps[0] +" Up");
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
        }*/

        upVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FieldValue incrementUp = FieldValue.increment(1);
                clickedDocument.update("upVotes", incrementUp);
                holder.upVote.setText(post.getUpVotes() + "UP");
            }
        });

        downVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FieldValue incrementDown = FieldValue.increment(1);
                clickedDocument.update("downVotes", incrementDown);
                holder.downVote.setText(post.getDownVotes() + "Down");
            }
        });

       /* upVote.setOnClickListener(new View.OnClickListener() {
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
        });*/


        //ImageViews code
//        ImageView userProfilePicture, image;
//        userProfilePicture = holder.userProfilePicture;
//        userProfilePicture.setImageBitmap(post.getUserProfilePicture());
//        image = holder.image;
//        image.setImageBitmap(post.getImage());
    }

    private void clickPhoto() {
        Dialog settingsDialog = new Dialog(mContext);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_photo_post, null);
        ImageView clickedPhoto = view.findViewById(R.id.img_clicked_post);
        Glide.with(mContext).load(post.getPostPhoto()).into(clickedPhoto);
        settingsDialog.setContentView(view);
        settingsDialog.show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTV, titleTV, contentTV, categoryTV, n_comments;
        ImageView userProfilePicture, image;
        Button upVote, downVote;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTV = itemView.findViewById(R.id.post_user_name);
            titleTV = itemView.findViewById(R.id.post_title_tv);
            contentTV = itemView.findViewById(R.id.post_content_tv);
            categoryTV = itemView.findViewById(R.id.post_category_tv);
            userProfilePicture = itemView.findViewById(R.id.comment_user_photo);
            image = itemView.findViewById(R.id.post_image);
            upVote = itemView.findViewById(R.id.up_vote_btn);
            downVote = itemView.findViewById(R.id.down_vote_btn);
            n_comments = itemView.findViewById(R.id.n_comments_tv);

        }


    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }
}

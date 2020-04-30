package com.example.medkit.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.medkit.R;
import com.example.medkit.activities.PostDetail;
import com.example.medkit.model.PostModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomPostAdapter extends FirestoreRecyclerAdapter<PostModel, CustomPostAdapter.CustomHolder> {

    OnItemClickListener mListener;
    Context mContext;
    PostModel clickedPost;
    DocumentReference clickedDocument;
    int cntrUp, cntrDown;


    public CustomPostAdapter(@NonNull FirestoreRecyclerOptions<PostModel> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }
  /*public CustomPostAdapter(Context mContext)
    {
        super();
        this.mContext = mContext;
    }*/

    @Override
    protected void onBindViewHolder(@NonNull final CustomHolder holder, final int position, @NonNull PostModel model) {
        holder.txtTitle.setText(model.getTitle());
        holder.txtDescription.setText(model.getDescription());
        holder.txtCategory.setText(model.getCategory());
        Glide.with(mContext).load(model.getUserPhoto()).into(holder.imgUser);
        cntrUp = model.getUpVotes();
        cntrDown = model.getDownVotes();
        //cntrUp = model.getUpVotes();
        //cntrDown = model.getDownVotes();
        holder.btnUp.setText(cntrUp + " UP");
        holder.btnDown.setText(cntrDown + " Down");
        clickedPost = model;
      /*  Timestamp temp = (Timestamp) getSnapshots().getSnapshot(position).getData().get("currentDate");
        Date tempDate = temp.toDate();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        Log.d("TAG", "onClick: "+dateFormat.format(tempDate));*/
        holder.imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPhoto(position);
            }
        });
        if (model.getPostPhoto() != null)
            Glide.with(mContext).load(model.getPostPhoto()).into(holder.imgPost);
        else
            holder.imgPost.setVisibility(View.GONE);
        holder.txtUserName.setText(model.getUserName());
        Log.d("TAG", "onBindViewHolder: " + model.getUserName());
       // getSnapshots().getSnapshot(position).getMetadata().hasPendingWrites();

        holder.btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cntrUp =  model.getUpVotes();
                DocumentReference tempDoc = getSnapshots().getSnapshot(position).getReference();
                holder.btnUp.setText(cntrUp + " UP");
                FieldValue incrementUp = FieldValue.increment(1);
                tempDoc.update("upVotes", incrementUp);
                holder.btnUp.setText(++cntrUp + " UP");
               /* Timestamp temp = (Timestamp) getSnapshots().getSnapshot(position).getData().get("createdTime");
                Log.d("TAG", "onClick: "+temp.toString());
                Date tempDate = temp.toDate();
                Log.d("TAG", "onClick: "+tempDate.toString());
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
                Log.d("TAG", "onClick: "+dateFormat.format(tempDate));*/
            }
        });

        holder.btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cntrDown = model.getDownVotes();
                DocumentReference tempDownDoc = getSnapshots().getSnapshot(position).getReference();
                holder.btnUp.setText(cntrDown + " Down");
                FieldValue incrementDown = FieldValue.increment(1);
                tempDownDoc.update("downVotes", incrementDown);
                holder.btnDown.setText(++cntrDown + " Down");
            }
        });
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new CustomHolder(view);
    }

    private void clickPhoto(int position) {
        Dialog settingsDialog = new Dialog(mContext);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_photo_post, null);
        ImageView clickedPhoto = view.findViewById(R.id.img_clicked_post);
        Glide.with(mContext).load(getSnapshots().getSnapshot(position).get("postPhoto")).into(clickedPhoto);
        settingsDialog.setContentView(view);
        settingsDialog.show();
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, Context mContext);
    }

   /* private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("Mon-DD",calendar).toString();
        return date;
    }*/

    public class CustomHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtDescription, txtCategory, txtUserName, txtTime;
        ImageView imgUser;
        ImageView imgPost;
        Button btnUp, btnDown;

        public CustomHolder(@NonNull final View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.post_title_tv);
            txtDescription = itemView.findViewById(R.id.post_content_tv);
            txtCategory = itemView.findViewById(R.id.post_category_tv);
            imgUser = itemView.findViewById(R.id.comment_user_photo);
            imgPost = itemView.findViewById(R.id.post_image);
            txtUserName = itemView.findViewById(R.id.post_user_name);
            btnUp = itemView.findViewById(R.id.up_vote_btn);
            btnDown = itemView.findViewById(R.id.down_vote_btn);
            txtTime = itemView.findViewById(R.id.post_title_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(mContext, PostDetail.class);
                        //mListener = new PostDetail();
                        PostModel clickedPost = getSnapshots().getSnapshot(position).toObject(PostModel.class);
                        //startActivity(new Intent(mContext,PostDetail.class));
                        if (clickedPost.getPostPhoto() == null)
                            intent.putExtra(PostModel.POST_IMAGE_FLAG, false);
                        else {
                            intent.putExtra(PostModel.POST_IMAGE_FLAG, true);
                            intent.putExtra(PostModel.POST_IMAGE_KEY, clickedPost.getPostPhoto());
                        }
                        String title = clickedPost.getTitle();
                        String description = clickedPost.getDescription();
                        //String createdTime = clickedPost.getCreatedTime().toString();
                        String userPhoto = clickedPost.getUserPhoto();

                        //To get current time of added post
                        Timestamp temp = (Timestamp) getSnapshots().getSnapshot(position).getData().get("createdTime");
                        Date tempDate = temp.toDate();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
                        String createdTime = dateFormat.format(tempDate);
                        //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
                        Log.d("TAG", "onClick: " + dateFormat.format(tempDate));
                        //String postKey = getSnapshots().getSnapshot(position).getId();
                        String postKey = clickedPost.getPostKey();
                        //String dateWithName = createdTime + " | by " + userName;
                        intent.putExtra(PostModel.POST_KEY, postKey);
                        intent.putExtra(PostModel.TITLE_KEY, title);
                        intent.putExtra(PostModel.DESCRIPTION_KEY, description);
                        //intent.putExtra(PostModel.USER_NAME_KEY,dateWithName);
                        intent.putExtra(PostModel.USER_IMAGE_KEY, userPhoto);
                        intent.putExtra(PostModel.TIME_KEY, createdTime);
                        //mListener.onItemClick(getSnapshots().getSnapshot(position),mContext);
                        //mContext.startActivity(new Intent(mContext,PostDetail.class));
                        //mContext.startActivity(new Intent(mContext,PostDetail.class));
                        mContext.startActivity(intent);
                    }
                }
            });
            //Toast.makeText(mContext,getSnapshots().size(),Toast.LENGTH_LONG).show();
        }

    }

   /* public void setOnItemClickListener(OnItemClickListener mListener)
    {
        this.mListener = mListener;
    }*/


}

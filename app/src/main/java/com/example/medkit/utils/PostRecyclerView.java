package com.example.medkit.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medkit.R;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostRecyclerView extends FirestorePostAdapter<PostRecyclerView.CustomPostViewHolder> {

    Context mContext;
    Query mQuery;

    public PostRecyclerView(Context mContext, Query mQuery) {
        super(mQuery);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PostRecyclerView.CustomPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false);
        return new CustomPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomPostViewHolder holder, int position) {

    }


    class CustomPostViewHolder extends RecyclerView.ViewHolder {

        public CustomPostViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

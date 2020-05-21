package com.example.medkit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.medkit.R;
import com.example.medkit.activities.ChatActivity;
import com.example.medkit.model.User;
import com.example.medkit.utils.FirestoreAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessageFragment extends Fragment {
    private FirestoreAdapter firestoreAdapter;

    public MessageFragment() {
    }
    @Override
    public void onStart() {
        super.onStart();
        if (firestoreAdapter != null) {
            firestoreAdapter.startListening();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (firestoreAdapter != null) {
            firestoreAdapter.stopListening();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        firestoreAdapter = new RecyclerViewAdapter(FirebaseFirestore.getInstance().collection("Users"));
        RecyclerView recyclerView = view.findViewById(R.id.chat_recycler_view);
        recyclerView.setLayoutManager( new LinearLayoutManager((inflater.getContext())));
        recyclerView.setAdapter(firestoreAdapter);
        return view;
    }
    class RecyclerViewAdapter extends FirestoreAdapter<CustomViewHolder> {
        final private RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));
        private StorageReference storageReference;
        private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RecyclerViewAdapter(Query query) {
            super(query);
            storageReference  = FirebaseStorage.getInstance().getReference();
        }
        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CustomViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false));
        }
        @Override
        public void onBindViewHolder(CustomViewHolder viewHolder, int position) {
            DocumentSnapshot documentSnapshot = getSnapshot(position);
            final User user = documentSnapshot.toObject(User.class);
            if (myUid.equals(user.getUid())) {
                viewHolder.itemView.setVisibility(View.INVISIBLE);
                viewHolder.itemView.getLayoutParams().height = 0;
                return;
            }
            viewHolder.user_name.setText(user.getFullName());
            if (user.getUid()==null) {
                Glide.with(getActivity()).load(R.drawable.com_facebook_profile_picture_blank_portrait)
                        .apply(requestOptions)
                        .into(viewHolder.user_photo);
            } else{
                Glide.with(getActivity())
 //                       .load(storageReference.child("userPhoto/"+user.getPhotoUrl()+".jpg"))
                        .load(storageReference.child("users/man"+".jpg"))
                        .apply(requestOptions)
                        .into(viewHolder.user_photo);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getView().getContext(), ChatActivity.class);
                    intent.putExtra("toUid", user.getUid());
                    intent.putExtra("roomTitle",user.getFullName());
                    intent.putExtra("userPhoto",user.getUid());
                    startActivity(intent);
                }
            });

        }

    }
    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView user_photo;
        public TextView user_name;

        CustomViewHolder(View view) {
            super(view);
            user_photo = view.findViewById(R.id.user_photo);
            user_name = view.findViewById(R.id.user_name);
        }
    }
   }
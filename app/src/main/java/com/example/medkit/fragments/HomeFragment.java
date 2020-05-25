package com.example.medkit.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.activities.AddPostActivity;
import com.example.medkit.activities.PostDetail;
import com.example.medkit.activities.ProfileActivity;
import com.example.medkit.databinding.FragmentHomeBinding;
import com.example.medkit.model.PostModel;
import com.example.medkit.model.User;
import com.example.medkit.utils.CustomPostAdapter;
import com.example.medkit.utils.GlideApp;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment implements CustomPostAdapter.OnPostLitener {
    RecyclerView recyclerPosts;
    private FragmentHomeBinding binding;
    public Context mContext;
    CollectionReference rootPost = FirebaseFirestore.getInstance().collection(PostModel.POST_COLLECTION);
    CustomPostAdapter customPostAdapter;
    Intent intent;
    private static final String TAG = "HomeFragment";
    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    StorageReference storagePosts;
    StorageReference storageUsers;
    CollectionReference rootUsers = FirebaseFirestore.getInstance().collection(User.USER_COLLECTION);
    User clickUser;
    Map<String, Integer> newMapValue, postVotes;
    int newVote, tempCurrentVote;
    PostModel tempPost;

    public HomeFragment() {

    }

    public HomeFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ArrayAdapter<CharSequence> diseases = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.category_spinner,
                android.R.layout.simple_spinner_item);
        diseases.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(diseases);
        binding.postTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AddPostActivity.class));
            }
        });
        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        iniRecyclerView();
        return view;
    }

    private void iniRecyclerView() {
        Query query = rootPost.orderBy("createdTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostModel> tempOption = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();
        recyclerPosts = binding.postsList;
        customPostAdapter = new CustomPostAdapter(tempOption, mContext, this);
        recyclerPosts.setAdapter(customPostAdapter);
        recyclerPosts.setLayoutManager(new LinearLayoutManager(mContext));
        ((SimpleItemAnimator) recyclerPosts.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        customPostAdapter.startListening();
       /* if (newAdapter != null)
            newAdapter.startListener();*/
        //binding.postsList.setAdapter(tempAdapter);
        //showMessage("start state");
        //recyclerPosts.setAdapter(tempAdapter);
        //tempAdapter.startListening();
       /* rootPost.orderBy("createdTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            showMessage("something get wrong while fetching real time posts' data");
                            return;
                        }
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            int newIndex = documentChange.getNewIndex();
                            int oldIndex = documentChange.getOldIndex();
                            DocumentSnapshot tempDoc = documentChange.getDocument();
                            PostModel currentPost = tempDoc.toObject(PostModel.class);
                            switch (documentChange.getType()) {
                                case ADDED:
                                    posts.add(0, currentPost);
                                    break;
                                case REMOVED:
                                    posts.remove(oldIndex);
                                    break;
                                case MODIFIED:
                                    Long cntrVotes = tempDoc.getLong("votes");
                                    break;
                            }
                        }
                        recyclerPosts.setAdapter(newAdapter);
                    }
                });*/
       /* rootPost.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    showMessage("something get wrong while fetching real time posts' data");
                    return;
                }
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    int newIndex = documentChange.getNewIndex();
                    int oldIndex = documentChange.getOldIndex();
                    DocumentSnapshot tempDoc = documentChange.getDocument();
                    PostModel currentPost = tempDoc.toObject(PostModel.class);
                    switch (documentChange.getType()) {
                        case ADDED:
                            posts.add(0, currentPost);
                            break;
                        case REMOVED:
                            posts.remove(oldIndex);
                            break;
                        case MODIFIED:
                            posts.add(newIndex, currentPost);
                            posts.remove(oldIndex);
                            upVotes = (int) tempDoc.get("upVotes");
                            downVotes = (int) tempDoc.get("downVotes");
                            posts.get(oldIndex).setUpVotes(upVotes);
                            posts.get(oldIndex).setDownVotes(downVotes);
                            break;
                    }
                }

                PostAdapter newAdapter = new PostAdapter(posts, mContext);
                binding.postsList.setAdapter(newAdapter);
            }
        });*/
    }

    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //showMessage("onActivityCreated");
        //iniRecyclerView();
    }

    @Override
    public void onStop() {
        super.onStop();
        customPostAdapter.stopListening();
       /* if (newAdapter != null)
            newAdapter.stopListener();*/
        //showMessage("stop state");
        //tempAdapter.stopListening();
        // mListener.remove();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }


    @Override
    public void onPostClick(PostModel clickedPost) {
        intent = new Intent(getActivity(), PostDetail.class);
        intent.putExtra(PostModel.OBJECT_KEY, clickedPost);
        if (clickedPost.getPostPhoto() == null)
            intent.putExtra(PostModel.POST_IMAGE_FLAG, false);
        else {
            intent.putExtra(PostModel.POST_IMAGE_FLAG, true);
        }
        startActivity(intent);
    }

    @Override
    public void onUpVoteClick(DocumentSnapshot documentSnapshot, String currentUserID) {
        tempPost = documentSnapshot.toObject(PostModel.class);
        postVotes = tempPost.getUpVotes();
        tempCurrentVote = 0;
        if (postVotes.containsKey(currentUserID))
            tempCurrentVote = postVotes.get(currentUserID);
        int newVote = (tempCurrentVote == 1) ? 0 : 1;
        Log.d("TAG", "addUpVote: tuesday" + tempCurrentVote);
        //tempCurrentVote = newVote;
        Log.d("TAG", "addUpVote: tuesday" + tempCurrentVote);
        newMapValue = new HashMap<>();
        newMapValue.put(currentUserID, newVote);
        documentSnapshot.getReference().update(PostModel.UP_VOTES, newMapValue);
    }

    @Override
    public void onDownVoteClick(DocumentSnapshot documentSnapshot, String currentUserID) {
        tempPost = documentSnapshot.toObject(PostModel.class);
        postVotes = tempPost.getUpVotes();
        tempCurrentVote = 0;
        if (postVotes.containsKey(currentUserID))
            tempCurrentVote = postVotes.get(currentUserID);
        newVote = (tempCurrentVote == -1) ? 0 : -1;
        Log.d("TAG", "addDownVote: tuesday" + tempCurrentVote);
        //currentUserVote = newVote;
        Log.d("TAG", "addDownVote: tuesday" + tempCurrentVote);
        newMapValue = new HashMap<>();
        newMapValue.put(currentUserID, newVote);
        documentSnapshot.getReference().update(PostModel.UP_VOTES, newMapValue);
    }

    @Override
    public void onUserNameClick(PostModel clickedPost) {
        intent = new Intent(mContext, ProfileActivity.class);
        //try onComplete instead of onsuccess
        rootUsers.whereEqualTo(User.USER_ID, clickedPost.getUserID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    clickUser = queryDocumentSnapshot.toObject(User.class);
                    Log.d(TAG, "onEvent: " + clickUser.toString());
                    intent.putExtra(User.OBJECT_KEY, clickUser);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });

        /*mListener =
        rootPost.whereEqualTo("userid",clickedPost.getUserID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    if (e != null)
                    {
                        showMessage(e.getMessage());
                        Log.d(TAG, "onEvent: "+e.getMessage());
                        return;
                    }
                    try {
                        wait(10000);
                        clickUser = documentSnapshot.toObject(User.class);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    Log.d(TAG, "onEvent: "+clickUser.toString());
                    intent.putExtra(User.OBJECT_KEY, clickUser);
                    startActivity(intent);
                }
            }
        });*/
    }

    @Override
    public void onPostImageClick(String postKey) {
        storagePosts = storageRef.getReference().child(PostModel.POST_IMAGES_STORAGE + "/" + postKey);
        Dialog settingsDialog = new Dialog(mContext);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_photo_post, null);
        ImageView clickedPhoto = view.findViewById(R.id.img_clicked_post);
        GlideApp.with(mContext).load(storagePosts).into(clickedPhoto);
        settingsDialog.setContentView(view);
        settingsDialog.show();
    }

}


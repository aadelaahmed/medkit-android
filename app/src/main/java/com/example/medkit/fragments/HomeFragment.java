package com.example.medkit.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.activities.AddPostActivity;
import com.example.medkit.activities.PostDetail;
import com.example.medkit.databinding.FragmentHomeBinding;
import com.example.medkit.model.PostModel;
import com.example.medkit.utils.CustomPostAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment implements CustomPostAdapter.OnPostLitener {
    RecyclerView recyclerPosts;
    private List<PostModel> posts;
    private FragmentHomeBinding binding;
    public Context mContext;
    CollectionReference rootPost = FirebaseFirestore.getInstance().collection(PostModel.POST_COLLECTION);
    CustomPostAdapter customPostAdapter;
    Intent intent;
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
        startActivity(intent);
    }
}


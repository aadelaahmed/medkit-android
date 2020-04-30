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
import com.example.medkit.databinding.FragmentHomeBinding;
import com.example.medkit.model.PostModel;
import com.example.medkit.utils.CustomPostAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment {
    RecyclerView recyclerPosts;
    private List<PostModel> posts;
    private FragmentHomeBinding binding;
    public Context mContext;
    CollectionReference rootPost = FirebaseFirestore.getInstance().collection("Posts");
    CustomPostAdapter tempAdapter;

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
        posts = new ArrayList<>();
       /* Bitmap ppbitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_type_user);
        Bitmap ibitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_communicate);
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,true,false));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,false,true));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,true,true));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,false,false));

        postAdapter = new PostAdapter(posts,mContext);

        binding.postsList.setAdapter(postAdapter);
        binding.postsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.postsList.setHasFixedSize(true);*/
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
        recyclerPosts = view.findViewById(R.id.posts_list);
        iniRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //iniRecyclerView();
    }
    private void iniRecyclerView() {
        Query query = rootPost.orderBy("createdTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostModel> tempOption = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();

        tempAdapter = new CustomPostAdapter(tempOption, mContext);
        recyclerPosts.setAdapter(tempAdapter);
        //recyclerPosts.setHasFixedSize(true);
        recyclerPosts.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerPosts.getItemAnimator().setChangeDuration(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        //binding.postsList.setAdapter(tempAdapter);
        //showMessage("start state");
        //recyclerPosts.setAdapter(tempAdapter);
        tempAdapter.startListening();
      /*  mListener =
                rootPost.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                       /* posts.add(newIndex,currentPost);
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
        //showMessage("stop state");
        tempAdapter.stopListening();
        // mListener.remove();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }


}


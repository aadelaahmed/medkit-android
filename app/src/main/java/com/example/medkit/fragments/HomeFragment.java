package com.example.medkit.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.medkit.utils.PostAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment {
    private List<PostModel> posts;
    private FragmentHomeBinding binding;
    public Context mContext;
    CollectionReference rootPost = FirebaseFirestore.getInstance().collection("Posts");

    public HomeFragment(Context mContext) {
        this.mContext = mContext;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        posts = new ArrayList<>();
        Bitmap ppbitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_type_user);
        Bitmap ibitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_communicate);
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,true,false));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,false,true));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,true,true));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,false,false));

        PostAdapter postAdapter = new PostAdapter(posts);
        binding.postsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.postsList.setAdapter(postAdapter);
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
                Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.postsList.getItemAnimator().setChangeDuration(0);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        rootPost.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null)
                    return;

                for (QueryDocumentSnapshot resDoc : queryDocumentSnapshots) {
                    PostModel tempPost = resDoc.toObject(PostModel.class);

                }
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

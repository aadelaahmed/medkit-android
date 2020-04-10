package com.example.medkit.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivityProfileBinding;
import com.example.medkit.model.PostModel;
import com.example.medkit.utils.PostAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private List<PostModel> posts;
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        posts = new ArrayList<>();
        Bitmap ppbitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_type_user);
        Bitmap ibitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_communicate);
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, true, false));
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, false, true));
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, true, true));
        posts.add(new PostModel("Ahmed Medra", "new Post", "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.", "Diabetes", ppbitmap, ibitmap, 10, 10, 10, false, false));


        PostAdapter postAdapter = new PostAdapter(posts);
        binding.profilePostsContainer.setLayoutManager(new LinearLayoutManager(this));
        binding.profilePostsContainer.setAdapter(postAdapter);
        binding.profilePostsContainer.getItemAnimator().setChangeDuration(0);
    }
}

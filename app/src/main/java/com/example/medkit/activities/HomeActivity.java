package com.example.medkit.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.medkit.R;
import com.example.medkit.model.PostModel;
import com.example.medkit.utils.PostAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class HomeActivity extends AppCompatActivity {
    List<PostModel> posts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        RecyclerView rvPosts = findViewById(R.id.posts_list);
        posts = new ArrayList<>();
        Bitmap ppbitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_type_user);
        Bitmap ibitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_communicate);
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap));

        PostAdapter postAdapter = new PostAdapter(posts);
        rvPosts.setAdapter(postAdapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
    }
}

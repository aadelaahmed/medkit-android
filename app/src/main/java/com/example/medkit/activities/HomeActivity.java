package com.example.medkit.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivityHomeBinding;
import com.example.medkit.model.PostModel;
import com.example.medkit.utils.PostAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class HomeActivity extends AppCompatActivity {
    List<PostModel> posts;
    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        posts = new ArrayList<>();
        Bitmap ppbitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_type_user);
        Bitmap ibitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_communicate);
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,true,false));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,false,true));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,true,true));
        posts.add(new PostModel("Ahemd Medra","new Post","Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.","Diabetes",ppbitmap,ibitmap,10,10,10,false,false));


        PostAdapter postAdapter = new PostAdapter(posts);
        binding.postsList.setAdapter(postAdapter);
        binding.postsList.setLayoutManager(new LinearLayoutManager(this));
        ArrayAdapter<CharSequence> diseases = ArrayAdapter.createFromResource(
                                                this,
                                                R.array.category_spinner,
                                                android.R.layout.simple_spinner_item);
        diseases.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(diseases);
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

    }
}

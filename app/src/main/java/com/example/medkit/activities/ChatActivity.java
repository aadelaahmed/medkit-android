package com.example.medkit.activities;

import android.Manifest;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.medkit.R;
import com.example.medkit.fragments.ChatFragment;
import com.example.medkit.utils.Util9;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    ChatFragment chatFragment;
    CircleImageView userImage;
    final private RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
        userImage=findViewById(R.id.user_photo);
        String toUid = getIntent().getStringExtra("toUid");
        final String roomID = getIntent().getStringExtra("roomID");
        String roomTitle = getIntent().getStringExtra("roomTitle");
        String userPhoto=getIntent().getStringExtra("userPhoto");
        if (roomTitle!=null) {
            actionBar.setTitle(roomTitle);
        }
        if (userPhoto==null) {
            Glide.with(ChatActivity.this).load(R.drawable.com_facebook_profile_picture_blank_portrait)
                    .apply(requestOptions)
                    .into(userImage);
        } else{
            Glide.with(ChatActivity.this)
                    .load(userPhoto)
                    .apply(requestOptions)
                    .into(userImage);
        }
        TextView chatName=findViewById(R.id.user_name);
        chatName.setText(roomTitle);
        // chatting area
        chatFragment = ChatFragment.getInstance(toUid, roomID);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, chatFragment )
                .commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        chatFragment.backPressed();
        finish();;
    }


}



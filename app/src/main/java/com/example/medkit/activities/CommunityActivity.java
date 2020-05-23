package com.example.medkit.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.medkit.R;
import com.example.medkit.databinding.ActivityCommunityBinding;
import com.example.medkit.fragments.HomeFragment;
import com.example.medkit.fragments.MessageFragment;
import com.example.medkit.fragments.NotificationFragment;
import com.example.medkit.model.NotificationModel;
import com.example.medkit.model.User;
import com.example.medkit.utils.GlideApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class CommunityActivity extends AppCompatActivity {


    private ActivityCommunityBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String currentUserId;
    Intent intent;
    FirebaseStorage storageInstance;
    StorageReference storageRef;
    DatabaseReference databaseReference;
    private BottomNavigationView.OnNavigationItemSelectedListener listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFreagment = null;
                    switch (item.getItemId()) {
                        case R.id.home_item:
                            selectedFreagment = new HomeFragment(CommunityActivity.this);
                            binding.txtMedkit.setText("medkit");
                            break;
                        case R.id.notify_item:
                            selectedFreagment = new NotificationFragment();
                            binding.txtMedkit.setText("notification");
                            break;
                        case R.id.message_item:
                            selectedFreagment = new MessageFragment();
                            binding.txtMedkit.setText("messaging");
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFreagment).commit();
                    return true;
                }
            };
    private FirebaseFirestore mfirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityCommunityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        storageInstance = FirebaseStorage.getInstance();
        storageRef = storageInstance.getReference(User.USER_IMAGES_STORAGE + "/" + currentUser.getUid());
        iniActionBar();
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(listener);
        binding.viewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CommunityActivity.this, ProfileActivity.class);
                intent.putExtra(User.USER_ID, currentUserId);
                startActivity(intent);
            }
        });


        //end bar
        // testing notification
        final FirebaseFirestore mfirebase = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(User.USER_COLLECTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_id);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
        mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                final String token_id = getTokenResult.getToken();
                String current_id = mAuth.getCurrentUser().getUid();
                Map<String, Object> tokenMap = new HashMap<>();
                tokenMap.put("token_id", token_id);
                mfirebase.collection(User.USER_COLLECTION).document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.w("token", token_id);
//                        Toast.makeText(CommunityActivity.this,"token" + token_id,Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("token", token_id);
//                        Toast.makeText(CommunityActivity.this,"FFFFFFFFFFFFFFFF" + token_id,Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        fetchData();
        // end notification

        //TODO --> add badge to notification and backlight on item click

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(this)).commit();
    }

    private void fetchData() {
        mfirebase = FirebaseFirestore.getInstance();
        mfirebase.collection(User.USER_IMAGES_STORAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Notification").orderBy("createdTime").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                    NotificationModel notificationModel = doc.getDocument().toObject(NotificationModel.class);
                    Log.d("notification", "updateUnRead: " + notificationModel.isRead());
                    if (!notificationModel.isRead()) {
                        binding.bottomNavigationView.getOrCreateBadge(R.id.notify_item).setVisible(true);
//                        binding.bottomNavigationView.getMenu().findItem(R.id.notify_item).setIcon(R.drawable.new_notification_icon);
                        break;
                    } else {
//                        binding.bottomNavigationView.getMenu().getItem(1).setIcon(R.drawable.ic_alarm);
                        binding.bottomNavigationView.getOrCreateBadge(R.id.notify_item).setVisible(false);
                    }
                }
            }
        });
    }
    private void iniActionBar() {
        if (currentUser != null) {
            //userPhoto = currentUser.getPhotoUrl();
            //Log.d("TAG", "iniActionBar: " + userPhoto.toString());
            // binding.imgUserCommunity.setImageURI(userPhoto);
            GlideApp.with(this)
                    .load(storageRef).into(binding.imgUserCommunity);
            String[] tempArr = currentUser.getEmail().split("@");
            String email = tempArr[0];
            binding.txtEmailCommunity.setText(email);
            String userName = currentUser.getDisplayName();
            binding.txtNameCommunity.setText(userName);
        } else {
            //currentUser.reload();
            currentUser = mAuth.getCurrentUser();
            iniActionBar();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlideApp.with(this).load(storageRef).into(binding.imgUserCommunity);
    }

}

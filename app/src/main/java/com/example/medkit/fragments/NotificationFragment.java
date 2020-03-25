package com.example.medkit.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medkit.R;
import com.example.medkit.databinding.FragmentNotificationBinding;
import com.example.medkit.model.NotificationModel;
import com.example.medkit.utils.NotificationAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private static List<NotificationModel> notifications;
    private static FragmentNotificationBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        notifications = new ArrayList<>();
        Bitmap ppbitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_type_user);
        notifications.add(new NotificationModel("Ahmed Medra",ppbitmap,"add a new post for diabetes section, tap to respond to him",false));
        notifications.add(new NotificationModel("Ahmed Medra",ppbitmap,"add a new post for diabetes section, tap to respond to him",false));
        notifications.add(new NotificationModel("Ahmed Medra",ppbitmap,"add a new post for diabetes section, tap to respond to him",false));
        notifications.add(new NotificationModel("Ahmed Medra",ppbitmap,"add a new post for diabetes section, tap to respond to him",false));
        notifications.add(new NotificationModel("Ahmed Medra",ppbitmap,"add a new post for diabetes section, tap to respond to him",false));
        notifications.add(new NotificationModel("Ahmed Medra",ppbitmap,"add a new post for diabetes section, tap to respond to him",false));

        NotificationAdapter postAdapter = new NotificationAdapter(notifications);
        binding.notificationList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.notificationList.setAdapter(postAdapter);
        updateUnRead();
        binding.notificationList.getItemAnimator().setChangeDuration(0);


        return view;
    }
    public static void updateUnRead(){
        int nUnRead = 0;
        for (int i = 0; i < notifications.size(); i++) {
            if(!notifications.get(i).isRead()){
                nUnRead++;
            }
        }
        binding.notificationCntTv.setText(String.valueOf(nUnRead));
    }
}

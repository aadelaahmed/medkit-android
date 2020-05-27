package com.example.medkit.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medkit.R;
import com.example.medkit.databinding.FragmentNotificationBinding;
import com.example.medkit.model.NotificationModel;
import com.example.medkit.model.User;
import com.example.medkit.utils.LinearLayoutManagerWrapper;
import com.example.medkit.utils.NotificationAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationFragment extends Fragment {
    private static FragmentNotificationBinding binding;
    private static List<NotificationModel> notificationList;
    private static String User_id;
    public boolean unReadNotification = false;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationRecyclerViewAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore mfirebase;
    public NotificationFragment() {

    }

    public static void updateUnRead() {
        int nUnRead = 0;
        for (int i = 0; i < notificationList.size(); i++) {
            if (!notificationList.get(i).isRead()) {
//                Log.d("notification", "updateUnRead: "+notificationList.get(i).isRead());
                nUnRead++;
            }
        }
        binding.notificationCntTv.setText(String.valueOf(nUnRead));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        User_id = auth.getCurrentUser().getUid();


        recyclerView = view.findViewById(R.id.notification_list);
        notificationList = new ArrayList<>();

        notificationRecyclerViewAdapter = new NotificationAdapter(notificationList, getContext());
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(container.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(notificationRecyclerViewAdapter);
        fetchData();
        binding.notificationList.getItemAnimator().setChangeDuration(0);
        return view;
    }

    private void fetchData() {
        mfirebase = FirebaseFirestore.getInstance();
        mfirebase.collection(User.USER_COLLECTION + "/" + User_id + "/" + NotificationModel.NOTIFICATION_COLLECTION).orderBy("createdTime", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        NotificationModel notificationModel = document.toObject(NotificationModel.class);
                        notificationList.add(notificationModel);
                        Log.d("notification", "updateUnRead: " + notificationModel.isRead());
                        notificationRecyclerViewAdapter.notifyDataSetChanged();
                        updateUnRead();
                    }
                } else
                    updateUnRead();
            }
        });
        //    addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
//                    if (doc.getType() == DocumentChange.Type.ADDED) {
//                        NotificationModel notificationModel = doc.getDocument().toObject(NotificationModel.class);
//                        notificationList.add(notificationModel);
////                        Log.d("notification", "updateUnRead: "+notificationModel.isRead());
//                        notificationRecyclerViewAdapter.notifyDataSetChanged();
//                        updateUnRead();
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        notificationList.clear();
//        mfirebase.collection("Users/"+User_id+"/Notification").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
//                    if (doc.getType() == DocumentChange.Type.ADDED) {
//                        NotificationModel notificationModel = doc.getDocument().toObject(NotificationModel.class);
//                        notificationList.add(notificationModel);
//                        Log.d("notification", "Read onEvent: "+notificationModel.isRead());
//                        notificationRecyclerViewAdapter.notifyDataSetChanged();
//                        updateUnRead();
//                    }
//                }
//            }
//        });
    }
}

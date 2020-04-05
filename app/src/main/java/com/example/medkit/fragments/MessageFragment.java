package com.example.medkit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medkit.R;
import com.example.medkit.databinding.FragmentMessageBinding;
import com.example.medkit.model.ChatItem;
import com.example.medkit.utils.ChatCustomAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.xwray.groupie.Section;

import java.util.ArrayList;

public class MessageFragment extends Fragment {
    private static FragmentMessageBinding binding;
    private Section chatSection;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.textViewToolbarTitle.setText("Messaging");
        ArrayList<ChatItem> chats = new ArrayList<ChatItem>();
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        chats.add(new ChatItem("Mohamed Mahmoud", "Good Bye", "3 pm", R.drawable.com_facebook_profile_picture_blank_portrait));
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        binding.chatRecyclerView.setAdapter(new ChatCustomAdapter(chats));

        return view;
    }
}
//    }    private ListenerRegistration addChatListener() {
//        return firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
//                if (e != null) {
//                    return;
//                }
//                List<Item> items = null;
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//
//                    items.add(new ChatItem(documentSnapshot.toObject(User.class), MessageFragment.this));
//
//                }
//
//            }
//        });
//    }
//
//
//    private void initRecyclerView(List<Item> item){
//        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        GroupAdapter adapter=new GroupAdapter();
//        binding.chatRecyclerView.setAdapter(adapter);
//        chatSection= new Section(item);
//        adapter.add(chatSection);
//
//
//
//    }
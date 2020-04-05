package com.example.medkit.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medkit.R;
import com.example.medkit.model.ChatItem;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatCustomAdapter extends RecyclerView.Adapter<ChatCustomAdapter.ViewHolder> {
    ArrayList<ChatItem> chats;

    public ChatCustomAdapter(ArrayList<ChatItem> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatItem chatItem = chats.get(position);
        holder.name.setText(chatItem.getName());
        holder.lastMessage.setText(chatItem.getLastmessage());
        holder.time.setText(chatItem.getTime());
        holder.chatImage.setImageResource(chatItem.getImageurl());

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name = itemView.findViewById(R.id.chat_name_tv);
        TextView time = itemView.findViewById(R.id.time_tv);
        TextView lastMessage = itemView.findViewById(R.id.last_message_tv);
        CircleImageView chatImage = itemView.findViewById(R.id.chat_image);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}

package com.project.myapplication.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.ChatBox;
import com.project.myapplication.DTO.Message;
import com.project.myapplication.R;

import java.util.List;

public class chatController extends RecyclerView.Adapter<chatController.ChatViewHolder> {

    private List<ChatBox> chatBoxList;
    private Message message;

    public chatController(List<ChatBox> chatBoxList, Message message) {
        this.chatBoxList = chatBoxList;
        this.message = message;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbox_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatBox chatItem = chatBoxList.get(position);
        holder.username.setText(chatItem.getName());
        holder.message.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return chatBoxList.size();
    }

    // ViewHolder cho từng mục chat
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView username, message;
        ImageView profileImage;
        ImageView muted;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.chatbox_username);
            message = itemView.findViewById(R.id.chatbox_message);
            profileImage = itemView.findViewById(R.id.chatbox_profileImage);
            muted = itemView.findViewById(R.id.chatbox_icon);
        }
    }
}

package com.project.myapplication.controller;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.project.myapplication.DTO.ChatBox;
import com.project.myapplication.DTO.Message;
import com.project.myapplication.R;
import com.project.myapplication.view.BottomSheetFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class chatController extends RecyclerView.Adapter<chatController.ChatViewHolder> {

    private final List<ChatBox> chatBoxList;
    private final String userID;

    public chatController(List<ChatBox> chatBoxList, String userID) {
        this.chatBoxList = chatBoxList;
        this.userID = userID;
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
        holder.message.setText(chatItem.getLastMessage()!= null? chatItem.getLastMessage():"Không có tin nhắn");
        holder.username.setText(chatItem.getName() != null ? chatItem.getName():"Tên không có");
        String imageUrl = chatItem.getImage_url();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.ic_launcher_foreground);
        }

        holder.dotButton.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(chatItem,userID);
            bottomSheetFragment.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Clicked on: " + chatItem.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return chatBoxList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateChatBoxes(List<ChatBox> newChatBoxList) {
        this.chatBoxList.clear();
        this.chatBoxList.addAll(newChatBoxList);
        notifyDataSetChanged();
    }

    // ViewHolder cho từng mục chat
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView username, message;
        ImageView profileImage;
        ImageButton dotButton;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.chatbox_username);
            message = itemView.findViewById(R.id.chatbox_message);
            profileImage = itemView.findViewById(R.id.avatarImageView);
            dotButton = itemView.findViewById(R.id.dot_icon);
        }
    }
}

package com.project.myapplication.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.ChatBox;
import com.project.myapplication.R;
import com.project.myapplication.view.BottomSheetFragment;
import com.project.myapplication.view.message_activity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class chatController extends RecyclerView.Adapter<chatController.ChatViewHolder> {

    private final List<ChatBox> chatBoxList;
    private final String userID;
    private final Context context;

    public chatController(List<ChatBox> chatBoxList, String userID, Context context) {
        this.context = context;
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
        Log.d("chatController", "onBindViewHolder: " + chatItem);
        holder.message.setText(chatItem.getLastMessage()!= null? chatItem.getLastMessage():"Không có tin nhắn");
        holder.username.setText(chatItem.getName() != null ? chatItem.getName():"Tên không có");
        String imageUrl = chatItem.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.unknow_avatar);
        }

        holder.dotButton.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(chatItem,userID);
            bottomSheetFragment.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, message_activity.class);
            intent.putExtra("chatBox", chatItem);
            intent.putExtra("userID", userID);
            context.startActivity(intent);
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

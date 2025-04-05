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
import com.project.myapplication.view.fragment.BottomSheetFragment;
import com.project.myapplication.view.activity.message_activity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

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
        // Lấy tên theo userID người khác, nếu không thì gán "Tên không có"
        String username = "Người dùng Điện tín tức thời";
        if (chatItem.getName() != null) {
            for (Map.Entry<String, String> entry : chatItem.getName().entrySet()) {
                if (!entry.getKey().equals(userID)) { // Tìm user khác userID
                    username = entry.getValue();
                    break; // Tìm thấy user đầu tiên thì thoát vòng lặp
                }
            }
        }
        holder.username.setText(username);

        // Lấy ảnh theo userID người khác, nếu không thì để ảnh mặc định
        String imageUrl = null;

        if (chatItem.getImageUrl() != null) {
            for (Map.Entry<String, String> entry : chatItem.getImageUrl().entrySet()) {
                if (!entry.getKey().equals(userID)) { // Tìm ảnh của user khác
                    imageUrl = entry.getValue();
                    break;
                }
            }
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.unknow_avatar);
        }

        String finalUsername = username;
        holder.dotButton.setOnClickListener(v -> {
            BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(chatItem, finalUsername,userID);
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

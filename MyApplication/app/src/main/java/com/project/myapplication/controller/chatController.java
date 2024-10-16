package com.project.myapplication.controller;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.ChatBox;
import com.project.myapplication.DTO.Message;
import com.project.myapplication.R;
import com.project.myapplication.model.ChatBoxModel;
import com.project.myapplication.model.MessageModel;
import com.project.myapplication.util.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

public class chatController extends RecyclerView.Adapter<chatController.ChatViewHolder> {

    private ChatBoxModel chatBoxModel;
    private List<ChatBox> chatBoxList;
    private MessageModel messageModel;
    private List<Message> messagesList;

    public chatController(List<ChatBox> chatBoxList,List<Message> messagesList) {
        this.chatBoxModel=new ChatBoxModel();
        this.messageModel= new MessageModel();
        this.chatBoxList = chatBoxList;
        this.messagesList = messagesList;
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
        Message messageItem = messagesList.size() > position ? messagesList.get(position) : null; // Kiểm tra kích thước danh sách

        if (chatItem != null && chatItem.getName() != null) {
            holder.username.setText(chatItem.getName());
        } else {
            holder.username.setText("Tên không có");
        }

        if (messageItem != null) {
            holder.message.setText(messageItem.getText() != null ? messageItem.getText() : "Tin nhắn trống");
        } else {
            holder.message.setText("Không có tin nhắn");
        }

        String imageUrl = chatItem != null ? chatItem.getImage_url() : null;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .transform(new RoundedTransformation(28, 0))
                    .into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.ic_launcher_foreground);
        }

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Clicked on: " + chatItem.getName(), Toast.LENGTH_SHORT).show();
        });
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
            profileImage = itemView.findViewById(R.id.avatarImageView);
//            muted = itemView.findViewById(R.id.chatbox_icon);
        }
    }
}

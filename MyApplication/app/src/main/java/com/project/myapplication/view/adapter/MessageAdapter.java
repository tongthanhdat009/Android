package com.project.myapplication.view.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Message;
import com.project.myapplication.R;
import com.project.myapplication.model.ChatBoxModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String userID;
    private final List<Message> messages;
    private static final int VIEW_TYPE_SEND = 1;
    private static final int VIEW_TYPE_RECEIVE = 2;

    public MessageAdapter(List<Message> messages, String userID) {
        this.userID=userID;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        String messageUserId = messages.get(position).getUserId();
        if (messageUserId != null && userID != null) {
            return messageUserId.equals(userID) ? VIEW_TYPE_SEND : VIEW_TYPE_RECEIVE;
        }
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MessageAdapter", "onCreateViewHolder: " + viewType);
        if (viewType == VIEW_TYPE_SEND) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_send, parent, false);
            return new SendMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_receive, parent, false);
            return new ReceiveMessageViewHolder(view);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof SendMessageViewHolder) {
            ((SendMessageViewHolder) holder).bind(message);
        } else {
            ((ReceiveMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class SendMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final ImageView image;
        private final ImageView messageImage;

        public SendMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            image = itemView.findViewById(R.id.sender_avatar);
            messageImage = itemView.findViewById(R.id.message_image);
        }

        public void bind(Message message) {
            ChatBoxModel chatBoxModel = new ChatBoxModel();

            // Hiển thị nội dung văn bản nếu có
            if (message.getText() != null && !message.getText().isEmpty()) {
                messageText.setText(message.getText());
                messageText.setVisibility(View.VISIBLE);
            } else {
                messageText.setVisibility(View.GONE); // Ẩn TextView nếu không có văn bản
            }

            // Hiển thị hình ảnh nếu có
            if (message.getMedia() != null && !message.getMedia().isEmpty()) {
                String imageUrl = message.getMedia().get(0);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(messageImage);
                    messageImage.setVisibility(View.VISIBLE);
                } else {
                    messageImage.setVisibility(View.GONE); // Ẩn ImageView nếu không có URL
                }
            } else {
                messageImage.setVisibility(View.GONE); // Ẩn ImageView nếu không có media
            }

            // Hiển thị avatar người gửi
            chatBoxModel.getAvatar(message.getUserId(), avatarUrl -> {
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Picasso.get().load(avatarUrl).into(image);
                } else {
                    image.setImageResource(R.drawable.unknow_avatar); // Hiển thị ảnh mặc định nếu không có URL
                }
            });
        }
    }

    static class ReceiveMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final ImageView image;
        private final ImageView messageImage;

        public ReceiveMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            image = itemView.findViewById(R.id.sender_avatar);
            messageImage = itemView.findViewById(R.id.message_image);
        }

        public void bind(Message message) {
            ChatBoxModel chatBoxModel = new ChatBoxModel();

            // Hiển thị nội dung văn bản nếu có
            if (message.getText() != null && !message.getText().isEmpty()) {
                messageText.setText(message.getText());
                messageText.setVisibility(View.VISIBLE);
            } else {
                messageText.setVisibility(View.GONE); // Ẩn TextView nếu không có văn bản
            }

            // Hiển thị hình ảnh nếu có
            if (message.getMedia() != null && !message.getMedia().isEmpty()) {
                String imageUrl = message.getMedia().get(0);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(messageImage);
                    messageImage.setVisibility(View.VISIBLE);
                } else {
                    messageImage.setVisibility(View.GONE); // Ẩn ImageView nếu không có URL
                }
            } else {
                messageImage.setVisibility(View.GONE); // Ẩn ImageView nếu không có media
            }

            // Hiển thị avatar người gửi
            chatBoxModel.getAvatar(message.getUserId(), avatarUrl -> {
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Picasso.get().load(avatarUrl).into(image);
                } else {
                    image.setImageResource(R.drawable.unknow_avatar); // Hiển thị ảnh mặc định nếu không có URL
                }
            });
        }
        }
}

package com.project.myapplication.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.project.myapplication.DTO.Message;
import com.project.myapplication.R;
import com.project.myapplication.model.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class message_activity extends AppCompatActivity {
    private String userID;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText messageInput;
    private ImageButton sendButton, imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        imageButton = findViewById(R.id.image_button);

        adapter = new MessageAdapter(messageList,userID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        String chatboxID ="";
        sendButton.setOnClickListener(v -> sendMessage(chatboxID));
        imageButton.setOnClickListener(v -> chooseImage());
    }

    private void sendMessage(String chatboxID) {
        String content = messageInput.getText().toString();
        MessageModel messageModel = new MessageModel();
        Message message = new Message(chatboxID, Timestamp.now(), new ArrayList<>(), new ArrayList<>(), content, userID);
            if (!content.isEmpty()) {
                messageModel.addMessage(message, new MessageModel.AddMessageCallback() {
                    @Override
                    public void onSuccess(String documentId) {
                        // Xử lý khi thêm thành công
                        Log.d("Firestore", "Message added with ID: " + documentId);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Xử lý khi xảy ra lỗi
                        Log.e("Firestore", "Error adding message", e);
                    }
                });
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messageList.size() - 1);
            messageInput.setText("");
        }
    }

    private void chooseImage() {
        // Xử lý chọn hình ảnh từ thư viện (Intent)
    }
}
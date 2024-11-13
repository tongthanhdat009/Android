package com.project.myapplication.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Rect;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.project.myapplication.DTO.ChatBox;
import com.project.myapplication.DTO.Message;
import com.project.myapplication.R;
import com.project.myapplication.model.ChatBoxModel;
import com.project.myapplication.model.MessageModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class message_activity extends AppCompatActivity {
    private String userID;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText messageInput;
    private ImageButton sendButton, imageButton, menuButton, backButton;
    private TextView chatBoxName;
    private ImageView chatBoxAvatar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ChatBox chatBox = getIntent().getParcelableExtra("chatBox");
        userID = Objects.requireNonNull(getIntent().getStringExtra("userID")).replaceAll("user", "");;

        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        imageButton = findViewById(R.id.image_button);
        menuButton = findViewById(R.id.menu_button);
        backButton = findViewById(R.id.back_button);
        chatBoxName = findViewById(R.id.user_name);
        chatBoxAvatar = findViewById(R.id.user_icon);

        // khởi tao ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Gọi hàm handleImagePickResult với chatBoxID và userID
                    assert chatBox != null;
                    handleImagePickResult(result, chatBox.getId(), userID);
                });

        assert chatBox != null;
        adapter = new MessageAdapter(messageList, userID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Cập nhật tên và avatar của chatbox
        chatBoxName.setText(chatBox.getName());

        if (chatBox.getImageUrl() != null && !chatBox.getImageUrl().isEmpty()) {
            Picasso.get().load(chatBox.getImageUrl()).into(chatBoxAvatar);
        } else {
            chatBoxAvatar.setImageResource(R.drawable.unknow_avatar);
        }

        // Lắng nghe nút quay lại
        backButton.setOnClickListener(v -> finish());

        // Gửi tin nhắn khi nhấn gửi
        sendButton.setOnClickListener(v -> sendMessage(chatBox.getId()));

        // Chọn hình ảnh
        imageButton.setOnClickListener(v -> chooseImage());

        // Mở DrawerLayout khi bấm menuButton
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(findViewById(R.id.navigation_view)));

        // Tải danh sách tin nhắn
        loadMessages(chatBox.getId());


        // Lắng nghe sự thay đổi kích thước của drawerLayout để phát hiện bàn phím bật/tắt
        drawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            drawerLayout.getWindowVisibleDisplayFrame(r);
            int screenHeight = drawerLayout.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            // Nếu bàn phím bật (khoảng cách lớn hơn 100)
            if (keypadHeight > 100) {
                // Cuộn xuống cuối cùng của RecyclerView
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });

        //ẩn bàn phím khi nhấn ra ngoài EditText
        drawerLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard(); // Gọi hàm ẩn bàn phím
            }
            return false; // Trả về false để cho phép các sự kiện khác tiếp tục
        });
        recyclerView.setOnTouchListener((v, event) -> {
            hideKeyboard();
            v.clearFocus(); // Xóa focus khỏi RecyclerView để không bật lại bàn phím
            return false;
        });

    }

    // Hàm tải tin nhắn từ Firestore và cập nhật RecyclerView
    private void loadMessages(String chatBoxID) {
        MessageModel messageModel = new MessageModel();
        messageModel.getMessages(chatBoxID, new MessageModel.GetMessagesCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Message> messages) {
                messageList.clear();
                messageList.addAll(messages);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1); // Cuộn xuống tin nhắn mới nhất
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage(String chatboxID) {
        String content = messageInput.getText().toString();
        MessageModel messageModel = new MessageModel();
        ChatBoxModel chatBoxModel = new ChatBoxModel();
        Message message = new Message(chatboxID, Timestamp.now(), new ArrayList<>(), false, content, userID);
        if (!content.isEmpty()) {
            messageModel.addMessage(chatboxID,message, new MessageModel.AddMessageCallback() {
                @Override
                public void onSuccess(String documentId) {
                    Log.d("Firestore", "Message added with ID: " + documentId);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("Firestore", "Error adding message", e);
                }
            });
            chatBoxModel.updateLastMessage(chatboxID, content);
            chatBoxModel.updateAllShowedToTrue(chatboxID);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messageList.size() - 1);
            messageInput.setText("");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void handleImagePickResult(ActivityResult result, String chatBoxID, String userID) {
        MessageModel messageModel= new MessageModel();
        ChatBoxModel chatBoxModel= new ChatBoxModel();
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    messageModel.uploadImageToFirebase(selectedImageUri, new MessageModel.UploadImageCallback() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            messageModel.saveNewMessageWithImageUrl(chatBoxID, userID, imageUrl);
                            chatBoxModel.updateLastMessage(chatBoxID, "Một hình ảnh được gửi");
                        }
                    });
                }
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}

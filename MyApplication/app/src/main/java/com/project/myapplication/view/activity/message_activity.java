package com.project.myapplication.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Rect;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.project.myapplication.DTO.ChatBox;
import com.project.myapplication.DTO.Message;
import com.project.myapplication.GeminiResponse;
import com.project.myapplication.R;
import com.project.myapplication.model.ChatBoxModel;
import com.project.myapplication.model.GeminiApiService;
import com.project.myapplication.model.GeminiRequest;
import com.project.myapplication.model.MessageModel;
import com.project.myapplication.util.RetrofitClient;
import com.project.myapplication.view.adapter.MessageAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class message_activity extends AppCompatActivity {
    private String userID;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText messageInput;
    private ImageButton sendButton, imageButton, menuButton, backButton;
    private TextView chatBoxName;
    private ImageView chatBoxAvatar;

    private boolean isAvatarUpdate = false; // Biến để xác định xem có đang cập nhật avatar hay không

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ChatBoxModel chatBoxModel = new ChatBoxModel();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ChatBox chatBox = getIntent().getParcelableExtra("chatBox");
        userID = Objects.requireNonNull(getIntent().getStringExtra("userID")).replaceAll("user", "");

        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        imageButton = findViewById(R.id.image_button);
        menuButton = findViewById(R.id.menu_button);
        backButton = findViewById(R.id.back_button);
        chatBoxName = findViewById(R.id.user_name);
        chatBoxAvatar = findViewById(R.id.user_icon);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);

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

        // Khởi tạo ActivityResultLauncher để chọn ảnh
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            handleImagePickResult(result, chatBox.getId(), userID);
                        }
                    }
                }
        );

        assert chatBox != null;
        adapter = new MessageAdapter(messageList, userID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

// Lấy danh sách user trong chat trừ userID hiện tại
        String displayName = "Tên không có";
        String displayImageUrl = null;

        if (chatBox.getName() != null) {
            for (Map.Entry<String, String> entry : chatBox.getName().entrySet()) {
                if (!entry.getKey().equals(userID)) {  // Tìm user khác userID
                    displayName = entry.getValue();
                    break;
                }
            }
        }

        if (chatBox.getImageUrl() != null) {
            for (Map.Entry<String, String> entry : chatBox.getImageUrl().entrySet()) {
                if (!entry.getKey().equals(userID)) {  // Tìm avatar của user khác userID
                    displayImageUrl = entry.getValue();
                    break;
                }
            }
        }
// Cập nhật tên và avatar
        chatBoxName.setText(displayName);

        if (displayImageUrl != null && !displayImageUrl.isEmpty()) {
            Picasso.get().load(displayImageUrl).into(chatBoxAvatar);
        } else {
            chatBoxAvatar.setImageResource(R.drawable.unknow_avatar);
        }

        // Lắng nghe nút quay lại
        backButton.setOnClickListener(v -> finish());

        // Gửi tin nhắn khi nhấn gửi
        sendButton.setOnClickListener(v -> sendMessage(chatBox));

        if(chatBoxModel.isAI(chatBox)){
            imageButton.setVisibility(View.GONE);
        }
        // Chọn hình ảnh
        imageButton.setOnClickListener(v -> chooseImage());

        // Mở DrawerLayout khi bấm menuButton
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(findViewById(R.id.navigation_view)));

        // Tải danh sách tin nhắn
        loadMessages(chatBox.getId());

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerOpened(View drawerView) {
                // Có thể thêm mã để xử lý khi Drawer mở
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Dừng các hiệu ứng hoặc cập nhật lại trạng thái khi Drawer đóng
                navigationView.getMenu().clear(); // Tùy chọn: làm mới menu nếu cần
                navigationView.inflateMenu(R.menu.drawer_menu);
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Xử lý trạng thái thay đổi của Drawer
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nameChange) {
                showRenameChatBoxDialog(chatBox);
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            } else if (item.getItemId() == R.id.avatarChange) {
                setAvatarUpdateMode(); // Thiết lập chế độ cập nhật avatar
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            } else {
                drawerLayout.closeDrawer(GravityCompat.END);
                return false;
            }
        });

        // Ẩn bàn phím khi nhấn ra ngoài EditText
        drawerLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard();
            }
            return false;
        });
        recyclerView.setOnTouchListener((v, event) -> {
            hideKeyboard();
            v.clearFocus();
            return false;
        });
    }

    private void loadMessages(String chatBoxID) {
        MessageModel messageModel = new MessageModel();
        messageModel.getMessages(chatBoxID, new MessageModel.GetMessagesCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Message> messages) {
                messageList.clear();
                messageList.addAll(messages);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage(ChatBox chatBox) {
        String content = messageInput.getText().toString();
        MessageModel messageModel = new MessageModel();
        ChatBoxModel chatBoxModel = new ChatBoxModel();

        if (!content.isEmpty()) {
            // Tạo đối tượng Message cho tin nhắn
            Message message = new Message(chatBox.getId(), Timestamp.now(), new ArrayList<>(), false, content, userID);

            if (chatBoxModel.isAI(chatBox)) {
                // Nếu là AI chatbox, gửi tin nhắn lên Firebase và lấy kết quả từ Gemini API

                // Lưu tin nhắn đầu tiên vào Firebase
                messageModel.addMessage(chatBox.getId(), message, new MessageModel.AddMessageCallback() {
                    @Override
                    public void onSuccess(String documentId) {
                        Log.d("Firestore", "Message added with ID: " + documentId);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Firestore", "Error adding message", e);
                    }
                });

                // Lấy kết quả từ AI (Gemini API)
                getAIResponse(content, chatBox.getId());
                messageInput.setText("");
            } else {
                // Nếu không phải là AI chatbox, gửi tin nhắn bình thường
                messageModel.addMessage(chatBox.getId(), message, new MessageModel.AddMessageCallback() {
                    @Override
                    public void onSuccess(String documentId) {
                        Log.d("Firestore", "Message added with ID: " + documentId);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Firestore", "Error adding message", e);
                    }
                });
                chatBoxModel.updateLastMessage(chatBox.getId(), content);
                chatBoxModel.updateAllShowedToTrue(chatBox.getId());
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
                messageInput.setText("");
            }
        }
    }

    private void getAIResponse(String content, String chatboxID) {
        // Gọi API Gemini để lấy phản hồi
        GeminiApiService apiService = RetrofitClient.getRetrofitInstance().create(GeminiApiService.class);

        // Tạo GeminiRequest với cấu trúc đúng
        GeminiRequest request = new GeminiRequest();
        List<GeminiRequest.Content> contents = new ArrayList<>();
        GeminiRequest.Content contentObj = new GeminiRequest.Content();
        List<GeminiRequest.Content.Part> parts = new ArrayList<>();

        GeminiRequest.Content.Part part = new GeminiRequest.Content.Part();
        part.setText("Trả lời câu hỏi sau không quá 50 ký tự: " + content);
        parts.add(part);
        contentObj.setParts(parts);
        contents.add(contentObj);
        request.setContents(contents);

//        String apiKey = "AIzaSyBTRGzEnoln2KcDdcOpIx6a2roZ1PTrn1M";

        // Gửi yêu cầu tới API Gemini
        Call<GeminiResponse> call = apiService.generateContent(request, apiKey);

        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Truy xuất AI response từ body của response
                    String aiResponse = response.body().getAIResponseText();
                    String fullResponse = new Gson().toJson(response.body());
                    Log.d("RawResponse", fullResponse);
                    Log.e("APIError", "Error: " + response.code() + " - " + response.message());
                    if (aiResponse != null) {
                        Log.d("AIResponse", aiResponse);
                        aiResponse = aiResponse.replaceAll("[\\r\\n]+", " ");
                        // Lưu kết quả AI vào Firebase
                        Message aiMessage = new Message(chatboxID, Timestamp.now(), new ArrayList<>(), true, aiResponse, "Chat bot");
                        MessageModel messageModel = new MessageModel();
                        messageModel.addMessage(chatboxID, aiMessage, new MessageModel.AddMessageCallback() {
                            @Override
                            public void onSuccess(String documentId) {
                                Log.d("Firestore", "AI Response added with ID: " + documentId);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.e("Firestore", "Error adding AI response", e);
                            }
                        });

                        // Cập nhật tin nhắn cuối cùng
                        ChatBoxModel chatBoxModel = new ChatBoxModel();
                        chatBoxModel.updateLastMessage(chatboxID, aiResponse);
                    } else {
                        Log.d("AIResponse", "AI response is null");
                    }
                } else {
                    Log.e("AIResponse", "Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                Log.e("AIResponse", "Error fetching AI response", t);
            }
        });
    }


    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void setAvatarUpdateMode() {
        isAvatarUpdate = true; // Thiết lập chế độ cập nhật avatar
        chooseImage();
    }

    private void handleImagePickResult(ActivityResult result, String chatBoxID, String userID) {
        MessageModel messageModel = new MessageModel();
        ChatBoxModel chatBoxModel = new ChatBoxModel();

        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    if (isAvatarUpdate) {
                        // If updating the avatar
                        chatBoxModel.uploadChatBoxImage(selectedImageUri, chatBoxID, new ChatBoxModel.UploadImageCallback() {
                            @Override
                            public void onSuccess(String imageUrl) {
                                // Update the avatar URL in Firestore
                                chatBoxModel.updateChatBoxImage(chatBoxID, userID,imageUrl, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Successfully updated the image URL in Firestore
                                            Picasso.get().load(imageUrl).into(chatBoxAvatar); // Load the new avatar into the ImageView
                                            Toast.makeText(message_activity.this, "Avatar đã được cập nhật", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Handle failure in Firestore update
                                            Toast.makeText(message_activity.this, "Cập nhật avatar thất bại trên Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception e) {
                                // Handle failure for avatar image upload
                                Toast.makeText(message_activity.this, "Cập nhật avatar thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // If not updating the avatar, it's a message image upload
                        messageModel.uploadImageToFirebase(selectedImageUri, new MessageModel.UploadImageCallback() {
                            @Override
                            public void onSuccess(String imageUrl) {
                                recyclerView.scrollToPosition(messageList.size() - 1);
                                // Send the image message
                                messageModel.saveNewMessageWithImageUrl(chatBoxID, userID, imageUrl);
                                chatBoxModel.updateLastMessage(chatBoxID, "Một hình ảnh được gửi"); // Update the last message text
                            }
                        });
                    }
                }
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View view = getCurrentFocus();
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void showRenameChatBoxDialog(ChatBox chatBox) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi tên ChatBox");

        final EditText input = new EditText(this);
        input.setText(chatBoxName.getText());
        builder.setView(input);

        builder.setPositiveButton("Đổi tên", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                Map<String, String> updatedNames = new HashMap<>(chatBox.getName()); // Sao chép Map hiện tại

                for (String key : updatedNames.keySet()) {
                    if (key.equals(userID)) continue; // Bỏ qua key của user hiện tại
                    updatedNames.put(key, newName); // Gán tên mới cho tất cả key (tất cả user)
                }

                ChatBoxModel chatBoxModel = new ChatBoxModel();
                chatBoxModel.updateChatBoxName(chatBox.getId(), updatedNames, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(Task<Boolean> task) {
                        if (task.isSuccessful() && task.getResult()) {
                            chatBox.setName(updatedNames); // Cập nhật Map trong ChatBox
                            chatBoxName.setText(newName); // Hiển thị tên mới
                        } else {
                            Toast.makeText(message_activity.this, "Không thể đổi tên", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
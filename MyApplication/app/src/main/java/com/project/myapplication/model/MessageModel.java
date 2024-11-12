package com.project.myapplication.model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.myapplication.DTO.Message;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageModel {
    private final FirebaseFirestore firestore;

    public MessageModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    // Hàm để thêm tin nhắn vào Firestore
    public void addMessage(String chatBoxID,Message message, AddMessageCallback callback) {
        // Tạo một Map từ đối tượng Message
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("datetime", message.getDatetime());
        messageData.put("media", message.getMedia());
        messageData.put("seenBy", message.getSeenBy());
        messageData.put("text", message.getText());
        messageData.put("userID", message.getUserId());

        // Thêm dữ liệu vào Firestore trong collection "messages"
        firestore.collection("chatbox")
                .document(chatBoxID)
                .collection("messages")
                .add(messageData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess(task.getResult().getId());
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    // Interface để định nghĩa callback
    public interface AddMessageCallback {
        void onSuccess(String documentId);
        void onFailure(Exception e);
    }

    // Hàm để tải tất cả tin nhắn từ Firestore
    public void getMessages(String chatBoxID, final GetMessagesCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chatbox")
                .document(chatBoxID)
                .collection("messages")
                .orderBy("datetime") // Sắp xếp theo thời gian
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    List<Message> messages = new ArrayList<>();
                    assert queryDocumentSnapshots != null;
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Message message = document.toObject(Message.class);
                        if (message != null) {
                            messages.add(message);
                        }
                    }
                    callback.onSuccess(messages);
                });
    }

    // Callback interface
    public interface GetMessagesCallback {
        void onSuccess(List<Message> messages);
    }

    // Phương thức tải ảnh lên Firebase Storage
    public void uploadImageToFirebase(Uri imageUri, UploadImageCallback callback) {
        // Lấy đường dẫn ảnh trong Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());

        // Tải ảnh lên
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của ảnh đã tải lên
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Gọi callback với URL của ảnh
                        callback.onSuccess(uri.toString());
                    });
                });
    }

    // Callback interface cho việc upload ảnh
    public interface UploadImageCallback {
        void onSuccess(String imageUrl);
    }

    // Phương thức tạo mới tài liệu tin nhắn với URL ảnh trong Firestore
    public void saveNewMessageWithImageUrl(String chatboxId, String userId, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo map để chứa dữ liệu của tin nhắn mới
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("datetime", FieldValue.serverTimestamp()); // Thời gian hiện tại
        messageData.put("media", Collections.singletonList(imageUrl)); // Danh sách chứa URL ảnh
        messageData.put("seenBy", false);
        messageData.put("text", "");
        messageData.put("userID", userId);

        // Tạo mới tài liệu tin nhắn trong collection "messages"
        db.collection("chatbox")
                .document(chatboxId)
                .collection("messages")
                .add(messageData);
    }
}

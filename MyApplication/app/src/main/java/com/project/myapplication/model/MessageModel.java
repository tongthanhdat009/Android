package com.project.myapplication.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.DTO.ChatBox;
import com.project.myapplication.DTO.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageModel {
    private final FirebaseFirestore firestore;

    public MessageModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    // Hàm để thêm tin nhắn vào Firestore
    public void addMessage(Message message, AddMessageCallback callback) {
        // Tạo một Map từ đối tượng Message
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("chatboxID", message.getChatboxID());
        messageData.put("datetime", message.getDatetime());
        messageData.put("media", message.getMedia());
        messageData.put("seenBy", message.getSeenBy());
        messageData.put("text", message.getText());
        messageData.put("userID", message.getUserId());

        // Thêm dữ liệu vào Firestore trong collection "messages"
        firestore.collection("messages")
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
}

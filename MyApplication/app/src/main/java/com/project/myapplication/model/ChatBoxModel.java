package com.project.myapplication.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.DTO.ChatBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatBoxModel {
    private final FirebaseFirestore firestore;

    public ChatBoxModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    public interface ChatBoxCallback {
        void onCallback(List<ChatBox> chatBoxList);
    }

    public void getChatBoxesByUserID(String userID, ChatBoxCallback callback) {
        firestore.collection("chatbox")
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("ChatBoxModel", "Error getting documents: ", e);
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        List<ChatBox> filteredList = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            ChatBox chatBox = document.toObject(ChatBox.class);
                            if (chatBox != null && chatBox.getShowed() != null) {
                                // Duyệt qua Map<String, Boolean> 'showed'
                                for (Map.Entry<String, Boolean> entry : chatBox.getShowed().entrySet()) {
                                    // Kiểm tra nếu userID có giá trị true
                                    if (entry.getKey().equals(userID) && entry.getValue()) {
                                        chatBox.setId(document.getId());
                                        filteredList.add(chatBox);
                                        break; // Thoát khỏi vòng lặp khi đã tìm thấy
                                    }
                                }
                            }
                        }
                        callback.onCallback(filteredList);  // Trả về kết quả sau khi lọc
                    }
                });
    }


    public void hideChatBox(String chatBoxId, String userID) {
        firestore.collection("chatbox")
                .document(chatBoxId)
                .update("showed." + userID, false)
                .addOnSuccessListener(aVoid -> Log.d("ChatBoxModel", "ChatBox hidden successfully"))
                .addOnFailureListener(e -> Log.e("ChatBoxModel", "Error hiding chatbox", e));
    }
}

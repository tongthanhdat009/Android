package com.project.myapplication.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.DTO.ChatBox;

import java.util.ArrayList;
import java.util.List;

public class ChatBoxModel {
    private final FirebaseFirestore firestore;

    public ChatBoxModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void getChatBoxList(String userID,final OnChatBoxListRetrievedCallback callback) {
        firestore.collection("chatbox")
                .whereArrayContains("usersID",userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<ChatBox> chatBoxList = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            ChatBox chatBox = document.toObject(ChatBox.class);
                            assert chatBox != null;
                            chatBox.setId(document.getId());
                            chatBoxList.add(chatBox);
                        }
                        callback.onChatBoxListRetrieved(chatBoxList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onChatBoxListRetrieved(null);
                    }
                });
    }

    public interface OnChatBoxListRetrievedCallback {
        void onChatBoxListRetrieved(List<ChatBox> users);
    }
}

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
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.DTO.ChatBox;

import java.util.ArrayList;
import java.util.List;

public class ChatBoxModel {
    private final FirebaseFirestore firestore;

    public ChatBoxModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void getChatBoxList(String userID, final OnChatBoxListRetrievedCallback callback) {
        firestore.collection("chatbox")
                .whereArrayContains("usersID", userID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            callback.onChatBoxListRetrieved(null);
                            return;
                        }

                        List<ChatBox> chatBoxList = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            ChatBox chatBox = document.toObject(ChatBox.class);
                            assert chatBox != null;
                            chatBox.setId(document.getId());
                            chatBoxList.add(chatBox);
                        }
                        Log.d("TEST", chatBoxList.toString());
                        callback.onChatBoxListRetrieved(chatBoxList);
                    }
                });
    }

    public interface OnChatBoxListRetrievedCallback {
        void onChatBoxListRetrieved(List<ChatBox> users);
    }
}

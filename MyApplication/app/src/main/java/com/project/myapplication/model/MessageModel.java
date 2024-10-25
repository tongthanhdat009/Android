package com.project.myapplication.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.DTO.ChatBox;
import com.project.myapplication.DTO.Message;

import java.util.List;

public class MessageModel {
    private final FirebaseFirestore firestore;

    public MessageModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void getClosetMess(String boxChatId, final OnClosetMessRetrievedCallback callback) {
        firestore.collection("chatbox")
                .document(boxChatId)
                .collection("messages")
                .orderBy("datetime", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            callback.onClosetMessRetrieved(null);
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                            Message closetMessage = document.toObject(Message.class);
                            closetMessage.setChatboxID(boxChatId);
                            callback.onClosetMessRetrieved(closetMessage);
                        }
                    }
                });
    }


    public interface OnClosetMessRetrievedCallback {
        void onClosetMessRetrieved(Message message);
    }
}

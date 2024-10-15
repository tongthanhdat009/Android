package com.project.myapplication.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
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
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                            Message closetMessage = document.toObject(Message.class);
                            callback.onClosetMessRetrieved(closetMessage);
                        } else {
                            callback.onClosetMessRetrieved(null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onClosetMessRetrieved(null);
                    }
                });
    }


    public interface OnClosetMessRetrievedCallback {
        void onClosetMessRetrieved(Message message);
    }
}

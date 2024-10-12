package com.project.myapplication.model;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MessageModel {
    private final FirebaseFirestore firestore;
    public MessageModel(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }
    public void getClosetMess(){
        firestore.collection("messages").limit(1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    }
                });
    }

}

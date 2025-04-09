package com.project.myapplication.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.DTO.ShortComment;
import com.project.myapplication.DTO.ShortVideo;
import com.project.myapplication.DTO.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ShortModel {
    private final FirebaseFirestore firestore;

    public ShortModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    // Lấy toàn bộ short videos
    public void getAllShortVideos(OnShortVideosRetrievedCallback callback) {
        firestore.collection("short")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("ShortModel", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        ArrayList<ShortVideo> videos = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            ShortVideo video = doc.toObject(ShortVideo.class);
                            if (video != null) {
                                video.setId(doc.getId());
                                videos.add(video);
                            }
                        }
                        callback.onShortVideosRetrieved(videos);
                    } else {
                        callback.onShortVideosRetrieved(new ArrayList<>());
                    }
                });
    }

    // Lấy comment trong short video
    public void getAllCommentsForShort(String shortID, OnCommentsRetrievedCallback callback) {
        firestore.collection("short")
                .document(shortID)
                .collection("comment")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("ShortModel", "Get comments failed.", error);
                        return;
                    }

                    if (value != null) {
                        ArrayList<ShortComment> comments = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            ShortComment comment = doc.toObject(ShortComment.class);
                            comments.add(comment);
                        }
                        callback.onCommentsRetrieved(comments);
                    }
                });
    }

    // Thêm comment mới vào short video
    public void addCommentToShort(String shortID, ShortComment comment) {
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("userID", comment.getUserID());
        commentMap.put("content", comment.getContent());
        commentMap.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("short")
                .document(shortID)
                .collection("comment")
                .add(commentMap)
                .addOnSuccessListener(documentReference ->
                        Log.d("ShortModel", "Comment added successfully"))
                .addOnFailureListener(e ->
                        Log.w("ShortModel", "Error adding comment", e));
    }

    // Thêm short video mới
    public void addShortVideo(ShortVideo shortVideo, OnShortAddedCallback callback) {
        Map<String, Object> shortMap = new HashMap<>();
        shortMap.put("userID", shortVideo.getUserID());
        shortMap.put("videoUrl", shortVideo.getVideoUrl());
        shortMap.put("title", shortVideo.getTitle());
        shortMap.put("likeBy", shortVideo.getLikeBy() != null ? shortVideo.getLikeBy() : new ArrayList<>());
        shortMap.put("timestamp", FieldValue.serverTimestamp());

        firestore.collection("short")
                .add(shortMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ShortModel", "Short video added: " + documentReference.getId());
                    callback.onShortAdded(true);
                })
                .addOnFailureListener(e -> {
                    Log.w("ShortModel", "Failed to add short video", e);
                    callback.onShortAdded(false);
                });
    }

    // Lấy thông tin người dùng từ userID
    public void getUserInfo(String userID, OnUserInfoCallback callback) {
        firestore.collection("users")
                .document(userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            callback.onUserInfoRetrieved(user);
                        } else {
                            Log.d("ShortModel", "No such user");
                        }
                    } else {
                        Log.e("ShortModel", "Error getting user", task.getException());
                    }
                });
    }

    // Interfaces cho callback
    public interface OnShortVideosRetrievedCallback {
        void onShortVideosRetrieved(ArrayList<ShortVideo> videos);
    }

    public interface OnCommentsRetrievedCallback {
        void onCommentsRetrieved(ArrayList<ShortComment> comments);
    }

    public interface OnShortAddedCallback {
        void onShortAdded(boolean success);
    }

    public interface OnUserInfoCallback {
        void onUserInfoRetrieved(User user);
    }
}

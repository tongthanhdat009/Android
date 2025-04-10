package com.project.myapplication.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.myapplication.DTO.Notification;
import com.project.myapplication.DTO.User;
import com.project.myapplication.view.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationModel {
    private final FirebaseFirestore firestore;
    public NotificationModel(){

        this.firestore = FirebaseFirestore.getInstance();
    }


    public void addNotification(String body,
                           boolean isRead,
                           String senderId,
                           Timestamp timestamp,
                           String title,
                           String type,
                           String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId); // Người nhận thông báo (chủ bài viết)
        notification.put("senderId", senderId); // Người gửi thông báo (người like)
        notification.put("title", title);
        notification.put("body", body);
        notification.put("timestamp", timestamp);
        notification.put("isRead", isRead);
        notification.put("type", type);

        db.collection("notifications").add(notification)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FCM", "Đã tạo thông báo: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("FCM", "Lỗi khi tạo thông báo: " + e.getMessage());
                });
    }

    public void getNotificationByUserID(String userID, getNotificatioByUserIDCallback callback) {
        CollectionReference notificationsRef = firestore.collection("notifications");

        // Truy vấn các thông báo có trường userID bằng userID truyền vào
        notificationsRef.whereEqualTo("userId", userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Notification> notificationList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification tempNotification = document.toObject(Notification.class);
                            tempNotification.setNotiID(document.getId()); // nếu bạn có trường này trong model
                            notificationList.add(tempNotification);
                        }
                        callback.getNotiByUserID(notificationList);
                    } else {
                        Log.e("NotificationModel", "Error getting notifications: ", task.getException());
                    }
                });
    }


    public interface getNotificatioByUserIDCallback{
        void getNotiByUserID(ArrayList<Notification> notifications);
    }

}

package com.project.myapplication.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.myapplication.R;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FCM", "Token mới: " + token);

        // Lấy userId hiện tại nếu đã đăng nhập
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Cập nhật token cho user hiện tại
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId)
                    .update("fcmTokens", token)
                    .addOnSuccessListener(aVoid -> Log.d("FCM", "Token mới đã được cập nhật"))
                    .addOnFailureListener(e -> Log.e("FCM", "Lỗi cập nhật token mới: " + e.getMessage()));
        } else {
            Log.d("FCM", "Không có người dùng đăng nhập để cập nhật token");
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "Thong_bao";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Thông báo",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.logo) // Thay icon theo bạn
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }
    private void showNotificationWithImage(String title, String message, String imageUrl) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "image_channel";

        createNotificationChannelIfNeeded(channelId, "Kênh thông báo có ảnh");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true);

        // Tải ảnh từ URL nếu có
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                java.net.URL url = new java.net.URL(imageUrl);
                android.graphics.Bitmap image = android.graphics.BitmapFactory.decodeStream(url.openConnection().getInputStream());
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image).bigLargeIcon((Bitmap) null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
    private void createNotificationChannelIfNeeded(String channelId, String channelName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }
    }
    private void parseDataMessage(RemoteMessage remoteMessage) {
        if (!remoteMessage.getData().isEmpty()) {
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String imageUrl = remoteMessage.getData().get("image"); // Nếu có

            // Gọi hiển thị
            showNotificationWithImage(title, message, imageUrl);
        }
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            // Có notification -> xử lý hiển thị cơ bản
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            showNotification(title, message);
        } else {
            // Data message → custom
            parseDataMessage(remoteMessage);
        }
    }

    public static void updateFCMTokenForUser(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        Log.d("FCM", "Token cho user " + userId + ": " + token);

                        // Lưu token vào document người dùng
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users").document(userId)
                                .update("fcmTokens", token)
                                .addOnSuccessListener(aVoid -> Log.d("FCM", "Token cập nhật thành công"))
                                .addOnFailureListener(e -> Log.e("FCM", "Lỗi cập nhật token: " + e.getMessage()));
                    } else {
                        Log.e("FCM", "Không thể lấy token: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }
}


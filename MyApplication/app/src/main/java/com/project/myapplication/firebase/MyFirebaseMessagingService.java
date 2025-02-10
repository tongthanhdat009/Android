package com.project.myapplication.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Token mới: " + token);

        // Gửi token này lên server nếu cần
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        // Thực hiện API call gửi token lên server (nếu cần)
    }
}

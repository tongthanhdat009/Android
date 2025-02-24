package com.project.myapplication.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.myapplication.view.activity.loginActivity;

import java.util.Objects;

public class resetPasswordController {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private Context context;
    public resetPasswordController(Context context) {
        this.context = context;
        auth = FirebaseAuth.getInstance();
    }

    public void resetPassword(String email, Button btn) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final long delayMillis = 1000;
        final int totalTimeSeconds = 60;

        btn.setEnabled(false);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Email đặt lại mật khẩu đã được gửi!", Toast.LENGTH_SHORT).show();
                        startResendCountdown(btn);
                    } else {
                        Toast.makeText(context, "Lỗi: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        btn.setEnabled(true);
                    }
                });
    }

    // Bắt đầu đếm ngược thời gian gửi lại mã
    private void startResendCountdown(Button btn) {
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            for (int i = 60; i >= 0; i--) {
                final int secondsLeft = i;
                handler.post(() -> btn.setText("Gửi lại sau: " + secondsLeft + "s"));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            handler.post(() -> {
                btn.setEnabled(true);
                btn.setText("Gửi lại mã");
            });
        }).start();
    }

}

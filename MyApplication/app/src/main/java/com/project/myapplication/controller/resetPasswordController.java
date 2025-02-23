package com.project.myapplication.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class resetPasswordController {
    private FirebaseAuth auth;
    Context context;
    public resetPasswordController(Context context){
        this.context = context;
        auth = FirebaseAuth.getInstance();
    }

    public void resetPassword(String email, Button btn) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final long delayMillis = 1000;
        final int totalTimeSeconds = 60;

        handler.post(() -> {
            btn.setEnabled(false);
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Email đặt lại mật khẩu đã được gửi!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        });

        new Thread(() -> {
            for (int i = totalTimeSeconds; i >= 0; i--) {
                final int secondsLeft = i;
                handler.post(() -> btn.setText("Gửi lại sau: " + secondsLeft + "s"));

                try {
                    Thread.sleep(delayMillis);
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

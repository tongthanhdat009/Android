package com.project.myapplication.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.myapplication.R;

import java.util.Objects;

public class verifyEmailActivity extends AppCompatActivity {

    private ImageButton backBTN;
    private Button getCodeBTN;
    private TextView label;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int RESEND_TIME = 60; // thời gian chờ gửi lại (giây)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        initUI();
        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAuthStateListener(); // Lắng nghe thay đổi trạng thái
    }

    private void initUI() {
        backBTN = findViewById(R.id.backBTN);
        getCodeBTN = findViewById(R.id.getCodeBTN);
        label = findViewById(R.id.label);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    private void setupUI() {
        updateUI(); // Cập nhật UI ban đầu
        getCodeBTN.setOnClickListener(v -> startEmailVerification());
        backBTN.setOnClickListener(v -> finish());
    }

    @SuppressLint("SetTextI18n")
    private void startEmailVerification() {
        getCodeBTN.setEnabled(false);
        if (user != null) {
            user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("INFO", "Email xác thực đã được gửi!");
                        Toast.makeText(this, "Vui lòng kiểm tra email để xác thực!", Toast.LENGTH_SHORT).show();
                        startResendCountdown();
                    } else {
                        Log.e("ERROR", "Gửi email thất bại: " + Objects.requireNonNull(task.getException()).getMessage());
                        getCodeBTN.setEnabled(true);
                    }
                });
        }
    }

    private void startResendCountdown() {
        for (int i = 0; i <= RESEND_TIME; i++) {
            final int timeLeft = RESEND_TIME - i;
            handler.postDelayed(() -> {
                if (timeLeft > 0) {
                    getCodeBTN.setText(String.format("Gửi lại sau: %ds", timeLeft));
                } else {
                    getCodeBTN.setEnabled(true);
                    getCodeBTN.setText("Gửi lại mã");
                }
            }, i * 1000L);
        }
    }

    private void updateUI() {
        if (user != null) {
            user.reload().addOnCompleteListener(task -> { // Reload user để cập nhật trạng thái
                if (user.isEmailVerified()) {
                    getCodeBTN.setVisibility(View.GONE);
                    label.setText("Email đã được xác thực");
                } else {
                    getCodeBTN.setVisibility(View.VISIBLE);
                    label.setText("Nhấn vào nút để nhận mã xác nhận");
                }
            });
        }
    }

    private void startAuthStateListener() {
        mAuth.addAuthStateListener(firebaseAuth -> {
            FirebaseUser updatedUser = firebaseAuth.getCurrentUser();
            if (updatedUser != null) {
                updatedUser.reload().addOnCompleteListener(task -> {
                    if (updatedUser.isEmailVerified()) {
                        updateUI(); // Cập nhật giao diện khi email đã xác thực
                    }
                });
            }
        });
    }

}

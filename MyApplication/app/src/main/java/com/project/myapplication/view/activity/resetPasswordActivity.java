package com.project.myapplication.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.myapplication.DTO.User;
import com.project.myapplication.controller.resetPasswordController;

import com.project.myapplication.R;
import com.project.myapplication.model.UserModel;

public class resetPasswordActivity extends AppCompatActivity {
    resetPasswordController controller;
    ImageButton backBTN;
    EditText emailInput;
    Button getCodeBTN;
    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        controller = new resetPasswordController(getApplicationContext());

        initUI();
        checkAuthStatus(); // Kiểm tra trạng thái tài khoản khi mở Activity
    }

    private void initUI() {
        backBTN = findViewById(R.id.backBTN);
        getCodeBTN = findViewById(R.id.getCodeBTN);
        emailInput = findViewById(R.id.email_input);

        backBTN.setOnClickListener(view -> finish());

        getCodeBTN.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            if (!email.isEmpty()) {
                controller.resetPassword(email, getCodeBTN);
            } else {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthStatus(); // Kiểm tra khi quay lại app
    }

    private void checkAuthStatus() {
        if (user != null) {
            user.reload().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("AUTH", "Phiên đăng nhập không hợp lệ, đăng xuất...");
                    logoutUser();
                } else {
                    checkIfPasswordChanged();
                }
            });
        }
    }

    private void checkIfPasswordChanged() {
        user.getIdToken(true)
                .addOnFailureListener(e -> {
                    Log.e("AUTH", "Mật khẩu đã bị thay đổi, đăng xuất...");
                    logoutUser();
                });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Mật khẩu đã thay đổi, vui lòng đăng nhập lại.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    UserModel userModel = new UserModel();
                    userModel.getUserInfor(user.getUid(), new UserModel.OnGetUserInfor() {
                        @Override
                        public void getInfor(User user) {
                            user.setLogged("");
                            userModel.userUpdate(user);
                            Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                })
                .show();
    }
}

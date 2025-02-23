package com.project.myapplication.view.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.myapplication.controller.resetPasswordController;

import com.project.myapplication.R;

public class resetPasswordActivity extends AppCompatActivity {
    resetPasswordController controller;
    ImageButton backBTN;
    EditText emailInput;
    Button getCodeBTN;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        backBTN = findViewById(R.id.backBTN);
        backBTN.setOnClickListener(view -> {
            finish();
        });

        //nút nhận mã
        getCodeBTN = findViewById(R.id.getCodeBTN);
        getCodeBTN.setOnClickListener(view -> {
            emailInput = findViewById(R.id.email_input);
            String email = emailInput.getText().toString().trim();
            controller = new resetPasswordController(getApplicationContext());
            if (!email.isEmpty()) {
                controller.resetPassword(email, getCodeBTN);
            } else {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

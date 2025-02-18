package com.project.myapplication.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.myapplication.controller.resetPasswordController;

import com.project.myapplication.MainActivity;
import com.project.myapplication.R;

public class resetPasswordActivity extends AppCompatActivity {
    resetPasswordController controller;
    ImageButton backBTN;
    TextView label;
    EditText emailInput, phoneNumberInput, inputCode;
    Button acceptBTN, changeMethodBTN, getCodeBTN;
    String labelEmail = "Nhập Email của bạn:";
    String labelPhoneNumber="Nhập số điện thoại của bạn:";
    String emailChangeMethodText = "Tìm kiếm tài khoản bằng Email";
    String phoneNumberChangeMethodText = "Tìm kiếm tài khoản bằng số điện thoại";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        backBTN = findViewById(R.id.backBTN);
        backBTN.setOnClickListener(view -> {
            finish();
        });

        //gắn label + thay đổi input infor khi nhấn đổi phương thức
        changeMethodBTN = findViewById(R.id.change_method_btn);
        changeMethodBTN.setOnClickListener(view -> {
            label = findViewById(R.id.infor_label);
            if(label.getText().toString().equals(labelEmail)){
                label.setText(labelPhoneNumber);
            }
            else{
                label.setText(labelEmail);
            }

            //thanh đổi edittext thông tin để kiếm tài khoản
            emailInput = findViewById(R.id.email_input);
            phoneNumberInput = findViewById(R.id.phonenumberResetPassword);
            if(label.getText().toString().equals(labelPhoneNumber)){
                emailInput.setVisibility(View.GONE);
                phoneNumberInput.setVisibility(View.VISIBLE);
                changeMethodBTN.setText(emailChangeMethodText);
            }
            else{
                emailInput.setVisibility(View.VISIBLE);
                phoneNumberInput.setVisibility(View.GONE);
                changeMethodBTN.setText(phoneNumberChangeMethodText);
            }
        });

        //nút nhận mã
        getCodeBTN = findViewById(R.id.getCodeBTN);
        getCodeBTN.setOnClickListener(view -> {
            emailInput = findViewById(R.id.email_input);
            String email = emailInput.getText().toString().trim();
            controller = new resetPasswordController(getApplicationContext());
            if (!email.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Email đặt lại mật khẩu đã được gửi!", Toast.LENGTH_SHORT).show();
                controller.resetPassword(email);
            } else {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.project.myapplication.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.project.myapplication.DTO.User;
import com.project.myapplication.MainActivity;
import com.project.myapplication.R;
import com.project.myapplication.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Login_page extends AppCompatActivity {
    EditText edtuser,edtpass;
    Button btnlogin,btnregister,btnforgetpass;
    UserModel userModel = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtuser = findViewById(R.id.edtuser);
        edtpass = findViewById(R.id.edtpass);
        btnlogin = findViewById(R.id.btnlogin);
        btnregister = findViewById(R.id.btnregister);
        btnforgetpass = findViewById(R.id.btnforgetpass);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_page.this, register_page.class);
                startActivity(intent);
                finish();
            }
        });
        btnforgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Login_page.this, "Chức năng này chưa được triễn khai", Toast.LENGTH_SHORT).show();
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtuser.getText().toString().trim();
                String pass = edtpass.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    edtuser.setError("Không được để trống Email!");
                    return;
                }

                if(!name.contains("@")){
                    edtuser.setError("Vui lòng nhập email hợp lệ");
                }

                if (TextUtils.isEmpty(pass)) {
                    edtpass.setError("Không được để trống mật khẩu!");
                    return;
                }

                userModel.loginCheck(name, pass, view.getContext(), new UserModel.OnUserLoginCallBack() {
                    @Override
                    public void loginCheck(User user, Boolean success, String noti) {
                        if(noti.equals("Đăng nhập thành công!")){
                            Intent intent = new Intent(Login_page.this, MainActivity.class);
                            intent.putExtra("userID", user.getUserID());
                            Login_page.this.startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(view.getContext(),noti,Toast.LENGTH_SHORT).show();
                        }
                    }
                    @SuppressLint("HardwareIds")
                    public String getDeviceId(Context context) {
                        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    }
                });
            }
        });
    }
}
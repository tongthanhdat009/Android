package com.example.login_page;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class register_page extends AppCompatActivity {
    EditText edtconnect,edtpass,edtusername,edtfullname;
    Button btnregister,btnlogin;
    FirebaseAuth fire = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtconnect = findViewById(R.id.edtconnect);
        edtpass = findViewById(R.id.edtpass);
        edtusername = findViewById(R.id.edtusername);
        edtfullname = findViewById(R.id.edtfullname);
        btnregister = findViewById(R.id.btnregister);
        btnlogin = findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(register_page.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String connect = edtconnect.getText().toString().trim();
                String pass = edtpass.getText().toString().trim();
                String username = edtusername.getText().toString().trim();
                String fullname = edtfullname.getText().toString().trim();
                if(TextUtils.isEmpty(connect)){
                    edtconnect.setError("Nhập Email ");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    edtpass.setError("Nhập mật khẩu");
                    return;
                }
                if(TextUtils.isEmpty(username)){
                    edtusername.setError("Nhập tên người dùng");
                    return;
                }
                if(TextUtils.isEmpty(fullname)){
                    edtfullname.setError("Nhập tên đầy đủ");
                    return;
                }
                fire.createUserWithEmailAndPassword(connect,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = fire.getCurrentUser();
                            String userId = user.getUid();
                            Map<String, Object> usermap = new HashMap<>();
                            usermap.put("username",username);
                            usermap.put("fullname",fullname);
                            usermap.put("email",connect);
                            db.collection("users").document(userId).set(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(register_page.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(register_page.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(register_page.this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(register_page.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
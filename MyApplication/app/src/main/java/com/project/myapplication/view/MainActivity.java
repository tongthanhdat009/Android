package com.example.login_page;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    EditText edtuser,edtpass;
    Button btnlogin,btnregister,btnforgetpass;
    FirebaseAuth fire = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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
                Intent intent = new Intent(MainActivity.this, register_page.class);
                startActivity(intent);
                finish();
            }
        });
        btnforgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Chức năng này chưa được triễn khai", Toast.LENGTH_SHORT).show();
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtuser.getText().toString().trim();
                String pass = edtpass.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    edtuser.setError("Nhập tên người dùng hoặc Email");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    edtpass.setError("Nhập mật khẩu");
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Đang đăng nhập...");
                progressDialog.show();

                if (name.contains("@")) {
                    fire.signInWithEmailAndPassword(name, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        // Lấy UID của người dùng hiện tại
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            String uid = user.getUid();
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        String username = documentSnapshot.getString("username");
                                                        String fullname = documentSnapshot.getString("fullname");

                                                        // Chuyển sang màn hình Home và truyền thông tin
                                                        Intent intent = new Intent(MainActivity.this, Home.class);
                                                        intent.putExtra("username", username);
                                                        intent.putExtra("fullname", fullname);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .whereEqualTo("username", name)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        String email = task.getResult().getDocuments().get(0).getString("email");
                                        String fullname = task.getResult().getDocuments().get(0).getString("fullname");
                                        fire.signInWithEmailAndPassword(email, pass)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                            if (user != null) {
                                                                String uid = user.getUid();
                                                                db.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        if (documentSnapshot.exists()) {
                                                                            String username = documentSnapshot.getString("username");
                                                                            Intent intent = new Intent(MainActivity.this, Home.class);
                                                                            intent.putExtra("username", username);
                                                                            intent.putExtra("fullname", fullname);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(MainActivity.this, "Tên người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fire.getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String fullname = documentSnapshot.getString("fullname");

                        Intent intent = new Intent(MainActivity.this, Home.class);
                        intent.putExtra("username", username);
                        intent.putExtra("fullname", fullname);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }
}
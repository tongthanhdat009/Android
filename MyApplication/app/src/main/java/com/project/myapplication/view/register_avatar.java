package com.project.myapplication.view;

import android.content.Intent;
import androidx.annotation.Nullable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//import com.project.myapplication.view.Profile;
import com.project.myapplication.R;
import com.project.myapplication.view.homeFragment;

public class register_avatar extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String userID;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    ImageView imgAvatar, imgCamera;
    Button btnFinish, btnSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_avatar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgAvatar = findViewById(R.id.imgAvatar);
        imgCamera = findViewById(R.id.imgCamera);
        btnFinish = findViewById(R.id.btn_finish);
        btnSkip = findViewById(R.id.btn_skip);

        userID = getIntent().getStringExtra("userID");
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("avatars/" + userID + ".jpg");
        imgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"), PICK_IMAGE_REQUEST);
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadAvatar(imageUri);
                } else {
                    saveDefaultAvatar();
                }
            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDefaultAvatar();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgAvatar.setImageURI(imageUri);
        }
    }
    private void uploadAvatar(Uri uri) {
        StorageReference avatarRef = storageRef;
        avatarRef.putFile(uri).addOnSuccessListener(taskSnapshot ->
                avatarRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    db.collection("users").document(userID).update("avatar", downloadUri.toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(register_avatar.this, "Avatar đã lưu thành công", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(register_avatar.this, Login_page.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Lỗi khi lưu avatar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
        ).addOnFailureListener(e ->
                Toast.makeText(this, "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveDefaultAvatar() {
        String defaultAvatarUrl = "gs://insta-clone-2e405.appspot.com/avatars/unknow_avatar.jpg";  // Điền URL của avatar mặc định từ Firebase
        db.collection("users").document(userID).update("avatarUrl", defaultAvatarUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã lưu avatar mặc định", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(register_avatar.this, Login_page.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi khi lưu avatar mặc định: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}

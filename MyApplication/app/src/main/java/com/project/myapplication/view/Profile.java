package com.example.login_page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class Profile extends AppCompatActivity {
    private static final long MAX_FILE_SIZE = 8 * 1024 * 1024; // 8MB
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    TextView tvusername, tvfullname;
    Button btnchange;
    ImageView imgAvatar;
    FirebaseUser user;
    FirebaseFirestore db;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase components
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("avatars/" + user.getUid() + ".jpg");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvusername = findViewById(R.id.tvusername);
        tvfullname = findViewById(R.id.tvfullname);
        btnchange = findViewById(R.id.btnchange);
        imgAvatar = findViewById(R.id.imageView);

        // Load user info
        loadUserInfo();

        // Handle change avatar button click
        btnchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                intent1.setType("image/*");
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent1, "Chọn ảnh"), PICK_IMAGE_REQUEST);
            }
        });

        // Handle logout
        tvusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(Profile.this, tvusername);
                popupMenu.getMenuInflater().inflate(R.menu.menu_logout, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.logout) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent1 = new Intent(Profile.this, Login_page.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent1);
                            finish();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Sử dụng biến toàn cục đã khai báo
            try {
                // Kiểm tra kích thước tệp
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                long fileSizeInBytes = inputStream.available();
                inputStream.close();

                if (fileSizeInBytes > MAX_FILE_SIZE) {
                    Toast.makeText(this, "Kích thước tệp quá lớn. Vui lòng chọn tệp nhỏ hơn.", Toast.LENGTH_SHORT).show();
                    return; // Không tiến hành upload
                }

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgAvatar.setImageBitmap(bitmap);

                // Tiến hành upload hình ảnh
                uploadImageToFirebase(imageUri); // Gọi phương thức upload ảnh

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Có lỗi xảy ra khi xử lý hình ảnh.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Upload image to Firebase Storage
    private void uploadImageToFirebase(Uri uri) {
        if (imageUri != null) {
            StorageReference avatarRef = storageRef;
            avatarRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Save image URL to Firestore
                            db.collection("users").document(user.getUid()).update("avatarUrl", uri.toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Profile.this, "Avatar updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@androidx.annotation.NonNull Exception e) {
                    Toast.makeText(Profile.this, "Failed to upload avatar", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Load user info including avatar
    private void loadUserInfo() {
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    String fullname = documentSnapshot.getString("fullname");
                    String avatarUrl = documentSnapshot.getString("avatarUrl");

                    if (username != null) {
                        tvusername.setText(username);
                    }
                    if (fullname != null) {
                        tvfullname.setText(fullname);
                    }
                    if (avatarUrl != null) {
                        Glide.with(Profile.this).load(avatarUrl).into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.drawable.images); // Sử dụng ảnh đại diện mặc định nếu không có avatarUrl
                    }
                }
            }
        });
    }
}

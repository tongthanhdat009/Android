package com.project.myapplication.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.myapplication.R;
import com.project.myapplication.view.Login_page;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class Profile extends AppCompatActivity {
    private static final long MAX_FILE_SIZE = 8 * 1024 * 1024; // 8MB
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    TextView tvusername, tvfullname,tvBiography;
    Button btnedit;
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
        btnedit = findViewById(R.id.btnedit);
        imgAvatar = findViewById(R.id.imageView);
        tvBiography = findViewById(R.id.tvBiography);

        // Load user info
        loadUserInfo();

        // Handle edit profile button click
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show PopupMenu for profile options
                PopupMenu popupMenu = new PopupMenu(Profile.this, btnedit);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.edit_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.menu_change_avatar) {
                            openImagePicker();
                            return true;
                        } else if (menuItem.getItemId() == R.id.menu_edit_bio) {
                            // Mở trang chỉnh sửa tiểu sử
                            editBiography();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
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
            imageUri = data.getData();
            try {

                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                long fileSizeInBytes = inputStream.available();
                inputStream.close();

                if (fileSizeInBytes > MAX_FILE_SIZE) {
                    Toast.makeText(this, "Kích thước tệp quá lớn. Vui lòng chọn tệp nhỏ hơn.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgAvatar.setImageBitmap(bitmap);


                uploadImageToFirebase(imageUri);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Có lỗi xảy ra khi xử lý hình ảnh.", Toast.LENGTH_SHORT).show();
            }
        }
    }


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


    private void loadUserInfo() {
        db.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    String fullname = documentSnapshot.getString("fullname");
                    String avatarUrl = documentSnapshot.getString("avatarUrl");
                    String biography = documentSnapshot.getString("biography");

                    if (username != null) {
                        tvusername.setText(username);
                    }
                    if (fullname != null) {
                        tvfullname.setText(fullname);
                    }
                    if (avatarUrl != null) {
                        Picasso.get().load(avatarUrl).into(imgAvatar);
                    }
                    else {
                        imgAvatar.setImageResource(R.drawable.unknow_avatar);
                    }
                    if (biography != null) {
                        tvBiography.setText(biography);
                    }
                }
            }
        });
    }


    private void openImagePicker() {
        Intent intent1 = new Intent();
        intent1.setType("image/*");
        intent1.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent1, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }


    private void editBiography() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa tiểu sử");


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setLines(3);
        builder.setView(input);

        // Nút Lưu
        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newBiography = input.getText().toString().trim();

                if (!newBiography.isEmpty()) {
                    tvBiography.setText(newBiography);
                    db.collection("users").document(user.getUid())
                            .update("biography", newBiography)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Profile.this, "Tiểu sử đã được cập nhật", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Profile.this, "Lỗi khi cập nhật tiểu sử", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Profile.this, "Tiểu sử không được để trống", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nút Hủy
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}

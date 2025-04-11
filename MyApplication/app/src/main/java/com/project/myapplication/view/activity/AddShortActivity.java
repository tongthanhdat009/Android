package com.project.myapplication.view.activity;

import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.myapplication.DTO.ShortVideo;
import com.project.myapplication.R;
import com.project.myapplication.model.ShortModel;
import com.project.myapplication.view.adapter.VideoGalleryAdapter;

import java.util.ArrayList;
import java.util.UUID;

public class AddShortActivity extends AppCompatActivity implements VideoGalleryAdapter.OnVideoSelectListener {

    private static final int REQUEST_VIDEO_PERMISSION = 100;

    private Uri selectedVideoUri;
    private EditText titleEditText;
    private Button uploadButton;
    private RecyclerView videoRecyclerView;
    private String userID;
    private ShortModel shortModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_short);

        userID = getIntent().getStringExtra("userID");
        shortModel = new ShortModel();

        titleEditText = findViewById(R.id.titleEditText);
        uploadButton = findViewById(R.id.uploadButton);
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        requestVideoPermission();
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        uploadButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedVideoUri != null) {
                uploadButton.setEnabled(false);
                uploadButton.setAlpha(0.5f);
                checkVideoDurationAndUpload(selectedVideoUri);
            } else {
                Toast.makeText(this, "Vui lòng chọn video", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void requestVideoPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_VIDEO}, REQUEST_VIDEO_PERMISSION);
            } else {
                loadVideosFromGallery();
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_VIDEO_PERMISSION);
            } else {
                loadVideosFromGallery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_VIDEO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadVideosFromGallery();
            } else {
                Toast.makeText(this, "Cần cấp quyền để truy cập video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadVideosFromGallery() {
        ArrayList<Uri> videoUris = new ArrayList<>();
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DURATION
        };

        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Video.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                long duration = cursor.getLong(durationColumn);
                Log.d("AddShortActivity", "Video found: id=" + id + ", duration=" + duration);

                if (duration <= 60_000) { // chỉ lấy video dưới 1 phút
                    Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                    videoUris.add(uri);
                }
            }
            cursor.close();
        }

        if (videoUris.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy video hợp lệ", Toast.LENGTH_SHORT).show();
        }

        VideoGalleryAdapter adapter = new VideoGalleryAdapter(this, videoUris, this);
        videoRecyclerView.setAdapter(adapter);
    }

    private void checkVideoDurationAndUpload(Uri uri) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(uri, "r");
            if (afd != null) {
                retriever.setDataSource(afd.getFileDescriptor());
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long durationMillis = Long.parseLong(time);
                afd.close();

                if (durationMillis <= 60_000) {
                    uploadVideoToFirebase(uri);
                } else {
                    Toast.makeText(this, "Chỉ được chọn video dưới 1 phút", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Không thể đọc video", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi đọc video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideoToFirebase(Uri uri) {
        String fileName = UUID.randomUUID().toString() + ".mp4";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("short/" + fileName);

        storageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            ShortVideo shortVideo = new ShortVideo();
                            shortVideo.setUserID(userID);
                            shortVideo.setTitle(titleEditText.getText().toString());
                            shortVideo.setVideoUrl(downloadUri.toString());

                            shortModel.addShortVideo(shortVideo, success -> {
                                uploadButton.setEnabled(true);
                                if (success) {
                                    Toast.makeText(this, "Upload thành công!", Toast.LENGTH_SHORT).show();
                                    uploadButton.postDelayed(this::finish, 500); // delay để hiển thị Toast
                                } else {
                                    Toast.makeText(this, "Lưu thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }))
                .addOnFailureListener(e -> {
                    uploadButton.setEnabled(true);
                    Toast.makeText(this, "Upload thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onVideoSelected(Uri uri) {
        selectedVideoUri = uri;
    }
}

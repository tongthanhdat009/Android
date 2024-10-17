package com.project.myapplication.view;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.myapplication.R;
import com.squareup.picasso.Picasso;

public class FullscreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        ImageView imageView = findViewById(R.id.fullscreen_image);

        // Lấy URI từ Intent
        Uri imageUri = getIntent().getParcelableExtra("imageUri");

        // Hiển thị ảnh bằng Picasso
        if (imageUri != null) {
            Log.d("FullscreenImageActivity", "Image URI: " + imageUri.toString());
            try {
                Picasso.get()
                        .load(imageUri)
                        .into(imageView);
            } catch (Exception e) {
                Log.e("PicassoError", "Error loading image: ", e);
            }
        } else {
            Log.e("FullscreenImageActivity", "Image URI is null!");
        }
    }
}

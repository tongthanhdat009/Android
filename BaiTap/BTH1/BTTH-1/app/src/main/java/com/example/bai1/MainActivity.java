package com.example.bai1;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.android);

        setContentView(img);
    }
}

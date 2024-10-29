package com.project.myapplication.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.R;
import com.project.myapplication.controller.commentController;

public class commentActivity extends AppCompatActivity {
    private RecyclerView commentRecyclerView; // Để sử dụng RecyclerView
    private String postID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment); // Đảm bảo layout này chứa RecyclerView

        // Nhận postID từ Intent
        postID = getIntent().getStringExtra("postID");

        // Thiết lập RecyclerView
        commentRecyclerView = findViewById(R.id.comment_recycler_view);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Đảm bảo ID đúng

        // Khởi tạo controller và hiển thị comment
        commentController controller = new commentController(commentRecyclerView);
        controller.commentDiplay(postID);
    }
}

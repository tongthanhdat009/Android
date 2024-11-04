package com.project.myapplication.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Comment;
import com.project.myapplication.R;
import com.project.myapplication.controller.commentController;

public class commentActivity extends AppCompatActivity {
    private RecyclerView commentRecyclerView; // Để sử dụng RecyclerView
    private String postID;
    private String userID;
    private commentController controller;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment); // Đảm bảo layout này chứa RecyclerView

        EditText commentInput = findViewById(R.id.type_comment);
        ImageView sendComment = findViewById(R.id.send_btn);
        ImageButton backBTN = findViewById(R.id.backBTN);

        // Nhận postID, userID từ Intent
        postID = getIntent().getStringExtra("postID");
        userID = getIntent().getStringExtra("userID");

        // Thiết lập RecyclerView
        commentRecyclerView = findViewById(R.id.comment_recycler_view);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo controller và hiển thị comment
        commentController controller = new commentController(commentRecyclerView);
        controller.commentDiplay(postID, userID);
        commentInput.setLongClickable(false);
        commentInput.setOnLongClickListener(v -> true); // Chặn long click
        commentInput.setTextIsSelectable(false); // Vô hiệu hóa chế độ lựa chọn văn bản

// Vô hiệu hóa menu ngữ cảnh
        commentInput.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false; // Không hiển thị menu ngữ cảnh
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

// Vô hiệu hóa menu chèn văn bản khi nhấn vào điểm chèn
        commentInput.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

// Sử dụng InputConnectionWrapper để chặn cắt, sao chép, dán
        commentInput.setCustomInsertionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentInput.getText().toString();
                Toast.makeText(v.getContext(), commentText, Toast.LENGTH_SHORT).show();
                if(commentInput.getText().toString().isEmpty()){
                    Toast.makeText(v.getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                }
                else{
                    controller.postComment(commentText, postID, userID);
                    commentInput.setText(""); // Xóa nội dung sau khi gửi
                }

            }
        });

        backBTN.setOnClickListener(v -> finish());
    }
}

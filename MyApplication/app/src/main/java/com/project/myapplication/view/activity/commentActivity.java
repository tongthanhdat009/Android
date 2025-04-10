package com.project.myapplication.view.activity;

import android.os.Bundle;
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

import com.google.firebase.Timestamp;
import com.project.myapplication.DTO.Comment;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.controller.commentController;
import com.project.myapplication.model.CommentModel;
import com.project.myapplication.model.NotificationModel;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.model.UserModel;

import java.util.ArrayList;

public class commentActivity extends AppCompatActivity {
    private RecyclerView commentRecyclerView; // Để sử dụng RecyclerView
    private String postID;
    private String userID;
    private commentController controller;
    private UserModel userModel;
    private NotificationModel notificationModel;
    private PostModel postModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment); // Đảm bảo layout này chứa RecyclerView

        userModel = new UserModel();
        postModel = new PostModel();
        notificationModel = new NotificationModel();

        EditText commentInput = findViewById(R.id.type_comment);
        ImageView sendComment = findViewById(R.id.send_btn);
        ImageButton backBTN = findViewById(R.id.backBTN);
        TextView notification = findViewById(R.id.notification);
        CommentModel commentModel = new CommentModel();
        // Nhận postID, userID từ Intent
        postID = getIntent().getStringExtra("postID");
        userID = getIntent().getStringExtra("userID");
        commentModel.getAllCommentInPost(postID, new CommentModel.OnCommentListRetrievedCallback() {
            @Override
            public void getAllCommentInPost(ArrayList<Comment> commentsList) {
                if (commentsList.isEmpty()) {
                    notification.setVisibility(View.VISIBLE);
                } else {
                    notification.setVisibility(View.GONE);
                }
            }
        });

        // Thiết lập RecyclerView
        commentRecyclerView = findViewById(R.id.comment_recycler_view);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo controller và hiển thị comment
        commentController controller = new commentController(commentRecyclerView);
        controller.commentDiplay(postID, userID);
        commentInput.setLongClickable(false);
        commentInput.setOnLongClickListener(v -> true); // Chặn long click
        commentInput.setTextIsSelectable(false); // Vô hiệu hóa chế độ lựa chọn văn bản

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentInput.getText().toString();
                Toast.makeText(v.getContext(), commentText, Toast.LENGTH_SHORT).show();
                if (commentInput.getText().toString().isEmpty()) {
                    Toast.makeText(v.getContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                } else {
                    controller.postComment(commentText, postID, userID);
                    userModel.getUserInfor(userID, new UserModel.OnGetUserInfor() {
                        @Override
                        public void getInfor(User user) {
                            postModel.getPostByID(postID, new PostModel.OnGetPostByID() {
                                @Override
                                public void getPostByID(Post post) {
                                    if (!user.getUserID().equals(post.getUserID()))
                                        notificationModel.addNotification(
                                                user.getName() + " Đã bình luận vào bài viết của bạn",
                                                false,
                                                user.getUserID(),
                                                Timestamp.now(),
                                                "Thông báo mới",
                                                "Comment",
                                                post.getUserID()
                                        );
                                }
                            });
                        }
                    });

                    commentInput.setText(""); // Xóa nội dung sau khi gửi
                }

            }
        });

        backBTN.setOnClickListener(v -> finish());
    }
}

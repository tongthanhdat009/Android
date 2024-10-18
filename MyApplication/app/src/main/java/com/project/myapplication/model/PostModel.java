package com.project.myapplication.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.myapplication.DTO.Comment;
import com.project.myapplication.DTO.Post;

public class PostModel {
    private final FirebaseFirestore firestore;

    public PostModel() {
        // Khởi tạo Firestore
        firestore = FirebaseFirestore.getInstance();
    }
    // Thêm post mới và comment vào cây dữ liệu
    public void addPostWithComment(Post post, Comment comment) {
        // Tham chiếu đến collection "posts"
        CollectionReference postsRef = firestore.collection("post");

        // Thêm một bài đăng mới
        postsRef.add(post).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    // Thành công, lấy ID của bài đăng vừa thêm
                    String postId = task.getResult().getId();
                    System.out.println("Post added successfully with ID: " + postId);
                    CollectionReference commentsRef = postsRef.document(postId).collection("Comments");
                    // Thêm comment dưới bài đăng
                    commentsRef.add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                // Thành công
                                System.out.println("Comment added successfully with ID: " + task.getResult().getId());
                            } else {
                                // Xử lý lỗi khi thêm comment
                                System.err.println("Error adding comment: " + task.getException());
                            }
                        }
                    });
                } else {
                    // Xử lý lỗi khi thêm post
                    System.err.println("Error adding post: " + task.getException());
                }
            }
        });
    }
}

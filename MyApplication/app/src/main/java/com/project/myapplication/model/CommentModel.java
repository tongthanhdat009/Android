package com.project.myapplication.model;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.DTO.Comment;

import java.util.ArrayList;

public class CommentModel {
    private final FirebaseFirestore firestore;
    public CommentModel() {
        // Khởi tạo Firestore
        firestore = FirebaseFirestore.getInstance();
    }
    public void getAllCommentInPost(String postID, CommentModel.OnCommentListRetrievedCallback callback){
        CollectionReference cmtRef = firestore.collection("post").document(postID).collection("Comments");
        cmtRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    ArrayList<Comment> commentList = new ArrayList<>();
                    // Duyệt qua từng DocumentSnapshot trong QuerySnapshot
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Comment cmt = document.toObject(Comment.class);
                        commentList.add(cmt);
                    }
                    // Gọi callback với danh sách bài đăng
                    callback.getAllCommentInPost(commentList);
                } else {
                    Log.d("PostModel", "No posts found");
                }
            } else {
                System.err.println("Error getting comments: " + task.getException());
            }
        });
    }
    //lấy tất cả comment trong 1 post
    public interface OnCommentListRetrievedCallback {
        void getAllCommentInPost(ArrayList<Comment> commentsList);
    }
}

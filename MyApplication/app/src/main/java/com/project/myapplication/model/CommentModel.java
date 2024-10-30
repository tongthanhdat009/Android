package com.project.myapplication.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.DTO.Comment;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public void getUserCommentInfor(String userID, CommentModel.onUserCommentRetrievedCallBack callback) {
        // Tham chiếu đến collection "users"
        CollectionReference usersRef = firestore.collection("users");

        // Lấy document dựa trên userID
        usersRef.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Gọi callback với userID
                        User tempUser = document.toObject(User.class);
                        callback.getUserCommentInfor(tempUser);
                    } else {
                        Log.d("PostModel", "No such user with ID: " + userID);
                    }
                } else {
                    Log.e("PostModel", "Error getting document: " + task.getException());
                }
            }
        });
    }

    public void commentUpdate(Comment cmt, String postID){
        DocumentReference docPost = firestore.collection("post").document(postID).collection("Comments").document(cmt.getCommentID());
        Map<String, Object> updates = new HashMap<>();
        updates.put("likedBy", cmt.getLikedBy());
        updates.put("likesCount", cmt.getLikedBy().size());
        updates.put("content", cmt.getCommentText());
        docPost.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error updating document", e);
                    }
                });
    }

    public void addComment(Comment comment, String postID) {
        // Tham chiếu đến collection "posts"
        CollectionReference postsRef = firestore.collection("post").document(postID).collection("Comments");

        // Thêm một bài đăng mới
        postsRef.add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    // Thành công, lấy ID của bài đăng vừa thêm
                    String commentId = task.getResult().getId();

                    // Cập nhật ID cho bài đăng
                    comment.setCommentID(commentId); // Cập nhật ID vào đối tượng Post

                    // Cập nhật lại bài đăng với ID mới (nếu cần thiết)
                    postsRef.document(commentId).set(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                System.out.println("Post updated with ID: " + commentId);
                            } else {
                                System.err.println("Error updating post ID: " + task.getException());
                            }
                        }
                    });

                    // Tạo sub-collection "Comments" cho bài đăng
                    CollectionReference commentsRef = postsRef.document(commentId).collection("Comments");
                    System.out.println("Comments sub-collection created for post ID: " + commentId);

                    // Tại đây, bạn có thể thêm bình luận vào commentsRef sau này nếu cần thiết

                } else {
                    System.err.println("Error adding post: " + task.getException());
                }
            }
        });
    }

    public interface onUserCommentRetrievedCallBack{
        void getUserCommentInfor(User user);
    }

    //lấy tất cả comment trong 1 post
    public interface OnCommentListRetrievedCallback {
        void getAllCommentInPost(ArrayList<Comment> commentsList);
    }
}

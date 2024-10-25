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

public class PostModel {
    private final FirebaseFirestore firestore;

    public PostModel() {
        // Khởi tạo Firestore
        firestore = FirebaseFirestore.getInstance();
    }

    // Thêm post mới và tạo sub-collection cho comment
    public void addPost(Post post) {
        // Tham chiếu đến collection "posts"
        CollectionReference postsRef = firestore.collection("post");

        // Thêm một bài đăng mới
        postsRef.add(post).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    // Thành công, lấy ID của bài đăng vừa thêm
                    String postId = task.getResult().getId();
                    System.out.println("Post added successfully with ID: " + postId);

                    // Cập nhật ID cho bài đăng
                    post.setPostID(postId); // Cập nhật ID vào đối tượng Post

                    // Cập nhật lại bài đăng với ID mới (nếu cần thiết)
                    postsRef.document(postId).set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                System.out.println("Post updated with ID: " + postId);
                            } else {
                                System.err.println("Error updating post ID: " + task.getException());
                            }
                        }
                    });

                    // Tạo sub-collection "Comments" cho bài đăng
                    CollectionReference commentsRef = postsRef.document(postId).collection("Comments");
                    System.out.println("Comments sub-collection created for post ID: " + postId);

                    // Tại đây, bạn có thể thêm bình luận vào commentsRef sau này nếu cần thiết

                } else {
                    System.err.println("Error adding post: " + task.getException());
                }
            }
        });
    }

    public void getUserInfor(String userID, OnUserListRetrievedCallback callback) {
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
                        callback.onUserListRetrievedCallback(tempUser);
                    } else {
                        Log.d("PostModel", "No such user with ID: " + userID);
                    }
                } else {
                    Log.e("PostModel", "Error getting document: " + task.getException());
                }
            }
        });
    }

    public void getAllPost(OnPostListRetrievedCallback callback) {
        CollectionReference postRef = firestore.collection("post");

        postRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        ArrayList<Post> postList = new ArrayList<>();

                        // Duyệt qua từng DocumentSnapshot trong QuerySnapshot
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Post post = document.toObject(Post.class);
                            postList.add(post);
                        }

                        // Gọi callback với danh sách bài đăng
                        callback.getAllPost(postList);
                    } else {
                        Log.d("PostModel", "No posts found");
                    }
                } else {
                    Log.e("PostModel", "Error getting documents: " + task.getException());
                }
            }
        });
    }

    public void postUpdate(Post post) {
        DocumentReference docPost = firestore.collection("post").document(post.getPostID());
        Map<String, Object> updates = new HashMap<>();
        updates.put("likedBy", post.getLikedBy());
        updates.put("likesCount", post.getLikedBy().size());
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

    // Định nghĩa interface callback
    public interface OnPostListRetrievedCallback {
        void getAllPost(ArrayList<Post> postsList); // Sửa tên phương thức cho phù hợp
    }

    public interface OnUserListRetrievedCallback {
        void onUserListRetrievedCallback(User user);
    }
}

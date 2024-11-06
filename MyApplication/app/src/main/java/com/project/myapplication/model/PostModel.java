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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.DTO.Comment;
import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Following;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;

import org.checkerframework.checker.interning.qual.CompareToMethod;

import java.lang.reflect.Array;
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

    public void getUserInfor(String userID, OnUserRetrievedCallback callback) {
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
                        callback.onUserRetrievedCallback(tempUser);
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

        postRef.orderBy("time", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    public void deletePost(Post post, OnPostDeletedCallback callback) {
        DocumentReference postRef = firestore.collection("post").document(post.getPostID());

        postRef.delete().addOnSuccessListener(aVoid -> {
            // Xóa thành công
            Log.d("PostModel", "Post successfully deleted!");
            callback.onPostDeleted(true);
        }).addOnFailureListener(e -> {
            // Xóa thất bại
            Log.e("PostModel", "Error deleting post: ", e);
            callback.onPostDeleted(false);
        });
    }

    public void postUpdate(Post post) {
        DocumentReference docPost = firestore.collection("post").document(post.getPostID());
        Map<String, Object> updates = new HashMap<>();
        updates.put("likedBy", post.getLikedBy());
        updates.put("likesCount", post.getLikedBy().size());
        updates.put("content", post.getContent());
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

    public void getAllFollowing(String userID, OnFollowingListRetrievedCallback callback){
        CollectionReference followingRef = firestore.collection("users")
                .document(userID)
                .collection("following");

        followingRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        ArrayList<Following> followingList = new ArrayList<>();

                        // Duyệt qua từng DocumentSnapshot trong QuerySnapshot
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Following following = document.toObject(Following.class);
                            followingList.add(following);
                        }
                        callback.getAllFollowing(followingList);
                    } else {
                        Log.d("PostModel", "No following found");
                        ArrayList<Following> followingList = new ArrayList<>();
                        callback.getAllFollowing(followingList);
                    }
                } else {
                    Log.e("PostModel", "Error getting documents: " + task.getException());
                }
            }
        });
    }

    public void getAllFollower(String authorID, OnFollowerListRetrievedCallback callback){
        CollectionReference followerRef = firestore.collection("users")
                .document(authorID)
                .collection("followers");

        followerRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        ArrayList<Followers> followerList = new ArrayList<>();

                        // Duyệt qua từng DocumentSnapshot trong QuerySnapshot
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Followers follower = document.toObject(Followers.class);
                            followerList.add(follower);
                        }

                        // Gọi callback với danh sách bài đăng
                        callback.getAllFollower(followerList);
                    } else {
                        Log.d("PostModel", "No follower found");
                        ArrayList<Followers> followerList = new ArrayList<>();
                        callback.getAllFollower(followerList);
                    }
                } else {
                    Log.e("PostModel", "Error getting documents: " + task.getException());
                }
            }
        });
    }

    public void addFollowingUser(String userID, Following following, OnAddFollowingCallback callback) {
        CollectionReference followingRef = firestore.collection("users").document(userID).collection("following");
        String generatedID = followingRef.document().getId();
        following.setIdFollowing(generatedID);
        followingRef.add(following).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("PostModel", "Following user successfully!");
                    String followingID = task.getResult().getId();
                    following.setIdFollowing(followingID);
                    followingRef.document(followingID).set(following).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                System.out.println("Post updated with ID: " + followingID);
                                callback.onAddFollowing(true);
                            } else {
                                System.err.println("Error updating post ID: " + task.getException());
                            }
                        }
                    });
                }
                else {
                    Log.d("PostModel", "Following user failed!");
                }

            }
        });
    }

    public void addFollowerUser(String authorID, Followers follower, OnAddFollowerCallback callback){
        CollectionReference followerRef = firestore.collection("users").document(authorID).collection("followers");
        followerRef.add(follower).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Log.d("PostModel", "Follower added successfully!");
                    String followerID = task.getResult().getId();
                    follower.setIdFollower(followerID);
                    followerRef.document(followerID).set(follower).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                System.out.println("Post updated with ID: " + followerID);
                                callback.onAddFollower(true);
                            } else {
                                System.err.println("Error updating post ID: " + task.getException());
                            }
                        }
                    });
                }
                else {
                    Log.d("PostModel", "Follower not added failed!");
                }

            }
        });
    }

    public void removeFollowingUser(String userID, String followingId, OnRemoveFollowingCallback callback) {
        DocumentReference followingRef = firestore.collection("users")
                .document(userID)
                .collection("following")
                .document(followingId);

        followingRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    System.out.println("Successfully removed following with ID: " + followingId);
                    callback.onRemoveFollowing(false);
                } else {
                    System.err.println("Error removing document: " + task.getException());
                }
            }
        });
    }

    public void removeFollowerUser(String authorID, String followerId, OnRemoveFollowerCallback callback) {
        DocumentReference followingRef = firestore.collection("users")
                .document(authorID)
                .collection("followers")
                .document(followerId);

        followingRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    System.out.println("Successfully removed follower with ID: " + followerId);
                    callback.onRemoveFollower(false);
                } else {
                    System.err.println("Error removing document: " + task.getException());
                }
            }
        });
    }

    public void getUserPost(String userID, OnUserPostListRetrievedCallback callback){
        Query postQuery = firestore.collection("post").whereEqualTo("userID", userID);
        postQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Post> postsList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        postsList.add(document.toObject(Post.class));
                    }
                    callback.getUserPost(postsList);
                }
                else{
                    Log.d("Firebase", "Lỗi không lấy được danh sách post người dùng đã đăng",task.getException());
                }
            }
        });
    }

    public void getPostByID(String postID, OnGetPostByID callback) {
        Query postQuery = firestore.collection("post").whereEqualTo("postID", postID);
        postQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Post post = null;
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            post = document.toObject(Post.class);
                            break;
                        }
                        if (post != null) {
                            callback.getPostByID(post);
                        } else {
                            Log.d("Firebase", "Không tìm thấy post với ID: " + postID);
                        }
                    } else {
                        Log.d("Firebase", "Không có dữ liệu với ID: " + postID);
                    }
                } else {
                    Log.d("Firebase", "Lỗi không lấy được danh sách post người dùng đã đăng", task.getException());
                }
            }
        });
    }

    // Định nghĩa interface callback
    // Lấy tất cả bài đăng của 1 người dùng
    public interface  OnUserPostListRetrievedCallback {
        void getUserPost(ArrayList<Post> postsList);
    }
    // Lấy tất cả post
    public interface OnPostListRetrievedCallback {
        void getAllPost(ArrayList<Post> postsList); // Sửa tên phương thức cho phù hợp
    }
    // lấy thông tin người dăng
    public interface OnUserRetrievedCallback {
        void onUserRetrievedCallback(User user);
    }
    // lấy thông tin người đang theo dõi dăng
    public interface OnFollowingListRetrievedCallback {
        void getAllFollowing(ArrayList<Following> followingList);
    }
    // lấy thông tin người theo dõi dăng
    public interface OnFollowerListRetrievedCallback {
        void getAllFollower(ArrayList<Followers> followerList);
    }
    // xóa post
    public interface OnPostDeletedCallback {
        void onPostDeleted(boolean success);
    }
    // hàm lấy thông tin khi người dùng nhấn follow hoặc unfollow
    public interface  OnAddFollowerCallback{
        void onAddFollower(boolean success);
    }
    public interface  OnAddFollowingCallback{
        void onAddFollowing(boolean success);
    }
    public interface  OnRemoveFollowerCallback{
        void onRemoveFollower(boolean success);
    }
    public interface  OnRemoveFollowingCallback{
        void onRemoveFollowing(boolean success);
    }
    public interface OnGetPostByID{
        void getPostByID(Post post);
    }
}

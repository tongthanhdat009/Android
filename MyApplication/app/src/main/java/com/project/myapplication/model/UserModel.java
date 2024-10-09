package com.project.myapplication.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.myapplication.DTO.User;

public class UserModel {
    private final FirebaseFirestore firestore;

    public UserModel() {
        firestore = FirebaseFirestore.getInstance(); // Khởi tạo Firestore
    }

    // Thêm người dùng
    public void addUser(User user, final OnUserAddedCallback callback) {
        firestore.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        callback.onUserAdded(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onUserAdded(null);
                    }
                });
    }

    // Lấy người dùng
    public void getUser(String userId, final OnUserRetrievedCallback callback) {
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document != null) {
                            User user = document.toObject(User.class);
                            callback.onUserRetrieved(user);
                        } else {
                            callback.onUserRetrieved(null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onUserRetrieved(null);
                    }
                });
    }

    // Sửa người dùng
    public void updateUser(String userId, User updatedUser, final OnUserUpdatedCallback callback) {
        firestore.collection("users").document(userId).set(updatedUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onUserUpdated(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onUserUpdated(false);
                    }
                });
    }

    // Xóa người dùng
    public void deleteUser(String userId, final OnUserDeletedCallback callback) {
        firestore.collection("users").document(userId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onUserDeleted(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onUserDeleted(false);
                    }
                });
    }

    // Interface callback cho việc thêm người dùng
    public interface OnUserAddedCallback {
        void onUserAdded(String userId);
    }

    // Interface callback cho việc lấy người dùng
    public interface OnUserRetrievedCallback {
        void onUserRetrieved(User user);
    }

    // Interface callback cho việc sửa người dùng
    public interface OnUserUpdatedCallback {
        void onUserUpdated(boolean success);
    }

    // Interface callback cho việc xóa người dùng
    public interface OnUserDeletedCallback {
        void onUserDeleted(boolean success);
    }
}

//    User newUser = new User("abc@gmail.com", "abc", "abcd", "abcd");
//        userModel.addUser(newUser, new UserModel.OnUserAddedCallback() {
//        @Override
//        public void onUserAdded(String userId) {
//            if (userId != null) {
//                Log.d("TAG", "User added with ID: " + userId);
//            } else {
//                Log.e("TAG", "Error adding user");
//            }
//        }
//    });



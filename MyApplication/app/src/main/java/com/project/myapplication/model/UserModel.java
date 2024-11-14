package com.project.myapplication.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.myapplication.DTO.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;

public class UserModel {
    private final FirebaseFirestore firestore;

    public UserModel() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void addUser(User user, OnUserRegisterCallback callback){
        CollectionReference userRef = firestore.collection("users");
        userRef.add(user).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    String userId = task.getResult().getId();
                    user.setUserID(userId);

                    userRef.document(userId).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                callback.register(user,task.isSuccessful());
                            } else {
                                callback.register(user,task.isSuccessful());
                            }
                        }
                    });
                } else {
                    System.err.println("Error adding post: " + task.getException());
                }
            }
        });
    }

    public void loginCheck(String email, String pass, Context context, OnUserLoginCallBack callback) {
        CollectionReference userRef = firestore.collection("users");
        userRef.whereEqualTo("Email", email)
                .whereEqualTo("Password", pass)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Đăng nhập thành công, trả về thông tin người dùng
                        DocumentSnapshot userDoc = task.getResult().getDocuments().get(0);
                        User user = userDoc.toObject(User.class);

                        if (user != null) {
                            // Lấy Android ID của thiết bị
                            if(user.getLogged().isEmpty()){
                                String deviceId = getDeviceId(context);

                                // Lưu mã thiết bị vào Firestore
                                userRef.document(userDoc.getId()).update("Logged", deviceId)
                                        .addOnCompleteListener(deviceTask -> {
                                            if (deviceTask.isSuccessful()) {
                                                callback.loginCheck(user, true, "Đăng nhập thành công!");
                                            } else {
                                                callback.loginCheck(null, false, "Không thể lưu mã thiết bị.");
                                            }
                                        });
                            }
                            else{
                                callback.loginCheck(null, false, "Tài khoản đã được đăng nhập ở một thiết bị khác");
                            }
                        } else {
                            callback.loginCheck(null, false, "User không tồn tại");
                        }
                    } else {
                        callback.loginCheck(null, false, "Email hoặc mật khẩu không tồn tại");
                    }
                });
    }

    public void loggedCheck(String deviceID, OnLoggedCheckCallback callback){
        CollectionReference userRef = firestore.collection("users");
        userRef.whereEqualTo("Logged", deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot userDoc = task.getResult().getDocuments().get(0);
                        User user = userDoc.toObject(User.class);
                        assert user != null;
                        user.setUserID(userDoc.getId());
                        callback.loggedCheck(user);
                    }
                    else{
                        callback.loggedCheck(null);
                    }
                });
    }

    public interface OnLoggedCheckCallback{
        void loggedCheck(User user);
    }

    // Hàm lấy mã thiết bị
    @SuppressLint("HardwareIds")
    public String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public interface OnUserLoginCallBack{
        void loginCheck(User user,Boolean success, String noti);
    }
    public interface UpdateDeviceIDCallback{
        void updateDeviceID(Boolean success, String deviceID);
    }
    public interface OnUserRegisterCallback{
        void register(User registedUser,Boolean success);
    }
}

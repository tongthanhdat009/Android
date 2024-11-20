package com.project.myapplication.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.myapplication.DTO.User;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserModel {
    private final FirebaseFirestore firestore;

    public UserModel() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void addUser(User user, OnUserRegisterCallback callback){
        CollectionReference userRef = firestore.collection("users");
        // Tạo map chứa thông tin
        Map<String, Object> userData = new HashMap<>();
        userData.put("Biography", user.getBiography());
        userData.put("Email", user.getEmail());
        userData.put("Logged", "");
        userData.put("Name", user.getName());
        userData.put("Password", user.getPassword());
        userData.put("UserName", user.getUserName());
        userData.put("avatar", "https://firebasestorage.googleapis.com/v0/b/insta-clone-2e405.appspot.com/o/avatars%2Funknow_avatar.jpg?alt=media&token=e28a3dcc-6925-4abc-b4ef-9998c32ec364");

        userRef.add(userData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    String userId = task.getResult().getId();
                    userData.put("UserID",userId);
                    user.setUserID(userId);
                    userRef.document(userId).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void userUpdate(User user) {
        DocumentReference docUser= firestore.collection("users").document(user.getUserID());
        Map<String, Object> updates = new HashMap<>();
        updates.put("Logged", user.getLogged());
        updates.put("Biography", user.getBiography());
        updates.put("avatar", user.getAvatar());
        docUser.update(updates)
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

    public void uploadAvatar(Uri uri, String userId, OnUpdateAvatarCallback callback){
        // Lấy instance của Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference avatarRef = storage.getReference().child("avatars/" + userId + ".jpg");
        // Upload file lên Firebase Storage
        avatarRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Có thể lấy link tải ảnh về nếu cần
                    avatarRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String avatarUrl = downloadUri.toString();
                        // Lưu avatarUrl vào Firestore hoặc Realtime Database nếu cần
                        callback.updateAvatar(avatarUrl,"Thay đổi avatar thành công");

                    });
                })
                .addOnFailureListener(e ->
                        callback.updateAvatar(null,"Thay đổi avatar không thành công")
                );
    }

    public void emailCheck(String email, OnCheckEmailCallBack callBack){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("Email", email) // Tìm tài liệu có trường email khớp
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult().isEmpty()){
                            String regex_email = "^[a-zA-Z0-9_+&*-]+(?:\\\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+.)+[a-zA-Z]{2,7}$";
                            Pattern p_email = Pattern.compile(regex_email);
                            Matcher m_email = p_email.matcher(email);
                            if (!(email.length() <= 345)) {
                                callBack.emailCheck("Email phải dài dưới 345 và không để trống!");
                                return;
                            }
                            else if (!m_email.matches()) {
                                callBack.emailCheck( "Sai định dạng email");
                                return;
                            }
                            else{
                                callBack.emailCheck( "Email hợp lệ!");
                            };
                        }
                        else{
                            callBack.emailCheck( "Email đã được sử dụng!");
                        }
                    }
                    else {
                        System.err.println("Lỗi khi truy vấn Firestore: " + task.getException());
                    }
                });
    }

    public void existUsernameCheck(String userName, OnCheckUserNameCallBack callBack){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("UserName", userName) // Tìm tài liệu có trường email khớp
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(task.getResult().isEmpty()){
                            String regex_account = "^[a-zA-Z0-9]{5,20}$";
                            Pattern p_account = Pattern.compile(regex_account);
                            Matcher m_account = p_account.matcher(userName);
                            if(!m_account.matches()) {
                                callBack.usernameCheck("Tài khoản không được chứa kí tự đặc biệt và phải dài từ 5 đến 20 kí tự!");
                            }
                            else{
                                callBack.usernameCheck("Tài khoản hợp lệ");
                            }
                        }
                        else{
                            callBack.usernameCheck("Tài khoản đã được sử dụng!");
                        }
                    } else {
                        System.err.println("Lỗi khi truy vấn Firestore: " + task.getException());
                    }
                });
    }

    public interface OnCheckEmailCallBack{
        void emailCheck(String status);
    }

    public interface OnCheckUserNameCallBack{
        void usernameCheck(String status);
    }

    public interface OnUpdateAvatarCallback{
        void updateAvatar(String avatar, String noti);
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

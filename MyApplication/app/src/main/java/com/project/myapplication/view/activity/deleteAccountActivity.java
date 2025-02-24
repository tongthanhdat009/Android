package com.project.myapplication.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.model.UserModel;

public class deleteAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);  // Đảm bảo layout được thiết lập đúng

        ImageButton backBTN = findViewById(R.id.backBTN);
        TextView notice = findViewById(R.id.notice);
        Button acceptBTN = findViewById(R.id.accept_delete_button);

        UserModel userModel = new UserModel();

        backBTN.setOnClickListener(v->{
            finish();
        });

        acceptBTN.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid(); // Lưu userId trước khi xóa

                PostModel postModel = new PostModel();
                postModel.deletePostByUserID(userId, new PostModel.OnPostDeleteByUserCallback() {
                    @Override
                    public void onSuccess(String message) {
                        // Tiếp tục xóa user trong Firestore sau khi xóa post thành công
                        UserModel userModel = new UserModel();
                        userModel.deleteUser(userId, new UserModel.OnDeleteUserCallBack() {
                            @Override
                            public void onSuccess(String message) {
                                // Khi đã xóa xong dữ liệu Firestore, mới xóa tài khoản Firebase Authentication
                                user.delete().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("AUTH", "Tài khoản đã bị xóa.");

                                        // Chuyển về màn hình login
                                        Intent intent = new Intent(v.getContext(), loginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        v.getContext().startActivity(intent);
                                    } else {
                                        Log.e("AUTH", "Xóa tài khoản thất bại: " + task.getException().getMessage());
                                        Toast.makeText(v.getContext(), "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(v.getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(v.getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        String userID = getIntent().getStringExtra("userID");


        userModel.getUserInfor(userID, new UserModel.OnGetUserInfor() {
            @Override
            public void getInfor(User user) {
                String noticeText = "<b>THÔNG BÁO XÓA TÀI KHOẢN</b><br><br>" +
                        "<b>Kính gửi:</b> <i>"+ user.getName() +"</i><br><br>" +
                        "Chúng tôi đã nhận được yêu cầu xóa tài khoản của bạn khỏi hệ thống <b>ĐIỆN TÍN TỨC THỜI</b>.<br><br>" +
                        "<b>⚠️ Lưu ý quan trọng:</b><br>" +
                        "- Sau khi tài khoản bị xóa, <b>toàn bộ dữ liệu liên quan</b> sẽ không thể khôi phục.<br>" +
                        "- Bạn sẽ không thể đăng nhập hoặc khôi phục tài khoản sau khi quá trình xóa hoàn tất.<br><br>" ;
                notice.setText(Html.fromHtml(noticeText));
            }
        });



    }
}

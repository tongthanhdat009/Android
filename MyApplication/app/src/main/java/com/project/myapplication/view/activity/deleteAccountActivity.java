package com.project.myapplication.view.activity;

import android.os.Bundle;
import android.text.Html;
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

        acceptBTN.setOnClickListener(v->{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            if(user.isEmailVerified()){
                Toast.makeText(getApplicationContext(),"đã " , Toast.LENGTH_SHORT).show();
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
                        "1️⃣ Sau khi tài khoản bị xóa, <b>toàn bộ dữ liệu liên quan</b> sẽ không thể khôi phục.<br>" +
                        "2️⃣ Nếu bạn đang sử dụng dịch vụ trả phí, hãy đảm bảo rằng mọi giao dịch đã được hoàn tất.<br>" +
                        "3️⃣ Bạn sẽ không thể đăng nhập hoặc khôi phục tài khoản sau khi quá trình xóa hoàn tất.<br><br>" ;
                notice.setText(Html.fromHtml(noticeText));
            }
        });



    }
}

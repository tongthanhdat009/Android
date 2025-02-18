package com.project.myapplication.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.project.myapplication.DTO.User;
import com.project.myapplication.MainActivity;
import com.project.myapplication.R;
import com.project.myapplication.model.UserModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class loginActivity extends AppCompatActivity {
    EditText emailInput,passwordInput;
    Button btnlogin,btnregister;
    UserModel userModel = new UserModel();
    TextView textResetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        btnlogin = findViewById(R.id.btnlogin);
        btnregister = findViewById(R.id.btnregister);
        textResetPassword = findViewById(R.id.resetPasswordText);

        EditText edtPass = findViewById(R.id.edtpass);
        LinearLayout showPassword = findViewById(R.id.show_password);
        ImageView iconEye = findViewById(R.id.iv_show_password);

        textResetPassword.setOnClickListener(v->{
            Intent intent = new Intent(loginActivity.this, resetPasswordActivity.class);
            startActivity(intent);
        });

        showPassword.setOnClickListener(v -> {
            // Kiểm tra xem mật khẩu có đang ẩn hay không
            if ((edtPass.getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0) {
                // Nếu mật khẩu đang ẩn, thay đổi để hiển thị
                edtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                iconEye.setImageResource(R.drawable.ic_open_eye);  // Hình mắt mở
            } else {
                edtPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconEye.setImageResource(R.drawable.ic_closed_eye);  // Hình mắt đóng
            }
            // Di chuyển con trỏ đến cuối văn bản sau khi thay đổi input type
            edtPass.setSelection(edtPass.getText().length());
        });


        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginActivity.this, registerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = emailInput.getText().toString().trim();
                String pass = passwordInput.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    emailInput.setError("Không được để trống Email!");
                    return;
                }

                String regex_email = "^[a-zA-Z0-9_+&*-]+(?:\\\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+.)+[a-zA-Z]{2,7}$";
                Pattern p_email = Pattern.compile(regex_email);
                Matcher m_email = p_email.matcher(name);
                if (!m_email.matches()) {
                    emailInput.setError("Sai định dạng email");
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    passwordInput.setError("Không được để trống mật khẩu!");
                    return;
                }

                userModel.loginCheck(name, pass, view.getContext(), new UserModel.OnUserLoginCallBack() {
                    @Override
                    public void loginCheck(User user, Boolean success, String noti) {
                        if(noti.equals("Đăng nhập thành công!")){
                            Intent intent = new Intent(loginActivity.this, MainActivity.class);
                            intent.putExtra("userID", user.getUserID());
                            loginActivity.this.startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(view.getContext(),noti,Toast.LENGTH_SHORT).show();
                        }
                    }
                    @SuppressLint("HardwareIds")
                    public String getDeviceId(Context context) {
                        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    }
                });
            }
        });
    }
}
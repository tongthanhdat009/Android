package com.project.myapplication.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.UserModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registerActivity extends AppCompatActivity {
    public EditText inputEmail,inputPassword,inputUsername,inputFullName;
    public Button registerBTN, loginBTN;
    public UserModel userModel = new UserModel();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputUsername = findViewById(R.id.inputUsername);
        inputFullName = findViewById(R.id.inputFullName);
        registerBTN = findViewById(R.id.acceptRegisterBTN);
        loginBTN = findViewById(R.id.loginBTN);


        LinearLayout showPassword = findViewById(R.id.show_password);
        ImageView iconEye = findViewById(R.id.iv_show_password);

        showPassword.setOnClickListener(v -> {
            // Kiểm tra xem mật khẩu có đang ẩn hay không
            if ((inputPassword.getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0) {
                // Nếu mật khẩu đang ẩn, thay đổi để hiển thị
                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                iconEye.setImageResource(R.drawable.ic_open_eye);  // Hình mắt mở
            } else {
                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconEye.setImageResource(R.drawable.ic_closed_eye);  // Hình mắt đóng
            }
            // Di chuyển con trỏ đến cuối văn bản sau khi thay đổi input type
            inputPassword.setSelection(inputPassword.getText().length());
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(registerActivity.this, loginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String username = inputUsername.getText().toString().trim();
                String fullname = inputFullName.getText().toString().trim();

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Đăng ký");
                AlertDialog alertDialog = builder.create();

                // kiểm tra thông tin để trống
                if(TextUtils.isEmpty(email)){
                    inputEmail.setError("Không để trống Email!");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    inputPassword.setError("Không để trống mật khẩu");
                    return;
                }

                if(TextUtils.isEmpty(fullname)){
                    inputFullName.setError("Không để trống họ và tên!");
                    return;
                }

                if(TextUtils.isEmpty(username)){
                    inputUsername.setError("Không để trống tên người dùng");
                    return;
                }

                userModel.emailCheck(email, new UserModel.OnCheckEmailCallBack() {
                    @Override
                    public void emailCheck(String status) {
                        switch (status) {
                            case "Email đã được sử dụng!":
                                alertDialog.setMessage("Email đã được sử dụng");
                                alertDialog.show();
                                break;
                            case "Sai định dạng email":
                                alertDialog.setMessage("Sai định dạng email");
                                alertDialog.show();
                                break;
                            case "Email phải dài dưới 345 và không để trống!":
                                alertDialog.setMessage("Email phải dài dưới 345 và không để trống!");
                                alertDialog.show();
                                break;
                            case "Email hợp lệ!":
                                //Kiểm tra họ và tên đầy đủ
                                String regex_userName = "^[\\p{L}\\p{M}']+(?:\\s[\\p{L}\\p{M}']+)*$";
                                Pattern p_userName = Pattern.compile(regex_userName);
                                Matcher m_userName = p_userName.matcher(fullname);
                                if(!(fullname.length()<=50)) {
                                    alertDialog.setMessage("Họ tên phải dài từ 1 đến 50 kí tự");
                                    alertDialog.show();
                                    return;
                                }
                                if(!m_userName.matches()) {
                                    alertDialog.setMessage("Họ và tên không được chứa kí tự đặc biệt và số");
                                    alertDialog.show();
                                    return;
                                }

                                //kiểm tra mật khẩu
                                String regex_pass = "^(?=.*[0-9])(?=.*[a-zA-Z]).{6,20}$";
                                Pattern p_pass = Pattern.compile(regex_pass);
                                Matcher m_pass = p_pass.matcher(password);

                                if(!m_pass.matches()) {
                                    alertDialog.setMessage("Mật khẩu phải từ 6 kí tự và bao gồm chữ và số !");
                                    alertDialog.show();
                                    return;
                                }

                                //Kiểm tra username đã tồn tại chưa
                                userModel.existUsernameCheck(username, new UserModel.OnCheckUserNameCallBack() {
                                    @Override
                                    public void usernameCheck(String status) {
                                        switch (status){
                                            case "Tài khoản không được chứa kí tự đặc biệt và phải dài từ 5 đến 20 kí tự!":
                                                alertDialog.setMessage(status);
                                                alertDialog.show();
                                                break;
                                            case "Tài khoản đã được sử dụng!":
                                                alertDialog.setMessage(status);
                                                alertDialog.show();
                                                break;
                                            case "Tài khoản hợp lệ":
                                                signUp(email, password, new OnSignUpCallback() {
                                                    @Override
                                                    public void onSuccess(String userID) {
                                                        User tempUser = new User(userID, fullname, username, password, email, "", "","","");

                                                        userModel.addUser(tempUser, new UserModel.OnUserRegisterCallback() {
                                                            @Override
                                                            public void register(User registedUser,Boolean success) {
                                                                if(success){
                                                                    Toast.makeText(registerActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(registerActivity.this, registerAvatarActivity.class);
                                                                    intent.putExtra("userID", registedUser.getUserID());
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else {
                                                                    Toast.makeText(registerActivity.this, "Đăng ký thất bại.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onFailure(String errorMessage) {
                                                        alertDialog.setMessage(errorMessage);
                                                        alertDialog.show();
                                                    }
                                                });

                                                break;
                                        }
                                    }
                                });
                                break;
                        }
                    }
                });
            }
        });
    }
    public void signUp(String email, String password, final OnSignUpCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userID = user.getUid();  // Lấy userID (UID của người dùng)
                            Log.d("FirebaseAuth", "Đăng ký thành công! User ID: " + userID);

                            // Gọi callback với userID trả về
                            callback.onSuccess(userID);
                        }
                    } else {
                        Log.e("FirebaseAuth", "Lỗi: " + task.getException().getMessage());
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Callback interface
    public interface OnSignUpCallback {
        void onSuccess(String userID);
        void onFailure(String errorMessage);
    }
}

package com.project.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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

import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.UserModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class register_page extends AppCompatActivity {
    public EditText edtconnect,edtpass,edtusername,edtfullname;
    public Button btnregister,btnlogin;
    public UserModel userModel = new UserModel();
    private boolean usernameExist, emailExist;
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

        edtconnect = findViewById(R.id.edtconnect);
        edtpass = findViewById(R.id.edtpass);
        edtusername = findViewById(R.id.edtusername);
        edtfullname = findViewById(R.id.edtfullname);
        btnregister = findViewById(R.id.btnregister);
        btnlogin = findViewById(R.id.btnlogin);

        LinearLayout showPassword = findViewById(R.id.show_password);
        ImageView iconEye = findViewById(R.id.iv_show_password);

        showPassword.setOnClickListener(v -> {
            // Kiểm tra xem mật khẩu có đang ẩn hay không
            if ((edtpass.getInputType() & InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0) {
                // Nếu mật khẩu đang ẩn, thay đổi để hiển thị
                edtpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                iconEye.setImageResource(R.drawable.ic_open_eye);  // Hình mắt mở
            } else {
                edtpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconEye.setImageResource(R.drawable.ic_closed_eye);  // Hình mắt đóng
            }
            // Di chuyển con trỏ đến cuối văn bản sau khi thay đổi input type
            edtpass.setSelection(edtpass.getText().length());
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(register_page.this, Login_page.class);
                startActivity(intent);
                finish();
            }
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameExist = false;
                emailExist = false;
                String connect = edtconnect.getText().toString().trim();
                String pass = edtpass.getText().toString().trim();
                String username = edtusername.getText().toString().trim();
                String fullname = edtfullname.getText().toString().trim();
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Đăng ký");
                AlertDialog alertDialog = builder.create();

                // kiểm tra thông tin để trống
                if(TextUtils.isEmpty(connect)){
                    edtconnect.setError("Không để trống Email!");
                    return;
                }

                if(TextUtils.isEmpty(pass)){
                    edtpass.setError("Không để trống mật khẩu");
                    return;
                }

                if(TextUtils.isEmpty(fullname)){
                    edtfullname.setError("Không để trống họ và tên!");
                    return;
                }

                if(TextUtils.isEmpty(username)){
                    edtusername.setError("Không để trống tên người dùng");
                    alertDialog.show();
                    return;
                }

                userModel.emailCheck(connect, new UserModel.OnCheckEmailCallBack() {
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
                                //kiểm tra mật khẩu
                                String regex_pass = "^(?=.*[0-9])(?=.*[a-zA-Z]).{6,20}$";
                                Pattern p_pass = Pattern.compile(regex_pass);
                                Matcher m_pass = p_pass.matcher(pass);

                                if(!m_pass.matches()) {
                                    alertDialog.setMessage("Mật khẩu phải từ 6 kí tự và bao gồm chữ và số !");
                                    alertDialog.show();
                                    return;
                                }


                                String regex_userName = "^[\\p{L}\\p{M}']+(?:[\\s][\\p{L}\\p{M}']+)*$";
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
                                                User tempUser = new User("", fullname, username, pass, connect, "", "","");

                                                userModel.addUser(tempUser, new UserModel.OnUserRegisterCallback() {
                                                    @Override
                                                    public void register(User registedUser,Boolean success) {
                                                        if(success){
                                                            Toast.makeText(register_page.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(register_page.this, register_avatar.class);
                                                            intent.putExtra("userID", registedUser.getUserID());
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        else {
                                                            Toast.makeText(register_page.this, "Đăng ký thất bại.", Toast.LENGTH_SHORT).show();
                                                        }
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



//                fire.createUserWithEmailAndPassword(connect, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = fire.getCurrentUser();
//                            if (user != null) {
//                                String userId = user.getUid();
//
//                                Map<String, Object> usermap = new HashMap<>();
//                                usermap.put("Biography", "");
//                                usermap.put("Email", connect);
//                                usermap.put("Logged",false);
//                                usermap.put("Name", fullname);
//                                usermap.put("UserName", username);
//                                usermap.put("Password", pass);
//                                usermap.put("avatar", "");
//
//                                db.collection("users").document(userId).set(usermap)
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Toast.makeText(register_page.this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        })
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void unused) {
//                                                Toast.makeText(register_page.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
//                                                Intent intent = new Intent(register_page.this, register_avatar.class);
//                                                startActivity(intent);
//                                                finish();
//                                    }
//                                });
//                            } else {
//                                Log.e("RegisterError", "User null after successful registration");
//                                Toast.makeText(register_page.this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(register_page.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
            }
        });
    }
}

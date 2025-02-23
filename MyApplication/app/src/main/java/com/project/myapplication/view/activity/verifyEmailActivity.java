package com.project.myapplication.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.project.myapplication.R;

import java.util.Objects;

public class verifyEmailActivity extends AppCompatActivity {

    ImageButton backBTN;

    Button getCodeBTN;
    String userID;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        getCodeBTN = findViewById(R.id.getCodeBTN);

        userID = getIntent().getStringExtra("userID");

        backBTN =findViewById(R.id.backBTN);
        backBTN.setOnClickListener(v ->{
            finish();
        });
        // Tạo đối tượng AlertDialog để thông báo lỗi
        alertDialog = new AlertDialog.Builder(this);

        getCodeBTN.setOnClickListener(v -> {
            startEmailVerification(getCodeBTN);
        });
    }
    @SuppressLint("SetTextI18n")
    private void startEmailVerification(Button btn) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final long delayMillis = 1000;
        final int totalTimeSeconds = 60;

        handler.post(() -> {
            btn.setEnabled(false);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.sendEmailVerification()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("INFO", "Email xác thực đã được gửi!");
                                Toast.makeText(btn.getContext(), "Vui lòng kiểm tra email để xác thực!", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("ERROR", "Gửi email thất bại: " + Objects.requireNonNull(task.getException()).getMessage());
                            }
                        });
            }
        });

        new Thread(() -> {
            for (int i = totalTimeSeconds; i >= 0; i--) {
                final int secondsLeft = i;
                handler.post(() -> btn.setText("Gửi lại sau: " + secondsLeft + "s"));

                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            handler.post(() -> {
                btn.setEnabled(true);
                btn.setText("Gửi lại mã");
            });
        }).start();
    }

}

package com.project.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.myapplication.view.navController;
import com.project.myapplication.view.Login_page;

public class MainActivity extends AppCompatActivity {

    private String userID = null;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        userID = checkLoginStatus();
        if (userID != null) {
            Intent intent = new Intent(MainActivity.this, navController.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        } else {
            Intent loginIntent = new Intent(MainActivity.this, Login_page.class);
            startActivity(loginIntent);
        }
        finish();
    }

    // Phương thức kiểm tra trạng thái đăng nhập qua Firebase Authentication
    private String checkLoginStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }
}

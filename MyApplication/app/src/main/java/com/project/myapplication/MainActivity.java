package com.project.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.project.myapplication.view.navController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String userID = "2";
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(MainActivity.this, navController.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
        finish();
    }
}
package com.project.myapplication.view.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.R;
import com.project.myapplication.view.adapter.accountCenterAdapter;

public class accountCenterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_center);

        String userID = getIntent().getStringExtra("userID");

        ImageButton backBTN = findViewById(R.id.backBTN);
        backBTN.setOnClickListener(v->{
            finish();
        });

        RecyclerView recyclerView = findViewById(R.id.setting_option);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Gán Adapter
        accountCenterAdapter adapter = new accountCenterAdapter(this, userID);
        recyclerView.setAdapter(adapter);
    }
}

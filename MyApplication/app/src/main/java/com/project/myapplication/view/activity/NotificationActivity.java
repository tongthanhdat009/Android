package com.project.myapplication.view.activity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.project.myapplication.DTO.Notification;
import com.project.myapplication.R;
import com.project.myapplication.model.NotificationModel;
import com.project.myapplication.view.adapter.NotificationAdapter;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private NotificationModel model;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        model = new NotificationModel();

        userID = getIntent().getStringExtra("UserID");

        ImageButton backBTN = findViewById(R.id.backBTN);
        backBTN.setOnClickListener(v->{
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        model.getNotificationByUserID(userID, new NotificationModel.getNotificatioByUserIDCallback() {
            @Override
            public void getNotiByUserID(ArrayList<Notification> notifications) {
                RecyclerView notiRecycler = findViewById(R.id.noti_recycler);
                NotificationAdapter adapter = new NotificationAdapter(getApplicationContext(),notifications);
                notiRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                notiRecycler.setAdapter(adapter);
            }
        });
    }
}

package com.project.myapplication.view;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Following;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.controller.postController;
import com.project.myapplication.model.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class authorProfileActivity extends AppCompatActivity {
    private String userID;
    private String postID;
    private PostModel postModel = new PostModel();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_profile_layout);

        // Nhận userID và postID từ Intent
        userID = getIntent().getStringExtra("userID");
        postID = getIntent().getStringExtra("postID");

        postModel.getUserInfor(userID, new PostModel.OnUserRetrievedCallback(){
            TextView authorName = findViewById(R.id.author_profile_title);
            TextView totalPosts = findViewById(R.id.total_posts);
            TextView biography = findViewById(R.id.biography);
            TextView displayPostImageChoice = findViewById(R.id.display_post_image_choice);
            TextView displayPostVideoChoice = findViewById(R.id.display_post_video_choice);
            Button followButton = findViewById(R.id.follow_button);
            Button chatButton = findViewById(R.id.chat);
            ImageView avatar = findViewById(R.id.avatar);

            @Override
            public void onUserRetrievedCallback(User user) {
                authorName.setText(user.getName());
                Picasso.get().load(user.getAvatar()).into(avatar);
                biography.setText(user.getBiography());
            }

        });

        postModel.getAllFollowing(userID, new PostModel.OnFollowingListRetrievedCallback() {
            TextView totalFollowing = findViewById(R.id.total_following);
            @Override
            public void getAllFollowing(ArrayList<Following> followingList) {
                totalFollowing.setText(String.valueOf(followingList.size()));
            }
        });

        postModel.getAllFollower(userID, new PostModel.OnFollowerListRetrievedCallback(){
            TextView totalFollowers = findViewById(R.id.total_followers);
            @Override
            public void getAllFollower(ArrayList<Followers> followerList) {
                totalFollowers.setText(String.valueOf(followerList.size()));
            }
        });

    }
}

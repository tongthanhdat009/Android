package com.project.myapplication.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Following;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class authorProfileActivity extends AppCompatActivity {
    private String userID;
    private String postID;
    private String authorID;
    private PostModel postModel = new PostModel();
    final Button followButton = findViewById(R.id.follow_button);
    final Button unfollowButton = findViewById(R.id.unfollow_button);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_profile_layout);

        // Nhận userID và postID từ Intent
        userID = getIntent().getStringExtra("userID");
        postID = getIntent().getStringExtra("postID");
        authorID = getIntent().getStringExtra("authorID");

        Toast.makeText(authorProfileActivity.this,userID,Toast.LENGTH_SHORT).show();

        ImageButton backBTN = findViewById(R.id.backBTN);
        backBTN.setOnClickListener(v -> finish());

        postModel.getUserPost(userID, new PostModel.OnUserPostListRetrievedCallback(){
            final TextView totalPosts = findViewById(R.id.total_posts);
            @Override
            public void getUserPost(ArrayList<Post> postsList) {
                totalPosts.setText(String.valueOf(postsList.size()));
            }
        });

        postModel.getUserInfor(authorID, new PostModel.OnUserRetrievedCallback(){
            final TextView authorName = findViewById(R.id.author_profile_title);
            final TextView biography = findViewById(R.id.biography);
            final ImageView avatar = findViewById(R.id.avatar);

            @Override
            public void onUserRetrievedCallback(User user) {
                authorName.setText(user.getName());
                Picasso.get().load(user.getAvatar()).into(avatar);
                biography.setText(user.getBiography());
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(authorProfileActivity.this, FullscreenImageActivity.class);
                        intent.putExtra("imageUri", Uri.parse(user.getAvatar()));
                        startActivity(intent);
                    }
                });
            }

        });

        postModel.getAllFollower(authorID, new PostModel.OnFollowerListRetrievedCallback() {
            final TextView totalFollowers = findViewById(R.id.total_followers);
            @Override
            public void getAllFollower(ArrayList<Followers> followerList) {
                totalFollowers.setText(String.valueOf(followerList.size()));
            }
        });

        postModel.getAllFollowing(authorID, new PostModel.OnFollowingListRetrievedCallback() {
            final TextView totalFollowing = findViewById(R.id.total_following);
            @Override
            public void getAllFollowing(ArrayList<Following> followingList) {
                totalFollowing.setText(String.valueOf(followingList.size()));
            }
        });



//        unfollowButton.setOnClickListener(v -> {
//            postModel.getAllFollower(authorID, followerList -> {
//                for (Followers follower : followerList){
//                    if(follower.getUserID().equals(userID)){
//                        postModel.removeFollowerUser(authorID, follower.getIdFollower());
//                        break;
//                    }
//                }
//            });
//            postModel.getAllFollowing(userID, followingList -> {
//                for (Following following : followingList){
//                    if(following.getUserID().equals(authorID)){
//                        postModel.removeFollowingUser(userID, following.getIdFollowing());
//                        break;
//                    }
//                }
//            });
//            recreate();
//        });
//
//        followButton.setOnClickListener(v -> {
//            postModel.addFollowerUser(authorID,new Followers("",userID, Timestamp.now()));
//            postModel.addFollowingUser(userID,new Following("",authorID, Timestamp.now()));
//            recreate();
//        });

        unfollowButton.setOnClickListener(v -> {
            postModel.getAllFollower(authorID, followerList -> {
                for (Followers follower : followerList){
                    if(follower.getUserID().equals(userID)){
                        postModel.removeFollowerUser(authorID, follower.getIdFollower(), () -> {
                            updateFollowButton(false);
                        });
                        break;
                    }
                }
            });
            postModel.getAllFollowing(userID, followingList -> {
                for (Following following : followingList){
                    if(following.getUserID().equals(authorID)){
                        postModel.removeFollowingUser(userID, following.getIdFollowing(), () -> {
                            updateFollowButton(false);
                        });
                        break;
                    }
                }
            });
        });


        followButton.setOnClickListener(v -> {
            postModel.addFollowerUser(authorID, new Followers("", userID, Timestamp.now()), () -> {
                updateFollowButton(true);
            });
            postModel.addFollowingUser(userID, new Following("", authorID, Timestamp.now()), () -> {
                updateFollowButton(true);
            });
        });

        postModel.getAllFollowing(userID, new PostModel.OnFollowingListRetrievedCallback(){
            @Override
            public void getAllFollowing(ArrayList<Following> followingList) {
                ArrayList<String> followingIDList = new ArrayList<>();
                for (Following following : followingList) {
                    followingIDList.add(following.getUserID());
                    Toast.makeText(authorProfileActivity.this, "following ID:" + following.getUserID(), Toast.LENGTH_SHORT).show();
                }
                if (!followingIDList.isEmpty() && followingIDList.contains(authorID)){
                    unfollowButton.setVisibility(View.VISIBLE);
                    followButton.setVisibility(View.GONE);
                }
                else{
                    unfollowButton.setVisibility(View.GONE);
                    followButton.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    private void updateFollowButton(boolean isFollowing) {
        if (isFollowing) {
            followButton.setVisibility(View.GONE);
            unfollowButton.setVisibility(View.VISIBLE);
        } else {
            followButton.setVisibility(View.VISIBLE);
            unfollowButton.setVisibility(View.GONE);
        }
    }
}

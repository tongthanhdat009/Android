package com.project.myapplication.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private final PostModel postModel = new PostModel();
    private Button followButton = null;
    private Button unfollowButton = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.author_profile_layout);

        // Nhận userID và postID từ Intent
        userID = getIntent().getStringExtra("userID");
        authorID = getIntent().getStringExtra("authorID");

        LinearLayout totalFollowerContainer = findViewById(R.id.total_followers_container);
        LinearLayout totalFollowingContainer = findViewById(R.id.total_following_container);
        followButton = findViewById(R.id.follow_button);
        unfollowButton = findViewById(R.id.unfollow_button);

        ImageButton backBTN = findViewById(R.id.backBTN);
        backBTN.setOnClickListener(v -> finish());

        totalFollowerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(authorProfileActivity.this, ShowFollowActivity.class);
                intent.putExtra("AuthorID", authorID);
                intent.putExtra("CurrentUser", userID);
                startActivity(intent);
                finish();
            }
        });

        totalFollowingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(authorProfileActivity.this, ShowFollowActivity.class);
                intent.putExtra("AuthorID", authorID);
                intent.putExtra("CurrentUser", userID);
                startActivity(intent);
                finish();
            }
        });

        postModel.getUserPost(authorID, new PostModel.OnUserPostListRetrievedCallback(){
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
                biography.setEllipsize(TextUtils.TruncateAt.END);

                biography.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(biography.getEllipsize() != null) {
                            biography.setEllipsize(null);
                            biography.setMaxLines(Integer.MAX_VALUE);
                            biography.setText(user.getBiography());
                        } else {
                            biography.setEllipsize(TextUtils.TruncateAt.END);
                            biography.setMaxLines(2);
                        }
                        biography.requestLayout();
                    }
                });

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

        unfollowButton.setOnClickListener(v -> {
            postModel.getAllFollower(authorID, followerList -> {
                for (Followers follower : followerList){
                    if(follower.getUserID().equals(userID)){
                        postModel.removeFollowerUser(authorID, follower.getIdFollower(), new PostModel.OnRemoveFollowerCallback() {
                                    @Override
                                    public void onRemoveFollower(boolean success) {
                                        updateFollowButton(false);
                                        updateTotalFollow();
                                    }
                                });
                        break;
                    }
                }
            });
            postModel.getAllFollowing(userID, followingList -> {
                for (Following following : followingList){
                    if(following.getUserID().equals(authorID)){
                        postModel.removeFollowingUser(userID, following.getIdFollowing(), new PostModel.OnRemoveFollowingCallback(){
                            @Override
                            public void onRemoveFollowing(boolean success) {
                                updateFollowButton(false);
                                updateTotalFollow();
                            }
                        });
                        break;
                    }
                }
            });
        });

        followButton.setOnClickListener(v -> {
            postModel.addFollowerUser(authorID, new Followers("", userID, Timestamp.now()), new PostModel.OnAddFollowerCallback() {
                @Override
                public void onAddFollower(boolean success) {
                    updateFollowButton(true);
                    updateTotalFollow();
                }
            });
            postModel.addFollowingUser(userID, new Following("", authorID, Timestamp.now()), new PostModel.OnAddFollowingCallback() {
                @Override
                public void onAddFollowing(boolean success) {
                    updateFollowButton(true);
                    updateTotalFollow();
                }
            });
            updateTotalFollow();
        });

        postModel.getAllFollowing(userID, new PostModel.OnFollowingListRetrievedCallback(){
            @Override
            public void getAllFollowing(ArrayList<Following> followingList) {
                ArrayList<String> followingIDList = new ArrayList<>();
                for (Following following : followingList) {
                    followingIDList.add(following.getUserID());
//                    Toast.makeText(authorProfileActivity.this, "following ID:" + following.getUserID(), Toast.LENGTH_SHORT).show();
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

        RecyclerView recyclerView = findViewById(R.id.post_show_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        postModel.getUserPost(authorID, new PostModel.OnUserPostListRetrievedCallback() {
            @Override
            public void getUserPost(ArrayList<Post> postsList) {
                postShowAdapter postShowAdapter = new postShowAdapter(authorProfileActivity.this, postsList, postModel);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(postShowAdapter);
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

    private void updateTotalFollow(){
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
    }

}

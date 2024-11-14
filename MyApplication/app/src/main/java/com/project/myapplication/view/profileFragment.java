package com.project.myapplication.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Following;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class profileFragment extends Fragment {
    public String userID;
    private final PostModel postModel = new PostModel();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.userID = getArguments().getString("userID");
            assert userID != null;
            Log.d("Profile",userID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ImageView avatar = view.findViewById(R.id.avatar);
        TextView profileTitle = view.findViewById(R.id.profile_title);
        TextView biography = view.findViewById(R.id.biography);
        LinearLayout totalFollowerContainer = view.findViewById(R.id.total_followers_container);
        LinearLayout totalFollowingContainer = view.findViewById(R.id.total_following_container);
        postModel.getUserInfor(userID, new PostModel.OnUserRetrievedCallback() {
            @Override
            public void onUserRetrievedCallback(User user) {
                Picasso.get().load(user.getAvatar()).into(avatar);
                profileTitle.setText(user.getName());
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
                        Intent intent = new Intent(view.getContext(), FullscreenImageActivity.class);
                        intent.putExtra("imageUri", Uri.parse(user.getAvatar()));
                        startActivity(intent);
                    }
                });
            }
        });

        postModel.getUserPost(userID, new PostModel.OnUserPostListRetrievedCallback(){
            final TextView totalPosts = view.findViewById(R.id.total_posts);
            @Override
            public void getUserPost(ArrayList<Post> postsList) {
                totalPosts.setText(String.valueOf(postsList.size()));
            }
        });

        postModel.getAllFollower(userID, new PostModel.OnFollowerListRetrievedCallback() {
            final TextView totalFollowers = view.findViewById(R.id.total_followers);
            @Override
            public void getAllFollower(ArrayList<Followers> followerList) {
                totalFollowers.setText(String.valueOf(followerList.size()));
            }
        });

        postModel.getAllFollowing(userID, new PostModel.OnFollowingListRetrievedCallback() {
            final TextView totalFollowing = view.findViewById(R.id.total_following);
            @Override
            public void getAllFollowing(ArrayList<Following> followingList) {
                totalFollowing.setText(String.valueOf(followingList.size()));
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.post_show_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(),3);
        postModel.getUserPost(userID, new PostModel.OnUserPostListRetrievedCallback() {
            @Override
            public void getUserPost(ArrayList<Post> postsList) {
                userPostShowAdapter postShowAdapter = new userPostShowAdapter(view.getContext(), postsList, postModel, userID);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(postShowAdapter);
            }
        });

        totalFollowerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), userShowFollowActivity.class);
                intent.putExtra("CurrentUser", userID);
                startActivity(intent);
            }
        });

        totalFollowingContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), userShowFollowActivity.class);
                intent.putExtra("CurrentUser", userID);
                startActivity(intent);
            }
        });
        return view;
    }
}
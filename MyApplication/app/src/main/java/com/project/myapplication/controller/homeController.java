package com.project.myapplication.controller;

import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.view.PostAdapter;
import com.project.myapplication.model.PostModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class homeController {
    private final View view;
    private final PostModel postModel;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public homeController(View view) {
        this.view = view;
        this.postModel = new PostModel();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        postRecyclerView = view.findViewById(R.id.post_recycler_view);
        // Thiết lập LayoutManager cho RecyclerView
        postRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    public void postDisplay(String userID) {
        // Lấy dữ liệu ban đầu từ model và hiển thị
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        // Lắng nghe sự kiện kéo xuống (pull-to-refresh)
        swipeRefreshLayout.setOnRefreshListener(() -> {
            postModel.getAllPost(postsList -> {
                postAdapter = new PostAdapter(view.getContext(), postsList, userID, postModel);
                postRecyclerView.setAdapter(postAdapter);
                swipeRefreshLayout.setRefreshing(false);
            });
        });
        postModel.getAllPost(postsList -> {
            postAdapter = new PostAdapter(view.getContext(), postsList, userID, postModel);
            postRecyclerView.setAdapter(postAdapter);
        });
    }

}
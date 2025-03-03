package com.project.myapplication.controller;

import android.support.annotation.NonNull;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.project.myapplication.R;
import com.project.myapplication.view.adapter.PostAdapter;
import com.project.myapplication.model.PostModel;

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
        //Dừng video khi lướt qua 30% chiều cao của video
        postRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                    for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
                        PostAdapter.PostViewHolder holder = (PostAdapter.PostViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (holder != null) {
                            postAdapter.stopVideoIfNotVisible(holder);
                        }
                    }
                }
            }
        });
    }

}
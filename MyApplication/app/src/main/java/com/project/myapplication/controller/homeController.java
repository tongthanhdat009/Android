package com.project.myapplication.controller;

import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.view.PostAdapter;
import com.project.myapplication.model.PostModel;

import java.util.ArrayList;

public class homeController {
    private final View view;
    private final PostModel postModel;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;

    public homeController(View view) {
        this.view = view;
        this.postModel = new PostModel();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        postRecyclerView = view.findViewById(R.id.post_recycler_view);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    public void postDiplay(String userID) {
        postModel.getAllPost(postsList -> {
            postAdapter = new PostAdapter(view.getContext(), postsList, userID, postModel);
            postRecyclerView.setAdapter(postAdapter);
        });
    }
}

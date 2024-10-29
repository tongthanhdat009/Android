package com.project.myapplication.controller;

import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Comment;
import com.project.myapplication.R;
import com.project.myapplication.model.CommentModel;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.commentAdapter;

import java.util.ArrayList;

public class commentController {
    private final View view;
    private RecyclerView commentRecyclerView;
    private commentAdapter commentAdapter;

    public commentController(View view) {
        this.view = view;
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        commentRecyclerView = view.findViewById(R.id.comment_recycler_view);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    public void commentDiplay(String postID) {
        CommentModel commentModel = new CommentModel();
        commentModel.getAllCommentInPost(postID, commentsList -> {
            commentAdapter = new commentAdapter(view.getContext(), commentsList, postID, commentModel);
            commentRecyclerView.setAdapter(commentAdapter);
        });
    }

}

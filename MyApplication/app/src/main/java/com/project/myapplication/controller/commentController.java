package com.project.myapplication.controller;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.project.myapplication.DTO.Comment;
import com.project.myapplication.R;
import com.project.myapplication.model.CommentModel;
import com.project.myapplication.view.adapter.commentAdapter;

import java.util.ArrayList;

public class commentController {
    public final View view;
    private RecyclerView commentRecyclerView;
    private commentAdapter commentAdapter;
    private CommentModel commentModel;
    public commentController(View view) {
        this.view = view;
        this.commentModel = new CommentModel();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        commentRecyclerView = view.findViewById(R.id.comment_recycler_view);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    public void commentDiplay(String postID,String userID) {
        CommentModel commentModel = new CommentModel();
        commentModel.getAllCommentInPost(postID, commentsList -> {
            commentAdapter = new commentAdapter(view.getContext(), commentsList, postID, commentModel, userID);
            commentRecyclerView.setAdapter(commentAdapter);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void postComment(String commentText, String postID, String userID){
        Comment tempComment = new Comment(
                "",
                userID,
                commentText,
                new ArrayList<>(),
                0,
                Timestamp.now());
        commentModel.addComment(tempComment, postID);
        if (commentAdapter != null) {
            // Add the new comment directly to the adapter's list
            commentAdapter.getCommentsList().add(tempComment);  // Ensure `getCommentsList` gives direct access to the list in adapter
            commentAdapter.notifyItemInserted(commentAdapter.getCommentsList().size() - 1);
            commentRecyclerView.scrollToPosition(commentAdapter.getCommentsList().size() - 1);  // Scroll to the new comment
        }
    }
}

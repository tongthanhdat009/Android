package com.project.myapplication.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Comment;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.model.CommentModel;
import com.project.myapplication.model.PostModel;

import java.util.ArrayList;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.commentViewHolder>{
    private final Context context;
    private final ArrayList<Comment> cmts;
    private final String postID;
    private final CommentModel commentModel;
    public commentAdapter(Context context, ArrayList<Comment> commentsList, String postID, CommentModel commentModel){
        this.context = context;
        this.cmts = commentsList;
        this.postID = postID;
        this.commentModel = commentModel;
    }
    @NonNull
    @Override
    public commentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_layout, parent, false);
        return new commentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull commentViewHolder holder, int position) {
        Comment cmt = cmts.get(position);
        holder.avatar.setImageResource(R.drawable.liked); // Thay đổi hình ảnh nếu cần
        holder.content.setText(cmt.getCommentText()); // Giả sử có phương thức getCommentText()
        holder.likes_count.setText(String.valueOf(cmt.getLikesCount())); // Giả sử có phương thức getLikesCount()
    }

    @Override
    public int getItemCount() {
        return cmts.size();
    }

    public static class commentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, like_comment;
        TextView username, time, content, likes_count;
        public commentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            like_comment = itemView.findViewById(R.id.like_comment);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.content);
            likes_count = itemView.findViewById(R.id.likes_count);
        }
    }
}

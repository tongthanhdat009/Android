package com.project.myapplication.view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Comment;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.CommentModel;
import com.project.myapplication.model.PostModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.commentViewHolder>{
    private final Context context;
    private final ArrayList<Comment> cmts;
    private final CommentModel commentModel;
    private final String postID;
    private final String userID;
    public commentAdapter(Context context, ArrayList<Comment> commentsList, String postID, CommentModel commentModel, String userID){
        this.context = context;
        this.cmts = commentsList;
        this.commentModel = commentModel;
        this.postID = postID;
        this.userID = userID;
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
        Toast.makeText(context,"Comments ID" + cmt.getCommentID(), Toast.LENGTH_SHORT).show();

        holder.content.setText(cmt.getCommentText()); // Giả sử có phương thức getCommentText()

        holder.likes_count.setText(String.valueOf(cmt.getLikesCount())); // Giả sử có phương thức getLikesCount()

        //hiển thị cho các bài viết đã được like bởi người dùng hiện tại
        if (cmt.getLikedBy().contains(userID)) {
            holder.like_comment.setImageResource(R.drawable.liked);
            holder.like_comment.clearColorFilter();
        }

        //xử lý khi nhấn thích bài viết
        holder.like_comment.setOnClickListener(v -> {
            boolean isLiked = cmt.getLikedBy().contains(userID);
            if (isLiked) {
                cmt.getLikedBy().remove(userID);
                holder.like_comment.setColorFilter(context.getResources().getColor(R.color.like_button_color));
                holder.like_comment.setImageResource(R.drawable.like);
                commentModel.commentUpdate(cmt,postID);
                Toast.makeText(context, "Đã bỏ like "+cmt.getCommentID(), Toast.LENGTH_SHORT).show();
            }
            else {
                cmt.getLikedBy().add(userID);
                holder.like_comment.setImageResource(R.drawable.liked);
                holder.like_comment.clearColorFilter();
                commentModel.commentUpdate(cmt,postID);
                Toast.makeText(context, "Đã like", Toast.LENGTH_SHORT).show();
            }
            holder.likes_count.setText(String.valueOf(cmt.getLikedBy().size()));
        });
        commentModel.getUserCommentInfor(cmt.getUserID(), new CommentModel.onUserCommentRetrievedCallBack() {
            @Override
            public void getUserCommentInfor(User user) {
                Picasso.get().load(user.getAvatar()).into(holder.avatar);
                holder.username.setText(user.getName());
            }
        });
        //hiển thị thòi gian đăng comment
        long timestamp = cmt.getTime().getSeconds() * 1000;
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(date);
        holder.time.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return cmts.size();
    }

    public ArrayList<Comment> getCommentsList(){
        return cmts;
    }

    public static class commentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, like_comment;
        TextView username, time, content, likes_count;;

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

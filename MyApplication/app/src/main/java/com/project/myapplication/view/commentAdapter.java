package com.project.myapplication.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
    public void onBindViewHolder(@NonNull commentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Comment cmt = cmts.get(position);
//        Toast.makeText(context,"Comments ID" + cmt.getCommentID(), Toast.LENGTH_SHORT).show();

        holder.content.setText(cmt.getCommentText()); // Giả sử có phương thức getCommentText()

        holder.likes_count.setText(String.valueOf(cmt.getLikesCount())); // Giả sử có phương thức getLikesCount()

        holder.content.setOnLongClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.comment_menu, popupMenu.getMenu());

            // Thêm mục mới vào menu động
            popupMenu.getMenu().add(0, 0, 0, "Xóa comment");
            popupMenu.getMenu().add(0, 1, 0, "Sửa comment");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 0) { // Kiểm tra ID của mục động
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setTitle("Xác nhận xóa")
                            .setMessage("Bạn có chắc chắn muốn xóa bình luận này?")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    commentModel.deleteComment(cmt, postID, new CommentModel.OnCommentDeletedCallback() {
                                        @Override
                                        public boolean onCommentDeleted(boolean success) {
                                            if(success){
                                                if(position != RecyclerView.NO_POSITION){
                                                    cmts.remove(position);
                                                    notifyItemRemoved(position);
                                                    Toast.makeText(holder.itemView.getContext(), "Xóa bình luận thành công!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else {
                                                Toast.makeText(holder.itemView.getContext(), "Lỗi khi xóa bình luận!", Toast.LENGTH_SHORT).show();
                                            }
                                            return false;
                                        }

                                    });
                                }
                            })
                            .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert) // Icon cho hộp thoại
                            .show();
                    return true;
                }
                if (item.getItemId() == 1) { // Kiểm tra ID của mục động
                    Toast.makeText(context, "Sửa comment", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Sửa comment");

                    // Tạo EditText để nhập thông tin
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newCaption = input.getText().toString();
                            // Xử lý thông tin nhập vào
                            if(newCaption.isEmpty()){
                                Toast.makeText(context, "Vui lòng nhập caption để sửa!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Caption đã nhập: " + newCaption, Toast.LENGTH_SHORT).show();
                                cmt.setCommentText(newCaption);
                                commentModel.commentUpdate(cmt, postID);
                                Toast.makeText(context, " Sửa caption thành công! " + newCaption, Toast.LENGTH_SHORT).show();
                                holder.content.setText(cmt.getCommentText());
                            }
                        }
                    });
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                    return true;
                }
                return false;
            });
            if(userID.equals(cmt.getUserID())){
                popupMenu.show();
            }
            return false;
        });

        //hiển thị cho các bài viết đã được like bởi người dùng hiện tại
        if (cmt.getLikedBy().contains(userID)) {
            holder.like_comment.setImageResource(R.drawable.liked);
            holder.like_comment.clearColorFilter();
        }
        else {
            holder.like_comment.setImageResource(R.drawable.like);
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

package com.project.myapplication.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.postImageAdapter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final Context context;
    private final ArrayList<Post> posts;
    private final String userID;
    private final PostModel postModel;
    public PostAdapter(Context context, ArrayList<Post> posts, String userID, PostModel postModel) {
        this.context = context;
        this.posts = posts;
        this.userID = userID;
        this.postModel = postModel;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_home_layout, parent, false);
        return new PostViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = posts.get(position);

        // Cài đặt nội dung cho từng bài đăng
        holder.caption.setText(post.getContent());
        ArrayList<Uri> mediaList = new ArrayList<>();
        for (String media : post.getMedia()) {
            mediaList.add(Uri.parse(media));
        }
        //thêm ảnh vào post
        holder.imageViewPager.setAdapter(new postImageAdapter(context, mediaList));

        //thêm phần đếm ảnh
        if(mediaList.size() > 1){
            holder.picture_counter.setVisibility(View.VISIBLE);
        }
        else{
            holder.picture_counter.setVisibility(View.GONE);
        }
        holder.imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(mediaList.size() > 1){
                    holder.picture_counter.setText(String.format("%d/%d", position + 1, mediaList.size()));
                }
            }
        });

        //thêm thời gian bài đăng
        long timestamp = post.getTime().getSeconds() * 1000;
        Date date = new Date(timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(date);

        holder.time_post.setText(formattedDate);

        //menu chỉnh sửa
        holder.more_option.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.more_option);
            popupMenu.getMenuInflater().inflate(R.menu.post_popup_menu, popupMenu.getMenu());

            if (post.getUserID().equals(userID)) {
                popupMenu.getMenu().getItem(0).setVisible(false);
                popupMenu.getMenu().getItem(1).setVisible(false);
                popupMenu.getMenu().getItem(2).setVisible(false);
            }

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                Toast.makeText(context, "Chọn " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.follow){
                    return true;
                }
                else if(menuItem.getItemId() == R.id.author_infor){
                    return true;
                }
                else if(menuItem.getItemId() == R.id.unfollow){
                    return true;
                }
                else if (menuItem.getItemId() == R.id.edit_post) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Sửa caption");

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
                                post.setContent(newCaption);
                                postModel.postUpdate(post);
                                Toast.makeText(context, " Sửa caption thành công! " + newCaption, Toast.LENGTH_SHORT).show();
                                holder.caption.setText(post.getContent());
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
                else if (menuItem.getItemId() == R.id.delete_post) {
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setTitle("Xác nhận xóa")
                            .setMessage("Bạn có chắc chắn muốn xóa bài đăng này?")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    postModel.deletePost(post, new PostModel.OnPostDeletedCallback() {
                                        @Override
                                        public void onPostDeleted(boolean success) {
                                            if(success){
                                                if(position != RecyclerView.NO_POSITION){
                                                    posts.remove(position);
                                                    notifyItemRemoved(position);
                                                    Toast.makeText(holder.itemView.getContext(), "Xóa post thành công!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else {
                                                Toast.makeText(holder.itemView.getContext(), "Lỗi khi xóa bài đăng!", Toast.LENGTH_SHORT).show();
                                            }
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
                return false;
            });

            popupMenu.show();
        });

        //số like bài viết
        int likeCount = post.getLikedBy().size();
        holder.likes_count.setText(String.valueOf(likeCount));

        //hiển thị cho các bài viết đã được like bởi người dùng hiện tại
        if (post.getLikedBy().contains(userID)) {
            holder.like.setImageResource(R.drawable.liked);
            holder.like.clearColorFilter();
        }

        //xử lý khi nhấn thích bài viết
        holder.like.setOnClickListener(v -> {
            boolean isLiked = post.getLikedBy().contains(userID);
            if (isLiked) {
                post.getLikedBy().remove(userID);
                holder.like.setColorFilter(context.getResources().getColor(R.color.like_button_color));
                holder.like.setImageResource(R.drawable.like);
                postModel.postUpdate(post);
                Toast.makeText(context, "Đã bỏ like", Toast.LENGTH_SHORT).show();
            }
            else {
                post.getLikedBy().add(userID);
                holder.like.setImageResource(R.drawable.liked);
                holder.like.clearColorFilter();
                postModel.postUpdate(post);
                Toast.makeText(context, "Đã like", Toast.LENGTH_SHORT).show();
            }
            holder.likes_count.setText(String.valueOf(post.getLikedBy().size()));
        });

        // Load thông tin người dùng
         postModel.getUserInfor(userID, user -> {
             Picasso.get().load(user.getAvatar()).into(holder.avatar);
             holder.username.setText(user.getName());
         });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, more_option, like;
        TextView username, caption, likes_count, time_post, picture_counter;
        ViewPager2 imageViewPager;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.username);
            more_option = itemView.findViewById(R.id.more_option);
            caption = itemView.findViewById(R.id.caption);
            imageViewPager = itemView.findViewById(R.id.imageViewPager);
            like = itemView.findViewById(R.id.like);
            likes_count = itemView.findViewById(R.id.likes_count);
            time_post = itemView.findViewById(R.id.time_post);
            picture_counter = itemView.findViewById(R.id.picture_counter);
        }
    }
}

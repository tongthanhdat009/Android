package com.project.myapplication.controller;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.postImageAdapter;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class homeController {
    private final View view;
    private final PostModel postModel;
    private final onePostActivity postActivity;
    private final View homePostLayout;
    public homeController(View view, View homePostLayout) {
        this.view = view;
        this.homePostLayout = homePostLayout;
        postModel = new PostModel();
        postActivity = new onePostActivity(homePostLayout);
    }

    public void postDiplay(String userID) {
        postModel.getAllPost(new PostModel.OnPostListRetrievedCallback() {
            @SuppressLint("DefaultLocale")
            @Override
            public void getAllPost(ArrayList<Post> postsList) {
                LinearLayout postContainer = view.findViewById(R.id.post_container);
                postContainer.removeAllViews();
                for (Post post : postsList) {
                    ArrayList<Uri> mediaList = new ArrayList<>();
                    View postView = View.inflate(view.getContext(), R.layout.post_home_layout, null);

                    for (String media : post.getMedia()) {
                        mediaList.add(Uri.parse(media));
                    }
                    onePostActivity postActivity = new onePostActivity(postView);
                    if(post.getLikedBy().isEmpty()){
                        postActivity.likes_count.setText("0");
                    }else{
                        postActivity.likes_count.setText(String.format("%d",post.getLikedBy().size()));
                    }
                    postActivity.likes_count.setText(String.format("%d",post.getLikedBy().size()));
                    postActivity.caption.setText(post.getContent());
                    postActivity.username.setText(post.getUserID());
                    postActivity.imageViewPager.setAdapter(new postImageAdapter(view.getContext(), mediaList));
                    postActivity.more_option.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu popupMenu;
                            popupMenu = new PopupMenu(view.getContext(), postActivity.more_option);
                            popupMenu.getMenuInflater().inflate(R.menu.post_popup_menu, popupMenu.getMenu());
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    Toast.makeText(view.getContext(), "Chọn " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            });
                            popupMenu.show();
                        }
                    });

                    boolean isLiked = post.getLikedBy().contains(userID);
                    if (isLiked) {
                        postActivity.like.setImageResource(R.drawable.liked); // Hiển thị icon đã thích
                        postActivity.like.clearColorFilter(); // Xóa filter màu
                    } else {
                        postActivity.like.setColorFilter(view.getResources().getColor(R.color.like_button_color)); // Áp dụng màu cho icon chưa thích
                    }

// Thiết lập OnClickListener
                    postActivity.like.setOnClickListener(new View.OnClickListener() {
                        boolean isLiked = post.getLikedBy().contains(userID);
                        @Override
                        public void onClick(View v) {
                            if (isLiked) {
                                // Khi người dùng bỏ thích (Unlike)
                                post.getLikedBy().remove(userID); // Xóa userID khỏi danh sách liked
                                postActivity.like.setColorFilter(view.getResources().getColor(R.color.like_button_color)); // Đặt màu filter cho icon
                                postActivity.like.setImageResource(R.drawable.like); // Đặt icon chưa thích
                                postModel.postUpdate(post); // Cập nhật bài viết trên Firestore
                                Toast.makeText(view.getContext(), "Đã bỏ like", Toast.LENGTH_SHORT).show();
                            } else {
                                // Khi người dùng thích (Like)
                                post.getLikedBy().add(userID); // Thêm userID vào danh sách liked
                                postActivity.like.setImageResource(R.drawable.liked); // Đặt icon đã thích
                                postActivity.like.clearColorFilter(); // Xóa filter màu
                                postModel.postUpdate(post); // Cập nhật bài viết trên Firestore
                                Toast.makeText(view.getContext(), "Đã like", Toast.LENGTH_SHORT).show();
                            }

                            // Cập nhật trạng thái isLiked
                            isLiked = !isLiked; // Đảo ngược trạng thái
                            // Cập nhật số lượng likes
                            postActivity.likes_count.setText(String.format("%d", post.getLikedBy().size()));
                        }
                    });
                    postModel.getUserInfor(post.getUserID(), new PostModel.OnUserListRetrievedCallback() {
                        @Override
                        public void onUserListRetrievedCallback(User user) {
                            postActivity.username.setText(user.getName());
                            if (user.getAvatar() != null) {
                                Picasso.get().load(user.getAvatar()).into(postActivity.avatar);
                            }
                        }
                    });
                    postContainer.addView(postView);
                }
            }
        });
    }

    // Hoạt động trong giao diện post
    public static class onePostActivity {
        public ImageView avatar;
        public TextView username;
        public ImageView more_option;
        public TextView caption;
        public ViewPager2 imageViewPager;
        public LinearLayout action_bar;
        public ImageView like;
        public TextView likes_count;
        public ImageView comment;
        public TextView cmts_count;
        public onePostActivity(View view){
            avatar = view.findViewById(R.id.avatar);
            username = view.findViewById(R.id.username);
            more_option = view.findViewById(R.id.more_option);
            caption = view.findViewById(R.id.caption);
            imageViewPager = view.findViewById(R.id.imageViewPager);
            action_bar = view.findViewById(R.id.action_bar);
            like = view.findViewById(R.id.like);
            likes_count = view.findViewById(R.id.likes_count);
            comment = view.findViewById(R.id.comment);
            cmts_count = view.findViewById(R.id.cmts_count);
        }
    }
}

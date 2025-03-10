package com.project.myapplication.view.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.Timestamp;
import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Following;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.model.CommentModel;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.activity.authorProfileActivity;
import com.project.myapplication.view.activity.commentActivity;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final Context context;
    private ArrayList<Post> posts;
    private final String userID; // ID người dùng hiện tại
    private final PostModel postModel;
    private final CommentModel commentModel;
    public PostAdapter(Context context, ArrayList<Post> posts, String userID, PostModel postModel) {
        this.context = context;
        this.posts = posts;
        this.userID = userID;
        this.postModel = postModel;
        this.commentModel = new CommentModel();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_home_layout, parent, false);
        return new PostViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = posts.get(position);

        //đối tượng hiển thị
        holder.targetAudience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(post.getTargetAudience().equals("Công khai")){
                    Toast.makeText(context, "Trạng thái bài viết là công khai", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "Trạng thái bài viết là: Chỉ cho người theo dõi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (post.getTargetAudience().equals("Công khai")) {
            holder.targetAudience.setImageResource(R.drawable.baseline_public_24);
            holder.itemView.setVisibility(View.VISIBLE); // Hiển thị bài viết công khai
        } else {
            holder.targetAudience.setImageResource(R.drawable.baseline_public_off_24);

            postModel.getAllFollower(post.getUserID(), new PostModel.OnFollowerListRetrievedCallback() {
                @Override
                public void getAllFollower(ArrayList<Followers> followerList) {
                    Set<String> IDUserFollowed = new HashSet<>();
                    for (Followers follower : followerList) {
                        IDUserFollowed.add(follower.getUserID());
                    }

                    if (IDUserFollowed.contains(userID) || userID.equals(post.getUserID())) {
                        holder.itemView.setVisibility(View.VISIBLE);
                    }
                    else {
                        posts.remove(position);
                        notifyItemRemoved(position);
                    }
                }
            });
        }


        // Cài đặt nội dung cho từng bài đăng
        holder.caption.setText(post.getContent());
        holder.caption.setEllipsize(TextUtils.TruncateAt.END);
        holder.caption.setMaxLines(3);

        holder.caption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.caption.getEllipsize() != null) {
                    holder.caption.setEllipsize(null);
                    holder.caption.setMaxLines(Integer.MAX_VALUE);
                    holder.caption.setText(post.getContent());
                } else {
                    holder.caption.setEllipsize(TextUtils.TruncateAt.END);
                    holder.caption.setMaxLines(3);
                }
                holder.caption.requestLayout();
            }
        });


        //thêm ảnh vào post
        switch (post.getType()){
            case "Ảnh":
                holder.image_container.setVisibility(View.VISIBLE);
                ArrayList<Uri> mediaList = new ArrayList<>();
                for (String media : post.getMedia()) {
                    mediaList.add(Uri.parse(media));
                }
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
                break;
            case "Video":
                ExoPlayer player = new ExoPlayer.Builder(context).build();
                holder.playerView.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi bắt đầu tải video
                holder.playerView.setOnClickListener(v -> {
                    if (player.isPlaying()) {
                        player.pause();
                    } else {
                        player.play();
                    }
                });
                holder.itemView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    Rect rect = new Rect();
                    holder.itemView.getGlobalVisibleRect(rect);
                    int height = holder.itemView.getHeight();
                    int visibleHeight = rect.bottom - rect.top;

                    if (visibleHeight > height / 2) {
                        // Bắt đầu phát video nếu hơn 50% video hiển thị
                        player.setPlayWhenReady(true);
                    } else {
                        // Dừng video nếu ít hơn 50% video hiển thị
                        player.setPlayWhenReady(false);
                    }
                });

                holder.playerView.setPlayer(player);

                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(post.getMedia().get(0)));
                player.setMediaItem(mediaItem);

                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        if (playbackState == Player.STATE_READY) {
                            holder.progressBar.setVisibility(View.GONE); // Ẩn ProgressBar khi video sẵn sàng
                        } else if (playbackState == Player.STATE_BUFFERING) {
                            holder.progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar khi video đang buffer
                        } else if (playbackState == Player.STATE_ENDED) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }
                });

                player.prepare();
                player.setPlayWhenReady(true);
                player.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL); // Lặp lại video
                holder.playerView.setUseController(false); // Ẩn thanh điều khiển
                break;
        }


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
                popupMenu.getMenu().getItem(3).setVisible(true);
                popupMenu.getMenu().getItem(4).setVisible(true);
                if(post.getCommentMode()){
                    popupMenu.getMenu().getItem(6).setVisible(true);
                }
                else {
                    popupMenu.getMenu().getItem(5).setVisible(true);
                }
            }
            else{
                popupMenu.getMenu().getItem(2).setVisible(true);
                postModel.getAllFollowing(userID, followingList -> {
                    ArrayList<String> followingListID = new ArrayList<>();

                    // Lấy ID của những người đang theo dõi
                    for (Following following : followingList) {
                        followingListID.add(following.getUserID()); // Lưu ID theo dõi
//                        Toast.makeText(context, "followingListID: " + following.getIdFollowing(), Toast.LENGTH_SHORT).show();
                    }
                    if(followingListID.isEmpty()){
                        popupMenu.getMenu().getItem(0).setVisible(true); // Không theo dõi
                    }
                    else{
                        // Kiểm tra xem người dùng có đang theo dõi người đăng bài hay không
                        if (followingListID.contains(post.getUserID())) {
                            popupMenu.getMenu().getItem(1).setVisible(true); // Có theo dõi
                        } else {
                            popupMenu.getMenu().getItem(0).setVisible(true); // Không theo dõi
                        }
                    }
                });
            }

            popupMenu.setOnMenuItemClickListener(menuItem -> {
//                Toast.makeText(context, "Chọn " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                if(menuItem.getItemId() == R.id.follow){
                    postModel.addFollowingUser(userID, new Following("",post.getUserID(), Timestamp.now()), new PostModel.OnAddFollowingCallback(){
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onAddFollowing(boolean success) {
                            notifyDataSetChanged();
                        }
                    });
                    postModel.addFollowerUser(post.getUserID(), new Followers("", userID, Timestamp.now()), new PostModel.OnAddFollowerCallback() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onAddFollower(boolean success) {
                        }
                    });
                    return true;
                }
                else if(menuItem.getItemId() == R.id.unfollow){
                    // xóa người dùng trong following
                    postModel.getAllFollowing(userID, followingList -> {
                        String idFollowing = "";
                        for (Following following : followingList) {
                            if(following.getUserID().equals(post.getUserID())){
                                idFollowing = following.getIdFollowing();
                                break;
                            }
                        }
                        postModel.removeFollowingUser(userID, idFollowing, new PostModel.OnRemoveFollowingCallback(){

                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onRemoveFollowing(boolean success) {
                                notifyDataSetChanged();
                            }
                        });
                    });
                    //xóa người dùng trong follower
                    postModel.getAllFollower(post.getUserID(), followerList -> {
                        String idFollower = "";
                        for (Followers follower : followerList) {
                            if(follower.getUserID().equals(userID)){
                                idFollower = follower.getIdFollower();
                                break;
                            }
                        }
                        postModel.removeFollowerUser(post.getUserID(), idFollower, new PostModel.OnRemoveFollowerCallback(){
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onRemoveFollower(boolean success) {
                            }
                        });
                    });
//                    Toast.makeText(context,"Đã bỏ theo dõi", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if(menuItem.getItemId() == R.id.author_infor){
                    Intent intent = new Intent(context, authorProfileActivity.class);
                    intent.putExtra("userID", userID);
                    intent.putExtra("authorID", post.getUserID());
                    context.startActivity(intent);
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
//                                Toast.makeText(context, "Caption đã nhập: " + newCaption, Toast.LENGTH_SHORT).show();
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
                else if(menuItem.getItemId() == R.id.close_comment){
                    post.setCommentMode(false);
                    postModel.postUpdate(post);
                    holder.comment.setVisibility(View.GONE);
                    holder.commentsCount.setVisibility(View.GONE);
                }
                else if (menuItem.getItemId() == R.id.open_comment) {
                    post.setCommentMode(true);
                    postModel.postUpdate(post);
                    holder.comment.setVisibility(View.VISIBLE);
                    holder.commentsCount.setVisibility(View.VISIBLE);
                }
                return false;
            });

            try {
                @SuppressLint("DiscouragedPrivateApi") Field popup = PopupMenu.class.getDeclaredField("mPopup");
                popup.setAccessible(true);
                Object menuPopupHelper = popup.get(popupMenu);
                assert menuPopupHelper != null;
                menuPopupHelper.getClass()
                        .getDeclaredMethod("setForceShowIcon", boolean.class)
                        .invoke(menuPopupHelper, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        else {
            holder.like.setImageResource(R.drawable.like);
            holder.like.setColorFilter(context.getResources().getColor(R.color.like_button_color));
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
         postModel.getUserInfor(post.getUserID(), user -> {
             Picasso.get().load(user.getAvatar()).into(holder.avatar);
             holder.username.setText(user.getName());
         });

         //đếm số comment hiển tại
        commentModel.getAllCommentInPost(post.getPostID(), commentsList -> {
            holder.commentsCount.setText(String.valueOf(commentsList.size()));
        });

         // Sự kiện nút comment
        if (post.getCommentMode()){
            holder.comment.setOnClickListener(v -> {
                Intent intent = new Intent(context, commentActivity.class);
                intent.putExtra("postID", post.getPostID());
                intent.putExtra("userID", userID);
                context.startActivity(intent);
            });
        }
        else{
            holder.comment.setVisibility(View.GONE);
            holder.commentsCount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull PostViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.playerView.getPlayer() != null) {
            holder.playerView.getPlayer().release();
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull PostViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.playerView.getPlayer() != null) {
            holder.playerView.getPlayer().setPlayWhenReady(true);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, more_option, like, comment, targetAudience;
        TextView username, caption, likes_count, commentsCount, time_post, picture_counter;
        ViewPager2 imageViewPager;
        FrameLayout image_container;
        PlayerView playerView;
        ExoPlayer exoPlayer;
        ProgressBar progressBar;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            targetAudience = itemView.findViewById(R.id.target_audience);
            comment = itemView.findViewById(R.id.comment);
            avatar = itemView.findViewById(R.id.avatar);
            username = itemView.findViewById(R.id.username);
            more_option = itemView.findViewById(R.id.more_option);
            caption = itemView.findViewById(R.id.caption);
            imageViewPager = itemView.findViewById(R.id.imageViewPager);
            like = itemView.findViewById(R.id.like);
            likes_count = itemView.findViewById(R.id.likes_count);
            time_post = itemView.findViewById(R.id.time_post);
            picture_counter = itemView.findViewById(R.id.picture_counter);
            commentsCount = itemView.findViewById(R.id.cmts_count);
            image_container = itemView.findViewById(R.id.image_container);
            progressBar = itemView.findViewById(R.id.progressBar);
            
            playerView = itemView.findViewById(R.id.playerView);
            exoPlayer = new ExoPlayer.Builder(itemView.getContext()).build();
            playerView.setPlayer(exoPlayer);
            exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
        }

        public void releasePlayer() {
            if (exoPlayer != null) {
                exoPlayer.release();
                exoPlayer = null;
            }
        }
    }
}

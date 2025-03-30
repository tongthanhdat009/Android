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
import android.util.LruCache;
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

import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.LoadControl;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.RenderersFactory;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.Timestamp;
import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Following;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.manager.CacheManager;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context context;
    private final ArrayList<Post> posts;
    private final String userID; // ID người dùng hiện tại
    private final PostModel postModel;
    private final CommentModel commentModel;
    private ExoPlayer player;
    private int currentPlayingPosition = -1; // Vị trí video đang phát
    public final LruCache<Integer, ExoPlayer> playerCache = new LruCache<Integer, ExoPlayer>(3) {
        @Override
        protected void entryRemoved(boolean evicted, Integer key, ExoPlayer oldValue, ExoPlayer newValue) {
            if (evicted && oldValue != null) {
                oldValue.release();
            }
        }
    };
    private RecyclerView recyclerView;
    private boolean hasAddedScrollListener = false;
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

    @OptIn(markerClass = UnstableApi.class)
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
                holder.videoUrl = post.getMedia().get(0);
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.playerView.setVisibility(View.VISIBLE);
                // Ẩn các view không cần thiết
                holder.image_container.setVisibility(View.GONE);
                holder.playerView.setVisibility(View.VISIBLE);
                // Ẩn các view không cần thiết
                holder.image_container.setVisibility(View.GONE);
                holder.playerView.setVisibility(View.VISIBLE);
                // Ẩn các view không cần thiết
                holder.image_container.setVisibility(View.GONE);
                holder.playerView.setVisibility(View.VISIBLE);
//                holder.progressBar.setVisibility(View.VISIBLE);

                // Lấy player từ cache hoặc tạo mới
                ExoPlayer player = playerCache.get(position);
                boolean isNewPlayer = false;

                if (player == null) {
                    // Giải phóng player cũ nếu vượt quá giới hạn
                    limitActivePlayers(position);

                    // Tạo player mới với cấu hình tối ưu
                    DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
                    trackSelector.setParameters(
                            trackSelector.buildUponParameters()
                                    .setMaxVideoSize(480, 270)
                                    .setMaxVideoBitrate(500000)
                                    .setPreferredAudioLanguage("default")
                                    .build());

                    player = new ExoPlayer.Builder(context)
                            .setTrackSelector(trackSelector)
                            .setLoadControl(createLoadControl())
                            .setRenderersFactory(createRenderersFactory())
                            .setBandwidthMeter(new DefaultBandwidthMeter.Builder(context).build())
                            .build();

                    player.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
                    player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);

                    playerCache.put(position, player);
                    isNewPlayer = true;
                }

                // Thiết lập player cho view
                holder.playerView.setPlayer(player);
                holder.itemView.setTag(R.id.player_tag, player);

                // Chỉ thiết lập video source nếu là player mới
                if (isNewPlayer) {
                    // Thiết lập source với cache
                    SimpleCache simpleCache = CacheManager.getInstance(context);
                    String videoUrl = post.getMedia().get(0);

                    // Tạo factory với chất lượng thấp
                    CacheDataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory()
                            .setCache(simpleCache)
                            .setUpstreamDataSourceFactory(new DefaultDataSource.Factory(context))
                            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)));

//                    // Thiết lập các listener
//                    player.addListener(new Player.Listener() {
//                        @Override
//                        public void onPlaybackStateChanged(int playbackState) {
//                            if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;
//
//                            if (playbackState == Player.STATE_READY) {
//                                holder.progressBar.setVisibility(View.GONE);
//                            } else if (playbackState == Player.STATE_BUFFERING) {
//                                holder.progressBar.setVisibility(View.VISIBLE);
//                            } else if (playbackState == Player.STATE_ENDED) {
//                                holder.progressBar.setVisibility(View.GONE);
//                            } else if (playbackState == Player.STATE_IDLE) {
//                                holder.progressBar.setVisibility(View.GONE);
//                            }
//                        }
//
//                        @Override
//                        public void onPlayerError(@NonNull PlaybackException error) {
//                            holder.progressBar.setVisibility(View.GONE);
//                            // Xử lý lỗi
//                        }
//                    });

                    // Thiết lập MediaSource và prepare
                    player.setMediaSource(mediaSource);
                    player.prepare();
                }

                // Thêm tag position vào view để sử dụng trong scroll listener
                holder.itemView.setTag(R.id.position_tag, position);

                // Kiểm tra khả năng hiển thị để quyết định phát hay không
                Rect rect = new Rect();
                boolean isVisible = holder.itemView.getGlobalVisibleRect(rect);
                int visibleHeight = rect.bottom - rect.top;
                int totalHeight = holder.itemView.getHeight();

                float visibilityRatio = totalHeight > 0 ? (visibleHeight / (float) totalHeight) : 0;

                // CHỈ PHÁT VIDEO NẾU LÀ VIDEO HIỂN THỊ NHẤT VÀ ĐỦ HIỂN THỊ
                if (isVisible && visibilityRatio >0.5) {
                    // Dừng video đang phát (nếu có)
                    if (currentPlayingPosition != -1 && currentPlayingPosition != position) {
                        pauseVideoAtPosition(currentPlayingPosition);
                    }

                    // Phát video hiện tại
                    player.setPlayWhenReady(true);
                    currentPlayingPosition = position;
                } else {
                    player.setPlayWhenReady(false);
                }

                // Click listener để phát/dừng video
                holder.playerView.setOnClickListener(v -> {
                    ExoPlayer clickedPlayer = (ExoPlayer) holder.playerView.getPlayer();
                    if (clickedPlayer == null) return;

                    if (clickedPlayer.isPlaying()) {
                        clickedPlayer.pause();
                    } else {
                        // Nếu click để phát video này, dừng video đang phát trước đó
                        if (currentPlayingPosition != -1 && currentPlayingPosition != position) {
                            pauseVideoAtPosition(currentPlayingPosition);
                        }
                        clickedPlayer.play();
                        currentPlayingPosition = position;
                    }
                });
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
        if (holder.player != null) {
            holder.player.setPlayWhenReady(false);
            holder.player.release();
            holder.player = null; // Giải phóng bộ nhớ
    private void pauseVideoAtPosition(int position) {
        ExoPlayer player = playerCache.get(position);
        if (player != null) {
            player.setPlayWhenReady(false);
        }

        // Đặt lại biến theo dõi nếu đây là video đang phát
        if (currentPlayingPosition == position) {
            currentPlayingPosition = -1;
        }
    }

    @Override
    public void onViewRecycled(@NonNull PostViewHolder holder) {
        super.onViewRecycled(holder);

        // Chỉ detach player khỏi view, không release
        if (holder.playerView.getPlayer() != null) {
            holder.playerView.getPlayer().setPlayWhenReady(false);
            holder.playerView.setPlayer(null);
        }
    }

    // 3. Thay đổi phương thức onViewDetachedFromWindow
    @Override
    public void onViewDetachedFromWindow(@NonNull PostViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.playerView.getPlayer() != null) {
            // Không release, chỉ pause
            holder.playerView.getPlayer().setPlayWhenReady(false);
        }
    }

    // 4. Thay đổi phương thức onViewAttachedToWindow
    @Override
    public void onViewAttachedToWindow(@NonNull PostViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (holder.videoUrl != null) {
            ExoPlayer player = new ExoPlayer.Builder(context).build();
            holder.playerView.setPlayer(player);

            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(holder.videoUrl));
            player.setMediaItem(mediaItem);
            player.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
            holder.playerView.setUseController(false);

            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_READY) {
                        holder.progressBar.setVisibility(View.GONE);
                    } else if (playbackState == Player.STATE_BUFFERING) {
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }
                }
            });

            player.prepare();
            holder.player = player; // Lưu lại player để dừng sau này
        }
    }

    public void stopVideoIfNotVisible(PostViewHolder holder) {
        Rect rect = new Rect();
        holder.itemView.getGlobalVisibleRect(rect);

        int visibleHeight = rect.height();
        int itemHeight = holder.itemView.getHeight();

        float visiblePercent = (float) visibleHeight / itemHeight * 100;

        if (visiblePercent < 70) {
            if (holder.player != null && holder.player.isPlaying()) {
                holder.player.setPlayWhenReady(false);
            }
        } else {
            if (holder.player != null && !holder.player.isPlaying()) {
                holder.player.setPlayWhenReady(true);
        // Kiểm tra và phát video nếu view đủ hiển thị
        Rect rect = new Rect();
        boolean isVisible = holder.itemView.getGlobalVisibleRect(rect);
        int height = holder.itemView.getHeight();
        int visibleHeight = rect.bottom - rect.top;

        if (isVisible && visibleHeight >= height * 0.85) {
            if (holder.playerView.getPlayer() != null) {
                holder.playerView.getPlayer().setPlayWhenReady(true);
            }
        }
    }

        // Kiểm tra và phát video nếu view đủ hiển thị
        Rect rect = new Rect();
        boolean isVisible = holder.itemView.getGlobalVisibleRect(rect);
        int height = holder.itemView.getHeight();
        int visibleHeight = rect.bottom - rect.top;

        if (isVisible && visibleHeight >= height * 0.85) {
            if (holder.playerView.getPlayer() != null) {
                holder.playerView.getPlayer().setPlayWhenReady(true);
            }
        }
    }

        // Kiểm tra và phát video nếu view đủ hiển thị
        Rect rect = new Rect();
        boolean isVisible = holder.itemView.getGlobalVisibleRect(rect);
        int height = holder.itemView.getHeight();
        int visibleHeight = rect.bottom - rect.top;

        if (isVisible && visibleHeight >= height * 0.85) {
            if (holder.playerView.getPlayer() != null) {
                holder.playerView.getPlayer().setPlayWhenReady(true);
            }
        }
    }

    public void releaseAllPlayers() {
        // Lấy snapshot của tất cả entries trong cache
        Map<Integer, ExoPlayer> snapshot = playerCache.snapshot();

        // Duyệt qua các values của snapshot
        for (ExoPlayer player : snapshot.values()) {
            if (player != null) {
                player.stop();
                player.clearMediaItems();
                player.release();
            }
        }

        // Xóa toàn bộ entries trong cache
        playerCache.evictAll();
        currentPlayingPosition = -1; // Reset vị trí đang phát

        // Nếu sử dụng cache
        CacheManager.releaseCache();
    }

    // Phương thức thiết lập RecyclerView
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    // Phương thức kiểm tra tất cả video hiển thị
    public void checkVisibleVideos(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisible = layoutManager.findFirstVisibleItemPosition();
            int lastVisible = layoutManager.findLastVisibleItemPosition();

            for (int i = firstVisible; i <= lastVisible; i++) {
                View view = layoutManager.findViewByPosition(i);
                if (view != null) {
                    ExoPlayer player = (ExoPlayer) view.getTag(R.id.player_tag);
                    if (player != null) {
                        Rect rect = new Rect();
                        boolean isVisible = view.getGlobalVisibleRect(rect);
                        int height = view.getHeight();
                        int visibleHeight = rect.bottom - rect.top;

                        if (isVisible && visibleHeight >= height * 0.85) {
                            if (player.getPlaybackState() == Player.STATE_IDLE) {
                                player.prepare();
                            }
                            player.setPlayWhenReady(true);
                        } else {
                            player.setPlayWhenReady(false);
                        }
                    }
                }
            }
        }
    }

    private void limitActivePlayers(int currentPosition) {
        // Chỉ giữ tối đa 3 player
        int MAX_ACTIVE_PLAYERS = 3;
        if (playerCache.size() > MAX_ACTIVE_PLAYERS) {
            // Lấy snapshot của cache dưới dạng Map
            Map<Integer, ExoPlayer> snapshot = playerCache.snapshot();

            // Lấy tập hợp các key từ snapshot
            List<Integer> positions = new ArrayList<>(snapshot.keySet());

            // Sắp xếp theo khoảng cách
            positions.sort((pos1, pos2) -> {
                int distance1 = Math.abs(pos1 - currentPosition);
                int distance2 = Math.abs(pos2 - currentPosition);
                return Integer.compare(distance2, distance1); // Sắp xếp giảm dần theo khoảng cách
            });

            // Giải phóng player xa nhất
            for (int i = 0; i < positions.size() - MAX_ACTIVE_PLAYERS; i++) {
                int posToRemove = positions.get(i);
                ExoPlayer player = playerCache.get(posToRemove);
                if (player != null) {
                    player.stop();
                    player.clearMediaItems();
                    player.release();
                    playerCache.remove(posToRemove);
                }
            }
        }
    }
    public void resumeVisiblePlayers(RecyclerView recyclerView) {
        checkVisibleVideos(recyclerView);
    }

    public void pauseAllPlayers() {
        // Lấy snapshot của tất cả entries trong cache
        Map<Integer, ExoPlayer> snapshot = playerCache.snapshot();

        // Duyệt qua các values của snapshot
        for (ExoPlayer player : snapshot.values()) {
            if (player != null && player.isPlaying()) {
                player.setPlayWhenReady(false);
            }
        }
        // Không reset currentPlayingPosition để có thể tiếp tục phát khi trở lại
    }
    // Thêm vào constructor hoặc phương thức init
    private void setupScrollListener() {
        if (recyclerView != null && !hasAddedScrollListener) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // Khi scroll dừng lại, tìm video hiển thị nhiều nhất để phát
                        findMostVisibleVideoAndPlay();
                    }
                }
            });
            hasAddedScrollListener = true;
        }
    }

    /**
     * Tìm video hiển thị nhiều nhất và chỉ phát video đó
     */
    private void findMostVisibleVideoAndPlay() {
        if (recyclerView == null) return;

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        int lastVisible = layoutManager.findLastVisibleItemPosition();

        int mostVisiblePosition = -1;
        float maxVisibilityRatio = 0;

        // Duyệt qua các item hiển thị để tìm video hiển thị nhiều nhất
        for (int i = firstVisible; i <= lastVisible; i++) {
            if (i < 0 || i >= getItemCount()) continue;

            Post post = posts.get(i);
            if ("Video".equals(post.getType())) {
                View itemView = layoutManager.findViewByPosition(i);
                if (itemView == null) continue;

                Rect rect = new Rect();
                boolean isVisible = itemView.getGlobalVisibleRect(rect);
                if (!isVisible) continue;

                int visibleHeight = rect.bottom - rect.top;
                int totalHeight = itemView.getHeight();
                float visibilityRatio = totalHeight > 0 ? (visibleHeight / (float) totalHeight) : 0;

                // Lưu vị trí có tỷ lệ hiển thị cao nhất
                if (visibilityRatio > maxVisibilityRatio && visibilityRatio > 0.85) {
                    maxVisibilityRatio = visibilityRatio;
                    mostVisiblePosition = i;
                }
            }
        }

        // Dừng tất cả video đang phát
        if (currentPlayingPosition != -1 && currentPlayingPosition != mostVisiblePosition) {
            pauseVideoAtPosition(currentPlayingPosition);
        }

        // Chỉ phát video hiển thị nhiều nhất nếu đủ hiển thị (>85%)
        if (mostVisiblePosition != -1 && maxVisibilityRatio > 0.85) {
            playVideoAtPosition(mostVisiblePosition);
        }
    }

    /**
     * Phát video tại vị trí chỉ định
     */
    private void playVideoAtPosition(int position) {
        ExoPlayer player = playerCache.get(position);
        if (player != null) {
            if (player.getPlaybackState() == Player.STATE_IDLE) {
                player.prepare();
            }
            player.setPlayWhenReady(true);
            currentPlayingPosition = position;
        }
    }
    @OptIn(markerClass = UnstableApi.class)
    private LoadControl createLoadControl() {
        return new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        DefaultLoadControl.DEFAULT_MIN_BUFFER_MS / 2,  // Giảm một nửa min buffer
                        DefaultLoadControl.DEFAULT_MAX_BUFFER_MS / 2,  // Giảm một nửa max buffer
                        DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                        DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();
    }

    @OptIn(markerClass = UnstableApi.class)
    private RenderersFactory createRenderersFactory() {
        return new DefaultRenderersFactory(context)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
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
        String videoUrl;
        ExoPlayer player;

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

            // Trong ViewHolder hoặc khi bind ViewHolder
            playerView = itemView.findViewById(R.id.playerView);
            exoPlayer = new ExoPlayer.Builder(itemView.getContext()).build();
            playerView.setPlayer(exoPlayer);
            exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);


        public void releasePlayer() {
            if (exoPlayer != null) {
                exoPlayer.release();
                exoPlayer = null;
            }
            // Thiết lập exoPlayer làm player tag
            itemView.setTag(R.id.player_tag, exoPlayer);
        }
    }
}

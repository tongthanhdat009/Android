package com.project.myapplication.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.myapplication.DTO.ShortVideo;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.ShortModel;
import com.project.myapplication.view.fragment.CommentBottomSheetFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShortVideoAdapter extends RecyclerView.Adapter<ShortVideoAdapter.ShortVideoViewHolder> {
    private final List<ShortVideo> videoList;
    private final Map<String, ExoPlayer> preloadedPlayers = new HashMap<>();
    private final List<ShortVideoViewHolder> activeViewHolders = new ArrayList<>();

    @OptIn(markerClass = UnstableApi.class)
    public ShortVideoAdapter(Context context, List<ShortVideo> videoList) {
        this.videoList = videoList;

        // Preload mỗi video vào một ExoPlayer riêng
        DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context);
        for (ShortVideo video : videoList) {
            String url = video.getVideoUrl();
            ExoPlayer player = new ExoPlayer.Builder(context).build();
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
            player.setMediaSource(mediaSource);
            player.prepare();
            preloadedPlayers.put(url, player);
        }
    }

    @NonNull
    @Override
    public ShortVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_short_video, parent, false);
        return new ShortVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortVideoViewHolder holder, int position) {
        holder.setVideo(videoList.get(position));
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ShortVideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.play();
        activeViewHolders.add(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ShortVideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.pause();
        activeViewHolders.remove(holder);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public void pauseAllPlayers() {
        for (ShortVideoViewHolder holder : activeViewHolders) {
            holder.pause();
        }
    }

    class ShortVideoViewHolder extends RecyclerView.ViewHolder {
        private final PlayerView playerView;
        private boolean isLiked = false;
        private final ImageView likeButton;
        private final ImageView commentButton;
        private ExoPlayer exoPlayer;
        private ShortVideo currentVideo; // ✅ Thêm dòng này

        public ShortVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            likeButton = itemView.findViewById(R.id.likeButton);
            commentButton = itemView.findViewById(R.id.commentButton);

            likeButton.setOnClickListener(v -> {
                isLiked = !isLiked;
                likeButton.setImageResource(isLiked ? R.drawable.liked : R.drawable.like);
                likeButton.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.like_scale));
                Toast.makeText(itemView.getContext(), isLiked ? "Liked!" : "Unliked", Toast.LENGTH_SHORT).show();
            });

            commentButton.setOnClickListener(v -> {
                if (currentVideo != null) {
                    CommentBottomSheetFragment.newInstance(currentVideo.getId())
                            .show(((AppCompatActivity) itemView.getContext()).getSupportFragmentManager(), "CommentBottomSheet");
                }
            });

            playerView.setOnClickListener(v -> {
                if (exoPlayer != null) {
                    if (exoPlayer.isPlaying()) {
                        exoPlayer.pause();
                    } else {
                        exoPlayer.play();
                    }
                }
            });
        }

        @OptIn(markerClass = UnstableApi.class)
        public void setVideo(ShortVideo video) {
            currentVideo = video;
            String videoUrl = video.getVideoUrl();
            releasePlayer();

            TextView userNameTextView = itemView.findViewById(R.id.userNameTextView);
            TextView videoCaption = itemView.findViewById(R.id.videoCaption);
            TextView likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            ImageView avatarImageView = itemView.findViewById(R.id.avatarImageView);

            ShortModel shortModel= new ShortModel();
            shortModel.getUserInfo(video.getUserID(), new ShortModel.OnUserInfoCallback() {
                @Override
                public void onUserInfoRetrieved(User user) {
                    if (user != null) {
                        userNameTextView.setText(user.getUserName());
                        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                            Glide.with(itemView.getContext())
                                    .load(user.getAvatar())
                                    .placeholder(R.drawable.unknow_avatar)
                                    .circleCrop()
                                    .into(avatarImageView);
                        } else {
                            avatarImageView.setImageResource(R.drawable.unknow_avatar);
                        }
                    }
                }
            });
            likeCountTextView.setText(formatCount(video.getLikeBy().size()));
            videoCaption.setText(video.getTitle());

            // Dùng ExoPlayer đã preload nếu có
            if (preloadedPlayers.containsKey(videoUrl)) {
                exoPlayer = preloadedPlayers.get(videoUrl);
            } else {
                // Fallback nếu preload thất bại
                exoPlayer = new ExoPlayer.Builder(itemView.getContext()).build();
                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
            }

            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            playerView.setPlayer(exoPlayer);
        }

        public void play() {
            if (exoPlayer != null) exoPlayer.play();
        }

        public void pause() {
            if (exoPlayer != null) exoPlayer.pause();
        }

        public void releasePlayer() {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.release();
                exoPlayer = null;
            }
        }
    }
    private String formatCount(int count) {
        if (count >= 1000000) {
            return String.format("%.1fM", count / 1000000.0);
        } else if (count >= 1000) {
            return String.format("%.1fK", count / 1000.0);
        } else {
            return String.valueOf(count);
        }
    }
}

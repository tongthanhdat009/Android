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
    private final String userID;
    private final Map<String, ExoPlayer> preloadedPlayers = new HashMap<>();
    private final List<ShortVideoViewHolder> activeViewHolders = new ArrayList<>();

    @OptIn(markerClass = UnstableApi.class)
    public ShortVideoAdapter(Context context, List<ShortVideo> videoList, String userID) {
        this.videoList = videoList;
        this.userID = userID;
        setHasStableIds(true); // NEW: Tối ưu RecyclerView

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
        holder.bind(videoList.get(position));
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

    @Override
    public long getItemId(int position) {
        return videoList.get(position).getId().hashCode();
    }

    public void pauseAllPlayers() {
        for (ShortVideoViewHolder holder : activeViewHolders) {
            holder.pause();
        }
    }

    class ShortVideoViewHolder extends RecyclerView.ViewHolder {
        private final PlayerView playerView;
        private final ImageView likeButton, commentButton, avatarImageView;
        private final TextView userNameTextView, videoCaption, likeCountTextView, commentCountTextView;

        private ExoPlayer exoPlayer;
        private ShortVideo currentVideo;
        private final ShortModel shortModel = new ShortModel();

        public ShortVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            likeButton = itemView.findViewById(R.id.likeButton);
            commentButton = itemView.findViewById(R.id.commentButton);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            videoCaption = itemView.findViewById(R.id.videoCaption);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);

            likeButton.setOnClickListener(v -> {
                if (currentVideo == null) return;
                List<String> likeByList = currentVideo.getLikeBy();
                boolean alreadyLiked = likeByList.contains(userID);

                if (alreadyLiked) {
                    likeByList.remove(userID);
                    shortModel.toggleLike(currentVideo.getId(), userID, false);
                    likeButton.setImageResource(R.drawable.like);
                    Toast.makeText(itemView.getContext(), "Unliked", Toast.LENGTH_SHORT).show();
                } else {
                    likeByList.add(userID);
                    shortModel.toggleLike(currentVideo.getId(), userID, true);
                    likeButton.setImageResource(R.drawable.liked);
                    Toast.makeText(itemView.getContext(), "Liked!", Toast.LENGTH_SHORT).show();
                }

                likeButton.startAnimation(AnimationUtils.loadAnimation(itemView.getContext(), R.anim.like_scale));
                likeCountTextView.setText(formatCount(likeByList.size()));
            });

            commentButton.setOnClickListener(v -> {
                if (currentVideo != null) {
                    CommentBottomSheetFragment.newInstance(currentVideo.getId(), userID)
                            .show(((AppCompatActivity) itemView.getContext()).getSupportFragmentManager(), "CommentBottomSheet");
                }
            });

            playerView.setOnClickListener(v -> {
                if (exoPlayer != null) {
                    if (exoPlayer.isPlaying()) exoPlayer.pause();
                    else exoPlayer.play();
                }
            });
        }

        public void bind(ShortVideo video) {
            currentVideo = video;

            videoCaption.setText(video.getTitle());

            // Load user info
            shortModel.getUserInfo(video.getUserID(), user -> {
                if (user != null) {
                    userNameTextView.setText(user.getUserName());
                    Glide.with(itemView.getContext())
                            .load(user.getAvatar())
                            .placeholder(R.drawable.unknow_avatar)
                            .circleCrop()
                            .into(avatarImageView);
                }
            });

            // Load comment count
            shortModel.getAllCommentsForShort(video.getId(), comments -> {
                commentCountTextView.setText(formatCount(comments.size()));
            });

            // Load like UI
            boolean isLiked = video.getLikeBy().contains(userID);
            likeButton.setImageResource(isLiked ? R.drawable.liked : R.drawable.like);
            likeCountTextView.setText(formatCount(video.getLikeBy().size()));

            // Dùng player đã preload
            exoPlayer = preloadedPlayers.get(video.getVideoUrl());
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            playerView.setPlayer(exoPlayer);
        }

        public void play() {
            if (exoPlayer != null) exoPlayer.play();
        }

        public void pause() {
            if (exoPlayer != null) exoPlayer.pause();
        }
    }

    private String formatCount(int count) {
        if (count >= 1_000_000) return String.format("%.1fM", count / 1_000_000f);
        if (count >= 1_000) return String.format("%.1fK", count / 1_000f);
        return String.valueOf(count);
    }
}


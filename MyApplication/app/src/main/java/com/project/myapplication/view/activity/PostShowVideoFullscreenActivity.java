package com.project.myapplication.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.PlayerView;

import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.manager.CacheManager;
import com.project.myapplication.model.PostModel;

@UnstableApi
public class PostShowVideoFullscreenActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private DefaultTrackSelector trackSelector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_show_video_fullscreen_activity);

        // Ánh xạ view
        playerView = findViewById(R.id.playerView);
        ImageButton backBTN = findViewById(R.id.backBTN);

        // Xử lý sự kiện quay lại
        backBTN.setOnClickListener(v -> finish());

        // Lấy postID từ Intent
        String postID = getIntent().getStringExtra("postID");
        if (postID == null || postID.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy video", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy thông tin post và thiết lập video
        PostModel postModel = new PostModel();
        postModel.getPostByID(postID, new PostModel.OnGetPostByID() {
            @Override
            public void getPostByID(Post post) {
                if (post != null && post.getMedia() != null && !post.getMedia().isEmpty()) {
                    // Khởi tạo player và thiết lập media
                    initializePlayer();

                    // Lấy URL video đầu tiên từ media
                    String videoUrl = post.getMedia().get(0);

                    // Thiết lập nguồn media với cache
                    prepareVideoSource(videoUrl);
                } else {
                    Toast.makeText(PostShowVideoFullscreenActivity.this,
                            "Không tìm thấy video", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void initializePlayer() {
        if (player == null) {
            // Thiết lập track selector với cấu hình video
            trackSelector = new DefaultTrackSelector(this);
            trackSelector.setParameters(
                    trackSelector.buildUponParameters()
                            .setMaxVideoSize(1280, 720) // Giới hạn độ phân giải 720p
                            .setMaxVideoBitrate(1500000) // Giới hạn bitrate 1.5Mbps
                            .setPreferredAudioLanguage("vi") // Ưu tiên ngôn ngữ Tiếng Việt nếu có
            );

            // Tạo ExoPlayer với các tùy chỉnh
            player = new ExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .setHandleAudioBecomingNoisy(true) // Tự động dừng khi rút tai nghe
                    .setSeekBackIncrementMs(10000) // Tua lùi 10 giây
                    .setSeekForwardIncrementMs(10000) // Tua tới 10 giây
                    .build();

            // Khôi phục trạng thái phát nếu có
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);

            // Thiết lập listener để phát hiện trạng thái
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    String stateString;
                    switch (state) {
                        case Player.STATE_IDLE:
                            stateString = "Idle";
                            break;
                        case Player.STATE_BUFFERING:
                            stateString = "Buffering";
                            break;
                        case Player.STATE_READY:
                            stateString = "Ready";
                            break;
                        case Player.STATE_ENDED:
                            stateString = "Ended";
                            // Tự động loop video (tùy chọn)
                            player.seekTo(0);
                            player.pause();
                            break;
                        default:
                            stateString = "Unknown";
                            break;
                    }
                }

                @Override
                public void onPlayerError(androidx.media3.common.PlaybackException error) {
                    // Log chi tiết
                    String errorMessage = "Error code: " + error.errorCode + " - " + error.getMessage();
                    Log.e("PlayerError", errorMessage, error);

                    // Phân loại lỗi
                    switch (error.errorCode) {
                        case PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED:
                        case PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT:
                            Toast.makeText(PostShowVideoFullscreenActivity.this,
                                    "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                            break;

                        case PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED:
                        case PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED:
                            Toast.makeText(PostShowVideoFullscreenActivity.this,
                                    "Lỗi định dạng video", Toast.LENGTH_SHORT).show();
                            break;

                        case PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND:
                            Toast.makeText(PostShowVideoFullscreenActivity.this,
                                    "Không tìm thấy file video", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            Toast.makeText(PostShowVideoFullscreenActivity.this,
                                    "Lỗi phát video: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                    }

                    // Thử lại sau một khoảng thời gian
                    playerView.postDelayed(() -> {
                        if (player != null) {
                            player.prepare();
                            player.play();
                        }
                    }, 3000);
                }
            });

            // Gán player cho PlayerView
            playerView.setPlayer(player);

            // Thiết lập các tùy chọn hiển thị
            playerView.setControllerShowTimeoutMs(3000); // Ẩn controls sau 3 giây
            playerView.setControllerHideOnTouch(true);

            // Tùy chỉnh giao diện controls (tùy chọn)
            customizePlayerView();
        }
    }

    private void customizePlayerView() {
        // Tùy chỉnh giao diện PlayerView nếu cần
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);

        // Các tùy chỉnh khác như ẩn/hiện các nút, thanh progress, etc.
    }

    private void prepareVideoSource(String videoUrl) {
        try {
            // Sử dụng DataSource Factory trực tiếp thay vì cache để test
            DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);

            // Tạo MediaSource phù hợp với loại video
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);

            // Kiểm tra loại video và tạo MediaSource tương ứng
            if (videoUrl.contains(".m3u8")) {
                // HLS media source cho streaming adaptive
                HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mediaItem);
                player.setMediaSource(hlsMediaSource);
            } else {
                // Progressive media source cho video thông thường
                ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mediaItem);
                player.setMediaSource(mediaSource);
            }

            // Chuẩn bị player
            player.prepare();

            // Bắt đầu phát
            player.play();
        } catch (Exception e) {
            // Log ngoại lệ
            Log.e("VideoPlayer", "Error preparing video source", e);
            Toast.makeText(this, "Không thể phát video này", Toast.LENGTH_SHORT).show();
        }
    }

    // Xử lý vòng đời của Activity để quản lý player đúng cách
    @Override
    protected void onStart() {
        super.onStart();
        if (player == null) {
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player == null) {
            initializePlayer();
        } else {
            player.play();
        }
        hideSystemUi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            // Lưu trạng thái phát hiện tại
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentMediaItemIndex();

            // Dừng phát
            player.pause();
            player.setPlayWhenReady(false); 
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            // Dừng hoàn toàn player khi ứng dụng background
            player.stop();
        }
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    private void releasePlayer() {
        if (player != null) {
            // Lưu trạng thái hiện tại
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentMediaItemIndex();

            // Giải phóng player
            player.release();
            player = null;
        }
    }

    private void hideSystemUi() {
        // Ẩn các UI hệ thống để có trải nghiệm fullscreen tốt hơn
        playerView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
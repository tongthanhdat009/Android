package com.project.myapplication.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.adapter.postImageAdapter;
import com.project.myapplication.view.components.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class postVideoController {
    private View view;
    private final PostModel postModel;
    private final CustomProgressDialog progressDialog;
    private final Spinner spinner;
    private final ImageView avatar;
    private final TextView userName;
    private final TextInputEditText caption;
    private final TextView wordCounter;
    private final Button chooseVideoBTN;
    private final LinearLayout placeholder;
    private Uri videoUri; // Lưu URI của video
    private ActivityResultLauncher<Intent> videoPickerLauncher;
    private final Button deleteVideoBTN;
    private final Button postVideoBTN;
    private final CheckBox allowComment;
    private PlayerView playerView;
    private ExoPlayer player;

    public postVideoController(View view, CustomProgressDialog progressDialog) {
        this.view = view;
        this.progressDialog = progressDialog;
        postModel = new PostModel();
        spinner = view.findViewById(R.id.target_audience);
        userName = view.findViewById(R.id.username);
        avatar = view.findViewById(R.id.avatar);
        caption = view.findViewById(R.id.content_input);
        wordCounter = view.findViewById(R.id.word_counter);
        chooseVideoBTN = view.findViewById(R.id.choose_video_Button);
        placeholder = view.findViewById(R.id.video_input_placeholder);
        deleteVideoBTN = view.findViewById(R.id.delete_video_button);
        postVideoBTN = view.findViewById(R.id.postVideoBTN);
        allowComment = view.findViewById(R.id.allow_comment_checkbox);
        playerView = view.findViewById(R.id.playerView);
        player = new ExoPlayer.Builder(view.getContext()).build();
        playerView.setPlayer(player);
        player.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
    }

    // Thêm mục vào Spinner
    public void addItemTargetSpinner() {
        List<String> targetAudience = Arrays.asList("Công khai", "Chỉ người theo dõi");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_item, targetAudience);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        spinner.setAdapter(adapter);
    }

    public void setUserInfor(String userID) {
        postModel.getUserInfor(userID, new PostModel.OnUserRetrievedCallback() {
            @Override
            public void onUserRetrievedCallback(User user) {
                if (user != null) {
                    userName.setText(user.getName());
                    String avatarUrl = user.getAvatar();
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Picasso.get().load(avatarUrl).into(avatar);
                    } else {
                        Log.d("setUserInfor", "Avatar URL is null or empty for user ID: " + userID);
                    }
                } else {
                    Log.d("setUserInfor", "User not found for ID: " + userID);
                }
            }
        });
    }

    public void wordCounter() {
        caption.addTextChangedListener(new TextWatcher() {
            @SuppressLint("DefaultLocale")
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int characterCount = charSequence.length();
                wordCounter.setText(String.format("%d / %d", characterCount, 500));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void setDeleteVideoBTN() {
        deleteVideoBTN.setOnClickListener(v -> {
            if (player != null) {
                player.stop(); // Dừng phát video
                player.release(); // Giải phóng bộ nhớ
                player = null;
            }
            videoUri = null; // Xóa URI video
            playerView.setPlayer(null); // Loại bỏ player
            placeholder.setVisibility(View.VISIBLE); // Hiện lại placeholder
            deleteVideoBTN.setVisibility(View.GONE); // Ẩn nút xóa video
            playerView.setVisibility(View.GONE); // Ẩn PlayerView
        });
    }


    public void registerVideoPicker(Fragment fragment) {
        videoPickerLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        videoUri = result.getData().getData();

                        player = new ExoPlayer.Builder(fragment.requireContext()).build();
                        playerView.setPlayer(player);

                        MediaItem mediaItem = MediaItem.fromUri(videoUri);
                        player.setMediaItem(mediaItem);
                        player.prepare();
                        player.setPlayWhenReady(true);

                        playerView.setVisibility(View.VISIBLE);
                        placeholder.setVisibility(View.GONE);
                        deleteVideoBTN.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    public void setPostVideoBTN(String userID) {
        postVideoBTN.setOnClickListener(view -> {
            progressDialog.show();
            String content = Objects.requireNonNull(caption.getText()).toString();
            if (!content.isEmpty() && videoUri != null) {
                ArrayList<String> likedBy = new ArrayList<>();
                Timestamp currentTime = Timestamp.now();

                uploadVideo(videoUri, new UploadCallback() {
                    @Override
                    public void onUploadSuccess(ArrayList<Uri> videoUrls) {
                        ArrayList<String> videoStrings = new ArrayList<>();
                        for (Uri url : videoUrls) {
                            videoStrings.add(url.toString());
                        }

                        Post newPost = new Post(
                                "",
                                userID,
                                content,
                                0,
                                0,
                                likedBy,
                                videoStrings,
                                currentTime,
                                spinner.getSelectedItem().toString(),
                                allowComment.isChecked(),
                                "Video",
                                0f
                        );

                        postModel.addPost(newPost, success -> {
                            progressDialog.dismiss();
                            if (success) {
                                Toast.makeText(view.getContext(), "Thêm bài viết thành công", Toast.LENGTH_SHORT).show();
                                caption.setText("");
                                deleteVideoBTN.setVisibility(View.GONE);
                                playerView.setVisibility(View.GONE);
                                placeholder.setVisibility(View.VISIBLE);
                                if (player != null) {
                                    player.stop();
                                    player.release();
                                    player = null;
                                }
                            } else {
                                Toast.makeText(view.getContext(), "Đăng bài thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onUploadFailure(String errorMessage) {
                        progressDialog.dismiss();
                        Toast.makeText(view.getContext(), "Tải video thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(view.getContext(), "Vui lòng nhập nội dung hoặc chọn video", Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface UploadCallback {

        void onUploadSuccess(ArrayList<Uri> videoUrls);
        void onUploadFailure(String errorMessage);

    }
    // Hàm upload ảnh lên Firebase Storage

    private void uploadVideo(Uri videoUri, UploadCallback callback) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        if (videoUri != null) {
            StorageReference fileRef = storageRef.child("Postuploads/" + System.currentTimeMillis() + ".mp4");
            fileRef.putFile(videoUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        ArrayList<Uri> videoUrls = new ArrayList<>();
                        videoUrls.add(uri);
                        callback.onUploadSuccess(videoUrls);
                    }))
                    .addOnFailureListener(e -> {
                        callback.onUploadFailure(e.getMessage());
                    });
        } else {
            callback.onUploadFailure("No video selected");
        }
    }

    public void chooseVideoButton() {
        chooseVideoBTN.setOnClickListener(v -> {
            if (videoPickerLauncher != null) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                videoPickerLauncher.launch(intent);
            } else {
                Log.e("chooseVideoButton", "videoPickerLauncher is null");
            }
        });
    }

    public void resetData() {
        caption.setText("");
        allowComment.setChecked(true);
        deleteVideoBTN.setVisibility(View.GONE);
        playerView.setVisibility(View.GONE);
        placeholder.setVisibility(View.VISIBLE);
        if (player != null) {
            player.stop();
        }
        spinner.setSelection(0);
    }
}

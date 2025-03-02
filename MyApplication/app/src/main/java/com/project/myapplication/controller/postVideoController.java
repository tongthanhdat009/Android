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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.components.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class postVideoController {
    private View view;
    private final CustomProgressDialog progressDialog;
    private final PostModel postModel;
    private final Spinner spinner;
    private final ImageView avatar;
    private final TextView userName;
    private final TextInputEditText caption;
    private final TextView wordCounter;
    private final Button chooseVideoBTN;
    private final VideoView videoView; // Thêm VideoView

    private Uri videoUri; // Lưu URI của video

    private ActivityResultLauncher<Intent> videoPickerLauncher;

    public postVideoController(View view, CustomProgressDialog progressDialog) {
        this.view = view;
        this.progressDialog = progressDialog;
        postModel = new PostModel();
        spinner = view.findViewById(R.id.spinner);
        userName = view.findViewById(R.id.username);
        avatar = view.findViewById(R.id.avatar);
        caption = view.findViewById(R.id.content_input);
        wordCounter = view.findViewById(R.id.word_counter);
        chooseVideoBTN = view.findViewById(R.id.choose_video_Button);
        videoView = view.findViewById(R.id.videoView); // Khởi tạo VideoView
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

    // Chọn video

    public void registerVideoPicker(Fragment fragment) {
        videoPickerLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        videoUri = result.getData().getData();
                        videoView.setVideoURI(videoUri);
                        videoView.start();
                    }
                }
        );
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


}

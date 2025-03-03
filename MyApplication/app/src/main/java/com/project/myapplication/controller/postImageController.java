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

import androidx.activity.result.ActivityResultLauncher;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

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

public class postImageController {
    private final View view;
    private final PostModel postModel;
    private final CustomProgressDialog progressDialog;
    private final ViewPager2 viewPager;
    private final Button chooseImageBTN;
    private final LinearLayout imageInputPlaceholder;
    private final CardView cardView;
    private final TextView imageCountText;
    private final Button deleteImageBTN;
    private final Spinner spinner;
    private ViewPager2.OnPageChangeCallback callback;
    private final ImageView avatar;
    private final TextView userName;

    private final TextInputEditText caption;

    private final Button postButton;
    private final CheckBox allowComment;
    private final TextView wordCounter;
    public postImageController(View view, CustomProgressDialog progressDialog) {

        this.view = view;
        this.progressDialog = progressDialog;
        postModel = new PostModel();
        viewPager = view.findViewById(R.id.viewPager);
        chooseImageBTN = view.findViewById(R.id.choose_image_Button);
        imageInputPlaceholder = view.findViewById(R.id.image_input_placeholder);
        cardView = view.findViewById(R.id.cardViewImageCount);
        deleteImageBTN = view.findViewById(R.id.delete_img_button);
        imageCountText = view.findViewById(R.id.imageCountText);
        spinner = view.findViewById(R.id.spinner);
        userName = view.findViewById(R.id.username);
        avatar = view.findViewById(R.id.avatar);
        caption = view.findViewById(R.id.content_input);
        postButton = view.findViewById(R.id.postBTN);
        allowComment = view.findViewById(R.id.allow_comment_checkbox);
        wordCounter = view.findViewById(R.id.word_counter);
    }

    // thêm mục vào spinner
    public void addItemTargetSpinner() {
        List<String> targetAudience = Arrays.asList("Công khai", "Chỉ người theo dõi");

        // Tạo ArrayAdapter để hiển thị danh sách trong Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_item, targetAudience);

        // Đặt layout cho danh sách dropdown
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);

        // Gán adapter cho Spinner
        spinner.setAdapter(adapter);
    }

    public void setUserInfor(String userID){
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

    // Xử lý sự kiện của nút đăng
    public void postBTNAction(ArrayList<Uri> imagesUriList, String userID) {
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String content = Objects.requireNonNull(caption.getText()).toString();
                if(!content.isEmpty()){
                    ArrayList<String>likedBy=new ArrayList<>();
                    Timestamp currentTime = Timestamp.now();
                    //up ảnh lên firebase storage
                    uploadImages(imagesUriList, new UploadCallback() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onUploadSuccess(ArrayList<Uri> imageUrls) {
                            ArrayList<String> imageString = new ArrayList<>();
                            for (Uri url : imageUrls) {
                                Log.d("DownloadLink", "Image URL: " + url);
                                imageString.add(url.toString());
                            }
                            Post newPost = new Post(
                                    "",
                                    userID, // userID
                                    content, // content
                                    0, // commentsCount ban đầu
                                    0, // likesCount ban đầu
                                    likedBy, // chưa có người like ban đầu
                                    imageString, // chưa có media ban đầu
                                    currentTime, // thời gian hiện tại
                                    spinner.getSelectedItem().toString(), // targetAudience
                                    allowComment.isChecked(), // mở chế độ comment
                                    "Ảnh",
                                    0f
                            );

                            postModel.addPost(newPost, new PostModel.OnAddPostSuccess() {
                                @Override
                                public void addPost(boolean success) {
                                    if(success){
                                        progressDialog.dismiss();
                                        Toast.makeText(
                                                view.getContext(),
                                                "Thêm bài viết thành công",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        caption.setText("");
                                        deleteImageBTN.setVisibility(View.GONE);
                                        imageInputPlaceholder.setVisibility(View.VISIBLE);
                                        imagesUriList.clear();
                                        postImageAdapter adapter = (postImageAdapter) viewPager.getAdapter();
                                        if(adapter != null){
                                            adapter.notifyDataSetChanged();  // Báo cho adapter biết dữ liệu đã thay đổi
                                        }
                                    }
                                }
                            });

                        }
                        @Override
                        public void onUploadFailure(String errorMessage) {
                            progressDialog.dismiss();
                            Toast.makeText(view.getContext(), "Vui lòng chọn ít nhất 1 ảnh.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(
                            view.getContext(),
                            "Vui lòng nhập nội dung.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }



    // Interface để xử lý callback khi ảnh đã được upload
    interface UploadCallback {
        void onUploadSuccess(ArrayList<Uri> imageUrls); // Gọi khi tất cả các ảnh đã được upload thành công
        void onUploadFailure(String errorMessage);         // Gọi khi xảy ra lỗi upload
    }

    // Hàm upload ảnh lên Firebase Storage
    private void uploadImages(ArrayList<Uri> imageUris, UploadCallback callback) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        ArrayList<Uri> downloadLinks = new ArrayList<>();

        if (!imageUris.isEmpty()) {
            for (Uri imageUri : imageUris) {
                StorageReference fileRef = storageRef.child("Postuploads/" + System.currentTimeMillis() + ".jpg");
                fileRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Thêm link download vào danh sách
                            downloadLinks.add(uri);

                            // Kiểm tra nếu tất cả các ảnh đã được upload
                            if (downloadLinks.size() == imageUris.size()) {
                                // Gọi callback khi tất cả các ảnh đã được upload thành công
                                callback.onUploadSuccess(downloadLinks);
                            }
                        }))
                        .addOnFailureListener(e -> {
                            // Gọi callback nếu upload bị lỗi
                            callback.onUploadFailure(e.getMessage());
                        });
            }
        } else {
            // Không có ảnh được chọn
            callback.onUploadFailure("No images selected");
        }
    }

    //Xử lý sự kiện nút chọn ảnh
    public void chooseImgBTNAction(ActivityResultLauncher<Intent> pickImageLauncher) {
        chooseImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo Intent để mở thư viện ảnh
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                // Cho phép chọn nhiều ảnh
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                // Khởi chạy ActivityResultLauncher
                pickImageLauncher.launch(intent);
            }
        });
    }

    public void deleteImgBTNAction(ArrayList<Uri> imagesUriList) {
        deleteImageBTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                int imagePos = viewPager.getCurrentItem();

                if (!imagesUriList.isEmpty() && imagePos >= 0 && imagePos < imagesUriList.size()) {
                    imagesUriList.remove(imagePos);

                    postImageAdapter adapter = (postImageAdapter) viewPager.getAdapter();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                        displayImageCounter(imagesUriList); // Cập nhật lại số lượng ảnh
                    }

                    Toast.makeText(view.getContext(), "Đã xóa ảnh", Toast.LENGTH_SHORT).show();

                    if (imagesUriList.isEmpty()) {
                        deleteImageBTN.setVisibility(View.GONE);
                        viewPager.setVisibility(View.GONE);
                        imageInputPlaceholder.setVisibility(View.VISIBLE);
                        cardView.setVisibility(View.GONE); // Ẩn bộ đếm ảnh khi không có ảnh
                    }
                }
            }
        });
    }



    @SuppressLint("NotifyDataSetChanged")
    public void displayImageChosen(ArrayList<Uri> images) {
        boolean hasImages = images != null && !images.isEmpty();
        viewPager.setVisibility(hasImages ? View.VISIBLE : View.GONE);
        imageInputPlaceholder.setVisibility(hasImages ? View.GONE : View.VISIBLE);
        deleteImageBTN.setVisibility(hasImages ? View.VISIBLE : View.GONE);
        cardView.setVisibility(hasImages ? View.VISIBLE : View.GONE);

        if (hasImages) {
            initViewPager(images);
            postImageAdapter adapter = new postImageAdapter(view.getContext(), images);
            viewPager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            displayImageCounter(images);
        } else {
            release();
        }
    }

    @SuppressLint("SetTextI18n")
    public void displayImageCounter(ArrayList<Uri> images) {
        if (images.isEmpty()) {
            cardView.setVisibility(View.GONE);
            return;
        }
        int currentItem = viewPager.getCurrentItem() + 1;
        imageCountText.setText(currentItem + "/" + images.size());
    }

    public void initViewPager(ArrayList<Uri> images) {
        if (callback == null) {
            callback = new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    displayImageCounter(images);
                }
            };
            viewPager.registerOnPageChangeCallback(callback);
        }
    }

    public void release() {
        if (callback != null) {
            viewPager.unregisterOnPageChangeCallback(callback);
            callback = null;
        }
    }

    public void wordCounter (){
        caption.addTextChangedListener(new TextWatcher() {
            @SuppressLint("DefaultLocale")
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Không cần làm gì trước khi văn bản thay đổi
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Đếm số ký tự khi văn bản thay đổi
                int characterCount = charSequence.length();
                wordCounter.setText(String.format("%d / %d", characterCount, 500));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void resetData() {
        caption.setText("");
        allowComment.setChecked(true);
        deleteImageBTN.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        imageInputPlaceholder.setVisibility(View.VISIBLE);
        spinner.setSelection(0);
        imageCountText.setVisibility(View.GONE);
    }
}

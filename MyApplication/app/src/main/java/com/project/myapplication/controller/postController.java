package com.project.myapplication.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.myapplication.DTO.Comment;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.PostAdapter;
import com.project.myapplication.view.homeFragment;
import com.project.myapplication.view.postImageAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class postController {
    private final View view;
    private final PostModel postModel;
    private final postActitvityController postBTNController;

    // Constructor để nhận Activity và Spinner
    public postController(View view) {

        this.view = view;
        postBTNController = new postActitvityController(view);
        postModel = new PostModel();
    }

    // thêm mục vào spinner
    public void addItemTargetSpinner() {
        List<String> targetAudience = Arrays.asList("Công khai", "Bạn bè", "Chỉ mình tôi");

        // Tạo ArrayAdapter để hiển thị danh sách trong Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_item, targetAudience);

        // Đặt layout cho danh sách dropdown
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);

        // Gán adapter cho Spinner
        postActitvityController.spinner.setAdapter(adapter);
    }

    public void setUserInfor(String userID){
        postModel.getUserInfor(userID, new PostModel.OnUserRetrievedCallback() {
            @Override
            public void onUserRetrievedCallback(User user) {
                if (user != null) {
                    postActitvityController.userName.setText(user.getName());
                    String avatarUrl = user.getAvatar();
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Picasso.get().load(avatarUrl).into(postActitvityController.avatar);
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
        postBTNController.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = Objects.requireNonNull(postActitvityController.text.getText()).toString();
                if(!content.isEmpty()){
                    Toast.makeText(
                            view.getContext(),
                            "Chạy hàm thêm bài viết trong database",
                            Toast.LENGTH_SHORT
                    ).show();
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
                                    postActitvityController.text.getText().toString(), // content
                                    0, // commentsCount ban đầu
                                    0, // likesCount ban đầu
                                    likedBy, // chưa có người like ban đầu
                                    imageString, // chưa có media ban đầu
                                    currentTime, // thời gian hiện tại
                                    postActitvityController.spinner.getSelectedItem().toString(), // targetAudience
                                    true // mở chế độ comment
                            );

                            postModel.addPost(newPost);
                            Toast.makeText(
                                    view.getContext(),
                                    "Thêm bài viết thành công",
                                    Toast.LENGTH_SHORT
                            ).show();
                            postActitvityController.text.setText("");
                            postActitvityController.deleteImageBTN.setVisibility(View.GONE);
                            imagesUriList.clear();
                            postImageAdapter adapter = (postImageAdapter) postActitvityController.viewPager.getAdapter();
                            if(adapter != null){
                                adapter.notifyDataSetChanged();  // Báo cho adapter biết dữ liệu đã thay đổi
                            }
                        }
                        @Override
                        public void onUploadFailure(String errorMessage) {
                            // Xử lý khi upload thất bại
                            Toast.makeText(view.getContext(), "Upload failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(
                            view.getContext(),
                            "Vui lòng nhập nội dung và chọn ảnh",
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
        postBTNController.chooseImageBTN.setOnClickListener(new View.OnClickListener() {
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
        postActitvityController.deleteImageBTN.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                // Lấy vị trí hiện tại của ảnh trong ViewPager
                int imagePos = postActitvityController.viewPager.getCurrentItem();

                // Kiểm tra danh sách ảnh không rỗng và vị trí hợp lệ
                if (!imagesUriList.isEmpty() && imagePos >= 0 && imagePos < imagesUriList.size()) {
                    // Xóa ảnh từ danh sách
                    imagesUriList.remove(imagePos);

                    // Cập nhật lại adapter của ViewPager sau khi xóa ảnh
                    postImageAdapter adapter = (postImageAdapter) postActitvityController.viewPager.getAdapter();
                    if(adapter != null){
                        adapter.notifyDataSetChanged();  // Báo cho adapter biết dữ liệu đã thay đổi
                    }

                    // Hiển thị thông báo ảnh đã được xóa
                    Toast.makeText(view.getContext(), "Đã xóa ảnh", Toast.LENGTH_SHORT).show();

                    // Kiểm tra nếu danh sách rỗng, ẩn nút xóa và ViewPager
                    if (imagesUriList.isEmpty()) {
                        postActitvityController.deleteImageBTN.setVisibility(View.GONE);
                        postActitvityController.viewPager.setVisibility(View.GONE); // Ẩn ViewPager nếu không còn ảnh
                    }
                }
            }
        });
    }


    // hiển thị ảnh được chọn
    @SuppressLint("NotifyDataSetChanged")
    public void displayImageChosen(ArrayList<Uri> images){
        if (images != null && !images.isEmpty()) {
            // Nếu danh sách ảnh không rỗng, hiển thị ViewPager và nút xóa
            postImageAdapter adapter = new postImageAdapter(view.getContext(), images);
            postActitvityController.viewPager.setVisibility(View.VISIBLE);  // Hiển thị ViewPager
            postActitvityController.viewPager.setAdapter(adapter);  // Cập nhật adapter cho ViewPager
            adapter.notifyDataSetChanged();  // Thông báo adapter cập nhật dữ liệu
            // Hiển thị nút xóa
            postActitvityController.deleteImageBTN.setVisibility(View.VISIBLE);
        } else {
            // Nếu không có ảnh nào, ẩn ViewPager và nút xóa
            postActitvityController.viewPager.setVisibility(View.GONE);
            postActitvityController.deleteImageBTN.setVisibility(View.GONE);
        }
    }


    // Hoạt động trong giao diện post
    public static class postActitvityController {
        ImageButton chooseImageBTN;
        Button postButton;
        @SuppressLint("StaticFieldLeak")
        static TextView userName;
        @SuppressLint("StaticFieldLeak")
        static Button deleteImageBTN;
        @SuppressLint("StaticFieldLeak")
        static Spinner spinner;
        static TextInputEditText text;
        static ViewPager2 viewPager;
        static ImageView avatar;
        public postActitvityController(View view){
            avatar = view.findViewById(R.id.avatar);
            userName = view.findViewById(R.id.username);
            deleteImageBTN = view.findViewById(R.id.delete_img_button);
            chooseImageBTN = view.findViewById(R.id.choose_image_Button);
            postButton = view.findViewById(R.id.post_button);
            spinner = view.findViewById(R.id.spinner_target);
            viewPager = view.findViewById(R.id.viewPager);
            text = view.findViewById(R.id.content_input);
        }
    }
}

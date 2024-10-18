package com.project.myapplication.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.Timestamp;
import com.project.myapplication.DTO.Comment;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.postImageAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class postController {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private View view;
    private PostModel postModel;
    private postActitvityController postBTNController;

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
        postBTNController.spinner.setAdapter(adapter);
    }

    // Xử lý sự kiện của nút đăng
    public void postBTNAction(ArrayList<Uri> imagesUriList) {
        postBTNController.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = Objects.requireNonNull(postBTNController.text.getText()).toString();
                int imageCount = imagesUriList.size();
                if(!content.isEmpty()){
                    Toast.makeText(
                            view.getContext(),
                            "Chạy hàm thêm bài viết trong database",
                            Toast.LENGTH_SHORT
                    ).show();
                    ArrayList<String>likedBy=new ArrayList<>();
                    Timestamp currentTime = Timestamp.now();

                    Post newPost = new Post(
                            "post123",
                            "user123", // userID
                            postActitvityController.text.getText().toString(), // content
                            0, // commentsCount ban đầu
                            0, // likesCount ban đầu
                            likedBy, // chưa có người like ban đầu
                            imagesUriList, // chưa có media ban đầu
                            currentTime, // thời gian hiện tại
                            postActitvityController.spinner.getSelectedItem().toString(), // targetAudience
                            true // mở chế độ comment
                    );
                    Comment newComment = new Comment(
                            "user123",
                            postActitvityController.text.getText().toString(),
                            likedBy,
                            0,
                            currentTime);
                    postModel.addPostWithComment(newPost,newComment);
                    Toast.makeText(
                            view.getContext(),
                            "Thêm bài viết thành công",
                            Toast.LENGTH_SHORT
                    ).show();
                    postActitvityController.text.setText("");
                    postActitvityController.deleteImageBTN.setVisibility(View.GONE);
                    imagesUriList.clear();
                    postImageAdapter adapter = (postImageAdapter) postActitvityController.viewPager.getAdapter();
                    adapter.notifyDataSetChanged();  // Báo cho adapter biết dữ liệu đã thay đổi
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
                    adapter.notifyDataSetChanged();  // Báo cho adapter biết dữ liệu đã thay đổi

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
        static Button deleteImageBTN;
        static Spinner spinner;
        static TextInputEditText text;
        static ViewPager2 viewPager;
        public postActitvityController(View view){
            deleteImageBTN = view.findViewById(R.id.delete_img_button);
            chooseImageBTN = view.findViewById(R.id.choose_image_Button);
            postButton = view.findViewById(R.id.post_button);
            spinner = view.findViewById(R.id.spinner_target);
            viewPager = view.findViewById(R.id.viewPager);
            text = view.findViewById(R.id.content_input);
        }
    }
}

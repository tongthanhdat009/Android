package com.project.myapplication.controller;

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

import com.project.myapplication.R;
import com.project.myapplication.view.postImageAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class postController {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private View view;
    private postActitvityController postBTNController;

    // Constructor để nhận Activity và Spinner
    public postController(View view) {
        this.view = view;
        postBTNController = new postActitvityController(view);
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
    public void postBTNAction() {
        postBTNController.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        view.getContext(), // Hoặc activity, tùy thuộc vào cách bạn khởi tạo activity
                        "Đăng thành công",
                        Toast.LENGTH_SHORT
                ).show();
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

                    // Kiểm tra nếu danh sách rỗng, ẩn nút xóa
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
        if(!images.isEmpty()){
            postImageAdapter adapter = new postImageAdapter(view.getContext(), images);
            postActitvityController.viewPager.setAdapter(adapter);
            postActitvityController.deleteImageBTN.setVisibility(View.VISIBLE);
        }
        else{
            postActitvityController.viewPager.setVisibility(View.GONE);
            postActitvityController.deleteImageBTN.setVisibility(View.GONE);
        }
    }


    // Hoạt động trong giao diện post
    public static class postActitvityController {
        ImageButton chooseImageBTN;
        Button postButton;
        static Button deleteImageBTN;
        Spinner spinner;
        static ViewPager2 viewPager;
        public postActitvityController(View activity){
            deleteImageBTN = activity.findViewById(R.id.delete_img_button);
            chooseImageBTN = activity.findViewById(R.id.choose_image_Button);
            postButton = activity.findViewById(R.id.post_button);
            spinner = activity.findViewById(R.id.spinner_target);
            viewPager = activity.findViewById(R.id.viewPager);
        }
    }
}

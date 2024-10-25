package com.project.myapplication.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.project.myapplication.R;
import com.project.myapplication.controller.postController;
import com.project.myapplication.model.ChatBoxModel;
import com.project.myapplication.model.MessageModel;

import java.util.ArrayList;

public class postFragment extends Fragment {
    private String userID;
    private ArrayList<Uri> imagesUriList;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Khởi tạo danh sách URI
        imagesUriList = new ArrayList<>();

        // Lựa chọn đối tượng được xem bài viết
        postController controller = new postController(view);
        controller.addItemTargetSpinner();

        // Đăng ký ActivityResultLauncher để xử lý kết quả trả về từ việc chọn ảnh
        pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();

                    if (data.getClipData() != null) { // Khi người dùng chọn nhiều ảnh
                        int count = data.getClipData().getItemCount(); // Lấy số lượng ảnh đã chọn
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = data.getClipData().getItemAt(i).getUri(); // Lấy URI của từng ảnh
                            imagesUriList.add(imageUri); // Thêm vào danh sách URI
                        }
                    } else if (data.getData() != null) { // Khi người dùng chọn một ảnh
                        Uri imageUri = data.getData();
                        imagesUriList.add(imageUri); // Thêm URI vào danh sách
                    }
                    controller.displayImageChosen(imagesUriList);
                }
            }
        );
        //gán thông tin người dùng đang sử dụng trong phần post
        controller.setUserInfor(userID);

        // Kích hoạt chức năng chọn ảnh khi nhấn nút chọn ảnh trong postController
        controller.chooseImgBTNAction(pickImageLauncher);

        // Gọi hàm xử lý xóa ảnh
        controller.deleteImgBTNAction(imagesUriList);

        // Gọi hàm xác nhận đăng post
        controller.postBTNAction(imagesUriList, userID);

        return view; // Trả về view đã inflate
    }
}

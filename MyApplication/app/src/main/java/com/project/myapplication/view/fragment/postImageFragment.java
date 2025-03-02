package com.project.myapplication.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.myapplication.R;
import com.project.myapplication.controller.postImageController;
import com.project.myapplication.view.components.CustomProgressDialog;

import java.util.ArrayList;

public class postImageFragment extends Fragment {
    private ArrayList<Uri> imagesUriList;

    private Button chooseImageBTN;

    private postImageController controller;

    private FirebaseUser user;

    private FirebaseAuth auth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_image_fragment, container, false);
        CustomProgressDialog progressDialog = new CustomProgressDialog(view.getContext());
        controller = new postImageController(view, progressDialog);

        imagesUriList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        if (data.getClipData() != null) { // Khi người dùng chọn nhiều ảnh
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                imagesUriList.add(imageUri);
                            }
                        } else if (data.getData() != null) {
                            Uri imageUri = data.getData();
                            imagesUriList.add(imageUri);
                        }
                        controller.displayImageChosen(imagesUriList);
                    }
                }
        );
        controller.setUserInfor(user.getUid());
        controller.chooseImgBTNAction(pickImageLauncher);
        controller.displayImageChosen(imagesUriList);
        controller.deleteImgBTNAction(imagesUriList);
        controller.addItemTargetSpinner();
        controller.postBTNAction(imagesUriList,user.getUid());
        controller.wordCounter();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        controller.release();
    }
}

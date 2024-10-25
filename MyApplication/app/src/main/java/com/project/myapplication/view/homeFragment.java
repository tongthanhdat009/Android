package com.project.myapplication.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.project.myapplication.R;
import com.project.myapplication.controller.homeController;

import java.util.ArrayList;

public class homeFragment extends Fragment {
    private String userID;
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        LinearLayout postContainer = view.findViewById(R.id.post_container);

        View postView = inflater.inflate(R.layout.post_home_layout, postContainer, false);
//        for (int i = 0; i < 5; i++) { // Giả sử bạn muốn thêm 5 lần
//            postContainer.addView(postView);
//
//        }

        homeController controller = new homeController(view,postView);
        controller.postDiplay(userID);
        return view;
    }

}
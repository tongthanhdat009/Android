package com.project.myapplication.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.myapplication.R;
import com.project.myapplication.view.adapter.PostViewPagerAdapter;

import java.util.ArrayList;

public class postFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        viewPager.setUserInputEnabled(false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new postImageFragment());
        fragmentList.add(new postVideoFragment());
        PostViewPagerAdapter viewPagerAdapter = new PostViewPagerAdapter(requireActivity(), fragmentList);
        viewPager.setAdapter(viewPagerAdapter);

        String[] tabTitles = new String[]{"Ảnh", "Video"};
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();

//
//        // Lựa chọn đối tượng được xem bài viết
//        postController controller = new postController(view, progressDialog);
//        controller.addItemTargetSpinner();
//

//        //gán thông tin người dùng đang sử dụng trong phần post
//        controller.setUserInfor(userID);
//

//

//
//        // Gọi hàm xác nhận đăng post
//        controller.postBTNAction(imagesUriList, userID);
//
//        // Gọi hàm đếm sso ký tự
//        controller.wordCounter();
        return view; // Trả về view đã inflate
    }
}

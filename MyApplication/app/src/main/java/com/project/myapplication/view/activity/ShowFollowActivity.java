package com.project.myapplication.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.adapter.FollowShowViewPagerAdapter;
import com.project.myapplication.view.fragment.followerFragment;
import com.project.myapplication.view.fragment.followingFragment;

import java.util.ArrayList;
import java.util.Objects;

public class ShowFollowActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_show_activity);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TextView title = findViewById(R.id.title);
        ImageButton backBTN = findViewById(R.id.backBTN);
        PostModel postModel = new PostModel();

        String authorID = getIntent().getStringExtra("AuthorID");
        String currentUserID = getIntent().getStringExtra("CurrentUser");

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        postModel.getUserInfor(authorID, new PostModel.OnUserRetrievedCallback() {
            @Override
            public void onUserRetrievedCallback(User user) {
                title.setText(user.getName());
            }
        });

        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new followerFragment(authorID, currentUserID));
        fragmentList.add(new followingFragment(authorID, currentUserID));
        FollowShowViewPagerAdapter viewPagerAdapter = new FollowShowViewPagerAdapter(this, fragmentList);
        viewPager.setAdapter(viewPagerAdapter);

        String[] tabTitles = new String[]{"Người theo dõi", "Đang theo dõi"};
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();

//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                // Đặt lại màu nền cho tất cả các tab về mặc định
//                for (int i = 0; i < tabLayout.getTabCount(); i++) {
//                    View tabView = Objects.requireNonNull(tabLayout.getTabAt(i)).view;
//                    if(Objects.requireNonNull(tabLayout.getTabAt(i)).isSelected()){
//                        tabView.setBackgroundColor(ContextCompat.getColor(ShowFollowActivity.this, R.color.button_text)); // Màu mặc định
//                    }
//                    else{
//                        tabView.setBackgroundColor(ContextCompat.getColor(ShowFollowActivity.this, R.color.background)); // Màu mặc định
//                    }
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                // Không cần làm gì vì tab đã được đặt lại màu trong onTabSelected
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                // Không cần xử lý gì thêm
//            }
//        });

    }
}

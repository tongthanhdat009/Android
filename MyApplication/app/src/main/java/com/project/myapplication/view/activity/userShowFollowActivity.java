package com.project.myapplication.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.adapter.FollowShowViewPagerAdapter;
import com.project.myapplication.view.fragment.userFollowerFragment;
import com.project.myapplication.view.fragment.userFollowingFragment;

import java.util.ArrayList;

public class userShowFollowActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_show_activity);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TextView title = findViewById(R.id.title);
        ImageButton backBTN = findViewById(R.id.backBTN);
        PostModel postModel = new PostModel();

        String currentUserID = getIntent().getStringExtra("CurrentUser");

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        postModel.getUserInfor(currentUserID, new PostModel.OnUserRetrievedCallback() {
            @Override
            public void onUserRetrievedCallback(User user) {
                title.setText(user.getName());
            }
        });

        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new userFollowerFragment(currentUserID));
        fragmentList.add(new userFollowingFragment(currentUserID));
        FollowShowViewPagerAdapter viewPagerAdapter = new FollowShowViewPagerAdapter(this, fragmentList);
        viewPager.setAdapter(viewPagerAdapter);

        String[] tabTitles = new String[]{"Người theo dõi", "Đang theo dõi"};
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();
    }
}

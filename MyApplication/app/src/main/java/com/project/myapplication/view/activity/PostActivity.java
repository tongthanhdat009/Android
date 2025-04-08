package com.project.myapplication.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.myapplication.R;
import com.project.myapplication.view.adapter.PostViewPagerAdapter;
import com.project.myapplication.view.fragment.postImageFragment;
import com.project.myapplication.view.fragment.postVideoFragment;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        viewPager.setUserInputEnabled(false);

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new postImageFragment());
        fragmentList.add(new postVideoFragment());
        PostViewPagerAdapter viewPagerAdapter = new PostViewPagerAdapter(this, fragmentList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(1);

        String[] tabTitles = new String[]{"Ảnh", "Video"};
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Fragment fragment = viewPagerAdapter.createFragment(position);
                if (fragment instanceof postImageFragment) {
                    ((postImageFragment) fragment).resetData();
                } else if (fragment instanceof postVideoFragment) {
                    ((postVideoFragment) fragment).resetData();
                }
            }
        });
    }
}

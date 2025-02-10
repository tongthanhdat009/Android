package com.project.myapplication.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.adapter.PostShowVPagerAdapter;

public class postShowFullScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_show_fullscreen_activity);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        ImageButton backBTN = findViewById(R.id.backBTN);
        TextView pictureCounter = findViewById(R.id.picture_counter);

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        PostModel postModel = new PostModel();
        String postID = getIntent().getStringExtra("postID");
        postModel.getPostByID(postID, new PostModel.OnGetPostByID() {
            @Override
            public void getPostByID(Post post) {
                PostShowVPagerAdapter adapter = new PostShowVPagerAdapter(postShowFullScreenActivity.this, post.getMedia(), postModel);
                viewPager.setAdapter(adapter);
                if(post.getMedia().size() > 1){
                    pictureCounter.setVisibility(View.VISIBLE);
                }
                else{
                    pictureCounter.setVisibility(View.GONE);
                }
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        if(post.getMedia().size() > 1){
                            pictureCounter.setText(String.format("%d/%d", position + 1, post.getMedia().size()));
                        }
                    }
                });
            }
        });
    }
}

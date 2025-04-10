package com.project.myapplication.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.activity.PostShowVideoFullscreenActivity;
import com.project.myapplication.view.activity.postShowFullScreenActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class postShowAdapter extends RecyclerView.Adapter<postShowAdapter.postShowViewHolder> {
    private final Context context;
    private final ArrayList<Post> postsList;
    private final PostModel postModel;
    private final String userID;
    public postShowAdapter(Context context, ArrayList<Post> postsList, PostModel postModel, String userID){
        this.context = context;
        this.postsList = postsList;
        this.postModel = postModel;
        this.userID = userID;
    }
    @NonNull
    @Override
    public postShowAdapter.postShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_show_layout, parent, false);
        return new postShowAdapter.postShowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull postShowAdapter.postShowViewHolder holder, @SuppressLint("RecyclerView") int position) {
        setupGridItemSize(holder);

        Post post = postsList.get(position);
        handlePostVisibility(holder,post,position);

        loadPostThumbnail(holder,position);

        if (post.getTargetAudience().equals("Công khai")) {
            holder.itemView.setVisibility(View.VISIBLE); // Hiển thị bài viết công khai
        } else {
            postModel.getAllFollower(post.getUserID(), new PostModel.OnFollowerListRetrievedCallback() {
                @Override
                public void getAllFollower(ArrayList<Followers> followerList) {
                    Set<String> IDUserFollowed = new HashSet<>();
                    for (Followers follower : followerList) {
                        IDUserFollowed.add(follower.getUserID());
                    }

                    if (IDUserFollowed.contains(userID) || userID.equals(post.getUserID())) {
                        holder.itemView.setVisibility(View.VISIBLE);
                    }
                    else {
                        postsList.remove(position);
                        notifyItemRemoved(position);
                    }
                }
            });
        }

        if(!postsList.get(position).getMedia().isEmpty()){
            Picasso.get().load(postsList.get(position).getMedia().get(0)).resize(350,350).into(holder.thumbnail);
        }

        if(postsList.get(position).getMedia().size() > 1){
            holder.iconLayer.setVisibility(View.VISIBLE);
        }
        else {
            holder.iconLayer.setVisibility(View.GONE);
        }
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onClick(View view) {

                Toast.makeText(context,postsList.get(position).getType(),Toast.LENGTH_SHORT).show();
                switch (postsList.get(position).getType()){
                    case "Ảnh":
                        Intent intentImage = new Intent(context, postShowFullScreenActivity.class);
                        intentImage.putExtra("postID", postsList.get(position).getPostID());
                        context.startActivity(intentImage);
                        break;
                    case "Video":
                        Intent intentVideo = new Intent(context, PostShowVideoFullscreenActivity.class);
                        intentVideo.putExtra("postID", postsList.get(position).getPostID());
                        context.startActivity(intentVideo);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    private void handlePostVisibility(postShowAdapter.postShowViewHolder holder, Post post, int position) {
        if (post.getTargetAudience().equals("Công khai")) {
            holder.itemView.setVisibility(View.VISIBLE);
        } else {
            postModel.getAllFollower(post.getUserID(), followerList -> {
                Set<String> IDUserFollowed = new HashSet<>();
                for (Followers follower : followerList) {
                    IDUserFollowed.add(follower.getUserID());
                }

                if (IDUserFollowed.contains(userID) || userID.equals(post.getUserID())) {
                    holder.itemView.setVisibility(View.VISIBLE);
                } else {
                    // Cải thiện: Sử dụng notifyItemRemoved sau khi xóa
                    int positionToRemove = postsList.indexOf(post);
                    if (positionToRemove != -1) {
                        postsList.remove(positionToRemove);
                        notifyItemRemoved(positionToRemove);
                    }
                }
            });
        }
    }

    private void loadPostThumbnail(postShowAdapter.postShowViewHolder holder, int position) {
        Post post = postsList.get(position);

        if (!post.getMedia().isEmpty()) {
            String mediaUrl = post.getMedia().get(0);
            int targetSize = Math.max(holder.itemView.getLayoutParams().width,
                    holder.itemView.getLayoutParams().height);

            if (post.getType().equals("Video")) {
                // Hiển thị icon play cho video
                holder.videoPlayIcon.setVisibility(View.VISIBLE);

                // Sử dụng Glide để tạo thumbnail từ video URL
                try {
                    com.bumptech.glide.Glide.with(context)
                            .asBitmap() // Yêu cầu bitmap thay vì GIF
                            .load(mediaUrl)
                            .frame(1000000) // Lấy frame tại 1 giây
                            .centerCrop()
                            .override(targetSize, targetSize)
                            .into(holder.thumbnail);
                } catch (Exception e) {
                    // Fallback nếu Glide gặp vấn đề
                    Log.e("ThumbnailLoader", "Error loading video thumbnail", e);
                }
            } else {
                // Ẩn icon play vì đây là ảnh
                holder.videoPlayIcon.setVisibility(View.GONE);

                // Tải ảnh bình thường với Picasso
                Picasso.get()
                        .load(mediaUrl)
                        .resize(targetSize, targetSize)
                        .centerCrop()
                        .into(holder.thumbnail);
            }
        } else {
            // Không có media
            holder.videoPlayIcon.setVisibility(View.GONE);
        }
    }

    private void setupGridItemSize(postShowAdapter.postShowViewHolder holder) {
        // Lấy kích thước màn hình
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

        // Tính toán số cột (giả sử 3 cột)
        int spanCount = 3;

        // Tính kích thước mỗi item dựa trên số cột và khoảng cách
        int spacing = context.getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        if (spacing == 0) spacing = 4; // Mặc định nếu không có resource

        // Tính toán kích thước thực tế của mỗi item
        int itemSize = (screenWidth - (spacing * (spanCount + 1))) / spanCount;

        // Áp dụng kích thước cho itemView
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = itemSize;
        layoutParams.width = itemSize;
        holder.itemView.setLayoutParams(layoutParams);
    }

    public static class postShowViewHolder extends RecyclerView.ViewHolder {
        ImageView iconLayer,thumbnail,videoPlayIcon;

        public postShowViewHolder(@NonNull View itemView) {
            super(itemView);
            iconLayer = itemView.findViewById(R.id.icon_layer);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            videoPlayIcon = itemView.findViewById(R.id.video_play_icon);
        }
    }
}

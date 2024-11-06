package com.project.myapplication.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostShowVPagerAdapter extends RecyclerView.Adapter<PostShowVPagerAdapter.PSVPHolder> {
    private final Context context;
    private final ArrayList<String> imageList;
    private final PostModel postModel;
    public PostShowVPagerAdapter(Context context, ArrayList<String> imageList, PostModel postModel){
        this.context = context;
        this.imageList = imageList;
        this.postModel = postModel;
    }
    @NonNull
    @Override
    public PostShowVPagerAdapter.PSVPHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_show_item, parent, false);
        return new PostShowVPagerAdapter.PSVPHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostShowVPagerAdapter.PSVPHolder holder, int position) {
        Picasso.get()
                .load(imageList.get(position))
                .into(holder.imageView); // Hiển thị ảnh vào ImageView
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class PSVPHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView pictureCounter;
        public PSVPHolder(@NonNull View itemView) {
            super(itemView);
            pictureCounter = itemView.findViewById(R.id.picture_counter);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

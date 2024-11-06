package com.project.myapplication.view;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Comment;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.model.CommentModel;
import com.project.myapplication.model.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
    public void onBindViewHolder(@NonNull postShowAdapter.postShowViewHolder holder, int position) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = 350;
        layoutParams.width = 350;
        holder.itemView.setLayoutParams(layoutParams);
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
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, postShowFullScreenActivity.class);
                intent.putExtra("postID", postsList.get(position).getPostID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public static class postShowViewHolder extends RecyclerView.ViewHolder {
        ImageView iconLayer,thumbnail;

        public postShowViewHolder(@NonNull View itemView) {
            super(itemView);
            iconLayer = itemView.findViewById(R.id.icon_layer);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}

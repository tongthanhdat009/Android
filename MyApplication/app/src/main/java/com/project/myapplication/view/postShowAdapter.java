package com.project.myapplication.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Comment;
import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.R;
import com.project.myapplication.model.CommentModel;
import com.project.myapplication.model.PostModel;
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
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = 350;
        layoutParams.width = 350;
        holder.itemView.setLayoutParams(layoutParams);

        Post post = postsList.get(position);

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

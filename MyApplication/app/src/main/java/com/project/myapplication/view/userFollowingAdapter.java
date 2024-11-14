package com.project.myapplication.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Following;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class userFollowingAdapter extends RecyclerView.Adapter<userFollowingAdapter.followingViewHolder> {
    private final Context context;
    private final PostModel postModel;
    private final ArrayList<Following> followingList;
    private final String currentUserID;
    public userFollowingAdapter(Context context, String currentUserID, ArrayList<Following> followingList, PostModel postModel){
        this.context = context;
        this.followingList = followingList;
        this.postModel = postModel;
        this.currentUserID = currentUserID;
    }
    @NonNull
    @Override
    public userFollowingAdapter.followingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.following_layout, parent, false);
        return new userFollowingAdapter.followingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userFollowingAdapter.followingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        postModel.getUserInfor(followingList.get(position).getUserID(), new PostModel.OnUserRetrievedCallback() {
            @Override
            public void onUserRetrievedCallback(User user) {
                holder.userName.setText(user.getName());
                Picasso.get().load(user.getAvatar()).into(holder.avatar);
            }
        });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, authorProfileActivity.class);
                intent.putExtra("authorID", followingList.get(position).getUserID());
                intent.putExtra("userID", currentUserID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return followingList.size();
    }

    public ArrayList<Following> getFollowingList(){
        return followingList;
    }
    public static class followingViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView userName;
        LinearLayout container;
        public followingViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.total_following_container);
            avatar = itemView.findViewById(R.id.avatar);
            userName = itemView.findViewById(R.id.username);
        }
    }
}

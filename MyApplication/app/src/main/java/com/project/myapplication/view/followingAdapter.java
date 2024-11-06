package com.project.myapplication.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Following;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class followingAdapter extends RecyclerView.Adapter<followingAdapter.followingViewHolder> {
    private final Context context;
    private final String userID;
    private final PostModel postModel;
    private final ArrayList<Following> followingList;
    private final String currentUserID;
    public followingAdapter(Context context, String userID, String currentUserID, ArrayList<Following> followingList, PostModel postModel){
        this.context = context;
        this.userID = userID;
        this.followingList = followingList;
        this.postModel = postModel;
        this.currentUserID = currentUserID;
    }
    @NonNull
    @Override
    public followingAdapter.followingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.following_layout, parent, false);
        return new followingAdapter.followingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull followingAdapter.followingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        postModel.getUserInfor(followingList.get(position).getUserID(), new PostModel.OnUserRetrievedCallback() {
            @Override
            public void onUserRetrievedCallback(User user) {
                holder.userName.setText(user.getName());
                Picasso.get().load(user.getAvatar()).into(holder.avatar);
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
        public followingViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            userName = itemView.findViewById(R.id.username);
        }
    }
}

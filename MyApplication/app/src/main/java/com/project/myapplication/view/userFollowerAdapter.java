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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class userFollowerAdapter extends RecyclerView.Adapter<userFollowerAdapter.followerViewHolder> {
    private Context context;
    private PostModel postModel;
    private ArrayList<Followers> followersList;
    private String currentUserID;
    public userFollowerAdapter(Context context, String currentUserID, ArrayList<Followers> followersList, PostModel postModel){
        this.context = context;
        this.followersList = followersList;
        this.postModel = postModel;
        this.currentUserID = currentUserID;
    }
    @NonNull
    @Override
    public userFollowerAdapter.followerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.follower_layout, parent, false);
        Toast.makeText(context, String.valueOf(getFollowersList().size()),Toast.LENGTH_SHORT).show();
        return new userFollowerAdapter.followerViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull userFollowerAdapter.followerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        postModel.getUserInfor(followersList.get(position).getUserID(), new PostModel.OnUserRetrievedCallback() {
            @Override
            public void onUserRetrievedCallback(User user) {
                holder.userName.setText(user.getName());
                if(!user.getAvatar().isEmpty()){
                    Picasso.get().load(user.getAvatar()).into(holder.avatar);
                }
            }
        });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, authorProfileActivity.class);
                intent.putExtra("authorID", followersList.get(position).getUserID());
                intent.putExtra("userID", currentUserID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return followersList.size();
    }

    public ArrayList<Followers> getFollowersList(){
        return followersList;
    }
    public static class followerViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView userName;
        LinearLayout container;
        public followerViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.total_followers_container);
            avatar = itemView.findViewById(R.id.avatar);
            userName = itemView.findViewById(R.id.username);
        }
    }
}

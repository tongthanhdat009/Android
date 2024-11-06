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
import com.project.myapplication.model.SearchModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class searchResultAdapter extends RecyclerView.Adapter<searchResultAdapter.resultViewHolder> {
    private final Context context;
    private SearchModel searchModel;
    private final ArrayList<User> usersList;
    private final String currentUserID;
    public searchResultAdapter(Context context, ArrayList<User> usersList, SearchModel searchModel, String currentUserID){
        this.context = context;
        this.usersList = usersList;
        this.searchModel = searchModel;
        this.currentUserID = currentUserID;
    }

    @NonNull
    @Override
    public searchResultAdapter.resultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_result_layout, parent, false);
        return new searchResultAdapter.resultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull resultViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(!usersList.get(position).getAvatar().isEmpty()){
            Picasso.get().load(usersList.get(position).getAvatar()).into(holder.avatar);
        }
        holder.userName.setText(usersList.get(position).getName());
        holder.resultContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,authorProfileActivity.class);
                intent.putExtra("userID", currentUserID);
                intent.putExtra("authorID", usersList.get(position).getUserID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class resultViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView userName;
        LinearLayout resultContainer;
        public resultViewHolder(@NonNull View itemView) {
            super(itemView);
            resultContainer = itemView.findViewById(R.id.result_container);
            avatar = itemView.findViewById(R.id.avatar);
            userName = itemView.findViewById(R.id.username);
        }
    }
}

package com.project.myapplication.view.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.Timestamp;
import com.project.myapplication.DTO.Notification;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.UserModel;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{
    private final Context context;
    private final ArrayList<Notification> notiList;
    private final UserModel userModel;
    public NotificationAdapter(Context context, ArrayList<Notification> notiList){
        this.context = context;
        this.notiList = notiList;
        this.userModel = new UserModel();
    }
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.noti_layout, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification noti = notiList.get(position);
        holder.body.setText(noti.getBody());

        Timestamp timestamp = noti.getTimestamp();
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(date);
        holder.time.setText(formattedTime);

        userModel.getUserInfor(noti.getSenderId(), new UserModel.OnGetUserInfor() {
            @Override
            public void getInfor(User user) {
                Picasso.get().load(user.getAvatar()).into(holder.avatar);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notiList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView body, time;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatar);
            this.body = itemView.findViewById(R.id.body);
            this.time = itemView.findViewById(R.id.time);
        }
    }
}

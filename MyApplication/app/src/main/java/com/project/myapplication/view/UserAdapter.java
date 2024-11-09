package com.project.myapplication.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private final OnUserCheckedListener onUserCheckedListener;

    // Giao diện callback khi checkbox được chọn/bỏ chọn
    public interface OnUserCheckedListener {
        void onUserChecked(User user, boolean isChecked);
    }

    public UserAdapter(List<User> userList, OnUserCheckedListener onUserCheckedListener) {
        this.userList = userList;
        this.onUserCheckedListener = onUserCheckedListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Cập nhật danh sách người dùng
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged(); // Cập nhật lại RecyclerView
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userAvatar;
        CheckBox userCheckBox;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userCheckBox = itemView.findViewById(R.id.customCheckBox);

            userCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onUserCheckedListener.onUserChecked(userList.get(position), isChecked);
                }
            });
        }

        void bind(User user) {
            userName.setText(user.getName());

            // Load avatar nếu có, nếu không hiển thị hình mặc định
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Picasso.get()
                        .load(user.getAvatar())
                        .placeholder(R.drawable.unknow_avatar) // Ảnh mặc định nếu chưa có avatar
                        .into(userAvatar);
            } else {
                userAvatar.setImageResource(R.drawable.unknow_avatar);
            }

            // Đặt giá trị cho checkbox
            userCheckBox.setChecked(false); // Đặt mặc định chưa chọn
        }
    }
}


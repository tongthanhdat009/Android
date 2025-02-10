package com.project.myapplication.view.adapter;

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
    private int selectedPosition = -1; // Track the selected checkbox position

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = userList.get(position);
        holder.bind(user);

        // Set the checkbox state based on whether this item is selected
        holder.userCheckBox.setChecked(position == selectedPosition);

        // Handle checkbox click events
        holder.userCheckBox.setOnClickListener(v -> {
            if (selectedPosition == position) {
                selectedPosition = -1; // Nếu checkbox được chọn lại thì bỏ chọn
                holder.userCheckBox.setChecked(false);
            } else {
                int previousSelectedPosition = selectedPosition;
                selectedPosition = position; // Cập nhật selectedPosition

                // Cập nhật lại chỉ các mục thay đổi
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public User getSelectedUser() {
        if (selectedPosition != -1 && selectedPosition < userList.size()) {
            return userList.get(selectedPosition);
        }
        return null; // Nếu không có người dùng nào được chọn
    }

    // Method to update user list and reset selection when necessary
    public void updateList(List<User> newUserList) {
        this.userList = newUserList;
        // Giữ lại selectedPosition khi danh sách được cập nhật
        if (selectedPosition >= newUserList.size()) {
            selectedPosition = -1; // Nếu selectedPosition không hợp lệ sau khi cập nhật, đặt lại.
        }
        notifyDataSetChanged(); // Cập nhật RecyclerView
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
        }

        void bind(User user) {
            userName.setText(user.getName() != null && !user.getName().isEmpty() ? user.getName() : "Tên không xác định");

            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Picasso.get()
                        .load(user.getAvatar())
                        .placeholder(R.drawable.unknow_avatar)
                        .into(userAvatar);
            } else {
                userAvatar.setImageResource(R.drawable.unknow_avatar);
            }
        }
    }
}

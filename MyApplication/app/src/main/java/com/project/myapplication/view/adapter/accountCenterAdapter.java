package com.project.myapplication.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.myapplication.R;
import com.project.myapplication.view.activity.deleteAccountActivity;
import com.project.myapplication.view.activity.resetPasswordActivity;
import com.project.myapplication.view.activity.verifyEmailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class accountCenterAdapter extends RecyclerView.Adapter<accountCenterAdapter.AccountCenterHolder> {
    private final Context context;
    private final List<Map.Entry<Integer, String>> itemList; // Danh sách icon và text

    private final String userID;
    public accountCenterAdapter(Context context, String userID) {
        this.context = context;
        this.userID = userID;
        // Khởi tạo HashMap chứa icon và text
        HashMap<Integer, String> iconTextMap = new HashMap<>();
        iconTextMap.put(R.drawable.baseline_app_settings_alt_24, "Đổi mật khẩu");
        iconTextMap.put(R.drawable.baseline_delete_24, "Xoá tài khoản");
        iconTextMap.put(R.drawable.baseline_attach_email_24,"Xác thực email");

        // Chuyển HashMap thành danh sách để dễ truy xuất
        this.itemList = new ArrayList<>(iconTextMap.entrySet());
    }

    @NonNull
    @Override
    public accountCenterAdapter.AccountCenterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.account_center_item, parent, false);
        return new AccountCenterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountCenterHolder holder, int position) {
        Map.Entry<Integer, String> item = itemList.get(position);
        holder.iconView.setImageResource(item.getKey());
        holder.textView.setText(item.getValue());
        holder.itemView.setOnClickListener(v->{
            switch (item.getValue()){
                case "Xác thực email":
                    Intent intent = new Intent(context, verifyEmailActivity.class);
                    intent.putExtra("userID",userID);
                    context.startActivity(intent);
                    break;
                case "Đổi mật khẩu":
                    Intent intent1 = new Intent(context, resetPasswordActivity.class);
                    intent1.putExtra("userID",userID);
                    context.startActivity(intent1);
                    break;
                case "Xoá tài khoản":
                    Intent intent2 = new Intent(context, deleteAccountActivity.class);
                    intent2.putExtra("userID",userID);
                    context.startActivity(intent2);
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class AccountCenterHolder extends RecyclerView.ViewHolder {
        ImageView iconView;
        TextView textView;

        public AccountCenterHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.icon_option); // ID từ account_center_item.xml
            textView = itemView.findViewById(R.id.title);
        }
    }
}

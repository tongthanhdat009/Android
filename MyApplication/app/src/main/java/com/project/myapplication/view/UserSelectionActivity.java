package com.project.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.ChatBoxModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UserSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>(); // Khởi tạo danh sách người dùng trống
    private ChatBoxModel chatBoxModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBoxModel = new ChatBoxModel();
        String userID = getIntent().getStringExtra("userID");
        setContentView(R.layout.activity_user_selection);

        ImageButton backButton = findViewById(R.id.backButton);
        EditText searchUser = findViewById(R.id.searchUser);
        ImageButton addButton = findViewById(R.id.add_button);
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);

        // Thiết lập nút quay lại
        backButton.setOnClickListener(v -> finish());

        addButton.setOnClickListener(v -> {
            User selectedUser = userAdapter.getSelectedUser();

            if (selectedUser == null) {
                // Nếu không có người dùng nào được chọn, thông báo cho người dùng
                Toast.makeText(this, "Vui lòng chọn người dùng để trò chuyện", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu có người dùng được chọn, tiếp tục tạo hoặc cập nhật chatbox
                String userID2 = (String)selectedUser.getUserID();

                chatBoxModel.createOrUpdateChatBox(userID, userID2, chatBox -> {
                    Intent intent = new Intent(this, message_activity.class);
                    intent.putExtra("chatBox", chatBox);
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                    finish();
                });
            }
        });

        // Thiết lập RecyclerView
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo UserAdapter và gán vào RecyclerView mà không cần callback
        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setAdapter(userAdapter);

        // Lấy danh sách người dùng
        fetchUsers(userID);

        // Thiết lập ô tìm kiếm
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchUsers(String userID) {
        chatBoxModel = new ChatBoxModel();
        // Lấy danh sách người dùng mà userID đang theo dõi
        chatBoxModel.getListFollowingUser(userID, fetchedUserList -> {
            if (fetchedUserList != null && !fetchedUserList.isEmpty()){
                // Sau khi có danh sách người dùng, lấy thông tin chi tiết người dùng
                chatBoxModel.getUsersInfo(fetchedUserList, users -> {
                    // Cập nhật danh sách `userList` và thông báo adapter cập nhật giao diện
                    userList.clear();
                    userList.addAll(users);
                    userAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void filterUsers(String query) {
        // Lọc danh sách người dùng dựa trên chuỗi tìm kiếm
        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        userAdapter.updateList(filteredList);
    }
}
package com.project.myapplication.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.myapplication.DTO.Message;
import com.project.myapplication.controller.chatController;
import com.project.myapplication.DTO.ChatBox;

import com.project.myapplication.R;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class chatFragment extends Fragment {

    private RecyclerView recyclerViewChat;
    private chatController chatController;
    private ArrayList<ChatBox> chatBoxList;

    public chatFragment() {
        // Required empty public constructor
    }

    public static chatFragment newInstance() {
        return new chatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Khởi tạo RecyclerView
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách chatBox
        chatBoxList = new ArrayList<>();

        // Tạo danh sách người dùng
        ArrayList<String> usersIDList = new ArrayList<>();
        usersIDList.add("UserID_1");
        usersIDList.add("UserID_2");

        // Tạo đối tượng ChatBox
        ChatBox chatBox1 = new ChatBox("User 1", false, "abc.png", usersIDList);
        ChatBox chatBox2 = new ChatBox("User 2", false, "abc.png", usersIDList);

        // Thêm vào danh sách
        chatBoxList.add(chatBox1);
        chatBoxList.add(chatBox2);

        ZonedDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = ZonedDateTime.now();
        }
        Message message = new Message(currentDateTime, "User1, User2", "Hello, how are you?", "user123");

        // Khởi tạo Adapter
        chatController = new chatController(chatBoxList,message);
        recyclerViewChat.setAdapter(chatController);

        return view; // Trả về view đã inflate
    }
}
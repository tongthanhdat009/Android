package com.project.myapplication.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.myapplication.DTO.Message;
import com.project.myapplication.controller.chatController;
import com.project.myapplication.DTO.ChatBox;

import com.project.myapplication.R;
import com.project.myapplication.model.ChatBoxModel;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class chatFragment extends Fragment {

    private RecyclerView recyclerViewChat;
    private chatController chatController;
    private ChatBoxModel chatBoxModel;
    public chatFragment() {

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
        chatBoxModel=new ChatBoxModel();
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Khởi tạo RecyclerView
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));

        chatBoxModel.getChatBoxList(new ChatBoxModel.OnChatBoxListRetrievedCallback() {
            @Override
            public void onChatBoxListRetrieved(List<ChatBox> chatBoxList) {
                if (chatBoxList != null) {
                    ZonedDateTime currentDateTime = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        currentDateTime = ZonedDateTime.now();
                    }
                    Message message = new Message(currentDateTime, "User1, User2", "Hello, how are you?", "user123");
                    chatController = new chatController(chatBoxList,message);
                    recyclerViewChat.setAdapter(chatController);
                } else {
                    showError("Failed to retrieve chat boxes");
                }
            }
        });
        return view;
    }
    //Thông báo lỗi
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
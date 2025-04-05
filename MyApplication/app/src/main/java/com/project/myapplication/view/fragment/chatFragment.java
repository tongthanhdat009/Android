package com.project.myapplication.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.project.myapplication.controller.chatController;

import com.project.myapplication.R;
import com.project.myapplication.model.ChatBoxModel;
import com.project.myapplication.view.activity.UserSelectionActivity;

import java.util.ArrayList;

public class chatFragment extends Fragment {

    private chatController chatController;
    private ChatBoxModel chatBoxModel;
    private String userID;
    public chatFragment() {}

    public static chatFragment newInstance() {
        return new chatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBoxModel=new ChatBoxModel();
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        RecyclerView recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));
        String TestuserID = userID;
        chatController = new chatController(new ArrayList<>(), TestuserID, view.getContext());
        recyclerViewChat.setAdapter(chatController);
        if (userID != null) {
            chatBoxModel.getChatBoxesByUserID(TestuserID, chatBoxList -> {
                chatController.updateChatBoxes(chatBoxList);
            });
        }

        ImageButton addChatBox = view.findViewById(R.id.addChatBox);
        addChatBox.setOnClickListener(v -> {
            // Truyền userID vào Intent
            Intent intent = new Intent(getActivity(), UserSelectionActivity.class);
            intent.putExtra("userID", userID); // Truyền userID vào Intent
            startActivityForResult(intent, 1); // Mã yêu cầu 1
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        // Kiểm tra và tạo ChatBox AI mỗi khi fragment được hiển thị
        if (userID != null) {
            chatBoxModel.checkAndCreateAIChatBox(userID);
            chatBoxModel.getChatBoxesByUserID(userID, chatBoxList -> {
                chatController.updateChatBoxes(chatBoxList);
            });
        }
    }
}
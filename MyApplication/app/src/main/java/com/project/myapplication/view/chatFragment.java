package com.project.myapplication.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.myapplication.controller.chatController;

import com.project.myapplication.R;
import com.project.myapplication.model.ChatBoxModel;

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
            userID = "user" + userID;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        RecyclerView recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));
        chatController = new chatController(new ArrayList<>(), userID);
        recyclerViewChat.setAdapter(chatController);
        if (userID != null) {
            chatBoxModel.getChatBoxesByUserID(userID, chatBoxList -> {
                chatController.updateChatBoxes(chatBoxList);
            });
        }
        return view;
    }
}
package com.project.myapplication.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.myapplication.DTO.Message;
import com.project.myapplication.controller.chatController;
import com.project.myapplication.DTO.ChatBox;

import com.project.myapplication.R;
import com.project.myapplication.model.ChatBoxModel;
import com.project.myapplication.model.MessageModel;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class chatFragment extends Fragment {

    private RecyclerView recyclerViewChat;
    private chatController chatController;
    private ChatBoxModel chatBoxModel;
    private MessageModel messageModel;
    private String userID;
    public chatFragment() {

    }

    public static chatFragment newInstance() {
        return new chatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatBoxModel=new ChatBoxModel();
        messageModel=new MessageModel();
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Khởi tạo RecyclerView
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Message> messageList = new ArrayList<>();
        chatBoxModel.getChatBoxList(userID,new ChatBoxModel.OnChatBoxListRetrievedCallback() {
            @Override
            public void onChatBoxListRetrieved(List<ChatBox> chatBoxList) {
                final int[] count = {0};
                for (ChatBox chatbox : chatBoxList) {
                    messageModel.getClosetMess(chatbox.getId(), newMessage -> {
                        messageList.add(newMessage);
                        count[0]++;
                        if (count[0] == chatBoxList.size()) {
                            Log.d("TEST", messageList.toString());
                            chatController = new chatController(chatBoxList, messageList);
                            recyclerViewChat.setAdapter(chatController);
                        }
                    });
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
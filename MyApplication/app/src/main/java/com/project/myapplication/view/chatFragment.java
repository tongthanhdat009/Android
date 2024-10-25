package com.project.myapplication.view;

import android.annotation.SuppressLint;
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
import com.project.myapplication.model.ChatBoxModel;
import com.project.myapplication.model.MessageModel;

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
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));

        chatController = new chatController(new ArrayList<>(), new ArrayList<>());
        recyclerViewChat.setAdapter(chatController);

        chatBoxModel.getChatBoxList(userID, new ChatBoxModel.OnChatBoxListRetrievedCallback() {
            @Override
            public void onChatBoxListRetrieved(List<ChatBox> chatBoxList) {
                if (chatBoxList != null) {
                    List<Message> messagesList = new ArrayList<>();
                    for (ChatBox chatBox : chatBoxList) {
                        messageModel.getClosetMess(chatBox.getId(), new MessageModel.OnClosetMessRetrievedCallback() {
                            @Override
                            public void onClosetMessRetrieved(Message message) {
                                if (message != null) {
                                    chatBox.setLastMessage(message.getText());
                                    chatController.updateData(chatBoxList);
                                }
                            }
                        });
                    }
                }
            }
        });

        return view;
    }
}
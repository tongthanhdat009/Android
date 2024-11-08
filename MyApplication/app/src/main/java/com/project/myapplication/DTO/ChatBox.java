package com.project.myapplication.DTO;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.Map;

public class ChatBox {
    private String Id;
    private String name;
    private Map<String,Boolean> showed;
    private Timestamp lastMessageTimestamp;
    private String lastMessage;
    private String image_url;

    public ChatBox() {}

    public ChatBox(String id, String name, Map<String, Boolean> showed, Timestamp lastMessageTimestamp, String lastMessage, String image_url) {
        Id = id;
        this.name = name;
        this.showed = showed;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessage = lastMessage;
        this.image_url = image_url;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setShowed(Map<String, Boolean> showed) {
        this.showed = showed;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getShowed() {
        return showed;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public String getImage_url() {
        return image_url;
    }
}

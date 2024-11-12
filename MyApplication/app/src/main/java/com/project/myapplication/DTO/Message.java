package com.project.myapplication.DTO;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.List;

public class Message {
    private String chatboxID;
    private Timestamp datetime;
    private List<String> media;
    private Boolean seenBy;
    private String text;
    private String userID;

    public Message(){};
    public Message(String chatboxID, Timestamp datetime, List<String> media, Boolean seenBy, String text, String userID){
        this.chatboxID = chatboxID;
        this.datetime = datetime;
        this.media = media;
        this.seenBy = seenBy;
        this.text = text;
        this.userID = userID;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }
    public void setChatboxID(String chatboxID) {
        this.chatboxID = chatboxID;
    }
    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }
    public void setSeenBy(Boolean seenBy) {
        this.seenBy = seenBy;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setUserId(String userId) {
        this.userID = userId;
    }

    public List<String> getMedia() {
        return media;
    }
    public String getChatboxID() {
        return chatboxID;
    }
    public Boolean getSeenBy() {
        return seenBy;
    }
    public String getText() {
        return text;
    }
    public String getUserId() {
        return userID;
    }
    public Timestamp getDatetime() {
        return datetime;
    }

    @NonNull
    public String toString(){
        return "Message{" +
                "chatboxID='" + chatboxID + '\'' +
                ", datetime=" + datetime +
                ", media=" + media +
                ", seenBy='" + seenBy + '\'' +
                ", text='" + text + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}

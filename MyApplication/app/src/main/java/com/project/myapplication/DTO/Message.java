package com.project.myapplication.DTO;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class Message {
    private String chatboxID;
    private Timestamp datetime;
    private String seenBy;
    private String text;
    private String userID;

    public Message(){};
    public Message(String chatboxID,Timestamp datetime, String seenBy, String text, String userId){
        setChatboxID(chatboxID);
        setDatetime(datetime);
        setSeenBy(seenBy);
        setText(text);
        setUserId(userId);
    }

    public void setChatboxID(String chatboxID) {
        this.chatboxID = chatboxID;
    }
    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }
    public void setSeenBy(String seenBy) {
        this.seenBy = seenBy;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setUserId(String userId) {
        this.userID = userId;
    }

    public String getChatboxID() {
        return chatboxID;
    }
    public String getSeenBy() {
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
    @Override
    public String toString() {
        return "Message{" +
                ", datetime=" + datetime +
                ", seenBy='" + seenBy + '\'' +
                ", text='" + text + '\'' +
                ", userId='" + userID + '\'' +
                '}';
    }
}

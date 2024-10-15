package com.project.myapplication.DTO;

import com.google.firebase.Timestamp;

public class Message {
    private Timestamp datetime;
    private String seenBy;
    private String text;
    private String userID;

    public Message(){};
    public Message(String Id,Timestamp datetime, String seenBy, String text, String userId){
        setDatetime(datetime);
        setSeenBy(seenBy);
        setText(text);
        setUserId(userId);
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

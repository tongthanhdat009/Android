package com.project.myapplication.DTO;

import com.google.firebase.Timestamp;

public class Notification {
    private String notiID;
    private String body;
    private Boolean isRead;
    private String senderId;
    private Timestamp timestamp;
    private String title;
    private String type;
    private String userId;

    public Notification(){}
    public Notification(String body,
                        boolean isRead,
                        String senderId,
                        Timestamp timestamp,
                        String title,
                        String type,
                        String userId){
        this.body = body;
        this.isRead = isRead;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.title = title;
        this.type = type;
        this.userId = userId;
    }

    public String getNotiID() {
        return notiID;
    }

    public void setNotiID(String notiID) {
        this.notiID = notiID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

package com.project.myapplication.DTO;

import java.util.Date;

public class ShortComment {
    private String userID;
    private String content;
    private Date timestamp;

    public ShortComment() {}

    public ShortComment(String userID, String content, Date timestamp) {
        this.userID = userID;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

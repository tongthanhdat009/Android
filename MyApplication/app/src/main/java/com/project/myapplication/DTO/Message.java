package com.project.myapplication.DTO;

import java.time.ZonedDateTime;

public class Message {
    private ZonedDateTime datetime;
    private String seenBy;
    private String text;
    private String userId;

    public Message(){};
    public Message(ZonedDateTime datetime, String seenBy, String text, String userId){
        setDatetime(datetime);
        setSeenBy(seenBy);
        setText(text);
        setUserId(userId);
    }
    public void setDatetime(ZonedDateTime datetime) {
        this.datetime = datetime;
    }

    public void setSeenBy(String seenBy) {
        this.seenBy = seenBy;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSeenBy() {
        return seenBy;
    }

    public String getText() {
        return text;
    }

    public String getUserId() {
        return userId;
    }

    public ZonedDateTime getDatetime() {
        return datetime;
    }
}

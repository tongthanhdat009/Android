package com.project.myapplication.DTO;

import java.util.ArrayList;
import java.util.Date;

public class ShortVideo {
    private String id;
    private String videoUrl;
    private String title;
    private String userID;
    private ArrayList<String> likeBy;
    private Date timestamp;

    public ShortVideo() {}

    public ShortVideo(String videoUrl, String title, String userID, ArrayList<String> likeBy, Date timestamp) {
        this.videoUrl = videoUrl;
        this.title = title;
        this.userID = userID;
        this.likeBy = likeBy;
        this.timestamp = timestamp;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getLikeBy() {
        return likeBy;
    }

    public void setLikeBy(ArrayList<String> likeBy) {
        this.likeBy = likeBy;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

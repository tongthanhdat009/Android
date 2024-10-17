package com.project.myapplication.DTO;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Comment {
    private String userID;
    private String commentText;
    private ArrayList<String> likedBy;
    private int likesCount;
    private Timestamp time;
    public Comment(String userID, String commentText, ArrayList<String> likedBy, int likesCount, Timestamp time){
        setUserID(userID);
        setCommentText(commentText);
        setLikedBy(likedBy);
        setLikesCount(likesCount);
        setTime(time);
    }
    public Comment(){
        this.commentText = "";
        this.likedBy = new ArrayList<String>();
        this.likesCount = 0;
        this.time = null;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}

package com.project.myapplication.DTO;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Post {
    private String postID; //ID bài đăng
    private String userID; //người post
    private String content; //Nội dụng bài viết
    private int commentsCount; //Số comment trong bài viết
    private int likesCount; // Số like của bài biết
    private ArrayList<String> likedBy; //mảng chứa userID đã like bài viết
    private ArrayList<String> media; //mảng chứa link media
    private Timestamp time; //thời gian đăng
    private String targetAudience; //đối tượng xem bài viết
    private boolean commentMode; // chế độ comment true = mở chế độ comment, false = đóng chế độ comment

    public Post(String postID, String userID, String content, int commentsCount, int likesCount, ArrayList<String> likedBy, ArrayList<String> media, Timestamp time, String targetAudience, boolean commentMode){
        setPostID(postID);
        setUserID(userID);
        setContent(content);
        setCommentsCount(commentsCount);
        setLikesCount(likesCount);
        setLikedBy(likedBy);
        setMedia(media);
        setTime(time);
        setTargetAudience(targetAudience);
        setCommentMode(commentMode);
    }

    public Post(){
        this.commentsCount = 0;
        this.likesCount = 0;
        this.content = "";
        this.likedBy = new ArrayList<String>();
        this.media = new ArrayList<String>();
        this.time = null;
        this.userID = "";
        this.postID = "";
        this.targetAudience = "";
        this.commentMode = true;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }

    public ArrayList<String> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<String> media) {
        this.media = media;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public boolean isCommentMode() {
        return commentMode;
    }

    public void setCommentMode(boolean commentMode) {
        this.commentMode = commentMode;
    }
}

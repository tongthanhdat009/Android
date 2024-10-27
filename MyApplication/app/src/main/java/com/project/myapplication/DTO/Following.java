package com.project.myapplication.DTO;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class Following {
    public String idFollowing;
    public String userID;
    public Timestamp time;
    public Following(String idFollowing, String userID, Timestamp time) {
        this.userID = userID;
        this.time = time;
        this.idFollowing = idFollowing;
    }
    public Following() {
        this.idFollowing = "";
        userID = "";
        time = new Timestamp(new Time(System.currentTimeMillis()));
    }
    public void setIdFollowing(String idFollowing){
        this.idFollowing = idFollowing;
    }
    public String getIdFollowing(){
        return this.idFollowing;
    }
    public void setUserID(String userID){
        this.userID = userID;
    }

    public void setTime(Timestamp time){
        this.time = time;
    }

    public String getUserID(){
        return this.userID;
    }

    public Timestamp getTime(){
        return this.time;
    }
}

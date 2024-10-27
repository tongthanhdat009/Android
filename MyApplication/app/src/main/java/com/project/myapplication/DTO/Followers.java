package com.project.myapplication.DTO;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class Followers {
    public String idFollower;
    public String userID;
    public Timestamp time;
    public Followers(String idFollower, String userID, Timestamp time) {
        this.userID = userID;
        this.time = time;
        this.idFollower = idFollower;
    }
    public Followers() {
        this.idFollower = "";
        userID = "";
        time = new Timestamp(new Time(System.currentTimeMillis()));
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

    public void setIdFollower(String idFollower){
        this.idFollower = idFollower;
    }
    public String getIdFollower(){
        return this.idFollower;
    }
}

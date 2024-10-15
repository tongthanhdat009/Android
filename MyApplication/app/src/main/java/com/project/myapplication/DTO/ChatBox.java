package com.project.myapplication.DTO;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ChatBox {
    private String Id;
    private String name;
    private boolean muted=false;
    private String image_url;
    private ArrayList<String> usersID;

    public ChatBox() {}

    public ChatBox(String Id,String name, boolean muted, String image, ArrayList<String> usersID){
        setId(Id);
        setName(name);
        setMuted(muted);
        setImage_url(image);
        setUsersID(usersID);
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void setUsersID(ArrayList<String> usersID) {
        this.usersID = usersID;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getUsersID() {
        return usersID;
    }

    public String getImage_url() {
        return image_url;
    }

    public boolean getMuted(){
        return muted;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatBox{" +
                "Id='" + Id + '\'' +
                ", name='" + name + '\'' +
                ", muted=" + muted +
                ", image_url='" + image_url + '\'' +
                ", usersID=" + usersID +
                '}';
    }
}

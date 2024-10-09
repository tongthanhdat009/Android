package com.project.myapplication.DTO;

import java.util.ArrayList;

public class ChatBox {
    private String name;
    private boolean muted=false;
    private String image_url;
    private ArrayList<String> usersID;

    public ChatBox(String name, boolean muted, String image, ArrayList<String> usersID){
        setName(name);
        setMuted(muted);
        setImage_url(image);
        setUsersID(usersID);
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
}

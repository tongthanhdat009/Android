package com.project.myapplication.User;

public class User {
    private String userID;
    private String fullname;
    private String username;
    private String pass;
    private String email;
    public User() {

    }
    public User(String userID, String fullname, String username, String pass, String email) {
        this.userID = userID;
        this.fullname = fullname;
        this.username = username;
        this.pass = pass;
        this.email = email;
    }
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


}

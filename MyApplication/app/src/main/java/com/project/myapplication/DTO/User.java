package com.project.myapplication.DTO;

public class User {
    private String userID;
    private String Name;
    private String UserName;
    private String Password;
    private String Email;
    private String avatar;
    public User() {

    }
    public User(String userID, String Name, String UserName, String Password, String Email, String avatar) {
        this.userID = userID;
        this.Name = Name;
        this.UserName = UserName;
        this.Password = Password;
        this.Email = Email;
        this.avatar = avatar;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

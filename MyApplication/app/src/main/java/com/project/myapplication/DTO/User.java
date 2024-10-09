package com.project.myapplication.DTO;

public class User {
    private String email;
    private String name;
    private String username;
    private String password;

    public User(String email, String name, String username, String password) {
        setEmail(email);
        setName(name);
        setUsername(username);
        setPassword(password);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}

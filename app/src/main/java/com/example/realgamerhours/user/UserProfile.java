package com.example.realgamerhours.user;

public class UserProfile {
    public String username;
    public String userEmail;

    public UserProfile(){

    }

    public UserProfile(String username, String userEmail) {
        this.username = username;
        this.userEmail = userEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

}

package com.example.realgamerhours.forum;

import java.util.Date;

public class forumPost extends forumID{

    public String userID, username, desc;
    public Date timesStamp;

    public forumPost(){

    }

    public forumPost(String userID, String username, String desc, Date timesStamp) {
        this.userID = userID;
        this.username = username;
        this.desc = desc;
        this.timesStamp = timesStamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getTimesStamp() {
        return timesStamp;
    }

    public void setTimesStamp(Date timesStamp) {
        this.timesStamp = timesStamp;
    }
}

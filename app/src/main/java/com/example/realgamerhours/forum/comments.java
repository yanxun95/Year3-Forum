package com.example.realgamerhours.forum;

import java.util.Date;

public class comments {

    private String comment, userID, username;
    private Date timesStamp;

    public comments(){

    }

    public comments(String comment, String userID, String username, Date timesStamp) {
        this.comment = comment;
        this.userID = userID;
        this.username = username;
        this.timesStamp = timesStamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Date getTimesStamp() {
        return timesStamp;
    }

    public void setTimesStamp(Date timesStamp) {
        this.timesStamp = timesStamp;
    }
}

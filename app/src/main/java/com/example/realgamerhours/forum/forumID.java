package com.example.realgamerhours.forum;

import androidx.annotation.NonNull;

public class forumID {

    public String forumID;

    public <T extends forumID>T withID(@NonNull final String id) {
        this.forumID = id;
        return (T) this;
    }
}

package com.example.dogmeet.entity;

import java.util.ArrayList;
import java.util.Date;

public class Message {
    private String user, message, userName, userImage, uid, mainUid;
    private long time;

    public Message(String user, String message, String userName, String userImage, String uid, String mainUid) {
        this.user = user;
        this.message = message;
        this.userName = userName;
        this.userImage = userImage;
        this.uid=uid;
        this.mainUid=mainUid;

        // Initialize to current time
        time = new Date().getTime();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Message(){

    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public long getTime() {
        return time;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMainUid() {
        return mainUid;
    }

    public void setMainUid(String mainUid) {
        this.mainUid = mainUid;
    }
}

package com.example.dogmeet.entity;

import java.util.ArrayList;
import java.util.Date;

public class Message {
    private String user, message, userName, userImage, uid;
    private long time;
    private ArrayList<Answer> answerArrayList;

    public Message(String user, String message, String userName, String userImage, String uid, ArrayList<Answer> answers) {
        this.user = user;
        this.message = message;
        this.userName = userName;
        this.userImage = userImage;
        this.uid=uid;
        this.answerArrayList =answers;

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

    public ArrayList<Answer> getAnswerArrayList() {
        return answerArrayList;
    }

    public void setAnswerArrayList(ArrayList<Answer> answerArrayList) {
        this.answerArrayList = answerArrayList;
    }
}

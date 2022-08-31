package com.example.dogmeet.entity;

public class Chat {
    private String recipient, name, url, uid, lastMessage, lastUid;
    private long time;


    public Chat(String recipient, String lastMessage,
                String name, String url, String uid, long time, String lastUid) {
        this.recipient = recipient;
        this.lastMessage = lastMessage;
        this.url=url;
        this.name=name;
        this.uid=uid;
        this.time=time;
        this.lastUid=lastUid;
    }

    public Chat(){

    }

    public String getLastUid() {
        return lastUid;
    }

    public void setLastUid(String lastUid) {
        this.lastUid = lastUid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}

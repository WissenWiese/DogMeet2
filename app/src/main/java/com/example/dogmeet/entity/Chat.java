package com.example.dogmeet.entity;

public class Chat {
    private String recipient, name, url, uid;
    private Message lastMessage;


    public Chat(String recipient, Message lastMessage,
                String name, String url, String uid) {
        this.recipient = recipient;
        this.lastMessage = lastMessage;
        this.url=url;
        this.name=name;
        this.uid=uid;
    }

    public Chat(){

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

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}

package com.example.dogmeet.entity;

import java.util.Objects;

public class User {
    private String name, email, age, info, avatarUri, uid, phone, web;

    public User() {}

    public User (String name, String email, String age, String info, String avatarUri, String uid,
    String phone, String web) {
        this.name=name;
        this.email=email;
        this.age=age;
        this.info=info;
        this.avatarUri = avatarUri;
        this.uid=uid;
        this.phone=phone;
        this.web=web;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equals(user.name) && email.equals(user.email) && age.equals(user.age)
                && info.equals(user.info) && avatarUri.equals(user.avatarUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, age, info, avatarUri);
    }

}

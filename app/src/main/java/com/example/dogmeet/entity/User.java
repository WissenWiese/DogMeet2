package com.example.dogmeet.entity;

import java.util.Objects;

public class User {
    private String name, email, age, info, avatar_uri;

    public User() {}

    public User (String name, String email, String age, String info, String avatar_uri) {
        this.name=name;
        this.email=email;
        this.age=age;
        this.info=info;
        this.avatar_uri=avatar_uri;

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

    public String getAvatar_uri() {
        return avatar_uri;
    }

    public void setAvatar_uri(String avatar_uri) {
        this.avatar_uri = avatar_uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equals(user.name) && email.equals(user.email) && age.equals(user.age) && info.equals(user.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, age, info);
    }

}

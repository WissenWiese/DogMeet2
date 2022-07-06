package com.example.dogmeet.entity;

import java.util.Objects;

public class User {
    private String name, email, age, info, avatarUri;

    public User() {}

    public User (String name, String email, String age, String info, String avatarUri) {
        this.name=name;
        this.email=email;
        this.age=age;
        this.info=info;
        this.avatarUri = avatarUri;

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

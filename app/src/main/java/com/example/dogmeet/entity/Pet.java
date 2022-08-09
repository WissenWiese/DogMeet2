package com.example.dogmeet.entity;

public class Pet {
    private String name, avatar_pet;
    private Boolean edit;

    public Pet() {}

    public Pet (String name, String breed, String age, String gender, String avatar_pet) {
        this.name=name;
        this.avatar_pet=avatar_pet;

    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar_pet(){
        return avatar_pet;
    }

    public void setAvatar_pet(String avatar_pet) {
        this.avatar_pet = avatar_pet;
    }
}

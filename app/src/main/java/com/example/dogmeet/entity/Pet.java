package com.example.dogmeet.entity;

public class Pet {
    private String name, avatar_pet, petUid;

    public Pet() {}

    public Pet (String name, String avatar_pet, String petUid) {
        this.name=name;
        this.avatar_pet=avatar_pet;
        this.petUid=petUid;

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

    public String getPetUid() {
        return petUid;
    }

    public void setPetUid(String petUid) {
        this.petUid = petUid;
    }
}

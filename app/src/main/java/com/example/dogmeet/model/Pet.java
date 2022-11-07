package com.example.dogmeet.model;

public class Pet {
    private String name, avatar_pet, petUid, gender, breed, size;

    public Pet() {}

    public Pet (String name, String avatar_pet, String petUid, String gender, String breed, String size) {
        this.name=name;
        this.avatar_pet=avatar_pet;
        this.petUid=petUid;
        this.gender=gender;
        this.breed=breed;
        this.size=size;

    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}

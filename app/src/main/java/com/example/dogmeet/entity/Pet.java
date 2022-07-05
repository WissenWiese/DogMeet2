package com.example.dogmeet.entity;

public class Pet {
    private String name, age, breed, gender, avatar_pet;

    public Pet() {}

    public Pet (String name, String breed, String age, String gender, String avatar_pet) {
        this.name=name;
        this.breed=breed;
        this.age=age;
        this.gender=gender;
        this.avatar_pet=avatar_pet;

    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed(){
        return breed;
    }

    public void setBreed(String breed) {
        this.breed =breed;
    }

    public String getAge(){
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender(){
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar_pet(){
        return avatar_pet;
    }

    public void setAvatar_pet(String avatar_pet) {
        this.avatar_pet = avatar_pet;
    }
}

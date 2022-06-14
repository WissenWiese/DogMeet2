package com.example.dogmeet.entity;

public class Pet {
    private String name, age, breed, gender;

    public Pet() {}

    public Pet (String name, String breed, String age, String gender) {
        this.name=name;
        this.breed=breed;
        this.age=age;
        this.gender=gender;

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
}

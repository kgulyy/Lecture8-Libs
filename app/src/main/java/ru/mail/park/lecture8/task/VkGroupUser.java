package ru.mail.park.lecture8.task;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class VkGroupUser {
    private String fullName;
    private int age;
    private double weight;
    private Gender gender;
    private boolean married;
    @SerializedName("protected")
    private int secret;

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public Gender getGender() {
        return gender;
    }

    public boolean getMarried() {
        return married;
    }

    public int getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return "VkGroupUser{" +
                "fullName='" + fullName + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", gender=" + gender +
                ", married=" + married +
                ", secret=" + secret +
                '}';
    }
}

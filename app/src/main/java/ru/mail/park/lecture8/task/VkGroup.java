package ru.mail.park.lecture8.task;

import java.util.ArrayList;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class VkGroup {
    private int id;
    private String name;
    private int size;
    private ArrayList<VkGroupUser> users;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<VkGroupUser> getUsers() {
        return users;
    }
}

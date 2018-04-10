package ru.mail.park.lecture8;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class VkUser {
    private String firstName = "";
    private String lastName = "";
    private String screenName = "";
    private String avatar = "";
    private Sex sex = Sex.UNKNOWN;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getAvatar() {
        return avatar;
    }

    public Sex getSex() {
        return sex;
    }
}

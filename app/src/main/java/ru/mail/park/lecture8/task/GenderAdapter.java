package ru.mail.park.lecture8.task;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GenderAdapter implements JsonDeserializer<Gender> {
    @Override
    public Gender deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String value = json.getAsString();
        switch (value) {
            case "NOT_SURE":
                return Gender.NOT_SURE;
            case "FEMALE":
                return Gender.FEMALE;
            case "MALE":
                return Gender.MALE;
        }
        return Gender.NOT_SURE;
    }
}


package ru.mail.park.lecture8;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

class SexAdapter implements JsonDeserializer<Sex> {

    @Override
    public Sex deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            final JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                int value = primitive.getAsNumber().intValue();
                switch (value) {
                    case 0:
                        return Sex.UNKNOWN;
                    case 1:
                        return Sex.FEMALE;
                    case 2:
                        return Sex.MALE;
                }
            }
        }
        return Sex.UNKNOWN;
    }
}

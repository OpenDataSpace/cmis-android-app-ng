package org.opendataspace.android.app;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.lang.reflect.Type;

public class ClassFileSerializer implements JsonSerializer<File>, JsonDeserializer<File> {

    @Override
    public File deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
            JsonParseException {
        return new File(json.getAsJsonPrimitive().getAsString());
    }

    @Override
    public JsonElement serialize(File src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getAbsolutePath());
    }
}

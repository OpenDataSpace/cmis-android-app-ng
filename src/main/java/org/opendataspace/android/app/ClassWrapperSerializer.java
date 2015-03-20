package org.opendataspace.android.app;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

class ClassWrapperSerializer implements JsonSerializer<ClassWrapper>, JsonDeserializer<ClassWrapper> {

    @Override
    public ClassWrapper deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
            JsonParseException {
        if (json.isJsonNull()) {
            return new ClassWrapper(null);
        }

        JsonObject jso = json.getAsJsonObject();
        Class<?> cls = context.deserialize(jso.get("c"), Class.class);
        Object val = context.deserialize(jso.get("o"), cls);

        return new ClassWrapper(val, cls);
    }

    @Override
    public JsonElement serialize(ClassWrapper src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.isEmpty()) {
            return JsonNull.INSTANCE;
        }

        JsonObject jso = new JsonObject();
        jso.add("c", context.serialize(src.getClassInfo(), Class.class));
        jso.add("o", context.serialize(src.getClass(), src.getClassInfo()));
        return jso;
    }
}

package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Locale;

public class EnumTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public TypeAdapter create(Gson gson, Class type) {
        if (type == null || !type.isEnum()) {
            return null;
        }
        return new TypeAdapter() {
            public void write(JsonWriter out, Object value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(((Enum) value).name().toLowerCase(Locale.ROOT));
                }
            }

            public Object read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                } else {
                    return Enum.valueOf(type, in.nextString().toUpperCase(Locale.ROOT));
                }
            }
        };
    }
}

package net.minecraft.util.datafix;

public interface TypeReference {
    String typeName();

    default String in(String schema) {
        return schema;
    }
}

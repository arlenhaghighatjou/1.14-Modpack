package com.mojang.datafixers;

import com.mojang.datafixers.schemas.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class DataFixerBuilder {
    private final int dataVersion;
    private final List<Schema> schemas = new ArrayList<>();

    public DataFixerBuilder(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public Schema addSchema(int version, BiFunction<Integer, Schema, Schema> factory) {
        Schema parent = this.schemas.isEmpty() ? null : this.schemas.get(this.schemas.size() - 1);
        Schema schema = factory.apply(version, parent);
        this.schemas.add(schema);
        return schema;
    }

    public void addFixer(Object fixer) {
    }

    public DataFixer buildUnoptimized() {
        DataFixer fixer = new DataFixer(this.dataVersion);
        for (Schema schema : this.schemas) {
            fixer.addSchema(schema);
        }
        return fixer;
    }
}

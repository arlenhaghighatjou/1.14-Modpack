package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DataFixesManager {
    private static final DataFixer DATA_FIXER = createFixer();

    public static DataFixer createFixer() {
        DataFixerBuilder builder = new DataFixerBuilder(SharedConstants.getVersion().getWorldVersion());
        addFixers(builder);
        return builder.buildUnoptimized();
    }

    public static DataFixer getDataFixer() {
        return DATA_FIXER;
    }

    private static void addFixers(DataFixerBuilder builder) {
        builder.addSchema(SharedConstants.getVersion().getWorldVersion(), (version, parent) -> {
            Schema schema = new Schema(version, parent);
            schema.registerType(true, TypeReferences.ENTITY_TYPE, DataFixesManager::anyResourceLocation);
            schema.registerType(false, TypeReferences.BLOCK_NAME, DataFixesManager::anyResourceLocation);
            return schema;
        });
    }

    private static Map<String, ?> anyResourceLocation() {
        return new AbstractMap<String, String>() {
            public Set<Map.Entry<String, String>> entrySet() {
                return Collections.emptySet();
            }

            public boolean containsKey(Object key) {
                return key instanceof String && ResourceLocation.tryCreate((String) key) != null;
            }
        };
    }
}

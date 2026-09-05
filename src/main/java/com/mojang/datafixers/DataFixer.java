package com.mojang.datafixers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class DataFixer implements IDataFixer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<IFixType, List<IDataWalker>> walkerMap = Maps.<IFixType, List<IDataWalker>>newHashMap();
    private final Map<IFixType, List<IFixableData>> fixMap = Maps.<IFixType, List<IFixableData>>newHashMap();
    private final Map<Integer, com.mojang.datafixers.schemas.Schema> schemas = Maps.newHashMap();
    private final int version;
    private int latestSchemaVersion = -1;

    public DataFixer(int versionIn) {
        this.version = versionIn;
    }

    public CompoundNBT process(IFixType type, CompoundNBT compound) {
        int i = compound.hasUniqueId("DataVersion") ? compound.getInt("DataVersion") : -1;
        return i >= 1343 ? compound : this.process(type, compound, i);
    }

    public CompoundNBT process(IFixType type, CompoundNBT compound, int versionIn) {
        if (versionIn < this.version) {
            compound = this.processFixes(type, compound, versionIn);
            compound = this.processWalkers(type, compound, versionIn);
        }

        return compound;
    }

    private CompoundNBT processFixes(IFixType type, CompoundNBT compound, int versionIn) {
        List<IFixableData> list = (List) this.fixMap.get(type);

        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                IFixableData ifixabledata = list.get(i);

                if (ifixabledata.getFixVersion() > versionIn) {
                    compound = ifixabledata.fixTagCompound(compound);
                }
            }
        }

        return compound;
    }

    private CompoundNBT processWalkers(IFixType type, CompoundNBT compound, int versionIn) {
        List<IDataWalker> list = (List) this.walkerMap.get(type);

        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                compound = ((IDataWalker) list.get(i)).process(this, compound, versionIn);
            }
        }

        return compound;
    }

    public void addSchema(com.mojang.datafixers.schemas.Schema schema) {
        this.schemas.put(schema.getVersionKey(), schema);
        if (schema.getVersionKey() > this.latestSchemaVersion) {
            this.latestSchemaVersion = schema.getVersionKey();
        }
    }

    public com.mojang.datafixers.schemas.Schema getSchema(int version) {
        com.mojang.datafixers.schemas.Schema schema = this.schemas.get(version);
        if (schema == null) {
            return this.schemas.get(this.latestSchemaVersion);
        }
        return schema;
    }

    public void registerWalker(FixTypes type, IDataWalker walker) {
        this.registerWalkerAdd(type, walker);
    }

    public void registerWalkerAdd(IFixType type, IDataWalker walker) {
        this.getTypeList(this.walkerMap, type).add(walker);
    }

    public void registerFix(IFixType type, IFixableData fixable) {
        List<IFixableData> list = this.<IFixableData>getTypeList(this.fixMap, type);
        int i = fixable.getFixVersion();

        if (i > this.version) {
            LOGGER.warn("Ignored fix registered for version: {} as the DataVersion of the game is: {}",
                    Integer.valueOf(i), Integer.valueOf(this.version));
        } else {
            if (!list.isEmpty() && ((IFixableData) list.get(list.size() - 1)).getFixVersion() > i) {
                for (int j = 0; j < list.size(); ++j) {
                    if (((IFixableData) list.get(j)).getFixVersion() > i) {
                        list.add(j, fixable);
                        break;
                    }
                }
            } else {
                list.add(fixable);
            }
        }
    }

    private <V> List<V> getTypeList(Map<IFixType, List<V>> map, IFixType type) {
        List<V> list = (List) map.get(type);

        if (list == null) {
            list = Lists.<V>newArrayList();
            map.put(type, list);
        }

        return list;
    }
}

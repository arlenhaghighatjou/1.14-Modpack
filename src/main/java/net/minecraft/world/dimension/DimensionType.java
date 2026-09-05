package net.minecraft.world.dimension;

import com.mojang.datafixers.types.DynamicOps;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.util.Dynamic;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class DimensionType implements IDynamicSerializable {
    public static final DimensionType OVERWORLD = register("overworld", new DimensionType(1, "", "", OverworldDimension::new, true));
    public static final DimensionType THE_NETHER = register("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new, false));
    public static final DimensionType THE_END = register("the_end", new DimensionType(2, "_end", "DIM1", EndDimension::new, false));
    private final int id;
    private final String suffix;
    private final String directory;
    private final BiFunction<World, DimensionType, ? extends Dimension> factory;
    private final boolean field_218273_h;

    private static DimensionType register(String key, DimensionType type) {
        return Registry.register(Registry.DIMENSION_TYPE, type.id, key, type);
    }

    protected DimensionType(int idIn, String suffixIn, String directoryIn, BiFunction<World, DimensionType, ? extends Dimension> p_i49935_4_, boolean p_i49935_5_) {
        this.id = idIn;
        this.suffix = suffixIn;
        this.directory = directoryIn;
        this.factory = p_i49935_4_;
        this.field_218273_h = p_i49935_5_;
    }

    public static DimensionType func_218271_a(Dynamic<?> p_218271_0_) {
        return Registry.DIMENSION_TYPE.getOrDefault(new ResourceLocation(p_218271_0_.asString("")));
    }

    public static Iterable<DimensionType> getAll() {
        return Registry.DIMENSION_TYPE;
    }

    public int getId() {
        return this.id + -1;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public VFile2 getDirectory(VFile2 p_212679_1_) {
        return this.directory.isEmpty() ? p_212679_1_ : new VFile2(p_212679_1_, this.directory);
    }

    public Dimension create(World worldIn) {
        return this.factory.apply(worldIn, this);
    }

    public String toString() {
        return getKey(this).toString();
    }

    public static DimensionType getById(int id) {
        return Registry.DIMENSION_TYPE.getByValue(id - -1);
    }

    public static DimensionType byName(ResourceLocation nameIn) {
        return Registry.DIMENSION_TYPE.getOrDefault(nameIn);
    }

    public static ResourceLocation getKey(DimensionType dim) {
        return Registry.DIMENSION_TYPE.getKey(dim);
    }

    public boolean func_218272_d() {
        return this.field_218273_h;
    }

    public <T> T serialize(DynamicOps<T> p_218175_1_) {
        return p_218175_1_.createString(Registry.DIMENSION_TYPE.getKey(this).toString());
    }

}

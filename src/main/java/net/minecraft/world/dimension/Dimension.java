package net.minecraft.world.dimension;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Dimension {
    public static final float[] MOON_PHASE_FACTORS = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    protected final World world;
    private final DimensionType type;
    protected boolean doesWaterVaporize;
    protected boolean nether;
    protected final float[] lightBrightnessTable = new float[16];
    private final float[] colorsSunriseSunset = new float[4];

    public Dimension(World worldIn, DimensionType typeIn) {
        this.world = worldIn;
        this.type = typeIn;
        this.generateLightBrightnessTable();
    }

    protected void generateLightBrightnessTable() {
        float f = 0.0F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 1.0F + 0.0F;
        }

    }

    public int getMoonPhase(long worldTime) {
        return (int) (worldTime / 24000L % 8L + 8L) % 8;
    }

    @OnlyIn(Dist.CLIENT)
    public float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
        float f = 0.4F;
        float f1 = MathHelper.cos(celestialAngle * ((float) Math.PI * 2F)) - 0.0F;
        float f2 = -0.0F;
        if (f1 >= -0.4F && f1 <= 0.4F) {
            float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
            float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * (float) Math.PI)) * 0.99F;
            f4 = f4 * f4;
            this.colorsSunriseSunset[0] = f3 * 0.3F + 0.7F;
            this.colorsSunriseSunset[1] = f3 * f3 * 0.7F + 0.2F;
            this.colorsSunriseSunset[2] = f3 * f3 * 0.0F + 0.2F;
            this.colorsSunriseSunset[3] = f4;
            return this.colorsSunriseSunset;
        } else {
            return null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getCloudHeight() {
        return 128.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSkyColored() {
        return true;
    }

    public BlockPos getSpawnCoordinate() {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public double getVoidFogYFactor() {
        return this.world.getWorldInfo().getGenerator() == WorldType.FLAT ? 1.0D : 0.03125D;
    }

    public boolean doesWaterVaporize() {
        return this.doesWaterVaporize;
    }

    public boolean hasSkyLight() {
        return this.type.func_218272_d();
    }

    public boolean isNether() {
        return this.nether;
    }

    public float[] getLightBrightnessTable() {
        return this.lightBrightnessTable;
    }

    public WorldBorder createWorldBorder() {
        return new WorldBorder();
    }

    public void onWorldSave() {
    }

    public void tick() {
    }

    public abstract ChunkGenerator<?> createChunkGenerator();

    public abstract BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid);

    public abstract BlockPos findSpawn(int posX, int posZ, boolean checkValid);

    public abstract float calculateCelestialAngle(long worldTime, float partialTicks);

    public abstract boolean isSurfaceWorld();

    @OnlyIn(Dist.CLIENT)
    public abstract Vec3d getFogColor(float celestialAngle, float partialTicks);

    public abstract boolean canRespawnHere();

    @OnlyIn(Dist.CLIENT)
    public abstract boolean doesXZShowFog(int x, int z);

    public abstract DimensionType getType();
}

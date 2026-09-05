package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FogRenderer {
    private final net.lax1dude.eaglercraft.internal.buffer.FloatBuffer blackBuffer = GLAllocation.createDirectFloatBuffer(16);
    private final net.lax1dude.eaglercraft.internal.buffer.FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private float red;
    private float green;
    private float blue;
    private float lastRed = -1.0F;
    private float lastGreen = -1.0F;
    private float lastBlue = -1.0F;
    private int lastWaterFogColor = -1;
    private int waterFogColor = -1;
    private long waterFogUpdateTime = -1L;
    private final GameRenderer entityRenderer;
    private final Minecraft mc;
    private final BlockPos.MutableBlockPos biomePos = new BlockPos.MutableBlockPos();

    public FogRenderer(GameRenderer entityRendererIn) {
        this.entityRenderer = entityRendererIn;
        this.mc = entityRendererIn.getMinecraft();
        this.blackBuffer.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip();
    }

    public void updateFogColor(ActiveRenderInfo p_217619_1_, float p_217619_2_) {
        World world = this.mc.world;
        IFluidState ifluidstate = p_217619_1_.getFluidState();
        if (ifluidstate.isTagged(FluidTags.WATER)) {
            this.func_217621_a(p_217619_1_, world);
        } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
            this.red = 0.6F;
            this.green = 0.1F;
            this.blue = 0.0F;
            this.waterFogUpdateTime = -1L;
        } else {
            this.func_217620_a(p_217619_1_, world, p_217619_2_);
            this.waterFogUpdateTime = -1L;
        }

        double d0 = p_217619_1_.getProjectedView().y * world.dimension.getVoidFogYFactor();
        if (p_217619_1_.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity) p_217619_1_.getRenderViewEntity()).isPotionActive(Effects.BLINDNESS)) {
            int i = ((LivingEntity) p_217619_1_.getRenderViewEntity()).getActivePotionEffect(Effects.BLINDNESS).getDuration();
            if (i < 20) {
                d0 *= (double) (1.0F - (float) i / 20.0F);
            } else {
                d0 = 0.0D;
            }
        }

        if (d0 < 1.0D) {
            if (d0 < 0.0D) {
                d0 = 0.0D;
            }

            d0 = d0 * d0;
            this.red = (float) ((double) this.red * d0);
            this.green = (float) ((double) this.green * d0);
            this.blue = (float) ((double) this.blue * d0);
        }

        float bossColorModifier = this.entityRenderer.getBossColorModifier(p_217619_2_);
        if (bossColorModifier > 0.0F) {
            float f = bossColorModifier;
            this.red = this.red * (1.0F - f) + this.red * 0.7F * f;
            this.green = this.green * (1.0F - f) + this.green * 0.6F * f;
            this.blue = this.blue * (1.0F - f) + this.blue * 0.6F * f;
        }

        if (ifluidstate.isTagged(FluidTags.WATER)) {
            float f1 = 0.0F;
            if (p_217619_1_.getRenderViewEntity() instanceof ClientPlayerEntity) {
                ClientPlayerEntity clientplayerentity = (ClientPlayerEntity) p_217619_1_.getRenderViewEntity();
                f1 = clientplayerentity.getWaterBrightness();
            }

            float f3 = 1.0F / this.red;
            if (f3 > 1.0F / this.green) {
                f3 = 1.0F / this.green;
            }

            if (f3 > 1.0F / this.blue) {
                f3 = 1.0F / this.blue;
            }

            this.red = this.red * (1.0F - f1) + this.red * f3 * f1;
            this.green = this.green * (1.0F - f1) + this.green * f3 * f1;
            this.blue = this.blue * (1.0F - f1) + this.blue * f3 * f1;
        } else if (p_217619_1_.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity) p_217619_1_.getRenderViewEntity()).isPotionActive(Effects.NIGHT_VISION)) {
            float f2 = this.entityRenderer.getNightVisionBrightness((LivingEntity) p_217619_1_.getRenderViewEntity(), p_217619_2_);
            float f4 = 1.0F / this.red;
            if (f4 > 1.0F / this.green) {
                f4 = 1.0F / this.green;
            }

            if (f4 > 1.0F / this.blue) {
                f4 = 1.0F / this.blue;
            }

            this.red = this.red * (1.0F - f2) + this.red * f4 * f2;
            this.green = this.green * (1.0F - f2) + this.green * f4 * f2;
            this.blue = this.blue * (1.0F - f2) + this.blue * f4 * f2;
        }

        GlStateManager.clearColor(this.red, this.green, this.blue, 0.0F);
    }

    private void func_217620_a(ActiveRenderInfo p_217620_1_, World p_217620_2_, float p_217620_3_) {
        float f = 0.25F + 0.75F * (float) this.mc.gameSettings.renderDistanceChunks / 32.0F;
        f = 1.0F - (float) Math.pow((double) f, 0.25D);
        Vec3d vec3d = p_217620_2_.getSkyColor(p_217620_1_.getBlockPos(), p_217620_3_);
        float f1 = (float) vec3d.x;
        float f2 = (float) vec3d.y;
        float f3 = (float) vec3d.z;
        Vec3d vec3d1 = p_217620_2_.getFogColor(p_217620_3_);
        this.red = (float) vec3d1.x;
        this.green = (float) vec3d1.y;
        this.blue = (float) vec3d1.z;
        if (this.mc.gameSettings.renderDistanceChunks >= 4) {
            double d0 = MathHelper.sin(p_217620_2_.getCelestialAngleRadians(p_217620_3_)) > 0.0F ? -1.0D : 1.0D;
            float f5 = (float)(p_217620_1_.getLookDirection().x * d0);
            if (f5 < 0.0F) {
                f5 = 0.0F;
            }

            if (f5 > 0.0F) {
                float[] afloat = p_217620_2_.dimension.calcSunriseSunsetColors(p_217620_2_.getCelestialAngle(p_217620_3_), p_217620_3_);
                if (afloat != null) {
                    f5 = f5 * afloat[3];
                    this.red = this.red * (1.0F - f5) + afloat[0] * f5;
                    this.green = this.green * (1.0F - f5) + afloat[1] * f5;
                    this.blue = this.blue * (1.0F - f5) + afloat[2] * f5;
                }
            }
        }

        this.red += (f1 - this.red) * f;
        this.green += (f2 - this.green) * f;
        this.blue += (f3 - this.blue) * f;
        float f6 = p_217620_2_.getRainStrength(p_217620_3_);
        if (f6 > 0.0F) {
            float f4 = 1.0F - f6 * 0.5F;
            float f8 = 1.0F - f6 * 0.4F;
            this.red *= f4;
            this.green *= f4;
            this.blue *= f8;
        }

        float f7 = p_217620_2_.getThunderStrength(p_217620_3_);
        if (f7 > 0.0F) {
            float f9 = 1.0F - f7 * 0.5F;
            this.red *= f9;
            this.green *= f9;
            this.blue *= f9;
        }

    }

    private void func_217621_a(ActiveRenderInfo p_217621_1_, IWorldReader p_217621_2_) {
        long i = Util.milliTime();
        int j = p_217621_2_.getBiome(p_217621_1_.getBlockPos()).getWaterFogColor();
        if (this.waterFogUpdateTime < 0L) {
            this.lastWaterFogColor = j;
            this.waterFogColor = j;
            this.waterFogUpdateTime = i;
        }

        int k = this.lastWaterFogColor >> 16 & 255;
        int l = this.lastWaterFogColor >> 8 & 255;
        int i1 = this.lastWaterFogColor & 255;
        int j1 = this.waterFogColor >> 16 & 255;
        int k1 = this.waterFogColor >> 8 & 255;
        int l1 = this.waterFogColor & 255;
        float f = MathHelper.clamp((float) (i - this.waterFogUpdateTime) / 5000.0F, 0.0F, 1.0F);
        float f1 = MathHelper.lerp(f, (float) j1, (float) k);
        float f2 = MathHelper.lerp(f, (float) k1, (float) l);
        float f3 = MathHelper.lerp(f, (float) l1, (float) i1);
        this.red = f1 / 255.0F;
        this.green = f2 / 255.0F;
        this.blue = f3 / 255.0F;
        if (this.lastWaterFogColor != j) {
            this.lastWaterFogColor = j;
            this.waterFogColor = MathHelper.floor(f1) << 16 | MathHelper.floor(f2) << 8 | MathHelper.floor(f3);
            this.waterFogUpdateTime = i;
        }

    }

    public void setupFog(ActiveRenderInfo p_217618_1_, int p_217618_2_) {
        this.applyFog(false);
        GlStateManager.normal3f(0.0F, -1.0F, 0.0F);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (!this.mc.gameSettings.fog) {
            GlStateManager.enableColorMaterial();
            GlStateManager.disableFog();
            GlStateManager.colorMaterial(1028, 4608);
            return;
        }
        IFluidState ifluidstate = p_217618_1_.getFluidState();
        if (p_217618_1_.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity) p_217618_1_.getRenderViewEntity()).isPotionActive(Effects.BLINDNESS)) {
            float f2 = 5.0F;
            int i = ((LivingEntity) p_217618_1_.getRenderViewEntity()).getActivePotionEffect(Effects.BLINDNESS).getDuration();
            if (i < 20) {
                f2 = MathHelper.lerp(1.0F - (float) i / 20.0F, 5.0F, this.entityRenderer.getFarPlaneDistance());
            }

            GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
            if (p_217618_2_ == -1) {
                GlStateManager.fogStart(0.0F);
                GlStateManager.fogEnd(f2 * 0.8F);
            } else {
                GlStateManager.fogStart(f2 * 0.25F);
                GlStateManager.fogEnd(f2);
            }

            GLX.setupNvFogDistance();
        } else if (ifluidstate.isTagged(FluidTags.WATER)) {
            GlStateManager.fogMode(GlStateManager.FogMode.EXP2);
            if (p_217618_1_.getRenderViewEntity() instanceof LivingEntity) {
                if (p_217618_1_.getRenderViewEntity() instanceof ClientPlayerEntity) {
                    ClientPlayerEntity clientplayerentity = (ClientPlayerEntity) p_217618_1_.getRenderViewEntity();
                    float f = 0.05F - clientplayerentity.getWaterBrightness() * clientplayerentity.getWaterBrightness() * 0.03F;
                    Biome biome = clientplayerentity.world.getBiome(this.biomePos.setPos(clientplayerentity));
                    if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
                        f += 0.005F;
                    }

                    GlStateManager.fogDensity(f);
                } else {
                    GlStateManager.fogDensity(0.05F);
                }
            } else {
                GlStateManager.fogDensity(0.1F);
            }
        } else if (ifluidstate.isTagged(FluidTags.LAVA)) {
            GlStateManager.fogMode(GlStateManager.FogMode.EXP);
            GlStateManager.fogDensity(2.0F);
        } else {
            float f1 = this.entityRenderer.getFarPlaneDistance();
            GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
            if (p_217618_2_ == -1) {
                GlStateManager.fogStart(0.0F);
                GlStateManager.fogEnd(f1);
            } else {
                GlStateManager.fogStart(f1 * 0.75F);
                GlStateManager.fogEnd(f1);
            }

            GLX.setupNvFogDistance();
            if (this.mc.world.dimension.doesXZShowFog(MathHelper.floor(p_217618_1_.getProjectedView().x), MathHelper.floor(p_217618_1_.getProjectedView().z)) || this.mc.ingameGUI.getBossOverlay().shouldCreateFog()) {
                GlStateManager.fogStart(f1 * 0.05F);
                GlStateManager.fogEnd(Math.min(f1, 192.0F) * 0.5F);
            }
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }

    public void applyFog(boolean blackIn) {
        if (blackIn) {
            GlStateManager.fog(2918, this.blackBuffer);
        } else {
            GlStateManager.fog(2918, this.getFogBuffer());
        }

    }

    private FloatBuffer getFogBuffer() {
        if (this.lastRed != this.red || this.lastGreen != this.green || this.lastBlue != this.blue) {
            this.buffer.clear();
            this.buffer.put(this.red).put(this.green).put(this.blue).put(1.0F);
            this.buffer.flip();
            this.lastRed = this.red;
            this.lastGreen = this.green;
            this.lastBlue = this.blue;
        }

        return this.buffer;
    }
}

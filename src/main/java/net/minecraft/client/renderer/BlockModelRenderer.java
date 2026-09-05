package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import net.lax1dude.eaglercraft.Random;
import me.jellysquid.mods.sodium.client.util.rand.XoRoShiRoRandom;
import me.jellysquid.mods.sodium.client.model.light.cache.ArrayLightDataCache;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFlags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.BitSet;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BlockModelRenderer {
    private final BlockColors blockColors;
    private final XoRoShiRoRandom modelBrightnessRandom = new XoRoShiRoRandom();
    private static final ThreadLocal<BlockModelRenderer.Cache> CACHE_COMBINED_LIGHT = new ThreadLocal<BlockModelRenderer.Cache>() {
        @Override
        protected BlockModelRenderer.Cache initialValue() {
            return new BlockModelRenderer.Cache();
        }
    };
    private static final Direction[] FACINGS = Direction.values();
    private final RenderContext renderContext = new RenderContext();

    public BlockModelRenderer(BlockColors blockColorsIn) {
        this.blockColors = blockColorsIn;
    }

    public boolean renderModel(IEnviromentBlockReader p_217631_1_, IBakedModel p_217631_2_, BlockState p_217631_3_, BlockPos p_217631_4_, BufferBuilder p_217631_5_, boolean p_217631_6_, Random p_217631_7_, long p_217631_8_) {
        boolean flag = false;
        try {
            flag = Minecraft.isAmbientOcclusionEnabled() && p_217631_3_.getLightValue() == 0 && p_217631_2_.isAmbientOcclusion();
            this.renderContext.cachedTintIndex = -1;
            this.renderContext.cachedTintColor = -1;
            return flag ? this.renderModelSmooth(p_217631_1_, p_217631_2_, p_217631_3_, p_217631_4_, p_217631_5_, p_217631_6_, p_217631_7_, p_217631_8_) : this.renderModelFlat(p_217631_1_, p_217631_2_, p_217631_3_, p_217631_4_, p_217631_5_, p_217631_6_, p_217631_7_, p_217631_8_);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, p_217631_4_, p_217631_3_);
            crashreportcategory.addDetail("Using AO", flag);
            throw new ReportedException(crashreport);
        }
    }

    public boolean renderModelSmooth(IEnviromentBlockReader p_217634_1_, IBakedModel p_217634_2_, BlockState p_217634_3_, BlockPos p_217634_4_, BufferBuilder p_217634_5_, boolean p_217634_6_, Random p_217634_7_, long p_217634_8_) {
        boolean flag = false;
        RenderContext ctx = this.renderContext;
        float[] afloat = ctx.quadBounds;
        BitSet bitset = ctx.boundsFlags;
        AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = ctx.ambientOcclusionFace;
        blockmodelrenderer$ambientocclusionface.reset(p_217634_4_);

        for (Direction direction : FACINGS) {
            p_217634_7_.setSeed(p_217634_8_);
            List<BakedQuad> list = p_217634_2_.getQuads(p_217634_3_, direction, p_217634_7_);
            if (!list.isEmpty() && (!p_217634_6_ || Block.shouldSideBeRendered(p_217634_3_, p_217634_1_, p_217634_4_, direction))) {
                this.renderQuadsSmooth(p_217634_1_, p_217634_3_, p_217634_4_, p_217634_5_, list, afloat, bitset, blockmodelrenderer$ambientocclusionface);
                flag = true;
            }
        }

        p_217634_7_.setSeed(p_217634_8_);
        List<BakedQuad> list1 = p_217634_2_.getQuads(p_217634_3_, (Direction) null, p_217634_7_);
        if (!list1.isEmpty()) {
            this.renderQuadsSmooth(p_217634_1_, p_217634_3_, p_217634_4_, p_217634_5_, list1, afloat, bitset, blockmodelrenderer$ambientocclusionface);
            flag = true;
        }

        return flag;
    }

    public boolean renderModelFlat(IEnviromentBlockReader p_217635_1_, IBakedModel p_217635_2_, BlockState p_217635_3_, BlockPos p_217635_4_, BufferBuilder p_217635_5_, boolean p_217635_6_, Random p_217635_7_, long p_217635_8_) {
        boolean flag = false;
        RenderContext ctx = this.renderContext;
        BitSet bitset = ctx.boundsFlags;
        BlockPos.MutableBlockPos mutablePos = ctx.blockPos;

        for (Direction direction : FACINGS) {
            p_217635_7_.setSeed(p_217635_8_);
            List<BakedQuad> list = p_217635_2_.getQuads(p_217635_3_, direction, p_217635_7_);
            if (!list.isEmpty() && (!p_217635_6_ || Block.shouldSideBeRendered(p_217635_3_, p_217635_1_, p_217635_4_, direction))) {
                int i = p_217635_3_.getPackedLightmapCoords(p_217635_1_, mutablePos.setPos(p_217635_4_).move(direction));
                this.renderQuadsFlat(p_217635_1_, p_217635_3_, p_217635_4_, i, false, p_217635_5_, list, bitset);
                flag = true;
            }
        }

        p_217635_7_.setSeed(p_217635_8_);
        List<BakedQuad> list1 = p_217635_2_.getQuads(p_217635_3_, (Direction) null, p_217635_7_);
        if (!list1.isEmpty()) {
            this.renderQuadsFlat(p_217635_1_, p_217635_3_, p_217635_4_, -1, true, p_217635_5_, list1, bitset);
            flag = true;
        }

        return flag;
    }

    private void renderQuadsSmooth(IEnviromentBlockReader p_217630_1_, BlockState p_217630_2_, BlockPos p_217630_3_, BufferBuilder p_217630_4_, List<BakedQuad> p_217630_5_, float[] p_217630_6_, BitSet p_217630_7_, BlockModelRenderer.AmbientOcclusionFace p_217630_8_) {
        Vec3d vec3d = p_217630_2_.getOffset(p_217630_1_, p_217630_3_);
        float d0 = (float) (p_217630_3_.getX() + vec3d.x);
        float d1 = (float) (p_217630_3_.getY() + vec3d.y);
        float d2 = (float) (p_217630_3_.getZ() + vec3d.z);
        int i = 0;
        RenderContext ctx = this.renderContext;

        for (int j = p_217630_5_.size(); i < j; ++i) {
            BakedQuad bakedquad = p_217630_5_.get(i);
            bakedquad.getSprite().markActive();
            if (bakedquad.getFlags() == ModelQuadFlags.IS_ALIGNED) {
                p_217630_7_.set(0, true);
                p_217630_7_.set(1, false);
            } else {
                this.fillQuadBounds(p_217630_1_, p_217630_2_, p_217630_3_, bakedquad.getVertexData(), bakedquad.getFace(), p_217630_6_, p_217630_7_);
            }
            p_217630_8_.updateVertexBrightness(p_217630_1_, p_217630_2_, p_217630_3_, bakedquad.getFace(), p_217630_6_, p_217630_7_);
            if (bakedquad.hasTintIndex()) {
                int tintIndex = bakedquad.getTintIndex();
                int k = ctx.cachedTintColor;
                if (tintIndex != ctx.cachedTintIndex) {
                    k = this.blockColors.getColor(p_217630_2_, p_217630_1_, p_217630_3_, tintIndex);
                    ctx.cachedTintIndex = tintIndex;
                    ctx.cachedTintColor = k;
                }
                float f = (float) (k >> 16 & 255) / 255.0F;
                float f1 = (float) (k >> 8 & 255) / 255.0F;
                float f2 = (float) (k & 255) / 255.0F;
                ctx.colorMultR[0] = p_217630_8_.vertexColorMultiplier[0] * f;
                ctx.colorMultR[1] = p_217630_8_.vertexColorMultiplier[1] * f;
                ctx.colorMultR[2] = p_217630_8_.vertexColorMultiplier[2] * f;
                ctx.colorMultR[3] = p_217630_8_.vertexColorMultiplier[3] * f;
                ctx.colorMultG[0] = p_217630_8_.vertexColorMultiplier[0] * f1;
                ctx.colorMultG[1] = p_217630_8_.vertexColorMultiplier[1] * f1;
                ctx.colorMultG[2] = p_217630_8_.vertexColorMultiplier[2] * f1;
                ctx.colorMultG[3] = p_217630_8_.vertexColorMultiplier[3] * f1;
                ctx.colorMultB[0] = p_217630_8_.vertexColorMultiplier[0] * f2;
                ctx.colorMultB[1] = p_217630_8_.vertexColorMultiplier[1] * f2;
                ctx.colorMultB[2] = p_217630_8_.vertexColorMultiplier[2] * f2;
                ctx.colorMultB[3] = p_217630_8_.vertexColorMultiplier[3] * f2;
            } else {
                ctx.colorMultR[0] = ctx.colorMultG[0] = ctx.colorMultB[0] = p_217630_8_.vertexColorMultiplier[0];
                ctx.colorMultR[1] = ctx.colorMultG[1] = ctx.colorMultB[1] = p_217630_8_.vertexColorMultiplier[1];
                ctx.colorMultR[2] = ctx.colorMultG[2] = ctx.colorMultB[2] = p_217630_8_.vertexColorMultiplier[2];
                ctx.colorMultR[3] = ctx.colorMultG[3] = ctx.colorMultB[3] = p_217630_8_.vertexColorMultiplier[3];
            }

            p_217630_4_.addQuadOptimized(bakedquad.getVertexData(), d0, d1, d2, p_217630_8_.vertexBrightness, ctx.colorMultR, ctx.colorMultG, ctx.colorMultB);
        }

    }

    private void fillQuadBounds(IEnviromentBlockReader p_217633_1_, BlockState p_217633_2_, BlockPos p_217633_3_, int[] p_217633_4_, Direction p_217633_5_, float[] p_217633_6_, BitSet p_217633_7_) {
        float f = 32.0F;
        float f1 = 32.0F;
        float f2 = 32.0F;
        float f3 = -32.0F;
        float f4 = -32.0F;
        float f5 = -32.0F;

        for (int i = 0; i < 4; ++i) {
            float f6 = Float.intBitsToFloat(p_217633_4_[i * 7]);
            float f7 = Float.intBitsToFloat(p_217633_4_[i * 7 + 1]);
            float f8 = Float.intBitsToFloat(p_217633_4_[i * 7 + 2]);
            f = Math.min(f, f6);
            f1 = Math.min(f1, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.max(f3, f6);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
        }

        if (p_217633_6_ != null) {
            p_217633_6_[Direction.WEST.getIndex()] = f;
            p_217633_6_[Direction.EAST.getIndex()] = f3;
            p_217633_6_[Direction.DOWN.getIndex()] = f1;
            p_217633_6_[Direction.UP.getIndex()] = f4;
            p_217633_6_[Direction.NORTH.getIndex()] = f2;
            p_217633_6_[Direction.SOUTH.getIndex()] = f5;
            int j = FACINGS.length;
            p_217633_6_[Direction.WEST.getIndex() + j] = 1.0F - f;
            p_217633_6_[Direction.EAST.getIndex() + j] = 1.0F - f3;
            p_217633_6_[Direction.DOWN.getIndex() + j] = 1.0F - f1;
            p_217633_6_[Direction.UP.getIndex() + j] = 1.0F - f4;
            p_217633_6_[Direction.NORTH.getIndex() + j] = 1.0F - f2;
            p_217633_6_[Direction.SOUTH.getIndex() + j] = 1.0F - f5;
        }

        float f9 = 1.0E-4F;
        float f10 = 0.9999F;
        switch (p_217633_5_) {
            case DOWN:
                p_217633_7_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                p_217633_7_.set(0, f1 == f4 && (f1 < 1.0E-4F || p_217633_2_.func_224756_o(p_217633_1_, p_217633_3_)));
                break;
            case UP:
                p_217633_7_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
                p_217633_7_.set(0, f1 == f4 && (f4 > 0.9999F || p_217633_2_.func_224756_o(p_217633_1_, p_217633_3_)));
                break;
            case NORTH:
                p_217633_7_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                p_217633_7_.set(0, f2 == f5 && (f2 < 1.0E-4F || p_217633_2_.func_224756_o(p_217633_1_, p_217633_3_)));
                break;
            case SOUTH:
                p_217633_7_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
                p_217633_7_.set(0, f2 == f5 && (f5 > 0.9999F || p_217633_2_.func_224756_o(p_217633_1_, p_217633_3_)));
                break;
            case WEST:
                p_217633_7_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                p_217633_7_.set(0, f == f3 && (f < 1.0E-4F || p_217633_2_.func_224756_o(p_217633_1_, p_217633_3_)));
                break;
            case EAST:
                p_217633_7_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
                p_217633_7_.set(0, f == f3 && (f3 > 0.9999F || p_217633_2_.func_224756_o(p_217633_1_, p_217633_3_)));
        }

    }

    private void renderQuadsFlat(IEnviromentBlockReader p_217636_1_, BlockState p_217636_2_, BlockPos p_217636_3_, int p_217636_4_, boolean p_217636_5_, BufferBuilder p_217636_6_, List<BakedQuad> p_217636_7_, BitSet p_217636_8_) {
        Vec3d vec3d = p_217636_2_.getOffset(p_217636_1_, p_217636_3_);
        float d0 = (float) (p_217636_3_.getX() + vec3d.x);
        float d1 = (float) (p_217636_3_.getY() + vec3d.y);
        float d2 = (float) (p_217636_3_.getZ() + vec3d.z);
        int i = 0;
        RenderContext ctx = this.renderContext;

        for (int j = p_217636_7_.size(); i < j; ++i) {
            BakedQuad bakedquad = p_217636_7_.get(i);
            bakedquad.getSprite().markActive();
            if (p_217636_5_) {
                BlockPos blockpos;
                if (ModelQuadFlags.contains(bakedquad.getFlags(), ModelQuadFlags.IS_ALIGNED)) {
                    blockpos = ctx.blockPos.setPos(p_217636_3_).move(bakedquad.getFace());
                } else {
                    this.fillQuadBounds(p_217636_1_, p_217636_2_, p_217636_3_, bakedquad.getVertexData(), bakedquad.getFace(), (float[]) null, p_217636_8_);
                    blockpos = p_217636_8_.get(0)
                            ? ctx.blockPos.setPos(p_217636_3_).move(bakedquad.getFace()) : p_217636_3_;
                }
                p_217636_4_ = p_217636_2_.getPackedLightmapCoords(p_217636_1_, blockpos);
            }

            ctx.vertexBrightness[0] = ctx.vertexBrightness[1] = ctx.vertexBrightness[2] = ctx.vertexBrightness[3] = p_217636_4_;

            if (bakedquad.hasTintIndex()) {
                int tintIndex = bakedquad.getTintIndex();
                int k = ctx.cachedTintColor;
                if (tintIndex != ctx.cachedTintIndex) {
                    k = this.blockColors.getColor(p_217636_2_, p_217636_1_, p_217636_3_, tintIndex);
                    ctx.cachedTintIndex = tintIndex;
                    ctx.cachedTintColor = k;
                }
                float f = (float) (k >> 16 & 255) / 255.0F;
                float f1 = (float) (k >> 8 & 255) / 255.0F;
                float f2 = (float) (k & 255) / 255.0F;
                ctx.colorMultR[0] = ctx.colorMultR[1] = ctx.colorMultR[2] = ctx.colorMultR[3] = f;
                ctx.colorMultG[0] = ctx.colorMultG[1] = ctx.colorMultG[2] = ctx.colorMultG[3] = f1;
                ctx.colorMultB[0] = ctx.colorMultB[1] = ctx.colorMultB[2] = ctx.colorMultB[3] = f2;
            } else {
                ctx.colorMultR[0] = ctx.colorMultR[1] = ctx.colorMultR[2] = ctx.colorMultR[3] = 1.0F;
                ctx.colorMultG[0] = ctx.colorMultG[1] = ctx.colorMultG[2] = ctx.colorMultG[3] = 1.0F;
                ctx.colorMultB[0] = ctx.colorMultB[1] = ctx.colorMultB[2] = ctx.colorMultB[3] = 1.0F;
            }

            p_217636_6_.addQuadOptimized(bakedquad.getVertexData(), d0, d1, d2, ctx.vertexBrightness, ctx.colorMultR, ctx.colorMultG, ctx.colorMultB);
        }

    }

    public void renderModelBrightnessColor(IBakedModel bakedModel, float brightness, float red, float green, float blue) {
        this.renderModelBrightnessColor((BlockState) null, bakedModel, brightness, red, green, blue);
    }

    public void renderModelBrightnessColor(BlockState state, IBakedModel modelIn, float brightness, float red, float green, float blue) {
        XoRoShiRoRandom random = this.modelBrightnessRandom;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        for (Direction direction : FACINGS) {
            this.appendModelBrightnessColorQuads(bufferbuilder, brightness, red, green, blue,
                    modelIn.getQuads(state, direction, random.setSeedAndReturn(42L)));
        }

        this.appendModelBrightnessColorQuads(bufferbuilder, brightness, red, green, blue,
                modelIn.getQuads(state, (Direction) null, random.setSeedAndReturn(42L)));
        tessellator.draw();
    }

    public void renderModelBrightness(IBakedModel model, BlockState state, float brightness, boolean glDisabled) {
        GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
        int i = this.blockColors.getColor(state, (IEnviromentBlockReader) null, (BlockPos) null, 0);
        float f = (float) (i >> 16 & 255) / 255.0F;
        float f1 = (float) (i >> 8 & 255) / 255.0F;
        float f2 = (float) (i & 255) / 255.0F;
        if (!glDisabled) {
            GlStateManager.color4f(brightness, brightness, brightness, 1.0F);
        }

        this.renderModelBrightnessColor(state, model, brightness, f, f1, f2);
    }

    private void appendModelBrightnessColorQuads(BufferBuilder bufferbuilder, float brightness, float red, float green, float blue, List<BakedQuad> listQuads) {
        int i = 0;

        for (int j = listQuads.size(); i < j; ++i) {
            BakedQuad bakedquad = listQuads.get(i);
            bakedquad.getSprite().markActive();
            bufferbuilder.addVertexData(bakedquad.getVertexData());
            if (bakedquad.hasTintIndex()) {
                bufferbuilder.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
            } else {
                bufferbuilder.putColorRGB_F4(brightness, brightness, brightness);
            }

            Vec3i vec3i = bakedquad.getFace().getDirectionVec();
            bufferbuilder.putNormal((float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ());
        }

    }

    public static void enableCache(BlockPos origin) {
        CACHE_COMBINED_LIGHT.get().func_222895_a(origin);
    }

    public static void enableCache() {
        CACHE_COMBINED_LIGHT.get().func_222895_a();
    }

    public static void disableCache() {
        CACHE_COMBINED_LIGHT.get().func_222897_b();
    }

    class RenderContext {
        final float[] quadBounds = new float[FACINGS.length * 2];
        final BitSet boundsFlags = new BitSet(3);
        final AmbientOcclusionFace ambientOcclusionFace = new AmbientOcclusionFace();
        final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        final float[] colorMultR = new float[4];
        final float[] colorMultG = new float[4];
        final float[] colorMultB = new float[4];
        final int[] vertexBrightness = new int[4];
        int cachedTintIndex = -1;
        int cachedTintColor = -1;
    }

    @OnlyIn(Dist.CLIENT)
    class AmbientOcclusionFace {
        private final float[] vertexColorMultiplier = new float[4];
        private final int[] vertexBrightness = new int[4];
        private final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        private static class AoFaceData {
            boolean hasData;
            float f9, f10, f11, f12;
            int i2, j2, k2, l2;
        }

        private long cachedPos = -1L;
        private final AoFaceData[] cachedFaceData = new AoFaceData[12];

        public AmbientOcclusionFace() {
            for (int i = 0; i < 12; i++) {
                cachedFaceData[i] = new AoFaceData();
            }
        }

        private void reset(BlockPos pos) {
            this.cachedPos = pos.toLong();
            for (int i = 0; i < 12; ++i) {
                this.cachedFaceData[i].hasData = false;
            }
        }

        private void populateFaceData(AoFaceData data, IEnviromentBlockReader reader, BlockState state, BlockPos pos, Direction dir, BitSet bits) {
            BlockPos blockpos = bits.get(0) ? pos.offset(dir) : pos;
            BlockModelRenderer.NeighborInfo info = BlockModelRenderer.NeighborInfo.getNeighbourInfo(dir);
            BlockPos.MutableBlockPos mpos = this.mutableBlockPos;
            BlockModelRenderer.Cache cache = BlockModelRenderer.CACHE_COMBINED_LIGHT.get();
            mpos.setPos(blockpos).move(info.corners[0]);
            BlockState blockstate = reader.getBlockState(mpos);
            int i = cache.func_222893_a(blockstate, reader, mpos);
            float f = cache.func_222896_b(blockstate, reader, mpos);
            mpos.setPos(blockpos).move(info.corners[1]);
            BlockState blockstate1 = reader.getBlockState(mpos);
            int j = cache.func_222893_a(blockstate1, reader, mpos);
            float f1 = cache.func_222896_b(blockstate1, reader, mpos);
            mpos.setPos(blockpos).move(info.corners[2]);
            BlockState blockstate2 = reader.getBlockState(mpos);
            int k = cache.func_222893_a(blockstate2, reader, mpos);
            float f2 = cache.func_222896_b(blockstate2, reader, mpos);
            mpos.setPos(blockpos).move(info.corners[3]);
            BlockState blockstate3 = reader.getBlockState(mpos);
            int l = cache.func_222893_a(blockstate3, reader, mpos);
            float f3 = cache.func_222896_b(blockstate3, reader, mpos);
            mpos.setPos(blockpos).move(info.corners[0]).move(dir);
            boolean flag = reader.getBlockState(mpos).getOpacity(reader, mpos) == 0;
            mpos.setPos(blockpos).move(info.corners[1]).move(dir);
            boolean flag1 = reader.getBlockState(mpos).getOpacity(reader, mpos) == 0;
            mpos.setPos(blockpos).move(info.corners[2]).move(dir);
            boolean flag2 = reader.getBlockState(mpos).getOpacity(reader, mpos) == 0;
            mpos.setPos(blockpos).move(info.corners[3]).move(dir);
            boolean flag3 = reader.getBlockState(mpos).getOpacity(reader, mpos) == 0;
            float f4;
            int i1;
            if (!flag2 && !flag) {
                f4 = f;
                i1 = i;
            } else {
                mpos.setPos(blockpos).move(info.corners[0]).move(info.corners[2]);
                BlockState blockstate4 = reader.getBlockState(mpos);
                f4 = cache.func_222896_b(blockstate4, reader, mpos);
                i1 = cache.func_222893_a(blockstate4, reader, mpos);
            }

            float f5;
            int j1;
            if (!flag3 && !flag) {
                f5 = f;
                j1 = i;
            } else {
                mpos.setPos(blockpos).move(info.corners[0]).move(info.corners[3]);
                BlockState blockstate6 = reader.getBlockState(mpos);
                f5 = cache.func_222896_b(blockstate6, reader, mpos);
                j1 = cache.func_222893_a(blockstate6, reader, mpos);
            }

            float f6;
            int k1;
            if (!flag2 && !flag1) {
                f6 = f;
                k1 = i;
            } else {
                mpos.setPos(blockpos).move(info.corners[1]).move(info.corners[2]);
                BlockState blockstate7 = reader.getBlockState(mpos);
                f6 = cache.func_222896_b(blockstate7, reader, mpos);
                k1 = cache.func_222893_a(blockstate7, reader, mpos);
            }

            float f7;
            int l1;
            if (!flag3 && !flag1) {
                f7 = f;
                l1 = i;
            } else {
                mpos.setPos(blockpos).move(info.corners[1]).move(info.corners[3]);
                BlockState blockstate8 = reader.getBlockState(mpos);
                f7 = cache.func_222896_b(blockstate8, reader, mpos);
                l1 = cache.func_222893_a(blockstate8, reader, mpos);
            }

            int i3 = cache.func_222893_a(state, reader, pos);
            mpos.setPos(pos).move(dir);
            BlockState blockstate5 = reader.getBlockState(mpos);
            if (bits.get(0) || !blockstate5.isOpaqueCube(reader, mpos)) {
                i3 = cache.func_222893_a(blockstate5, reader, mpos);
            }

            float f8 = bits.get(0) ? cache.func_222896_b(reader.getBlockState(blockpos), reader, blockpos) : cache.func_222896_b(reader.getBlockState(pos), reader, pos);

            data.f9 = (f3 + f + f5 + f8) * 0.25F;
            data.f10 = (f2 + f + f4 + f8) * 0.25F;
            data.f11 = (f2 + f1 + f6 + f8) * 0.25F;
            data.f12 = (f3 + f1 + f7 + f8) * 0.25F;
            data.i2 = this.getAoBrightness(l, i, j1, i3);
            data.j2 = this.getAoBrightness(k, i, i1, i3);
            data.k2 = this.getAoBrightness(k, j, k1, i3);
            data.l2 = this.getAoBrightness(l, j, l1, i3);
        }

        public void updateVertexBrightness(IEnviromentBlockReader reader, BlockState state, BlockPos pos, Direction dir, float[] p_217629_5_, BitSet bits) {
            long posLong = pos.toLong();
            if (this.cachedPos != posLong) {
                this.cachedPos = posLong;
                for (int i = 0; i < 12; i++) {
                    this.cachedFaceData[i].hasData = false;
                }
            }

            int cacheIdx = dir.getIndex() * 2 + (bits.get(0) ? 1 : 0);
            AoFaceData faceData = this.cachedFaceData[cacheIdx];

            if (!faceData.hasData) {
                this.populateFaceData(faceData, reader, state, pos, dir, bits);
                faceData.hasData = true;
            }

            BlockModelRenderer.VertexTranslations trans = BlockModelRenderer.VertexTranslations.getVertexTranslations(dir);
            if (bits.get(1) && BlockModelRenderer.NeighborInfo.getNeighbourInfo(dir).doNonCubicWeight) {
                BlockModelRenderer.NeighborInfo info = BlockModelRenderer.NeighborInfo.getNeighbourInfo(dir);
                float f13 = p_217629_5_[info.vert0Weights[0].shape] * p_217629_5_[info.vert0Weights[1].shape];
                float f14 = p_217629_5_[info.vert0Weights[2].shape] * p_217629_5_[info.vert0Weights[3].shape];
                float f15 = p_217629_5_[info.vert0Weights[4].shape] * p_217629_5_[info.vert0Weights[5].shape];
                float f16 = p_217629_5_[info.vert0Weights[6].shape] * p_217629_5_[info.vert0Weights[7].shape];
                float f17 = p_217629_5_[info.vert1Weights[0].shape] * p_217629_5_[info.vert1Weights[1].shape];
                float f18 = p_217629_5_[info.vert1Weights[2].shape] * p_217629_5_[info.vert1Weights[3].shape];
                float f19 = p_217629_5_[info.vert1Weights[4].shape] * p_217629_5_[info.vert1Weights[5].shape];
                float f20 = p_217629_5_[info.vert1Weights[6].shape] * p_217629_5_[info.vert1Weights[7].shape];
                float f21 = p_217629_5_[info.vert2Weights[0].shape] * p_217629_5_[info.vert2Weights[1].shape];
                float f22 = p_217629_5_[info.vert2Weights[2].shape] * p_217629_5_[info.vert2Weights[3].shape];
                float f23 = p_217629_5_[info.vert2Weights[4].shape] * p_217629_5_[info.vert2Weights[5].shape];
                float f24 = p_217629_5_[info.vert2Weights[6].shape] * p_217629_5_[info.vert2Weights[7].shape];
                float f25 = p_217629_5_[info.vert3Weights[0].shape] * p_217629_5_[info.vert3Weights[1].shape];
                float f26 = p_217629_5_[info.vert3Weights[2].shape] * p_217629_5_[info.vert3Weights[3].shape];
                float f27 = p_217629_5_[info.vert3Weights[4].shape] * p_217629_5_[info.vert3Weights[5].shape];
                float f28 = p_217629_5_[info.vert3Weights[6].shape] * p_217629_5_[info.vert3Weights[7].shape];

                this.vertexColorMultiplier[trans.vert0] = faceData.f9 * f13 + faceData.f10 * f14 + faceData.f11 * f15 + faceData.f12 * f16;
                this.vertexColorMultiplier[trans.vert1] = faceData.f9 * f17 + faceData.f10 * f18 + faceData.f11 * f19 + faceData.f12 * f20;
                this.vertexColorMultiplier[trans.vert2] = faceData.f9 * f21 + faceData.f10 * f22 + faceData.f11 * f23 + faceData.f12 * f24;
                this.vertexColorMultiplier[trans.vert3] = faceData.f9 * f25 + faceData.f10 * f26 + faceData.f11 * f27 + faceData.f12 * f28;
                this.vertexBrightness[trans.vert0] = this.getVertexBrightness(faceData.i2, faceData.j2, faceData.k2, faceData.l2, f13, f14, f15, f16);
                this.vertexBrightness[trans.vert1] = this.getVertexBrightness(faceData.i2, faceData.j2, faceData.k2, faceData.l2, f17, f18, f19, f20);
                this.vertexBrightness[trans.vert2] = this.getVertexBrightness(faceData.i2, faceData.j2, faceData.k2, faceData.l2, f21, f22, f23, f24);
                this.vertexBrightness[trans.vert3] = this.getVertexBrightness(faceData.i2, faceData.j2, faceData.k2, faceData.l2, f25, f26, f27, f28);
            } else {
                this.vertexColorMultiplier[trans.vert0] = faceData.f9;
                this.vertexColorMultiplier[trans.vert1] = faceData.f10;
                this.vertexColorMultiplier[trans.vert2] = faceData.f11;
                this.vertexColorMultiplier[trans.vert3] = faceData.f12;

                this.vertexBrightness[trans.vert0] = faceData.i2;
                this.vertexBrightness[trans.vert1] = faceData.j2;
                this.vertexBrightness[trans.vert2] = faceData.k2;
                this.vertexBrightness[trans.vert3] = faceData.l2;
            }
        }

        private int getAoBrightness(int br1, int br2, int br3, int br4) {
            if (br1 == 0) {
                br1 = br4;
            }

            if (br2 == 0) {
                br2 = br4;
            }

            if (br3 == 0) {
                br3 = br4;
            }

            return br1 + br2 + br3 + br4 >> 2 & 16711935;
        }

        private int getVertexBrightness(int b1, int b2, int b3, int b4, float w1, float w2, float w3, float w4) {
            int i = (int) ((float) (b1 >> 16 & 255) * w1 + (float) (b2 >> 16 & 255) * w2 + (float) (b3 >> 16 & 255) * w3 + (float) (b4 >> 16 & 255) * w4) & 255;
            int j = (int) ((float) (b1 & 255) * w1 + (float) (b2 & 255) * w2 + (float) (b3 & 255) * w3 + (float) (b4 & 255) * w4) & 255;
            return i << 16 | j;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class Cache {
        private boolean enabled;
        private boolean useArray;
        private final ArrayLightDataCache cache = new ArrayLightDataCache();
        private final Long2LongLinkedOpenHashMap fallback = new Long2LongLinkedOpenHashMap(64, 0.50F);

        private Cache() {
        }

        public void func_222895_a() {
            this.fallback.clear();
            this.useArray = false;
            this.enabled = true;
        }

        public void func_222895_a(BlockPos origin) {
            this.cache.reset(origin);
            this.useArray = true;
            this.enabled = true;
        }

        public void func_222897_b() {
            this.enabled = false;
            if (!this.useArray) {
                this.fallback.clear();
            }
        }

        public int func_222893_a(BlockState p_222893_1_, IEnviromentBlockReader p_222893_2_, BlockPos p_222893_3_) {
            if (this.enabled) {
                return (int)(this.get(p_222893_1_, p_222893_2_, p_222893_3_) >>> 32);
            }

            return p_222893_1_.getPackedLightmapCoords(p_222893_2_, p_222893_3_);
        }

        public float func_222896_b(BlockState p_222896_1_, IEnviromentBlockReader p_222896_2_, BlockPos p_222896_3_) {
            if (this.enabled) {
                return Float.intBitsToFloat((int)this.get(p_222896_1_, p_222896_2_, p_222896_3_));
            }

            return p_222896_1_.func_215703_d(p_222896_2_, p_222896_3_);
        }

        private long get(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
            if (this.useArray) {
                return this.cache.get(state, world, pos);
            }

            long key = pos.toLong();
            long word = this.fallback.get(key);
            if (word == 0L) {
                int light = state.getPackedLightmapCoords(world, pos);
                int ambientOcclusion = Float.floatToRawIntBits(state.func_215703_d(world, pos));
                this.fallback.put(key, word = (long) light << 32 | (long) ambientOcclusion & 4294967295L);
            }
            return word;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum NeighborInfo {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

        private final Direction[] corners;
        private final boolean doNonCubicWeight;
        private final BlockModelRenderer.Orientation[] vert0Weights;
        private final BlockModelRenderer.Orientation[] vert1Weights;
        private final BlockModelRenderer.Orientation[] vert2Weights;
        private final BlockModelRenderer.Orientation[] vert3Weights;
        private static final BlockModelRenderer.NeighborInfo[] VALUES = Util.make(new BlockModelRenderer.NeighborInfo[6], (p_209260_0_) -> {
            p_209260_0_[Direction.DOWN.getIndex()] = DOWN;
            p_209260_0_[Direction.UP.getIndex()] = UP;
            p_209260_0_[Direction.NORTH.getIndex()] = NORTH;
            p_209260_0_[Direction.SOUTH.getIndex()] = SOUTH;
            p_209260_0_[Direction.WEST.getIndex()] = WEST;
            p_209260_0_[Direction.EAST.getIndex()] = EAST;
        });

        private NeighborInfo(Direction[] cornersIn, float brightness, boolean doNonCubicWeightIn, BlockModelRenderer.Orientation[] vert0WeightsIn, BlockModelRenderer.Orientation[] vert1WeightsIn, BlockModelRenderer.Orientation[] vert2WeightsIn, BlockModelRenderer.Orientation[] vert3WeightsIn) {
            this.corners = cornersIn;
            this.doNonCubicWeight = doNonCubicWeightIn;
            this.vert0Weights = vert0WeightsIn;
            this.vert1Weights = vert1WeightsIn;
            this.vert2Weights = vert2WeightsIn;
            this.vert3Weights = vert3WeightsIn;
        }

        public static BlockModelRenderer.NeighborInfo getNeighbourInfo(Direction facing) {
            return VALUES[facing.getIndex()];
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Orientation {
        DOWN(Direction.DOWN, false),
        UP(Direction.UP, false),
        NORTH(Direction.NORTH, false),
        SOUTH(Direction.SOUTH, false),
        WEST(Direction.WEST, false),
        EAST(Direction.EAST, false),
        FLIP_DOWN(Direction.DOWN, true),
        FLIP_UP(Direction.UP, true),
        FLIP_NORTH(Direction.NORTH, true),
        FLIP_SOUTH(Direction.SOUTH, true),
        FLIP_WEST(Direction.WEST, true),
        FLIP_EAST(Direction.EAST, true);

        private final int shape;

        private Orientation(Direction facingIn, boolean flip) {
            this.shape = facingIn.getIndex() + (flip ? Direction.values().length : 0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static enum VertexTranslations {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        private final int vert0;
        private final int vert1;
        private final int vert2;
        private final int vert3;
        private static final BlockModelRenderer.VertexTranslations[] VALUES = Util.make(new BlockModelRenderer.VertexTranslations[6], (p_209261_0_) -> {
            p_209261_0_[Direction.DOWN.getIndex()] = DOWN;
            p_209261_0_[Direction.UP.getIndex()] = UP;
            p_209261_0_[Direction.NORTH.getIndex()] = NORTH;
            p_209261_0_[Direction.SOUTH.getIndex()] = SOUTH;
            p_209261_0_[Direction.WEST.getIndex()] = WEST;
            p_209261_0_[Direction.EAST.getIndex()] = EAST;
        });

        private VertexTranslations(int vert0In, int vert1In, int vert2In, int vert3In) {
            this.vert0 = vert0In;
            this.vert1 = vert1In;
            this.vert2 = vert2In;
            this.vert3 = vert3In;
        }

        public static BlockModelRenderer.VertexTranslations getVertexTranslations(Direction facingIn) {
            return VALUES[facingIn.getIndex()];
        }
    }
}

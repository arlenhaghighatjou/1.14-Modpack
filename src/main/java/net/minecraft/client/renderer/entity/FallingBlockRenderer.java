package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.lax1dude.eaglercraft.Random;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingBlockRenderer extends EntityRenderer<FallingBlockEntity> {
    private final Random random = new Random();
    private final BlockPos.MutableBlockPos entityPos = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos renderPos = new BlockPos.MutableBlockPos();

    public FallingBlockRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    public void doRender(FallingBlockEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        BlockState blockstate = entity.getBlockState();
        if (blockstate.getRenderType() == BlockRenderType.MODEL) {
            World world = entity.getWorldObj();
            if (blockstate != world.getBlockState(this.entityPos.setPos(entity)) && blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                if (this.renderOutlines) {
                    GlStateManager.enableColorMaterial();
                    GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
                }

                bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
                BlockPos blockpos = this.renderPos.setPos(entity.posX, entity.getBoundingBox().maxY, entity.posZ);
                GlStateManager.translatef((float) (x - (double) blockpos.getX() - 0.5D), (float) (y - (double) blockpos.getY()), (float) (z - (double) blockpos.getZ() - 0.5D));
                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
                blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(blockstate), blockstate, blockpos, bufferbuilder, false, this.random, blockstate.getPositionRandom(entity.getOrigin()));
                tessellator.draw();
                if (this.renderOutlines) {
                    GlStateManager.tearDownSolidRenderingTextureCombine();
                    GlStateManager.disableColorMaterial();
                }

                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
                super.doRender(entity, x, y, z, entityYaw, partialTicks);
            }
        }
    }

    protected ResourceLocation getEntityTexture(FallingBlockEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}

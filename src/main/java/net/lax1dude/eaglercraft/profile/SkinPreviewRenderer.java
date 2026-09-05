package net.lax1dude.eaglercraft.profile;

import net.lax1dude.eaglercraft.opengl.EaglerMeshLoader;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.util.ResourceLocation;

public class SkinPreviewRenderer {

    private static PlayerModel playerModelSteve = null;
    private static PlayerModel playerModelAlex = null;
    private static ZombieModel playerModelZombie = null;

    public static void initialize() {
        playerModelSteve = new PlayerModel(0.0f, false);
        playerModelSteve.isChild = false;
        playerModelAlex = new PlayerModel(0.0f, true);
        playerModelAlex.isChild = false;
        playerModelZombie = new ZombieModel(0.0f, false);
        playerModelZombie.isChild = false;
    }

    public static void renderPreview(int x, int y, int mx, int my, SkinModel skinModel) {
        renderPreview(x, y, mx, my, false, skinModel, null, null);
    }

    public static void renderPreview(int x, int y, int mx, int my, boolean capeMode, SkinModel skinModel, ResourceLocation skinTexture, ResourceLocation capeTexture) {
        if (playerModelSteve == null) {
            initialize();
        }
        BipedModel model;
        switch (skinModel) {
            case STEVE:
            default:
                model = playerModelSteve;
                break;
            case ALEX:
                model = playerModelAlex;
                break;
            case ZOMBIE:
                model = playerModelZombie;
                break;
            case LONG_ARMS:
            case WEIRD_CLIMBER_DUDE:
            case LAXATIVE_DUDE:
            case BABY_CHARLES:
            case BABY_WINSTON:
                if (skinModel.highPoly != null && Minecraft.getInstance().gameSettings.enableFNAWSkins) {
                    renderHighPoly(x, y, mx, my, skinModel.highPoly);
                    return;
                }
                model = playerModelSteve;
                break;
        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y - 80.0f, 100.0f);
        GlStateManager.scale(50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(1.0f, -1.0f, 1.0f);

        RenderHelper.enableStandardItemLighting();

        GlStateManager.translate(0.0f, 1.0f, 0.0f);
        if (capeMode) {
            GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            mx = mx - 20;
            GlStateManager.rotate(((y - my) * -0.02f), 1.0f, 0.0f, 0.0f);
        } else {
            GlStateManager.rotate(((y - my) * -0.06f), 1.0f, 0.0f, 0.0f);
        }
        GlStateManager.rotate(((x - mx) * 0.06f), 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(0.0f, -1.0f, 0.0f);

        if (skinTexture != null) {
            Minecraft.getInstance().getTextureManager().bindTexture(skinTexture);
        }

        model.bipedHead.rotateAngleX = ((y - my) * -0.1f) * ((float) Math.PI / 180F);
        model.bipedHead.rotateAngleY = 0.0f;
        model.bipedHead.render(0.0625f);
        model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
        model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
        model.bipedHeadwear.render(0.0625f);
        model.bipedBody.render(0.0625f);
        model.bipedRightArm.render(0.0625f);
        model.bipedLeftArm.render(0.0625f);
        model.bipedRightLeg.render(0.0625f);
        model.bipedLeftLeg.render(0.0625f);

        if (capeTexture != null && model instanceof PlayerModel) {
            Minecraft.getInstance().getTextureManager().bindTexture(capeTexture);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.25F);
            GlStateManager.scale(1.0F, 1.0F, -1.0F);
            GlStateManager.rotate(6.0F, 1.0F, 0.0F, 0.0F);
            ((PlayerModel) model).renderCape(0.0625f);

            GlStateManager.popMatrix();

            if (skinTexture != null) {
                Minecraft.getInstance().getTextureManager().bindTexture(skinTexture);
            }
        }

        if (model instanceof PlayerModel) {
            PlayerModel pm = (PlayerModel) model;
            pm.bipedLeftArmwear.rotateAngleX = pm.bipedLeftArm.rotateAngleX;
            pm.bipedLeftArmwear.rotateAngleY = pm.bipedLeftArm.rotateAngleY;
            pm.bipedLeftArmwear.rotateAngleZ = pm.bipedLeftArm.rotateAngleZ;
            pm.bipedRightArmwear.rotateAngleX = pm.bipedRightArm.rotateAngleX;
            pm.bipedRightArmwear.rotateAngleY = pm.bipedRightArm.rotateAngleY;
            pm.bipedRightArmwear.rotateAngleZ = pm.bipedRightArm.rotateAngleZ;
            pm.bipedLeftLegwear.rotateAngleX = pm.bipedLeftLeg.rotateAngleX;
            pm.bipedLeftLegwear.rotateAngleY = pm.bipedLeftLeg.rotateAngleY;
            pm.bipedLeftLegwear.rotateAngleZ = pm.bipedLeftLeg.rotateAngleZ;
            pm.bipedRightLegwear.rotateAngleX = pm.bipedRightLeg.rotateAngleX;
            pm.bipedRightLegwear.rotateAngleY = pm.bipedRightLeg.rotateAngleY;
            pm.bipedRightLegwear.rotateAngleZ = pm.bipedRightLeg.rotateAngleZ;
            pm.bipedBodyWear.rotateAngleX = pm.bipedBody.rotateAngleX;
            pm.bipedBodyWear.rotateAngleY = pm.bipedBody.rotateAngleY;
            pm.bipedBodyWear.rotateAngleZ = pm.bipedBody.rotateAngleZ;
            pm.bipedLeftArmwear.render(0.0625f);
            pm.bipedRightArmwear.render(0.0625f);
            pm.bipedLeftLegwear.render(0.0625f);
            pm.bipedRightLegwear.render(0.0625f);
            pm.bipedBodyWear.render(0.0625f);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableCull();
    }

    private static void renderHighPoly(int x, int y, int mx, int my, HighPolySkin msh) {
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y - 80.0f, 100.0f);
        GlStateManager.scale(50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(1.0f, -1.0f, 1.0f);

        RenderHelper.enableStandardItemLighting();

        GlStateManager.translate(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(((y - my) * -0.06f), 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(((x - mx) * 0.06f), 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(0.0f, -0.6f, 0.0f);

        GlStateManager.scale(HighPolySkin.highPolyScale, HighPolySkin.highPolyScale, HighPolySkin.highPolyScale);
        Minecraft.getInstance().getTextureManager().bindTexture(msh.texture);

        if (msh.bodyModel != null) {
            EaglercraftGPU.drawHighPoly(EaglerMeshLoader.getEaglerMesh(msh.bodyModel));
        }

        if (msh.headModel != null) {
            EaglercraftGPU.drawHighPoly(EaglerMeshLoader.getEaglerMesh(msh.headModel));
        }

        if (msh.limbsModel != null && msh.limbsModel.length > 0) {
            for (int i = 0; i < msh.limbsModel.length; ++i) {
                float offset = 0.0f;
                if (msh.limbsOffset != null) {
                    if (msh.limbsOffset.length == 1) {
                        offset = msh.limbsOffset[0];
                    } else {
                        offset = msh.limbsOffset[i];
                    }
                }
                if (offset != 0.0f || msh.limbsInitialRotation != 0.0f) {
                    GlStateManager.pushMatrix();
                    if (offset != 0.0f) {
                        GlStateManager.translate(0.0f, offset, 0.0f);
                    }
                    if (msh.limbsInitialRotation != 0.0f) {
                        GlStateManager.rotate(msh.limbsInitialRotation, 1.0f, 0.0f, 0.0f);
                    }
                }

                EaglercraftGPU.drawHighPoly(EaglerMeshLoader.getEaglerMesh(msh.limbsModel[i]));

                if (offset != 0.0f || msh.limbsInitialRotation != 0.0f) {
                    GlStateManager.popMatrix();
                }
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableCull();
    }

}

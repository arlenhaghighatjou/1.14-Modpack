package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.util.math.Vec3d;

public class RenderHelper {
    private static final net.lax1dude.eaglercraft.internal.buffer.FloatBuffer COLOR_BUFFER = GLAllocation.createDirectFloatBuffer(4);
    private static final Vec3d LIGHT0_POS = (new Vec3d(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
    private static final Vec3d LIGHT1_POS = (new Vec3d(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();

    public static void disableStandardItemLighting() {
        GlStateManager.disableLighting();
        GlStateManager.disableMCLight(0);
        GlStateManager.disableMCLight(1);
        GlStateManager.disableColorMaterial();
    }

    public static void enableStandardItemLighting() {
        GlStateManager.enableLighting();
        GlStateManager.enableMCLight(0, 0.6f, LIGHT0_POS.x, LIGHT0_POS.y, LIGHT0_POS.z, 0.0D);
        GlStateManager.enableMCLight(1, 0.6f, LIGHT1_POS.x, LIGHT1_POS.y, LIGHT1_POS.z, 0.0D);
        GlStateManager.setMCLightAmbient(0.4f, 0.4f, 0.4f);
        GlStateManager.enableColorMaterial();
    }

    public static void enableGUIStandardItemLighting() {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
        enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public static FloatBuffer setColorBuffer(float red, float green, float blue, float alpha) {
        COLOR_BUFFER.clear();
        COLOR_BUFFER.put(red).put(green).put(blue).put(alpha);
        COLOR_BUFFER.flip();
        return COLOR_BUFFER;
    }
}
package com.mojang.blaze3d.platform;

import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglClearDepth;
import static net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglDepthFunc;

@OnlyIn(Dist.CLIENT)
public class GlStateManager {

    private static final GlStateManager.TexGenState TEX_GEN = new GlStateManager.TexGenState();
    private static double lastClearDepth = Double.NaN;

    public static void pushLightingAttributes() {
    }

    public static void popAttributes() {
    }

    public static void disableAlphaTest() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableAlpha();
    }

    public static void enableAlphaTest() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableAlpha();
    }

    public static void alphaFunc(int p_alphaFunc_0_, float p_alphaFunc_1_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.alphaFunc(516, p_alphaFunc_1_);
    }

    public static void enableLighting() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableLighting();
    }

    public static void disableLighting() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableLighting();
    }

    public static void enableLight(int p_enableLight_0_) {
    }

    public static void disableLight(int p_disableLight_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableMCLight(p_disableLight_0_);
    }

    public static void enableColorMaterial() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableColorMaterial();
    }

    public static void disableColorMaterial() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableColorMaterial();
    }

    public static void disableMCLight(int p_disableLight_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableMCLight(p_disableLight_0_);
    }

    public static void enableMCLight(int light, float diffuse, double dirX, double dirY, double dirZ, double dirW) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableMCLight(light, diffuse, dirX, dirY, dirZ, dirW);
    }

    public static void setMCLightAmbient(float r, float g, float b) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.setMCLightAmbient(r, g, b);
    }

    public static void colorMaterial(int p_colorMaterial_0_, int p_colorMaterial_1_) {
        GL11.glColorMaterial(p_colorMaterial_0_, p_colorMaterial_1_);
    }

    public static void light(int p_light_0_, int p_light_1_, FloatBuffer p_light_2_) {
    }

    public static void lightModel(int p_lightModel_0_, FloatBuffer p_lightModel_1_) {
    }

    public static void normal3f(float p_normal3f_0_, float p_normal3f_1_, float p_normal3f_2_) {
        GL11.glNormal3f(p_normal3f_0_, p_normal3f_1_, p_normal3f_2_);
    }

    public static void disableDepthTest() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableDepth();
    }

    public static void enableDepthTest() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableDepth();
    }

    public static void enableScissorTest() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableScissorTest();
    }

    public static void disableScissorTest() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableScissorTest();
    }

    public static void scissor(int x, int y, int w, int h) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.scissor(x, y, w, h);
    }

    public static void depthFunc(int p_depthFunc_0_) {
        _wglDepthFunc(p_depthFunc_0_);
    }

    public static void depthMask(boolean p_depthMask_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.depthMask(p_depthMask_0_);
    }

    public static void disableBlend() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableBlend();
    }

    public static void enableBlend() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableBlend();
    }

    public static void blendFunc(GlStateManager.SourceFactor p_blendFunc_0_, GlStateManager.DestFactor p_blendFunc_1_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.blendFunc(p_blendFunc_0_.value, p_blendFunc_1_.value);
    }

    public static void blendFunc(int p_blendFunc_0_, int p_blendFunc_1_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.blendFunc(p_blendFunc_0_, p_blendFunc_1_);
    }

    public static void blendFuncSeparate(GlStateManager.SourceFactor p_blendFuncSeparate_0_, GlStateManager.DestFactor p_blendFuncSeparate_1_, GlStateManager.SourceFactor p_blendFuncSeparate_2_, GlStateManager.DestFactor p_blendFuncSeparate_3_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.tryBlendFuncSeparate(p_blendFuncSeparate_0_.value, p_blendFuncSeparate_1_.value, p_blendFuncSeparate_2_.value, p_blendFuncSeparate_3_.value);
    }

    public static void blendFuncSeparate(int p_blendFuncSeparate_0_, int p_blendFuncSeparate_1_, int p_blendFuncSeparate_2_, int p_blendFuncSeparate_3_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.tryBlendFuncSeparate(p_blendFuncSeparate_0_, p_blendFuncSeparate_1_, p_blendFuncSeparate_2_, p_blendFuncSeparate_3_);
    }

    public static void blendEquation(int p_blendEquation_0_) {
    }

    public static void setupSolidRenderingTextureCombine(int color) {
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        net.lax1dude.eaglercraft.opengl.GlStateManager.setShaderBlendSrc(0.0F, 0.0F, 0.0F, 1.0F);
        net.lax1dude.eaglercraft.opengl.GlStateManager.setShaderBlendAdd(r, g, b, 0.0F);
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableShaderBlendAdd();
    }

    public static void tearDownSolidRenderingTextureCombine() {
        disableShaderColorOverlay();
    }

    public static void setShaderColorOverlay(float mulR, float mulG, float mulB, float addR, float addG, float addB) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.setShaderBlendSrc(mulR, mulG, mulB, 1.0F);
        net.lax1dude.eaglercraft.opengl.GlStateManager.setShaderBlendAdd(addR, addG, addB, 0.0F);
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableShaderBlendAdd();
    }

    public static void disableShaderColorOverlay() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableShaderBlendAdd();
        net.lax1dude.eaglercraft.opengl.GlStateManager.setShaderBlendSrc(1.0F, 1.0F, 1.0F, 1.0F);
        net.lax1dude.eaglercraft.opengl.GlStateManager.setShaderBlendAdd(0.0F, 0.0F, 0.0F, 0.0F);
    }

    public static void enableFog() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableFog();
    }

    public static void disableFog() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableFog();
    }

    public static void fogMode(GlStateManager.FogMode p_fogMode_0_) {
        GL11.glFogi(2917, p_fogMode_0_.field_187351_d);
    }

    private static void fogMode(int p_fogMode_0_) {
        GL11.glFogi(2917, p_fogMode_0_);
    }

    public static void fogDensity(float p_fogDensity_0_) {
        GL11.glFogf(2914, p_fogDensity_0_);
    }

    public static void fogStart(float p_fogStart_0_) {
        GL11.glFogf(2915, p_fogStart_0_);
    }

    public static void fogEnd(float p_fogEnd_0_) {
        GL11.glFogf(2916, p_fogEnd_0_);
    }

    public static void fog(int p_fog_0_, net.lax1dude.eaglercraft.internal.buffer.FloatBuffer p_fog_1_) {
        net.lax1dude.eaglercraft.opengl.EaglercraftGPU.glFog(p_fog_0_, p_fog_1_);
    }

    public static void fogi(int p_fogi_0_, int p_fogi_1_) {
        GL11.glFogi(p_fogi_0_, p_fogi_1_);
    }

    public static void enableCull() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableCull();
    }

    public static void disableCull() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableCull();
    }

    public static void cullFace(GlStateManager.CullFace p_cullFace_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.cullFace(p_cullFace_0_.field_187328_d);
    }

    private static void cullFace(int p_cullFace_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.cullFace(p_cullFace_0_);
    }

    public static void polygonMode(int p_polygonMode_0_, int p_polygonMode_1_) {
    }

    public static void enablePolygonOffset() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enablePolygonOffset();
    }

    public static void disablePolygonOffset() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disablePolygonOffset();
    }

    public static void enableLineOffset() {
    }

    public static void disableLineOffset() {
    }

    public static void polygonOffset(float p_polygonOffset_0_, float p_polygonOffset_1_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.doPolygonOffset(p_polygonOffset_0_, p_polygonOffset_1_);
    }

    public static void enableColorLogicOp() {
    }

    public static void disableColorLogicOp() {
    }

    public static void logicOp(GlStateManager.LogicOp p_logicOp_0_) {
    }

    public static void logicOp(int p_logicOp_0_) {
    }

    public static void enableTexGen(GlStateManager.TexGen p_enableTexGen_0_) {
        getTexGen(p_enableTexGen_0_).field_179067_a.func_179200_b();
    }

    public static void disableTexGen(GlStateManager.TexGen p_disableTexGen_0_) {
        getTexGen(p_disableTexGen_0_).field_179067_a.func_179198_a();
    }

    public static void texGenMode(GlStateManager.TexGen p_texGenMode_0_, int p_texGenMode_1_) {
        GlStateManager.TexGenCoord glstatemanager$texgencoord = getTexGen(p_texGenMode_0_);
        glstatemanager$texgencoord.field_179066_c = p_texGenMode_1_;
        net.lax1dude.eaglercraft.opengl.GlStateManager.TexGen mappedCoord = mapTexGen(p_texGenMode_0_);
        if (mappedCoord != null) {
            net.lax1dude.eaglercraft.opengl.GlStateManager.texGen(mappedCoord, p_texGenMode_1_);
        }
    }

    public static void texGenParam(GlStateManager.TexGen p_texGenParam_0_, int p_texGenParam_1_, net.lax1dude.eaglercraft.internal.buffer.FloatBuffer p_texGenParam_2_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.TexGen mappedCoord = mapTexGen(p_texGenParam_0_);
        if (mappedCoord != null) {
            net.lax1dude.eaglercraft.opengl.GlStateManager.func_179105_a(mappedCoord, p_texGenParam_1_, p_texGenParam_2_);
        }
    }

    private static net.lax1dude.eaglercraft.opengl.GlStateManager.TexGen mapTexGen(GlStateManager.TexGen texGen) {
        switch (texGen) {
            case S: return net.lax1dude.eaglercraft.opengl.GlStateManager.TexGen.S;
            case T: return net.lax1dude.eaglercraft.opengl.GlStateManager.TexGen.T;
            case R: return net.lax1dude.eaglercraft.opengl.GlStateManager.TexGen.R;
            case Q: return net.lax1dude.eaglercraft.opengl.GlStateManager.TexGen.Q;
            default: return null;
        }
    }

    private static GlStateManager.TexGenCoord getTexGen(GlStateManager.TexGen p_getTexGen_0_) {
        switch (p_getTexGen_0_) {
            case S:
                return TEX_GEN.field_179064_a;
            case T:
                return TEX_GEN.field_179062_b;
            case R:
                return TEX_GEN.field_179063_c;
            case Q:
                return TEX_GEN.field_179061_d;
            default:
                return TEX_GEN.field_179064_a;
        }
    }

    public static void activeTexture(int p_activeTexture_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.setActiveTexture(p_activeTexture_0_);
    }

    public static void enableTexture() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.enableTexture2D();
    }

    public static void disableTexture() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.disableTexture2D();
    }

    public static void texEnv(int p_texEnv_0_, int p_texEnv_1_, net.lax1dude.eaglercraft.internal.buffer.FloatBuffer p_texEnv_2_) {
    }

    public static void texEnv(int p_texEnv_0_, int p_texEnv_1_, int p_texEnv_2_) {
    }

    public static void texEnv(int p_texEnv_0_, int p_texEnv_1_, float p_texEnv_2_) {
    }

    public static void texParameter(int p_texParameter_0_, int p_texParameter_1_, float p_texParameter_2_) {
        GL11.glTexParameterf(p_texParameter_0_, p_texParameter_1_, p_texParameter_2_);
    }

    public static void texParameter(int p_texParameter_0_, int p_texParameter_1_, int p_texParameter_2_) {
        GL11.glTexParameteri(p_texParameter_0_, p_texParameter_1_, p_texParameter_2_);
    }

    public static int genTexture() {
        return net.lax1dude.eaglercraft.opengl.GlStateManager.generateTexture();
    }

    public static void deleteTexture(int texture) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.deleteTexture(texture);
    }

    public static void bindTexture(int p_bindTexture_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.bindTexture(p_bindTexture_0_);
    }

    public static void texImage2D(int p_texImage2D_0_, int p_texImage2D_1_, int p_texImage2D_2_, int p_texImage2D_3_, int p_texImage2D_4_, int p_texImage2D_5_, int p_texImage2D_6_, int p_texImage2D_7_, net.lax1dude.eaglercraft.internal.buffer.IntBuffer p_texImage2D_8_) {
        GL11.glTexImage2D(p_texImage2D_0_, p_texImage2D_1_, p_texImage2D_2_, p_texImage2D_3_, p_texImage2D_4_, p_texImage2D_5_, p_texImage2D_6_, p_texImage2D_7_, p_texImage2D_8_);
    }

    public static void texSubImage2D(int p_texSubImage2D_0_, int p_texSubImage2D_1_, int p_texSubImage2D_2_, int p_texSubImage2D_3_, int p_texSubImage2D_4_, int p_texSubImage2D_5_, int p_texSubImage2D_6_, int p_texSubImage2D_7_, long p_texSubImage2D_8_) {
    }

    public static void getTexImage(int p_getTexImage_0_, int p_getTexImage_1_, int p_getTexImage_2_, int p_getTexImage_3_, long p_getTexImage_4_) {
    }

    public static void enableNormalize() {
    }

    public static void disableNormalize() {
    }

    public static void shadeModel(int p_shadeModel_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.shadeModel(p_shadeModel_0_);
    }

    public static void enableRescaleNormal() {
    }

    public static void disableRescaleNormal() {
    }

    public static void viewport(int p_viewport_0_, int p_viewport_1_, int p_viewport_2_, int p_viewport_3_) {
        GlStateManager.Viewport.INSTANCE.field_199289_b = p_viewport_0_;
        GlStateManager.Viewport.INSTANCE.field_199290_c = p_viewport_1_;
        GlStateManager.Viewport.INSTANCE.field_199291_d = p_viewport_2_;
        GlStateManager.Viewport.INSTANCE.field_199292_e = p_viewport_3_;
        net.lax1dude.eaglercraft.opengl.GlStateManager.viewport(p_viewport_0_, p_viewport_1_, p_viewport_2_, p_viewport_3_);
    }

    public static void colorMask(boolean p_colorMask_0_, boolean p_colorMask_1_, boolean p_colorMask_2_, boolean p_colorMask_3_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.colorMask(p_colorMask_0_, p_colorMask_1_, p_colorMask_2_, p_colorMask_3_);
    }

    public static void stencilFunc(int p_stencilFunc_0_, int p_stencilFunc_1_, int p_stencilFunc_2_) {
    }

    public static void stencilMask(int p_stencilMask_0_) {
    }

    public static void stencilOp(int p_stencilOp_0_, int p_stencilOp_1_, int p_stencilOp_2_) {
    }

    public static void clearDepth(double p_clearDepth_0_) {
        if (p_clearDepth_0_ != lastClearDepth) {
            _wglClearDepth((float) p_clearDepth_0_);
            lastClearDepth = p_clearDepth_0_;
        }
    }

    public static void clearColor(float p_clearColor_0_, float p_clearColor_1_, float p_clearColor_2_, float p_clearColor_3_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.clearColor(p_clearColor_0_, p_clearColor_1_, p_clearColor_2_, p_clearColor_3_);
    }

    public static void clearStencil(int p_clearStencil_0_) {
    }

    public static void clear(int p_clear_0_, boolean p_clear_1_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.clear(p_clear_0_);
    }

    public static void matrixMode(int p_matrixMode_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.matrixMode(p_matrixMode_0_);
    }

    public static void loadIdentity() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.loadIdentity();
    }

    public static void pushMatrix() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.pushMatrix();
    }

    public static void popMatrix() {
        net.lax1dude.eaglercraft.opengl.GlStateManager.popMatrix();
    }

    public static void getMatrix(int p_getMatrix_0_, net.lax1dude.eaglercraft.internal.buffer.FloatBuffer p_getMatrix_1_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.getFloat(p_getMatrix_0_, p_getMatrix_1_);
    }

    public static Matrix4f getMatrix4f(int p_getMatrix4f_0_) {
        net.lax1dude.eaglercraft.internal.buffer.FloatBuffer buffer = net.lax1dude.eaglercraft.EagRuntime.allocateFloatBuffer(16);
        net.lax1dude.eaglercraft.opengl.GlStateManager.getFloat(p_getMatrix4f_0_, buffer);
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.read(buffer);
        net.lax1dude.eaglercraft.EagRuntime.freeFloatBuffer(buffer);
        return matrix4f;
    }

    public static void ortho(double p_ortho_0_, double p_ortho_2_, double p_ortho_4_, double p_ortho_6_, double p_ortho_8_, double p_ortho_10_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.ortho(p_ortho_0_, p_ortho_2_, p_ortho_4_, p_ortho_6_, p_ortho_8_, p_ortho_10_);
    }

    public static void rotatef(float p_rotatef_0_, float p_rotatef_1_, float p_rotatef_2_, float p_rotatef_3_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.rotate(p_rotatef_0_, p_rotatef_1_, p_rotatef_2_, p_rotatef_3_);
    }

    public static void rotated(double p_rotated_0_, double p_rotated_2_, double p_rotated_4_, double p_rotated_6_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.rotate((float) p_rotated_0_, (float) p_rotated_2_, (float) p_rotated_4_, (float) p_rotated_6_);
    }

    public static void scalef(float p_scalef_0_, float p_scalef_1_, float p_scalef_2_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.scale(p_scalef_0_, p_scalef_1_, p_scalef_2_);
    }

    public static void scaled(double p_scaled_0_, double p_scaled_2_, double p_scaled_4_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.scale((float) p_scaled_0_, (float) p_scaled_2_, (float) p_scaled_4_);
    }

    public static void translatef(float p_translatef_0_, float p_translatef_1_, float p_translatef_2_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.translate(p_translatef_0_, p_translatef_1_, p_translatef_2_);
    }

    public static void translated(double p_translated_0_, double p_translated_2_, double p_translated_4_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.translate(p_translated_0_, p_translated_2_, p_translated_4_);
    }

    public static void multMatrix(float[] p_multMatrix_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.multMatrix(p_multMatrix_0_);
    }

    public static void multMatrix(net.lax1dude.eaglercraft.vector.Matrix4f p_multMatrix_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.multMatrix(p_multMatrix_0_);
    }

    public static void color4f(float p_color4f_0_, float p_color4f_1_, float p_color4f_2_, float p_color4f_3_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.color(p_color4f_0_, p_color4f_1_, p_color4f_2_, p_color4f_3_);
    }

    public static void color3f(float p_color3f_0_, float p_color3f_1_, float p_color3f_2_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.color(p_color3f_0_, p_color3f_1_, p_color3f_2_, 1.0F);
    }

    public static void texCoord2f(float p_texCoord2f_0_, float p_texCoord2f_1_) {
        GL11.glTexCoord2f(p_texCoord2f_0_, p_texCoord2f_1_);
    }

    public static void clearCurrentColor() {
    }

    public static void normalPointer(int p_normalPointer_0_, int p_normalPointer_1_, int p_normalPointer_2_) {
    }

    public static void normalPointer(int p_normalPointer_0_, int p_normalPointer_1_, ByteBuffer p_normalPointer_2_) {
    }

    public static void texCoordPointer(int p_texCoordPointer_0_, int p_texCoordPointer_1_, int p_texCoordPointer_2_, int p_texCoordPointer_3_) {
    }

    public static void texCoordPointer(int p_texCoordPointer_0_, int p_texCoordPointer_1_, int p_texCoordPointer_2_, ByteBuffer p_texCoordPointer_3_) {
    }

    public static void vertexPointer(int p_vertexPointer_0_, int p_vertexPointer_1_, int p_vertexPointer_2_, int p_vertexPointer_3_) {
    }

    public static void vertexPointer(int p_vertexPointer_0_, int p_vertexPointer_1_, int p_vertexPointer_2_, ByteBuffer p_vertexPointer_3_) {
    }

    public static void colorPointer(int p_colorPointer_0_, int p_colorPointer_1_, int p_colorPointer_2_, int p_colorPointer_3_) {
    }

    public static void colorPointer(int p_colorPointer_0_, int p_colorPointer_1_, int p_colorPointer_2_, ByteBuffer p_colorPointer_3_) {
    }

    public static void disableClientState(int p_disableClientState_0_) {
    }

    public static void enableClientState(int p_enableClientState_0_) {
    }

    public static void begin(int p_begin_0_) {
        GL11.glBegin(p_begin_0_);
    }

    public static void end() {
        GL11.glEnd();
    }

    public static void drawArrays(int p_drawArrays_0_, int p_drawArrays_1_, int p_drawArrays_2_) {
        EaglercraftGPU.drawArrays(p_drawArrays_0_, p_drawArrays_1_, p_drawArrays_2_);
    }

    public static void lineWidth(float p_lineWidth_0_) {
        GL11.glLineWidth(p_lineWidth_0_);
    }

    public static void callList(int p_callList_0_) {
        GL11.glCallList(p_callList_0_);
    }

    public static void deleteLists(int p_deleteLists_0_, int p_deleteLists_1_) {
        GL11.glDeleteLists(p_deleteLists_0_, p_deleteLists_1_);
    }

    public static void newList(int p_newList_0_, int p_newList_1_) {
        GL11.glNewList(p_newList_0_, p_newList_1_);
    }

    public static void endList() {
        GL11.glEndList();
    }

    public static int genLists(int p_genLists_0_) {
        return GL11.glGenLists(p_genLists_0_);
    }

    public static int getError() {
        return GL11.glGetError();
    }

    public static String getString(int p_getString_0_) {
        return GL11.glGetString(p_getString_0_);
    }

    public static void getInteger(int p_getInteger_0_, int[] p_getInteger_1_) {
        EaglercraftGPU.glGetInteger(p_getInteger_0_, p_getInteger_1_);
    }

    public static int getInteger(int p_getInteger_0_) {
        return EaglercraftGPU.glGetInteger(p_getInteger_0_);
    }

    public static void setProfile(GlStateManager.Profile p_setProfile_0_) {
        p_setProfile_0_.func_187373_a();
    }

    public static void unsetProfile(GlStateManager.Profile p_unsetProfile_0_) {
        p_unsetProfile_0_.func_187374_b();
    }

    public static void vertexPointer(int elementCount, int k, int i, net.lax1dude.eaglercraft.internal.buffer.ByteBuffer bytebuffer) {
    }

    public static void colorPointer(int elementCount, int k, int i, net.lax1dude.eaglercraft.internal.buffer.ByteBuffer bytebuffer) {
    }

    public static void texCoordPointer(int elementCount, int k, int i, net.lax1dude.eaglercraft.internal.buffer.ByteBuffer bytebuffer) {
    }

    public static void normalPointer(int k, int i, net.lax1dude.eaglercraft.internal.buffer.ByteBuffer bytebuffer) {
    }

    @OnlyIn(Dist.CLIENT)
    public static enum CullFace {
        FRONT(1028),
        BACK(1029),
        FRONT_AND_BACK(1032);

        public final int field_187328_d;

        private CullFace(int p_i50865_3_) {
            this.field_187328_d = p_i50865_3_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum DestFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int value;

        private DestFactor(int p_i51106_3_) {
            this.value = p_i51106_3_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum FogMode {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);

        public final int field_187351_d;

        private FogMode(int p_i50862_3_) {
            this.field_187351_d = p_i50862_3_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum LogicOp {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int field_187370_q;

        private LogicOp(int p_i50860_3_) {
            this.field_187370_q = p_i50860_3_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Profile {
        DEFAULT {
            public void func_187373_a() {
            }

            public void func_187374_b() {
            }
        },
        PLAYER_SKIN {
            public void func_187373_a() {
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            }

            public void func_187374_b() {
                GlStateManager.disableBlend();
            }
        },
        TRANSPARENT_MODEL {
            public void func_187373_a() {
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            public void func_187374_b() {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.depthMask(true);
            }
        };

        private Profile() {
        }

        public abstract void func_187373_a();

        public abstract void func_187374_b();
    }

    @OnlyIn(Dist.CLIENT)
    public static enum SourceFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int value;

        private SourceFactor(int p_i50328_3_) {
            this.value = p_i50328_3_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static enum TexGen {
        S,
        T,
        R,
        Q;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Viewport {
        INSTANCE;

        protected int field_199289_b;
        protected int field_199290_c;
        protected int field_199291_d;
        protected int field_199292_e;
    }

    @OnlyIn(Dist.CLIENT)
    static class AlphaState {
        public final GlStateManager.BooleanState field_179208_a = new GlStateManager.BooleanState(3008);
        public int field_179206_b = 519;
        public float field_179207_c = -1.0F;

        private AlphaState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class BlendState {
        public final GlStateManager.BooleanState field_179213_a = new GlStateManager.BooleanState(3042);
        public int field_179211_b = 1;
        public int field_179212_c = 0;
        public int field_179209_d = 1;
        public int field_179210_e = 0;

        private BlendState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class BooleanState {
        private final int field_179202_a;
        private boolean field_179201_b;

        public BooleanState(int p_i50871_1_) {
            this.field_179202_a = p_i50871_1_;
        }

        public void func_179198_a() {
            this.func_179199_a(false);
        }

        public void func_179200_b() {
            this.func_179199_a(true);
        }

        public void func_179199_a(boolean p_179199_1_) {
            this.field_179201_b = p_179199_1_;
            if (p_179199_1_) {
                GL11.glEnable(this.field_179202_a);
            } else {
                GL11.glDisable(this.field_179202_a);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class ClearState {
        public final GlStateManager.Color field_179203_b = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
        public double field_179205_a = 1.0D;
        public int field_212901_c;

        private ClearState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class Color {
        public float field_179195_a = 1.0F;
        public float field_179193_b = 1.0F;
        public float field_179194_c = 1.0F;
        public float field_179192_d = 1.0F;

        public Color() {
            this(1.0F, 1.0F, 1.0F, 1.0F);
        }

        public Color(float p_i50869_1_, float p_i50869_2_, float p_i50869_3_, float p_i50869_4_) {
            this.field_179195_a = p_i50869_1_;
            this.field_179193_b = p_i50869_2_;
            this.field_179194_c = p_i50869_3_;
            this.field_179192_d = p_i50869_4_;
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class ColorLogicState {
        public final GlStateManager.BooleanState field_179197_a = new GlStateManager.BooleanState(3058);
        public int field_179196_b = 5379;

        private ColorLogicState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class ColorMask {
        public boolean field_179188_a = true;
        public boolean field_179186_b = true;
        public boolean field_179187_c = true;
        public boolean field_179185_d = true;

        private ColorMask() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class ColorMaterialState {
        public final GlStateManager.BooleanState field_179191_a = new GlStateManager.BooleanState(2903);
        public int field_179189_b = 1032;
        public int field_179190_c = 5634;

        private ColorMaterialState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class CullState {
        public final GlStateManager.BooleanState field_179054_a = new GlStateManager.BooleanState(2884);
        public int field_179053_b = 1029;

        private CullState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class DepthState {
        public final GlStateManager.BooleanState field_179052_a = new GlStateManager.BooleanState(2929);
        public boolean field_179050_b = true;
        public int field_179051_c = 513;

        private DepthState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class FogState {
        public final GlStateManager.BooleanState field_179049_a = new GlStateManager.BooleanState(2912);
        public int field_179047_b = 2048;
        public float field_179048_c = 1.0F;
        public float field_179045_d;
        public float field_179046_e = 1.0F;

        private FogState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class PolygonOffsetState {
        public final GlStateManager.BooleanState field_179044_a = new GlStateManager.BooleanState(32823);
        public final GlStateManager.BooleanState field_179042_b = new GlStateManager.BooleanState(10754);
        public float field_179043_c;
        public float field_179041_d;

        private PolygonOffsetState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class StencilFunc {
        public int field_179081_a = 519;
        public int field_212902_b;
        public int field_179080_c = -1;

        private StencilFunc() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class StencilState {
        public final GlStateManager.StencilFunc field_179078_a = new GlStateManager.StencilFunc();
        public int field_179076_b = -1;
        public int field_179077_c = 7680;
        public int field_179074_d = 7680;
        public int field_179075_e = 7680;

        private StencilState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class TexGenCoord {
        public final GlStateManager.BooleanState field_179067_a;
        public final int field_179065_b;
        public int field_179066_c = -1;

        public TexGenCoord(int p_i50853_1_, int p_i50853_2_) {
            this.field_179065_b = p_i50853_1_;
            this.field_179067_a = new GlStateManager.BooleanState(p_i50853_2_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class TexGenState {
        public final GlStateManager.TexGenCoord field_179064_a = new GlStateManager.TexGenCoord(8192, 3168);
        public final GlStateManager.TexGenCoord field_179062_b = new GlStateManager.TexGenCoord(8193, 3169);
        public final GlStateManager.TexGenCoord field_179063_c = new GlStateManager.TexGenCoord(8194, 3170);
        public final GlStateManager.TexGenCoord field_179061_d = new GlStateManager.TexGenCoord(8195, 3171);

        private TexGenState() {
        }
    }

    @OnlyIn(Dist.CLIENT)
    static class TextureState {
        public final GlStateManager.BooleanState field_179060_a = new GlStateManager.BooleanState(3553);
        public int field_179059_b;

        private TextureState() {
        }
    }
}

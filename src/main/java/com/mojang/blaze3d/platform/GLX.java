package com.mojang.blaze3d.platform;

import com.google.common.collect.Maps;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class GLX {
    public static final boolean useFbo = true;
    public static final boolean useVbo = true;
    public static final net.lax1dude.eaglercraft.internal.GLObjectMap<net.lax1dude.eaglercraft.internal.IBufferGL> mapBuffersGL = new net.lax1dude.eaglercraft.internal.GLObjectMap<>(8192);
    public static final net.lax1dude.eaglercraft.internal.GLObjectMap<net.lax1dude.eaglercraft.internal.IProgramGL> mapProgramsGL = new net.lax1dude.eaglercraft.internal.GLObjectMap<>(8192);
    public static final net.lax1dude.eaglercraft.internal.GLObjectMap<net.lax1dude.eaglercraft.internal.IShaderGL> mapShadersGL = new net.lax1dude.eaglercraft.internal.GLObjectMap<>(8192);
    public static final net.lax1dude.eaglercraft.internal.GLObjectMap<net.lax1dude.eaglercraft.internal.IFramebufferGL> mapFramebuffersGL = new net.lax1dude.eaglercraft.internal.GLObjectMap<>(8192);
    public static final net.lax1dude.eaglercraft.internal.GLObjectMap<net.lax1dude.eaglercraft.internal.IRenderbufferGL> mapRenderbuffersGL = new net.lax1dude.eaglercraft.internal.GLObjectMap<>(8192);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Integer, String> LOOKUP_MAP = make(Maps.newHashMap(), (p_212906_0_) -> {
        p_212906_0_.put(0, "No error");
        p_212906_0_.put(1280, "Enum parameter is invalid for this function");
        p_212906_0_.put(1281, "Parameter is invalid for this function");
        p_212906_0_.put(1282, "Current state is invalid for this function");
        p_212906_0_.put(1283, "Stack overflow");
        p_212906_0_.put(1284, "Stack underflow");
        p_212906_0_.put(1285, "Out of memory");
        p_212906_0_.put(1286, "Operation on incomplete framebuffer");
        p_212906_0_.put(1286, "Operation on incomplete framebuffer");
    });
    public static boolean isNvidia;
    public static boolean isAmd;
    public static int GL_FRAMEBUFFER;
    public static int GL_RENDERBUFFER;
    public static int GL_COLOR_ATTACHMENT0;
    public static int GL_DEPTH_ATTACHMENT;
    public static int GL_FRAMEBUFFER_COMPLETE;
    public static int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
    public static int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
    public static int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
    public static int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
    public static int GL_LINK_STATUS;
    public static int GL_COMPILE_STATUS;
    public static int GL_VERTEX_SHADER;
    public static int GL_FRAGMENT_SHADER;
    public static int GL_TEXTURE0 = 33984;
    public static int GL_TEXTURE1 = 33985;
    public static int GL_TEXTURE2 = 33986;
    public static int GL_COMBINE;
    public static int GL_INTERPOLATE;
    public static int GL_PRIMARY_COLOR;
    public static int GL_CONSTANT;
    public static int GL_PREVIOUS;
    public static int GL_COMBINE_RGB;
    public static int GL_SOURCE0_RGB;
    public static int GL_SOURCE1_RGB;
    public static int GL_SOURCE2_RGB;
    public static int GL_OPERAND0_RGB;
    public static int GL_OPERAND1_RGB;
    public static int GL_OPERAND2_RGB;
    public static int GL_COMBINE_ALPHA;
    public static int GL_SOURCE0_ALPHA;
    public static int GL_SOURCE1_ALPHA;
    public static int GL_SOURCE2_ALPHA;
    public static int GL_OPERAND0_ALPHA;
    public static int GL_OPERAND1_ALPHA;
    public static int GL_OPERAND2_ALPHA;
    public static boolean useSeparateBlendExt;
    public static boolean isOpenGl21;
    public static boolean usePostProcess;
    public static boolean needVbo;
    public static int GL_ARRAY_BUFFER = 34962;
    public static int GL_STATIC_DRAW = 35044;
    private static GLX.FboMode fboMode;
    private static boolean hasShaders;
    private static boolean useShaderArb;
    private static boolean useMultitextureArb;
    private static boolean useTexEnvCombineArb;
    private static boolean separateBlend;
    private static String capsString = "";
    private static String cpuInfo;
    private static boolean useVboArb;

    public static String getOpenGLVersionString() {
        return "3.2.2";
    }

    public static int getRefreshRate(MainWindow p_getRefreshRate_0_) {
        return 60;
    }

    public static String getLWJGLVersion() {
        return "3.3.2"; 
    }

    public static LongSupplier initGlfw() {
        return () -> net.lax1dude.eaglercraft.EagRuntime.nanoTime();
    }

    public static boolean shouldClose() {
        return Display.isCloseRequested();
    }

    public static void pollEvents() {
        net.lax1dude.eaglercraft.internal.PlatformInput.pollEvents();
    }

    public static String getOpenGLVersion() {
        return GlStateManager.getString(7938);
    }

    public static String getRenderer() {
        return GlStateManager.getString(7937);
    }

    public static String getVendor() {
        return GlStateManager.getString(7936);
    }

    public static void setupNvFogDistance() {

    }

    public static boolean supportsOpenGL2() {
        return true;
    }

    public static void init() {

    }

    public static int glCreateShader(int p_glCreateShader_0_) {
        return 0;
    }

    public static void glShaderSource(int p_glShaderSource_0_, CharSequence p_glShaderSource_1_) {

    }

    public static void glCompileShader(int p_glCompileShader_0_) {

    }

    public static void glUseProgram(int p_glUseProgram_0_) {

    }

    public static void glDeleteProgram(int p_glDeleteProgram_0_) {

    }

    public static void glLinkProgram(int p_glLinkProgram_0_) {

    }

    public static void glUniform1(int p_glUniform1_0_, IntBuffer p_glUniform1_1_) {

    }

    public static void glUniform1i(int p_glUniform1i_0_, int p_glUniform1i_1_) {

    }

    public static void glUniform1(int p_glUniform1_0_, FloatBuffer p_glUniform1_1_) {

    }

    public static void glUniform2(int p_glUniform2_0_, IntBuffer p_glUniform2_1_) {

    }

    public static void glUniform2(int p_glUniform2_0_, FloatBuffer p_glUniform2_1_) {

    }

    public static void glUniform3(int p_glUniform3_0_, IntBuffer p_glUniform3_1_) {

    }

    public static void glUniform3(int p_glUniform3_0_, FloatBuffer p_glUniform3_1_) {

    }

    public static void glUniform4(int p_glUniform4_0_, IntBuffer p_glUniform4_1_) {

    }

    public static void glUniform4(int p_glUniform4_0_, FloatBuffer p_glUniform4_1_) {

    }

    public static void glUniformMatrix2(int p_glUniformMatrix2_0_, boolean p_glUniformMatrix2_1_, FloatBuffer p_glUniformMatrix2_2_) {

    }

    public static void glUniformMatrix3(int p_glUniformMatrix3_0_, boolean p_glUniformMatrix3_1_, FloatBuffer p_glUniformMatrix3_2_) {

    }

    public static void glUniformMatrix4(int p_glUniformMatrix4_0_, boolean p_glUniformMatrix4_1_, FloatBuffer p_glUniformMatrix4_2_) {

    }

    public static void glBindBuffer(int p_glBindBuffer_0_, int p_glBindBuffer_1_) {
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglBindBuffer(p_glBindBuffer_0_, p_glBindBuffer_1_ == 0 ? null : mapBuffersGL.get(p_glBindBuffer_1_));
    }

    public static void glBufferData(int p_glBufferData_0_, ByteBuffer p_glBufferData_1_, int p_glBufferData_2_) {
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglBufferData(p_glBufferData_0_, p_glBufferData_1_, p_glBufferData_2_);
    }

    public static void glDeleteBuffers(int p_glDeleteBuffers_0_) {
        if (p_glDeleteBuffers_0_ != 0) {
            net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglDeleteBuffers(mapBuffersGL.free(p_glDeleteBuffers_0_));
        }
    }

    public static void glMultiTexCoord2f(int p_glMultiTexCoord2f_0_, float p_glMultiTexCoord2f_1_, float p_glMultiTexCoord2f_2_) {
        GL13.glMultiTexCoord2f(p_glMultiTexCoord2f_0_, p_glMultiTexCoord2f_1_, p_glMultiTexCoord2f_2_);
    }

    public static void glClientActiveTexture(int p_glClientActiveTexture_0_) {
        GL13.glClientActiveTexture(p_glClientActiveTexture_0_);
    }

    public static void glDeleteBuffers(IntBuffer p_glDeleteBuffers_0_) {
        for (int i = p_glDeleteBuffers_0_.position(); i < p_glDeleteBuffers_0_.limit(); i++) {
            glDeleteBuffers(p_glDeleteBuffers_0_.get(i));
        }
    }

    public static boolean useVbo() {
        return false;
    }

    public static void glBindFramebuffer(int p_glBindFramebuffer_0_, int p_glBindFramebuffer_1_) {
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglBindFramebuffer(p_glBindFramebuffer_0_, p_glBindFramebuffer_1_ == 0 ? null : mapFramebuffersGL.get(p_glBindFramebuffer_1_));
    }

    public static void glBindRenderbuffer(int p_glBindRenderbuffer_0_, int p_glBindRenderbuffer_1_) {
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglBindRenderbuffer(p_glBindRenderbuffer_0_, p_glBindRenderbuffer_1_ == 0 ? null : mapRenderbuffersGL.get(p_glBindRenderbuffer_1_));
    }

    public static void glDeleteRenderbuffers(int p_glDeleteRenderbuffers_0_) {
        if (p_glDeleteRenderbuffers_0_ != 0) {
            net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglDeleteRenderbuffer(mapRenderbuffersGL.free(p_glDeleteRenderbuffers_0_));
        }
    }

    public static void glDeleteFramebuffers(int p_glDeleteFramebuffers_0_) {
        if (p_glDeleteFramebuffers_0_ != 0) {
            net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglDeleteFramebuffer(mapFramebuffersGL.free(p_glDeleteFramebuffers_0_));
        }
    }

    public static int glGenFramebuffers() {
        return mapFramebuffersGL.register(net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglCreateFramebuffer());
    }

    public static int glGenRenderbuffers() {
        return mapRenderbuffersGL.register(net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglCreateRenderbuffer());
    }

    public static void glRenderbufferStorage(int p_glRenderbufferStorage_0_, int p_glRenderbufferStorage_1_, int p_glRenderbufferStorage_2_, int p_glRenderbufferStorage_3_) {
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglRenderbufferStorage(p_glRenderbufferStorage_0_, p_glRenderbufferStorage_1_, p_glRenderbufferStorage_2_, p_glRenderbufferStorage_3_);
    }

    public static void glFramebufferRenderbuffer(int p_glFramebufferRenderbuffer_0_, int p_glFramebufferRenderbuffer_1_, int p_glFramebufferRenderbuffer_2_, int p_glFramebufferRenderbuffer_3_) {
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglFramebufferRenderbuffer(p_glFramebufferRenderbuffer_0_, p_glFramebufferRenderbuffer_1_, p_glFramebufferRenderbuffer_2_, p_glFramebufferRenderbuffer_3_ == 0 ? null : mapRenderbuffersGL.get(p_glFramebufferRenderbuffer_3_));
    }

    public static int glCheckFramebufferStatus(int p_glCheckFramebufferStatus_0_) {
        return net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglCheckFramebufferStatus(p_glCheckFramebufferStatus_0_);
    }

    public static void glFramebufferTexture2D(int p_glFramebufferTexture2D_0_, int p_glFramebufferTexture2D_1_, int p_glFramebufferTexture2D_2_, int p_glFramebufferTexture2D_3_, int p_glFramebufferTexture2D_4_) {
        net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglFramebufferTexture2D(p_glFramebufferTexture2D_0_, p_glFramebufferTexture2D_1_, p_glFramebufferTexture2D_2_, p_glFramebufferTexture2D_3_ == 0 ? null : net.lax1dude.eaglercraft.opengl.EaglercraftGPU.mapTexturesGL.get(p_glFramebufferTexture2D_3_), p_glFramebufferTexture2D_4_);
    }

    public static int getBoundFramebuffer() {
        switch (fboMode) {
            case BASE:
                return GlStateManager.getInteger(36006);
            case ARB:
                return GlStateManager.getInteger(36006);
            case EXT:
                return GlStateManager.getInteger(36006);
            default:
                return 0;
        }
    }

    public static void glActiveTexture(int p_glActiveTexture_0_) {
        net.lax1dude.eaglercraft.opengl.GlStateManager.setActiveTexture(p_glActiveTexture_0_);
    }

    public static void glBlendFuncSeparate(int p_glBlendFuncSeparate_0_, int p_glBlendFuncSeparate_1_, int p_glBlendFuncSeparate_2_, int p_glBlendFuncSeparate_3_) {
        org.lwjgl.opengl.GL11.glBlendFuncSeparate(p_glBlendFuncSeparate_0_, p_glBlendFuncSeparate_1_, p_glBlendFuncSeparate_2_, p_glBlendFuncSeparate_3_);
    }

    public static boolean isUsingFBOs() {
        return false;
    }

    public static String getCpuInfo() {
        return cpuInfo == null ? "<unknown>" : cpuInfo;
    }

    public static void renderCrosshair(int p_renderCrosshair_0_) {
        renderCrosshair(p_renderCrosshair_0_, true, true, true);
    }

    public static void renderCrosshair(int p_renderCrosshair_0_, boolean p_renderCrosshair_1_, boolean p_renderCrosshair_2_, boolean p_renderCrosshair_3_) {
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GL11.glLineWidth(4.0F);
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        if (p_renderCrosshair_1_) {
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos((double) p_renderCrosshair_0_, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
        }

        if (p_renderCrosshair_2_) {
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(0.0D, (double) p_renderCrosshair_0_, 0.0D).color(0, 0, 0, 255).endVertex();
        }

        if (p_renderCrosshair_3_) {
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, (double) p_renderCrosshair_0_).color(0, 0, 0, 255).endVertex();
        }

        tessellator.draw();
        GL11.glLineWidth(2.0F);
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        if (p_renderCrosshair_1_) {
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
            bufferbuilder.pos((double) p_renderCrosshair_0_, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
        }

        if (p_renderCrosshair_2_) {
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 255, 0, 255).endVertex();
            bufferbuilder.pos(0.0D, (double) p_renderCrosshair_0_, 0.0D).color(0, 255, 0, 255).endVertex();
        }

        if (p_renderCrosshair_3_) {
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(127, 127, 255, 255).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, (double) p_renderCrosshair_0_).color(127, 127, 255, 255).endVertex();
        }

        tessellator.draw();
        GL11.glLineWidth(1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
    }

    public static String getErrorString(int p_getErrorString_0_) {
        return LOOKUP_MAP.get(p_getErrorString_0_);
    }

    public static <T> T make(Supplier<T> p_make_0_) {
        return p_make_0_.get();
    }

    public static <T> T make(T p_make_0_, Consumer<T> p_make_1_) {
        p_make_1_.accept(p_make_0_);
        return p_make_0_;
    }

    public static int glGenBuffers() {
        return mapBuffersGL.register(net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglGenBuffers());
    }

    @OnlyIn(Dist.CLIENT)
    static enum FboMode {
        BASE,
        ARB,
        EXT;
    }
}

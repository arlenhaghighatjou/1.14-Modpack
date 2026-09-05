package net.minecraft.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.lax1dude.eaglercraft.internal.PlatformInput;
import net.minecraft.client.renderer.IWindowEventListener;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.InputStream;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public final class MainWindow implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IWindowEventListener mc;
    private int prevWindowX;
    private int prevWindowY;
    private int prevWindowWidth;
    private int prevWindowHeight;
    private boolean fullscreen;
    private boolean lastFullscreen;
    private int windowX;
    private int windowY;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private int scaledWidth;
    private int scaledHeight;
    private double guiScaleFactor;
    private String renderPhase = "";
    private boolean videoModeChanged;
    private double frameEndTime = Double.MIN_VALUE;
    private int framerateLimit;
    private boolean vsync;

    public MainWindow(IWindowEventListener p_i51170_1_, ScreenSize p_i51170_3_, String p_i51170_4_, String p_i51170_5_) {
        this.setThrowExceptionOnGlError();
        this.setRenderPhase("Pre startup");
        this.mc = p_i51170_1_;

        this.lastFullscreen = this.fullscreen = p_i51170_3_.fullscreen;
        this.prevWindowWidth = this.width = p_i51170_3_.width > 0 ? p_i51170_3_.width : 1;
        this.prevWindowHeight = this.height = p_i51170_3_.height > 0 ? p_i51170_3_.height : 1;
    }

    public static void checkGlfwError(BiConsumer<Integer, String> p_211162_0_) {

    }

    public void loadGUIRenderMatrix(boolean onMac) {
        GlStateManager.clear(256, onMac);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double) this.getFramebufferWidth() / this.getGuiScaleFactor(), (double) this.getFramebufferHeight() / this.getGuiScaleFactor(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
    }

    public void setWindowIcon(InputStream p_216529_1_, InputStream p_216529_2_) {

    }

    public void setRenderPhase(String renderPhaseIn) {
        this.renderPhase = renderPhaseIn;
    }

    private void setThrowExceptionOnGlError() {
    }

    private static void throwExceptionForGlError(int error, long description) {
    }

    public void logGlError(int error, long description) {
        LOGGER.error("########## GL ERROR ##########");
        LOGGER.error("@ {}", (Object) this.renderPhase);
    }

    public void setLogOnGlError() {

    }

    public void setVsync(boolean vsyncEnabled) {
        Display.setVSync(vsyncEnabled);
    }

    public void close() {
    }

    private void onWindowPosUpdate(long windowPointer, int windowXIn, int windowYIn) {
        this.windowX = windowXIn;
        this.windowY = windowYIn;
    }

    private void onFramebufferSizeUpdate(long windowPointer, int framebufferWidth, int framebufferHeight) {

    }

    private void updateFramebufferSize() {
        this.framebufferWidth = net.lax1dude.eaglercraft.Display.getWidth();
        this.framebufferHeight = net.lax1dude.eaglercraft.Display.getHeight();
        if (this.framebufferWidth <= 0) this.framebufferWidth = this.width > 0 ? this.width : 854;
        if (this.framebufferHeight <= 0) this.framebufferHeight = this.height > 0 ? this.height : 480;
    }

    private void onWindowSizeUpdate(long windowPointer, int windowWidthIn, int windowHeightIn) {
        this.width = windowWidthIn;
        this.height = windowHeightIn;
    }

    private void onWindowFocusUpdate(long windowPointer, boolean hasFocus) {

    }

    public void setFramerateLimit(int p_216526_1_) {
        this.framerateLimit = p_216526_1_;
    }

    public int getLimitFramerate() {
        return this.framerateLimit;
    }

    public void update(boolean limitFps) {
        Display.update();
    }

    public void waitFramerateLimit() {
        double d0 = this.frameEndTime + 1.0D / (double) this.getLimitFramerate();

    }

    public void update() {
        if (this.fullscreen && this.videoModeChanged) {
            this.videoModeChanged = false;
            this.updateVideoMode();
            this.mc.updateWindowSize();
        }

    }

    private void updateVideoMode() {
        if (this.fullscreen) {

        } else {
            this.windowX = this.prevWindowX;
            this.windowY = this.prevWindowY;
            this.width = this.prevWindowWidth;
            this.height = this.prevWindowHeight;
        }

    }

    public void toggleFullscreen() {
        PlatformInput.toggleFullscreen();
        this.fullscreen = !this.fullscreen;
    }

    private void toggleFullscreen(boolean vsyncEnabled) {
        try {
            this.updateVideoMode();
            this.mc.updateWindowSize();
            this.setVsync(vsyncEnabled);
            this.mc.updateDisplay(false);
        } catch (Exception exception) {
            LOGGER.error("Couldn't toggle fullscreen", (Throwable) exception);
        }

    }

    public int calcGuiScale(int guiScaleIn, boolean forceUnicode) {
        this.updateFramebufferSize();
        int i;
        for (i = 1; i != guiScaleIn && i < this.framebufferWidth && i < this.framebufferHeight && this.framebufferWidth / (i + 1) >= 320 && this.framebufferHeight / (i + 1) >= 240; ++i) {
            ;
        }

        if (forceUnicode && i % 2 != 0) {
            ++i;
        }

        return i;
    }

    public void setGuiScale(double scaleFactor) {
        this.updateFramebufferSize();
        this.guiScaleFactor = scaleFactor;
        int i = (int) ((double) this.framebufferWidth / scaleFactor);
        this.scaledWidth = (double) this.framebufferWidth / scaleFactor > (double) i ? i + 1 : i;
        int j = (int) ((double) this.framebufferHeight / scaleFactor);
        this.scaledHeight = (double) this.framebufferHeight / scaleFactor > (double) j ? j + 1 : j;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public int getFramebufferWidth() {
        int fw = net.lax1dude.eaglercraft.Display.getWidth();
        return fw > 0 ? fw : this.width;
    }

    public int getFramebufferHeight() {
        int fh = net.lax1dude.eaglercraft.Display.getHeight();
        return fh > 0 ? fh : this.height;
    }

    public int getWidth() {
        int w = net.lax1dude.eaglercraft.Display.getWidth();
        return w > 0 ? w : this.width;
    }

    public int getHeight() {
        int h = net.lax1dude.eaglercraft.Display.getHeight();
        return h > 0 ? h : this.height;
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }

    public int getWindowX() {
        return this.windowX;
    }

    public int getWindowY() {
        return this.windowY;
    }

    public double getGuiScaleFactor() {
        return this.guiScaleFactor;
    }

    public void func_224798_d(boolean p_224798_1_) {
    }
}

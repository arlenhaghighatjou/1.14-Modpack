package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.eymenwsmc.java.CompletableFuture;
import net.eymenwsmc.gui.CreditsScreen;
import net.eymenwsmc.Util;
import net.eymenwsmc.gui.UpdateOverlay;
import net.eymenwsmc.network.NetworkHandler;
import net.eymenwsmc.socials.GuiSocialInfoScreen;
import net.eymenwsmc.socials.GuiSocialLoginScreen;
import net.lax1dude.eaglercraft.*;
import net.lax1dude.eaglercraft.internal.EnumCursorType;
import net.lax1dude.eaglercraft.profile.GuiScreenEditProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.resources.IResource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
;import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executor;

@OnlyIn(Dist.CLIENT)
public class MainMenuScreen extends Screen {
    public static final RenderSkyboxCube PANORAMA_RESOURCES = new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama"));
    private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private static final ResourceLocation ACCESSIBILITY_TEXTURES = new ResourceLocation("textures/gui/friends.png");
    private static final ResourceLocation ACCESSIBILITY_TEXTURES1 = new ResourceLocation("textures/gui/accessibility.png");
    private final boolean showTitleWronglySpelled;
    private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
    private EaglercraftRandom random = new EaglercraftRandom();
    private String splashText;
    private Button buttonResetDemo;

    private MainMenuScreen.WarningDisplay openGLWarning1;
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation MINECRAFT_TITLE_EDITION = new ResourceLocation("textures/gui/title/edition.png");
    private boolean hasCheckedForRealmsNotification;
    private int widthCopyright;
    private int widthCopyrightRest;
    private final RenderSkybox panorama = new RenderSkybox(PANORAMA_RESOURCES);
    private final boolean showFadeInAnimation;
    private long firstRenderTime;

    private boolean updateAvailable = false;
    private String updateDownloadUrl = null;
    private UpdateOverlay updateOverlay;
    private boolean updateCheckRequested = false;

    public MainMenuScreen() {
        this(false);
    }

    public MainMenuScreen(boolean fadeIn) {
        super(new TranslationTextComponent("narrator.screen.title"));
        this.showFadeInAnimation = fadeIn;
        this.showTitleWronglySpelled = (double) (new Random()).nextFloat() < 1.0E-4D;
        if (!GLX.supportsOpenGL2()) {
            this.openGLWarning1 = new MainMenuScreen.WarningDisplay((new TranslationTextComponent("title.oldgl.eol.line1")).applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.BOLD), (new TranslationTextComponent("title.oldgl.eol.line2")).applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.BOLD), "https://help.mojang.com/customer/portal/articles/325948?ref=game");
        }

    }

    public void tick() {
        NetworkHandler.tick();

        if (!updateCheckRequested) {
            if (NetworkHandler.isConnected()) {
                NetworkHandler.requestVersionCheck();
                updateCheckRequested = true;
            } else if (!NetworkHandler.isConnecting) {
                NetworkHandler.connect();
            }
        }

        if (NetworkHandler.versionCheckDone) {
            boolean hasUpdate = Util.checkForUpdates();
            updateAvailable = hasUpdate;
            updateDownloadUrl = NetworkHandler.latestDownloadUrl;
            if (this.updateOverlay != null) {
                this.updateOverlay.setUpdateAvailable(hasUpdate);
            }
        }
    }

    public static CompletableFuture<Void> loadAsync(TextureManager texMngr, Executor backgroundExecutor) {
        return CompletableFuture.allOf(texMngr.loadAsync(MINECRAFT_TITLE_TEXTURES, backgroundExecutor), texMngr.loadAsync(MINECRAFT_TITLE_EDITION, backgroundExecutor), texMngr.loadAsync(PANORAMA_OVERLAY_TEXTURES, backgroundExecutor), PANORAMA_RESOURCES.loadAsync(texMngr, backgroundExecutor));
    }

    public boolean isPauseScreen() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
        this.splashText = "missingno";
        IResource iresource = null;

        try {
            List<String> list = Lists.<String>newArrayList();
            iresource = Minecraft.getInstance().getResourceManager().getResource(SPLASH_TEXTS);
            BufferedReader bufferedreader = new BufferedReader(
                    new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
            String s;

            while ((s = bufferedreader.readLine()) != null) {
                s = s.trim();

                if (!s.isEmpty()) {
                    list.add(s);
                }
            }

            if (!list.isEmpty()) {
                while (true) {
                    this.splashText = list.get(random.nextInt(list.size()));

                    if (this.splashText.hashCode() != 125780783) {
                        break;
                    }
                }
            }
        } catch (IOException var8) {
            ;
        } finally {
            IOUtils.closeQuietly((Closeable) iresource);
        }

        this.widthCopyright = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!");
        this.widthCopyrightRest = this.width - this.widthCopyright - 2;
        int i = 24;
        int j = this.height / 4 + 48;
        if (this.mc.isDemo()) {
            this.addDemoButtons(j, 24);
        } else {
            this.addSingleplayerMultiplayerButtons(j, 24);
        }

        this.addButton(new ImageButton(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, (p_213090_1_) -> {
            this.mc.displayGuiScreen(new LanguageScreen(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }, I18n.format("narrator.button.language")));
        this.addButton(new Button(this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options"), (p_213096_1_) -> {
            this.mc.displayGuiScreen(new OptionsScreen(this, this.mc.gameSettings));
        }));
        this.addButton(new Button(this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("Edit Profile"), (p_213094_1_) -> {
            this.mc.displayGuiScreen(new GuiScreenEditProfile(new MainMenuScreen()));
        }));
        if (mc.gameSettings.socialFeatures) {
            this.addButton(new ImageButton(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 0, ACCESSIBILITY_TEXTURES, 16, 16, (p_213088_1_) -> {

                if (!mc.gameSettings.socialFeatures) {
                    this.mc.displayGuiScreen(new GuiSocialInfoScreen("INFO", "You have disabled social features! Enable it from Chat Settings.", this));
                } else {
                    this.mc.displayGuiScreen(new GuiSocialLoginScreen(this));
                }

            }, I18n.format("narrator.button.accessibility")) {
                @Override
                public void renderButton(int mouseX, int mouseY, float partialTicks) {
                    MainMenuScreen.this.mc.getTextureManager().bindTexture(WIDGETS_LOCATION);
                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                    int i = this.getYImage(this.isHovered());
                    GlStateManager.enableBlend();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    this.blit(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
                    this.blit(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
                    MainMenuScreen.this.mc.getTextureManager().bindTexture(ACCESSIBILITY_TEXTURES);
                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                    int iconX = this.x + 2;
                    int iconY = this.y + 2;

                    this.blit(iconX, iconY, 0, 0, 16, 16, 16, 16);

                    if (this.isHovered()) {
                        MainMenuScreen.this.renderTooltip(I18n.format("Friends"), mouseX, mouseY);
                    }
                }
            });
        }else {
            this.addButton(new ImageButton(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURES1, 32, 64, (p_213088_1_) -> {
                this.mc.displayGuiScreen(new AccessibilityScreen(this, this.mc.gameSettings));
            }, I18n.format("narrator.button.accessibility")));
        }
        if (this.openGLWarning1 != null) {
            this.openGLWarning1.init(j);
        }

        if (this.updateOverlay == null) {
            this.updateOverlay = new UpdateOverlay();
        }
        this.addButton(this.updateOverlay.getDownloadButton());
        this.updateCheckRequested = false;

    }

    private void addSingleplayerMultiplayerButtons(int yIn, int rowHeightIn) {
        this.addButton(new Button(this.width / 2 - 100, yIn, 200, 20, I18n.format("menu.singleplayer"), (p_213089_1_) -> {
            this.mc.displayGuiScreen(new WorldSelectionScreen(this));
        }));
        this.addButton(new Button(this.width / 2 - 100, yIn + rowHeightIn * 1, 200, 20, I18n.format("menu.multiplayer"), (p_213086_1_) -> {
            this.mc.displayGuiScreen(new MultiplayerScreen(this));
        }));
        this.addButton(new Button(this.width / 2 - 100, yIn + rowHeightIn * 2, 200, 20, I18n.format("Fork On Github"), (p_213095_1_) -> {
            this.forkOnGithub();
        }));
    }

    private void addDemoButtons(int yIn, int rowHeightIn) {
        this.addButton(new Button(this.width / 2 - 100, yIn, 200, 20, I18n.format("menu.playdemo"), (p_213092_1_) -> {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", MinecraftServer.DEMO_WORLD_SETTINGS);
        }));
        this.buttonResetDemo = this.addButton(new Button(this.width / 2 - 100, yIn + rowHeightIn * 1, 200, 20, I18n.format("menu.resetdemo"), (p_213091_1_) -> {
            SaveFormat saveformat1 = this.mc.getSaveLoader();
            WorldInfo worldinfo1 = saveformat1.getWorldInfo("Demo_World");
            if (worldinfo1 != null) {
                this.mc.displayGuiScreen(new ConfirmScreen(this::deleteDemoWorld, new TranslationTextComponent("selectWorld.deleteQuestion"), new TranslationTextComponent("selectWorld.deleteWarning", worldinfo1.getWorldName()), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel")));
            }

        }));
        SaveFormat saveformat = this.mc.getSaveLoader();
        WorldInfo worldinfo = saveformat.getWorldInfo("Demo_World");
        if (worldinfo == null) {
            this.buttonResetDemo.active = false;
        }

    }

    private void forkOnGithub() {
        EagRuntime.openLink("https://github.com/Eagler-Versions/eaglercraft-1.14-workspace");
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        GlStateManager.enableAlphaTest();
        GlStateManager.enableBlend();
        if (this.firstRenderTime == 0L && this.showFadeInAnimation) {
            this.firstRenderTime = net.minecraft.util.Util.milliTime();
        }

        float f = this.showFadeInAnimation ? (float) (net.minecraft.util.Util.milliTime() - this.firstRenderTime) / 1000.0F : 1.0F;
        fill(0, 0, this.width, this.height, -1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        this.panorama.render(p_render_3_, MathHelper.clamp(f, 0.0F, 1.0F));
        int i = 274;
        int j = this.width / 2 - 137;
        int k = 30;
        this.mc.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.showFadeInAnimation ? (float) MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
        blit(0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        float f1 = this.showFadeInAnimation ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
        int l = MathHelper.ceil(f1 * 255.0F) << 24;
        if ((l & -67108864) != 0) {
            this.mc.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, f1);
            if (this.showTitleWronglySpelled) {
                this.blit(j + 0, 30, 0, 0, 99, 44);
                this.blit(j + 99, 30, 129, 0, 27, 44);
                this.blit(j + 99 + 26, 30, 126, 0, 3, 44);
                this.blit(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
                this.blit(j + 155, 30, 0, 45, 155, 44);
            } else {
                this.blit(j + 0, 30, 0, 0, 155, 44);
                this.blit(j + 155, 30, 0, 45, 155, 44);
            }

            this.mc.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);
            blit(j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
            if (this.splashText != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translatef((float) (this.width / 2 + 90), 70.0F, 0.0F);
                GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
                float f2 = 1.8F - MathHelper.abs(MathHelper.sin((float) (net.minecraft.util.Util.milliTime() % 1000L) / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
                f2 = f2 * 100.0F / (float) (this.font.getStringWidth(this.splashText) + 32);
                GlStateManager.scalef(f2, f2, f2);
                this.drawCenteredString(this.font, this.splashText, 0, -8, 16776960 | l);
                GlStateManager.popMatrix();
            }

            String s = "Minecraft " + SharedConstants.getVersion().getName();
            String s1 = EaglercraftVersion.projectForkName + " (" + EaglercraftVersion.projectForkVersion + ")";
            if (this.mc.isDemo()) {
                s = s + " Demo";
            } else {
                s = s + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType());
            }

            String lbl = "CREDITS.txt";
            int w = font.getStringWidth(lbl) * 3 / 4;

            int startX = this.width - w - 4;
            int endX = this.width;
            int startY = 0;
            int endY = 10;
            boolean isHovered = p_render_1_ >= startX && p_render_1_ <= endX && p_render_2_ >= startY && p_render_2_ <= endY;

            if (isHovered) {
                Mouse.showCursor(EnumCursorType.HAND);
                fill(startX, startY, endX, endY, 0x55000099);
            } else {
                fill(startX, startY, endX, endY, 0x55200000);
            }
            GlStateManager.pushMatrix();
            GlStateManager.translated((this.width - w - 2), 2.0f, 0.0f);
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            drawString(font, lbl, 0, 0, 16777215);
            GlStateManager.popMatrix();

            this.drawString(this.font, s1, 2, this.height - 10, 16777215 | l);
            this.drawString(this.font, s, 2, this.height - 20, 16777215 | l);
            this.drawString(this.font, "Copyright Mojang AB. Do not distribute!", this.widthCopyrightRest, this.height - 10, 16777215 | l);
            if (p_render_1_ > this.widthCopyrightRest && p_render_1_ < this.widthCopyrightRest + this.widthCopyright && p_render_2_ > this.height - 10 && p_render_2_ < this.height) {
                fill(this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, 16777215 | l);
            }

            if (this.openGLWarning1 != null) {
                this.openGLWarning1.render(l);
            }

            for (Widget widget : this.buttons) {
                widget.setAlpha(f1);
            }

            if (this.updateOverlay != null) {
                this.updateOverlay.render(p_render_1_, p_render_2_, p_render_3_);
            }

            super.render(p_render_1_, p_render_2_, p_render_3_);

        }
    }

    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        String lbl = "CREDITS.txt";
        int w = font.getStringWidth(lbl) * 3 / 4;
        if (p_mouseClicked_1_ >= (this.width - w - 4) && p_mouseClicked_1_ <= this.width && p_mouseClicked_3_ >= 0 && p_mouseClicked_3_ <= 10) {
            String resStr = EagRuntime.getResourceString("/assets/eagler/CREDITS.txt");
            if (resStr != null) {
                this.mc.displayGuiScreen(new CreditsScreen());
            }
        }
        if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
        } else if (this.openGLWarning1 != null && this.openGLWarning1.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_)) {
            return true;
        } else {
            if (p_mouseClicked_1_ > (double) this.widthCopyrightRest && p_mouseClicked_1_ < (double) (this.widthCopyrightRest + this.widthCopyright) && p_mouseClicked_3_ > (double) (this.height - 10) && p_mouseClicked_3_ < (double) this.height) {
            }

            return false;
        }

    }

    public void removed() {

    }

    private void deleteDemoWorld(boolean p_213087_1_) {
        if (p_213087_1_) {
            SaveFormat saveformat = this.mc.getSaveLoader();
            saveformat.deleteWorldDirectory("Demo_World");
        }

        this.mc.displayGuiScreen(this);
    }

    @OnlyIn(Dist.CLIENT)
    class WarningDisplay {
        private int secondLineWidth;
        private int left;
        private int top;
        private int right;
        private int bottom;
        private final ITextComponent firstLine;
        private final ITextComponent secondLine;
        private final String onClickURL;

        public WarningDisplay(ITextComponent line1, ITextComponent line2, String url) {
            this.firstLine = line1;
            this.secondLine = line2;
            this.onClickURL = url;
        }

        public void init(int yIn) {
            int i = MainMenuScreen.this.font.getStringWidth(this.firstLine.getString());
            this.secondLineWidth = MainMenuScreen.this.font.getStringWidth(this.secondLine.getString());
            int j = Math.max(i, this.secondLineWidth);
            this.left = (MainMenuScreen.this.width - j) / 2;
            this.top = yIn - 24;
            this.right = this.left + j;
            this.bottom = this.top + 24;
        }

        public void render(int alpha) {
            AbstractGui.fill(this.left - 2, this.top - 2, this.right + 2, this.bottom - 1, 1428160512);
            MainMenuScreen.this.drawString(MainMenuScreen.this.font, this.firstLine.getFormattedText(), this.left, this.top, 16777215 | alpha);
            MainMenuScreen.this.drawString(MainMenuScreen.this.font, this.secondLine.getFormattedText(), (MainMenuScreen.this.width - this.secondLineWidth) / 2, this.top + 12, 16777215 | alpha);
        }

        public boolean mouseClicked(double mouseX, double p_223418_3_) {
            if (!StringUtils.isNullOrEmpty(this.onClickURL) && mouseX >= (double) this.left && mouseX <= (double) this.right && p_223418_3_ >= (double) this.top && p_223418_3_ <= (double) this.bottom) {
                MainMenuScreen.this.mc.displayGuiScreen(new ConfirmOpenLinkScreen((p_223421_1_) -> {
                    if (p_223421_1_) {
                        net.minecraft.util.Util.getOSType().openURI(this.onClickURL);
                    }

                    MainMenuScreen.this.mc.displayGuiScreen(MainMenuScreen.this);
                }, this.onClickURL, true));
                return true;
            } else {
                return false;
            }
        }
    }
}

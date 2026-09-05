package net.minecraft.client.gui.screen;

import net.lax1dude.eaglercraft.internal.EnumPlatformType;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.LockIconButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.cookie.GuiScreenRevokeSessionToken;
import net.lax1dude.eaglercraft.cookie.ServerCookieDataStore;
import net.lax1dude.eaglercraft.internal.EnumCursorType;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.profile.GuiScreenGenericErrorMessage;
import net.lax1dude.eaglercraft.recording.GuiScreenRecordingNote;
import net.lax1dude.eaglercraft.recording.GuiScreenRecordingSettings;
import net.lax1dude.eaglercraft.recording.ScreenRecordingController;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsScreen extends Screen {
   private static final AbstractOption[] SCREEN_OPTIONS = new AbstractOption[]{AbstractOption.FOV};
   private final Screen lastScreen;
   private final GameSettings settings;
   private Button difficultyButton;
   private LockIconButton lockButton;
   private Difficulty field_213062_f;
   private Button recordingButton;

   public OptionsScreen(Screen p_i1046_1_, GameSettings p_i1046_2_) {
      super(new TranslationTextComponent("options.title"));
      this.lastScreen = p_i1046_1_;
      this.settings = p_i1046_2_;
   }

   protected void init() {
      int i = 0;

      for(AbstractOption abstractoption : SCREEN_OPTIONS) {
         int j = this.width / 2 - 155 + i % 2 * 160;
         int k = this.height / 6 - 12 + 24 * (i >> 1);
         this.addButton(abstractoption.createWidget(this.mc.gameSettings, j, k, 150));
         ++i;
      }

      if (this.mc.world != null) {
         this.field_213062_f = this.mc.world.getDifficulty();
         this.difficultyButton = this.addButton(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyText(this.field_213062_f), (p_213051_1_) -> {
            this.field_213062_f = Difficulty.byId(this.field_213062_f.getId() + 1);
            this.mc.getConnection().sendPacket(new CSetDifficultyPacket(this.field_213062_f));
            this.difficultyButton.setMessage(this.getDifficultyText(this.field_213062_f));
         }));
         if (this.mc.isSingleplayer() && !this.mc.world.getWorldInfo().isHardcore()) {
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
            this.lockButton = this.addButton(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, (p_213054_1_) -> {
               this.mc.displayGuiScreen(new ConfirmScreen(this::func_213050_a, new TranslationTextComponent("difficulty.lock.title"), new TranslationTextComponent("difficulty.lock.question", new TranslationTextComponent("options.difficulty." + this.mc.world.getWorldInfo().getDifficulty().getTranslationKey()))));
            }));
            this.lockButton.setLocked(this.mc.world.getWorldInfo().isDifficultyLocked());
            this.lockButton.active = !this.lockButton.isLocked();
            this.difficultyButton.active = !this.lockButton.isLocked();
         } else {
            this.difficultyButton.active = false;
         }
      } else {

      }

      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation"), (p_213055_1_) -> {
         this.mc.displayGuiScreen(new CustomizeSkinScreen(this));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.sounds"), (p_213061_1_) -> {
         this.mc.displayGuiScreen(new OptionsSoundsScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.video"), (p_213059_1_) -> {
         this.mc.displayGuiScreen(new VideoSettingsScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.controls"), (p_213052_1_) -> {
         this.mc.displayGuiScreen(new ControlsScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.language"), (p_213053_1_) -> {
         this.mc.displayGuiScreen(new LanguageScreen(this, this.settings, this.mc.getLanguageManager()));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.chat.title"), (p_213049_1_) -> {
         this.mc.displayGuiScreen(new ChatOptionsScreen(this, this.settings));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.resourcepack"), (p_213060_1_) -> {
         this.mc.displayGuiScreen(new ResourcePacksScreen(this));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.accessibility.title"), (p_213058_1_) -> {
         this.mc.displayGuiScreen(new AccessibilityScreen(this, this.settings));
      }));
      int bottomRow = this.height / 6 + 138;
      boolean support = ScreenRecordingController.isSupported();
      this.addButton(recordingButton = new Button(this.width / 2 - 155, bottomRow, 150, 20,
              I18n.format(support ? "options.screenRecording.button" : "options.screenRecording.unsupported"), (p_213050_1_) -> {
         if (ScreenRecordingController.isSupported()) {
            Screen screen;
            if (!GuiScreenRecordingNote.hasShown) {
               screen = new GuiScreenRecordingNote(OptionsScreen.this);
            } else {
               screen = new GuiScreenRecordingSettings(OptionsScreen.this);
            }
            OptionsScreen.this.mc.displayGuiScreen(screen);
         }
      }));
      recordingButton.active = support;
      Button retard = new Button(this.width / 2 + 5, bottomRow, 150, 20, "Debug Console", (p_213057_1_) -> {
         EagRuntime.showDebugConsole();
      });

      retard.active = (EagRuntime.getPlatformType() == EnumPlatformType.DESKTOP) ? false : true;

      this.addButton(retard);
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.format("gui.done"), (p_213056_1_) -> {
         this.mc.displayGuiScreen(this.lastScreen);
      }));
   }

   public String getDifficultyText(Difficulty p_175355_1_) {
      return (new TranslationTextComponent("options.difficulty")).appendText(": ").appendSibling(p_175355_1_.getDisplayName()).getFormattedText();
   }

   private void func_213050_a(boolean p_213050_1_) {
      this.mc.displayGuiScreen(this);
      if (p_213050_1_ && this.mc.world != null) {
         this.mc.getConnection().sendPacket(new CLockDifficultyPacket(true));
         this.lockButton.setLocked(true);
         this.lockButton.active = false;
         this.difficultyButton.active = false;
      }

   }

   public void removed() {
      this.settings.saveOptions();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 15, 16777215);

      if (EagRuntime.getConfiguration().isEnableServerCookies() && this.mc.player == null) {
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.75f, 0.75f, 0.75f);
         GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

         String text = I18n.format("revokeSessionToken.button");

         int w = this.font.getStringWidth(text);
         boolean hover = p_render_1_ > width - 5 - (w + 5) * 3 / 4 && p_render_2_ > 1 && p_render_1_ < width - 2 && p_render_2_ < 12;
         if (hover) {
            Mouse.showCursor(EnumCursorType.HAND);
         }

         this.drawString(this.font, TextFormatting.UNDERLINE + text, (width - 1) * 4 / 3 - w - 5, 5,
                 hover ? 0xFFEEEE22 : 0xFFCCCCCC);

         GlStateManager.popMatrix();
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (EagRuntime.getConfiguration().isEnableServerCookies() && this.mc.player == null) {
         int w = this.font.getStringWidth(I18n.format("revokeSessionToken.button"));
         if (mouseX > width - 5 - (w + 5) * 3 / 4 && mouseY > 1 && mouseX < width - 2 && mouseY < 12) {
            ServerCookieDataStore.flush();
            this.mc.displayGuiScreen(ServerCookieDataStore.numRevokable() == 0
                    ? new GuiScreenGenericErrorMessage("errorNoSessions.title", "errorNoSessions.desc", this)
                    : new GuiScreenRevokeSessionToken(this));
            return true;
         }
      }
      return super.mouseClicked(mouseX, mouseY, button);
   }
}

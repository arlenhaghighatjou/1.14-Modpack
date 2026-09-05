package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import net.lax1dude.eaglercraft.ArrayUtils;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.KeyboardConstants;
import net.lax1dude.eaglercraft.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.recording.EnumScreenRecordingCodec;
import net.lax1dude.eaglercraft.recording.ScreenRecordingController;
import net.lax1dude.eaglercraft.opengl.ext.deferred.EaglerDeferredConfig;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.profile.EaglerProfile;
import net.minecraft.client.gui.screen.GuiScreenVideoSettingsWarning;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.HandSide;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GameSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();

   public static final Splitter COLON_SPLITTER = Splitter.on(':');
   public double mouseSensitivity = 0.5D;
   public int renderDistanceChunks= 4;
   public int framerateLimit = 260;
   public CloudOption cloudOption = CloudOption.OFF;
   public boolean fancyGraphics = false;
   public AmbientOcclusionStatus ambientOcclusionStatus = AmbientOcclusionStatus.OFF;
   public List<String> resourcePacks = Lists.newArrayList();
   public List<String> incompatibleResourcePacks = Lists.newArrayList();
   public ChatVisibility chatVisibility = ChatVisibility.FULL;
   public double chatOpacity = 1.0D;
   public double accessibilityTextBackgroundOpacity = 0.5D;

   public String fullscreenResolution;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus = true;
   private final Set<PlayerModelPart> setModelParts = Sets.newHashSet(PlayerModelPart.values());
   public HandSide mainHand = HandSide.RIGHT;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   public double chatScale = 1.0D;
   public double chatWidth = 1.0D;
   public double chatHeightUnfocused = (double)0.44366196F;
   public double chatHeightFocused = 1.0D;
   public int mipmapLevels = 0;
   private final Map<SoundCategory, Float> soundLevels = Maps.newEnumMap(SoundCategory.class);
   public boolean useNativeTransport = true;
   public AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.CROSSHAIR;
   public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
   public int biomeBlendRadius = 0;
   public double mouseWheelSensitivity = 1.0D;
   public boolean field_225307_E = true;
   public int glDebugVerbosity = 1;
   public boolean autoJump = true;
   public boolean autoSuggestCommands = true;
   public boolean chatColor = true;
   public boolean chatLinks = true;
   public boolean chatLinksPrompt = true;
   public boolean vsync = true;
   public boolean hideVideoSettingsWarning = EagRuntime.getPlatformType() == EnumPlatformType.DESKTOP;
   public boolean entityShadows = true;
   public boolean forceUnicodeFont;
   public boolean invertMouse;
   public boolean discreteMouseScroll;
   public boolean realmsNotifications = true;
   public boolean reducedDebugInfo;
   public boolean snooper = true;
   public boolean showSubtitles;
   public boolean accessibilityTextBackground = true;
   public boolean touchscreen;
   public float touchControlOpacity = 1.0f;
   public boolean fullscreen;
   public boolean viewBobbing = true;
   public final KeyBinding keyBindForward = new KeyBinding("key.forward", 87, "key.categories.movement");
   public final KeyBinding keyBindLeft = new KeyBinding("key.left", 65, "key.categories.movement");
   public final KeyBinding keyBindBack = new KeyBinding("key.back", 83, "key.categories.movement");
   public final KeyBinding keyBindRight = new KeyBinding("key.right", 68, "key.categories.movement");
   public final KeyBinding keyBindJump = new KeyBinding("key.jump", 32, "key.categories.movement");
   public final KeyBinding keyBindSneak = new KeyBinding("key.sneak", 340, "key.categories.movement");
   public final KeyBinding keyBindSprint = new KeyBinding("key.sprint", 341, "key.categories.movement");
   public final KeyBinding keyBindInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
   public final KeyBinding keyBindSwapHands = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
   public final KeyBinding keyBindDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
   public final KeyBinding keyBindUseItem = new KeyBinding("key.use", InputMappings.Type.MOUSE, 1, "key.categories.gameplay");
   public final KeyBinding keyBindAttack = new KeyBinding("key.attack", InputMappings.Type.MOUSE, 0, "key.categories.gameplay");
   public final KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", InputMappings.Type.MOUSE, 2, "key.categories.gameplay");
   public final KeyBinding keyBindChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
   public final KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
   public final KeyBinding keyBindCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
   public final KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
   public final KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
   public final KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
   public final KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
   public final KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
   public final KeyBinding keyBindAdvancements = new KeyBinding("key.advancements", 76, "key.categories.misc");
   public final KeyBinding[] keyBindsHotbar = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
   public final KeyBinding keyBindSaveToolbar = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
   public final KeyBinding keyBindLoadToolbar = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
    public final KeyBinding keyBindZoom = new KeyBinding("Zoom", KeyboardConstants.KEY_C, "key.categories.misc");
    public final KeyBinding keyBindClose = new KeyBinding("key.close", 96, "key.categories.misc");
    public final KeyBinding[] keyBindings = ArrayUtils.addAll(new KeyBinding[]{this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindFullscreen, this.keyBindSpectatorOutlines, this.keyBindSwapHands, this.keyBindSaveToolbar, this.keyBindLoadToolbar, this.keyBindAdvancements, this.keyBindZoom, this.keyBindClose}, this.keyBindsHotbar);
   protected Minecraft mc;
   public final VFile2 optionsFile;
   public Difficulty difficulty = Difficulty.NORMAL;
   public boolean hideGUI;
   public int thirdPersonView;
   public boolean showDebugInfo;
   public boolean showDebugProfilerChart;
   public boolean showLagometer;
   public String lastServer = "";
    public boolean smoothCamera;
    public boolean enableFNAWSkins = true;
   public double fov = 70.0D;
   public double gamma;
   public int guiScale;
   public ParticleStatus particles = ParticleStatus.MINIMAL;
   public NarratorStatus narrator = NarratorStatus.OFF;
   public String language = "en_us";
   public boolean chunkFix = true;
    public int updatesPerFrame = 1;
    public boolean fastEntityRender = true;
    public boolean fastTileEntityRender = true;
    public boolean disableWeather = true;
    public boolean fog = true;
    public boolean socialFeatures = true;
    public EnumScreenRecordingCodec screenRecordCodec;
    public int screenRecordFPS = ScreenRecordingController.DEFAULT_FPS;
    public int screenRecordResolution = ScreenRecordingController.DEFAULT_RESOLUTION;
    public int screenRecordAudioBitrate = ScreenRecordingController.DEFAULT_AUDIO_BITRATE;
    public int screenRecordVideoBitrate = ScreenRecordingController.DEFAULT_VIDEO_BITRATE;
    public float screenRecordGameVolume = ScreenRecordingController.DEFAULT_GAME_VOLUME;
    public float screenRecordMicVolume = ScreenRecordingController.DEFAULT_MIC_VOLUME;
    public boolean showFps = true;
    public boolean showXYZ = true;
    public boolean hudWorld = false;
    public boolean hudStats = false;
    public boolean shaders = false;
    public boolean shadersAODisable = false;
    public EaglerDeferredConfig deferredShaderConf = new EaglerDeferredConfig();
    public boolean hasReadIt = false;
    public boolean enableProfanityFilter = false;
    public boolean hideDefaultUsernameWarning = false;
    public boolean hasShownProfanityFilter = false;
    public int voicePTTKey = 47; 
    public int voiceListenRadius = 16;
    public float voiceListenVolume = 0.5f;
    public float voiceSpeakVolume = 0.5f;

   public GameSettings(Minecraft mcIn, VFile2 mcDataDir) {
      this.mc = mcIn;
      this.optionsFile = new VFile2(mcDataDir, "options.txt");
      if (mcIn.isJava64bit() && true) {
         AbstractOption.RENDER_DISTANCE.func_216728_a(32.0F);
      } else {
         AbstractOption.RENDER_DISTANCE.func_216728_a(16.0F);
      }

      this.renderDistanceChunks = mcIn.isJava64bit() ? 8 : 4;
      this.screenRecordCodec = ScreenRecordingController.getDefaultCodec();
      this.loadOptions();
   }

   public float func_216840_a(float p_216840_1_) {
      return this.accessibilityTextBackground ? p_216840_1_ : (float)this.accessibilityTextBackgroundOpacity;
   }

   public int func_216841_b(float p_216841_1_) {
      return (int)(this.func_216840_a(p_216841_1_) * 255.0F) << 24 & -16777216;
   }

   public int func_216839_a(int p_216839_1_) {
      return this.accessibilityTextBackground ? p_216839_1_ : (int)(this.accessibilityTextBackgroundOpacity * 255.0D) << 24 & -16777216;
   }

   public void setKeyBindingCode(KeyBinding keyBindingIn, InputMappings.Input inputIn) {
      keyBindingIn.bind(inputIn);
      this.saveOptions();
   }

   public void loadOptions() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         this.soundLevels.clear();
         List<String> list = IOUtils.readLines(this.optionsFile.getInputStream(), Charsets.UTF_8);
         CompoundNBT compoundnbt = new CompoundNBT();

         for(String s : list) {
            try {
               int idx = s.indexOf(':');
               if (idx <= 0) {
                  if (!s.trim().isEmpty()) {
                     LOGGER.warn("Skipping bad option: {}", (Object)s);
                  }
                  continue;
               }

               compoundnbt.putString(s.substring(0, idx), s.substring(idx + 1));
            } catch (Exception var10) {
               LOGGER.warn("Skipping bad option: {}", (Object)s);
            }
         }

         String eaglerProfileName = compoundnbt.getString("eaglerProfileName");
         String eaglerProfileSkin = compoundnbt.getString("eaglerProfileSkin");
         String eaglerCape = compoundnbt.getString("eaglerCape");
         compoundnbt = this.dataFix(compoundnbt);

         if (!eaglerProfileName.isEmpty()) {
             compoundnbt.putString("eaglerProfileName", eaglerProfileName);
         }
         if (!eaglerProfileSkin.isEmpty()) {
             compoundnbt.putString("eaglerProfileSkin", eaglerProfileSkin);
         }

         if (!eaglerCape.isEmpty()) {
            compoundnbt.putString("eaglerCape", eaglerCape);
         }

         for(String s1 : compoundnbt.keySet()) {
            String s2 = compoundnbt.getString(s1);

            try {
               if ("eaglerProfileName".equals(s1)) {
                  net.lax1dude.eaglercraft.profile.EaglerProfile.username = s2;
               }

               if ("eaglerCape".equals(s1)) {
                  EaglerProfile.presetCapeId = Integer.parseInt(s2);
               }

               if ("eaglerProfileSkin".equals(s1)) {
                  net.lax1dude.eaglercraft.profile.EaglerProfile.presetSkinId = Integer.parseInt(s2);
               }

                if ("hasReadIt".equals(s1)) {
                    this.hasReadIt = "true".equals(s2);
                }

                if ("enableProfanityFilter".equals(s1)) {
                    this.enableProfanityFilter = "true".equals(s2);
                }

                if ("hasShownProfanityFilter".equals(s1)) {
                    this.hasShownProfanityFilter = "true".equals(s2);
                }

                if ("hideDefaultUsernameWarning".equals(s1)) {
                    this.hideDefaultUsernameWarning = "true".equals(s2);
                }

                if ("voicePTTKey".equals(s1)) {
                    this.voicePTTKey = Integer.parseInt(s2);
                }

                if ("voiceListenRadius".equals(s1)) {
                    this.voiceListenRadius = Integer.parseInt(s2);
                }

                if ("voiceListenVolume".equals(s1)) {
                    this.voiceListenVolume = Float.parseFloat(s2);
                }

                if ("voiceSpeakVolume".equals(s1)) {
                    this.voiceSpeakVolume = Float.parseFloat(s2);
                }

               if ("autoJump".equals(s1)) {
                  AbstractOption.AUTO_JUMP.set(this, s2);
               }

                if ("socialFeatures".equals(s1)) {
                    AbstractOption.SOCIAL_FEATURES.set(this, s2);
                }

               if ("chunkFix".equals(s1)){
                  AbstractOption.CHUNK_FIX.set(this, s2);
               }

                if ("showFps".equals(s1)){
                    AbstractOption.SHOW_FPS.set(this, s2);
                }
                if ("showXYZ".equals(s1)){
                    AbstractOption.SHOW_XYZ.set(this, s2);
                }
                if ("hudWorld".equals(s1)){
                    this.hudWorld = "true".equals(s2);
                }
                if ("hudStats".equals(s1)){
                    this.hudStats = "true".equals(s2);
                }

               if ("enableFNAWSkins".equals(s1)) {
                  this.enableFNAWSkins = "true".equals(s2);
               }

               if ("autoSuggestions".equals(s1)) {
                  AbstractOption.AUTO_SUGGEST_COMMANDS.set(this, s2);
               }

               if ("chatColors".equals(s1)) {
                  AbstractOption.CHAT_COLOR.set(this, s2);
               }

               if ("chatLinks".equals(s1)) {
                  AbstractOption.CHAT_LINKS.set(this, s2);
               }

               if ("chatLinksPrompt".equals(s1)) {
                  AbstractOption.CHAT_LINKS_PROMPT.set(this, s2);
               }

               if ("enableVsync".equals(s1)) {
                  AbstractOption.VSYNC.set(this, s2);
               }

               if ("hideVideoSettingsWarning".equals(s1)) {
                  this.hideVideoSettingsWarning = "true".equals(s2);
               }

               if ("entityShadows".equals(s1)) {
                  AbstractOption.ENTITY_SHADOWS.set(this, s2);
               }

               if ("forceUnicodeFont".equals(s1)) {
                  AbstractOption.FORCE_UNICODE_FONT.set(this, s2);
               }

               if ("discrete_mouse_scroll".equals(s1)) {
                  AbstractOption.DISCRETE_MOUSE_SCROLL.set(this, s2);
               }

               if ("invertYMouse".equals(s1)) {
                  AbstractOption.INVERT_MOUSE.set(this, s2);
               }

               if ("realmsNotifications".equals(s1)) {
                  AbstractOption.REALMS_NOTIFICATIONS.set(this, s2);
               }

               if ("reducedDebugInfo".equals(s1)) {
                  AbstractOption.REDUCED_DEBUG_INFO.set(this, s2);
               }

               if ("showSubtitles".equals(s1)) {
                  AbstractOption.SHOW_SUBTITLES.set(this, s2);
               }

               if ("snooperEnabled".equals(s1)) {
                  AbstractOption.SNOOPER.set(this, s2);
               }

               if ("touchscreen".equals(s1)) {
                  AbstractOption.TOUCHSCREEN.set(this, s2);
               }

               if ("fullscreen".equals(s1)) {
                  AbstractOption.FULLSCREEN.set(this, s2);
               }

               if ("bobView".equals(s1)) {
                  AbstractOption.VIEW_BOBBING.set(this, s2);
               }

               if ("mouseSensitivity".equals(s1)) {
                  this.mouseSensitivity = (double)parseFloat(s2);
               }

               if ("fov".equals(s1)) {
                  this.fov = (double)(parseFloat(s2) * 40.0F + 70.0F);
               }

               if ("gamma".equals(s1)) {
                  this.gamma = (double)parseFloat(s2);
               }

               if ("renderDistance".equals(s1)) {
                  this.renderDistanceChunks = Integer.parseInt(s2);
               }

               if ("guiScale".equals(s1)) {
                  this.guiScale = Integer.parseInt(s2);
               }

               if ("particles".equals(s1)) {
                  this.particles = ParticleStatus.byId(Integer.parseInt(s2));
               }

if ("maxFps".equals(s1)) {
                   this.framerateLimit = Integer.parseInt(s2);
                   if (this.mc.mainWindow != null) {
                      this.mc.mainWindow.setFramerateLimit(this.framerateLimit);
                   }
                }

                if ("updatesPerFrame".equals(s1)) {
                   this.updatesPerFrame = Integer.parseInt(s2);
                }

                if ("fastEntityRender".equals(s1)) {
                   AbstractOption.FAST_ENTITY_RENDER.set(this, s2);
                }

                if ("fastTileEntityRender".equals(s1)) {
                   AbstractOption.FAST_TILEENTITY_RENDER.set(this, s2);
                }

                if ("disableWeather".equals(s1)) {
                   AbstractOption.DISABLE_WEATHER.set(this, s2);
                }

                if ("fog".equals(s1)) {
                   AbstractOption.FOG.set(this, s2);
                }

               if ("difficulty".equals(s1)) {
                  this.difficulty = Difficulty.byId(Integer.parseInt(s2));
               }

               if ("fancyGraphics".equals(s1)) {
                  this.fancyGraphics = "true".equals(s2);
               }

               if ("tutorialStep".equals(s1)) {
                  this.tutorialStep = TutorialSteps.byName(s2);
               }

               if ("ao".equals(s1)) {
                  if ("true".equals(s2)) {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.MAX;
                  } else if ("false".equals(s2)) {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.OFF;
                  } else {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.func_216570_a(Integer.parseInt(s2));
                  }
               }

               if ("renderClouds".equals(s1)) {
                  if ("true".equals(s2)) {
                     this.cloudOption = CloudOption.FANCY;
                  } else if ("false".equals(s2)) {
                     this.cloudOption = CloudOption.OFF;
                  } else if ("fast".equals(s2)) {
                     this.cloudOption = CloudOption.FAST;
                  }
               }

               if ("attackIndicator".equals(s1)) {
                  this.attackIndicator = AttackIndicatorStatus.byId(Integer.parseInt(s2));
               }

               if ("resourcePacks".equals(s1)) {
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if ("incompatibleResourcePacks".equals(s1)) {
                  if (this.incompatibleResourcePacks == null) {
                     this.incompatibleResourcePacks = Lists.newArrayList();
                  }
               }

               if ("lastServer".equals(s1)) {
                  this.lastServer = s2;
               }

               if ("lang".equals(s1)) {
                  this.language = s2;
               }

               if ("chatVisibility".equals(s1)) {
                  this.chatVisibility = ChatVisibility.func_221252_a(Integer.parseInt(s2));
               }

               if ("chatOpacity".equals(s1)) {
                  this.chatOpacity = (double)parseFloat(s2);
               }

               if ("textBackgroundOpacity".equals(s1)) {
                  this.accessibilityTextBackgroundOpacity = (double)parseFloat(s2);
               }

               if ("backgroundForChatOnly".equals(s1)) {
                  this.accessibilityTextBackground = "true".equals(s2);
               }

               if ("fullscreenResolution".equals(s1)) {
                  this.fullscreenResolution = s2;
               }

               if ("hideServerAddress".equals(s1)) {
                  this.hideServerAddress = "true".equals(s2);
               }

               if ("advancedItemTooltips".equals(s1)) {
                  this.advancedItemTooltips = "true".equals(s2);
               }

               if ("pauseOnLostFocus".equals(s1)) {
                  this.pauseOnLostFocus = "true".equals(s2);
               }

               if ("overrideHeight".equals(s1)) {
                  this.overrideHeight = Integer.parseInt(s2);
               }

               if ("overrideWidth".equals(s1)) {
                  this.overrideWidth = Integer.parseInt(s2);
               }

               if ("heldItemTooltips".equals(s1)) {
                  this.heldItemTooltips = "true".equals(s2);
               }

               if ("chatHeightFocused".equals(s1)) {
                  this.chatHeightFocused = (double)parseFloat(s2);
               }

               if ("chatHeightUnfocused".equals(s1)) {
                  this.chatHeightUnfocused = (double)parseFloat(s2);
               }

               if ("chatScale".equals(s1)) {
                  this.chatScale = (double)parseFloat(s2);
               }

               if ("chatWidth".equals(s1)) {
                  this.chatWidth = (double)parseFloat(s2);
               }

               if ("mipmapLevels".equals(s1)) {
                  this.mipmapLevels = Integer.parseInt(s2);
               }

               if ("useNativeTransport".equals(s1)) {
                  this.useNativeTransport = "true".equals(s2);
               }

               if ("mainHand".equals(s1)) {
                  this.mainHand = "left".equals(s2) ? HandSide.LEFT : HandSide.RIGHT;
               }

               if ("narrator".equals(s1)) {
                  this.narrator = NarratorStatus.byId(Integer.parseInt(s2));
               }

               if ("biomeBlendRadius".equals(s1)) {
                  this.biomeBlendRadius = Integer.parseInt(s2);
               }

               if ("mouseWheelSensitivity".equals(s1)) {
                  this.mouseWheelSensitivity = (double)parseFloat(s2);
               }

               if ("rawMouseInput".equals(s1)) {
                  this.field_225307_E = "true".equals(s2);
               }

                if ("glDebugVerbosity".equals(s1)) {
                   this.glDebugVerbosity = Integer.parseInt(s2);
                }

                if ("screenRecordCodec".equals(s1)) {
                   try {
                      EnumScreenRecordingCodec codec = EnumScreenRecordingCodec.valueOf(s2);
                      if (ScreenRecordingController.codecs.contains(codec)) {
                         this.screenRecordCodec = codec;
                      }
                   } catch (Exception var12) {
                   }
                }

                if ("screenRecordFPS".equals(s1)) {
                   this.screenRecordFPS = Integer.parseInt(s2);
                }

                if ("screenRecordResolution".equals(s1)) {
                   this.screenRecordResolution = Integer.parseInt(s2);
                }

                if ("screenRecordAudioBitrate".equals(s1)) {
                   this.screenRecordAudioBitrate = Integer.parseInt(s2);
                }

                if ("screenRecordVideoBitrate".equals(s1)) {
                   this.screenRecordVideoBitrate = Integer.parseInt(s2);
                }

                if ("screenRecordGameVolume".equals(s1)) {
                   this.screenRecordGameVolume = parseFloat(s2);
                }

                if ("screenRecordMicVolume".equals(s1)) {
                   this.screenRecordMicVolume = parseFloat(s2);
                }

                if ("shaders".equals(s1)) {
                   this.shaders = "true".equals(s2);
                }

                if ("shadersAODisable".equals(s1)) {
                   this.shadersAODisable = "true".equals(s2);
                }

                deferredShaderConf.readOption(s1, s2);

                for(KeyBinding keybinding : this.keyBindings) {
                  if (s1.equals("key_" + keybinding.getKeyDescription())) {
                     keybinding.bind(InputMappings.getInputByName(s2));
                  }
               }

               for(SoundCategory soundcategory : SoundCategory.values()) {
                  if (s1.equals("soundCategory_" + soundcategory.getName())) {
                     this.soundLevels.put(soundcategory, parseFloat(s2));
                  }
               }

               for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
                  if (s1.equals("modelPart_" + playermodelpart.getPartName())) {
                     this.setModelPartEnabled(playermodelpart, "true".equals(s2));
                  }
               }
            } catch (Exception var11) {
               LOGGER.warn("Skipping bad option: {}:{}", s1, s2);
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
         if (this.mc.getRenderManager() != null) {
            this.mc.getRenderManager().setEnableFNAWSkins(this.enableFNAWSkins);

	         if (this.shaders && !net.lax1dude.eaglercraft.opengl.ext.deferred.EaglerDeferredPipeline.isSupported()) {
	            LOGGER.error("Setting shaders to false because they are not supported");
	            this.shaders = false;
	         }
         }
      } catch (Exception exception) {
         LOGGER.error("Failed to load options", (Throwable)exception);
      }

   }

   private CompoundNBT dataFix(CompoundNBT nbt) {
      int i = 0;

      try {
         i = Integer.parseInt(nbt.getString("version"));
      } catch (RuntimeException var4) {
         ;
      }

      return NBTUtil.update(this.mc.getDataFixer(), DefaultTypeReferences.OPTIONS, nbt, i);
   }

   private static float parseFloat(String p_74305_0_) {
      if ("true".equals(p_74305_0_)) {
         return 1.0F;
      } else {
         return "false".equals(p_74305_0_) ? 0.0F : Float.parseFloat(p_74305_0_);
      }
   }

   public void saveOptions() {
      try (PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(this.optionsFile.getOutputStream(), StandardCharsets.UTF_8))) {
         printwriter.println("version:" + SharedConstants.getVersion().getWorldVersion());
         printwriter.println("eaglerProfileName:" + net.lax1dude.eaglercraft.profile.EaglerProfile.username);
         printwriter.println("eaglerProfileSkin:" + net.lax1dude.eaglercraft.profile.EaglerProfile.presetSkinId);
         printwriter.println("eaglerCape:" + EaglerProfile.presetCapeId);
          printwriter.println("autoJump:" + AbstractOption.AUTO_JUMP.get(this));
          printwriter.println("socialFeatures:" + AbstractOption.SOCIAL_FEATURES.get(this));
          printwriter.println("chunkFix:" + AbstractOption.CHUNK_FIX.get(this));
          printwriter.println("showFps:" + AbstractOption.SHOW_FPS.get(this));
          printwriter.println("hasReadIt:" + this.hasReadIt);
          printwriter.println("enableProfanityFilter:" + this.enableProfanityFilter);
          printwriter.println("hasShownProfanityFilter:" + this.hasShownProfanityFilter);
          printwriter.println("hideDefaultUsernameWarning:" + this.hideDefaultUsernameWarning);
          printwriter.println("voicePTTKey:" + this.voicePTTKey);
          printwriter.println("voiceListenRadius:" + this.voiceListenRadius);
          printwriter.println("voiceListenVolume:" + this.voiceListenVolume);
          printwriter.println("voiceSpeakVolume:" + this.voiceSpeakVolume);
          printwriter.println("showXYZ:" + AbstractOption.SHOW_XYZ.get(this));
          printwriter.println("hudWorld:" + this.hudWorld);
          printwriter.println("hudStats:" + this.hudStats);
          printwriter.println("enableFNAWSkins:" + this.enableFNAWSkins);
         printwriter.println("autoSuggestions:" + AbstractOption.AUTO_SUGGEST_COMMANDS.get(this));
         printwriter.println("chatColors:" + AbstractOption.CHAT_COLOR.get(this));
         printwriter.println("chatLinks:" + AbstractOption.CHAT_LINKS.get(this));
         printwriter.println("chatLinksPrompt:" + AbstractOption.CHAT_LINKS_PROMPT.get(this));
         printwriter.println("enableVsync:" + AbstractOption.VSYNC.get(this));
         printwriter.println("hideVideoSettingsWarning:" + this.hideVideoSettingsWarning);
         printwriter.println("entityShadows:" + AbstractOption.ENTITY_SHADOWS.get(this));
         printwriter.println("forceUnicodeFont:" + AbstractOption.FORCE_UNICODE_FONT.get(this));
         printwriter.println("discrete_mouse_scroll:" + AbstractOption.DISCRETE_MOUSE_SCROLL.get(this));
         printwriter.println("invertYMouse:" + AbstractOption.INVERT_MOUSE.get(this));
         printwriter.println("realmsNotifications:" + AbstractOption.REALMS_NOTIFICATIONS.get(this));
         printwriter.println("reducedDebugInfo:" + AbstractOption.REDUCED_DEBUG_INFO.get(this));
         printwriter.println("snooperEnabled:" + AbstractOption.SNOOPER.get(this));
         printwriter.println("showSubtitles:" + AbstractOption.SHOW_SUBTITLES.get(this));
         printwriter.println("touchscreen:" + AbstractOption.TOUCHSCREEN.get(this));
         printwriter.println("fullscreen:" + AbstractOption.FULLSCREEN.get(this));
         printwriter.println("bobView:" + AbstractOption.VIEW_BOBBING.get(this));
         printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
         printwriter.println("fov:" + (this.fov - 70.0D) / 40.0D);
         printwriter.println("gamma:" + this.gamma);
         printwriter.println("renderDistance:" + this.renderDistanceChunks);
         printwriter.println("guiScale:" + this.guiScale);
         printwriter.println("particles:" + this.particles.func_216832_b());
         printwriter.println("maxFps:" + this.framerateLimit);
printwriter.println("updatesPerFrame:" + this.updatesPerFrame);
          printwriter.println("fastEntityRender:" + AbstractOption.FAST_ENTITY_RENDER.get(this));
          printwriter.println("fastTileEntityRender:" + AbstractOption.FAST_TILEENTITY_RENDER.get(this));
          printwriter.println("disableWeather:" + AbstractOption.DISABLE_WEATHER.get(this));
          printwriter.println("fog:" + AbstractOption.FOG.get(this));
          printwriter.println("difficulty:" + this.difficulty.getId());
         printwriter.println("fancyGraphics:" + this.fancyGraphics);
         printwriter.println("ao:" + this.ambientOcclusionStatus.func_216572_a());
         printwriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
         switch(this.cloudOption) {
         case FANCY:
            printwriter.println("renderClouds:true");
            break;
         case FAST:
            printwriter.println("renderClouds:fast");
            break;
         case OFF:
            printwriter.println("renderClouds:false");
         }

         printwriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
         printwriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
         printwriter.println("lastServer:" + this.lastServer);
         printwriter.println("lang:" + this.language);
         printwriter.println("chatVisibility:" + this.chatVisibility.func_221254_a());
         printwriter.println("chatOpacity:" + this.chatOpacity);
         printwriter.println("textBackgroundOpacity:" + this.accessibilityTextBackgroundOpacity);
         printwriter.println("backgroundForChatOnly:" + this.accessibilityTextBackground);

         printwriter.println("hideServerAddress:" + this.hideServerAddress);
         printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
         printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
         printwriter.println("overrideWidth:" + this.overrideWidth);
         printwriter.println("overrideHeight:" + this.overrideHeight);
         printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
         printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
         printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
         printwriter.println("chatScale:" + this.chatScale);
         printwriter.println("chatWidth:" + this.chatWidth);
         printwriter.println("mipmapLevels:" + this.mipmapLevels);
         printwriter.println("useNativeTransport:" + this.useNativeTransport);
         printwriter.println("mainHand:" + (this.mainHand == HandSide.LEFT ? "left" : "right"));
         printwriter.println("attackIndicator:" + this.attackIndicator.func_216751_a());
         printwriter.println("narrator:" + this.narrator.func_216827_a());
         printwriter.println("tutorialStep:" + this.tutorialStep.getName());
         printwriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
         printwriter.println("rawMouseInput:" + AbstractOption.field_225302_l.get(this));
         printwriter.println("glDebugVerbosity:" + this.glDebugVerbosity);

         for(KeyBinding keybinding : this.keyBindings) {
            printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getTranslationKey());
         }

         for(SoundCategory soundcategory : SoundCategory.values()) {
            printwriter.println("soundCategory_" + soundcategory.getName() + ":" + this.getSoundLevel(soundcategory));
         }

         for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
             printwriter.println("modelPart_" + playermodelpart.getPartName() + ":" + this.setModelParts.contains(playermodelpart));
          }
          if (screenRecordCodec != null) {
             printwriter.println("screenRecordCodec:" + this.screenRecordCodec);
          }
          printwriter.println("screenRecordFPS:" + this.screenRecordFPS);
          printwriter.println("screenRecordResolution:" + this.screenRecordResolution);
          printwriter.println("screenRecordAudioBitrate:" + this.screenRecordAudioBitrate);
          printwriter.println("screenRecordVideoBitrate:" + this.screenRecordVideoBitrate);
          printwriter.println("screenRecordGameVolume:" + this.screenRecordGameVolume);
          printwriter.println("screenRecordMicVolume:" + this.screenRecordMicVolume);
          printwriter.println("shaders:" + this.shaders);
          printwriter.println("shadersAODisable:" + this.shadersAODisable);
          deferredShaderConf.writeOptions(printwriter);
       } catch (Exception exception) {
         LOGGER.error("Failed to save options", (Throwable)exception);
      }

      this.sendSettingsToServer();
   }

   public int checkBadVideoSettings() {
      return hideVideoSettingsWarning ? 0
            : ((renderDistanceChunks > 6 ? GuiScreenVideoSettingsWarning.WARNING_RENDER_DISTANCE : 0)
                  | (!vsync ? GuiScreenVideoSettingsWarning.WARNING_VSYNC : 0)
                  | (framerateLimit < 30 ? GuiScreenVideoSettingsWarning.WARNING_FRAME_LIMIT : 0));
   }

   public void fixBadVideoSettings() {
      if (renderDistanceChunks > 6)
         renderDistanceChunks = 4;
      if (!vsync)
         vsync = true;
      if (framerateLimit < 30)
         framerateLimit = 260;
   }

   public float getSoundLevel(SoundCategory category) {
      return this.soundLevels.containsKey(category) ? this.soundLevels.get(category) : 1.0F;
   }

   public void setSoundLevel(SoundCategory category, float volume) {
      this.soundLevels.put(category, volume);
      this.mc.getSoundHandler().setSoundLevel(category, volume);
   }

   public void sendSettingsToServer() {
      if (this.mc.player != null) {
         int i = 0;

         for(PlayerModelPart playermodelpart : this.setModelParts) {
            i |= playermodelpart.getPartMask();
         }

         this.mc.player.connection.sendPacket(new CClientSettingsPacket(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColor, i, this.mainHand));
      }

   }

   public Set<PlayerModelPart> getModelParts() {
      return ImmutableSet.copyOf(this.setModelParts);
   }

   public void setModelPartEnabled(PlayerModelPart modelPart, boolean enable) {
      if (enable) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public void switchModelPartEnabled(PlayerModelPart modelPart) {
      if (this.getModelParts().contains(modelPart)) {
         this.setModelParts.remove(modelPart);
      } else {
         this.setModelParts.add(modelPart);
      }

      this.sendSettingsToServer();
   }

   public CloudOption getCloudOption() {
      return this.renderDistanceChunks >= 4 ? this.cloudOption : CloudOption.OFF;
   }

   public boolean isUsingNativeTransport() {
      return this.useNativeTransport;
   }

   public void fillResourcePackList(ResourcePackList<ClientResourcePackInfo> resourcePackListIn) {
      resourcePackListIn.reloadPacksFromFinders();
      Set<ClientResourcePackInfo> set = Sets.newLinkedHashSet();
      Iterator<String> iterator = this.resourcePacks.iterator();

      while(iterator.hasNext()) {
         String s = iterator.next();
         ClientResourcePackInfo clientresourcepackinfo = resourcePackListIn.getPackInfo(s);
         if (clientresourcepackinfo == null && !s.startsWith("file/")) {
            clientresourcepackinfo = resourcePackListIn.getPackInfo("file/" + s);
         }

         if (clientresourcepackinfo == null) {
            LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)s);
            iterator.remove();
         } else if (!clientresourcepackinfo.getCompatibility().func_198968_a() && !this.incompatibleResourcePacks.contains(s)) {
            LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)s);
            iterator.remove();
         } else if (clientresourcepackinfo.getCompatibility().func_198968_a() && this.incompatibleResourcePacks.contains(s)) {
            LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)s);
            this.incompatibleResourcePacks.remove(s);
         } else {
            set.add(clientresourcepackinfo);
         }
      }

      resourcePackListIn.setEnabledPacks(set);
   }
}

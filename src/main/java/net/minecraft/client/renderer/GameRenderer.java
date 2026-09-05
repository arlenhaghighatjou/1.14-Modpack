package net.minecraft.client.renderer;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.Random;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager;
import net.lax1dude.eaglercraft.opengl.ext.deferred.EaglerDeferredConfig;
import net.lax1dude.eaglercraft.opengl.ext.deferred.EaglerDeferredPipeline;
import net.lax1dude.eaglercraft.opengl.ext.deferred.NameTagRenderer;
import net.lax1dude.eaglercraft.opengl.ext.deferred.ShaderPackInfoReloadListener;
import net.lax1dude.eaglercraft.opengl.ext.deferred.program.SharedPipelineShaders;
import net.lax1dude.eaglercraft.opengl.ext.deferred.texture.EmissiveItems;
import net.lax1dude.eaglercraft.vector.Vector4f;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.SimpleResource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GameRenderer implements AutoCloseable, IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation RAIN_TEXTURES = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation SNOW_TEXTURES = new ResourceLocation("textures/environment/snow.png");
   private static final Predicate<Entity> MOUSE_OVER_PREDICATE = entity ->
         !entity.isSpectator() && entity.canBeCollidedWith();
   private final Minecraft mc;
   private final IResourceManager resourceManager;
   private final Random random = new Random();
   private float farPlaneDistance;
   public final FirstPersonRenderer itemRenderer;
   private final MapItemRenderer mapItemRenderer;
   private int rendererUpdateCount;
   private float fovModifierHand;
   private float fovModifierHandPrev;
   private float bossColorModifier;
   private float bossColorModifierPrev;
   private final Frustum cachedFrustum = new Frustum();
   private boolean renderHand = true;
   private boolean drawBlockOutline = true;
   private long timeWorldIcon;
   private String lastWorldFolderName = null;
   private long prevFrameTime = Util.milliTime();
   private final LightTexture lightmapTexture;
   private int rainSoundCounter;
   private final float[] rainXCoords = new float[1024];
   private final float[] rainYCoords = new float[1024];
   private final BlockPos.MutableBlockPos weatherRenderPos = new BlockPos.MutableBlockPos();
   private final List<Entity> mouseOverCandidates = new ArrayList<>(16);
   private final FogRenderer fogRenderer;
   private boolean debugView;
   private double cameraZoom = 1.0D;
   private double cameraYaw;
   private double cameraPitch;
   private ItemStack itemActivationItem;
   private int itemActivationTicks;
   private float itemActivationOffX;
   private float itemActivationOffY;
   private ShaderGroup shaderGroup;
   private static final ResourceLocation[] SHADERS_TEXTURES = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
   public static final int SHADER_COUNT = SHADERS_TEXTURES.length;
   private int shaderIndex = SHADER_COUNT;
   private boolean useShader;
   private int frameCount;
   private final ActiveRenderInfo activeRender = new ActiveRenderInfo();
   private float eagPartialTicks = 0.0f;
   private boolean eaglerShadersInitialized = false;
   private int shadowFrameIndex = 0;
   private double blockWaveOffsetX = 0.0;
   private double blockWaveOffsetY = 0.0;
   private double blockWaveOffsetZ = 0.0;
   private final Vector4f tmpVec4f_1 = new Vector4f();

   public GameRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
      this.mc = mcIn;
      this.resourceManager = resourceManagerIn;
      this.itemRenderer = mcIn.getFirstPersonRenderer();
      this.mapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
      this.lightmapTexture = new LightTexture(this);
      this.fogRenderer = new FogRenderer(this);
      this.shaderGroup = null;

      for(int i = 0; i < 32; ++i) {
         for(int j = 0; j < 32; ++j) {
            float f = (float)(j - 16);
            float f1 = (float)(i - 16);
            float f2 = MathHelper.sqrt(f * f + f1 * f1);
            this.rainXCoords[i << 5 | j] = -f1 / f2;
            this.rainYCoords[i << 5 | j] = f / f2;
         }
      }

   }

   public void close() {
      this.lightmapTexture.close();
      this.mapItemRenderer.close();
      this.stopUseShader();
   }

   public ICamera getRenderFrustum() {
      return this.cachedFrustum;
   }

   public boolean isShaderActive() {
      return GLX.usePostProcess && this.shaderGroup != null;
   }

   public void stopUseShader() {
      if (this.shaderGroup != null) {
         this.shaderGroup.close();
      }

      this.shaderGroup = null;
      this.shaderIndex = SHADER_COUNT;
   }

   public void switchUseShader() {
      this.useShader = !this.useShader;
   }

   public void loadEntityShader( Entity entityIn) {
      if (GLX.usePostProcess) {
         if (this.shaderGroup != null) {
            this.shaderGroup.close();
         }

         this.shaderGroup = null;
         if (entityIn instanceof CreeperEntity) {
            this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
         } else if (entityIn instanceof SpiderEntity) {
            this.loadShader(new ResourceLocation("shaders/post/spider.json"));
         } else if (entityIn instanceof EndermanEntity) {
            this.loadShader(new ResourceLocation("shaders/post/invert.json"));
         }

      }
   }

   private void loadShader(ResourceLocation resourceLocationIn) {
      if (this.shaderGroup != null) {
         this.shaderGroup.close();
      }

      try {
         this.shaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn);
         this.shaderGroup.createBindFramebuffers(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
         this.useShader = true;
      } catch (IOException ioexception) {
         LOGGER.warn("Failed to load shader: {}", resourceLocationIn, ioexception);
         this.shaderIndex = SHADER_COUNT;
         this.useShader = false;
      } catch (JsonSyntaxException jsonsyntaxexception) {
         LOGGER.warn("Failed to load shader: {}", resourceLocationIn, jsonsyntaxexception);
         this.shaderIndex = SHADER_COUNT;
         this.useShader = false;
      }

   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      if (this.shaderGroup != null) {
         this.shaderGroup.close();
      }

      this.shaderGroup = null;
      if (this.shaderIndex == SHADER_COUNT) {
         this.loadEntityShader(this.mc.getRenderViewEntity());
      } else {
         this.loadShader(SHADERS_TEXTURES[this.shaderIndex]);
      }

   }

   public void tick() {
      if (GLX.usePostProcess && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
         ShaderLinkHelper.setNewStaticShaderLinkHelper();
      }

      this.updateFovModifierHand();
      this.lightmapTexture.tick();
      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.player);
      }

      this.activeRender.interpolateHeight();
      ++this.rendererUpdateCount;
      this.itemRenderer.tick();
      this.addRainParticles();
      this.bossColorModifierPrev = this.bossColorModifier;
      if (this.mc.ingameGUI.getBossOverlay().shouldDarkenSky()) {
         this.bossColorModifier += 0.05F;
         if (this.bossColorModifier > 1.0F) {
            this.bossColorModifier = 1.0F;
         }
      } else if (this.bossColorModifier > 0.0F) {
         this.bossColorModifier -= 0.0125F;
      }

      if (this.itemActivationTicks > 0) {
         --this.itemActivationTicks;
         if (this.itemActivationTicks == 0) {
            this.itemActivationItem = null;
         }
      }

   }

   public ShaderGroup getShaderGroup() {
      return this.shaderGroup;
   }

   public void updateShaderGroupSize(int width, int height) {
      if (GLX.usePostProcess) {
         if (this.shaderGroup != null) {
            this.shaderGroup.createBindFramebuffers(width, height);
         }

         this.mc.worldRenderer.createBindEntityOutlineFbs(width, height);
      }
   }

   public void getMouseOver(float partialTicks) {
      Entity entity = this.mc.getRenderViewEntity();
      if (entity != null) {
         if (this.mc.world != null) {
            this.mc.getProfiler().startSection("pick");
            this.mc.pointedEntity = null;
            double d0 = (double)this.mc.playerController.getBlockReachDistance();
            this.mc.objectMouseOver = entity.func_213324_a(d0, partialTicks, false);
            Vec3d vec3d = entity.getEyePosition(partialTicks);
            boolean flag = false;
            int i = 3;
            double d1 = d0;
            if (this.mc.playerController.extendedReach()) {
               d1 = 6.0D;
               d0 = d1;
            } else {
               if (d0 > 3.0D) {
                  flag = true;
               }

               d0 = d0;
            }

            d1 = d1 * d1;
            if (this.mc.objectMouseOver != null) {
               d1 = this.mc.objectMouseOver.getHitVec().squareDistanceTo(vec3d);
            }

            Vec3d vec3d1 = entity.getLook(1.0F);
            Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
            float f = 1.0F;
            AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vec3d1.x * d0,
                  vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D);
            this.mouseOverCandidates.clear();
            this.mc.world.getEntitiesInAABBexcluding(entity, axisalignedbb, MOUSE_OVER_PREDICATE, this.mouseOverCandidates);
            EntityRayTraceResult entityraytraceresult = ProjectileHelper.func_221273_a(entity, vec3d, vec3d2,
                  this.mouseOverCandidates, d1);
            if (entityraytraceresult != null) {
               Entity entity1 = entityraytraceresult.getEntity();
               Vec3d vec3d3 = entityraytraceresult.getHitVec();
               double d2 = vec3d.squareDistanceTo(vec3d3);
               if (flag && d2 > 9.0D) {
                  this.mc.objectMouseOver = BlockRayTraceResult.createMiss(vec3d3, Direction.getFacingFromVector(vec3d1.x, vec3d1.y, vec3d1.z), new BlockPos(vec3d3));
               } else if (d2 < d1 || this.mc.objectMouseOver == null) {
                  this.mc.objectMouseOver = entityraytraceresult;
                  if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrameEntity) {
                     this.mc.pointedEntity = entity1;
                  }
               }
            }

            this.mc.getProfiler().endSection();
         }
      }
   }

   private void updateFovModifierHand() {
      float f = 1.0F;
      if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayerEntity) {
         AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity)this.mc.getRenderViewEntity();
         f = abstractclientplayerentity.getFovModifier();
      }

      this.fovModifierHandPrev = this.fovModifierHand;
      this.fovModifierHand += (f - this.fovModifierHand) * 0.5F;
      if (this.fovModifierHand > 1.5F) {
         this.fovModifierHand = 1.5F;
      }

      if (this.fovModifierHand < 0.1F) {
         this.fovModifierHand = 0.1F;
      }

   }

   private double zoomProgress = 1.0D;
   public double zoomTarget = 0.25D;
   private double getFOVModifier(ActiveRenderInfo p_215311_1_, float p_215311_2_, boolean p_215311_3_) {
      if (this.debugView) {
         return 90.0D;
      } else {
         double d0 = 70.0D;
         if (p_215311_3_) {
            d0 = this.mc.gameSettings.fov;
            d0 = d0 * (double)MathHelper.lerp(p_215311_2_, this.fovModifierHandPrev, this.fovModifierHand);
         }

         if (p_215311_1_.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)p_215311_1_.getRenderViewEntity()).getHealth() <= 0.0F) {
            float f = (float)((LivingEntity)p_215311_1_.getRenderViewEntity()).deathTime + p_215311_2_;
            d0 /= (double)((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
         }

         IFluidState ifluidstate = p_215311_1_.getFluidState();
         if (!ifluidstate.isEmpty()) {
            d0 = d0 * 60.0D / 70.0D;
         }
         if (this.mc.gameSettings.keyBindZoom.isKeyDown()) {
            this.zoomProgress += (this.zoomTarget - this.zoomProgress) * 0.1D * (double)p_215311_2_;
         } else {
            this.zoomProgress += (1.0D - this.zoomProgress) * 0.1D * (double)p_215311_2_;

            this.zoomTarget = 0.25D;
         }

         d0 *= this.zoomProgress;
         return d0;
      }
   }

   private void hurtCameraEffect(float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)this.mc.getRenderViewEntity();
         float f = (float)livingentity.hurtTime - partialTicks;
         if (livingentity.getHealth() <= 0.0F) {
            float f1 = (float)livingentity.deathTime + partialTicks;
            GlStateManager.rotatef(40.0F - 8000.0F / (f1 + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if (f < 0.0F) {
            return;
         }

         f = f / (float)livingentity.maxHurtTime;
         f = MathHelper.sin(f * f * f * f * (float)Math.PI);
         float f2 = livingentity.attackedAtYaw;
         GlStateManager.rotatef(-f2, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(-f * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
      }

   }

   private void applyBobbing(float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof PlayerEntity) {
         PlayerEntity playerentity = (PlayerEntity)this.mc.getRenderViewEntity();
         float f = playerentity.distanceWalkedModified - playerentity.prevDistanceWalkedModified;
         float f1 = -(playerentity.distanceWalkedModified + f * partialTicks);
         float f2 = MathHelper.lerp(partialTicks, playerentity.prevCameraYaw, playerentity.cameraYaw);
         GlStateManager.translatef(MathHelper.sin(f1 * (float)Math.PI) * f2 * 0.5F, -Math.abs(MathHelper.cos(f1 * (float)Math.PI) * f2), 0.0F);
         GlStateManager.rotatef(MathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(Math.abs(MathHelper.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
      }
   }

   private void setupCameraTransform(float partialTicks) {
      this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      if (this.cameraZoom != 1.0D) {
         GlStateManager.translatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
         GlStateManager.scaled(this.cameraZoom, this.cameraZoom, 1.0D);
      }

      GlStateManager.multMatrix(net.lax1dude.eaglercraft.vector.Matrix4f.perspective(this.getFOVModifier(this.activeRender, partialTicks, true), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * MathHelper.SQRT_2).getElements());
      DeferredStateManager.setGBufferNearFarPlanes(0.05f, this.farPlaneDistance * MathHelper.SQRT_2);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      this.hurtCameraEffect(partialTicks);
      if (this.mc.gameSettings.viewBobbing) {
         this.applyBobbing(partialTicks);
      }

      float f = MathHelper.lerp(partialTicks, this.mc.player.prevTimeInPortal, this.mc.player.timeInPortal);
      if (f > 0.0F) {
         int i = 20;
         if (this.mc.player.isPotionActive(Effects.NAUSEA)) {
            i = 7;
         }

         float f1 = 5.0F / (f * f + 5.0F) - f * 0.04F;
         f1 = f1 * f1;
         GlStateManager.rotatef(((float)this.rendererUpdateCount + partialTicks) * (float)i, 0.0F, 1.0F, 1.0F);
         GlStateManager.scalef(1.0F / f1, 1.0F, 1.0F);
         GlStateManager.rotatef(-((float)this.rendererUpdateCount + partialTicks) * (float)i, 0.0F, 1.0F, 1.0F);
      }

   }

   private void func_215308_a(ActiveRenderInfo p_215308_1_, float p_215308_2_) {
      if (!this.debugView) {
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrix(Matrix4f.perspective(this.getFOVModifier(p_215308_1_, p_215308_2_, false), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * 2.0F).getElements());
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.pushMatrix();
         this.hurtCameraEffect(p_215308_2_);
         if (this.mc.gameSettings.viewBobbing) {
            this.applyBobbing(p_215308_2_);
         }

         boolean flag = this.mc.getRenderViewEntity() instanceof LivingEntity && ((LivingEntity)this.mc.getRenderViewEntity()).isSleeping();
         if (this.mc.gameSettings.thirdPersonView == 0 && !flag && !this.mc.gameSettings.hideGUI && this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR) {
            this.enableLightmap();
            this.itemRenderer.renderItemInFirstPerson(p_215308_2_);
            this.disableLightmap();
         }

         GlStateManager.popMatrix();
         if (this.mc.gameSettings.thirdPersonView == 0 && !flag) {
            this.itemRenderer.renderOverlays(p_215308_2_);
            this.hurtCameraEffect(p_215308_2_);
         }

         if (this.mc.gameSettings.viewBobbing) {
            this.applyBobbing(p_215308_2_);
         }

      }
   }

   public void disableLightmap() {
      this.lightmapTexture.disableLightmap();
   }

   public void enableLightmap() {
      this.lightmapTexture.enableLightmap();
   }

   public float getNightVisionBrightness(LivingEntity entitylivingbaseIn, float partialTicks) {
      int i = entitylivingbaseIn.getActivePotionEffect(Effects.NIGHT_VISION).getDuration();
      return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)i - partialTicks) * (float)Math.PI * 0.2F) * 0.3F;
   }

   public void updateCameraAndRender(float partialTicks, long nanoTime, boolean renderWorldIn) {
      if (!this.mc.isGameFocused() && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !this.mc.mouseHelper.isRightDown())) {
         if (Util.milliTime() - this.prevFrameTime > 500L) {
            this.mc.displayInGameMenu(false);
         }
      } else {
         this.prevFrameTime = Util.milliTime();
      }

      if (!this.mc.skipRenderWorld) {
         int i = (int)(this.mc.mouseHelper.getMouseX() * (double)this.mc.mainWindow.getScaledWidth() / (double)this.mc.mainWindow.getWidth());
         int j = (int)(this.mc.mouseHelper.getMouseY() * (double)this.mc.mainWindow.getScaledHeight() / (double)this.mc.mainWindow.getHeight());
         int k = this.mc.gameSettings.framerateLimit;
         if (renderWorldIn && this.mc.world != null) {
            this.mc.getProfiler().startSection("level");
            int l = Math.min(Minecraft.getDebugFPS(), k);
            l = Math.max(l, 60);
            long i1 = Util.nanoTime() - nanoTime;
            long j1 = Math.max((long)(1000000000 / l / 4) - i1, 0L);
            this.renderWorld(partialTicks, Util.nanoTime() + j1);
            if (this.mc.currentWorldFolderName != null) {
               if (!this.mc.currentWorldFolderName.equals(this.lastWorldFolderName)) {
                  this.lastWorldFolderName = this.mc.currentWorldFolderName;
                  this.timeWorldIcon = Util.milliTime();
               }
               if (this.timeWorldIcon != 0L && Util.milliTime() > this.timeWorldIcon + 7000L) {
                  this.timeWorldIcon = 0L; 
                  this.createWorldIcon();
               }
            } else {
               this.lastWorldFolderName = null;
            }

            if (GLX.usePostProcess) {
               this.mc.worldRenderer.renderEntityOutlineFramebuffer();
               if (this.shaderGroup != null && this.useShader) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.loadIdentity();
                  this.shaderGroup.render(partialTicks);
                  GlStateManager.popMatrix();
               }

               this.mc.getFramebuffer().bindFramebuffer(true);
            }

            this.mc.getProfiler().endStartSection("gui");
            if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
               GlStateManager.alphaFunc(516, 0.1F);
               this.mc.mainWindow.loadGUIRenderMatrix(Minecraft.IS_RUNNING_ON_MAC);
               this.renderItemActivation(this.mc.mainWindow.getScaledWidth(), this.mc.mainWindow.getScaledHeight(), partialTicks);
               this.mc.ingameGUI.renderGameOverlay(partialTicks);
            }

            this.mc.getProfiler().endSection();
         } else {
            GlStateManager.viewport(0, 0, this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.mc.mainWindow.loadGUIRenderMatrix(Minecraft.IS_RUNNING_ON_MAC);
         }

         if (this.mc.loadingGui != null) {
            GlStateManager.clear(256, Minecraft.IS_RUNNING_ON_MAC);
            GlStateManager.disableDepthTest();

            try {
               this.mc.loadingGui.render(i, j, this.mc.getTickLength());
            } catch (Throwable throwable1) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Rendering overlay");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Overlay render details");
               crashreportcategory.addDetail("Overlay name", () -> {
                  return this.mc.loadingGui.getClass().getCanonicalName();
               });
               throw new ReportedException(crashreport);
            }
         } else if (this.mc.currentScreen != null) {
            GlStateManager.clear(256, Minecraft.IS_RUNNING_ON_MAC);
            GlStateManager.disableDepthTest();

            try {
               this.mc.currentScreen.render(i, j, this.mc.getTickLength());
            } catch (Throwable throwable) {
               CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Rendering screen");
               CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Screen render details");
               crashreportcategory1.addDetail("Screen name", () -> {
                  return this.mc.currentScreen.getClass().getCanonicalName();
               });
               crashreportcategory1.addDetail("Mouse location", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", i, j, this.mc.mouseHelper.getMouseX(), this.mc.mouseHelper.getMouseY());
               });
               crashreportcategory1.addDetail("Screen size", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.mc.mainWindow.getScaledWidth(), this.mc.mainWindow.getScaledHeight(), this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight(), this.mc.mainWindow.getGuiScaleFactor());
               });
               throw new ReportedException(crashreport1);
            }
         }

      }
   }

   public void createWorldIcon() {
      String folderName = this.mc.currentWorldFolderName;
      if (folderName != null) {
         int fw = this.mc.mainWindow.getFramebufferWidth();
         int fh = this.mc.mainWindow.getFramebufferHeight();
         NativeImage nativeimage = new NativeImage(fw, fh, false);
         this.mc.getFramebuffer().bindFramebuffer(true);
         nativeimage.downloadFromFramebuffer(true);
         nativeimage.flip();

         SimpleResource.RESOURCE_IO_EXECUTOR.execute(() -> {
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = 0;
            int l = 0;
            if (i > j) {
               k = (i - j) / 2;
               i = j;
            } else {
               l = (j - i) / 2;
               j = i;
            }

            try (NativeImage nativeimage1 = new NativeImage(128, 128, false)) {
               nativeimage.resizeSubRectToBilinear(k, l, i, j, nativeimage1);
               int[] pixels = nativeimage1.makePixelArray();
               java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
               for(int p = 0; p < pixels.length; p++) {
                  int c = pixels[p];
                  baos.write(c & 0xFF);
                  baos.write((c >> 8) & 0xFF);
                  baos.write((c >> 16) & 0xFF);
                  baos.write((c >> 24) & 0xFF);
               }
               net.peyton.eagler.fs.WorldsDB.newVFile(folderName, "icon.raw").setAllBytes(baos.toByteArray());
            } catch (Exception ioexception) {
               LOGGER.warn("Couldn't save auto screenshot", (Throwable)ioexception);
            } finally {
               nativeimage.close();
            }

         });
      }

   }

   private boolean isDrawBlockOutline() {
      if (!this.drawBlockOutline) {
         return false;
      } else {
         Entity entity = this.mc.getRenderViewEntity();
         boolean flag = entity instanceof PlayerEntity && !this.mc.gameSettings.hideGUI;
         if (flag && !((PlayerEntity)entity).abilities.allowEdit) {
            ItemStack itemstack = ((LivingEntity)entity).getHeldItemMainhand();
            RayTraceResult raytraceresult = this.mc.objectMouseOver;
            if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
               BlockPos blockpos = ((BlockRayTraceResult)raytraceresult).getPos();
               BlockState blockstate = this.mc.world.getBlockState(blockpos);
               if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
                  flag = blockstate.getContainer(this.mc.world, blockpos) != null;
               } else {
                  CachedBlockInfo cachedblockinfo = new CachedBlockInfo(this.mc.world, blockpos, false);
                  flag = !itemstack.isEmpty() && (itemstack.canDestroy(this.mc.world.getTags(), cachedblockinfo) || itemstack.canPlaceOn(this.mc.world.getTags(), cachedblockinfo));
               }
            }
         }

         return flag;
      }
   }

   public void renderWorld(float partialTicks, long finishTimeNano) {
      this.lightmapTexture.updateLightmap(partialTicks);
      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.player);
      }

      this.getMouseOver(partialTicks);
      GlStateManager.enableDepthTest();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.5F);
      this.mc.getProfiler().startSection("center");
      this.updateCameraAndRender(partialTicks, finishTimeNano);
      this.mc.getProfiler().endSection();
   }

   private void updateCameraAndRender(float partialTicks, long nanoTime) {
      if (this.mc.gameSettings.shaders) {
         try {
            this.eaglercraftShaders(partialTicks);
         } catch (Throwable t) {
            LOGGER.error("Exception caught running deferred render!");
            LOGGER.error(t);
            if (EaglerDeferredPipeline.instance != null) {
               EaglerDeferredPipeline.instance.resetContextStateAfterException();
            }
            LOGGER.error("Suspending shaders...");
            EaglerDeferredPipeline.isSuspended = true;
         }
         return;
      }

      WorldRenderer worldrenderer = this.mc.worldRenderer;
      ParticleManager particlemanager = this.mc.particles;
      boolean flag = this.isDrawBlockOutline();
      GlStateManager.enableCull();
      this.mc.getProfiler().endStartSection("camera");
      this.setupCameraTransform(partialTicks);
      ActiveRenderInfo activerenderinfo = this.activeRender;
      activerenderinfo.update(this.mc.world, (Entity)(this.mc.getRenderViewEntity() == null ? this.mc.player : this.mc.getRenderViewEntity()), this.mc.gameSettings.thirdPersonView > 0, this.mc.gameSettings.thirdPersonView == 2, partialTicks);
      worldrenderer.func_224745_a(activerenderinfo);
      this.mc.getProfiler().endStartSection("clear");
      GlStateManager.viewport(0, 0, this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
      this.fogRenderer.updateFogColor(activerenderinfo, partialTicks);
      GlStateManager.clear(16640, Minecraft.IS_RUNNING_ON_MAC);
      this.mc.getProfiler().endStartSection("culling");
      this.cachedFrustum.update();
      ICamera icamera = this.cachedFrustum;
      double d0 = activerenderinfo.getProjectedView().x;
      double d1 = activerenderinfo.getProjectedView().y;
      double d2 = activerenderinfo.getProjectedView().z;
      icamera.setPosition(d0, d1, d2);
      if (this.mc.gameSettings.renderDistanceChunks >= 4) {
         this.fogRenderer.setupFog(activerenderinfo, -1);
         this.mc.getProfiler().endStartSection("sky");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrix(Matrix4f.perspective(this.getFOVModifier(activerenderinfo, partialTicks, true), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * 2.0F).getElements());
         GlStateManager.matrixMode(5888);
         worldrenderer.renderSky(partialTicks);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrix(Matrix4f.perspective(this.getFOVModifier(activerenderinfo, partialTicks, true), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * MathHelper.SQRT_2).getElements());
         GlStateManager.matrixMode(5888);
      }

      this.fogRenderer.setupFog(activerenderinfo, 0);
      GlStateManager.shadeModel(7425);
      if (activerenderinfo.getProjectedView().y < 128.0D) {
         this.renderClouds(activerenderinfo, worldrenderer, partialTicks, d0, d1, d2);
      }

      this.mc.getProfiler().endStartSection("prepareterrain");
      this.fogRenderer.setupFog(activerenderinfo, 0);
      this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.texParameter(3553, 10241, 9728);
      GlStateManager.texParameter(3553, 10240, 9728);
      RenderHelper.disableStandardItemLighting();
      this.mc.getProfiler().endStartSection("terrain_setup");
      worldrenderer.setupTerrain(activerenderinfo, icamera, this.frameCount++, this.mc.player.isSpectator());
      this.mc.getProfiler().endStartSection("updatechunks");
      this.mc.worldRenderer.updateChunks(Util.nanoTime() + 2_000_000L);
      this.mc.getProfiler().endStartSection("terrain");
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      this.enableLightmap();
      GlStateManager.disableAlphaTest();
      GlStateManager.disableBlend();
      worldrenderer.renderBlockLayer(BlockRenderLayer.SOLID, activerenderinfo);
      GlStateManager.enableAlphaTest();
      worldrenderer.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, activerenderinfo);
      this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      worldrenderer.renderBlockLayer(BlockRenderLayer.CUTOUT, activerenderinfo);
      this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      this.disableLightmap();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      RenderHelper.enableStandardItemLighting();
      this.mc.getProfiler().endStartSection("entities");
      worldrenderer.renderEntities(activerenderinfo, icamera, partialTicks);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      if (flag && this.mc.objectMouseOver != null) {
         GlStateManager.disableAlphaTest();
         this.mc.getProfiler().endStartSection("outline");
         worldrenderer.drawSelectionBox(activerenderinfo, this.mc.objectMouseOver, 0);
         GlStateManager.enableAlphaTest();
      }

      if (this.mc.debugRenderer.shouldRender()) {
         this.mc.debugRenderer.renderDebug(nanoTime);
      }

      this.mc.getProfiler().endStartSection("destroyProgress");
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      worldrenderer.func_215318_a(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), activerenderinfo);
      this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      GlStateManager.disableBlend();
      this.enableLightmap();
      this.fogRenderer.setupFog(activerenderinfo, 0);
      this.mc.getProfiler().endStartSection("particles");
      particlemanager.renderParticles(activerenderinfo, partialTicks);
      this.disableLightmap();
      GlStateManager.depthMask(false);
      GlStateManager.enableCull();
      this.mc.getProfiler().endStartSection("weather");
      this.renderRainSnow(partialTicks);
      GlStateManager.depthMask(true);
      worldrenderer.renderWorldBorder(activerenderinfo, partialTicks);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.alphaFunc(516, 0.1F);
      this.fogRenderer.setupFog(activerenderinfo, 0);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
      GlStateManager.shadeModel(7425);
      this.mc.getProfiler().endStartSection("translucent");
      this.enableLightmap();
      worldrenderer.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, activerenderinfo);
      this.disableLightmap();
      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableFog();
      if (activerenderinfo.getProjectedView().y >= 128.0D) {
         this.mc.getProfiler().endStartSection("aboveClouds");
         this.renderClouds(activerenderinfo, worldrenderer, partialTicks, d0, d1, d2);
      }

      this.mc.getProfiler().endStartSection("hand");
      if (this.renderHand) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.colorMask(true, true, true, true);
         GlStateManager.depthMask(true);
         GlStateManager.enableDepthTest();
         GlStateManager.enableTexture();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.clear(256, Minecraft.IS_RUNNING_ON_MAC);
         this.func_215308_a(activerenderinfo, partialTicks);
      }

   }

   private void renderClouds(ActiveRenderInfo p_215313_1_, WorldRenderer p_215313_2_, float p_215313_3_, double p_215313_4_, double p_215313_6_, double p_215313_8_) {
      if (this.mc.gameSettings.getCloudOption() != CloudOption.OFF) {
         this.mc.getProfiler().endStartSection("clouds");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrix(Matrix4f.perspective(this.getFOVModifier(p_215313_1_, p_215313_3_, true), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * 4.0F).getElements());
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         this.fogRenderer.setupFog(p_215313_1_, 0);
         p_215313_2_.renderClouds(p_215313_3_, p_215313_4_, p_215313_6_, p_215313_8_);
         GlStateManager.disableFog();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrix(Matrix4f.perspective(this.getFOVModifier(p_215313_1_, p_215313_3_, true), (float)this.mc.mainWindow.getFramebufferWidth() / (float)this.mc.mainWindow.getFramebufferHeight(), 0.05F, this.farPlaneDistance * MathHelper.SQRT_2).getElements());
         GlStateManager.matrixMode(5888);
      }

   }

   private void addRainParticles() {
      if (this.mc.gameSettings.disableWeather) {
         return;
      }
      float f = this.mc.world.getRainStrength(1.0F);
      if (!this.mc.gameSettings.fancyGraphics) {
         f /= 2.0F;
      }

      if (f != 0.0F) {
         this.random.setSeed((long)this.rendererUpdateCount * 312987231L);
         IWorldReader iworldreader = this.mc.world;
         BlockPos blockpos = this.activeRender.getBlockPos();
         int i = 10;
         double d0 = 0.0D;
         double d1 = 0.0D;
         double d2 = 0.0D;
         int j = 0;
         int k = (int)(100.0F * f * f);
         if (this.mc.gameSettings.particles == ParticleStatus.DECREASED) {
            k >>= 1;
         } else if (this.mc.gameSettings.particles == ParticleStatus.MINIMAL) {
            k = 0;
         }

         for(int l = 0; l < k; ++l) {
            BlockPos blockpos1 = iworldreader.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos.add(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10)));
            Biome biome = iworldreader.getBiome(blockpos1);
            BlockPos blockpos2 = blockpos1.down();
            if (blockpos1.getY() <= blockpos.getY() + 10 && blockpos1.getY() >= blockpos.getY() - 10 && biome.getPrecipitation() == Biome.RainType.RAIN && biome.func_225486_c(blockpos1) >= 0.15F) {
               double d3 = this.random.nextDouble();
               double d4 = this.random.nextDouble();
               BlockState blockstate = iworldreader.getBlockState(blockpos2);
               IFluidState ifluidstate = iworldreader.getFluidState(blockpos1);
               VoxelShape voxelshape = blockstate.getCollisionShape(iworldreader, blockpos2);
               double d7 = voxelshape.max(Direction.Axis.Y, d3, d4);
               double d8 = (double)ifluidstate.func_215679_a(iworldreader, blockpos1);
               double d5;
               double d6;
               if (d7 >= d8) {
                  d5 = d7;
                  d6 = voxelshape.min(Direction.Axis.Y, d3, d4);
               } else {
                  d5 = 0.0D;
                  d6 = 0.0D;
               }

               if (d5 > -Double.MAX_VALUE) {
                  if (!ifluidstate.isTagged(FluidTags.LAVA) && blockstate.getBlock() != Blocks.MAGMA_BLOCK && (blockstate.getBlock() != Blocks.CAMPFIRE || !blockstate.get(CampfireBlock.LIT))) {
                     ++j;
                     if (this.random.nextInt(j) == 0) {
                        d0 = (double)blockpos2.getX() + d3;
                        d1 = (double)((float)blockpos2.getY() + 0.1F) + d5 - 1.0D;
                        d2 = (double)blockpos2.getZ() + d4;
                     }

                     this.mc.world.addParticle(ParticleTypes.RAIN, (double)blockpos2.getX() + d3, (double)((float)blockpos2.getY() + 0.1F) + d5, (double)blockpos2.getZ() + d4, 0.0D, 0.0D, 0.0D);
                  } else {
                     this.mc.world.addParticle(ParticleTypes.SMOKE, (double)blockpos1.getX() + d3, (double)((float)blockpos1.getY() + 0.1F) - d6, (double)blockpos1.getZ() + d4, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         }

         if (j > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
            this.rainSoundCounter = 0;
            if (d1 > (double)(blockpos.getY() + 1) && iworldreader.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos).getY() > MathHelper.floor((float)blockpos.getY())) {
               this.mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
            } else {
               this.mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
            }
         }

      }
   }

   protected void renderRainSnow(float partialTicks) {
      if (this.mc.gameSettings.disableWeather) {
         return;
      }
      float f = this.mc.world.getRainStrength(partialTicks);
      if (!(f <= 0.0F)) {
         this.enableLightmap();
         World world = this.mc.world;
         int i = MathHelper.floor(this.activeRender.getProjectedView().x);
         int j = MathHelper.floor(this.activeRender.getProjectedView().y);
         int k = MathHelper.floor(this.activeRender.getProjectedView().z);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         GlStateManager.disableCull();
         GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.alphaFunc(516, 0.1F);
         double d0 = this.activeRender.getProjectedView().x;
         double d1 = this.activeRender.getProjectedView().y;
         double d2 = this.activeRender.getProjectedView().z;
         int l = MathHelper.floor(d1);
         int i1 = 5;
         if (this.mc.gameSettings.fancyGraphics) {
            i1 = 10;
         }

         int j1 = -1;
         float f1 = (float)this.rendererUpdateCount + partialTicks;
         bufferbuilder.setTranslation(-d0, -d1, -d2);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = this.weatherRenderPos;

         for(int k1 = k - i1; k1 <= k + i1; ++k1) {
            for(int l1 = i - i1; l1 <= i + i1; ++l1) {
               int i2 = (k1 - k + 16) * 32 + l1 - i + 16;
               double d3 = (double)this.rainXCoords[i2] * 0.5D;
               double d4 = (double)this.rainYCoords[i2] * 0.5D;
               blockpos$mutableblockpos.setPos(l1, 0, k1);
               Biome biome = world.getBiome(blockpos$mutableblockpos);
               if (biome.getPrecipitation() != Biome.RainType.NONE) {
                  int j2 = world.getHeight(Heightmap.Type.MOTION_BLOCKING, blockpos$mutableblockpos).getY();
                  int k2 = j - i1;
                  int l2 = j + i1;
                  if (k2 < j2) {
                     k2 = j2;
                  }

                  if (l2 < j2) {
                     l2 = j2;
                  }

                  int i3 = j2;
                  if (j2 < l) {
                     i3 = l;
                  }

                  if (k2 != l2) {
                     this.random.setSeed((long)(l1 * l1 * 3121 + l1 * 45238971 ^ k1 * k1 * 418711 + k1 * 13761));
                     blockpos$mutableblockpos.setPos(l1, k2, k1);
                     float f2 = biome.func_225486_c(blockpos$mutableblockpos);
                     if (f2 >= 0.15F) {
                        if (j1 != 0) {
                           if (j1 >= 0) {
                              tessellator.draw();
                           }

                           j1 = 0;
                           this.mc.getTextureManager().bindTexture(RAIN_TEXTURES);
                           bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                        }

                        double d5 = -((double)(this.rendererUpdateCount + l1 * l1 * 3121 + l1 * 45238971 + k1 * k1 * 418711 + k1 * 13761 & 31) + (double)partialTicks) / 32.0D * (3.0D + this.random.nextDouble());
                        double d6 = (double)((float)l1 + 0.5F) - this.activeRender.getProjectedView().x;
                        double d7 = (double)((float)k1 + 0.5F) - this.activeRender.getProjectedView().z;
                        float f3 = MathHelper.sqrt(d6 * d6 + d7 * d7) / (float)i1;
                        float f4 = ((1.0F - f3 * f3) * 0.5F + 0.5F) * f;
                        blockpos$mutableblockpos.setPos(l1, i3, k1);
                        int j3 = world.getCombinedLight(blockpos$mutableblockpos, 0);
                        int k3 = j3 >> 16 & '\uffff';
                        int l3 = j3 & '\uffff';
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)l2, (double)k1 - d4 + 0.5D).tex(0.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)l2, (double)k1 + d4 + 0.5D).tex(1.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)k2, (double)k1 + d4 + 0.5D).tex(1.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)k2, (double)k1 - d4 + 0.5D).tex(0.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4).lightmap(k3, l3).endVertex();
                     } else {
                        if (j1 != 1) {
                           if (j1 >= 0) {
                              tessellator.draw();
                           }

                           j1 = 1;
                           this.mc.getTextureManager().bindTexture(SNOW_TEXTURES);
                           bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                        }

                        double d8 = (double)(-((float)(this.rendererUpdateCount & 511) + partialTicks) / 512.0F);
                        double d9 = this.random.nextDouble() + (double)f1 * 0.01D * (double)((float)this.random.nextGaussian());
                        double d10 = this.random.nextDouble() + (double)(f1 * (float)this.random.nextGaussian()) * 0.001D;
                        double d11 = (double)((float)l1 + 0.5F) - this.activeRender.getProjectedView().x;
                        double d12 = (double)((float)k1 + 0.5F) - this.activeRender.getProjectedView().z;
                        float f6 = MathHelper.sqrt(d11 * d11 + d12 * d12) / (float)i1;
                        float f5 = ((1.0F - f6 * f6) * 0.3F + 0.5F) * f;
                        blockpos$mutableblockpos.setPos(l1, i3, k1);
                        int i4 = (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 15728880) / 4;
                        int j4 = i4 >> 16 & '\uffff';
                        int k4 = i4 & '\uffff';
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)l2, (double)k1 - d4 + 0.5D).tex(0.0D + d9, (double)k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)l2, (double)k1 + d4 + 0.5D).tex(1.0D + d9, (double)k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                        bufferbuilder.pos((double)l1 + d3 + 0.5D, (double)k2, (double)k1 + d4 + 0.5D).tex(1.0D + d9, (double)l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                        bufferbuilder.pos((double)l1 - d3 + 0.5D, (double)k2, (double)k1 - d4 + 0.5D).tex(0.0D + d9, (double)l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5).lightmap(j4, k4).endVertex();
                     }
                  }
               }
            }
         }

         if (j1 >= 0) {
            tessellator.draw();
         }

         bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         this.disableLightmap();
      }
   }

   public void setupFogColor(boolean black) {
      this.fogRenderer.applyFog(black);
   }

   public void resetData() {
      this.itemActivationItem = null;
      this.mapItemRenderer.clearLoadedMaps();
      this.activeRender.clear();
   }

   public MapItemRenderer getMapItemRenderer() {
      return this.mapItemRenderer;
   }

   public static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isSneaking) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef(x, y, z);
      GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-viewerYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(viewerPitch, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      if (!isSneaking) {
         GlStateManager.disableDepthTest();
      }

      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      int i = fontRendererIn.getStringWidth(str) / 2;
      GlStateManager.disableTexture();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      float f = Minecraft.getInstance().gameSettings.func_216840_a(0.25F);
      bufferbuilder.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, f).endVertex();
      bufferbuilder.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, f).endVertex();
      bufferbuilder.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, f).endVertex();
      bufferbuilder.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, f).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture();
      if (!isSneaking) {
         fontRendererIn.drawString(str, (float)(-fontRendererIn.getStringWidth(str) / 2), (float)verticalShift, 553648127);
         GlStateManager.enableDepthTest();
      }

      GlStateManager.depthMask(true);
      fontRendererIn.drawString(str, (float)(-fontRendererIn.getStringWidth(str) / 2), (float)verticalShift, isSneaking ? 553648127 : -1);
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public void displayItemActivation(ItemStack stack) {
      this.itemActivationItem = stack;
      this.itemActivationTicks = 40;
      this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
      this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
   }

   private void renderItemActivation(int widthsp, int heightScaled, float partialTicks) {
      if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
         int i = 40 - this.itemActivationTicks;
         float f = ((float)i + partialTicks) / 40.0F;
         float f1 = f * f;
         float f2 = f * f1;
         float f3 = 10.25F * f2 * f1 - 24.95F * f1 * f1 + 25.5F * f2 - 13.8F * f1 + 4.0F * f;
         float f4 = f3 * (float)Math.PI;
         float f5 = this.itemActivationOffX * (float)(widthsp / 4);
         float f6 = this.itemActivationOffY * (float)(heightScaled / 4);
         GlStateManager.enableAlphaTest();
         GlStateManager.pushMatrix();
         GlStateManager.pushLightingAttributes();
         GlStateManager.enableDepthTest();
         GlStateManager.disableCull();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.translatef((float)(widthsp / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), (float)(heightScaled / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), -50.0F);
         float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
         GlStateManager.scalef(f7, -f7, f7);
         GlStateManager.rotatef(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);
         this.mc.getItemRenderer().renderItem(this.itemActivationItem, ItemCameraTransforms.TransformType.FIXED);
         GlStateManager.popAttributes();
         GlStateManager.popMatrix();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.enableCull();
         GlStateManager.disableDepthTest();
      }
   }

   public Minecraft getMinecraft() {
      return this.mc;
   }

   public float getBossColorModifier(float partialTicks) {
      return MathHelper.lerp(partialTicks, this.bossColorModifierPrev, this.bossColorModifier);
   }

   public float getFarPlaneDistance() {
      return this.farPlaneDistance;
   }

   public ActiveRenderInfo getActiveRenderInfo() {
      return this.activeRender;
   }

    private void eaglercraftShaders(float partialTicks) {
      if (this.mc.gameSettings.shaders) {
         EaglerDeferredConfig dfc = this.mc.gameSettings.deferredShaderConf;
         if (dfc != null && dfc.shaderPackInfo != null) {
            if (EaglerDeferredPipeline.instance == null) {
               try {
                  EaglerDeferredPipeline.instance = new EaglerDeferredPipeline(this.mc);
               } catch (Throwable ex) {
                  LOGGER.error("Could not create deferred pipeline!");
                  LOGGER.error(ex);
                  EaglerDeferredPipeline.isSuspended = true;
               }
            }
            if (!EaglerDeferredPipeline.isSuspended && !eaglerShadersInitialized) {
               try {
                  SharedPipelineShaders.init();
                  EaglerDeferredPipeline.instance.rebuild(dfc);
                  EaglerDeferredPipeline.isSuspended = false;
                  eaglerShadersInitialized = true;
               } catch (Throwable ex) {
                  LOGGER.error("Could not enable shaders!");
                  LOGGER.error(ex);
                  EaglerDeferredPipeline.isSuspended = true;
               }
            }
         } else {
            EaglerDeferredPipeline.isSuspended = true;
         }
      } else {
         if (EaglerDeferredPipeline.instance != null) {
            try {
               EaglerDeferredPipeline.instance.destroy();
               EaglerDeferredPipeline.instance = null;
            } catch (Throwable ex) {
               LOGGER.error("Could not safely disable shaders!");
               LOGGER.error(ex);
            }
         }
         SharedPipelineShaders.free();
         eaglerShadersInitialized = false;
      }

      if ((EaglerDeferredPipeline.isSuspended || EaglerDeferredPipeline.instance == null)) {
         EaglerDeferredPipeline.renderSuspended();
         return;
      }
      EaglerDeferredPipeline.instance.setPartialTicks(partialTicks);
      eagPartialTicks = partialTicks;
      EaglerDeferredConfig conf = this.mc.gameSettings.deferredShaderConf;
      boolean flag = this.isDrawBlockOutline();
      GlStateManager.viewport(0, 0, this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
      this.setupCameraTransform(partialTicks);
      ActiveRenderInfo activerenderinfo = this.activeRender;
      activerenderinfo.update(this.mc.world, this.mc.getRenderViewEntity() == null ? this.mc.player : this.mc.getRenderViewEntity(), this.mc.gameSettings.thirdPersonView > 0, this.mc.gameSettings.thirdPersonView == 2, partialTicks);
      EaglerDeferredPipeline.instance.loadViewMatrix();
      ICamera icamera = this.cachedFrustum;
      final Entity entity = this.mc.getRenderViewEntity() == null ? this.mc.player : this.mc.getRenderViewEntity();
      double d0 = activerenderinfo.getProjectedView().x;
      double d1 = activerenderinfo.getProjectedView().y;
      double d2 = activerenderinfo.getProjectedView().z;
      EaglerDeferredPipeline.instance.setRenderPosGlobal(d0, d1, d2);
      EaglerDeferredPipeline.instance.updateReprojectionCoordinates(d0, d1, d2);
      this.cachedFrustum.update();
      icamera.setPosition(d0, d1, d2);

      float waveTimer = (float)((EagRuntime.steadyTimeMillis() % 600000l) * 0.001);
      DeferredStateManager.setWaterWindOffset(waveTimer * 0.25f, waveTimer * 0.13f, waveTimer * 1.45f, waveTimer);

      float blockWaveDistX = (float)(d0 - blockWaveOffsetX);
      float blockWaveDistY = (float)(d1 - blockWaveOffsetY);
      float blockWaveDistZ = (float)(d2 - blockWaveOffsetZ);
      if (blockWaveDistX * blockWaveDistX + blockWaveDistY * blockWaveDistY + blockWaveDistZ * blockWaveDistZ > 128.0f * 128.0f) {
         blockWaveOffsetX = MathHelper.floor(d0);
         blockWaveOffsetY = MathHelper.floor(d1);
         blockWaveOffsetZ = MathHelper.floor(d2);
         blockWaveDistX = (float)(d0 - blockWaveOffsetX);
         blockWaveDistY = (float)(d1 - blockWaveOffsetY);
         blockWaveDistZ = (float)(d2 - blockWaveOffsetZ);
      }

      boolean wavingBlocks = conf.is_rendering_wavingBlocks;

      DeferredStateManager.setWavingBlockOffset(blockWaveDistX, blockWaveDistY, blockWaveDistZ);
      if (wavingBlocks) {
         DeferredStateManager.setWavingBlockParams(1.0f * waveTimer, 200.0f * waveTimer, 0.0f, 0.0f);
      }

      this.mc.worldRenderer.setupTerrain(activerenderinfo, icamera, this.frameCount++, this.mc.player.isSpectator());

      GlStateManager.enableCull();
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlphaTest();
      GlStateManager.disableBlend();

      EaglerDeferredPipeline.instance.beginDrawDeferred();
      EaglerDeferredPipeline.instance.beginDrawMainGBuffer();
      EaglerDeferredPipeline.instance.beginDrawMainGBufferTerrain();

      this.mc.worldRenderer.updateChunks(0L);

      this.mc.worldRenderer.renderBlockLayer(BlockRenderLayer.SOLID, activerenderinfo);
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.5F);
      if (wavingBlocks) DeferredStateManager.enableDrawWavingBlocks();
      this.mc.worldRenderer.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, activerenderinfo);
      this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      this.mc.worldRenderer.renderBlockLayer(BlockRenderLayer.CUTOUT, activerenderinfo);
      this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      if (wavingBlocks) DeferredStateManager.disableDrawWavingBlocks();

      EaglerDeferredPipeline.instance.beginDrawMainGBufferEntities();
      if (conf.is_rendering_dynamicLights) {
         net.lax1dude.eaglercraft.opengl.ext.deferred.DynamicLightManager.setIsRenderingLights(true);
      }

      DeferredStateManager.forwardCallbackHandler = DeferredStateManager.forwardCallbackGBuffer;
      DeferredStateManager.forwardCallbackHandler.reset();

      NameTagRenderer.doRenderNameTags = true;
      NameTagRenderer.nameTagsCount = 0;
      GlStateManager.pushMatrix();
      DeferredStateManager.setDefaultMaterialConstants();
      DeferredStateManager.startUsingEnvMap();
      this.mc.worldRenderer.renderEntities(activerenderinfo, icamera, partialTicks);
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      net.minecraft.client.particle.Particle.interpPosX = d0;
      net.minecraft.client.particle.Particle.interpPosY = d1;
      net.minecraft.client.particle.Particle.interpPosZ = d2;
      this.enableLightmap();
      GlStateManager.pushMatrix();
      this.mc.particles.renderParticles(activerenderinfo, partialTicks);
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      DeferredStateManager.endUsingEnvMap();
      this.disableLightmap();
      net.lax1dude.eaglercraft.opengl.ext.deferred.DynamicLightManager.setIsRenderingLights(false);
      NameTagRenderer.doRenderNameTags = false;

      EaglerDeferredPipeline.instance.endDrawMainGBuffer();

      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
      float celestialAngle = this.mc.world.getCelestialAngle(partialTicks) * 360.0F;
      GlStateManager.rotatef(DeferredStateManager.sunAngle, 0.0F, 1.0F, 0.0F);

      if (this.mc.world.dimension.getType().getId() == 0) {
         GlStateManager.pushMatrix();
         GlStateManager.rotatef(celestialAngle + 90.0f, 1.0F, 0.0F, 0.0F);
         tmpVec4f_1.set(0.0f, 0.0f, 1.0f, 1.0f);
         net.lax1dude.eaglercraft.opengl.GlStateManager.transform(tmpVec4f_1, tmpVec4f_1);
         tmpVec4f_1.normalise();
         DeferredStateManager.setCurrentSunAngle(tmpVec4f_1);
         if (tmpVec4f_1.y > 0.1f) {
            celestialAngle += 180.0f;
         }
         GlStateManager.popMatrix();
      } else {
         tmpVec4f_1.set(0.0f, 1.0f, 0.0f, 1.0f);
         DeferredStateManager.setCurrentSunAngle(tmpVec4f_1);
         celestialAngle = 270.0f;
      }

      if (conf.is_rendering_shadowsSun_clamped > 0) {
         if (conf.is_rendering_shadowsColored) {
            DeferredStateManager.forwardCallbackHandler = DeferredStateManager.forwardCallbackSun;
            DeferredStateManager.forwardCallbackHandler.reset();
         } else {
            DeferredStateManager.forwardCallbackHandler = null;
         }
         EaglerDeferredPipeline.instance.beginDrawMainShadowMap();
         ++shadowFrameIndex;
         EaglerDeferredPipeline.instance.beginDrawMainShadowMapLOD(0);
         GlStateManager.enableCull();
         GlStateManager.matrixMode(5889);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         int shadowMapDist = 16;
         GlStateManager.ortho(-shadowMapDist, shadowMapDist, -shadowMapDist, shadowMapDist, -64.0f, 64.0f);

         setupSunCameraTransform(celestialAngle);

         DeferredStateManager.loadShadowPassViewMatrix();
         DeferredStateManager.loadSunShadowMatrixLOD0();

         GlStateManager.disableAlphaTest();
         GlStateManager.disableBlend();

         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();

         final AxisAlignedBB aabb = matrixToBounds(DeferredStateManager.getSunShadowMatrixLOD0(), d0, d1 + entity.getEyeHeight(), d2);
         DeferredStateManager.setShadowMapBounds(aabb);

         final net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum shadowLOD0Frustrum = new net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum(DeferredStateManager.getSunShadowMatrixLOD0());

         net.minecraft.client.renderer.WorldRenderer.ChunkCullAdapter shadowCullAdapter = (renderChunk) -> {
            if (renderChunk.shadowLOD0FrameIndex != shadowFrameIndex) {
               renderChunk.shadowLOD0FrameIndex = shadowFrameIndex;
               AxisAlignedBB aabb2 = renderChunk.boundingBox;
               if (aabb.intersects(aabb2)) {
                  int shadowVisRet = shadowLOD0Frustrum.intersectAab((float)(aabb2.minX - d0), (float)(aabb2.minY - d1 - entity.getEyeHeight()), (float)(aabb2.minZ - d2), (float)(aabb2.maxX - d0), (float)(aabb2.maxY - d1 - entity.getEyeHeight()), (float)(aabb2.maxZ - d2));
                  renderChunk.shadowLOD0InFrustum = shadowVisRet == net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum.INSIDE ? net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.INSIDE : (shadowVisRet == net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum.INTERSECT ? net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.INTERSECT : net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE);
               } else {
                  renderChunk.shadowLOD0InFrustum = net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE_BB;
                  return true;
               }
            }
            return renderChunk.shadowLOD0InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE;
         };

         this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
         this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
         this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.SOLID, aabb, shadowCullAdapter);
         GlStateManager.enableAlphaTest();
         GlStateManager.alphaFunc(516, 0.5F);
         if (wavingBlocks) {
            DeferredStateManager.enableDrawWavingBlocks();
            this.enableLightmap();
         }
         this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.CUTOUT_MIPPED, aabb, shadowCullAdapter);
         this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.CUTOUT, aabb, shadowCullAdapter);
         this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
         GlStateManager.alphaFunc(516, 0.1F);
         if (wavingBlocks) {
            DeferredStateManager.disableDrawWavingBlocks();
            this.disableLightmap();
         }

         this.mc.worldRenderer.renderShadowLODEntities(entity, partialTicks, (renderChunk) -> {
            return renderChunk.shadowLOD0FrameIndex == shadowFrameIndex && (renderChunk.shadowLOD0InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE || renderChunk.shadowLOD0InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE_BB);
         }, (renderChunk, rm, renderEntity) -> {
            boolean b;
            if (renderEntity.ignoreFrustumCheck) {
               return false;
            } else if (!renderEntity.isInRangeToRender3d(d0, d1, d2)) {
               return true;
            } else if (renderChunk.shadowLOD0FrameIndex == shadowFrameIndex && ((b = renderChunk.shadowLOD0InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE) || renderChunk.shadowLOD0InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.INSIDE)) {
               return b;
            } else {
               AxisAlignedBB aabbEntity = renderEntity.getBoundingBox();
               if (aabbEntity.hasNaN() || aabbEntity.getAverageEdgeLength() == 0.0) {
                  aabbEntity = new AxisAlignedBB(d0 - 2.0, d1 - 2.0, d2 - 2.0, d0 + 2.0, d1 + 2.0, d2 + 2.0);
               }
               if (shadowLOD0Frustrum.testAab((float)(aabbEntity.minX - d0), (float)(aabbEntity.minY - d1 - entity.getEyeHeight()), (float)(aabbEntity.minZ - d2), (float)(aabbEntity.maxX - d0), (float)(aabbEntity.maxY - d1 - entity.getEyeHeight()), (float)(aabbEntity.maxZ - d2))) {
                  return !rm.shouldRender(renderEntity, icamera, d0, d1, d2);
               } else {
                  return true;
               }
            }
         });
         this.disableLightmap();

         if (conf.is_rendering_shadowsColored) {
            EaglerDeferredPipeline.instance.beginDrawColoredShadows();
            java.util.List<net.lax1dude.eaglercraft.opengl.ext.deferred.ShadersRenderPassFuture> lst = DeferredStateManager.forwardCallbackHandler.renderPassList;
            for (int i = 0, l = lst.size(); i < l; ++i) {
               lst.get(i).draw(net.lax1dude.eaglercraft.opengl.ext.deferred.ShadersRenderPassFuture.PassType.SHADOW);
            }
            DeferredStateManager.forwardCallbackHandler.reset();
            this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.TRANSLUCENT, aabb, shadowCullAdapter);
            this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
            if (conf.is_rendering_realisticWater) {
               GlStateManager.disableTexture();
               net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.disableMaterialTexture();
               net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setRoughnessConstant(0.117f);
               net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setMetalnessConstant(0.067f);
               net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setEmissionConstant(0.0f);
               GlStateManager.color4f(0.173f, 0.239f, 0.957f, 0.25f);
               this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.TRANSLUCENT, aabb, shadowCullAdapter);
               GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
               GlStateManager.enableTexture();
               net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.enableMaterialTexture();
            }
            EaglerDeferredPipeline.instance.endDrawColoredShadows();
         }
         this.disableLightmap();

         GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
         GlStateManager.disableAlphaTest();
         GlStateManager.matrixMode(5889);
         GlStateManager.popMatrix();

         if (conf.is_rendering_shadowsSun_clamped > 1) {
            EaglerDeferredPipeline.instance.beginDrawMainShadowMapLOD(1);
            if (conf.is_rendering_shadowsColored) {
               DeferredStateManager.forwardCallbackHandler.reset();
            }

            GlStateManager.enableCull();
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            shadowMapDist = 32;
            GlStateManager.ortho(-shadowMapDist, shadowMapDist, -shadowMapDist, shadowMapDist, -64.0f, 64.0f);

            setupSunCameraTransform(celestialAngle);

            DeferredStateManager.loadShadowPassViewMatrix();
            DeferredStateManager.loadSunShadowMatrixLOD1();

            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();

            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();

            final AxisAlignedBB aabb2 = matrixToBounds(DeferredStateManager.getSunShadowMatrixLOD1(), d0, d1 + entity.getEyeHeight(), d2);
            DeferredStateManager.setShadowMapBounds(aabb2);

            net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum shadowLOD1Frustrum = new net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum(DeferredStateManager.getSunShadowMatrixLOD1());

            net.minecraft.client.renderer.WorldRenderer.ChunkCullAdapter shadowCullAdapter2 = (renderChunk) -> {
               if (renderChunk.shadowLOD1FrameIndex != shadowFrameIndex) {
                  renderChunk.shadowLOD1FrameIndex = shadowFrameIndex;
                  if (renderChunk.shadowLOD0FrameIndex == shadowFrameIndex && renderChunk.shadowLOD0InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.INSIDE) {
                     renderChunk.shadowLOD1InFrustum = net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE;
                     return true;
                  } else {
                     AxisAlignedBB aabb3 = renderChunk.boundingBox;
                     if (aabb2.intersects(aabb3)) {
                        int shadowVisRet = shadowLOD1Frustrum.intersectAab((float)(aabb3.minX - d0), (float)(aabb3.minY - d1 - entity.getEyeHeight()), (float)(aabb3.minZ - d2), (float)(aabb3.maxX - d0), (float)(aabb3.maxY - d1 - entity.getEyeHeight()), (float)(aabb3.maxZ - d2));
                        renderChunk.shadowLOD1InFrustum = shadowVisRet == net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum.INSIDE ? net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.INSIDE : (shadowVisRet == net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum.INTERSECT ? net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.INTERSECT : net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE);
                     } else {
                        renderChunk.shadowLOD1InFrustum = net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE_BB;
                        return true;
                     }
                  }
               }
               return renderChunk.shadowLOD1InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE;
            };

            this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.SOLID, aabb2, shadowCullAdapter2);
            GlStateManager.enableAlphaTest();
            GlStateManager.alphaFunc(516, 0.5F);
            if (wavingBlocks) {
               DeferredStateManager.enableDrawWavingBlocks();
               this.enableLightmap();
            }
            this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.CUTOUT_MIPPED, aabb2, shadowCullAdapter2);
            this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.CUTOUT, aabb2, shadowCullAdapter2);
            this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
            GlStateManager.alphaFunc(516, 0.1F);
            if (wavingBlocks) {
               DeferredStateManager.disableDrawWavingBlocks();
               this.disableLightmap();
            }

            this.mc.worldRenderer.renderShadowLODEntities(entity, partialTicks, (renderChunk) -> {
               return renderChunk.shadowLOD1FrameIndex == shadowFrameIndex && (renderChunk.shadowLOD1InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE || renderChunk.shadowLOD1InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE_BB);
            }, (renderChunk, rm2, renderEntity) -> {
               boolean b;
               if (renderEntity.ignoreFrustumCheck) {
                  return false;
               } else if (!renderEntity.isInRangeToRender3d(d0, d1, d2)) {
                  return true;
               } else if (renderChunk.shadowLOD1FrameIndex == shadowFrameIndex && (b = renderChunk.shadowLOD1InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE)) {
                  return b;
               } else {
                  AxisAlignedBB aabbEntity = renderEntity.getBoundingBox();
                  if (aabbEntity.hasNaN() || aabbEntity.getAverageEdgeLength() == 0.0) {
                     aabbEntity = new AxisAlignedBB(d0 - 2.0, d1 - 2.0, d2 - 2.0, d0 + 2.0, d1 + 2.0, d2 + 2.0);
                  }
                  if (shadowLOD1Frustrum.testAab((float)(aabbEntity.minX - d0), (float)(aabbEntity.minY - d1 - entity.getEyeHeight()), (float)(aabbEntity.minZ - d2), (float)(aabbEntity.maxX - d0), (float)(aabbEntity.maxY - d1 - entity.getEyeHeight()), (float)(aabbEntity.maxZ - d2))) {
                     return !rm2.shouldRender(renderEntity, icamera, d0, d1, d2);
                  } else {
                     return true;
                  }
               }
            });
            this.disableLightmap();

            if (conf.is_rendering_shadowsColored) {
               EaglerDeferredPipeline.instance.beginDrawColoredShadows();
               java.util.List<net.lax1dude.eaglercraft.opengl.ext.deferred.ShadersRenderPassFuture> lst = DeferredStateManager.forwardCallbackHandler.renderPassList;
               for (int i = 0, l = lst.size(); i < l; ++i) {
                  lst.get(i).draw(net.lax1dude.eaglercraft.opengl.ext.deferred.ShadersRenderPassFuture.PassType.SHADOW);
               }
               DeferredStateManager.forwardCallbackHandler.reset();
               this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
               this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
               this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.TRANSLUCENT, aabb2, shadowCullAdapter2);
               this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
               if (conf.is_rendering_realisticWater) {
                  GlStateManager.disableTexture();
                  net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.disableMaterialTexture();
                  net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setRoughnessConstant(0.117f);
                  net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setMetalnessConstant(0.067f);
                  net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setEmissionConstant(0.0f);
                  GlStateManager.color4f(0.173f, 0.239f, 0.957f, 0.25f);
                  this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.TRANSLUCENT, aabb2, shadowCullAdapter2);
                  GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                  GlStateManager.enableTexture();
                  net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.enableMaterialTexture();
               }
               EaglerDeferredPipeline.instance.endDrawColoredShadows();
            }
            this.disableLightmap();

            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableAlphaTest();
            GlStateManager.matrixMode(5889);
            GlStateManager.popMatrix();

            if (conf.is_rendering_shadowsSun_clamped > 2) {
               EaglerDeferredPipeline.instance.beginDrawMainShadowMapLOD(2);

               GlStateManager.enableCull();
               GlStateManager.matrixMode(5889);
               GlStateManager.pushMatrix();
               GlStateManager.loadIdentity();
               shadowMapDist = 1 << (conf.is_rendering_shadowsSun_clamped + 3);
               GlStateManager.ortho(-shadowMapDist, shadowMapDist, -shadowMapDist, shadowMapDist, -64.0f, 64.0f);

               setupSunCameraTransform(celestialAngle);

               DeferredStateManager.loadShadowPassViewMatrix();
               DeferredStateManager.loadSunShadowMatrixLOD2();

               GlStateManager.disableAlphaTest();
               GlStateManager.disableBlend();

               GlStateManager.matrixMode(5888);
               GlStateManager.loadIdentity();

               DeferredStateManager.loadPassViewMatrix();
               DeferredStateManager.loadPassProjectionMatrix();

               AxisAlignedBB aabb3 = matrixToBounds(DeferredStateManager.getSunShadowMatrixLOD2(), d0, d1 + entity.getEyeHeight(), d2);
               DeferredStateManager.setShadowMapBounds(aabb3);

               net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum shadowLOD2Frustum = new net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum(DeferredStateManager.getSunShadowMatrixLOD2());

               net.minecraft.client.renderer.WorldRenderer.ChunkCullAdapter shadowCullAdapter3 = (renderChunk) -> {
                  if (renderChunk.shadowLOD2FrameIndex != shadowFrameIndex) {
                     renderChunk.shadowLOD2FrameIndex = shadowFrameIndex;
                     if (renderChunk.shadowLOD0FrameIndex == shadowFrameIndex && renderChunk.shadowLOD0InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.INSIDE) {
                        renderChunk.shadowLOD2InFrustum = net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE;
                        return true;
                     } else if (renderChunk.shadowLOD1FrameIndex == shadowFrameIndex && renderChunk.shadowLOD1InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.INSIDE) {
                        renderChunk.shadowLOD2InFrustum = net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE;
                        return true;
                     } else {
                        AxisAlignedBB aabb4 = renderChunk.boundingBox;
                        if (aabb3.intersects(aabb4)) {
                           int shadowVisRet = shadowLOD2Frustum.intersectAab((float)(aabb4.minX - d0), (float)(aabb4.minY - d1 - entity.getEyeHeight()), (float)(aabb4.minZ - d2), (float)(aabb4.maxX - d0), (float)(aabb4.maxY - d1 - entity.getEyeHeight()), (float)(aabb4.maxZ - d2));
                           renderChunk.shadowLOD2InFrustum = shadowVisRet == net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum.INSIDE ? net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.INSIDE : (shadowVisRet == net.lax1dude.eaglercraft.opengl.ext.deferred.BetterFrustum.INTERSECT ? net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.INTERSECT : net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE);
                        } else {
                           renderChunk.shadowLOD2InFrustum = net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE_BB;
                           return true;
                        }
                     }
                  }
                  return renderChunk.shadowLOD2InFrustum == net.minecraft.client.renderer.chunk.ChunkRender.ShadowFrustumState.OUTSIDE;
               };

               this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
               this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
               this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.SOLID, aabb3, shadowCullAdapter3);
               GlStateManager.enableAlphaTest();
               GlStateManager.alphaFunc(516, 0.5F);
               this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.CUTOUT_MIPPED, aabb3, shadowCullAdapter3);
               this.mc.worldRenderer.renderBlockLayerShadow(BlockRenderLayer.CUTOUT, aabb3, shadowCullAdapter3);
               this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
               this.disableLightmap();

               GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
               GlStateManager.disableAlphaTest();
               GlStateManager.matrixMode(5889);
               GlStateManager.popMatrix();
            }
         }

         EaglerDeferredPipeline.instance.endDrawMainShadowMap();
         if (conf.is_rendering_shadowsColored) {
            DeferredStateManager.forwardCallbackHandler.reset();
         }
      }

      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();

      if (conf.is_rendering_dynamicLights && entity != null && this.mc.gameSettings.thirdPersonView == 0) {
         if (entity instanceof LivingEntity) {
            net.lax1dude.eaglercraft.opengl.ext.deferred.DynamicLightManager.setIsRenderingLights(true);
            ItemStack itemStack = ((LivingEntity) entity).getHeldItemMainhand();
            if (!itemStack.isEmpty()) {
               float[] emission = EmissiveItems.getItemEmission(itemStack);
               if (emission != null) {
                  float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
                  yaw *= 0.017453293f;
                  float s = 0.2f;
                  double d02 = d0 + MathHelper.sin(yaw) * s;
                  double d22 = d2 + MathHelper.cos(yaw) * s;
                  float mag = 0.7f;
                  net.lax1dude.eaglercraft.opengl.ext.deferred.DynamicLightManager.renderDynamicLight("render_view_entity_holding", d02, d1 + entity.getEyeHeight(), d22, emission[0] * mag, emission[1] * mag, emission[2] * mag, false);
               }
            }
            net.lax1dude.eaglercraft.opengl.ext.deferred.DynamicLightManager.setIsRenderingLights(false);
         }
      }

      EaglerDeferredPipeline.instance.combineGBuffersAndIlluminate();

      if (conf.is_rendering_useEnvMap) {
         DeferredStateManager.forwardCallbackHandler = null;
         EaglerDeferredPipeline.instance.beginDrawEnvMap();
         GlStateManager.enableCull();

         EaglerDeferredPipeline.instance.beginDrawEnvMapTop(entity.getEyeHeight());
         EaglerDeferredPipeline.instance.beginDrawEnvMapSolid();
         GlStateManager.disableAlphaTest();
         this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
         this.mc.worldRenderer.renderParaboloidBlockLayer(BlockRenderLayer.SOLID, (double)partialTicks, 1, entity);
         GlStateManager.enableAlphaTest();
         GlStateManager.alphaFunc(516, 0.5F);
         this.mc.worldRenderer.renderParaboloidBlockLayer(BlockRenderLayer.CUTOUT, (double)partialTicks, 1, entity);
         this.mc.worldRenderer.renderParaboloidBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double)partialTicks, 1, entity);
         net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setDefaultMaterialConstants();
         this.mc.worldRenderer.renderParaboloidTileEntities(entity, partialTicks, 1);
         GlStateManager.alphaFunc(516, 0.1F);
         EaglerDeferredPipeline.instance.beginDrawEnvMapTranslucent();
         if (conf.is_rendering_realisticWater) {
            GlStateManager.disableTexture();
            net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.disableMaterialTexture();
            net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setRoughnessConstant(0.117f);
            net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setMetalnessConstant(0.067f);
            net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setEmissionConstant(0.0f);
            GlStateManager.color4f(0.173f, 0.239f, 0.957f, 0.65f);
            this.mc.worldRenderer.renderParaboloidBlockLayer(BlockRenderLayer.TRANSLUCENT, (double)partialTicks, 1, entity);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableTexture();
            net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.enableMaterialTexture();
         }
         this.mc.worldRenderer.renderParaboloidBlockLayer(BlockRenderLayer.TRANSLUCENT, (double)partialTicks, 1, entity);
         this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
         GlStateManager.disableAlphaTest();

         EaglerDeferredPipeline.instance.beginDrawEnvMapBottom(entity.getEyeHeight());
         EaglerDeferredPipeline.instance.beginDrawEnvMapSolid();
         GlStateManager.disableAlphaTest();
         this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
         this.mc.worldRenderer.renderParaboloidBlockLayer(BlockRenderLayer.SOLID, (double)partialTicks, -1, entity);
         GlStateManager.enableAlphaTest();
         GlStateManager.alphaFunc(516, 0.5F);
         this.mc.worldRenderer.renderParaboloidBlockLayer(BlockRenderLayer.CUTOUT, (double)partialTicks, -1, entity);
         this.mc.worldRenderer.renderParaboloidBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double)partialTicks, -1, entity);
         net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setDefaultMaterialConstants();
         this.mc.worldRenderer.renderParaboloidTileEntities(entity, partialTicks, -1);
         GlStateManager.alphaFunc(516, 0.1F);
         EaglerDeferredPipeline.instance.beginDrawEnvMapTranslucent();
         if (conf.is_rendering_realisticWater) {
            GlStateManager.disableTexture();
            net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.disableMaterialTexture();
            net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setRoughnessConstant(0.117f);
            net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setMetalnessConstant(0.067f);
            net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setEmissionConstant(0.0f);
            GlStateManager.color4f(0.173f, 0.239f, 0.957f, 0.65f);
            this.mc.worldRenderer.renderParaboloidBlockLayer(BlockRenderLayer.TRANSLUCENT, (double)partialTicks, -1, entity);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableTexture();
            net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.enableMaterialTexture();
         }
         this.mc.worldRenderer.renderParaboloidBlockLayer(BlockRenderLayer.TRANSLUCENT, (double)partialTicks, -1, entity);
         this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
         GlStateManager.disableAlphaTest();

         EaglerDeferredPipeline.instance.endDrawEnvMap();
      }

      if (conf.is_rendering_realisticWater) {
         EaglerDeferredPipeline.instance.beginDrawRealisticWaterMask();
         this.enableLightmap();
         this.mc.worldRenderer.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, activerenderinfo);
         this.disableLightmap();
         EaglerDeferredPipeline.instance.endDrawRealisticWaterMask();
      }

      int dim = this.mc.world.dimension.getType().getId();
      float ff;
      if (dim == 0) {
         Vec3d fogColorVec = this.mc.world.getFogColor(partialTicks);
         float fogBrightness = (float)fogColorVec.x * 0.3f + (float)fogColorVec.y * 0.59f + (float)fogColorVec.z * 0.11f;
         ff = fogBrightness * 4.8f - 2.8f;
         if (ff < 0.0f) ff = 0.0f;
         if (ff > 1.0f) ff = 1.0f;
      } else {
         ff = 1.0f;
      }

      if (!this.mc.gameSettings.fog) {
         DeferredStateManager.disableFog();
      } else if (entity instanceof LivingEntity && ((LivingEntity)entity).isPotionActive(Effects.BLINDNESS)) {
         float f1 = 5.0F;
         int i = ((LivingEntity) entity).getActivePotionEffect(Effects.BLINDNESS).getDuration();
         if (i < 20) {
            f1 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float) i / 20.0F);
         }
         DeferredStateManager.enableFogLinear(f1 * 0.25F, f1, false, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);
      } else if (entity.areEyesInFluid(FluidTags.WATER)) {
         this.fogRenderer.updateFogColor(activerenderinfo, partialTicks);
         ff *= 0.2f;
         ff += 0.8f;
         Vec3d fogColorVec = this.mc.world.getFogColor(partialTicks);
         float fogRed = (float)fogColorVec.x * 0.5f;
         float fogGreen = (float)fogColorVec.y * 0.5f;
         float fogBlue = (float)fogColorVec.z * 0.5f;
         if (entity instanceof LivingEntity && ((LivingEntity)entity).isPotionActive(Effects.WATER_BREATHING)) {
            DeferredStateManager.enableFogExp(0.01F, false, (float)fogColorVec.x, (float)fogColorVec.y, (float)fogColorVec.z, 1.0f, fogRed * ff, fogGreen * ff, fogBlue * ff, 1.0f);
         } else {
            int respiration = net.minecraft.enchantment.EnchantmentHelper.getRespirationModifier((LivingEntity)entity);
            DeferredStateManager.enableFogExp(0.1F - (float)respiration * 0.03F, false, (float)fogColorVec.x, (float)fogColorVec.y, (float)fogColorVec.z, 1.0f, fogRed * ff, fogGreen * ff, fogBlue * ff, 1.0f);
         }
      } else if (entity.areEyesInFluid(FluidTags.LAVA)) {
         this.fogRenderer.updateFogColor(activerenderinfo, partialTicks);
         Vec3d fogColorVec = this.mc.world.getFogColor(partialTicks);
         DeferredStateManager.enableFogExp(2.0F, false, (float)fogColorVec.x, (float)fogColorVec.y, (float)fogColorVec.z, 1.0f, (float)fogColorVec.x, (float)fogColorVec.y, (float)fogColorVec.z, 1.0f);
      } else {
         float ds = 0.0005f;
         if (this.mc.gameSettings.renderDistanceChunks < 6) {
            ds *= 3.0f - this.mc.gameSettings.renderDistanceChunks * 0.33f;
         }
         ds *= 1.5f + this.mc.world.getRainStrength(partialTicks) * 10.0f + this.mc.world.getThunderStrength(partialTicks) * 5.0f;
         ds *= MathHelper.clamp(6.0f - DeferredStateManager.getSunHeight() * 17.0f, 1.0f, 3.0f);
         if (conf.is_rendering_lightShafts) {
            ds *= Math.max(2.5f - Math.abs(DeferredStateManager.getSunHeight()) * 5.0f, 2.5f);
         }
         DeferredStateManager.enableFogExp(ds, true, 1.0f, 1.0f, 1.0f, 1.0f, ff, ff, ff, 1.0f);
      }

      EaglerDeferredPipeline.instance.beginDrawHDRTranslucent();
      DeferredStateManager.setDefaultMaterialConstants();

      if (conf.is_rendering_realisticWater) {
         EaglerDeferredPipeline.instance.beginDrawRealisticWaterSurface();
         this.mc.worldRenderer.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, activerenderinfo);
         EaglerDeferredPipeline.instance.endDrawRealisticWaterSurface();
      }

      EaglerDeferredPipeline.instance.applyGBufferFog();

      EaglerDeferredPipeline.instance.beginDrawTranslucentEntities();
      this.disableLightmap();

      if (DeferredStateManager.forwardCallbackGBuffer != null) {
         DeferredStateManager.forwardCallbackGBuffer.sort(0.0f, 0.0f, 0.0f);
         java.util.List<net.lax1dude.eaglercraft.opengl.ext.deferred.ShadersRenderPassFuture> lst = DeferredStateManager.forwardCallbackGBuffer.renderPassList;
         for (int i = 0, l = lst.size(); i < l; ++i) {
            lst.get(i).draw(net.lax1dude.eaglercraft.opengl.ext.deferred.ShadersRenderPassFuture.PassType.MAIN);
         }
         DeferredStateManager.forwardCallbackGBuffer.reset();
      }

      EaglerDeferredPipeline.instance.beginDrawTranslucentBlocks();
      this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
      this.mc.worldRenderer.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, activerenderinfo);

      EaglerDeferredPipeline.instance.beginDrawMainGBufferDestroyProgress();

      GlStateManager.enableBlend();
      com.mojang.blaze3d.platform.GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.5f);
      this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      this.mc.worldRenderer.func_215318_a(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), activerenderinfo);
      this.mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

      EaglerDeferredPipeline.instance.endDrawMainGBufferDestroyProgress();

      GlStateManager.pushMatrix();
      net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.setHDRTranslucentPassBlendFunc();
      net.lax1dude.eaglercraft.opengl.ext.deferred.DeferredStateManager.reportForwardRenderObjectPosition2(0.0f, 0.0f, 0.0f);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      this.mc.particles.renderParticles(activerenderinfo, partialTicks);
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.enableBlend();
      GlStateManager.depthMask(true);

      if (conf.is_rendering_useEnvMap) {
         EaglerDeferredPipeline.instance.beginDrawGlassHighlights();
         this.mc.worldRenderer.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, activerenderinfo);
         EaglerDeferredPipeline.instance.endDrawGlassHighlights();
      }

      EaglerDeferredPipeline.instance.saveReprojData();

      this.renderRainSnow(partialTicks);

      GlStateManager.disableBlend();

      if (this.renderHand) {
         EaglerDeferredPipeline.instance.beginDrawHandOverlay();
         DeferredStateManager.reportForwardRenderObjectPosition2(0.0f, 0.0f, 0.0f);
         DeferredStateManager.forwardCallbackHandler = DeferredStateManager.forwardCallbackGBuffer;
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.pushMatrix();
         GlStateManager.enableAlphaTest();
         this.func_215308_a(activerenderinfo, partialTicks);
         DeferredStateManager.forwardCallbackHandler = null;
         GlStateManager.enableBlend();
         DeferredStateManager.setHDRTranslucentPassBlendFunc();
         if (DeferredStateManager.forwardCallbackGBuffer != null) {
            java.util.List<net.lax1dude.eaglercraft.opengl.ext.deferred.ShadersRenderPassFuture> lst2 = DeferredStateManager.forwardCallbackGBuffer.renderPassList;
            for (int i = 0, l = lst2.size(); i < l; ++i) {
               lst2.get(i).draw(net.lax1dude.eaglercraft.opengl.ext.deferred.ShadersRenderPassFuture.PassType.MAIN);
            }
            DeferredStateManager.forwardCallbackGBuffer.reset();
         }
         GlStateManager.matrixMode(5889);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         EaglerDeferredPipeline.instance.endDrawHandOverlay();
         GlStateManager.disableBlend();
         GlStateManager.disableAlphaTest();
      }

      EaglerDeferredPipeline.instance.endDrawHDRTranslucent();

      EaglerDeferredPipeline.instance.endDrawDeferred();

      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);

      GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
      GlStateManager.enableDepthTest();
      GlStateManager.depthMask(true);

      if (!net.lax1dude.eaglercraft.opengl.ext.deferred.DebugFramebufferView.debugViewShown) {
         GlStateManager.disableAlphaTest();
         if (flag && this.mc.objectMouseOver != null) {
            this.mc.worldRenderer.drawSelectionBox(activerenderinfo, this.mc.objectMouseOver, 0);
         }
         GlStateManager.enableAlphaTest();
         if (NameTagRenderer.nameTagsCount > 0) {
            this.enableLightmap();
            java.util.Arrays.sort(NameTagRenderer.nameTagsThisFrame, 0, NameTagRenderer.nameTagsCount, (n1, n2) -> {
               return n1.dst2 < n2.dst2 ? 1 : (n1.dst2 > n2.dst2 ? -1 : 0);
            });
            for (int i = 0; i < NameTagRenderer.nameTagsCount; ++i) {
               net.lax1dude.eaglercraft.opengl.ext.deferred.NameTagRenderer n = NameTagRenderer.nameTagsThisFrame[i];
               int ii = n.entityIn.getBrightnessForRender();
               int j = ii % 65536;
               int k = ii / 65536;
               net.lax1dude.eaglercraft.opengl.OpenGlHelper.setLightmapTextureCoords(net.lax1dude.eaglercraft.opengl.OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               if (n.maxDistance == -69) {
                  net.minecraft.client.renderer.entity.EntityRenderer.renderNameAdapter(this.mc.getRenderManager().getRenderer(n.entityIn), n.entityIn, n.x, n.y, n.z);
               } else {
                  net.minecraft.client.renderer.entity.EntityRenderer.renderNameAdapter(this.mc.getRenderManager().getRenderer(n.entityIn), n.entityIn, n.x, n.y, n.z);
               }
            }
            NameTagRenderer.nameTagsCount = 0;
         }
         this.disableLightmap();
         GlStateManager.disableLighting();
         this.mc.worldRenderer.renderWorldBorder(activerenderinfo, partialTicks);
      }
   }

   private static void setupSunCameraTransform(float celestialAngle) {
      com.mojang.blaze3d.platform.GlStateManager.rotatef(celestialAngle + 90.0f, 1.0F, 0.0F, 0.0F);
      com.mojang.blaze3d.platform.GlStateManager.rotatef(-DeferredStateManager.sunAngle, 0.0F, 1.0F, 0.0F);
      com.mojang.blaze3d.platform.GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
   }

   private static final net.lax1dude.eaglercraft.vector.Matrix4f matrixToBounds_tmpMat4f_1 = new net.lax1dude.eaglercraft.vector.Matrix4f();
   private static final net.lax1dude.eaglercraft.vector.Vector4f matrixToBounds_tmpVec4f_1 = new net.lax1dude.eaglercraft.vector.Vector4f();
   private static final net.lax1dude.eaglercraft.vector.Vector4f[] matrixToBounds_tmpVec4f_array = new net.lax1dude.eaglercraft.vector.Vector4f[] {
      new net.lax1dude.eaglercraft.vector.Vector4f(-1, -1, -1, 1),
      new net.lax1dude.eaglercraft.vector.Vector4f(-1, -1, 1, 1),
      new net.lax1dude.eaglercraft.vector.Vector4f(-1, 1, -1, 1),
      new net.lax1dude.eaglercraft.vector.Vector4f(-1, 1, 1, 1),
      new net.lax1dude.eaglercraft.vector.Vector4f(1, -1, -1, 1),
      new net.lax1dude.eaglercraft.vector.Vector4f(1, -1, 1, 1),
      new net.lax1dude.eaglercraft.vector.Vector4f(1, 1, -1, 1),
      new net.lax1dude.eaglercraft.vector.Vector4f(1, 1, 1, 1)
   };

   private static AxisAlignedBB matrixToBounds(net.lax1dude.eaglercraft.vector.Matrix4f matrixIn, double x, double y, double z) {
      net.lax1dude.eaglercraft.vector.Matrix4f.invert(matrixIn, matrixToBounds_tmpMat4f_1);
      float minX = Float.MAX_VALUE;
      float minY = Float.MAX_VALUE;
      float minZ = Float.MAX_VALUE;
      float maxX = Float.MIN_VALUE;
      float maxY = Float.MIN_VALUE;
      float maxZ = Float.MIN_VALUE;
      net.lax1dude.eaglercraft.vector.Vector4f tmpVec = matrixToBounds_tmpVec4f_1;
      for (int i = 0; i < 8; ++i) {
         net.lax1dude.eaglercraft.vector.Matrix4f.transform(matrixToBounds_tmpMat4f_1, matrixToBounds_tmpVec4f_array[i], tmpVec);
         if (tmpVec.x < minX) minX = tmpVec.x;
         if (tmpVec.y < minY) minY = tmpVec.y;
         if (tmpVec.z < minZ) minZ = tmpVec.z;
         if (tmpVec.x > maxX) maxX = tmpVec.x;
         if (tmpVec.y > maxY) maxY = tmpVec.y;
         if (tmpVec.z > maxZ) maxZ = tmpVec.z;
      }
      return new AxisAlignedBB(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z);
   }
}

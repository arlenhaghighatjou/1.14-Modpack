package net.minecraft.client.gui.screen.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Set;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ContainerScreen<T extends Container> extends Screen implements IHasContainer<T> {
   public static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation("textures/gui/container/inventory.png");
   protected int xSize = 176;
   protected int ySize = 166;
   protected final T container;
   protected final PlayerInventory playerInventory;
   protected int guiLeft;
   protected int guiTop;

   public int getGuiLeft() {
      return this.guiLeft;
   }

   public int getGuiTop() {
      return this.guiTop;
   }
   protected int titleX;
   protected int titleY;
   protected int playerInventoryTitleX;
   protected int playerInventoryTitleY;

   protected int getSlotColor(int index) {
      return -2130706433;
   }

   protected Slot hoveredSlot;
   protected Slot clickedSlot;
   protected boolean isRightMouseClick;
   protected ItemStack draggedStack = ItemStack.EMPTY;
   private int touchUpX;
   private int touchUpY;
   private Slot returningStackDestSlot;
   private long returningStackTime;
   private ItemStack returningStack = ItemStack.EMPTY;
   private Slot currentDragTargetSlot;
   private long dragItemDropDelay;
   protected final Set<Slot> dragSplittingSlots = Sets.newHashSet();
   protected boolean dragSplitting;
   protected int dragSplittingLimit;
   protected int dragSplittingButton;
   private boolean ignoreMouseUp;
   protected int dragSplittingRemnant;
   private long lastClickTime;
   private Slot lastClickSlot;
   private int lastClickButton;
   protected boolean doubleClick;
   protected ItemStack shiftClickedSlot = ItemStack.EMPTY;
   private int escWarningTimer;

   public ContainerScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
      super(titleIn);
      this.container = screenContainer;
      this.playerInventory = inv;
      this.ignoreMouseUp = true;
   }

   protected void init() {
      super.init();
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      int i = this.guiLeft;
      int j = this.guiTop;
      this.drawGuiContainerBackgroundLayer(p_render_3_, p_render_1_, p_render_2_);
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.disableDepthTest();
      super.render(p_render_1_, p_render_2_, p_render_3_);

      if (this.escWarningTimer > 0) {
         String text = "Use '" + this.mc.gameSettings.keyBindInventory.getLocalizedName() + "' to close the screen";
         int textWidth = this.font.getStringWidth(text);
         this.fill(this.width / 2 - textWidth / 2 - 2, 2, this.width / 2 + textWidth / 2 + 2, 14, 0xAA000000);
         this.drawCenteredString(this.font, text, this.width / 2, 4, 0xFF5555);
      }
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)i, (float)j, 0.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      this.hoveredSlot = null;
      int k = 240;
      int l = 240;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

      for(int i1 = 0; i1 < this.container.inventorySlots.size(); ++i1) {
         Slot slot = this.container.inventorySlots.get(i1);
         if (slot.isEnabled()) {
            this.drawSlot(slot);
         }

         if (this.isSlotSelected(slot, (double)p_render_1_, (double)p_render_2_) && slot.isEnabled()) {
            this.hoveredSlot = slot;
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            int j1 = slot.xPos;
            int k1 = slot.yPos;
            GlStateManager.colorMask(true, true, true, false);
            this.fillGradient(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }
      }

      RenderHelper.disableStandardItemLighting();
      this.drawGuiContainerForegroundLayer(p_render_1_, p_render_2_);
      RenderHelper.enableGUIStandardItemLighting();
      PlayerInventory playerinventory = this.mc.player.inventory;
      ItemStack itemstack = this.draggedStack.isEmpty() ? playerinventory.getItemStack() : this.draggedStack;
      if (!itemstack.isEmpty()) {
         int j2 = 8;
         int k2 = this.draggedStack.isEmpty() ? 8 : 16;
         String s = null;
         if (!this.draggedStack.isEmpty() && this.isRightMouseClick) {
            itemstack = itemstack.copy();
            itemstack.setCount(MathHelper.ceil((float)itemstack.getCount() / 2.0F));
         } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
            itemstack = itemstack.copy();
            itemstack.setCount(this.dragSplittingRemnant);
            if (itemstack.isEmpty()) {
               s = "" + TextFormatting.YELLOW + "0";
            }
         }

         this.drawItemStack(itemstack, p_render_1_ - i - 8, p_render_2_ - j - k2, s);
      }

      if (!this.returningStack.isEmpty()) {
         float f = (float)(Util.milliTime() - this.returningStackTime) / 100.0F;
         if (f >= 1.0F) {
            f = 1.0F;
            this.returningStack = ItemStack.EMPTY;
         }

         int l2 = this.returningStackDestSlot.xPos - this.touchUpX;
         int i3 = this.returningStackDestSlot.yPos - this.touchUpY;
         int l1 = this.touchUpX + (int)((float)l2 * f);
         int i2 = this.touchUpY + (int)((float)i3 * f);
         this.drawItemStack(this.returningStack, l1, i2, (String)null);
      }

      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
      GlStateManager.enableDepthTest();
      RenderHelper.enableStandardItemLighting();
   }

   protected void renderHoveredToolTip(int p_191948_1_, int p_191948_2_) {
      if (this.mc.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
         this.renderTooltip(this.hoveredSlot.getStack(), p_191948_1_, p_191948_2_);
      }

   }

   private void drawItemStack(ItemStack stack, int x, int y, String altText) {
      GlStateManager.translatef(0.0F, 0.0F, 32.0F);
      this.blitOffset = 200;
      this.itemRenderer.zLevel = 200.0F;
      this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
      this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x, y - (this.draggedStack.isEmpty() ? 0 : 8), altText);
      this.blitOffset = 0;
      this.itemRenderer.zLevel = 0.0F;
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
   }

   protected abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);

   protected void drawSlot(Slot slotIn) {
      int i = slotIn.xPos;
      int j = slotIn.yPos;
      ItemStack itemstack = slotIn.getStack();
      boolean flag = false;
      boolean flag1 = slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && !this.isRightMouseClick;
      ItemStack itemstack1 = this.mc.player.inventory.getItemStack();
      String s = null;
      if (slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && this.isRightMouseClick && !itemstack.isEmpty()) {
         itemstack = itemstack.copy();
         itemstack.setCount(itemstack.getCount() / 2);
      } else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && !itemstack1.isEmpty()) {
         if (this.dragSplittingSlots.size() == 1) {
            return;
         }

         if (Container.canAddItemToSlot(slotIn, itemstack1, true) && this.container.canDragIntoSlot(slotIn)) {
            itemstack = itemstack1.copy();
            flag = true;
            Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slotIn.getStack().isEmpty() ? 0 : slotIn.getStack().getCount());
            int k = Math.min(itemstack.getMaxStackSize(), slotIn.getItemStackLimit(itemstack));
            if (itemstack.getCount() > k) {
               s = TextFormatting.YELLOW.toString() + k;
               itemstack.setCount(k);
            }
         } else {
            this.dragSplittingSlots.remove(slotIn);
            this.updateDragSplitting();
         }
      }

      this.blitOffset = 100;
      this.itemRenderer.zLevel = 100.0F;
      if (itemstack.isEmpty() && slotIn.isEnabled()) {
         String s1 = slotIn.getSlotTexture();
         if (s1 != null) {
            TextureAtlasSprite textureatlassprite = this.mc.getTextureMap().getAtlasSprite(s1);
            GlStateManager.disableLighting();
            this.mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            blit(i, j, this.blitOffset, 16, 16, textureatlassprite);
            GlStateManager.enableLighting();
            flag1 = true;
         }
      }

      if (!flag1) {
         if (flag) {
            fill(i, j, i + 16, j + 16, -2130706433);
         }

         GlStateManager.enableDepthTest();
         this.itemRenderer.renderItemAndEffectIntoGUI(this.mc.player, itemstack, i, j);
         this.itemRenderer.renderItemOverlayIntoGUI(this.font, itemstack, i, j, s);
      }

      this.itemRenderer.zLevel = 0.0F;
      this.blitOffset = 0;
   }

   protected void updateDragSplitting() {
      ItemStack itemstack = this.mc.player.inventory.getItemStack();
      if (!itemstack.isEmpty() && this.dragSplitting) {
         if (this.dragSplittingLimit == 2) {
            this.dragSplittingRemnant = itemstack.getMaxStackSize();
         } else {
            this.dragSplittingRemnant = itemstack.getCount();

            for(Slot slot : this.dragSplittingSlots) {
               ItemStack itemstack1 = itemstack.copy();
               ItemStack itemstack2 = slot.getStack();
               int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
               Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);
               int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));
               if (itemstack1.getCount() > j) {
                  itemstack1.setCount(j);
               }

               this.dragSplittingRemnant -= itemstack1.getCount() - i;
            }

         }
      }
   }

   protected Slot getSelectedSlot(double p_195360_1_, double p_195360_3_) {
      for(int i = 0; i < this.container.inventorySlots.size(); ++i) {
         Slot slot = this.container.inventorySlots.get(i);
         if (this.isSlotSelected(slot, p_195360_1_, p_195360_3_) && slot.isEnabled()) {
            return slot;
         }
      }

      return null;
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         boolean flag = this.mc.gameSettings.keyBindPickBlock.matchesMouseKey(p_mouseClicked_5_);
         Slot slot = this.getSelectedSlot(p_mouseClicked_1_, p_mouseClicked_3_);
         long i = Util.milliTime();
         this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == p_mouseClicked_5_;
         this.ignoreMouseUp = false;
         if (p_mouseClicked_5_ == 0 || p_mouseClicked_5_ == 1 || flag) {
            int j = this.guiLeft;
            int k = this.guiTop;
            boolean flag1 = this.hasClickedOutside(p_mouseClicked_1_, p_mouseClicked_3_, j, k, p_mouseClicked_5_);
            int l = -1;
            if (slot != null) {
               l = slot.slotNumber;
            }

            if (flag1) {
               l = -999;
            }

            if (this.mc.gameSettings.touchscreen && flag1 && this.mc.player.inventory.getItemStack().isEmpty()) {
               this.mc.displayGuiScreen((Screen)null);
               return true;
            }

            if (l != -1) {
               if (this.mc.gameSettings.touchscreen) {
                  if (slot != null && slot.getHasStack()) {
                     this.clickedSlot = slot;
                     this.draggedStack = ItemStack.EMPTY;
                     this.isRightMouseClick = p_mouseClicked_5_ == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if (!this.dragSplitting) {
                  if (this.mc.player.inventory.getItemStack().isEmpty()) {
                     if (this.mc.gameSettings.keyBindPickBlock.matchesMouseKey(p_mouseClicked_5_)) {
                        this.handleMouseClick(slot, l, p_mouseClicked_5_, ClickType.CLONE);
                     } else {
                        boolean flag2 = l != -999 && (InputMappings.isKeyDown( 340) || InputMappings.isKeyDown( 344));
                        ClickType clicktype = ClickType.PICKUP;
                        if (flag2) {
                           this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                           clicktype = ClickType.QUICK_MOVE;
                        } else if (l == -999) {
                           clicktype = ClickType.THROW;
                        }

                        this.handleMouseClick(slot, l, p_mouseClicked_5_, clicktype);
                     }

                     this.ignoreMouseUp = true;
                  } else {
                     this.dragSplitting = true;
                     this.dragSplittingButton = p_mouseClicked_5_;
                     this.dragSplittingSlots.clear();
                     if (p_mouseClicked_5_ == 0) {
                        this.dragSplittingLimit = 0;
                     } else if (p_mouseClicked_5_ == 1) {
                        this.dragSplittingLimit = 1;
                     } else if (this.mc.gameSettings.keyBindPickBlock.matchesMouseKey(p_mouseClicked_5_)) {
                        this.dragSplittingLimit = 2;
                     }
                  }
               }
            }
         }

         this.lastClickSlot = slot;
         this.lastClickTime = i;
         this.lastClickButton = p_mouseClicked_5_;
         return true;
      }
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      return p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      Slot slot = this.getSelectedSlot(p_mouseDragged_1_, p_mouseDragged_3_);
      ItemStack itemstack = this.mc.player.inventory.getItemStack();
      if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
         if (p_mouseDragged_5_ == 0 || p_mouseDragged_5_ == 1) {
            if (this.draggedStack.isEmpty()) {
               if (slot != this.clickedSlot && !this.clickedSlot.getStack().isEmpty()) {
                  this.draggedStack = this.clickedSlot.getStack().copy();
               }
            } else if (this.draggedStack.getCount() > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false)) {
               long i = Util.milliTime();
               if (this.currentDragTargetSlot == slot) {
                  if (i - this.dragItemDropDelay > 500L) {
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
                     this.handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
                     this.dragItemDropDelay = i + 750L;
                     this.draggedStack.shrink(1);
                  }
               } else {
                  this.currentDragTargetSlot = slot;
                  this.dragItemDropDelay = i;
               }
            }
         }
      } else if (this.dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.dragSplittingSlots.size() || this.dragSplittingLimit == 2) && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && this.container.canDragIntoSlot(slot)) {
         this.dragSplittingSlots.add(slot);
         this.updateDragSplitting();
      }

      return true;
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      Slot slot = this.getSelectedSlot(p_mouseReleased_1_, p_mouseReleased_3_);
      int i = this.guiLeft;
      int j = this.guiTop;
      boolean flag = this.hasClickedOutside(p_mouseReleased_1_, p_mouseReleased_3_, i, j, p_mouseReleased_5_);
      int k = -1;
      if (slot != null) {
         k = slot.slotNumber;
      }

      if (flag) {
         k = -999;
      }

      if (this.doubleClick && slot != null && p_mouseReleased_5_ == 0 && this.container.canMergeSlot(ItemStack.EMPTY, slot)) {
         if (hasShiftDown()) {
            if (!this.shiftClickedSlot.isEmpty()) {
               for(Slot slot2 : this.container.inventorySlots) {
                  if (slot2 != null && slot2.canTakeStack(this.mc.player) && slot2.getHasStack() && slot2.inventory == slot.inventory && Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true)) {
                     this.handleMouseClick(slot2, slot2.slotNumber, p_mouseReleased_5_, ClickType.QUICK_MOVE);
                  }
               }
            }
         } else {
            this.handleMouseClick(slot, k, p_mouseReleased_5_, ClickType.PICKUP_ALL);
         }

         this.doubleClick = false;
         this.lastClickTime = 0L;
      } else {
         if (this.dragSplitting && this.dragSplittingButton != p_mouseReleased_5_) {
            this.dragSplitting = false;
            this.dragSplittingSlots.clear();
            this.ignoreMouseUp = true;
            return true;
         }

         if (this.ignoreMouseUp) {
            this.ignoreMouseUp = false;
            return true;
         }

         if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
            if (p_mouseReleased_5_ == 0 || p_mouseReleased_5_ == 1) {
               if (this.draggedStack.isEmpty() && slot != this.clickedSlot) {
                  this.draggedStack = this.clickedSlot.getStack();
               }

               boolean flag2 = Container.canAddItemToSlot(slot, this.draggedStack, false);
               if (k != -1 && !this.draggedStack.isEmpty() && flag2) {
                  this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, p_mouseReleased_5_, ClickType.PICKUP);
                  this.handleMouseClick(slot, k, 0, ClickType.PICKUP);
                  if (this.mc.player.inventory.getItemStack().isEmpty()) {
                     this.returningStack = ItemStack.EMPTY;
                  } else {
                     this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, p_mouseReleased_5_, ClickType.PICKUP);
                     this.touchUpX = MathHelper.floor(p_mouseReleased_1_ - (double)i);
                     this.touchUpY = MathHelper.floor(p_mouseReleased_3_ - (double)j);
                     this.returningStackDestSlot = this.clickedSlot;
                     this.returningStack = this.draggedStack;
                     this.returningStackTime = Util.milliTime();
                  }
               } else if (!this.draggedStack.isEmpty()) {
                  this.touchUpX = MathHelper.floor(p_mouseReleased_1_ - (double)i);
                  this.touchUpY = MathHelper.floor(p_mouseReleased_3_ - (double)j);
                  this.returningStackDestSlot = this.clickedSlot;
                  this.returningStack = this.draggedStack;
                  this.returningStackTime = Util.milliTime();
               }

               this.draggedStack = ItemStack.EMPTY;
               this.clickedSlot = null;
            }
         } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
            this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit), ClickType.QUICK_CRAFT);

            for(Slot slot1 : this.dragSplittingSlots) {
               this.handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
            }

            this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
         } else if (!this.mc.player.inventory.getItemStack().isEmpty()) {
            if (this.mc.gameSettings.keyBindPickBlock.matchesMouseKey(p_mouseReleased_5_)) {
               this.handleMouseClick(slot, k, p_mouseReleased_5_, ClickType.CLONE);
            } else {
               boolean flag1 = k != -999 && (InputMappings.isKeyDown( 340) || InputMappings.isKeyDown( 344));
               if (flag1) {
                  this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
               }

               this.handleMouseClick(slot, k, p_mouseReleased_5_, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
            }
         }
      }

      if (this.mc.player.inventory.getItemStack().isEmpty()) {
         this.lastClickTime = 0L;
      }

      this.dragSplitting = false;
      return true;
   }

   protected boolean isSlotSelected(Slot p_195362_1_, double p_195362_2_, double p_195362_4_) {
      return this.isPointInRegion(p_195362_1_.xPos, p_195362_1_.yPos, 16, 16, p_195362_2_, p_195362_4_);
   }

   protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
      int i = this.guiLeft;
      int j = this.guiTop;
      p_195359_5_ = p_195359_5_ - (double)i;
      p_195359_7_ = p_195359_7_ - (double)j;
      return p_195359_5_ >= (double)(p_195359_1_ - 1) && p_195359_5_ < (double)(p_195359_1_ + p_195359_3_ + 1) && p_195359_7_ >= (double)(p_195359_2_ - 1) && p_195359_7_ < (double)(p_195359_2_ + p_195359_4_ + 1);
   }

   protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
      if (slotIn != null) {
         slotId = slotIn.slotNumber;
      }

      this.mc.playerController.windowClick(this.container.windowId, slotId, mouseButton, type, this.mc.player);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else {
         if (this.mc.gameSettings.keyBindInventory.matchesKey(p_keyPressed_1_, p_keyPressed_2_) || p_keyPressed_1_ == 96) {
            this.mc.player.closeScreen();
         } else if (p_keyPressed_1_ == 256 && !this.shouldCloseOnEsc()) {
            this.escWarningTimer = 60;
         }

         this.func_195363_d(p_keyPressed_1_, p_keyPressed_2_);
         if (this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            if (this.mc.gameSettings.keyBindPickBlock.matchesKey(p_keyPressed_1_, p_keyPressed_2_)) {
               this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 0, ClickType.CLONE);
            } else if (this.mc.gameSettings.keyBindDrop.matchesKey(p_keyPressed_1_, p_keyPressed_2_)) {
               this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, hasControlDown() ? 1 : 0, ClickType.THROW);
            }
         }

         return true;
      }
   }

   protected boolean func_195363_d(int p_195363_1_, int p_195363_2_) {
      if (this.mc.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null) {
         for(int i = 0; i < 9; ++i) {
            if (this.mc.gameSettings.keyBindsHotbar[i].matchesKey(p_195363_1_, p_195363_2_)) {
               this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, i, ClickType.SWAP);
               return true;
            }
         }
      }

      return false;
   }

   public void removed() {
      if (this.mc.player != null) {
         this.container.onContainerClosed(this.mc.player);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void tick() {
      super.tick();
      if (this.escWarningTimer > 0) {
         --this.escWarningTimer;
      }
      if (!this.mc.player.isAlive() || this.mc.player.removed) {
         this.mc.player.closeScreen();
      }

   }

   public T getContainer() {
      return this.container;
   }

   protected boolean primaryTouchPoint = false;
   public int lastTouchX = -1;
   public int lastTouchY = -1;

   public void touchStarted(int x, int y, int evt) {
      if (!primaryTouchPoint) {
         primaryTouchPoint = true;
         lastTouchX = x;
         lastTouchY = y;
         this.mouseClicked(x, y, 0);
      }
   }

   public void touchMoved(int x, int y, int evt) {
      if (primaryTouchPoint) {
         this.mouseDragged(x, y, 0, x - lastTouchX, y - lastTouchY);
         lastTouchX = x;
         lastTouchY = y;
      }
   }

   public void touchEndMove(int x, int y, int evt) {
      if (primaryTouchPoint) {
         primaryTouchPoint = false;
         this.mouseReleased(x, y, 0);
      }
   }

   public void touchTapped(int x, int y, int evt) {
      if (primaryTouchPoint) {
         primaryTouchPoint = false;
         this.mouseReleased(x, y, 0);
      }
   }

   public boolean shouldTouchGenerateMouseEvents() {
      return false;
   }

   public float getTouchModeScale() {
      return 1.25f;
   }
}

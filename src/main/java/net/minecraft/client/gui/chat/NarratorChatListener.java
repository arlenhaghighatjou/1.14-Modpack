package net.minecraft.client.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NarratorChatListener implements IChatListener {
   public static final ITextComponent field_216868_a = new StringTextComponent("");
   private static final Logger LOGGER = LogManager.getLogger();
   public static final NarratorChatListener INSTANCE = new NarratorChatListener();

   public void say(ChatType chatTypeIn, ITextComponent message) {

   }

   public void func_216864_a(String p_216864_1_) {

   }

   private static NarratorStatus func_223131_d() {
      return Minecraft.getInstance().gameSettings.narrator;
   }

   private void func_216866_a(boolean p_216866_1_, String p_216866_2_) {

   }

   public void func_216865_a(NarratorStatus p_216865_1_) {

   }

   public boolean isActive() {
      return false;
   }

   public void clear() {

   }

   public void func_216867_c() {

   }
}

package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractResourcePackList;
import net.minecraft.client.gui.widget.list.AvailableResourcePackList;
import net.minecraft.client.gui.widget.list.SelectedResourcePackList;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourcePacksScreen extends Screen {
   private final Screen parentScreen;
   private AvailableResourcePackList availableResourcePacksList;
   private SelectedResourcePackList selectedResourcePacksList;
   private boolean changed;

   public ResourcePacksScreen(Screen parentScreenIn) {
      super(new TranslationTextComponent("resourcePack.title"));
      this.parentScreen = parentScreenIn;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 154, this.height - 48, 150, 20, I18n.format("resourcePack.openFolder"), (p_214298_1_) -> {
         net.lax1dude.eaglercraft.EagRuntime.displayFileChooser("application/zip", "zip");
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 48, 150, 20, I18n.format("gui.done"), (p_214296_1_) -> {
         if (this.changed) {
            List<ClientResourcePackInfo> list1 = Lists.newArrayList();

            for(AbstractResourcePackList.ResourcePackEntry abstractresourcepacklist$resourcepackentry : this.selectedResourcePacksList.children()) {
               list1.add(abstractresourcepacklist$resourcepackentry.func_214418_e());
            }

            Collections.reverse(list1);
            this.mc.getResourcePackList().setEnabledPacks(list1);
            this.mc.gameSettings.resourcePacks.clear();
            this.mc.gameSettings.incompatibleResourcePacks.clear();

            for(ClientResourcePackInfo clientresourcepackinfo2 : list1) {
               if (!clientresourcepackinfo2.isOrderLocked()) {
                  this.mc.gameSettings.resourcePacks.add(clientresourcepackinfo2.getName());
                  if (!clientresourcepackinfo2.getCompatibility().func_198968_a()) {
                     this.mc.gameSettings.incompatibleResourcePacks.add(clientresourcepackinfo2.getName());
                  }
               }
            }

            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentScreen);
            this.mc.reloadResources();
         } else {
            this.mc.displayGuiScreen(this.parentScreen);
         }

      }));
      AvailableResourcePackList availableresourcepacklist = this.availableResourcePacksList;
      SelectedResourcePackList selectedresourcepacklist = this.selectedResourcePacksList;
      this.availableResourcePacksList = new AvailableResourcePackList(this.mc, 200, this.height);
      this.availableResourcePacksList.setLeftPos(this.width / 2 - 4 - 200);
      if (availableresourcepacklist != null) {
         this.availableResourcePacksList.children().addAll(availableresourcepacklist.children());
      }

      this.children.add(this.availableResourcePacksList);
      this.selectedResourcePacksList = new SelectedResourcePackList(this.mc, 200, this.height);
      this.selectedResourcePacksList.setLeftPos(this.width / 2 + 4);
      if (selectedresourcepacklist != null) {
         this.selectedResourcePacksList.children().addAll(selectedresourcepacklist.children());
      }

      this.children.add(this.selectedResourcePacksList);
      if (!this.changed) {
         this.availableResourcePacksList.children().clear();
         this.selectedResourcePacksList.children().clear();
         ResourcePackList<ClientResourcePackInfo> resourcepacklist = this.mc.getResourcePackList();
         resourcepacklist.reloadPacksFromFinders();
         List<ClientResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getAllPacks());
         list.removeAll(resourcepacklist.getEnabledPacks());

         for(ClientResourcePackInfo clientresourcepackinfo : list) {
            this.availableResourcePacksList.func_214365_a(new AbstractResourcePackList.ResourcePackEntry(this.availableResourcePacksList, this, clientresourcepackinfo));
         }

         for(ClientResourcePackInfo clientresourcepackinfo1 : Lists.reverse(Lists.newArrayList(resourcepacklist.getEnabledPacks()))) {
            this.selectedResourcePacksList.func_214365_a(new AbstractResourcePackList.ResourcePackEntry(this.selectedResourcePacksList, this, clientresourcepackinfo1));
         }
      }

   }

   public void func_214300_a(AbstractResourcePackList.ResourcePackEntry p_214300_1_) {
      this.availableResourcePacksList.children().remove(p_214300_1_);
      p_214300_1_.func_214422_a(this.selectedResourcePacksList);
      this.markChanged();
   }

   public void func_214297_b(AbstractResourcePackList.ResourcePackEntry p_214297_1_) {
      this.selectedResourcePacksList.children().remove(p_214297_1_);
      this.availableResourcePacksList.func_214365_a(p_214297_1_);
      this.markChanged();
   }

   public boolean func_214299_c(AbstractResourcePackList.ResourcePackEntry p_214299_1_) {
      return this.selectedResourcePacksList.children().contains(p_214299_1_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderDirtBackground(0);
      this.availableResourcePacksList.render(p_render_1_, p_render_2_, p_render_3_);
      this.selectedResourcePacksList.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.font, I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void markChanged() {
      this.changed = true;
   }

   public void tick() {
      if (net.lax1dude.eaglercraft.EagRuntime.fileChooserHasResult()) {
         net.lax1dude.eaglercraft.internal.FileChooserResult result = net.lax1dude.eaglercraft.EagRuntime.getFileChooserResult();
         if (result != null) {
            try {
               net.lax1dude.eaglercraft.minecraft.EaglerFolderResourcePack.importResourcePack(result.fileName, net.lax1dude.eaglercraft.minecraft.EaglerFolderResourcePack.RESOURCE_PACKS, result.fileData);
               this.changed = false;
               this.init(this.mc, this.width, this.height);
            } catch (java.io.IOException e) {
               org.apache.logging.log4j.LogManager.getLogger().error("Could not load resource pack: {}", result.fileName);
               org.apache.logging.log4j.LogManager.getLogger().error(e);
               this.mc.displayGuiScreen(new AlertScreen(() -> {
                  this.mc.displayGuiScreen(this);
               }, new TranslationTextComponent("resourcePack.title"), new TranslationTextComponent("Failed to import resource pack: " + result.fileName)));
            }
         }
      }
   }
}

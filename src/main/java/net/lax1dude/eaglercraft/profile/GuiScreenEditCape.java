package net.lax1dude.eaglercraft.profile;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.internal.FileChooserResult;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiScreenEditCape extends Screen {

    private static final ResourceLocation eaglerGui = new ResourceLocation("eagler:gui/eagler_gui.png");
    private final GuiScreenEditProfile parent;
    protected int selectedSlot = 0;
    protected String screenTitle = "Edit Cape";
    private boolean dropDownOpen = false;
    private String[] dropDownOptions;
    private int slotsVisible = 0;
    private int scrollPos = -1;
    private int skinsHeight = 0;
    private boolean dragging = false;
    private int mousex = 0;
    private int mousey = 0;

    public GuiScreenEditCape(GuiScreenEditProfile parent) {
        super(new TranslationTextComponent("editCape.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.mc.keyboardListener.enableRepeatEvents(true);
        screenTitle = I18n.format("editCape.title");
        selectedSlot = EaglerProfile.presetCapeId == -1 ? GuiScreenEditProfile.customCapeId : (EaglerProfile.presetCapeId + GuiScreenEditProfile.customCapes.size());
        addButton(new Button(width / 2 - 100, height / 6 + 168, 200, 20, I18n.format("gui.done"), (btn) -> {
            safeProfile();
            this.mc.displayGuiScreen(parent);
        }));
        addButton(new Button(width / 2 - 21, height / 6 + 80, 71, 20, I18n.format("editCape.addCape"), (btn) -> {
            EagRuntime.displayFileChooser("image/png", "png");
        }));
        addButton(new Button(width / 2 - 21 + 71, height / 6 + 80, 72, 20, I18n.format("editCape.clearCape"), (btn) -> {
            GuiScreenEditProfile.clearCustomCapes();
            safeProfile();
            updateOptions();
            selectedSlot = 0;
        }));
        updateOptions();
    }

    private void updateOptions() {
        int numCustom = GuiScreenEditProfile.customCapes.size();
        String[] n = new String[numCustom + DefaultCapes.defaultCapesMap.length];
        for (int i = 0; i < numCustom; ++i) {
            n[i] = GuiScreenEditProfile.customCapes.get(i).name;
        }
        int numDefault = DefaultCapes.defaultCapesMap.length;
        for (int j = 0; j < numDefault; ++j) {
            n[numCustom + j] = DefaultCapes.defaultCapesMap[j].name;
        }
        dropDownOptions = n;
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        renderBackground();
        drawCenteredString(font, screenTitle, width / 2, 15, 16777215);
        drawString(font, I18n.format("editCape.playerCape"), width / 2 - 20, height / 6 + 36, 10526880);

        mousex = mx;
        mousey = my;

        int skinX = width / 2 - 120;
        int skinY = height / 6 + 8;
        int skinWidth = 80;
        int skinHeight = 130;

        fill(skinX, skinY, skinX + skinWidth, skinY + skinHeight, 0xFFA0A0A0);
        fill(skinX + 1, skinY + 1, skinX + skinWidth - 1, skinY + skinHeight - 1, 0xFF000015);

        if (!dropDownOpen) {
            super.render(mx, my, partialTicks);
        } else {
            super.render(0, 0, partialTicks);
        }

        int numberOfCustomSkins = GuiScreenEditProfile.customSkins.size();
        int numberOfCustomCapes = GuiScreenEditProfile.customCapes.size();
        ResourceLocation skinTexture;
        SkinModel model;
        if (parent.selectedSlot < numberOfCustomSkins) {
            GuiScreenEditProfile.CustomSkin customSkin = GuiScreenEditProfile.customSkins.get(parent.selectedSlot);
            skinTexture = customSkin.getResource();
            model = customSkin.model;
        } else {
            DefaultSkins defaultSkin = DefaultSkins.getSkinFromId(parent.selectedSlot - numberOfCustomSkins);
            skinTexture = defaultSkin.location;
            model = defaultSkin.model;
        }

        if (model.highPoly != null) {
            drawCenteredString(font, I18n.format(this.mc.gameSettings.enableFNAWSkins ? "editProfile.disableFNAW" : "editProfile.enableFNAW"), width / 2, height / 6 + 150, 10526880);
        }

        skinX = width / 2 - 20;
        skinY = height / 6 + 52;
        skinWidth = 140;
        skinHeight = 22;

        fill(skinX, skinY, skinX + skinWidth, skinY + skinHeight, -6250336);
        fill(skinX + 1, skinY + 1, skinX + skinWidth - 21, skinY + skinHeight - 1, -16777216);
        fill(skinX + skinWidth - 20, skinY + 1, skinX + skinWidth - 1, skinY + skinHeight - 1, -16777216);

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        mc.getTextureManager().bindTexture(eaglerGui);
        blit(skinX + skinWidth - 18, skinY + 3, 0, 0, 16, 16);

        drawString(font, dropDownOptions[selectedSlot], skinX + 5, skinY + 7, 14737632);

        skinX = width / 2 - 20;
        skinY = height / 6 + 73;
        skinWidth = 140;
        skinHeight = (height - skinY - 10);
        slotsVisible = (skinHeight / 10);
        if (slotsVisible > dropDownOptions.length) slotsVisible = dropDownOptions.length;
        skinHeight = slotsVisible * 10 + 7;
        skinsHeight = skinHeight;
        if (scrollPos == -1) {
            scrollPos = selectedSlot - 2;
        }
        if (scrollPos > (dropDownOptions.length - slotsVisible)) {
            scrollPos = (dropDownOptions.length - slotsVisible);
        }
        if (scrollPos < 0) {
            scrollPos = 0;
        }
        if (dropDownOpen) {
            fill(skinX, skinY, skinX + skinWidth, skinY + skinHeight, -6250336);
            fill(skinX + 1, skinY + 1, skinX + skinWidth - 1, skinY + skinHeight - 1, -16777216);
            for (int i = 0; i < slotsVisible; i++) {
                if (i + scrollPos < dropDownOptions.length) {
                    if (selectedSlot == i + scrollPos) {
                        fill(skinX + 1, skinY + i * 10 + 4, skinX + skinWidth - 1, skinY + i * 10 + 14, 0x77ffffff);
                    } else if (mx >= skinX && mx < (skinX + skinWidth - 10) && my >= (skinY + i * 10 + 5) && my < (skinY + i * 10 + 15)) {
                        fill(skinX + 1, skinY + i * 10 + 4, skinX + skinWidth - 1, skinY + i * 10 + 14, 0x55ffffff);
                    }
                    drawString(font, dropDownOptions[i + scrollPos], skinX + 5, skinY + 5 + i * 10, 14737632);
                }
            }
            int scrollerSize = skinHeight * slotsVisible / dropDownOptions.length;
            int scrollerPos = skinHeight * scrollPos / dropDownOptions.length;
            fill(skinX + skinWidth - 4, skinY + scrollerPos + 1, skinX + skinWidth - 1, skinY + scrollerPos + scrollerSize, 0xff888888);
        }

        if (!EagRuntime.getConfiguration().isDemo()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.75f, 0.75f, 0.75f);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            String text = I18n.format("editProfile.importExport");

            int w = font.getStringWidth(text);
            boolean hover = mx > 1 && my > 1 && mx < (w * 3 / 4) + 7 && my < 12;

            drawString(font, TextFormatting.UNDERLINE + text, 5, 5, hover ? 0xFFEEEE22 : 0xFFCCCCCC);

            GlStateManager.popMatrix();
        }

        int xx = width / 2 - 80;
        int yy = height / 6 + 130;

        skinX = this.width / 2 - 120;
        skinY = this.height / 6 + 8;
        skinWidth = 80;
        skinHeight = 130;

        ResourceLocation capeTexture;
        if (selectedSlot < numberOfCustomCapes) {
            capeTexture = GuiScreenEditProfile.customCapes.get(selectedSlot).getResource();
        } else {
            capeTexture = DefaultCapes.getCapeFromId(selectedSlot - numberOfCustomCapes).location;
        }

        SkinPreviewRenderer.renderPreview(xx, yy, mx, my, true, model, skinTexture, capeTexture);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (dropDownOpen) {
            if (amount < 0) {
                scrollPos += 3;
            }
            if (amount > 0) {
                scrollPos -= 3;
                if (scrollPos < 0) {
                    scrollPos = 0;
                }
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void tick() {
        if (EagRuntime.fileChooserHasResult()) {
            FileChooserResult result = EagRuntime.getFileChooserResult();
            if (result != null) {
                ImageData loadedCape = ImageData.loadImageFile(result.fileData, ImageData.getMimeFromType(result.fileName));
                if (loadedCape != null) {
                    if ((loadedCape.width == 32 || loadedCape.width == 64) && loadedCape.height == 32) {
                        byte[] resized = new byte[1173];
                        SkinConverter.convertCape32x32RGBAto23x17RGB(loadedCape, resized);
                        int k;
                        if ((k = GuiScreenEditProfile.addCustomCape(result.fileName, resized)) != -1) {
                            selectedSlot = k;
                            updateOptions();
                            safeProfile();
                        }
                    } else {
                        EagRuntime.showPopup("The selected image '" + result.fileName + "' is not the right size!\nEaglercraft only supports 32x32 or 64x32 capes");
                    }
                } else {
                    EagRuntime.showPopup("The selected file '" + result.fileName + "' is not a supported format!");
                }
            }
        }
        if (dropDownOpen) {
            if (Mouse.isButtonDown(0)) {
                int skinX = width / 2 - 20;
                int skinY = height / 6 + 73;
                int skinWidth = 140;
                if (mousex >= (skinX + skinWidth - 10) && mousex < (skinX + skinWidth) && mousey >= skinY && mousey < (skinY + skinsHeight)) {
                    dragging = true;
                }
                if (dragging) {
                    int scrollerSize = skinsHeight * slotsVisible / dropDownOptions.length;
                    scrollPos = (mousey - skinY - (scrollerSize / 2)) * dropDownOptions.length / skinsHeight;
                }
            } else {
                dragging = false;
            }
        } else {
            dragging = false;
        }
    }

    @Override
    public void removed() {
        if (this.mc != null) {
            this.mc.keyboardListener.enableRepeatEvents(false);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 200 && selectedSlot > 0) {
            --selectedSlot;
            scrollPos = selectedSlot - 2;
            return true;
        }
        if (keyCode == 208 && selectedSlot < (dropDownOptions.length - 1)) {
            ++selectedSlot;
            scrollPos = selectedSlot - 2;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            if (!EagRuntime.getConfiguration().isDemo()) {
                int w = font.getStringWidth(I18n.format("editProfile.importExport"));
                if (mx > 1 && my > 1 && mx < (w * 3 / 4) + 7 && my < 12) {
                    safeProfile();
                    this.mc.gameSettings.saveOptions();
                    this.mc.displayGuiScreen(new GuiScreenImportExportProfile(this.parent));
                    return true;
                }
            }

            int skinX = width / 2 + 140 - 40;
            int skinY = height / 6 + 52;

            if (mx >= skinX && mx < (skinX + 20) && my >= skinY && my < (skinY + 22)) {
                dropDownOpen = !dropDownOpen;
                return true;
            }

            skinX = width / 2 - 20;
            skinY = height / 6 + 52;
            int skinWidth = 140;

            if (!(mx >= skinX && mx < (skinX + skinWidth) && my >= skinY && my < (skinY + skinsHeight + 22))) {
                dragging = false;
                if (dropDownOpen) {
                    dropDownOpen = false;
                    return true;
                }
            } else if (dropDownOpen && !dragging) {
                skinY += 21;
                for (int i = 0; i < slotsVisible; i++) {
                    if (i + scrollPos < dropDownOptions.length) {
                        if (mx >= skinX && mx < (skinX + skinWidth - 10) && my >= (skinY + i * 10 + 5) && my < (skinY + i * 10 + 15) && selectedSlot != i + scrollPos) {
                            selectedSlot = i + scrollPos;
                            dropDownOpen = false;
                            dragging = false;
                            return true;
                        }
                    }
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    protected void safeProfile() {
        int customLen = GuiScreenEditProfile.customCapes.size();
        if (selectedSlot < customLen) {
            EaglerProfile.presetCapeId = -1;
            GuiScreenEditProfile.customCapeId = selectedSlot;
        } else {
            EaglerProfile.presetCapeId = selectedSlot - customLen;
            GuiScreenEditProfile.customCapeId = -1;
        }
    }

}

package net.p3pp3rf1y.sophisticatedbackpacks.client;

import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingBackpackItemEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackTileEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.GameSettings;
import com.mojang.blaze3d.platform.GlStateManager;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.p3pp3rf1y.sophisticatedbackpacks.util.inventory.ItemHandlerLookup;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.init.ModBlockColors;
import net.p3pp3rf1y.sophisticatedbackpacks.client.init.ModItemColors;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackDynamicModel;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackLayerRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackTESR;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackTooltipRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.CommonProxy;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModParticles;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackCloseMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackInsertMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackOpenMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BlockToolSwapMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.EntityToolSwapMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.InventoryInteractionMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.network.UpgradeToggleMessage;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.battery.BatteryUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.BackpackSoundHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.tank.TankUpgradeContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.toolswapper.ToolSwapperFilterContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.util.RecipeHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.RegistryHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.WorldHelper;

import java.util.Collections;
import java.util.Map;

import static net.p3pp3rf1y.sophisticatedbackpacks.client.gui.utils.TranslationHelper.translKeybind;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.EVERLASTING_BACKPACK_ITEM_ENTITY;

public class ClientProxy extends CommonProxy {
	private static final int KEY_B = 66;
	private static final int KEY_C = 67;
	private static final int KEY_Z = 90;
	private static final int KEY_X = 88;
	private static final int KEY_UNKNOWN = -1;
	private static final int MIDDLE_BUTTON = 2;

	private static final String KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY = "keybind.sophisticatedbackpacks.category";

	public static final KeyBinding BACKPACK_OPEN_KEYBIND = new KeyBinding(translKeybind("open_backpack"), InputMappings.Type.KEYSYM, KEY_B, KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyBinding INVENTORY_INTERACTION_KEYBIND = new KeyBinding(translKeybind("inventory_interaction"), InputMappings.Type.KEYSYM, KEY_C, KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyBinding TOOL_SWAP_KEYBIND = new KeyBinding(translKeybind("tool_swap"), InputMappings.Type.KEYSYM, KEY_UNKNOWN, KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyBinding SORT_KEYBIND = new KeyBinding(translKeybind("sort"), InputMappings.Type.MOUSE, MIDDLE_BUTTON, KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);

	public static final KeyBinding BACKPACK_TOGGLE_UPGRADE_1 = new KeyBinding(translKeybind("toggle_upgrade_1"), InputMappings.Type.KEYSYM, KEY_Z, KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyBinding BACKPACK_TOGGLE_UPGRADE_2 = new KeyBinding(translKeybind("toggle_upgrade_2"), InputMappings.Type.KEYSYM, KEY_X, KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyBinding BACKPACK_TOGGLE_UPGRADE_3 = new KeyBinding(translKeybind("toggle_upgrade_3"), InputMappings.Type.KEYSYM, KEY_UNKNOWN, KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyBinding BACKPACK_TOGGLE_UPGRADE_4 = new KeyBinding(translKeybind("toggle_upgrade_4"), InputMappings.Type.KEYSYM, KEY_UNKNOWN, KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);
	public static final KeyBinding BACKPACK_TOGGLE_UPGRADE_5 = new KeyBinding(translKeybind("toggle_upgrade_5"), InputMappings.Type.KEYSYM, KEY_UNKNOWN, KEYBIND_SOPHISTICATEDBACKPACKS_CATEGORY);

	private static final Map<Integer, KeyBinding> UPGRADE_SLOT_TOGGLE_KEYBINDS = ImmutableMap.of(
			0, BACKPACK_TOGGLE_UPGRADE_1,
			1, BACKPACK_TOGGLE_UPGRADE_2,
			2, BACKPACK_TOGGLE_UPGRADE_3,
			3, BACKPACK_TOGGLE_UPGRADE_4,
			4, BACKPACK_TOGGLE_UPGRADE_5
	);
	private static final int CHEST_SLOT_INDEX = 6;

	private static boolean tryCallSort(Screen gui) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && mc.player.openContainer instanceof BackpackContainer && gui instanceof BackpackScreen) {
			BackpackScreen screen = (BackpackScreen) gui;
			MouseHelper mh = mc.mouseHelper;
			double mouseX = mh.getMouseX() * mc.mainWindow.getScaledWidth() / mc.mainWindow.getWidth();
			double mouseY = mh.getMouseY() * mc.mainWindow.getScaledHeight() / mc.mainWindow.getHeight();
			BackpackContainer container = (BackpackContainer) mc.player.openContainer;
			Slot selectedSlot = screen.getSelectedSlot(mouseX, mouseY);
			if (selectedSlot != null && !container.isPlayersInventorySlot(selectedSlot.slotNumber)) {
				container.sort();
				return true;
			}
		}
		return false;
	}

	public static boolean handleGuiKeyPress(Screen gui, int keyCode, int scanCode) {
		return SORT_KEYBIND.isActiveAndMatches(InputMappings.getInputByCode(keyCode, scanCode)) && tryCallSort(gui);
	}

	public static boolean handleGuiMouseKeyPress(Screen gui, int button) {
		InputMappings.Input input = InputMappings.Type.MOUSE.getOrMakeInput(button);
		if (SORT_KEYBIND.isActiveAndMatches(input) && tryCallSort(gui)) {
			return true;
		}
		if (BACKPACK_OPEN_KEYBIND.isActiveAndMatches(input)) {
			sendBackpackOpenOrCloseMessage();
			return true;
		}
		return false;
	}

	public static void handleKeyInputEvent() {
		if (BACKPACK_OPEN_KEYBIND.isPressed()) {
			sendBackpackOpenOrCloseMessage();
		} else if (INVENTORY_INTERACTION_KEYBIND.isPressed()) {
			sendInteractWithInventoryMessage();
		} else if (TOOL_SWAP_KEYBIND.isPressed()) {
			sendToolSwapMessage();
		} else {
			for (Map.Entry<Integer, KeyBinding> slotKeybind : UPGRADE_SLOT_TOGGLE_KEYBINDS.entrySet()) {
				if (slotKeybind.getValue().isPressed()) {
					PacketHandler.sendToServer(new UpgradeToggleMessage(slotKeybind.getKey()));
				}
			}
		}
	}

	private static void sendToolSwapMessage() {
		Minecraft mc = Minecraft.getInstance();
		ClientPlayerEntity player = mc.player;
		if (player == null || mc.objectMouseOver == null) {
			return;
		}
		if (player.getHeldItemMainhand().getItem() instanceof BackpackItem) {
			player.sendStatusMessage(new TranslationTextComponent("gui.sophisticatedbackpacks.status.unable_to_swap_tool_for_backpack"), true);
			return;
		}
		RayTraceResult rayTrace = mc.objectMouseOver;
		if (rayTrace.getType() == RayTraceResult.Type.BLOCK) {
			BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTrace;
			BlockPos pos = blockRayTraceResult.getPos();
			PacketHandler.sendToServer(new BlockToolSwapMessage(pos));
		} else if (rayTrace.getType() == RayTraceResult.Type.ENTITY) {
			EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTrace;
			PacketHandler.sendToServer(new EntityToolSwapMessage(entityRayTraceResult.getEntity().getEntityId()));
		}
	}

	private static void sendInteractWithInventoryMessage() {
		Minecraft mc = Minecraft.getInstance();
		RayTraceResult rayTrace = mc.objectMouseOver;
		if (rayTrace == null || rayTrace.getType() != RayTraceResult.Type.BLOCK) {
			return;
		}
		BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) rayTrace;
		BlockPos pos = blockraytraceresult.getPos();

		if (!WorldHelper.getTile(mc.world, pos, TileEntity.class).map(te -> ItemHandlerLookup.get(te, null).isPresent()).orElse(false)) {
			return;
		}

		PacketHandler.sendToServer(new InventoryInteractionMessage(pos, blockraytraceresult.getFace()));
	}

	@SuppressWarnings({"java:S2440", "InstantiationOfUtilityClass"})
	private static void sendBackpackOpenOrCloseMessage() {
		Screen screen = Minecraft.getInstance().currentScreen;
		if (screen == null) {
			PacketHandler.sendToServer(new BackpackOpenMessage());
			return;
		}

		if (screen instanceof BackpackScreen) {
			BackpackScreen backpackScreen = (BackpackScreen) screen;

			Slot slot = backpackScreen.getSlotUnderMouse();
			if (slot != null && slot.getStack().getItem() instanceof BackpackItem) {
				if (slot.getStack().getCount() == 1) {
					PacketHandler.sendToServer(new BackpackOpenMessage(slot.slotNumber));
				}
			} else {
				PacketHandler.sendToServer(new BackpackCloseMessage());
			}
		} else if (screen instanceof InventoryScreen) {
			Slot slot = ((InventoryScreen) screen).getSlotUnderMouse();

			if (slot != null && isSupportedPlayerInventorySlot(slot.slotNumber) && slot.getStack().getItem() instanceof BackpackItem) {
				PacketHandler.sendToServer(new BackpackOpenMessage(slot.getSlotIndex()));
			}
		}
	}

	private static boolean isSupportedPlayerInventorySlot(int slotIndex) {
		return slotIndex == CHEST_SLOT_INDEX || (slotIndex > 8 && slotIndex < 46);
	}

	@Override
	public void registerClientHandlers() {
		clientSetup();
		stitchTextures();
		ModParticles.registerFactories();
		ModItemColors.init();
		ModBlockColors.init();
		registerBackpackLayer();
	}


	public static void onDrawScreen(int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getInstance();
		Screen gui = mc.currentScreen;
		if (!(gui instanceof ContainerScreen<?>) || gui instanceof CreativeScreen || mc.player == null) {
			return;
		}

		ContainerScreen<?> containerGui = (ContainerScreen<?>) gui;
		Container menu = containerGui.getContainer();
		ClientPlayerEntity player = mc.player;
		ItemStack held = player.inventory.getItemStack();
		if (!held.isEmpty() && !(held.getItem() instanceof BackpackItem)) {
			Slot under = containerGui.hoveredSlot;
			
			for (Slot s : menu.inventorySlots) {
				ItemStack stack = s.getStack();
				if (!s.canTakeStack(player) || stack.getCount() != 1) {
					continue;
				}

				BackpackWrapperLookup.get(stack).ifPresent(backpackWrapper -> {
					if (s == under) {
						GlStateManager.pushMatrix();
						GlStateManager.translated(0, 0, 100);
						BackpackTooltipRenderer.renderTooltipWithContents(stack, mc, mouseX, mouseY, mc.fontRenderer, Collections.singletonList(new TranslationTextComponent("gui.sophisticatedbackpacks.tooltip.right_click_to_add_to_backpack")));
						GlStateManager.popMatrix();
					} else {
						int x = containerGui.getGuiLeft() + s.xPos;
						int y = containerGui.getGuiTop() + s.yPos;

						GlStateManager.pushMatrix();
						GlStateManager.translated(0, 0, 499);
						mc.fontRenderer.drawStringWithShadow("+", (float) x + 10, (float) y + 8, 0xFFFF00);
						GlStateManager.popMatrix();
					}
				});
			}

		}
	}

	public static boolean onRightClick(double mouseX, double mouseY, int button) {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.currentScreen;
		if (screen instanceof ContainerScreen<?> && !(screen instanceof CreativeScreen) && button == 1 && mc.player != null) {
			ContainerScreen<?> container = (ContainerScreen<?>) screen;
			Slot under = container.hoveredSlot;
			ItemStack held = mc.player.inventory.getItemStack();

			if (under != null && !held.isEmpty() && under.canTakeStack(mc.player)) {
				ItemStack stack = under.getStack();
				if (stack.getItem() instanceof BackpackItem && stack.getCount() == 1) {
					PacketHandler.sendToServer(new BackpackInsertMessage(under.slotNumber));
					screen.mouseReleased(0, 0, -1);
					return true;
				}
			}
		}
		return false;
	}

	private void clientSetup() {
		GameSettings.registerKeyBinding(BACKPACK_OPEN_KEYBIND);
		GameSettings.registerKeyBinding(INVENTORY_INTERACTION_KEYBIND);
		GameSettings.registerKeyBinding(TOOL_SWAP_KEYBIND);
		GameSettings.registerKeyBinding(SORT_KEYBIND);
		UPGRADE_SLOT_TOGGLE_KEYBINDS.forEach((slot, keybind) -> GameSettings.registerKeyBinding(keybind));
		Minecraft.getInstance().gameSettings.addModKeyBindings();

		TileEntityRendererDispatcher.instance.register(BackpackTileEntity.class, new BackpackTESR());
	}

	@SuppressWarnings("java:S3740") //explanation below
	private void registerBackpackLayer() {
		EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
		Map<String, PlayerRenderer> skinMap = renderManager.getSkinMap();
		PlayerRenderer render = skinMap.get("default");
		render.addLayer(new BackpackLayerRenderer<>(render));
		render = skinMap.get("slim");
		render.addLayer(new BackpackLayerRenderer<>(render));
		renderManager.renderers.forEach((e, r) -> {
			if (r instanceof LivingRenderer<?, ?>) {
				//noinspection rawtypes ,unchecked - this is not going to fail as the LivingRenderer makes sure the types are right, but there doesn't seem to be a way to us inference here
				((LivingRenderer) r).addLayer(new BackpackLayerRenderer((LivingRenderer) r));
			}
		});
	}

	public void stitchTextures() {
		AtlasTexture.addExtraSprite(BackpackContainer.EMPTY_UPGRADE_SLOT_BACKGROUND);
		AtlasTexture.addExtraSprite(ToolSwapperFilterContainer.EMPTY_WEAPON_SLOT_BACKGROUND);
		ToolSwapperFilterContainer.EMPTY_TOOL_SLOT_BACKGROUNDS.values().forEach(AtlasTexture::addExtraSprite);
		AtlasTexture.addExtraSprite(TankUpgradeContainer.EMPTY_TANK_INPUT_SLOT_BACKGROUND);
		AtlasTexture.addExtraSprite(TankUpgradeContainer.EMPTY_TANK_OUTPUT_SLOT_BACKGROUND);
		AtlasTexture.addExtraSprite(BatteryUpgradeContainer.EMPTY_BATTERY_INPUT_SLOT_BACKGROUND);
		AtlasTexture.addExtraSprite(BatteryUpgradeContainer.EMPTY_BATTERY_OUTPUT_SLOT_BACKGROUND);
	}

	public static void onPlayerJoinServer() {
		//noinspection ConstantConditions - by the time player is joining the world is not null
		RecipeHelper.setWorld(Minecraft.getInstance().world);
	}


}

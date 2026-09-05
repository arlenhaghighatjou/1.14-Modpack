package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IRenderedBatteryUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IRenderedTankUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IUpgradeRenderData;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IUpgradeRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.api.UpgradeRenderDataType;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.TankPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BackpackLayerRenderer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> {
	private static final float CHILD_Y_OFFSET = 0.3F;
	private static final float CHILD_Z_OFFSET = 0.1F;
	private static final float CHILD_SCALE = 0.55F;
	private static final ResourceLocation TANK_GLASS_TEXTURE = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "textures/entity/tank_glass.png");

	private static final BackpackModel MODEL = new BackpackModel();
	private static final TankGlassModel TANK_GLASS_MODEL = new TankGlassModel();

	private static final Map<EntityType<?>, Vec3d> entityTranslations;

	static {
		entityTranslations = new HashMap<>();
		entityTranslations.put(EntityType.ENDERMAN, new Vec3d(0, -0.8, 0));
	}

	public BackpackLayerRenderer(IEntityRenderer<T, M> entityRendererIn) {
		super(entityRendererIn);
	}

	@Override
	public void render(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (entity instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entity;
			SophisticatedBackpacks.PROXY.getPlayerInventoryProvider().getBackpackFromRendered(player).ifPresent(backpackRenderInfo -> {
				boolean wearsArmor = !backpackRenderInfo.isArmorSlot() && !player.inventory.armorInventory.get(EquipmentSlotType.CHEST.getIndex()).isEmpty();
				renderBackpack(player, backpackRenderInfo.getBackpack(), wearsArmor, scale);
			});
		} else {
			ItemStack chestStack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
			if (chestStack.getItem() instanceof BackpackItem) {
				renderBackpack(entity, chestStack, false, scale);
			}
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

	public static void renderBackpack(LivingEntity livingEntity, ItemStack backpack, boolean wearsArmor, float scale) {
		GlStateManager.pushMatrix();

		if (livingEntity.isSneaking()) {
			GlStateManager.translatef(0F, 0.2F, 0F);
			GlStateManager.rotatef(90F / (float) Math.PI, 1F, 0F, 0F);
		}

		GlStateManager.rotatef(180F, 0F, 1F, 0F);
		float zOffset = wearsArmor ? -0.35F : -0.3F;
		float yOffset = -0.75F;

		if (livingEntity.isChild()) {
			zOffset += CHILD_Z_OFFSET;
			yOffset = CHILD_Y_OFFSET;
		}

		GlStateManager.translatef(0F, yOffset, zOffset);

		if (livingEntity.isChild()) {
			GlStateManager.scalef(CHILD_SCALE, CHILD_SCALE, CHILD_SCALE);
		}

		Vec3d translVector = entityTranslations.get(livingEntity.getType());
		if (translVector != null) {
			GlStateManager.translated(translVector.x, translVector.y, translVector.z);
		}

		BackpackWrapperLookup.get(backpack).ifPresent(wrapper -> {
			Minecraft.getInstance().getTextureManager().bindTexture(RenderHelper.BACKPACK_ENTITY_TEXTURE);

			int clothColor = wrapper.getClothColor();
			int borderColor = wrapper.getBorderColor();
			Item backpackItem = backpack.getItem();

			BackpackRenderInfo renderInfo = wrapper.getRenderInfo();
			Set<TankPosition> tankPositions = renderInfo.getTankRenderInfos().keySet();
			boolean showLeftTank = tankPositions.contains(TankPosition.LEFT);
			boolean showRightTank = tankPositions.contains(TankPosition.RIGHT);
			Optional<IRenderedBatteryUpgrade.BatteryRenderInfo> batteryRenderInfo = renderInfo.getBatteryRenderInfo();
			MODEL.render(scale, clothColor, borderColor, backpackItem, showLeftTank, showRightTank, batteryRenderInfo.isPresent());

			renderFluids(scale, renderInfo, showLeftTank, showRightTank);
			batteryRenderInfo.ifPresent(info -> renderBatteryCharge(info.getChargeRatio()));
			renderUpgrades(livingEntity, renderInfo);
			renderItemShown(renderInfo);
		});

		GlStateManager.popMatrix();
	}

	private static void renderItemShown(BackpackRenderInfo renderInfo) {
		BackpackRenderInfo.ItemDisplayRenderInfo itemDisplayRenderInfo = renderInfo.getItemDisplayRenderInfo();
		if (!itemDisplayRenderInfo.getItem().isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0F, 0.9F, -0.25F);
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.rotatef(180F + itemDisplayRenderInfo.getRotation(), 0F, 0F, 1F);
			Minecraft.getInstance().getItemRenderer().renderItem(itemDisplayRenderInfo.getItem(), ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}
	}

	private static void renderUpgrades(LivingEntity livingEntity, BackpackRenderInfo renderInfo) {
		if (Minecraft.getInstance().isGamePaused() || livingEntity.world.rand.nextInt(32) != 0) {
			return;
		}
		renderInfo.getUpgradeRenderData().forEach((type, data) -> UpgradeRenderRegistry.getUpgradeRenderer(type).ifPresent(renderer -> renderUpgrade(renderer, livingEntity, type, data)));
	}

	private static Vec3d getBackpackMiddleFacePoint(LivingEntity livingEntity, Vec3d vector3d) {
		return vector3d.rotatePitch(livingEntity.isSneaking() ? 25 * ((float) Math.PI / 180F) : 0).add(0, 0.8, livingEntity.isSneaking() ? 0.9 : 0.7).rotateYaw((float) (-livingEntity.renderYawOffset * (Math.PI / 180F) - Math.PI)).add(livingEntity.getPositionVector());
	}

	private static <T extends IUpgradeRenderData> void renderUpgrade(IUpgradeRenderer<T> renderer, LivingEntity livingEntity, UpgradeRenderDataType<?> type, IUpgradeRenderData data) {
		//noinspection unchecked
		type.cast(data).ifPresent(renderData -> renderer.render(livingEntity.world, livingEntity.world.rand, vector3d -> getBackpackMiddleFacePoint(livingEntity, vector3d), (T) renderData));
	}

	private static void renderBatteryCharge(float chargeRatio) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef(0F, 1.5F, 0F);
		RenderHelper.renderBatteryCharge(chargeRatio);
		GlStateManager.popMatrix();
	}

	private static void renderFluids(float scale, BackpackRenderInfo renderInfo, boolean showLeftTank, boolean showRightTank) {
		GlStateManager.pushMatrix();
		GlStateManager.scalef(1 / 2F, 6 / 10F, 1 / 2F);
		Minecraft.getInstance().getTextureManager().bindTexture(TANK_GLASS_TEXTURE);
		TANK_GLASS_MODEL.render(scale, showLeftTank, showRightTank);
		if (showLeftTank) {
			IRenderedTankUpgrade.TankRenderInfo tankRenderInfo = renderInfo.getTankRenderInfos().get(TankPosition.LEFT);
			tankRenderInfo.getFluid().ifPresent(fluid -> RenderHelper.renderFluid(fluid, tankRenderInfo.getFillRatio(), -14.5F, 37.5F, -1, -2F));
		}
		if (showRightTank) {
			IRenderedTankUpgrade.TankRenderInfo tankRenderInfo = renderInfo.getTankRenderInfos().get(TankPosition.RIGHT);
			tankRenderInfo.getFluid().ifPresent(fluid -> RenderHelper.renderFluid(fluid, tankRenderInfo.getFillRatio(), 11F, 37.5F, -1, -2F));
		}
		GlStateManager.popMatrix();
	}
}

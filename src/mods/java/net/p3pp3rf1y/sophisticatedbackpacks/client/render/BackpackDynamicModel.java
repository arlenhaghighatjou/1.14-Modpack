package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import com.google.common.collect.ImmutableMap;
import net.lax1dude.eaglercraft.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IRenderedBatteryUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IRenderedTankUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.TankPosition;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidAttributes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

import static net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock.BATTERY;
import static net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock.LEFT_TANK;
import static net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock.RIGHT_TANK;

/**
 * The backpack swaps pouches for tanks or a battery depending on the upgrades it carries, so its
 * model is assembled at bake time out of the seven parts and then picks quads per state or per stack.
 */
public class BackpackDynamicModel implements IBakedModel {
	private static final ResourceLocation MODULES_TEXTURE = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "block/backpack_modules");

	private static final Map<String, String> CLIPS_BY_TIER = ImmutableMap.of(
			"backpack", "sophisticatedbackpacks:block/leather_clips",
			"iron_backpack", "sophisticatedbackpacks:block/iron_clips",
			"gold_backpack", "sophisticatedbackpacks:block/gold_clips",
			"diamond_backpack", "sophisticatedbackpacks:block/diamond_clips",
			"netherite_backpack", "sophisticatedbackpacks:block/netherite_clips");

	private static final Direction[] FACINGS = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	private static final FaceBakery FACE_BAKERY = new FaceBakery();
	private static final BlockPartFace TANK_FLUID_LEFT = fluidFace(2);
	private static final BlockPartFace TANK_FLUID_RIGHT = fluidFace(3);
	private static final BlockPartFace CHARGE_FACE = new BlockPartFace(null, -1, "charge", new BlockFaceUV(new float[] {14F, 6F, 16F, 6.5F}, 0));

	private static final ItemCameraTransforms TRANSFORMS = new ItemCameraTransforms(
			transform(85F, -90F, 0F, 0F, -2F, -4.5F, 0.75F),
			transform(85F, -90F, 0F, 0F, -2F, -4.5F, 0.75F),
			transform(0F, 0F, 0F, 0F, 0F, 0F, 0.5F),
			transform(0F, 0F, 0F, 0F, 0F, 0F, 0.5F),
			transform(0F, 0F, 0F, 0F, 14.25F, 0F, 1F),
			transform(30F, 225F, 0F, 0F, 1.25F, 0F, 0.9F),
			transform(0F, 0F, 0F, 0F, 3F, 0F, 0.5F),
			transform(0F, 0F, 0F, 0F, 0F, -2.25F, 0.75F));

	private final Map<ModelPart, IBakedModel> models;
	private final boolean itemModel;
	private final BackpackItemOverrideList overrideList = new BackpackItemOverrideList(this);
	private final List<BakedQuad>[] quadCache = new List[8 * 7];

	private boolean tankLeft;
	@Nullable
	private IRenderedTankUpgrade.TankRenderInfo leftTankRenderInfo = null;
	private boolean tankRight;
	@Nullable
	private IRenderedTankUpgrade.TankRenderInfo rightTankRenderInfo = null;
	private boolean battery;
	@Nullable
	private IRenderedBatteryUpgrade.BatteryRenderInfo batteryRenderInfo = null;

	private BackpackDynamicModel(Map<ModelPart, IBakedModel> models, boolean itemModel) {
		this.models = models;
		this.itemModel = itemModel;
	}

	/**
	 * Hands the bakery a part model per tier so its clips texture gets stitched along with everything else.
	 */
	public static void registerModels(BiConsumer<ResourceLocation, IUnbakedModel> out) {
		CLIPS_BY_TIER.forEach((tier, clips) -> {
			for (ModelPart part : ModelPart.values()) {
				out.accept(partLocation(tier, part), new BlockModel(part.location(), Collections.emptyList(), ImmutableMap.of("clips", clips), true, true, ItemCameraTransforms.DEFAULT, Collections.emptyList()));
			}
		});
	}

	public static void bakeInto(ModelBakery bakery, Map<ResourceLocation, IBakedModel> registry) {
		CLIPS_BY_TIER.keySet().forEach(tier -> {
			ResourceLocation block = new ResourceLocation(SophisticatedBackpacks.MOD_ID, tier);
			for (Direction facing : FACINGS) {
				ModelRotation rotation = ModelRotation.getModelRotation(0, (int) facing.getHorizontalAngle());
				registry.put(new ModelResourceLocation(block, "facing=" + facing.getName()), new BackpackDynamicModel(bakeParts(bakery, tier, rotation), false));
			}
			registry.put(new ModelResourceLocation(block, "inventory"), new BackpackDynamicModel(bakeParts(bakery, tier, ModelRotation.X0_Y0), true));
		});
	}

	private static Map<ModelPart, IBakedModel> bakeParts(ModelBakery bakery, String tier, ModelRotation rotation) {
		Map<ModelPart, IBakedModel> parts = new EnumMap<>(ModelPart.class);
		for (ModelPart part : ModelPart.values()) {
			parts.put(part, bakery.func_217845_a(partLocation(tier, part), rotation));
		}
		return parts;
	}

	private static ResourceLocation partLocation(String tier, ModelPart part) {
		return new ResourceLocation(SophisticatedBackpacks.MOD_ID, "dynamic/" + tier + "/" + part.name().toLowerCase(Locale.ENGLISH));
	}

	private static BlockPartFace fluidFace(int tintIndex) {
		return new BlockPartFace(null, tintIndex, "fluid", new BlockFaceUV(new float[] {0F, 0F, 5F, 5F}, 0));
	}

	private static ItemTransformVec3f transform(float rotX, float rotY, float rotZ, float trX, float trY, float trZ, float scale) {
		return new ItemTransformVec3f(new Vector3f(rotX, rotY, rotZ), new Vector3f(trX * 0.0625F, trY * 0.0625F, trZ * 0.0625F), new Vector3f(scale, scale, scale));
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		boolean showLeftTank;
		boolean showRightTank;
		boolean showBattery;
		if (state == null) {
			showLeftTank = tankLeft;
			showRightTank = tankRight;
			showBattery = battery;
		} else {
			showLeftTank = state.get(LEFT_TANK);
			showRightTank = state.get(RIGHT_TANK);
			showBattery = state.get(BATTERY);
		}

		List<BakedQuad> statics = getStaticQuads(state, side, rand, showLeftTank, showRightTank, showBattery);

		if (side != null) {
			return statics;
		}

		List<BakedQuad> dynamic = getDynamicQuads(showLeftTank, showRightTank, showBattery);
		if (dynamic.isEmpty()) {
			return statics;
		}

		dynamic.addAll(statics);
		return dynamic;
	}

	private List<BakedQuad> getStaticQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, boolean showLeftTank, boolean showRightTank, boolean showBattery) {
		int index = ((showLeftTank ? 1 : 0) | (showRightTank ? 2 : 0) | (showBattery ? 4 : 0)) * 7 + (side == null ? 6 : side.getIndex());
		List<BakedQuad> cached = quadCache[index];
		if (cached != null) {
			return cached;
		}

		List<BakedQuad> quads = new ArrayList<>(models.get(ModelPart.BASE).getQuads(state, side, rand));
		quads.addAll(models.get(showLeftTank ? ModelPart.LEFT_TANK : ModelPart.LEFT_POUCH).getQuads(state, side, rand));
		quads.addAll(models.get(showRightTank ? ModelPart.RIGHT_TANK : ModelPart.RIGHT_POUCH).getQuads(state, side, rand));
		quads.addAll(models.get(showBattery ? ModelPart.BATTERY : ModelPart.FRONT_POUCH).getQuads(state, side, rand));

		List<BakedQuad> immutable = Collections.unmodifiableList(quads);
		quadCache[index] = immutable;
		return immutable;
	}

	private List<BakedQuad> getDynamicQuads(boolean showLeftTank, boolean showRightTank, boolean showBattery) {
		List<BakedQuad> dynamic = new ArrayList<>();
		if (showLeftTank && leftTankRenderInfo != null) {
			leftTankRenderInfo.getFluid().ifPresent(fluid -> addFluid(dynamic, fluid, leftTankRenderInfo.getFillRatio(), 12.85F, TANK_FLUID_LEFT));
		}
		if (showRightTank && rightTankRenderInfo != null) {
			rightTankRenderInfo.getFluid().ifPresent(fluid -> addFluid(dynamic, fluid, rightTankRenderInfo.getFillRatio(), 0.6F, TANK_FLUID_RIGHT));
		}
		if (showBattery && batteryRenderInfo != null) {
			addCharge(dynamic, batteryRenderInfo.getChargeRatio());
		}
		return dynamic;
	}

	private void addFluid(List<BakedQuad> ret, Fluid fluid, float ratio, float xMin, BlockPartFace face) {
		if (ratio <= 0F) {
			return;
		}

		TextureAtlasSprite still = Minecraft.getInstance().getTextureMap().getSprite(FluidAttributes.getStillTexture(fluid));
		Vector3f from = new Vector3f(xMin, 1.5F, 6.75F);
		Vector3f to = new Vector3f(xMin + 2.5F, 1.5F + ratio * 6F, 9.25F);
		addBox(ret, from, to, face, still);
	}

	private void addCharge(List<BakedQuad> ret, float chargeRatio) {
		if (chargeRatio <= 0F) {
			return;
		}

		int pixels = (int) (chargeRatio * 4);
		if (pixels <= 0) {
			return;
		}

		TextureAtlasSprite sprite = Minecraft.getInstance().getTextureMap().getSprite(MODULES_TEXTURE);
		Vector3f from = new Vector3f(10F - pixels, 2F, 1.95F);
		Vector3f to = new Vector3f(10F, 3F, 1.95F);
		ret.add(FACE_BAKERY.func_217648_a(from, to, CHARGE_FACE, sprite, Direction.NORTH, ModelRotation.X0_Y0, null, false));
	}

	private void addBox(List<BakedQuad> ret, Vector3f from, Vector3f to, BlockPartFace face, TextureAtlasSprite sprite) {
		for (Direction direction : Direction.values()) {
			ret.add(FACE_BAKERY.func_217648_a(from, to, face, sprite, direction, ModelRotation.X0_Y0, null, false));
		}
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return itemModel;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return models.get(ModelPart.BASE).getParticleTexture();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return itemModel ? TRANSFORMS : ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return overrideList;
	}

	private static class BackpackItemOverrideList extends ItemOverrideList {
		private final BackpackDynamicModel backpackModel;

		public BackpackItemOverrideList(BackpackDynamicModel backpackModel) {
			this.backpackModel = backpackModel;
		}

		@Override
		public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
			backpackModel.tankRight = false;
			backpackModel.tankLeft = false;
			backpackModel.battery = false;
			BackpackWrapperLookup.get(stack).ifPresent(backpackWrapper -> {
				BackpackRenderInfo renderInfo = backpackWrapper.getRenderInfo();
				renderInfo.getTankRenderInfos().forEach((pos, info) -> {
					if (pos == TankPosition.LEFT) {
						backpackModel.tankLeft = true;
						backpackModel.leftTankRenderInfo = info;
					} else {
						backpackModel.tankRight = true;
						backpackModel.rightTankRenderInfo = info;
					}
				});
				renderInfo.getBatteryRenderInfo().ifPresent(batteryRenderInfo -> {
					backpackModel.battery = true;
					backpackModel.batteryRenderInfo = batteryRenderInfo;
				});
			});

			return backpackModel;
		}
	}

	private enum ModelPart {
		BASE,
		BATTERY,
		FRONT_POUCH,
		LEFT_POUCH,
		LEFT_TANK,
		RIGHT_POUCH,
		RIGHT_TANK;

		private final ResourceLocation location = new ResourceLocation(SophisticatedBackpacks.MOD_ID, "block/backpack_" + name().toLowerCase(Locale.ENGLISH));

		public ResourceLocation location() {
			return location;
		}
	}
}

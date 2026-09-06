package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidActionResult;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidAttributes;
import net.p3pp3rf1y.sophisticatedbackpacks.util.fluid.FluidUtil;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.inventory.IItemHandlerModifiable;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IUpgradeRenderData;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IUpgradeRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.api.UpgradeRenderDataType;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.UpgradeRenderRegistry;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.ServerBackpackSoundHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.WorldHelper;

import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

public class BackpackBlock extends Block implements IWaterLoggable {
	public static final BooleanProperty LEFT_TANK = BooleanProperty.create("left_tank");
	public static final BooleanProperty RIGHT_TANK = BooleanProperty.create("right_tank");
	public static final BooleanProperty BATTERY = BooleanProperty.create("battery");

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final int BEDROCK_RESISTANCE = 3600000;

	public BackpackBlock() {
		super(Properties.of(Material.WOOL).noOcclusion().strength(0.8F).sound(SoundType.WOOL));
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(LEFT_TANK, false).setValue(RIGHT_TANK, false));
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState pState) {
		return PushReaction.DESTROY;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, World level, BlockPos pos) {
		return WorldHelper.getTile(level, pos, BackpackTileEntity.class).map(t -> {
			IItemHandlerModifiable handler = t.getBackpackWrapper().getInventoryForInputOutput();
			AtomicDouble totalFilled = new AtomicDouble(0);
			AtomicBoolean isEmpty = new AtomicBoolean(true);
			InventoryHelper.iterate(handler, (slot, stack) -> {
				if (!stack.isEmpty()) {
					int slotLimit = handler.getSlotLimit(slot);
					totalFilled.addAndGet(stack.getCount() / (slotLimit / ((float) 64 / stack.getMaxStackSize())));
					isEmpty.set(false);
				}
			});
			double percentFilled = totalFilled.get() / handler.getSlots();
			return MathHelper.floor(percentFilled * 14.0F) + (isEmpty.get() ? 0 : 1);
		}).orElse(0);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return Boolean.TRUE.equals(state.get(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (Boolean.TRUE.equals(stateIn.get(WATERLOGGED))) {
			worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
		}

		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, LEFT_TANK, RIGHT_TANK, BATTERY);
	}

	@Override
	public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
		if (hasEverlastingUpgrade(world, pos)) {
			return BEDROCK_RESISTANCE;
		}
		return super.getExplosionResistance(state, world, pos, explosion);
	}

	private boolean hasEverlastingUpgrade(IBlockReader world, BlockPos pos) {
		return WorldHelper.getTile(world, pos, BackpackTileEntity.class).map(te -> !te.getBackpackWrapper().getUpgradeHandler().getTypeWrappers(EverlastingUpgradeItem.TYPE).isEmpty()).orElse(false);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BackpackShapes.getShape(state.get(FACING), state.get(LEFT_TANK), state.get(RIGHT_TANK), state.get(BATTERY));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new BackpackTileEntity();
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}

		ItemStack heldItem = player.getHeldItem(hand);
		if (player.isSneaking() && heldItem.isEmpty()) {
			putInPlayersHandAndRemove(state, world, pos, player, hand);
			return ActionResultType.SUCCESS;
		}

		if (!heldItem.isEmpty() && FluidHandlerLookup.getItem(heldItem).isPresent()) {
			WorldHelper.getTile(world, pos, BackpackTileEntity.class)
					.flatMap(te -> te.getBackpackWrapper().getFluidHandler()).ifPresent(backpackFluidHandler ->
							ItemHandlerLookup.get(player, null).ifPresent(playerInventory -> {
								FluidActionResult resultOfEmptying = FluidUtil.tryEmptyContainerAndStow(heldItem, backpackFluidHandler, playerInventory, FluidAttributes.BUCKET, player, true);
								if (resultOfEmptying.isSuccess()) {
									player.setHeldItem(hand, resultOfEmptying.getResult());
								} else {
									FluidActionResult resultOfFilling = FluidUtil.tryFillContainerAndStow(heldItem, backpackFluidHandler, playerInventory, FluidAttributes.BUCKET, player, true);
									if (resultOfFilling.isSuccess()) {
										player.setHeldItem(hand, resultOfFilling.getResult());
									}
								}
							}));
			return ActionResultType.SUCCESS;
		}

		BackpackContext.Block backpackContext = new BackpackContext.Block(pos);
		PacketHandler.openContainer((ServerPlayerEntity) player, new SimpleNamedContainerProvider((w, p, pl) -> new BackpackContainer(w, pl, backpackContext),
				getBackpackDisplayName(world, pos)), backpackContext);
		return ActionResultType.SUCCESS;
	}

	private ITextComponent getBackpackDisplayName(World world, BlockPos pos) {
		ITextComponent defaultDisplayName = new ItemStack(ModItems.BACKPACK).getDisplayName();
		return WorldHelper.getTile(world, pos, BackpackTileEntity.class).map(te -> te.getBackpackWrapper().getBackpack().getDisplayName()).orElse(defaultDisplayName);
	}

	private static void putInPlayersHandAndRemove(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
		ItemStack backpack = WorldHelper.getTile(world, pos, BackpackTileEntity.class).map(te -> te.getBackpackWrapper().getBackpack()).orElse(ItemStack.EMPTY);
		player.setHeldItem(hand, backpack);
		player.getCooldowns().addCooldown(backpack.getItem(), 5);
		world.removeBlock(pos, false);

		stopBackpackSounds(backpack, world, pos);

		SoundType soundType = state.getSoundType();
		world.playSound(null, pos, soundType.getBreakSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
	}

	private static void stopBackpackSounds(ItemStack backpack, World world, BlockPos pos) {
		BackpackWrapperLookup.get(backpack).ifPresent(wrapper -> wrapper.getContentsUuid().ifPresent(uuid ->
				ServerBackpackSoundHandler.stopPlayingDisc((ServerWorld) world, new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D), uuid))
		);
	}

	public static boolean playerInteract(PlayerEntity player, World world, BlockPos pos) {
		if (!player.isSneaking() || !hasEmptyMainHandAndSomethingInOffhand(player)) {
			return false;
		}

		BlockState state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof BackpackBlock)) {
			return false;
		}

		if (world.isRemote) {
			return true;
		}

		putInPlayersHandAndRemove(state, world, pos, player, player.getHeldItemMainhand().isEmpty() ? Hand.MAIN_HAND : Hand.OFF_HAND);
		return true;
	}

	private static boolean hasEmptyMainHandAndSomethingInOffhand(PlayerEntity player) {
		return player.getHeldItemMainhand().isEmpty() && !player.getHeldItemOffhand().isEmpty();
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		super.entityInside(state, world, pos, entity);
		if (!world.isRemote && entity instanceof ItemEntity) {
			ItemEntity itemEntity = (ItemEntity) entity;
			WorldHelper.getTile(world, pos, BackpackTileEntity.class).ifPresent(te -> tryToPickup(world, itemEntity, te.getBackpackWrapper()));
		}
	}

	@Override
	public boolean canEntityDestroy(BlockState state, IBlockReader world, BlockPos pos, Entity entity) {
		if (hasEverlastingUpgrade(world, pos)) {
			return false;
		}
		return super.canEntityDestroy(state, world, pos, entity);
	}

	private void tryToPickup(World world, ItemEntity itemEntity, IBackpackWrapper w) {
		ItemStack remainingStack = itemEntity.getItem().copy();
		remainingStack = InventoryHelper.runPickupOnBackpack(world, remainingStack, w, false);
		if (remainingStack.getCount() < itemEntity.getItem().getCount()) {
			itemEntity.setItem(remainingStack);
		}
	}

	@Override
	public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
		WorldHelper.getTile(level, pos, BackpackTileEntity.class).ifPresent(te -> {
			BackpackRenderInfo renderInfo = te.getBackpackWrapper().getRenderInfo();
			renderUpgrades(level, rand, pos, state.get(FACING), renderInfo);
		});

	}

	private static void renderUpgrades(World world, Random rand, BlockPos pos, Direction facing, BackpackRenderInfo renderInfo) {
		if (Minecraft.getInstance().isPaused()) {
			return;
		}
		renderInfo.getUpgradeRenderData().forEach((type, data) -> UpgradeRenderRegistry.getUpgradeRenderer(type).ifPresent(renderer -> renderUpgrade(renderer, world, rand, pos, facing, type, data)));
	}

	private static Vec3d getBackpackMiddleFacePoint(BlockPos pos, Direction facing, Vec3d vector3d) {
		return vector3d.add(0, 0, 0.41).yRot((float) (-facing.toYRot() * (Math.PI / 180F))).add(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
	}

	private static <T extends IUpgradeRenderData> void renderUpgrade(IUpgradeRenderer<T> renderer, World world, Random rand, BlockPos pos, Direction facing, UpgradeRenderDataType<?> type, IUpgradeRenderData data) {
		//noinspection unchecked
		type.cast(data).ifPresent(renderData -> renderer.render(world, rand, vector3d -> getBackpackMiddleFacePoint(pos, facing, vector3d), (T) renderData));
	}
}
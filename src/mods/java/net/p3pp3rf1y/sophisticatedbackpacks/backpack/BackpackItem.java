package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapperLookup;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.p3pp3rf1y.sophisticatedbackpacks.util.LazyOptional;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackISTER;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.BackpackContext;
import net.p3pp3rf1y.sophisticatedbackpacks.crafting.BackpackDyeRecipe;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingBackpackItemEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.everlasting.EverlastingUpgradeItem;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.jukebox.ServerBackpackSoundHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.util.InventoryInteractionHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.util.ItemBase;
import net.p3pp3rf1y.sophisticatedbackpacks.util.PlayerInventoryProvider;
import net.p3pp3rf1y.sophisticatedbackpacks.util.WorldHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

public class BackpackItem extends ItemBase {
    public static final String BACKPACK_TOOLTIP = "item.sophisticatedbackpacks.backpack.tooltip.";
    private final IntSupplier numberOfSlots;
    private final IntSupplier numberOfUpgradeSlots;
    private final Supplier<BackpackBlock> blockSupplier;

    public BackpackItem(IntSupplier numberOfSlots, IntSupplier numberOfUpgradeSlots, Supplier<BackpackBlock> blockSupplier) {
        this(numberOfSlots, numberOfUpgradeSlots, blockSupplier, p -> p);
    }

    public BackpackItem(IntSupplier numberOfSlots, IntSupplier numberOfUpgradeSlots, Supplier<BackpackBlock> blockSupplier, UnaryOperator<Properties> updateProperties) {
        super(updateProperties.apply(new Properties().stacksTo(1).setISTER(() -> BackpackISTER::new)));
        this.numberOfSlots = numberOfSlots;
        this.numberOfUpgradeSlots = numberOfUpgradeSlots;
        this.blockSupplier = blockSupplier;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);

        if (!allowdedIn(group) || this != ModItems.BACKPACK || !Config.COMMON.enabledItems.isItemEnabled(this)) {
            return;
        }

        for (DyeColor color : DyeColor.values()) {
            ItemStack stack = new ItemStack(this);
            new BackpackWrapper(stack).setColors(color.getColorValue(), color.getColorValue());
            items.add(stack);
        }

        int clothColor = BackpackDyeRecipe.calculateColor(BackpackWrapper.DEFAULT_CLOTH_COLOR, BackpackWrapper.DEFAULT_CLOTH_COLOR, ImmutableList.of(
                DyeColor.BLUE, DyeColor.YELLOW, DyeColor.LIME
        ));
        int trimColor = BackpackDyeRecipe.calculateColor(BackpackWrapper.DEFAULT_BORDER_COLOR, BackpackWrapper.DEFAULT_BORDER_COLOR, ImmutableList.of(
                DyeColor.BLUE, DyeColor.BLACK
        ));

        ItemStack stack = new ItemStack(this);
        new BackpackWrapper(stack).setColors(clothColor, trimColor);
        items.add(stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (flagIn == ITooltipFlag.TooltipFlags.ADVANCED) {
            BackpackWrapperLookup.get(stack)
                    .ifPresent(w -> w.getContentsUuid().ifPresent(uuid -> tooltip.add(new StringTextComponent("UUID: " + uuid).applyTextStyle(TextFormatting.DARK_GRAY))));
        }
        if (!Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent(
                    BACKPACK_TOOLTIP + "press_for_contents",
                    new TranslationTextComponent(BACKPACK_TOOLTIP + "shift").applyTextStyle(TextFormatting.AQUA)
            ).applyTextStyle(TextFormatting.GRAY));
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return hasEverlastingUpgrade(stack);
    }

    private boolean hasEverlastingUpgrade(ItemStack stack) {
        return BackpackWrapperLookup.get(stack).map(w -> !w.getUpgradeHandler().getTypeWrappers(EverlastingUpgradeItem.TYPE).isEmpty()).orElse(false);
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity entity, ItemStack itemstack) {
        if (!(entity instanceof ItemEntity)) {
            return null;
        }
        return hasEverlastingUpgrade(itemstack) ? createEverlastingBackpack(world, (ItemEntity) entity, itemstack) : null;
    }

    @Nullable
    private EverlastingBackpackItemEntity createEverlastingBackpack(World world, ItemEntity itemEntity, ItemStack itemstack) {
        EverlastingBackpackItemEntity backpackItemEntity = ModItems.EVERLASTING_BACKPACK_ITEM_ENTITY.create(world);
        if (backpackItemEntity != null) {
            backpackItemEntity.setPos(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
            backpackItemEntity.setItem(itemstack);
            backpackItemEntity.setPickUpDelay(getPickupDelay(itemEntity));
            backpackItemEntity.setThrower(itemEntity.getThrower());
            backpackItemEntity.setDeltaMovement(itemEntity.getDeltaMovement());
        }
        return backpackItemEntity;
    }

    private int getPickupDelay(ItemEntity itemEntity) {
        Integer result = itemEntity.getPickupDelay();
        if (result == null) {
            SophisticatedBackpacks.LOGGER.error("Reflection get of pickupDelay (pickupDelay) from ItemEntity returned null");
            return 20;
        }
        return result;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null || !player.isSneaking()) {
            return ActionResultType.PASS;
        }

        if (InventoryInteractionHelper.tryInventoryInteraction(context)) {
            return ActionResultType.SUCCESS;
        }

        Direction direction = player.getDirection().getOpposite();

        BlockItemUseContext blockItemUseContext = new BlockItemUseContext(context);
        ActionResultType result = tryPlace(player, direction, blockItemUseContext);
        return result == ActionResultType.PASS ? super.useOn(context) : result;
    }

    public ActionResultType tryPlace(@Nullable PlayerEntity player, Direction direction, BlockItemUseContext blockItemUseContext) {
        if (!blockItemUseContext.canPlace()) {
            return ActionResultType.FAIL;
        }
        World world = blockItemUseContext.getWorld();
        BlockPos pos = blockItemUseContext.getPos();

        FluidState fluidstate = blockItemUseContext.getWorld().getFluidState(pos);
        BlockState placementState = blockSupplier.get().defaultBlockState().setValue(BackpackBlock.FACING, direction)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        if (!canPlace(blockItemUseContext, placementState)) {
            return ActionResultType.FAIL;
        }

        if (world.setBlockAndUpdate(pos, placementState)) {
            ItemStack backpack = blockItemUseContext.getItemInHand();
            WorldHelper.getTile(world, pos, BackpackTileEntity.class).ifPresent(te -> {
                te.setBackpack(getBackpackCopy(player, backpack));
                te.refreshRenderState();
            });

            if (!world.isRemote) {
                stopBackpackSounds(backpack, world, pos);
            }

            SoundType soundtype = placementState.getSoundType(world, pos, player);
            world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            if (player == null || !player.isCreative()) {
                backpack.shrink(1);
            }

            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    private static void stopBackpackSounds(ItemStack backpack, World world, BlockPos pos) {
        BackpackWrapperLookup.get(backpack).ifPresent(wrapper -> wrapper.getContentsUuid().ifPresent(uuid ->
                ServerBackpackSoundHandler.stopPlayingDisc((ServerWorld) world, Vec3d.atCenterOf(pos), uuid))
        );
    }

    private ItemStack getBackpackCopy(@Nullable PlayerEntity player, ItemStack backpack) {
        if (player == null || !player.isCreative()) {
            return backpack.copy();
        }
        return BackpackWrapperLookup.get(backpack)
                .map(IBackpackWrapper::cloneBackpack).orElse(new ItemStack(ModItems.BACKPACK));
    }

    protected boolean canPlace(BlockItemUseContext context, BlockState state) {
        PlayerEntity playerentity = context.getPlayer();
        ISelectionContext iselectioncontext = playerentity == null ? ISelectionContext.empty() : ISelectionContext.of(playerentity);
        return (state.canSurvive(context.getWorld(), context.getPos())) && context.getWorld().isUnobstructed(state, context.getPos(), iselectioncontext);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote && player instanceof ServerPlayerEntity) {
            String handlerName = hand == Hand.MAIN_HAND ? PlayerInventoryProvider.MAIN_INVENTORY : PlayerInventoryProvider.OFFHAND_INVENTORY;
            int slot = hand == Hand.MAIN_HAND ? player.inventory.currentItem : 0;
            BackpackContext.Item context = new BackpackContext.Item(handlerName, slot);
            PacketHandler.openContainer((ServerPlayerEntity) player, new SimpleNamedContainerProvider((w, p, pl) -> new BackpackContainer(w, pl, context), stack.getDisplayName()), context);
        }
        return ActionResult.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote || !(entityIn instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) entityIn;
        BackpackWrapperLookup.get(stack).ifPresent(
                wrapper -> wrapper.getUpgradeHandler().getWrappersThatImplement(ITickableUpgrade.class)
                        .forEach(upgrade -> upgrade.tick(player, player.world, player.getPosition()))
        );
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public int getNumberOfSlots() {
        return numberOfSlots.getAsInt();
    }

    public int getNumberOfUpgradeSlots() {
        return numberOfUpgradeSlots.getAsInt();
    }

    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.CHEST;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return stack.getItem() == ModItems.GOLD_BACKPACK;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
        if (!(player.openContainer instanceof BackpackContainer)) {
            return true;
        }
        BackpackContainer backpackContainer = (BackpackContainer) player.openContainer;
        return backpackContainer.getVisibleStorageItem().map(visibleStorageItem -> visibleStorageItem != item).orElse(true);
    }
}
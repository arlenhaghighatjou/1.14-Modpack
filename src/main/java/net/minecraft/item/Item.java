package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.Tag;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;

public class Item implements IItemProvider {
    public static final Map<Block, Item> BLOCK_TO_ITEM = Maps.newHashMap();
    private static final IItemPropertyGetter DAMAGED_GETTER = (p_210306_0_, p_210306_1_, p_210306_2_) -> {
        return p_210306_0_.isDamaged() ? 1.0F : 0.0F;
    };
    private static final IItemPropertyGetter DAMAGE_GETTER = (p_210307_0_, p_210307_1_, p_210307_2_) -> {
        return MathHelper.clamp((float) p_210307_0_.getDamage() / (float) p_210307_0_.getMaxDamage(), 0.0F, 1.0F);
    };
    private static final IItemPropertyGetter LEFTHANDED_GETTER = (p_210305_0_, p_210305_1_, p_210305_2_) -> {
        return p_210305_2_ != null && p_210305_2_.getPrimaryHand() != HandSide.RIGHT ? 1.0F : 0.0F;
    };
    private static final IItemPropertyGetter COOLDOWN_GETTER = (p_210308_0_, p_210308_1_, p_210308_2_) -> {
        return p_210308_2_ instanceof PlayerEntity ? ((PlayerEntity) p_210308_2_).getCooldownTracker().getCooldown(p_210308_0_.getItem(), 0.0F) : 0.0F;
    };
    private static final IItemPropertyGetter MODELDATA_GETTER = (p_219969_0_, p_219969_1_, p_219969_2_) -> {
        return p_219969_0_.hasTag() ? (float) p_219969_0_.getTag().getInt("CustomModelData") : 0.0F;
    };
    protected static final EaglercraftUUID ATTACK_DAMAGE_MODIFIER = EaglercraftUUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final EaglercraftUUID ATTACK_SPEED_MODIFIER = EaglercraftUUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    protected static final Random random = new Random();
    private final Map<ResourceLocation, IItemPropertyGetter> properties = Maps.newHashMap();
    protected final ItemGroup group;
    private final Rarity rarity;
    private final int maxStackSize;
    private final int maxDamage;
    private final Item containerItem;

    private String translationKey;

    private final Food food;

    public static int getIdFromItem(Item itemIn) {
        return itemIn == null ? 0 : Registry.ITEM.getId(itemIn);
    }

    public static Item getItemById(int id) {
        return Registry.ITEM.getByValue(id);
    }

    @Deprecated
    public static Item getItemFromBlock(Block blockIn) {
        return BLOCK_TO_ITEM.getOrDefault(blockIn, Items.AIR);
    }

    public Item(Item.Properties properties) {
        this.addPropertyOverride(new ResourceLocation("lefthanded"), LEFTHANDED_GETTER);
        this.addPropertyOverride(new ResourceLocation("cooldown"), COOLDOWN_GETTER);
        this.addPropertyOverride(new ResourceLocation("custom_model_data"), MODELDATA_GETTER);
        this.group = properties.group;
        this.rarity = properties.rarity;
        this.containerItem = properties.containerItem;
        this.maxDamage = properties.maxDamage;
        this.maxStackSize = properties.maxStackSize;
        this.food = properties.food;
        if (this.maxDamage > 0) {
            this.addPropertyOverride(new ResourceLocation("damaged"), DAMAGED_GETTER);
            this.addPropertyOverride(new ResourceLocation("damage"), DAMAGE_GETTER);
        }

    }

    public void func_219972_a(World worldIn, LivingEntity livingEntityIn, ItemStack stack, int p_219972_4_) {
    }

    @OnlyIn(Dist.CLIENT)
    public IItemPropertyGetter getPropertyGetter(ResourceLocation key) {
        return this.properties.get(key);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasCustomProperties() {
        return !this.properties.isEmpty();
    }

    public boolean updateItemStackNBT(CompoundNBT nbt) {
        return false;
    }

    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        return true;
    }

    public Item asItem() {
        return this;
    }

    public final void addPropertyOverride(ResourceLocation key, IItemPropertyGetter getter) {
        this.properties.put(key, getter);
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        return ActionResultType.PASS;
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 1.0F;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (this.isFood()) {
            ItemStack itemstack = playerIn.getHeldItem(handIn);
            if (playerIn.canEat(this.getFood().canEatWhenFull())) {
                playerIn.setActiveHand(handIn);
                return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
            } else {
                return new ActionResult<>(ActionResultType.FAIL, itemstack);
            }
        } else {
            return new ActionResult<>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
        }
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        return this.isFood() ? entityLiving.onFoodEaten(worldIn, stack) : stack;
    }

    public final int getMaxStackSize() {
        return this.maxStackSize;
    }

    public final int getMaxDamage() {
        return this.maxDamage;
    }

    public boolean isDamageable() {
        return this.maxDamage > 0;
    }

    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return false;
    }

    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        return false;
    }

    public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }

    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public ITextComponent getName() {
        return new TranslationTextComponent(this.getTranslationKey());
    }

    public String toString() {
        return Registry.ITEM.getKey(this).getPath();
    }

    protected String getDefaultTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.makeTranslationKey("item", Registry.ITEM.getKey(this));
        }

        return this.translationKey;
    }

    public String getTranslationKey() {
        return this.getDefaultTranslationKey();
    }

    public String getTranslationKey(ItemStack stack) {
        return this.getTranslationKey();
    }

    public boolean shouldSyncTag() {
        return true;
    }

    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        return ActionResultType.PASS;
    }

    public boolean showDurabilityBar(ItemStack stack) {
        return stack.isDamaged();
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        return (double) stack.getDamage() / (double) stack.getMaxDamage();
    }

    public final Item getContainerItem() {
        return this.containerItem;
    }

    public boolean hasContainerItem() {
        return this.containerItem != null;
    }

    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    }

    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
    }

    public boolean isComplex() {
        return false;
    }

    public UseAction getUseAction(ItemStack stack) {
        return stack.getItem().isFood() ? UseAction.EAT : UseAction.NONE;
    }

    public int getUseDuration(ItemStack stack) {
        if (stack.getItem().isFood()) {
            return this.getFood().isFastEating() ? 16 : 32;
        } else {
            return 0;
        }
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    }

    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent(this.getTranslationKey(stack));
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return stack.isEnchanted();
    }

    public Rarity getRarity(ItemStack stack) {
        if (!stack.isEnchanted()) {
            return this.rarity;
        } else {
            switch (this.rarity) {
                case COMMON:
                case UNCOMMON:
                    return Rarity.RARE;
                case RARE:
                    return Rarity.EPIC;
                case EPIC:
                default:
                    return this.rarity;
            }
        }
    }

    public boolean isEnchantable(ItemStack stack) {
        return this.getMaxStackSize() == 1 && this.isDamageable();
    }

    protected static RayTraceResult rayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        Vec3d vec3d = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * ((float) Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 5.0D;
        Vec3d vec3d1 = vec3d.add((double) f6 * 5.0D, (double) f5 * 5.0D, (double) f7 * 5.0D);
        return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }

    public int getItemEnchantability() {
        return 0;
    }

    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this));
        }

    }

    protected boolean isInGroup(ItemGroup group) {
        ItemGroup itemgroup = this.getGroup();
        return itemgroup != null && (group == ItemGroup.SEARCH || group == itemgroup);
    }

    public final ItemGroup getGroup() {
        return this.group;
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return HashMultimap.create();
    }

    public boolean isCrossbow(ItemStack stack) {
        return stack.getItem() == Items.CROSSBOW;
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getDefaultInstance() {
        return new ItemStack(this);
    }

    public boolean isIn(Tag<Item> tagIn) {
        return tagIn.contains(this);
    }

    public boolean isFood() {
        return this.food != null;
    }

    public Food getFood() {
        return this.food;
    }

    public static class Properties {
        private int maxStackSize = 64;
        private int maxDamage;
        private Item containerItem;
        private ItemGroup group;
        private Rarity rarity = Rarity.COMMON;
        private Food food;

        public Item.Properties food(Food foodIn) {
            this.food = foodIn;
            return this;
        }

        public Item.Properties maxStackSize(int maxStackSizeIn) {
            if (this.maxDamage > 0) {
                throw new RuntimeException("Unable to have damage AND stack.");
            } else {
                this.maxStackSize = maxStackSizeIn;
                return this;
            }
        }

        public Item.Properties defaultMaxDamage(int maxDamageIn) {
            return this.maxDamage == 0 ? this.maxDamage(maxDamageIn) : this;
        }

        public Item.Properties maxDamage(int maxDamageIn) {
            this.maxDamage = maxDamageIn;
            this.maxStackSize = 1;
            return this;
        }

        public Item.Properties containerItem(Item containerItemIn) {
            this.containerItem = containerItemIn;
            return this;
        }

        public Item.Properties group(ItemGroup groupIn) {
            this.group = groupIn;
            return this;
        }

        public Item.Properties rarity(Rarity rarityIn) {
            this.rarity = rarityIn;
            return this;
        }
    }

	public boolean shouldUseOnTouchEagler(ItemStack stack) {
		return stack.getItem().isFood() || stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof ToolItem || stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem;
	}
}

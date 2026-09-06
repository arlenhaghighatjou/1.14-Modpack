package net.minecraft.server.management;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerInteractionManager {
    private static final Logger field_225418_c = LogManager.getLogger();
    public ServerWorld world;
    public ServerPlayerEntity player;
    private GameType gameType = GameType.NOT_SET;
    private boolean isDestroyingBlock;
    private int initialDamage;
    private BlockPos destroyPos = BlockPos.ZERO;
    private int ticks;
    private boolean receivedFinishDiggingPacket;
    private BlockPos delayedDestroyPos = BlockPos.ZERO;
    private int initialBlockDamage;
    private int durabilityRemainingOnBlock = -1;

    public PlayerInteractionManager(ServerWorld p_i50702_1_) {
        this.world = p_i50702_1_;
    }

    public void setGameType(GameType type) {
        this.gameType = type;
        type.configurePlayerCapabilities(this.player.abilities);
        this.player.sendPlayerAbilities();
        this.player.server.getPlayerList().sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_GAME_MODE, this.player));
        this.world.updateAllPlayersSleepingFlag();
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public boolean survivalOrAdventure() {
        return this.gameType.isSurvivalOrAdventure();
    }

    public boolean isCreative() {
        return this.gameType.isCreative();
    }

    public void initializeGameType(GameType type) {
        if (this.gameType == GameType.NOT_SET) {
            this.gameType = type;
        }

        this.setGameType(this.gameType);
    }

    public void tick() {
        ++this.ticks;
        if (this.receivedFinishDiggingPacket) {
            BlockState blockstate = this.world.getBlockState(this.delayedDestroyPos);
            if (blockstate.isAir()) {
                this.receivedFinishDiggingPacket = false;
            } else {
                float f = this.func_225417_a(blockstate, this.delayedDestroyPos);
                if (f >= 1.0F) {
                    this.receivedFinishDiggingPacket = false;
                    this.tryHarvestBlock(this.delayedDestroyPos);
                }
            }
        } else if (this.isDestroyingBlock) {
            BlockState blockstate1 = this.world.getBlockState(this.destroyPos);
            if (blockstate1.isAir()) {
                this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
                this.durabilityRemainingOnBlock = -1;
                this.isDestroyingBlock = false;
            } else {
                this.func_225417_a(blockstate1, this.destroyPos);
            }
        }

    }

    private float func_225417_a(BlockState p_225417_1_, BlockPos p_225417_2_) {
        int i = this.ticks - this.initialBlockDamage;
        float f = p_225417_1_.getPlayerRelativeBlockHardness(this.player, this.player.world, p_225417_2_) * (float) (i + 1);
        int j = (int) (f * 10.0F);
        if (j != this.durabilityRemainingOnBlock) {
            this.world.sendBlockBreakProgress(this.player.getEntityId(), p_225417_2_, j);
            this.durabilityRemainingOnBlock = j;
        }

        return f;
    }

    public void func_225416_a(BlockPos p_225416_1_, CPlayerDiggingPacket.Action p_225416_2_, Direction p_225416_3_, int p_225416_4_) {
        double d0 = this.player.posX - ((double) p_225416_1_.getX() + 0.5D);
        double d1 = this.player.posY - ((double) p_225416_1_.getY() + 0.5D) + 1.5D;
        double d2 = this.player.posZ - ((double) p_225416_1_.getZ() + 0.5D);
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
        if (d3 > 36.0D) {
            this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, false));
        } else if (p_225416_1_.getY() >= p_225416_4_) {
            this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, false));
        } else {
            if (p_225416_2_ == CPlayerDiggingPacket.Action.START_DESTROY_BLOCK) {
                if (!this.world.isBlockModifiable(this.player, p_225416_1_)) {
                    this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, false));
                    return;
                }

                if (this.isCreative()) {
                    if (!this.world.extinguishFire((PlayerEntity) null, p_225416_1_, p_225416_3_)) {
                        this.func_225415_a(p_225416_1_, p_225416_2_);
                    } else {
                        this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, true));
                    }

                    return;
                }

                if (this.player.func_223729_a(this.world, p_225416_1_, this.gameType)) {
                    this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, false));
                    return;
                }

                this.world.extinguishFire((PlayerEntity) null, p_225416_1_, p_225416_3_);
                this.initialDamage = this.ticks;
                float f = 1.0F;
                BlockState blockstate = this.world.getBlockState(p_225416_1_);
                if (!blockstate.isAir()) {
                    blockstate.onBlockClicked(this.world, p_225416_1_, this.player);
                    modpack.ModHooks.onBlockClick(this.player, p_225416_1_);
                    f = blockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, p_225416_1_);
                }

                if (!blockstate.isAir() && f >= 1.0F) {
                    this.func_225415_a(p_225416_1_, p_225416_2_);
                } else {
                    this.isDestroyingBlock = true;
                    this.destroyPos = p_225416_1_;
                    int i = (int) (f * 10.0F);
                    this.world.sendBlockBreakProgress(this.player.getEntityId(), p_225416_1_, i);
                    this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, true));
                    this.durabilityRemainingOnBlock = i;
                }
            } else if (p_225416_2_ == CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK) {
                if (p_225416_1_.equals(this.destroyPos)) {
                    int j = this.ticks - this.initialDamage;
                    BlockState blockstate1 = this.world.getBlockState(p_225416_1_);
                    if (!blockstate1.isAir()) {
                        float f1 = blockstate1.getPlayerRelativeBlockHardness(this.player, this.player.world, p_225416_1_) * (float) (j + 1);
                        if (f1 >= 0.7F) {
                            this.isDestroyingBlock = false;
                            this.world.sendBlockBreakProgress(this.player.getEntityId(), p_225416_1_, -1);
                            this.func_225415_a(p_225416_1_, p_225416_2_);
                            return;
                        }

                        if (!this.receivedFinishDiggingPacket) {
                            this.isDestroyingBlock = false;
                            this.receivedFinishDiggingPacket = true;
                            this.delayedDestroyPos = p_225416_1_;
                            this.initialBlockDamage = this.initialDamage;
                        }
                    }
                }

                this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, true));
            } else if (p_225416_2_ == CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK) {
                this.isDestroyingBlock = false;
                this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
                this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225416_1_, this.world.getBlockState(p_225416_1_), p_225416_2_, true));
            }

        }
    }

    public void func_225415_a(BlockPos p_225415_1_, CPlayerDiggingPacket.Action p_225415_2_) {
        if (this.tryHarvestBlock(p_225415_1_)) {
            this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225415_1_, this.world.getBlockState(p_225415_1_), p_225415_2_, true));
        } else {
            this.player.connection.sendPacket(new SPlayerDiggingPacket(p_225415_1_, this.world.getBlockState(p_225415_1_), p_225415_2_, false));
        }

    }

    public boolean tryHarvestBlock(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        if (!this.player.getHeldItemMainhand().getItem().canPlayerBreakBlockWhileHolding(blockstate, this.world, pos, this.player)) {
            return false;
        } else {
            TileEntity tileentity = this.world.getTileEntity(pos);
            Block block = blockstate.getBlock();
            if ((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.player.canUseCommandBlock()) {
                this.world.notifyBlockUpdate(pos, blockstate, blockstate, 3);
                return false;
            } else if (this.player.func_223729_a(this.world, pos, this.gameType)) {
                return false;
            } else {
                block.onBlockHarvested(this.world, pos, blockstate, this.player);
                boolean flag = this.world.removeBlock(pos, false);
                if (flag) {
                    block.onPlayerDestroy(this.world, pos, blockstate);
                }

                if (this.isCreative()) {
                    return true;
                } else {
                    ItemStack itemstack = this.player.getHeldItemMainhand();
                    boolean flag1 = this.player.canHarvestBlock(blockstate);
                    itemstack.onBlockDestroyed(this.world, blockstate, pos, this.player);
                    if (flag && flag1) {
                        ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
                        block.harvestBlock(this.world, this.player, pos, blockstate, tileentity, itemstack1);
                    }

                    return true;
                }
            }
        }
    }

    public ActionResultType processRightClick(PlayerEntity player, World worldIn, ItemStack stack, Hand hand) {
        if (this.gameType == GameType.SPECTATOR) {
            return ActionResultType.PASS;
        } else if (player.getCooldownTracker().hasCooldown(stack.getItem())) {
            return ActionResultType.PASS;
        } else {
            int i = stack.getCount();
            int j = stack.getDamage();
            ActionResult<ItemStack> actionresult = stack.useItemRightClick(worldIn, player, hand);
            ItemStack itemstack = actionresult.getResult();
            if (itemstack == stack && itemstack.getCount() == i && itemstack.getUseDuration() <= 0 && itemstack.getDamage() == j) {
                return actionresult.getType();
            } else if (actionresult.getType() == ActionResultType.FAIL && itemstack.getUseDuration() > 0 && !player.isHandActive()) {
                return actionresult.getType();
            } else {
                player.setHeldItem(hand, itemstack);
                if (this.isCreative()) {
                    itemstack.setCount(i);
                    if (itemstack.isDamageable()) {
                        itemstack.setDamage(j);
                    }
                }

                if (itemstack.isEmpty()) {
                    player.setHeldItem(hand, ItemStack.EMPTY);
                }

                if (!player.isHandActive()) {
                    ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
                }

                return actionresult.getType();
            }
        }
    }

    public ActionResultType func_219441_a(PlayerEntity p_219441_1_, World p_219441_2_, ItemStack p_219441_3_, Hand p_219441_4_, BlockRayTraceResult p_219441_5_) {
        BlockPos blockpos = p_219441_5_.getPos();
        if (modpack.ModHooks.onRightClickBlock(p_219441_1_, p_219441_4_, p_219441_2_, blockpos)) {
            return ActionResultType.SUCCESS;
        }
        BlockState blockstate = p_219441_2_.getBlockState(blockpos);
        if (this.gameType == GameType.SPECTATOR) {
            INamedContainerProvider inamedcontainerprovider = blockstate.getContainer(p_219441_2_, blockpos);
            if (inamedcontainerprovider != null) {
                p_219441_1_.openContainer(inamedcontainerprovider);
                return ActionResultType.SUCCESS;
            } else {
                return ActionResultType.PASS;
            }
        } else {
            if (!p_219441_3_.isEmpty()) {
                ActionResultType actionresulttype1 = p_219441_3_.getItem().onItemUseFirst(p_219441_3_, new ItemUseContext(p_219441_1_, p_219441_4_, p_219441_5_));
                if (actionresulttype1 != ActionResultType.PASS) {
                    return actionresulttype1;
                }
            }

            boolean flag = !p_219441_1_.getHeldItemMainhand().isEmpty() || !p_219441_1_.getHeldItemOffhand().isEmpty();
            boolean flag1 = p_219441_1_.isSneaking() && flag;
            if (flag1 && blockstate.onSneakBlockActivated(p_219441_2_, p_219441_1_, p_219441_4_, p_219441_5_)) {
                return ActionResultType.SUCCESS;
            } else if (!flag1 && blockstate.onBlockActivated(p_219441_2_, p_219441_1_, p_219441_4_, p_219441_5_)) {
                return ActionResultType.SUCCESS;
            } else if (!p_219441_3_.isEmpty() && !p_219441_1_.getCooldownTracker().hasCooldown(p_219441_3_.getItem())) {
                ItemUseContext itemusecontext = new ItemUseContext(p_219441_1_, p_219441_4_, p_219441_5_);
                if (this.isCreative()) {
                    int i = p_219441_3_.getCount();
                    ActionResultType actionresulttype = p_219441_3_.onItemUse(itemusecontext);
                    p_219441_3_.setCount(i);
                    return actionresulttype;
                } else {
                    return p_219441_3_.onItemUse(itemusecontext);
                }
            } else {
                return ActionResultType.PASS;
            }
        }
    }

    public void setWorld(ServerWorld serverWorld) {
        this.world = serverWorld;
    }
}

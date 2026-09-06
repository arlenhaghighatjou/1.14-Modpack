package com.polyvalord.extcaves.blocks.basic;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockTallGroundFacingWL extends BlockGroundFacingWL 
{
	
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	public BlockTallGroundFacingWL(Properties properties) 
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(HALF, DoubleBlockHalf.LOWER).with(HORIZONTAL_FACING, Direction.NORTH).with(WATERLOGGED, false));
	}
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) 
	{
		DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
		if (facing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP)
				|| facingState.isIn(this) && facingState.get(HALF) != doubleblockhalf) 
		{
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN
					&& !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState()
							: super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		} 
		else 
		{
			return Blocks.AIR.getDefaultState();
		}
	}

	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) 
	{
		BlockPos blockpos = context.getPos();
		return blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context)
				? super.getStateForPlacement(context)
				: null;
	}

	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) 
	{
		worldIn.setBlockState(pos.up(), this.getDefaultState()
				.with(HALF, DoubleBlockHalf.UPPER)
				.with(HORIZONTAL_FACING, state.get(HORIZONTAL_FACING)), 3);
	}

	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) 
	{
		if (state.get(HALF) != DoubleBlockHalf.UPPER) 
		{
			return super.isValidPosition(state, worldIn, pos);
		} 
		else
		{
			BlockState blockstate = worldIn.getBlockState(pos.down());
			if (state.getBlock() != this)
				return super.isValidPosition(state, worldIn, pos); // Forge: This function is called during world gen
																	// and placement, before this block is set, so if we
																	// are not 'here' then assume it's the pre-check.
			return blockstate.isIn(this) && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
		}
	}

	public void placeAt(IWorld worldIn, BlockPos pos, int flags) 
	{
		worldIn.setBlockState(pos, this.getDefaultState().with(HALF, DoubleBlockHalf.LOWER), flags);
		worldIn.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), flags);
	}

	/**
	 * Called before the Block is set to air in the world. Called regardless of if
	 * the player's tool can actually collect this block
	 */
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) 
	{
		if (!worldIn.isRemote) 
		{
			if (player.isCreative()) 
			{
				func_241471_b_(worldIn, pos, state, player);
			} 
			else 
			{
				spawnDrops(state, worldIn, pos, (TileEntity) null, player, player.getHeldItemMainhand());
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	/**
	 * Spawns the block's drops in the world. By the time this is called the Block
	 * has possibly been set to air via Block.removedByPlayer
	 */
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state,
			@Nullable TileEntity te, ItemStack stack) 
	{
		super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
	}

	protected static void func_241471_b_(World world_, BlockPos pos_, BlockState state_,
			PlayerEntity player_) 
	{
		DoubleBlockHalf doubleblockhalf = state_.get(HALF);
		if (doubleblockhalf == DoubleBlockHalf.UPPER) 
		{
			BlockPos blockpos = pos_.down();
			BlockState blockstate = world_.getBlockState(blockpos);
			if (blockstate.getBlock() == state_.getBlock() && blockstate.get(HALF) == DoubleBlockHalf.LOWER) 
			{
				world_.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
				world_.playEvent(player_, 2001, blockpos, Block.getStateId(blockstate));
			}
		}

	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) 
	{
		builder.add(HALF, HORIZONTAL_FACING, WATERLOGGED);
	}

}

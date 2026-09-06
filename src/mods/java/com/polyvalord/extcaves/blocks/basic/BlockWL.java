package com.polyvalord.extcaves.blocks.basic;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;

public class BlockWL extends Block implements IWaterLoggable
{
	
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public BlockWL(Properties properties) 
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, Boolean.valueOf(false)));
	}
	
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
	
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) 
	{
		FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
		return this.getDefaultState().with(WATERLOGGED,
				Boolean.valueOf(fluidstate.isTagged(FluidTags.WATER) && fluidstate.getLevel() == 8));
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) 
	{
		builder.add(WATERLOGGED);
	}

}

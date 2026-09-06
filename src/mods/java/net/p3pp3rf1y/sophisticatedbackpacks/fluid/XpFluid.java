package net.p3pp3rf1y.sophisticatedbackpacks.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class XpFluid extends Fluid {
	private final boolean source;

	public XpFluid(boolean source) {
		this.source = source;
	}

	@Override
	protected BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public Item getFilledBucket() {
		return Items.AIR;
	}

	@Override
	protected boolean func_215665_a(IFluidState state, IBlockReader reader, BlockPos pos, Fluid fluid, Direction direction) {
		return false;
	}

	@Override
	protected Vec3d func_215663_a(IBlockReader reader, BlockPos pos, IFluidState state) {
		return Vec3d.ZERO;
	}

	@Override
	public int getTickRate(IWorldReader reader) {
		return 5;
	}

	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}

	@Override
	public float func_215662_a(IFluidState state, IBlockReader reader, BlockPos pos) {
		return 0.0F;
	}

	@Override
	public float func_223407_a(IFluidState state) {
		return 0.0F;
	}

	@Override
	protected BlockState getBlockState(IFluidState state) {
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public boolean isSource(IFluidState state) {
		return source;
	}

	@Override
	public int getLevel(IFluidState state) {
		return source ? 8 : 7;
	}

	@Override
	public VoxelShape func_215664_b(IFluidState state, IBlockReader reader, BlockPos pos) {
		return VoxelShapes.empty();
	}
}

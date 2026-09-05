/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package biomesoplenty.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import biomesoplenty.api.block.BOPBlocks;

import java.util.Iterator;

public class WatersidePlantBlock extends PlantBlockBOP
{
    public WatersidePlantBlock(Block.Properties properties)
    {
        super(properties);
    }
    
    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        Block block = state.getBlock();

        return super.isValidGround(state, worldIn, pos) || block == Blocks.SAND || block == Blocks.RED_SAND || block == BOPBlocks.white_sand || block == BOPBlocks.mud;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldReader, BlockPos pos)
    {
        BlockState soil = worldReader.getBlockState(pos.down());
        if (this.isValidGround(soil, worldReader, pos.down()))
        {
            BlockPos blockpos = pos.down();
            Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

            BlockState BlockState;
            IFluidState ifluidstate;
            do {
                if (!var7.hasNext()) {
                    return false;
                }

                Direction Direction = (Direction)var7.next();
                BlockState = worldReader.getBlockState(blockpos.offset(Direction));
                ifluidstate = worldReader.getFluidState(blockpos.offset(Direction));
            } while(!ifluidstate.isTagged(FluidTags.WATER) && BlockState.getBlock() != Blocks.FROSTED_ICE);

            return true;
        }
        else
            return false;
    }
}

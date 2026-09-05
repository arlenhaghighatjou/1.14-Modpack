package net.minecraft.client.renderer.chunk;

import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class VisGraph {
    private static final int DX = (int) Math.pow(16.0D, 0.0D);
    private static final int DZ = (int) Math.pow(16.0D, 1.0D);
    private static final int DY = (int) Math.pow(16.0D, 2.0D);
    private static final Direction[] DIRECTIONS = Direction.values();
    private final long[] bitSet = new long[64];
    private final int[] floodFillQueue = new int[4096];
    private static final int[] INDEX_OF_EDGES = Util.make(new int[1352], (p_209264_0_) -> {
        int i = 0;
        int j = 15;
        int k = 0;

        for (int l = 0; l < 16; ++l) {
            for (int i1 = 0; i1 < 16; ++i1) {
                for (int j1 = 0; j1 < 16; ++j1) {
                    if (l == 0 || l == 15 || i1 == 0 || i1 == 15 || j1 == 0 || j1 == 15) {
                        p_209264_0_[k++] = getIndex(l, i1, j1);
                    }
                }
            }
        }

    });
    private int empty = 4096;
    private SetVisibility cachedVisibility = null;
    private boolean dirty = false;

    public void reset() {
        Arrays.fill(this.bitSet, 0L);
        this.empty = 4096;
        this.cachedVisibility = null;
        this.dirty = false;
    }

    public void setOpaqueCube(BlockPos pos) {
        int index = getIndex(pos);
        this.bitSet[index >> 6] |= (1L << index);
        --this.empty;
        this.dirty = true;
    }

    private static int getIndex(BlockPos pos) {
        return getIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    private static int getIndex(int x, int y, int z) {
        return x << 0 | y << 8 | z << 4;
    }

    public SetVisibility computeVisibility() {
        return this.computeVisibility(new SetVisibility());
    }

    public SetVisibility computeVisibility(SetVisibility setvisibility) {
        if (!this.dirty && this.cachedVisibility != null) {
            return this.cachedVisibility;
        }
        setvisibility.setAllVisible(false);
        if (4096 - this.empty < 256) {
            setvisibility.setAllVisible(true);
        } else if (this.empty == 0) {
            setvisibility.setAllVisible(false);
        } else {
            for (int i : INDEX_OF_EDGES) {
                if ((this.bitSet[i >> 6] & (1L << i)) == 0) {
                    setvisibility.setManyVisible(this.floodFillMask(i));
                }
            }
        }

        this.cachedVisibility = setvisibility;
        this.dirty = false;
        return setvisibility;
    }

    public Set<Direction> getVisibleFacings(BlockPos pos) {
        return this.floodFill(getIndex(pos));
    }

    private Set<Direction> floodFill(int pos) {
        int facingMask = this.floodFillMask(pos);
        Set<Direction> set = EnumSet.noneOf(Direction.class);
        for (int i = 0; i < DIRECTIONS.length; ++i) {
            if ((facingMask & 1 << i) != 0) {
                set.add(DIRECTIONS[i]);
            }
        }
        return set;
    }

    private int floodFillMask(int pos) {
        int facingMask = 0;
        int[] queue = this.floodFillQueue;
        int head = 0;
        int tail = 0;
        queue[tail++] = pos;
        this.bitSet[pos >> 6] |= (1L << pos);

        while (head < tail) {
            int i = queue[head++];
            facingMask |= this.getEdgeMask(i);

            for (Direction direction : DIRECTIONS) {
                int j = this.getNeighborIndexAtFace(i, direction);
                if (j >= 0 && (this.bitSet[j >> 6] & (1L << j)) == 0) {
                    this.bitSet[j >> 6] |= (1L << j);
                    queue[tail++] = j;
                }
            }
        }

        return facingMask;
    }

    private int getEdgeMask(int pos) {
        int facingMask = 0;
        int i = pos >> 0 & 15;
        if (i == 0) {
            facingMask |= 1 << Direction.WEST.ordinal();
        } else if (i == 15) {
            facingMask |= 1 << Direction.EAST.ordinal();
        }

        int j = pos >> 8 & 15;
        if (j == 0) {
            facingMask |= 1 << Direction.DOWN.ordinal();
        } else if (j == 15) {
            facingMask |= 1 << Direction.UP.ordinal();
        }

        int k = pos >> 4 & 15;
        if (k == 0) {
            facingMask |= 1 << Direction.NORTH.ordinal();
        } else if (k == 15) {
            facingMask |= 1 << Direction.SOUTH.ordinal();
        }
        return facingMask;
    }

    private int getNeighborIndexAtFace(int pos, Direction facing) {
        switch (facing) {
            case DOWN:
                if ((pos >> 8 & 15) == 0) {
                    return -1;
                }

                return pos - DY;
            case UP:
                if ((pos >> 8 & 15) == 15) {
                    return -1;
                }

                return pos + DY;
            case NORTH:
                if ((pos >> 4 & 15) == 0) {
                    return -1;
                }

                return pos - DZ;
            case SOUTH:
                if ((pos >> 4 & 15) == 15) {
                    return -1;
                }

                return pos + DZ;
            case WEST:
                if ((pos >> 0 & 15) == 0) {
                    return -1;
                }

                return pos - DX;
            case EAST:
                if ((pos >> 0 & 15) == 15) {
                    return -1;
                }

                return pos + DX;
            default:
                return -1;
        }
    }
}

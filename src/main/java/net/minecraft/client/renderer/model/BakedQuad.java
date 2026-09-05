package net.minecraft.client.renderer.model;

import java.util.Arrays;
import me.jellysquid.mods.sodium.client.model.quad.properties.ModelQuadFlags;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BakedQuad {
    protected int[] vertexData;
    protected final int tintIndex;
    protected final Direction face;
    protected final TextureAtlasSprite sprite;
    private final int flags;

    public BakedQuad(int[] vertexDataIn, int tintIndexIn, Direction faceIn, TextureAtlasSprite spriteIn) {
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
        this.sprite = spriteIn;
        this.flags = ModelQuadFlags.getQuadFlags(this);
    }

    public TextureAtlasSprite getSprite() {
        return this.sprite;
    }

    public int[] getVertexData() {
        return this.vertexData;
    }

    public boolean hasTintIndex() {
        return this.tintIndex != -1;
    }

    public int getTintIndex() {
        return this.tintIndex;
    }

    public Direction getFace() {
        return this.face;
    }

    public int getFlags() {
        return this.flags;
    }

    void compactVertexData() {
        this.vertexData = VertexDataPool.intern(this.vertexData);
    }

    static void beginVertexDataCompaction() {
        VertexDataPool.begin();
    }

    static void endVertexDataCompaction() {
        VertexDataPool.end();
    }

    private static final class VertexDataPool {
        private static int[][] table;
        private static int mask;
        private static int size;
        private static int resizeAt;

        static void begin() {
            table = new int[1024][];
            mask = table.length - 1;
            size = 0;
            resizeAt = table.length * 3 / 5;
        }

        static void end() {
            table = null;
            mask = 0;
            size = 0;
            resizeAt = 0;
        }

        static int[] intern(int[] data) {
            if (table == null) {
                return data;
            }
            if (size >= resizeAt) {
                rehash();
            }
            int slot = hash(data) & mask;
            int[] current;
            while ((current = table[slot]) != null) {
                if (current == data || Arrays.equals(current, data)) {
                    return current;
                }
                slot = slot + 1 & mask;
            }
            table[slot] = data;
            ++size;
            return data;
        }

        private static void rehash() {
            int[][] oldTable = table;
            table = new int[oldTable.length << 1][];
            mask = table.length - 1;
            resizeAt = table.length * 3 / 5;
            for (int i = 0; i < oldTable.length; ++i) {
                int[] data = oldTable[i];
                if (data != null) {
                    int slot = hash(data) & mask;
                    while (table[slot] != null) {
                        slot = slot + 1 & mask;
                    }
                    table[slot] = data;
                }
            }
        }

        private static int hash(int[] data) {
            int hash = Arrays.hashCode(data);
            hash ^= hash >>> 16;
            hash *= 0x7feb352d;
            return hash ^ hash >>> 15;
        }
    }
}

package net.lax1dude.eaglercraft.sp.server;

import com.mojang.datafixers.DataFixer;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.ChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copyright (c) 2023-2024 lax1dude. All Rights Reserved.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
public class EaglerChunkLoader extends ChunkLoader {

    private static final String hex = "0123456789ABCDEF";
    private static final Logger logger = LogManager.getLogger("EaglerChunkLoader");
    public final VFile2 chunkDirectory;

    public EaglerChunkLoader(VFile2 chunkDirectory, DataFixer dataFixerIn) {
        super(new java.io.File(chunkDirectory.getPath()), dataFixerIn);
        this.chunkDirectory = chunkDirectory;
    }

    public static String getChunkPath(int x, int z) {
        int unsignedX = x + 1900000;
        int unsignedZ = z + 1900000;

        char[] path = new char[12];
        for (int i = 5; i >= 0; --i) {
            path[i] = hex.charAt((unsignedX >> (i * 4)) & 0xF);
            path[i + 6] = hex.charAt((unsignedZ >> (i * 4)) & 0xF);
        }

        return new String(path);
    }

    public static ChunkPos getChunkCoords(String filename) {
        String strX = filename.substring(0, 6);
        String strZ = filename.substring(6);

        int retX = 0;
        int retZ = 0;

        for (int i = 0; i < 6; ++i) {
            retX |= hex.indexOf(strX.charAt(i)) << (i << 2);
            retZ |= hex.indexOf(strZ.charAt(i)) << (i << 2);
        }

        return new ChunkPos(retX - 1900000, retZ - 1900000);
    }

    @Override
    public CompoundNBT readChunk(ChunkPos pos) throws IOException {
        VFile2 file = new VFile2(chunkDirectory, getChunkPath(pos.x, pos.z) + ".dat");
        if (!file.exists()) {
            return null;
        }
        try {
            CompoundNBT nbt;
            try (InputStream is = file.getInputStream()) {
                nbt = CompressedStreamTools.readCompressed(is);
            }
            return nbt;
        } catch (Throwable t) {
        }
        return null;
    }

    @Override
    public void writeChunk(ChunkPos pos, CompoundNBT compound) throws IOException {
        VFile2 file = new VFile2(chunkDirectory, getChunkPath(pos.x, pos.z) + ".dat");
        try (OutputStream os = file.getOutputStream()) {
            CompressedStreamTools.writeCompressed(compound, os);
        }
    }

}

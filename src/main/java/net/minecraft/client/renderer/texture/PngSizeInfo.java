package net.minecraft.client.renderer.texture;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PngSizeInfo {
    public final int width;
    public final int height;

    public PngSizeInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public PngSizeInfo(String location, InputStream inputStream) throws IOException {
        DataInputStream data = new DataInputStream(inputStream);
        long magic = data.readLong();
        if (magic != 0x89504E470D0A1A0AL) {
            throw new IOException("Not a PNG file");
        }
        int chunkLen = data.readInt();
        if (chunkLen < 13) {
            throw new IOException("Invalid IHDR chunk");
        }
        int chunkType = data.readInt();
        if (chunkType != 0x49484452) {
            throw new IOException("Missing IHDR chunk");
        }
        int w = data.readInt();
        int h = data.readInt();
        this.width = w < 16 ? 16 : w;
        this.height = h < 16 ? 16 : h;
    }
} 
package net.minecraft.client.gui;

import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.minecraft.client.renderer.GLAllocation;

public class EaglerFillHelper {
    private static ByteBuffer fillBuffer;

    public static void fill(int x1, int y1, int x2, int y2, int argb) {
        if (x1 >= x2 || y1 >= y2) return;

        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        int a = (argb >> 24) & 0xFF;

        int size = 4 * (3 * 4 + 4);
        if (fillBuffer == null || fillBuffer.capacity() < size) {
            fillBuffer = GLAllocation.createDirectByteBuffer(size);
        }
        fillBuffer.clear();

        int color = r | (g << 8) | (b << 16) | (a << 24);

        fillBuffer.putFloat(x1);
        fillBuffer.putFloat(y2);
        fillBuffer.putFloat(0.0F);
        fillBuffer.putInt(color);
        fillBuffer.putFloat(x2);
        fillBuffer.putFloat(y2);
        fillBuffer.putFloat(0.0F);
        fillBuffer.putInt(color);
        fillBuffer.putFloat(x2);
        fillBuffer.putFloat(y1);
        fillBuffer.putFloat(0.0F);
        fillBuffer.putInt(color);
        fillBuffer.putFloat(x1);
        fillBuffer.putFloat(y1);
        fillBuffer.putFloat(0.0F);
        fillBuffer.putInt(color);

        fillBuffer.flip();
        EaglercraftGPU.renderBuffer(fillBuffer, EaglercraftGPU.ATTRIB_COLOR, RealOpenGLEnums.GL_QUADS, 4);
    }
}

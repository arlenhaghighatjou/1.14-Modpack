package net.minecraft.client.gui.fonts.providers;

import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.Closeable;

@OnlyIn(Dist.CLIENT)
public interface IGlyphProvider extends Closeable {
    default void close() {
    }

    default IGlyphInfo func_212248_a(char p_212248_1_) {
        return null;
    }
}

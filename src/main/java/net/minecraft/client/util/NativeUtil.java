package net.minecraft.client.util;

import net.lax1dude.eaglercraft.EagRuntime;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NativeUtil {
    public static void func_216393_a() {
    }

    public static double func_216394_b() {
        return (double) EagRuntime.nanoTime() / 1.0E9D;
    }
}

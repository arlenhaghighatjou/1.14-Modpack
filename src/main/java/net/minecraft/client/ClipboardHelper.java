package net.minecraft.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public class ClipboardHelper {
    private final ByteBuffer field_216490_a = ByteBuffer.allocateDirect(1024);

}

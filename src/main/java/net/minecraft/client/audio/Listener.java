package net.minecraft.client.audio;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Listener {
    public static final Vec3d field_216470_a = new Vec3d(0.0D, 1.0D, 0.0D);
    private float gain = 1.0F;

    public void setPosition(Vec3d p_216465_1_) {
    }

    public void setOrientation(Vec3d p_216469_1_, Vec3d p_216469_2_) {
    }

    public void setGain(float gainIn) {
        this.gain = gainIn;
    }

    public float getGain() {
        return this.gain;
    }

    public void init() {
    }
}

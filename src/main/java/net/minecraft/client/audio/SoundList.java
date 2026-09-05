package net.minecraft.client.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SoundList {
    private final List<Sound> sounds;
    private final boolean replaceExisting;
    private final String subtitle;

    public SoundList(List<Sound> soundsIn, boolean replceIn, String subtitleIn) {
        this.sounds = soundsIn;
        this.replaceExisting = replceIn;
        this.subtitle = subtitleIn;
    }

    public List<Sound> getSounds() {
        return this.sounds;
    }

    public boolean canReplaceExisting() {
        return this.replaceExisting;
    }

    public String getSubtitle() {
        return this.subtitle;
    }
}

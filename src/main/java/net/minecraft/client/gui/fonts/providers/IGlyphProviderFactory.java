package net.minecraft.client.gui.fonts.providers;

import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGlyphProviderFactory {

    IGlyphProvider create(IResourceManager resourceManagerIn);
}

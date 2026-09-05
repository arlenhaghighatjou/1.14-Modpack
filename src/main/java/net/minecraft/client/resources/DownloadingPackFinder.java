package net.minecraft.client.resources;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.VanillaPack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DownloadingPackFinder implements IPackFinder {
    private final VanillaPack vanillaPack;
    private net.minecraft.client.resources.ClientResourcePackInfo serverPack;

    public DownloadingPackFinder(VFile2 p_i48116_1_, ResourceIndex p_i48116_2_) {
        this.vanillaPack = new VirtualAssetsPack(p_i48116_2_);
    }

    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
        T t = ResourcePackInfo.createResourcePack("vanilla", true, () -> {
            return this.vanillaPack;
        }, packInfoFactory, ResourcePackInfo.Priority.BOTTOM);
        if (t != null) {
            nameToPackMap.put("vanilla", t);
        }
        if (this.serverPack != null) {
            nameToPackMap.put("server", (T) this.serverPack);
        }
    }

    public void setServerPack(net.minecraft.client.resources.ClientResourcePackInfo pack) {
        this.serverPack = pack;
    }

    public VanillaPack getVanillaPack() {
        return this.vanillaPack;
    }

    public void clearResourcePack() {
        this.serverPack = null;
    }
}

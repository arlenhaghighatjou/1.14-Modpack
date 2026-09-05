package net.minecraft.resources;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;

import java.util.Map;
import java.util.function.Supplier;

public class FolderPackFinder implements IPackFinder {
    private final VFile2 folder;

    public FolderPackFinder(VFile2 folderIn) {
        this.folder = folderIn;
    }

    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory) {
        if (!this.folder.dirExists()) {
        }

        java.util.List<net.lax1dude.eaglercraft.internal.vfs2.VFile2> validPacks = new java.util.ArrayList<>();
        for (net.lax1dude.eaglercraft.internal.vfs2.VFile2 f : this.folder.listFiles(true)) {
            String path = f.getPath();
            String folderPath = this.folder.getPath();
            if (path.startsWith(folderPath + "/")) {
                String relative = path.substring(folderPath.length() + 1);
                if (relative.endsWith("/pack.mcmeta")) {
                    String dirName = relative.substring(0, relative.length() - 12);
                    if (!dirName.contains("/")) {
                        validPacks.add(new net.lax1dude.eaglercraft.internal.vfs2.VFile2(this.folder, dirName));
                    }
                } else if (relative.endsWith(".zip") && !relative.contains("/")) {
                    validPacks.add(f);
                }
            }
        }
        net.lax1dude.eaglercraft.internal.vfs2.VFile2[] afile = validPacks.toArray(new net.lax1dude.eaglercraft.internal.vfs2.VFile2[0]);
        if (afile != null) {
            for (VFile2 file1 : afile) {
                String s = "file/" + file1.getName();
                T t = ResourcePackInfo.createResourcePack(s, false, this.makePackSupplier(file1), packInfoFactory, ResourcePackInfo.Priority.TOP);
                if (t != null) {
                    nameToPackMap.put(s, t);
                }
            }

        }
    }

    private Supplier<IResourcePack> makePackSupplier(VFile2 fileIn) {
        return fileIn.dirExists() ? () -> {
            return new FolderPack(fileIn);
        } : () -> {
            return new FilePack(fileIn);
        };
    }
}

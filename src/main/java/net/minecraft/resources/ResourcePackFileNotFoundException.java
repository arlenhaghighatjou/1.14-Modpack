package net.minecraft.resources;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;

import java.io.FileNotFoundException;

public class ResourcePackFileNotFoundException extends FileNotFoundException {
    public ResourcePackFileNotFoundException(VFile2 resourcePack, String fileName) {
        super(String.format("'%s' in ResourcePack '%s'", fileName, resourcePack));
    }
}

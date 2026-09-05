package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.datafix.DefaultTypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;

public class TemplateManager implements IResourceManagerReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<ResourceLocation, Template> templates = Maps.newHashMap();
    private final DataFixer fixer;
    private final MinecraftServer minecraftServer;
    private final VFile2 pathGenerated;

    public TemplateManager(MinecraftServer server, File templateFolder, DataFixer fixerIn) {
        this.minecraftServer = server;
        this.fixer = fixerIn;
        this.pathGenerated = new VFile2(templateFolder.getPath(), "generated");
        server.getResourceManager().addReloadListener(this);
    }

    public Template getTemplateDefaulted(ResourceLocation p_200220_1_) {
        Template template = this.getTemplate(p_200220_1_);
        if (template == null) {
            template = new Template();
            this.templates.put(p_200220_1_, template);
        }

        return template;
    }

    public Template getTemplate(ResourceLocation p_200219_1_) {
        return this.templates.computeIfAbsent(p_200219_1_, (p_209204_1_) -> {
            Template template = this.loadTemplateFile(p_209204_1_);
            return template != null ? template : this.loadTemplateResource(p_209204_1_);
        });
    }

    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.templates.clear();
    }

    private Template loadTemplateResource(ResourceLocation p_209201_1_) {
        ResourceLocation resourcelocation = new ResourceLocation(p_209201_1_.getNamespace(), "structures/" + p_209201_1_.getPath() + ".nbt");

        try (IResource iresource = this.minecraftServer.getResourceManager().getResource(resourcelocation)) {
            Template template = this.loadTemplate(iresource.getInputStream());
            return template;
        } catch (FileNotFoundException var18) {
            return null;
        } catch (Throwable throwable) {
            LOGGER.error("Couldn't load structure {}: {}", p_209201_1_, throwable.toString());
            return null;
        }
    }

    private Template loadTemplateFile(ResourceLocation locationIn) {
        if (!this.pathGenerated.dirExists()) {
            return null;
        } else {
            VFile2 path = this.resolvePath(locationIn, ".nbt");

            try (InputStream inputstream = path.getInputStream()) {
                Template template = this.loadTemplate(inputstream);
                return template;
            } catch (FileNotFoundException var18) {
                return null;
            } catch (IOException ioexception) {
                LOGGER.error("Couldn't load structure from {}", path, ioexception);
                return null;
            }
        }
    }

    private Template loadTemplate(InputStream inputStreamIn) throws IOException {
        CompoundNBT compoundnbt = CompressedStreamTools.readCompressed(inputStreamIn);
        if (!compoundnbt.contains("DataVersion", 99)) {
            compoundnbt.putInt("DataVersion", 500);
        }

        Template template = new Template();
        template.read(NBTUtil.update(this.fixer, DefaultTypeReferences.STRUCTURE, compoundnbt, compoundnbt.getInt("DataVersion")));
        return template;
    }

    public boolean writeToFile(ResourceLocation templateName) {
        Template template = this.templates.get(templateName);
        if (template == null) {
            return false;
        } else {
            VFile2 path = this.resolvePath(templateName, ".nbt");
            VFile2 path1 = new VFile2(path.getParent());
            if (path1 == null) {
                return false;
            } else {

                CompoundNBT compoundnbt = template.writeToNBT(new CompoundNBT());

                try (OutputStream outputstream = path.getOutputStream()) {
                    CompressedStreamTools.writeCompressed(compoundnbt, outputstream);
                    return true;
                } catch (Throwable var21) {
                    return false;
                }
            }
        }
    }

    private VFile2 resolvePathStructures(ResourceLocation locationIn, String extIn) {
        try {
            VFile2 path = new VFile2(this.pathGenerated, locationIn.getNamespace());
            VFile2 path1 = new VFile2(path, "structures");
            return FileUtil.func_214993_b(path1, locationIn.getPath(), extIn);
        } catch (Exception invalidpathexception) {
            throw new ResourceLocationException("Invalid resource path: " + locationIn, invalidpathexception);
        }
    }

    private VFile2 resolvePath(ResourceLocation locationIn, String extIn) {
        if (locationIn.getPath().contains("//")) {
            throw new ResourceLocationException("Invalid resource path: " + locationIn);
        } else {
            VFile2 path = this.resolvePathStructures(locationIn, extIn);
            if (path.getPath().startsWith(this.pathGenerated.getPath()) && FileUtil.func_214995_a(path) && FileUtil.func_214994_b(path)) {
                return path;
            } else {
                throw new ResourceLocationException("Invalid resource path: " + path);
            }
        }
    }

    public void remove(ResourceLocation templatePath) {
        this.templates.remove(templatePath);
    }
}

package net.peyton.eagler.fs;

import com.google.common.collect.Lists;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FileUtils {

    /*
     * BE CAREFUL WHEN EDITING THIS FILE!!!
     *
     * Literally every single operation in this file that iterates over any sort of list or array
     * HAS to be done with a for each loop because of a stupid TeaVM bug that caused it to inline
     * the code when baking in the switch statements that allow it to emulate multiple threads.
     * This is because it shares the same scope as IndexedDB which causes it to suspend and resume
     * the TeaVM thread.
     *
     * The reason that this "fix" works is because TeaVM doesn't inline for each loops with
     * lambda expressions
     *
     * There is also some other random shit that causes TeaVM to break here, so be careful when
     * modifying something or adding something new to this file.
     */

    public static final VFile2 worldsList = WorldsDB.newVFile("worlds_list.txt");

    public static final String dataDir = "eaglercraft";

    public static List<WorldSummary> getSaveList(VFile2 savesDirectory, SaveFormat saveFormat) {
        convertWorldListIfNeeded(savesDirectory);
        ArrayList<WorldSummary> arraylist = Lists.newArrayList();
        if (worldsList.exists()) {
            String[] lines = worldsList.getAllLines();
            for (int i = 0; i < lines.length; ++i) {
                String s = lines[i];
                WorldInfo worldinfo = saveFormat.getWorldInfo(s);
                if (worldinfo != null && (worldinfo.getSaveVersion() == 19132 || worldinfo.getSaveVersion() == 19133)) {
                    boolean flag = worldinfo.getSaveVersion() != 19133;
                    String s1 = worldinfo.getWorldName();

                    if (StringUtils.isEmpty(s1)) {
                        s1 = s;
                    }

                    arraylist.add(new WorldSummary(worldinfo, s, s1, 0L, flag));
                }
            }
        }
        return arraylist;
    }

    private static void convertWorldListIfNeeded(VFile2 savesDirectory) {
        boolean exists = worldsList.exists();
        if (!Minecraft.getInstance().gameSettings.field_225307_E || !exists) {
            if (exists) {
                worldsList.delete();
            }
            convertWorldList(savesDirectory);
            Minecraft.getInstance().gameSettings.field_225307_E = true;
            Minecraft.getInstance().gameSettings.saveOptions();
        }
    }

    private static void convertWorldList(VFile2 savesDirectory) {
        List<String> filesList = savesDirectory.listFilenames(true);
        if (filesList.size() > 0) {
            Set<String> worldNames = new HashSet<>();
            String prefix = savesDirectory.getPath();
            if (prefix != null) {
                if (!prefix.endsWith("/")) {
                    prefix += "/";
                }
                for (String path : filesList) {
                    if (path.startsWith(prefix)) {
                        String relative = path.substring(prefix.length());
                        int slashIndex = relative.indexOf('/');
                        if (slashIndex != -1) {
                            worldNames.add(relative.substring(0, slashIndex));
                        }
                    }
                }
                formatWorldList(worldNames.toArray(new String[0]));
            }
        }
    }

    public static void formatWorldList(String[] worlds) {
        if (worldsList.exists()) {
            worldsList.delete();
        }
        List<String> list = Arrays.asList(worlds);
        Set<String> set = new HashSet<String>();
        list.forEach(world -> {
            set.add(world);
        });
        String[] newWorldList = set.toArray(new String[set.size()]);
        worldsList.setAllChars(String.join("\n", newWorldList));
    }

    public static void removeWorldIfExists(String worldName) {
        String[] worldsTxt = FileUtils.worldsList.getAllLines();
        if (worldsTxt != null) {
            List<String> newWorlds = new ArrayList<>();
            for (int i = 0; i < worldsTxt.length; ++i) {
                String str = worldsTxt[i];
                if (!str.equalsIgnoreCase(worldName)) {
                    newWorlds.add(str);
                }
            }
            FileUtils.worldsList.setAllChars(String.join("\n", newWorlds));
        }
    }

    public static SaveHandler getSaveLoader(VFile2 savesDir, String saveName, MinecraftServer server, com.mojang.datafixers.DataFixer dataFixer) {
        return new net.lax1dude.eaglercraft.sp.server.EaglerSaveHandler(savesDir, saveName, server, dataFixer);
    }
}

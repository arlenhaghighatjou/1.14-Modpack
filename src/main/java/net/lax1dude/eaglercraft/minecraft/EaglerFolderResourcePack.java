package net.lax1dude.eaglercraft.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.lax1dude.eaglercraft.ArrayUtils;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglerInputStream;
import net.lax1dude.eaglercraft.crypto.SHA1Digest;
import net.lax1dude.eaglercraft.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EaglerFolderResourcePack {

    public static final Logger logger = LogManager.getLogger("EaglerFolderResourcePack");

    public static final String SERVER_RESOURCE_PACKS = "srp";
    public static final String RESOURCE_PACKS = "resourcepacks";
    private static boolean isSupported = true;
    private final String prefix;
    private final String resourcePackFile;
    private final String displayName;
    private final Set<String> domains;
    private final long timestamp;

    public EaglerFolderResourcePack(String resourcePackFileIn, String displayName, String prefix, Set<String> domains,
                                    long timestamp) {
        this.resourcePackFile = resourcePackFileIn;
        this.displayName = displayName;
        this.prefix = prefix;
        this.domains = domains;
        this.timestamp = timestamp;
    }

    public static boolean isSupported() {
        return isSupported;
    }

    public static void setSupported(boolean supported) {
        isSupported = supported;
    }

    public static List<EaglerFolderResourcePack> getFolderResourcePacks(String prefix) {
        if (!isSupported) {
            return Collections.emptyList();
        }
        String str = (new VFile2(prefix, "manifest.json")).getAllChars();
        if (str == null) {
            return Collections.emptyList();
        }
        try {
            JSONArray json = (new JSONObject(str)).getJSONArray("resourcePacks");
            List<EaglerFolderResourcePack> ret = new ArrayList<>(json.length());
            for (int i = 0, l = json.length(); i < l; ++i) {
                JSONObject jp = json.getJSONObject(i);
                String folderName = jp.getString("folder");
                String displayName = jp.optString("name", folderName);
                long timestamp = jp.getLong("timestamp");
                Set<String> domains = Sets.newHashSet();
                JSONArray jsonDomains = jp.getJSONArray("domains");
                for (int j = 0, k = jsonDomains.length(); j < k; ++j) {
                    domains.add(jsonDomains.getString(j));
                }
                ret.add(new EaglerFolderResourcePack(folderName, displayName, prefix, domains, timestamp));
            }
            return ret;
        } catch (JSONException ex) {
            logger.error("Failed to load resource pack manifest!");
            logger.error(ex);
            return Collections.emptyList();
        }
    }

    public static EaglerFolderResourcePack importResourcePack(String name, String prefix, byte[] file)
            throws IOException {
        if (!isSupported) {
            return null;
        }
        logger.info("Importing resource pack: {}", name);
        int idx = name.lastIndexOf('.');
        if (idx != -1) {
            name = name.substring(0, idx);
        }
        String folderName = name.replaceAll("[^A-Za-z0-9\\-_ \\(\\)]", "_");

        final List<EaglerFolderResourcePack> existingLst = getFolderResourcePacks(prefix);

        vigg:
        for (; ; ) {
            for (int i = 0, l = existingLst.size(); i < l; ++i) {
                EaglerFolderResourcePack rp = existingLst.get(i);
                if (rp.resourcePackFile.equalsIgnoreCase(folderName)) {
                    folderName = folderName + "-";
                    continue vigg;
                }
            }
            break;
        }

        List<String> fileNames = Lists.newArrayList();

        logger.info("Counting files...");
        ZipEntry zipEntry;
        try (ZipInputStream ziss = new ZipInputStream(new EaglerInputStream(file))) {
            while ((zipEntry = ziss.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    fileNames.add(zipEntry.getName());
                }
            }
        }

        int prefixLen = Integer.MAX_VALUE;
        for (int i = 0, l = fileNames.size(); i < l; ++i) {
            String fn = fileNames.get(i);
            if (fn.equals("pack.mcmeta") || fn.endsWith("/pack.mcmeta")) {
                int currPrefixLen = fn.length() - 11;
                if (prefixLen > currPrefixLen) {
                    prefixLen = currPrefixLen;
                }
            }
        }
        if (prefixLen == Integer.MAX_VALUE) {
            prefixLen = 0;
        }

        Set<String> domainsList = Sets.newHashSet();
        JSONArray propertyFiles = new JSONArray();
        JSONArray potionsFiles = new JSONArray();
        String fn;
        for (int i = 0, l = fileNames.size(); i < l; ++i) {
            fn = fileNames.get(i);
            if (fn.length() > prefixLen + 7) {
                fn = fn.substring(prefixLen + 7);
                int j = fn.indexOf('/');
                if (j != -1) {
                    String dm = fn.substring(0, j);
                    domainsList.add(dm);
                    if ("minecraft".equals(dm)) {
                        if (fn.endsWith(".properties")) {
                            propertyFiles.put(fn.substring(10));
                        } else if ((fn.startsWith("minecraft/mcpatcher/cit/potion/")
                                || fn.startsWith("minecraft/mcpatcher/cit/Potion/")) && fn.endsWith(".png")) {
                            potionsFiles.put(fn.substring(10));
                        } else if (fn.startsWith("minecraft/optifine/cit/potion/") && fn.endsWith(".png")) {
                            potionsFiles.put(fn.substring(10));
                        }
                    }
                }
            }
        }

        VFile2 dstDir = new VFile2(prefix, folderName);
        logger.info("Extracting to: {}", dstDir.getPath());

        try {
            int totalSize = 0;
            int totalFiles = 0;
            int lastProg = 0;
            try (ZipInputStream ziss = new ZipInputStream(new EaglerInputStream(file))) {
                int sz;
                while ((zipEntry = ziss.getNextEntry()) != null) {
                    if (!zipEntry.isDirectory()) {
                        fn = zipEntry.getName();
                        if (fn.length() > prefixLen) {
                            byte[] buffer;
                            sz = (int) zipEntry.getSize();
                            if (sz >= 0) {
                                buffer = new byte[sz];
                                int i = 0, j;
                                while (i < buffer.length && (j = ziss.read(buffer, i, buffer.length - i)) != -1) {
                                    i += j;
                                }
                            } else {
                                buffer = EaglerInputStream.inputStreamToBytes(ziss);
                            }
                            (new VFile2(prefix, folderName, fn.substring(prefixLen))).setAllBytes(buffer);
                            totalSize += buffer.length;
                            ++totalFiles;
                            if (totalSize - lastProg > 25000) {
                                lastProg = totalSize;
                                logger.info("Extracted {} files, {} bytes from ZIP file...", totalFiles, totalSize);
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("Encountered an error extracting zip file, deleting extracted files...");
            for (int i = 0, l = fileNames.size(); i < l; ++i) {
                fn = fileNames.get(i);
                if (fn.length() > prefixLen) {
                    (new VFile2(dstDir, fn.substring(prefixLen))).delete();
                }
            }
            throw ex;
        }

        logger.info("Updating manifest...");

        JSONObject json = new JSONObject();
        json.put("propertyFiles", propertyFiles);
        (new VFile2(prefix, folderName, "assets/minecraft/optifine/_property_files_index.json"))
                .setAllChars(json.toString());

        json = new JSONObject();
        json.put("potionsFiles", potionsFiles);
        (new VFile2(prefix, folderName, "assets/minecraft/mcpatcher/cit/potion/_potions_files_index.json"))
                .setAllChars(json.toString());

        VFile2 manifestFile = new VFile2(prefix, "manifest.json");
        String str = manifestFile.getAllChars();
        JSONArray arr = null;
        if (str != null) {
            try {
                arr = (new JSONObject(str)).getJSONArray("resourcePacks");
            } catch (JSONException ex) {
            }
        }

        if (arr == null) {
            arr = new JSONArray();
        }

        JSONObject manifestEntry = new JSONObject();
        manifestEntry.put("folder", folderName);
        manifestEntry.put("name", name);
        long timestamp = System.currentTimeMillis();
        manifestEntry.put("timestamp", timestamp);
        JSONArray domainsListJson = new JSONArray();
        for (String str2 : domainsList) {
            domainsListJson.put(str2);
        }
        manifestEntry.put("domains", domainsListJson);
        arr.put(manifestEntry);

        manifestFile.setAllChars((new JSONObject()).put("resourcePacks", arr).toString());

        logger.info("Done!");
        return new EaglerFolderResourcePack(folderName, name, prefix, domainsList, timestamp);
    }

    public static void loadRemoteResourcePack(String url, String hash, Consumer<EaglerFolderResourcePack> cb,
                                              Consumer<Runnable> ast, Runnable loading) {
        if (!isSupported || !hash.matches("^[a-f0-9]{40}$")) {
            cb.accept(null);
            return;
        }
        final List<EaglerFolderResourcePack> lst = getFolderResourcePacks(SERVER_RESOURCE_PACKS);
        for (int i = 0, l = lst.size(); i < l; ++i) {
            EaglerFolderResourcePack rp = lst.get(i);
            if (rp.resourcePackFile.equals(hash)) {
                cb.accept(rp);
                return;
            }
        }
        PlatformRuntime.downloadRemoteURIByteArray(url, arr -> {
            ast.accept(() -> {
                if (arr == null) {
                    cb.accept(null);
                    return;
                }
                SHA1Digest digest = new SHA1Digest();
                digest.update(arr, 0, arr.length);
                byte[] hashOut = new byte[20];
                digest.doFinal(hashOut, 0);
                if (!hash.equals(ArrayUtils.hexString(hashOut))) {
                    logger.error("Downloaded resource pack hash does not equal expected resource pack hash!");
                    cb.accept(null);
                    return;
                }
                if (lst.size() >= 5) {
                    lst.sort(Comparator.comparingLong(pack -> pack.timestamp));
                    for (int i = 0; i < lst.size() - 5; i++) {
                        deleteResourcePack(SERVER_RESOURCE_PACKS, lst.get(i).resourcePackFile);
                    }
                }
                loading.run();
                try {
                    cb.accept(importResourcePack(hash, SERVER_RESOURCE_PACKS, arr));
                } catch (IOException ex) {
                    logger.error("Failed to load resource pack downloaded from server!");
                    logger.error(ex);
                    cb.accept(null);
                }
            });
        });
    }

    public static void deleteResourcePack(EaglerFolderResourcePack pack) {
        deleteResourcePack(pack.prefix, pack.resourcePackFile);
    }

    public static void deleteResourcePack(String prefix, String name) {
        if (!isSupported) {
            return;
        }
        logger.info("Deleting resource pack: {}/{}", prefix, name);
        (new VFile2(prefix, name)).listFiles(true).forEach(VFile2::delete);
        VFile2 manifestFile = new VFile2(prefix, "manifest.json");
        String str = manifestFile.getAllChars();
        if (str != null) {
            try {
                JSONArray json = (new JSONObject(str)).getJSONArray("resourcePacks");
                boolean changed = false;
                for (int i = 0, l = json.length(); i < l; ++i) {
                    if (json.getJSONObject(i).getString("folder").equals(name)) {
                        json.remove(i);
                        changed = true;
                        break;
                    }
                }
                if (changed) {
                    manifestFile.setAllChars((new JSONObject()).put("resourcePacks", json).toString());
                } else {
                    logger.warn(
                            "Failed to remove pack \"{}\" from manifest, it wasn't found in the list for some reason",
                            name);
                }
            } catch (JSONException ex) {
            }
        }
    }

    public static void deleteOldResourcePacks(String prefix, long maxAge) {
        if (!isSupported) {
            return;
        }
        long millis = System.currentTimeMillis();
        List<EaglerFolderResourcePack> lst = getFolderResourcePacks(prefix);
        for (int i = 0, l = lst.size(); i < l; ++i) {
            EaglerFolderResourcePack rp = lst.get(i);
            if (millis - rp.timestamp > maxAge) {
                deleteResourcePack(rp);
            }
        }
    }

    public static Collection<String> loadPropertyFileList(String str) {
        try {
            JSONObject json = new JSONObject(str);
            JSONArray arr = json.getJSONArray("propertyFiles");
            int l = arr.length();
            Collection<String> ret = new ArrayList<>(l);
            for (int i = 0; i < l; ++i) {
                ret.add(arr.getString(i));
            }
            return ret;
        } catch (JSONException ex) {
            EagRuntime.debugPrintStackTrace(ex);
            return null;
        }
    }

    public static Collection<String> loadPotionsFileList(String str) {
        try {
            JSONObject json = new JSONObject(str);
            JSONArray arr = json.getJSONArray("potionsFiles");
            int l = arr.length();
            Collection<String> ret = new ArrayList<>(l);
            for (int i = 0; i < l; ++i) {
                ret.add(arr.getString(i));
            }
            return ret;
        } catch (JSONException ex) {
            EagRuntime.debugPrintStackTrace(ex);
            return null;
        }
    }

    public String getResourcePackFile() {
        return resourcePackFile;
    }

    public Set<String> getResourceDomains() {
        return domains;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDisplayName() {
        return displayName;
    }
}

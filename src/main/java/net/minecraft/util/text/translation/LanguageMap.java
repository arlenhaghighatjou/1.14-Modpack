package net.minecraft.util.text.translation;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lax1dude.eaglercraft.EagRuntime;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class LanguageMap {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern NUMERIC_VARIABLE_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final LanguageMap INSTANCE = new LanguageMap();
    private final Map<String, String> languageList = Maps.newHashMap();
    private long lastUpdateTimeInMilliseconds;

    public LanguageMap() {
        try (InputStream inputstream = EagRuntime.getResourceStream("/assets/minecraft/lang/en_us.json")) {
            if (inputstream == null) {
                return;
            }

            JsonElement jsonelement = (new Gson()).fromJson(new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject jsonobject = JSONUtils.getJsonObject(jsonelement, "strings");

            for (Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                String s = NUMERIC_VARIABLE_PATTERN.matcher(JSONUtils.getString(entry.getValue(), entry.getKey())).replaceAll("%s"); 
                this.languageList.put(entry.getKey(), s);
            }

            this.lastUpdateTimeInMilliseconds = Util.milliTime();
        } catch (Throwable t) {
            LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", t);
        }

    }

    public static LanguageMap getInstance() {
        return INSTANCE;
    }

    @OnlyIn(Dist.CLIENT)
    public static synchronized void replaceWith(Map<String, String> p_135063_0_) {
        INSTANCE.languageList.clear();
        INSTANCE.languageList.putAll(p_135063_0_);
        INSTANCE.lastUpdateTimeInMilliseconds = Util.milliTime();
    }

    public synchronized String translateKey(String key) {
        return this.tryTranslateKey(key);
    }

    private String tryTranslateKey(String key) {
        String s = this.languageList.get(key);
        return s == null ? key : s;
    }

    public synchronized boolean exists(String p_210813_1_) {
        return this.languageList.containsKey(p_210813_1_);
    }

    public long getLastUpdateTimeInMilliseconds() {
        return this.lastUpdateTimeInMilliseconds;
    }

    public static synchronized void initServer(java.util.List<String> strs) {
        INSTANCE.languageList.clear();
        for (String s : strs) {
            int idx = s.indexOf('=');
            if (idx > 0) {
                INSTANCE.languageList.put(s.substring(0, idx), s.substring(idx + 1));
            }
        }
        INSTANCE.lastUpdateTimeInMilliseconds = Util.milliTime();
    }

    public static synchronized java.util.List<String> dump() {
        java.util.List<String> list = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, String> entry : INSTANCE.languageList.entrySet()) {
            list.add(entry.getKey() + "=" + entry.getValue());
        }
        return list;
    }
}

package net.lax1dude.eaglercraft.profile;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.TexturesProperty;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.socket.protocol.pkt.client.CPacketGetOtherSkinEAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.client.CPacketGetOtherSkinV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.client.CPacketGetSkinByURLEAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.client.CPacketGetSkinByURLV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.util.SkinPacketVersionCache;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Copyright (c) 2022-2023 lax1dude, ayunami2000. All Rights Reserved.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
public class ServerSkinCache {

    private static final Logger logger = LogManager.getLogger("ServerSkinCache");
    public static boolean needReloadClientSkin = false;
    private static int texId = 0;
    protected final TextureManager textureManager;
    private final SkinCacheEntry defaultCacheEntry = new SkinCacheEntry(0);
    private final SkinCacheEntry defaultSlimCacheEntry = new SkinCacheEntry(1);
    private final Map<EaglercraftUUID, SkinCacheEntry> skinsCache = new HashMap<>();
    private final Map<EaglercraftUUID, WaitingSkin> waitingSkins = new HashMap<>();
    private final Map<Integer, EaglercraftUUID> waitingSkinsV5 = new HashMap<>();
    private final Map<EaglercraftUUID, Long> evictedSkins = new HashMap<>();

    private final ClientPlayNetHandler netHandler;
    private final EaglercraftUUID clientPlayerId;
    private SkinCacheEntry clientPlayerCacheEntry;
    private long lastFlush = EagRuntime.steadyTimeMillis();
    private long lastFlushReq = EagRuntime.steadyTimeMillis();
    private long lastFlushEvict = EagRuntime.steadyTimeMillis();
    private int nextRequestIdV5 = 0;
    public ServerSkinCache(ClientPlayNetHandler netHandler, TextureManager textureManager) {
        this.netHandler = netHandler;
        this.textureManager = textureManager;
        this.clientPlayerId = netHandler.getGameProfile().getId();
        reloadClientPlayerSkin();
    }

    public void reloadClientPlayerSkin() {
        needReloadClientSkin = false;
        this.clientPlayerCacheEntry = new SkinCacheEntry(EaglerProfile.getActiveSkinResourceLocation(), EaglerProfile.getActiveSkinModel());
    }

    public SkinCacheEntry getClientPlayerSkin() {
        return clientPlayerCacheEntry;
    }

    public SkinCacheEntry getSkin(GameProfile player) {
        EaglercraftUUID eagUuid = player.getId();
        EaglercraftUUID uuid = eagUuid;
        if (uuid != null && uuid.equals(clientPlayerId)) {
            return clientPlayerCacheEntry;
        }
        TexturesProperty props = TexturesProperty.parseProfile(player);
        if (props.eaglerPlayer || props.skin == null) {
            if (uuid != null) {
                return _getSkin(uuid);
            } else {
                if ("slim".equalsIgnoreCase(props.model)) {
                    return defaultSlimCacheEntry;
                } else {
                    return defaultCacheEntry;
                }
            }
        } else {
            return getSkin(props.skin, SkinModel.getModelFromId(props.model));
        }
    }

    public SkinCacheEntry getSkin(EaglercraftUUID player) {
        if (player.equals(clientPlayerId)) {
            return clientPlayerCacheEntry;
        }
        return _getSkin(player);
    }

    private SkinCacheEntry _getSkin(EaglercraftUUID player) {
        SkinCacheEntry etr = skinsCache.get(player);
        if (etr == null) {
            if (!waitingSkins.containsKey(player) && !evictedSkins.containsKey(player)) {
                waitingSkins.put(player, new WaitingSkin(EagRuntime.steadyTimeMillis(), null));
                if (isProtocolV5()) {
                    int requestId = nextRequestIdV5();
                    waitingSkinsV5.put(requestId, player);
                    netHandler.sendEaglerMessage(new CPacketGetOtherSkinV5EAG(requestId, player.msb, player.lsb));
                } else {
                    netHandler.sendEaglerMessage(new CPacketGetOtherSkinEAG(player.msb, player.lsb));
                }
            }
            return defaultCacheEntry;
        } else {
            etr.lastCacheHit = EagRuntime.steadyTimeMillis();
            return etr;
        }
    }

    public SkinCacheEntry getSkin(String url, SkinModel skinModelResponse) {
        if (url.length() > 0x7F00) {
            return skinModelResponse == SkinModel.ALEX ? defaultSlimCacheEntry : defaultCacheEntry;
        }
        EaglercraftUUID generatedUUID = SkinPackets.createEaglerURLSkinUUID(url);
        SkinCacheEntry etr = skinsCache.get(generatedUUID);
        if (etr != null) {
            etr.lastCacheHit = EagRuntime.steadyTimeMillis();
            return etr;
        } else {
            if (!waitingSkins.containsKey(generatedUUID) && !evictedSkins.containsKey(generatedUUID)) {
                waitingSkins.put(generatedUUID, new WaitingSkin(EagRuntime.steadyTimeMillis(), skinModelResponse));
                if (isProtocolV5()) {
                    int requestId = nextRequestIdV5();
                    waitingSkinsV5.put(requestId, generatedUUID);
                    netHandler.sendEaglerMessage(new CPacketGetSkinByURLV5EAG(requestId, url));
                } else {
                    netHandler.sendEaglerMessage(new CPacketGetSkinByURLEAG(generatedUUID.msb, generatedUUID.lsb, url));
                }
            }
        }
        return skinModelResponse == SkinModel.ALEX ? defaultSlimCacheEntry : defaultCacheEntry;
    }

    public void cacheSkinPreset(EaglercraftUUID player, int presetId) {
        if (waitingSkins.remove(player) != null) {
            SkinCacheEntry etr = skinsCache.remove(player);
            if (etr != null) {
                etr.free();
            }
            skinsCache.put(player, new SkinCacheEntry(presetId));
        } else {
            logger.error("Unsolicited skin response recieved for \"{}\"! (preset {})", player, presetId);
        }
    }

    public void cacheSkinCustom(EaglercraftUUID player, byte[] pixels, SkinModel model) {
        WaitingSkin waitingSkin;
        if ((waitingSkin = waitingSkins.remove(player)) != null) {
            SkinCacheEntry etr = skinsCache.remove(player);
            if (etr != null) {
                etr.free();
            }
            if (waitingSkin.model != null) {
                model = waitingSkin.model;
            } else if (model == null) {
                model = (player.hashCode() & 1) != 0 ? SkinModel.ALEX : SkinModel.STEVE;
            }
            try {
                etr = new SkinCacheEntry(new EaglerSkinTexture(pixels, model.width, model.height),
                        new ResourceLocation("eagler:skins/multiplayer/tex_" + texId++), model);
            } catch (Throwable t) {
                etr = new SkinCacheEntry(0);
                logger.error("Could not process custom skin packet for \"{}\"!", player);
                logger.error(t);
            }
            skinsCache.put(player, etr);
        } else {
            logger.error("Unsolicited skin response recieved for \"{}\"! (custom {}x{})", player, model.width, model.height);
        }
    }

    public void cacheSkinPresetV5(int requestId, int presetId) {
        EaglercraftUUID player = waitingSkinsV5.remove(requestId);
        if (player != null) {
            cacheSkinPreset(player, presetId);
        }
    }

    public void cacheSkinCustomV5(int requestId, byte[] pixels, int modelId) {
        EaglercraftUUID player = waitingSkinsV5.remove(requestId);
        if (player == null) {
            return;
        }
        SkinModel model;
        if (modelId == 0xFF) {
            model = getRequestedSkinType(player);
        } else {
            model = SkinModel.getModelFromId(modelId & 0x7F);
            if ((modelId & 0x80) != 0 && model.sanitize) {
                model = SkinModel.STEVE;
            }
        }
        if (model != null && model.highPoly != null) {
            model = SkinModel.STEVE;
        }
        cacheSkinCustom(player, SkinPacketVersionCache.convertToV3Raw(pixels), model);
    }

    private boolean isProtocolV5() {
        return netHandler.getEaglerMessageProtocol() != null && netHandler.getEaglerMessageProtocol().ver >= 5;
    }

    private int nextRequestIdV5() {
        return nextRequestIdV5 = (nextRequestIdV5 + 1) & 0x3FFF;
    }

    public SkinModel getRequestedSkinType(EaglercraftUUID waiting) {
        WaitingSkin waitingSkin;
        if ((waitingSkin = waitingSkins.get(waiting)) != null) {
            return waitingSkin.model;
        } else {
            return null;
        }
    }

    public void flush() {
        long millis = EagRuntime.steadyTimeMillis();
        if (millis - lastFlushReq > 5000l) {
            lastFlushReq = millis;
            if (!waitingSkins.isEmpty()) {
                Iterator<WaitingSkin> waitingItr = waitingSkins.values().iterator();
                while (waitingItr.hasNext()) {
                    if (millis - waitingItr.next().timeout > 20000l) {
                        waitingItr.remove();
                    }
                }
            }
            if (!waitingSkinsV5.isEmpty()) {
                Iterator<Map.Entry<Integer, EaglercraftUUID>> waitingV5Itr = waitingSkinsV5.entrySet().iterator();
                while (waitingV5Itr.hasNext()) {
                    if (!waitingSkins.containsKey(waitingV5Itr.next().getValue())) {
                        waitingV5Itr.remove();
                    }
                }
            }
        }
        if (millis - lastFlushEvict > 1000l) {
            lastFlushEvict = millis;
            if (!evictedSkins.isEmpty()) {
                Iterator<Long> evictItr = evictedSkins.values().iterator();
                while (evictItr.hasNext()) {
                    if (millis - evictItr.next().longValue() > 3000l) {
                        evictItr.remove();
                    }
                }
            }
        }
        if (millis - lastFlush > 60000l) {
            lastFlush = millis;
            if (!skinsCache.isEmpty()) {
                Iterator<SkinCacheEntry> entryItr = skinsCache.values().iterator();
                while (entryItr.hasNext()) {
                    SkinCacheEntry etr = entryItr.next();
                    if (millis - etr.lastCacheHit > 900000l) { // 15 minutes
                        entryItr.remove();
                        etr.free();
                    }
                }
            }
        }
        if (needReloadClientSkin) {
            reloadClientPlayerSkin();
        }
    }

    public void destroy() {
        Iterator<SkinCacheEntry> entryItr = skinsCache.values().iterator();
        while (entryItr.hasNext()) {
            entryItr.next().free();
        }
        skinsCache.clear();
        waitingSkins.clear();
        waitingSkinsV5.clear();
        evictedSkins.clear();
    }

    public void evictSkin(EaglercraftUUID uuid) {
        evictedSkins.put(uuid, Long.valueOf(EagRuntime.steadyTimeMillis()));
        SkinCacheEntry etr = skinsCache.remove(uuid);
        if (etr != null) {
            etr.free();
        }
    }

    public void handleInvalidate(EaglercraftUUID uuid) {
        SkinCacheEntry etr = skinsCache.remove(uuid);
        if (etr != null) {
            etr.free();
        }
    }

    protected static class CacheCustomSkin {

        protected final EaglerSkinTexture textureInstance;
        protected final ResourceLocation resourceLocation;
        protected final SkinModel model;

        protected CacheCustomSkin(EaglerSkinTexture textureInstance, ResourceLocation resourceLocation, SkinModel model) {
            this.textureInstance = textureInstance;
            this.resourceLocation = resourceLocation;
            this.model = model;
        }

    }

    protected static class WaitingSkin {

        protected final long timeout;
        protected final SkinModel model;

        protected WaitingSkin(long timeout, SkinModel model) {
            this.timeout = timeout;
            this.model = model;
        }

    }

    public class SkinCacheEntry {

        protected final boolean isPresetSkin;
        protected final int presetSkinId;
        protected final CacheCustomSkin customSkin;

        protected long lastCacheHit = EagRuntime.steadyTimeMillis();

        protected SkinCacheEntry(EaglerSkinTexture textureInstance, ResourceLocation resourceLocation, SkinModel model) {
            this.isPresetSkin = false;
            this.presetSkinId = -1;
            this.customSkin = new CacheCustomSkin(textureInstance, resourceLocation, model);
            ServerSkinCache.this.textureManager.loadTexture(resourceLocation, textureInstance);
        }

        /**
         * Use only for the constant for the client player
         */
        protected SkinCacheEntry(ResourceLocation resourceLocation, SkinModel model) {
            this.isPresetSkin = false;
            this.presetSkinId = -1;
            this.customSkin = new CacheCustomSkin(null, resourceLocation, model);
        }

        protected SkinCacheEntry(int presetSkinId) {
            this.isPresetSkin = true;
            this.presetSkinId = presetSkinId;
            this.customSkin = null;
        }

        public ResourceLocation getResourceLocation() {
            if (isPresetSkin) {
                return DefaultSkins.getSkinFromId(presetSkinId).location;
            } else {
                if (customSkin != null) {
                    return customSkin.resourceLocation;
                } else {
                    return DefaultSkins.DEFAULT_STEVE.location;
                }
            }
        }

        public SkinModel getSkinModel() {
            if (isPresetSkin) {
                return DefaultSkins.getSkinFromId(presetSkinId).model;
            } else {
                if (customSkin != null) {
                    return customSkin.model;
                } else {
                    return DefaultSkins.DEFAULT_STEVE.model;
                }
            }
        }

        protected void free() {
            if (!isPresetSkin) {
                ServerSkinCache.this.textureManager.deleteTexture(customSkin.resourceLocation);
            }
        }

    }

}

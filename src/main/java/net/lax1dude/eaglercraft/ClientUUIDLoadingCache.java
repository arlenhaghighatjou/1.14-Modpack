package net.lax1dude.eaglercraft;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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
public class ClientUUIDLoadingCache {

    public static final EaglercraftUUID NULL_UUID = new EaglercraftUUID(0l, 0l);
    public static final EaglercraftUUID PENDING_UUID = new EaglercraftUUID(0x6969696969696969l, 0x6969696969696969l);
    public static final EaglercraftUUID VANILLA_UUID = new EaglercraftUUID(0x1DCE015CD384374El, 0x85030A4DE95E5736l);
    private static final Logger logger = LogManager.getLogger("ClientUUIDLoadingCache");
    private static final Map<Integer, WaitingLookup> waitingIDs = new HashMap<>();
    private static final Map<EaglercraftUUID, WaitingLookup> waitingUUIDs = new HashMap<>();
    private static final Map<EaglercraftUUID, Long> evictedUUIDs = new HashMap<>();
    private static final EaglercraftUUID MAGIC_DISABLE_NON_EAGLER_PLAYERS = new EaglercraftUUID(0xEEEEA64771094C4EL, 0x86E55B81D17E67EBL);
    private static int requestId = 0;
    private static long lastFlushReq = EagRuntime.steadyTimeMillis();
    private static long lastFlushEvict = EagRuntime.steadyTimeMillis();
    private static boolean ignoreNonEaglerPlayers = false;

    public static void update() {
        long timestamp = EagRuntime.steadyTimeMillis();
        if (timestamp - lastFlushReq > 5000l) {
            lastFlushReq = timestamp;
            if (!waitingIDs.isEmpty()) {
                Iterator<WaitingLookup> itr = waitingIDs.values().iterator();
                while (itr.hasNext()) {
                    WaitingLookup lookup = itr.next();
                    if (timestamp - lookup.timestamp > 15000l) {
                        itr.remove();
                        waitingUUIDs.remove(lookup.uuid);
                    }
                }
            }
        }
        if (timestamp - lastFlushEvict > 1000l) {
            lastFlushEvict = timestamp;
            if (!evictedUUIDs.isEmpty()) {
                Iterator<Long> evictItr = evictedUUIDs.values().iterator();
                while (evictItr.hasNext()) {
                    if (timestamp - evictItr.next().longValue() > 3000l) {
                        evictItr.remove();
                    }
                }
            }
        }
    }

    public static void flushRequestCache() {
        waitingIDs.clear();
        waitingUUIDs.clear();
        evictedUUIDs.clear();
    }

    public static void handleResponse(int requestId, EaglercraftUUID clientId) {
        WaitingLookup lookup = waitingIDs.remove(requestId);
        if (lookup != null) {
            lookup.player.clientBrandUUIDCache = clientId;
            waitingUUIDs.remove(lookup.uuid);
        } else {
            if (requestId == -1 && MAGIC_DISABLE_NON_EAGLER_PLAYERS.equals(clientId)) {
                ignoreNonEaglerPlayers = true;
            } else {
                logger.warn("Unsolicited client brand UUID lookup response #{} recieved! (Brand UUID: {})", requestId, clientId);
            }
        }
    }

    public static void evict(EaglercraftUUID clientId) {
        evictedUUIDs.put(clientId, Long.valueOf(EagRuntime.steadyTimeMillis()));
        WaitingLookup lk = waitingUUIDs.remove(clientId);
        if (lk != null) {
            waitingIDs.remove(lk.reqID);
        }
    }

    public static void resetFlags() {
        ignoreNonEaglerPlayers = false;
    }

    private static class WaitingLookup {

        private final int reqID;
        private final EaglercraftUUID uuid;
        private final long timestamp;
        private final AbstractClientPlayerEntity player;

        public WaitingLookup(int reqID, EaglercraftUUID uuid, long timestamp, AbstractClientPlayerEntity player) {
            this.reqID = reqID;
            this.uuid = uuid;
            this.timestamp = timestamp;
            this.player = player;
        }

    }
}

package net.lax1dude.eaglercraft.sp.server.skins;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherCapePresetEAG;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
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
public class IntegratedCapeService {

    public static final Logger logger = LogManager.getLogger("IntegratedCapeService");

    public static final int masterRateLimitPerPlayer = 250;

    private final Map<EaglercraftUUID, GameMessagePacket> capesCache = new HashMap<>();

    public void processLoginPacket(byte[] packetData, ServerPlayerEntity sender) {
        try {
            IntegratedCapePackets.registerEaglerPlayer(new EaglercraftUUID(sender.getUniqueID().getMostSignificantBits(), sender.getUniqueID().getLeastSignificantBits()), packetData, this);
        } catch (IOException e) {
            logger.error("Invalid skin data packet recieved from player {}!", sender.getName());
            logger.error(e);
        }
    }

    public void registerEaglercraftPlayer(EaglercraftUUID playerUUID, GameMessagePacket capePacket) {
        capesCache.put(playerUUID, capePacket);
    }

    public void processGetOtherCape(EaglercraftUUID searchUUID, ServerPlayerEntity sender) {
        GameMessagePacket maybeCape = capesCache.get(searchUUID);
        if (maybeCape == null) {
            maybeCape = new SPacketOtherCapePresetEAG(searchUUID.msb, searchUUID.lsb, 0);
        }
    }

    public void unregisterPlayer(EaglercraftUUID playerUUID) {
        synchronized (capesCache) {
            capesCache.remove(playerUUID);
        }
    }
}

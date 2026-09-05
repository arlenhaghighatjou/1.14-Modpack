package net.lax1dude.eaglercraft.sp.server.skins;

import net.lax1dude.eaglercraft.Base64;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.crypto.SHA1Digest;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherSkinPresetEAG;
import net.lax1dude.eaglercraft.socket.protocol.util.SkinPacketVersionCache;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.peyton.eagler.fs.WorldsDB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Copyright (c) 2022-2024 lax1dude. All Rights Reserved.
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
public class IntegratedSkinService {

    public static final Logger logger = LogManager.getLogger("IntegratedSkinService");

    public static final byte[] skullNotFoundTexture = new byte[4096];
    private static final String hex = "0123456789abcdef";

    static {
        for (int y = 0; y < 16; ++y) {
            for (int x = 0; x < 64; ++x) {
                int i = (y << 8) | (x << 2);
                byte j = ((x + y) & 1) == 1 ? (byte) 255 : 0;
                skullNotFoundTexture[i] = (byte) 255;
                skullNotFoundTexture[i + 1] = j;
                skullNotFoundTexture[i + 2] = 0;
                skullNotFoundTexture[i + 3] = j;
            }
        }
    }

    public final VFile2 skullsDirectory;
    public final Map<EaglercraftUUID, SkinPacketVersionCache> playerSkins = new HashMap<>();
    public final Map<String, CustomSkullData> customSkulls = new HashMap<>();
    private long lastFlush = 0l;

    public IntegratedSkinService(VFile2 skullsDirectory) {
        this.skullsDirectory = skullsDirectory;
    }

    public void processLoginPacket(byte[] packetData, ServerPlayerEntity sender, int protocolVers) {
        try {
            IntegratedSkinPackets.registerEaglerPlayer(new EaglercraftUUID(sender.getUniqueID().getMostSignificantBits(), sender.getUniqueID().getLeastSignificantBits()), packetData, this, protocolVers);
        } catch (IOException e) {
            logger.error("Invalid skin data packet recieved from player {}!", sender.getName());
            logger.error(e);
        }
    }

    public void processPacketGetOtherSkin(EaglercraftUUID searchUUID, ServerPlayerEntity sender) {
        SkinPacketVersionCache playerSkin = playerSkins.get(searchUUID);
        GameMessagePacket toSend = null;
        if (playerSkin != null) {
            toSend = playerSkin.get(sender.connection.getEaglerMessageProtocol(), searchUUID.msb, searchUUID.lsb);
        }
        if (toSend == null) {
            toSend = new SPacketOtherSkinPresetEAG(searchUUID.msb, searchUUID.lsb,
                    (searchUUID.hashCode() & 1) != 0 ? 1 : 0);
        }
        sender.connection.sendEaglerMessage(toSend);
    }

    public void processPacketGetOtherSkin(EaglercraftUUID searchUUID, String urlStr, ServerPlayerEntity sender) {
        urlStr = urlStr.toLowerCase();
        GameMessagePacket playerSkin;
        if (!urlStr.startsWith("eagler://")) {
            playerSkin = new SPacketOtherSkinPresetEAG(searchUUID.msb, searchUUID.lsb, 0);
        } else {
            urlStr = urlStr.substring(9);
            if (urlStr.contains(VFile2.pathSeperator)) {
                playerSkin = new SPacketOtherSkinPresetEAG(searchUUID.msb, searchUUID.lsb, 0);
            } else {
                CustomSkullData sk = customSkulls.get(urlStr);
                if (sk == null) {
                    customSkulls.put(urlStr, sk = loadCustomSkull(urlStr));
                } else {
                    sk.lastHit = EagRuntime.steadyTimeMillis();
                }
                playerSkin = sk.getSkinPacket(searchUUID, sender.connection.getEaglerMessageProtocol());
            }
        }
        sender.connection.sendEaglerMessage(playerSkin);
    }

    public void processPacketPlayerSkin(EaglercraftUUID clientUUID, SkinPacketVersionCache generatedPacket, int skinModel) {
        playerSkins.put(clientUUID, generatedPacket);
    }

    public void unregisterPlayer(EaglercraftUUID clientUUID) {
        playerSkins.remove(clientUUID);
    }

    public void processPacketInstallNewSkin(byte[] skullData, ServerPlayerEntity sender) {
        if (!sender.server.isCommandBlockEnabled()) {
            StringTextComponent cc = new StringTextComponent("command.skull.nopermission");
            cc.getStyle().setColor(TextFormatting.RED);
            sender.sendMessage(cc);
            return;
        }
        String fileName = "eagler://" + installNewSkull(skullData);
        CompoundNBT rootTagCompound = new CompoundNBT();
        CompoundNBT ownerTagCompound = new CompoundNBT();
        ownerTagCompound.putString("Name", "Eagler");
        ownerTagCompound.putString("Id", EaglercraftUUID.nameUUIDFromBytes((("EaglerSkullUUID:" + fileName).getBytes(StandardCharsets.UTF_8))).toString());
        CompoundNBT propertiesTagCompound = new CompoundNBT();
        ListNBT texturesTagList = new ListNBT();
        CompoundNBT texturesTagCompound = new CompoundNBT();
        String texturesProp = "{\"textures\":{\"SKIN\":{\"url\":\"" + fileName + "\",\"metadata\":{\"model\":\"default\"}}}}";
        texturesTagCompound.putString("Value", Base64.encodeBase64String(texturesProp.getBytes(StandardCharsets.UTF_8)));
        texturesTagList.add(texturesTagCompound);
        propertiesTagCompound.put("textures", texturesTagList);
        ownerTagCompound.put("Properties", propertiesTagCompound);
        rootTagCompound.put("SkullOwner", ownerTagCompound);
        CompoundNBT displayTagCompound = new CompoundNBT();
        displayTagCompound.putString("Name", TextFormatting.RESET + "Custom Eaglercraft Skull");
        ListNBT loreList = new ListNBT();
        loreList.add(new StringNBT(TextFormatting.GRAY + (fileName.length() > 24 ? (fileName.substring(0, 22) + "...") : fileName)));
        displayTagCompound.put("Lore", loreList);
        rootTagCompound.put("display", displayTagCompound);
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        stack.setTag(rootTagCompound);
        boolean flag = sender.inventory.addItemStackToInventory(stack);
        if (flag) {
            sender.container.detectAndSendChanges();
        }
        sender.sendMessage(new TranslationTextComponent("command.skull.feedback", fileName));
    }

    public String installNewSkull(byte[] skullData) {
        // set to 16384 to save a full 64x64 skin
        if (skullData.length > 4096) {
            byte[] tmp = skullData;
            skullData = new byte[4096];
            System.arraycopy(tmp, 0, skullData, 0, 4096);
        }
        SHA1Digest sha = new SHA1Digest();
        sha.update(skullData, 0, skullData.length);
        byte[] hash = new byte[20];
        sha.doFinal(hash, 0);
        char[] hashText = new char[40];
        for (int i = 0; i < 20; ++i) {
            hashText[i << 1] = hex.charAt((hash[i] & 0xF0) >> 4);
            hashText[(i << 1) + 1] = hex.charAt(hash[i] & 0x0F);
        }
        String str = "skin-" + new String(hashText) + ".bmp";
        customSkulls.put(str, new CustomSkullData(str, skullData));
        WorldsDB.newVFile(skullsDirectory, str).setAllBytes(skullData);
        return str;
    }

    private CustomSkullData loadCustomSkull(String urlStr) {
        byte[] data = WorldsDB.newVFile(skullsDirectory, urlStr).getAllBytes();
        if (data == null) {
            return new CustomSkullData(urlStr, skullNotFoundTexture);
        } else {
            return new CustomSkullData(urlStr, data);
        }
    }

    public void flushCache() {
        long cur = EagRuntime.steadyTimeMillis();
        if (cur - lastFlush > 300000l) {
            lastFlush = cur;
            Iterator<CustomSkullData> customSkullsItr = customSkulls.values().iterator();
            while (customSkullsItr.hasNext()) {
                if (cur - customSkullsItr.next().lastHit > 900000l) {
                    customSkullsItr.remove();
                }
            }
        }
    }
}

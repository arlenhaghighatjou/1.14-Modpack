package net.lax1dude.eaglercraft.profile;

import net.lax1dude.eaglercraft.ArrayUtils;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.crypto.MD5Digest;

public class SkinPackets {

    public static final int PACKET_MY_SKIN_PRESET = 0x01;
    public static final int PACKET_MY_SKIN_CUSTOM = 0x02;

    public static final int PACKET_MY_CAPE_PRESET = 0x01;
    public static final int PACKET_MY_CAPE_CUSTOM = 0x02;

    public static byte[] writeMySkinPreset(int skinId) {
        return new byte[]{(byte) PACKET_MY_SKIN_PRESET, (byte) (skinId >>> 24), (byte) (skinId >>> 16),
                (byte) (skinId >>> 8), (byte) (skinId & 0xFF)};
    }

    public static byte[] writeMySkinCustomV3(GuiScreenEditProfile.CustomSkin customSkin) {
        byte[] packet = new byte[2 + 16384];
        packet[0] = (byte) PACKET_MY_SKIN_CUSTOM;
        packet[1] = (byte) customSkin.model.id;
        System.arraycopy(customSkin.data, 0, packet, 2, 16384);
        return packet;
    }

    public static byte[] writeMySkinCustomV4(GuiScreenEditProfile.CustomSkin customSkin) {
        byte[] packet = new byte[2 + 12288];
        packet[0] = (byte) PACKET_MY_SKIN_CUSTOM;
        packet[1] = (byte) customSkin.model.id;
        byte[] v3data = customSkin.data;
        for (int i = 0, j, k; i < 4096; ++i) {
            j = i << 2;
            k = i * 3 + 2;
            packet[k] = v3data[j + 1];
            packet[k + 1] = v3data[j + 2];
            packet[k + 2] = (byte) (((v3data[j + 3] & 0xFF) >>> 1) | (v3data[j] & 0x80));
        }
        return packet;
    }

    public static byte[] writeMyCapePreset(int capeId) {
        return new byte[]{(byte) PACKET_MY_CAPE_PRESET, (byte) (capeId >>> 24), (byte) (capeId >>> 16),
                (byte) (capeId >>> 8), (byte) (capeId & 0xFF)};
    }

    public static EaglercraftUUID createEaglerURLSkinUUID(String skinUrl) {
        MD5Digest dg = new MD5Digest();
        byte[] bytes = ArrayUtils.asciiString("EaglercraftSkinURL:" + skinUrl);
        dg.update(bytes, 0, bytes.length);
        byte[] md5Bytes = new byte[16];
        dg.doFinal(md5Bytes, 0);
        md5Bytes[6] &= 0x0f;
        md5Bytes[6] |= 0x30;
        md5Bytes[8] &= 0x3f;
        md5Bytes[8] |= 0x80;
        return new EaglercraftUUID(md5Bytes);
    }

}

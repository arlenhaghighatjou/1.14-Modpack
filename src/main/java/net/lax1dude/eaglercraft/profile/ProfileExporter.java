/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 *
 * 
 *
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

package net.lax1dude.eaglercraft.profile;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglerOutputStream;
import net.lax1dude.eaglercraft.EaglerZLIB;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.minecraft.EaglerFolderResourcePack;
import net.lax1dude.eaglercraft.sp.relay.RelayManager;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.zip.CRC32;

public class ProfileExporter {

    public static void exportProfileAndSettings(boolean doExportProfile, boolean doExportSettings,
            boolean doExportServers, boolean doExportResourcePacks) throws IOException {
        doExportResourcePacks &= EaglerFolderResourcePack.isSupported();
        EaglerOutputStream osb = new EaglerOutputStream();
        osb.write(new byte[]{(byte) 69, (byte) 65, (byte) 71, (byte) 80, (byte) 75, (byte) 71, (byte) 36, (byte) 36}); // EAGPKG$$
        osb.write(new byte[]{(byte) 6, (byte) 118, (byte) 101, (byte) 114, (byte) 50, (byte) 46, (byte) 48}); // 6 + ver2.0
        Date d = new Date();

        byte[] filename = "profile.epk".getBytes(StandardCharsets.UTF_8);
        osb.write(filename.length);
        osb.write(filename);

        byte[] comment = ("\n\n #  Eaglercraft profile backup - \"" + EaglerProfile.getName() + "\""
                + "\n #  Contains: " + (doExportProfile ? "profile " : "") + (doExportSettings ? "settings " : "")
                + (doExportServers ? "servers " : "") + (doExportResourcePacks ? "resourcePacks" : "") + "\n\n")
                .getBytes(StandardCharsets.UTF_8);

        osb.write((comment.length >>> 8) & 255);
        osb.write(comment.length & 255);
        osb.write(comment);

        writeLong(d.getTime(), osb);

        int lengthIntegerOffset = osb.size();
        osb.write(new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255}); // this will be replaced with the file count

        osb.write('G'); // compression type: gzip
        int fileCount = 2;
        try (OutputStream os = EaglerZLIB.newGZIPOutputStream(osb)) {
            os.write(new byte[]{(byte) 72, (byte) 69, (byte) 65, (byte) 68}); // HEAD
            os.write(new byte[]{(byte) 9, (byte) 102, (byte) 105, (byte) 108, (byte) 101, (byte) 45, (byte) 116, (byte) 121,
                    (byte) 112, (byte) 101}); // 9 + file-type
            os.write(new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 14, (byte) 101, (byte) 112, (byte) 107, (byte) 47, (byte) 112, (byte) 114, (byte) 111,
                    (byte) 102, (byte) 105, (byte) 108, (byte) 101, (byte) 49, (byte) 56, (byte) 56}); // 14 + epk/profile188
            os.write('>');

            os.write(new byte[]{(byte) 72, (byte) 69, (byte) 65, (byte) 68}); // HEAD
            os.write(new byte[]{(byte) 12, (byte) 102, (byte) 105, (byte) 108, (byte) 101, (byte) 45, (byte) 101, (byte) 120,
                    (byte) 112, (byte) 111, (byte) 114, (byte) 116, (byte) 115, (byte) 0, (byte) 0, (byte) 0, (byte) 1}); // 12 + file-exports + 1
            os.write((doExportProfile ? 1 : 0) | (doExportSettings ? 2 : 0) | (doExportServers ? 4 : 0) | (doExportResourcePacks ? 8 : 0));
            os.write('>');

            if (doExportProfile) {
                byte[] profileData = EaglerProfile.write();
                if (profileData == null) {
                    throw new IOException("Could not write profile data!");
                }
                exportFileToEPK("_eaglercraftX.p", profileData, os);
                ++fileCount;
            }

            if (doExportSettings) {
                Minecraft mc = Minecraft.getInstance();
                mc.gameSettings.saveOptions();
                byte[] gameSettings = mc.gameSettings.optionsFile.getAllBytes();
                if (gameSettings == null) {
                    throw new IOException("Could not write game settings!");
                }
                exportFileToEPK("_eaglercraftX.g", gameSettings, os);
                ++fileCount;
                byte[] relays = RelayManager.relayManager.write();
                if (relays == null) {
                    throw new IOException("Could not write relay settings!");
                }
                exportFileToEPK("_eaglercraftX.r", relays, os);
                ++fileCount;
            }

            if (doExportServers) {
                Minecraft mc = Minecraft.getInstance();
                byte[] servers = new VFile2(mc.gameDir, "servers.dat").getAllBytes();
                if (servers == null) {
                    servers = new byte[0];
                }
                exportFileToEPK("_eaglercraftX.s", servers, os);
                ++fileCount;
            }

            if (doExportResourcePacks) {
                byte[] packManifest = (new VFile2(EaglerFolderResourcePack.RESOURCE_PACKS + "/manifest.json")).getAllBytes();
                if (packManifest != null) {
                    exportFileToEPK(EaglerFolderResourcePack.RESOURCE_PACKS + "/manifest.json", packManifest, os);
                    ++fileCount;
                    VFile2 baseDir = new VFile2(EaglerFolderResourcePack.RESOURCE_PACKS);
                    List<VFile2> files = baseDir.listFiles(true);
                    for (int i = 0, l = files.size(); i < l; ++i) {
                        VFile2 f = files.get(i);
                        if (f.getPath().equals(EaglerFolderResourcePack.RESOURCE_PACKS + "/manifest.json")) {
                            continue;
                        }
                        byte[] b = f.getAllBytes();
                        if (b != null) {
                            exportFileToEPK(f.getPath(), b, os);
                            ++fileCount;
                        }
                    }
                }
            }

            os.write(new byte[]{(byte) 69, (byte) 78, (byte) 68, (byte) 36}); // END$
        }

        osb.write(new byte[]{(byte) 58, (byte) 58, (byte) 58, (byte) 89, (byte) 69, (byte) 69, (byte) 58, (byte) 62}); // :::YEE:>

        byte[] ret = osb.toByteArray();

        ret[lengthIntegerOffset] = (byte) ((fileCount >>> 24) & 0xFF);
        ret[lengthIntegerOffset + 1] = (byte) ((fileCount >>> 16) & 0xFF);
        ret[lengthIntegerOffset + 2] = (byte) ((fileCount >>> 8) & 0xFF);
        ret[lengthIntegerOffset + 3] = (byte) (fileCount & 0xFF);

        EagRuntime.downloadFileWithName(EaglerProfile.getName() + "-backup.epk", ret);
    }

    private static void exportFileToEPK(String name, byte[] contents, OutputStream os) throws IOException {
        CRC32 checkSum = new CRC32();
        checkSum.update(contents);
        long sum = checkSum.getValue();

        os.write(new byte[]{(byte) 70, (byte) 73, (byte) 76, (byte) 69}); // FILE

        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        os.write(nameBytes.length);
        os.write(nameBytes);
        writeInt(contents.length + 5, os);
        writeInt((int) sum, os);

        os.write(contents);
        os.write(':');
        os.write('>');
    }

    private static void writeInt(int i, OutputStream os) throws IOException {
        os.write((i >>> 24) & 0xFF);
        os.write((i >>> 16) & 0xFF);
        os.write((i >>> 8) & 0xFF);
        os.write(i & 0xFF);
    }

    private static void writeLong(long i, OutputStream os) throws IOException {
        os.write((int) ((i >>> 56l) & 0xFFl));
        os.write((int) ((i >>> 48l) & 0xFFl));
        os.write((int) ((i >>> 40l) & 0xFFl));
        os.write((int) ((i >>> 32l) & 0xFFl));
        os.write((int) ((i >>> 24l) & 0xFFl));
        os.write((int) ((i >>> 16l) & 0xFFl));
        os.write((int) ((i >>> 8l) & 0xFFl));
        os.write((int) (i & 0xFFl));
    }
}

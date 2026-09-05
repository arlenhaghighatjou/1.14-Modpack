/*
 * Copyright (c) 2022-2023 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.internal.lwjgl;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EagUtils;
import net.lax1dude.eaglercraft.Filesystem;
import net.lax1dude.eaglercraft.internal.EnumPlatformANGLE;
import net.lax1dude.eaglercraft.internal.IEaglerFilesystem;
import net.lax1dude.eaglercraft.internal.PlatformInput;
import net.lax1dude.eaglercraft.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.profile.EaglerProfile;
import net.lax1dude.eaglercraft.sp.relay.RelayManager;
import net.minecraft.client.Minecraft;
import net.peyton.eagler.fs.WorldsDB;

import javax.swing.*;

public class LWJGLEntryPoint {

    public static Thread mainThread = null;

    public static void main_(String[] args) {
        mainThread = Thread.currentThread();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | UnsupportedLookAndFeelException e) {
            System.err.println("Could not set system look and feel: " + e.toString());
        }

        boolean hideRenderDocDialog = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equalsIgnoreCase("hide-renderdoc")) {
                hideRenderDocDialog = true;
            }
        }

        if (!hideRenderDocDialog) {
            LaunchRenderDocDialog lr = new LaunchRenderDocDialog();
            lr.setLocationRelativeTo(null);
            lr.setVisible(true);

            while (lr.isVisible()) {
                EagUtils.sleep(100);
            }

            lr.dispose();
        }

        getPlatformOptionsFromArgs(args);

        RelayManager.relayManager.load(EagRuntime.getStorage("r"));

        if (RelayManager.relayManager.count() <= 0) {
            RelayManager.relayManager.loadDefaults();
            RelayManager.relayManager.save();
        }
        EagRuntime.create();


        IEaglerFilesystem worldsFS = Filesystem.getHandleFor(
                DesktopClientConfigAdapter.instance.getWorldsDB());
        WorldsDB.setWorldsDBProvider(() -> worldsFS);

        net.minecraft.client.GameConfiguration.UserInformation userInfo = new net.minecraft.client.GameConfiguration.UserInformation(
                new net.minecraft.util.Session(EaglerProfile.username, "1", "-", "legacy"),
                new com.mojang.authlib.properties.PropertyMap(),
                new com.mojang.authlib.properties.PropertyMap()
        );

        net.minecraft.client.renderer.ScreenSize displayInfo = new net.minecraft.client.renderer.ScreenSize(854, 480, java.util.OptionalInt.of(1920), java.util.OptionalInt.of(1080), false);

        net.minecraft.client.GameConfiguration.FolderInformation folderInfo = new net.minecraft.client.GameConfiguration.FolderInformation(
                new VFile2("run"),
                new VFile2("resourcepacks"),
                new VFile2("assets"),
                null
        );

        net.minecraft.client.GameConfiguration.GameInformation gameInfo = new net.minecraft.client.GameConfiguration.GameInformation(
                false,
                "1.14.4",
                "release"
        );

        net.minecraft.client.GameConfiguration.ServerInformation serverInfo = new net.minecraft.client.GameConfiguration.ServerInformation(
                null,
                25565
        );

        net.minecraft.client.GameConfiguration gameConfig = new net.minecraft.client.GameConfiguration(userInfo, displayInfo, folderInfo, gameInfo, serverInfo);
        new Minecraft(gameConfig).run();
    }

    private static void getPlatformOptionsFromArgs(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equalsIgnoreCase("fullscreen")) {
                PlatformInput.setStartupFullscreen(true);
            } else if (args[i].equalsIgnoreCase("gles=200")) {
                PlatformRuntime.requestGL(200);
            } else if (args[i].equalsIgnoreCase("gles=300")) {
                PlatformRuntime.requestGL(300);
            } else if (args[i].equalsIgnoreCase("gles=310")) {
                PlatformRuntime.requestGL(310);
            } else if (args[i].equalsIgnoreCase("disableKHRDebug")) {
                PlatformRuntime.requestDisableKHRDebug(true);
            } else {
                EnumPlatformANGLE angle = EnumPlatformANGLE.fromId(args[i]);
                if (angle != EnumPlatformANGLE.DEFAULT) {
                    PlatformRuntime.requestANGLE(angle);
                }
            }
        }
    }

}
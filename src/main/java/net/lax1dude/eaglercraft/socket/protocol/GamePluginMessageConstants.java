package net.lax1dude.eaglercraft.socket.protocol;

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
public class GamePluginMessageConstants {

    public static final String V3_SKIN_CHANNEL = "EAG|Skins-1.8";
    public static final String V3_CAPE_CHANNEL = "EAG|Capes-1.8";
    public static final String V3_VOICE_CHANNEL = "EAG|Voice-1.8";
    public static final String V3_UPDATE_CHANNEL = "EAG|UpdateCert-1.8";
    public static final String V3_FNAW_EN_CHANNEL = "EAG|FNAWSEn-1.8";

    public static final String V4_CHANNEL = "EAG|1.8";
    public static final int CLIENT_TO_SERVER = 0;
    public static final int SERVER_TO_CLIENT = 1;

    public static String fromResourceLocation(String rl) {
        switch (rl) {
            case "legacy:eag_1.8":
                return V4_CHANNEL;
            case "legacy:eag_skins_1.8":
                return V3_SKIN_CHANNEL;
            case "legacy:eag_capes_1.8":
                return V3_CAPE_CHANNEL;
            case "legacy:eag_voice_1.8":
                return V3_VOICE_CHANNEL;
            case "legacy:eag_updatecert_1.8":
                return V3_UPDATE_CHANNEL;
            case "legacy:eag_fnawsen_1.8":
                return V3_FNAW_EN_CHANNEL;
            case "eagler:1-8":
                return V4_CHANNEL;
            case "eagler:skins-1-8":
                return V3_SKIN_CHANNEL;
            case "eagler:capes-1-8":
                return V3_CAPE_CHANNEL;
            case "eagler:voice-1-8":
                return V3_VOICE_CHANNEL;
            case "eagler:updatecrt-1-8":
                return V3_UPDATE_CHANNEL;
            case "eagler:fnawsen-1-8":
                return V3_FNAW_EN_CHANNEL;
            default:
                return rl;
        }
    }

    public static String toResourceLocation(String channel) {
        switch (channel) {
            case V4_CHANNEL:
                return "eagler:1-8";
            case V3_SKIN_CHANNEL:
                return "eagler:skins-1-8";
            case V3_CAPE_CHANNEL:
                return "eagler:capes-1-8";
            case V3_VOICE_CHANNEL:
                return "eagler:voice-1-8";
            case V3_UPDATE_CHANNEL:
                return "eagler:updatecrt-1-8";
            case V3_FNAW_EN_CHANNEL:
                return "eagler:fnawsen-1-8";
            default:
                return channel;
        }
    }

    public static String getDirectionString(int dir) {
        switch (dir) {
            case CLIENT_TO_SERVER:
                return "CLIENT_TO_SERVER";
            case SERVER_TO_CLIENT:
                return "SERVER_TO_CLIENT";
            default:
                return "UNKNOWN";
        }
    }

    public static int oppositeDirection(int dir) {
        switch (dir) {
            case CLIENT_TO_SERVER:
                return SERVER_TO_CLIENT;
            case SERVER_TO_CLIENT:
                return CLIENT_TO_SERVER;
            default:
                throw new IllegalArgumentException("Invalid direction: " + dir);
        }
    }
}

/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.internal;

import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketWebViewMessageV4EAG;

public class PlatformWebView {


    public static boolean supported() {
        return false;
    }

    public static boolean isShowing() {
        return false;
    }

    public static void beginShowing(WebViewOptions options, int x, int y, int w, int h) {

    }

    public static void resize(int x, int y, int w, int h) {

    }

    public static void endShowing() {

    }

    public static boolean fallbackSupported() {
        return true;
    }

    public static void launchFallback(WebViewOptions options) {

    }

    public static boolean fallbackRunning() {
        return false;
    }

    public static String getFallbackURL() {
        return "idk";
    }

    public static void endFallbackServer() {

    }

    public static void handleMessageFromServer(SPacketWebViewMessageV4EAG packet) {

    }


    public static void runTick() {

    }

}
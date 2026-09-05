package net.lax1dude.eaglercraft.sp.server.socket;

import net.lax1dude.eaglercraft.sp.server.EaglerMinecraftServer;
import net.minecraft.network.handshake.IHandshakeNetHandler;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.util.text.ITextComponent;

/**
 * Copyright (c) 2023-2024 lax1dude. All Rights Reserved.
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
public class NetHandlerHandshakeEagler implements IHandshakeNetHandler {

    private final EaglerMinecraftServer mcServer;
    private final IntegratedServerPlayerNetworkManager networkManager;

    public NetHandlerHandshakeEagler(EaglerMinecraftServer parMinecraftServer, IntegratedServerPlayerNetworkManager parNetworkManager) {
        this.mcServer = parMinecraftServer;
        this.networkManager = parNetworkManager;
    }

    @Override
    public void onDisconnect(ITextComponent var1) {

    }

    @Override
    public void processHandshake(CHandshakePacket var1) {
        this.networkManager.setConnectionState(var1.getRequestedState());
        this.networkManager.setNetHandler(new ServerLoginNetHandler(this.mcServer, this.networkManager));
    }

    @Override
    public net.minecraft.network.NetworkManager getNetworkManager() {
        return this.networkManager;
    }

}

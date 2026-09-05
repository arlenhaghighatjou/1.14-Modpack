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

package net.lax1dude.eaglercraft.cookie;

import net.lax1dude.eaglercraft.cookie.ServerCookieDataStore.ServerCookie;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiScreenRevokeSessionToken extends Screen {

    protected final Screen parentScreen;
    private List list;
    private Button inspectButton;
    private Button revokeButton;
    private String selectedCookieName = null;

    public GuiScreenRevokeSessionToken(Screen parent) {
        super(new TranslationTextComponent("revokeSessionToken.title"));
        this.parentScreen = parent;
    }

    @Override
    protected void init() {
        this.addButton(inspectButton = new Button(this.width / 2 - 154, this.height - 38, 100, 20, I18n.format("revokeSessionToken.inspect"), (btn) -> {
            if (selectedCookieName != null) {
                ServerCookie cookie = ServerCookieDataStore.loadCookie(selectedCookieName);
                if (cookie != null) {
                    this.mc.displayGuiScreen(new GuiScreenInspectSessionToken(this, cookie));
                } else {
                    this.init(this.mc, this.width, this.height);
                }
            }
        }));
        this.addButton(revokeButton = new Button(this.width / 2 - 50, this.height - 38, 100, 20, I18n.format("revokeSessionToken.revoke"), (btn) -> {
            if (selectedCookieName != null) {
                ServerCookie cookie = ServerCookieDataStore.loadCookie(selectedCookieName);
                if (cookie != null) {
                    this.mc.displayGuiScreen(new GuiScreenSendRevokeRequest(this, cookie));
                } else {
                    this.init(this.mc, this.width, this.height);
                }
            }
        }));
        this.addButton(new Button(this.width / 2 + 54, this.height - 38, 100, 20, I18n.format("gui.done"), (btn) -> {
            this.mc.displayGuiScreen(parentScreen);
        }));
        this.list = new List(this);
        this.children.add(list);
        updateButtons();
    }

    void updateButtons() {
        inspectButton.active = revokeButton.active = selectedCookieName != null;
    }

    void selectCookie(String name) {
        selectedCookieName = name;
        updateButtons();
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        this.renderBackground();
        this.list.render(mx, my, partialTicks);
        this.drawCenteredString(font, I18n.format("revokeSessionToken.title"), this.width / 2, 16, 16777215);
        this.drawCenteredString(font, I18n.format("revokeSessionToken.note.0"), this.width / 2, this.height - 66, 8421504);
        this.drawCenteredString(font, I18n.format("revokeSessionToken.note.1"), this.width / 2, this.height - 56, 8421504);
        super.render(mx, my, partialTicks);
    }

    public class List extends AbstractList<List.ServerEntry> {

        private final java.util.List<String> cookieNames = new ArrayList<>();

        public List(GuiScreenRevokeSessionToken screen) {
            super(screen.mc, screen.width, screen.height, 32, screen.height - 75 + 4, 18);
            ServerCookieDataStore.flush();
            cookieNames.addAll(ServerCookieDataStore.getRevokableServers());
            Collections.sort(cookieNames);
            for (int i = 0, l = cookieNames.size(); i < l; ++i) {
                addEntry(new ServerEntry(screen, cookieNames.get(i)));
            }
        }

        @Override
        public int getRowWidth() {
            return 220;
        }

        @Override
        protected boolean isSelectedItem(int index) {
            return selectedCookieName != null && selectedCookieName.equals(cookieNames.get(index));
        }

        class ServerEntry extends AbstractList.AbstractListEntry<ServerEntry> {

            private final GuiScreenRevokeSessionToken screen;
            private final String name;

            ServerEntry(GuiScreenRevokeSessionToken screen, String name) {
                this.screen = screen;
                this.name = name;
            }

            @Override
            public void render(int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTicks) {
                screen.drawCenteredString(screen.font, name, screen.width / 2, rowTop + 1, 16777215);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                screen.selectCookie(name);
                return true;
            }
        }
    }
}

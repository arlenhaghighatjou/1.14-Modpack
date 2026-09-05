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
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GuiScreenInspectSessionToken extends Screen {

    private final Screen parent;
    private final ServerCookie cookie;

    public GuiScreenInspectSessionToken(GuiScreenRevokeSessionToken parent, ServerCookie cookie) {
        super(new TranslationTextComponent("inspectSessionToken.title"));
        this.parent = parent;
        this.cookie = cookie;
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 106, 200, 20, I18n.format("gui.done"), (btn) -> {
            this.mc.displayGuiScreen(parent);
        }));
    }

    @Override
    public void render(int mx, int my, float partialTicks) {
        this.renderBackground();
        String[][] toDraw = new String[][]{
                {
                        I18n.format("inspectSessionToken.details.server"),
                        I18n.format("inspectSessionToken.details.expires"),
                        I18n.format("inspectSessionToken.details.length")
                },
                {
                        cookie.server.length() > 32 ? cookie.server.substring(0, 30) + "..." : cookie.server,
                        (new SimpleDateFormat("M/d/yyyy h:mm aa")).format(new Date(cookie.expires)),
                        Integer.toString(cookie.cookie.length)
                }
        };
        int[] maxWidth = new int[2];
        for (int i = 0; i < 2; ++i) {
            String[] strs = toDraw[i];
            int w = 0;
            for (int j = 0; j < strs.length; ++j) {
                int k = font.getStringWidth(strs[j]);
                if (k > w) {
                    w = k;
                }
            }
            maxWidth[i] = w + 10;
        }
        int totalWidth = maxWidth[0] + maxWidth[1];
        this.drawCenteredString(font, I18n.format("inspectSessionToken.title"), this.width / 2, 70, 16777215);
        this.drawString(font, toDraw[0][0], (this.width - totalWidth) / 2, 90, 11184810);
        this.drawString(font, toDraw[0][1], (this.width - totalWidth) / 2, 104, 11184810);
        this.drawString(font, toDraw[0][2], (this.width - totalWidth) / 2, 118, 11184810);
        this.drawString(font, toDraw[1][0], (this.width - totalWidth) / 2 + maxWidth[0], 90, 11184810);
        this.drawString(font, toDraw[1][1], (this.width - totalWidth) / 2 + maxWidth[0], 104, 11184810);
        this.drawString(font, toDraw[1][2], (this.width - totalWidth) / 2 + maxWidth[0], 118, 11184810);
        super.render(mx, my, partialTicks);
    }
}

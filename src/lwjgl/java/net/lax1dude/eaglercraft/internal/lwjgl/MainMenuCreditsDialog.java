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

package net.lax1dude.eaglercraft.internal.lwjgl;

import javax.swing.*;
import java.awt.*;

public class MainMenuCreditsDialog extends JFrame {

    private static final long serialVersionUID = 696969696L;
    private JPanel contentPane;
    private JTextArea textArea;

    /**
     * Create the frame.
     */
    public MainMenuCreditsDialog() {
        setIconImage(Toolkit.getDefaultToolkit().getImage("icon32.png"));
        setTitle("Eaglercraft 1.14.4 Credits");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 850, 700);
        setLocationByPlatform(true);
        setAlwaysOnTop(true);
        contentPane = new JPanel();

        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Font daFont = null;
        for (int i = 0; i < fonts.length; ++i) {
            if (fonts[i].equalsIgnoreCase("consolas")) {
                daFont = new Font(fonts[i], Font.PLAIN, 15);
                break;
            }
        }
        if (daFont == null) {
            daFont = new Font(Font.MONOSPACED, Font.PLAIN, 15);
        }
        textArea.setFont(daFont);
        scrollPane.setViewportView(textArea);
    }

    public void setCreditsText(String str) {
        textArea.setText(str);
    }
}
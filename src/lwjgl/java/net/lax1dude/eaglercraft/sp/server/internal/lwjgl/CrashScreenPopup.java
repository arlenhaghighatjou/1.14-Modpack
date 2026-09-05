package net.lax1dude.eaglercraft.sp.server.internal.lwjgl;

import javax.swing.*;
import java.awt.*;

public class CrashScreenPopup extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextArea txtrTest;

    public CrashScreenPopup() {
        setType(Type.UTILITY);
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage("icon32.png"));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(true);
        setTitle("EaglercraftX Integrated Server");
        setBounds(100, 100, 900, 600);
        setLocationByPlatform(true);
        contentPane = new JPanel();
        contentPane.setBorder(null);

        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        txtrTest = new JTextArea();
        txtrTest.setBackground(new Color(0, 0, 0));
        txtrTest.setForeground(new Color(255, 255, 255));
        txtrTest.setText("test");
        txtrTest.setFont(new Font("Monospaced", Font.BOLD, 18));
        txtrTest.setLineWrap(true);
        txtrTest.setWrapStyleWord(true);
        txtrTest.setEditable(false);
        scrollPane.setViewportView(txtrTest);
    }

    public void setCrashText(String txt) {
        txtrTest.setText(txt);
    }
}

package org.dmgarcia.app;

import com.formdev.flatlaf.FlatLightLaf;
import org.dmgarcia.app.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            new MainFrame().setVisible(true);
        });
    }
}
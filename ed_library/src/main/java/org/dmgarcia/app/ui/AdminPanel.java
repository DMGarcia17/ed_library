package org.dmgarcia.app.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class AdminPanel extends JPanel {
    public AdminPanel(NavigationController nav) {
        setLayout(new MigLayout("insets 16", "[grow]", "[]"));
        add(new JLabel("Admin Dashboard"), "wrap");
        var btnLogout = new JButton("Cerrar Sesión");
        btnLogout.addActionListener(e -> nav.logout());
        add(btnLogout);
    }
}

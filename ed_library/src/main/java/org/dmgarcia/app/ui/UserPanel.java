package org.dmgarcia.app.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class UserPanel extends JPanel {
    public UserPanel(NavigationController nav) {
        setLayout(new MigLayout("insets 16", "[grow]", "[]"));
        add(new JLabel("User Home"), "wrap");
        var btnLogout = new JButton("Cerrar Sesión");
        btnLogout.addActionListener(e -> nav.logout());
        add(btnLogout);
    }
}

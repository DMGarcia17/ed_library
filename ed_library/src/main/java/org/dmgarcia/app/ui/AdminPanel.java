package org.dmgarcia.app.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AdminPanel extends JPanel {

    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    private final Map<String, JComponent> registeredPanels = new HashMap<>();

    public AdminPanel(NavigationController nav) {
        /*setLayout(new MigLayout("insets 16", "[grow]", "[]"));
        add(new JLabel("Admin Dashboard"), "wrap");
        var btnLogout = new JButton("Cerrar Sesión");
        btnLogout.addActionListener(e -> nav.logout());
        add(btnLogout);*/

        setLayout(new BorderLayout());

        JPanel sideMenu = buildSideMenu(nav);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        registerDefaultScreens();

        add(sideMenu, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel buildSideMenu(NavigationController nav) {
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(180,0));
        menu.setBackground(new Color(235,235,235));

        JButton btnUsers = new JButton("Usuarios");
        btnUsers.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUsers.addActionListener(e->showScreen("usuarios"));

        JButton btnRoles = new JButton("Roles");
        btnRoles.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRoles.addActionListener(e->showScreen("roles"));

        JButton btnBooks = new JButton("Libros");
        btnBooks.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBooks.addActionListener(e->showScreen("libros"));

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e->nav.logout());

        menu.add(Box.createVerticalStrut(15));
        menu.add(btnUsers);
        menu.add(Box.createVerticalStrut(15));
        menu.add(btnRoles);
        menu.add(Box.createVerticalStrut(15));
        menu.add(btnBooks);
        menu.add(Box.createVerticalStrut(15));
        menu.add(btnLogout);

        menu.add(Box.createVerticalGlue());

        return menu;
    }

    private void registerDefaultScreens() {
        JPanel usersPanel = buildPlaceholderPanel("Pantalla de administración de usuarios");
        JPanel rolesPanel = buildPlaceholderPanel("Pantalla de administración de roles");
        JPanel booksPanel = buildPlaceholderPanel("Pantalla de administración de libros");

        registerScreen("usuarios", usersPanel);
        registerScreen("roles", rolesPanel);
        registerScreen("libros", booksPanel);

        showScreen("usuarios");
    }

    private void registerScreen(String id, JComponent panel) {
        contentPanel.add(panel, id);
        registeredPanels.put(id, panel);
    }

    private void showScreen(String id) {
        cardLayout.show(contentPanel, id);
    }

    private JPanel buildPlaceholderPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(text, SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }

    public JComponent getRegisteredPanel(String id){
        return registeredPanels.get(id);
    }
}

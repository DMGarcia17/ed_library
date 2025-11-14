package org.dmgarcia.app.ui;

import org.dmgarcia.app.infra.Params;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class UserPanel extends JPanel {
    private final JPanel contentPanel;
    private final CardLayout cardLayout;

    private final Map<String, JComponent> registeredPanels = new HashMap<>();

    public UserPanel(NavigationController nav) {

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
        menu.setPreferredSize(new Dimension(180, 0));
        menu.setBackground(new Color(235, 235, 235));

        JButton btnInfo = new JButton("Modificar mi información");
        btnInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnInfo.addActionListener(e -> showScreen("myInfo", () -> new UserProfilePanel(Params.getUser())));

        JButton btnLoan = new JButton("Prestar libros");
        btnLoan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLoan.addActionListener(e -> showScreen("myLoans", MyLoansPanel::new));

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> nav.logout());


        menu.add(Box.createVerticalStrut(15));
        menu.add(btnLoan);
        menu.add(Box.createVerticalStrut(15));
        menu.add(btnInfo);
        menu.add(Box.createVerticalStrut(15));
        menu.add(btnLogout);

        menu.add(Box.createVerticalGlue());

        return menu;
    }

    private void registerDefaultScreens() {

        registerScreen("myInfo", new UserProfilePanel(Params.getUser()));
        registerScreen("myLoans", new MyLoansPanel());
        registerScreen("default", new JPanel());

        showScreen("default", JPanel::new);
    }

    private void registerScreen(String id, JComponent panel) {
        contentPanel.add(panel, id);
        registeredPanels.put(id, panel);
    }

    private void showScreen(String id, Supplier<JComponent> screenSupplier) {
        if (registeredPanels.containsKey(id)) {
            contentPanel.remove(registeredPanels.get(id));
            registeredPanels.remove(id);
        }

        JComponent newPanel = screenSupplier.get();
        contentPanel.add(newPanel, id);
        registeredPanels.put(id, newPanel);

        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, id);
    }
}

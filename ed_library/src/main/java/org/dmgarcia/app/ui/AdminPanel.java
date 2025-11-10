package org.dmgarcia.app.ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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
        menu.setPreferredSize(new Dimension(180, 0));
        menu.setBackground(new Color(235, 235, 235));

        JButton btnUsers = new JButton("Usuarios");
        btnUsers.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUsers.addActionListener(e -> showScreen("usuarios", UsersAdminPanel::new));

        JButton btnRoles = new JButton("Roles");
        btnRoles.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRoles.addActionListener(e -> showScreen("roles", null));

        JButton btnBooks = new JButton("Libros");
        btnBooks.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBooks.addActionListener(e -> showScreen("libros", BookAdminPanel::new));

        JButton btnCategories = new JButton("Categorías");
        btnCategories.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCategories.addActionListener(e -> showScreen("categorias", CategoryAdminPanel::new));

        JButton btnAuthor = new JButton("Autores");
        btnAuthor.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAuthor.addActionListener(e -> showScreen("autor", AuthorPanel::new));

        JButton btnImport = new JButton("Importar");
        btnImport.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnImport.addActionListener(e -> showScreen("import", BookImportsPanel::new));

        JButton btnLoan = new JButton("loan");
        btnLoan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLoan.addActionListener(e -> showScreen("loan", BookLoanPanel::new));

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> nav.logout());

        menu.add(Box.createVerticalStrut(15));
        menu.add(btnUsers);
        menu.add(Box.createVerticalStrut(15));
        menu.add(btnCategories);
        menu.add(Box.createVerticalStrut(15));
        menu.add(btnBooks);
        menu.add(Box.createVerticalStrut(15));
        menu.add(btnImport);
        menu.add(Box.createVerticalStrut(15));
        menu.add(btnLoan);
        menu.add(Box.createVerticalStrut(15));
        menu.add(btnLogout);

        menu.add(Box.createVerticalGlue());

        return menu;
    }

    private void registerDefaultScreens() {
        JPanel usersPanel = new UsersAdminPanel();
        JPanel rolesPanel = buildPlaceholderPanel("Pantalla de administración de roles");
        JPanel booksPanel = new BookAdminPanel();
        JPanel categoriesPanel = new CategoryAdminPanel();
        JPanel importPanel = new BookImportsPanel();
        JPanel authorPanel = new AuthorPanel();
        JPanel bookLoanPanel = new BookLoanPanel();

        registerScreen("usuarios", usersPanel);
        registerScreen("roles", rolesPanel);
        registerScreen("libros", booksPanel);
        registerScreen("categorias", categoriesPanel);
        registerScreen("autor", authorPanel);
        registerScreen("import", importPanel);
        registerScreen("loan", bookLoanPanel);

        showScreen("usuarios", UsersAdminPanel::new);
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

    private JPanel buildPlaceholderPanel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(text, SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }

    public JComponent getRegisteredPanel(String id) {
        return registeredPanels.get(id);
    }
}

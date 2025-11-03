package org.dmgarcia.app.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public static final String CARD_LOGIN = "login";
    public static final String CARD_ADMIN = "admin";
    public static final String CARD_USER = "user";

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final NavigationController nav = new NavigationController(this, root, cards);

    public MainFrame (){
        super("ED Library");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,650);
        setLocationRelativeTo(null);

        root.add(new LoginPanel(nav), CARD_LOGIN);
        root.add(new AdminPanel(nav), CARD_ADMIN);
        root.add(new UserPanel(nav), CARD_USER);

        setContentPane(root);
        nav.showLogin();
    }
}

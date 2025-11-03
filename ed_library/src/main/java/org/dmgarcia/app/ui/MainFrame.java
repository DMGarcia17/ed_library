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

        // TODO: add panes
    }
}

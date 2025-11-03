package org.dmgarcia.app.ui;

import org.dmgarcia.app.security.SessionContext;
import org.dmgarcia.app.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class NavigationController {

    private final JFrame frame;
    private final JPanel root;
    private final CardLayout cards;
    private final AuthService auth = new AuthService();
    private SessionContext session;

    public NavigationController(JFrame frame, JPanel root, CardLayout cards) {
        this.frame = frame;
        this.root = root;
        this.cards = cards;
    }

    public void showLogin() {
        cards.show(root, MainFrame.CARD_LOGIN);
    }

    public void showAdmin() {
        cards.show(root, MainFrame.CARD_ADMIN);
    }

    public void showUser() {
        cards.show(root, MainFrame.CARD_USER);
    }

    public void attemptLogin(String username, String password) {
        var ctx = auth.login(username, password);
        if (ctx == null) {
            JOptionPane.showMessageDialog(frame, "Invalid credentials");
            return;
        }
        this.session = ctx;
        if (ctx.isAdmin()) showAdmin();
        else showUser();
    }

    public void logout() {
        session = null; showLogin();
    }
}

package org.dmgarcia.app.ui;

import jakarta.persistence.EntityManager;
import net.miginfocom.swing.MigLayout;
import org.dmgarcia.app.infra.JPAUtil;
import org.dmgarcia.app.service.AuthService;

import javax.swing.*;
import java.util.Set;

public class LoginPanel extends JPanel {
    public LoginPanel(NavigationController nav) {
        setLayout(new MigLayout("insets 40, wrap 2", "[right][grow]", ""));
        var lblUser = new JLabel("Nombre de usuario:");
        var txtUser = new JTextField(20);
        var lblPass = new JLabel("Contraseña:");
        var txtPass = new JPasswordField(20);
        var btnLogin = new JButton("Iniciar Sesión");

        EntityManager em= JPAUtil.getEMF().createEntityManager();
        em.close();
        
        add(lblUser);
        add(txtUser);
        add(lblPass);
        add(txtPass);
        add(new JLabel());
        add(btnLogin, "right");

        btnLogin.addActionListener(e ->
                nav.attemptLogin(txtUser.getText().trim(), new String(txtPass.getPassword()))
        );
    }
}

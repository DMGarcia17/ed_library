package org.dmgarcia.app.ui;

import com.toedter.calendar.JDateChooser;
import org.dmgarcia.app.infra.Params;
import org.dmgarcia.app.model.User;
import org.dmgarcia.app.security.UserRepository;
import org.dmgarcia.app.infra.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

public class UserProfilePanel extends JPanel {

    private final UserRepository userRepo;

    private JTextField txtUsername;
    private JTextField txtFirstName;
    private JTextField txtMiddleName;
    private JTextField txtLastName;
    private JTextField txtFamilyName;
    private JDateChooser txtBirthday;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    private User currentUser;

    public UserProfilePanel(User currentUser) {
        this.userRepo = new UserRepository();
        setLayout(new BorderLayout());

        if (currentUser == null) {
            add(new JLabel("No hay usuario en sesión"), BorderLayout.CENTER);
            return;
        }

        this.currentUser = currentUser;

        // ===== datos básicos =====
        JPanel dataPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtUsername = new JTextField(20);
        txtUsername.setEditable(false);

        txtFirstName = new JTextField(20);
        txtMiddleName = new JTextField(20);
        txtLastName = new JTextField(20);
        txtFamilyName = new JTextField(20);
        txtBirthday = new JDateChooser();
        txtBirthday.setDateFormatString("dd/MM/yyyy");// yyyy-MM-dd
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -13);
        txtBirthday.setMaxSelectableDate(calendar.getTime());

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        dataPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dataPanel.add(txtUsername, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        dataPanel.add(new JLabel("Primer nombre:"), gbc);
        gbc.gridx = 1;
        dataPanel.add(txtFirstName, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        dataPanel.add(new JLabel("Segundo nombre:"), gbc);
        gbc.gridx = 1;
        dataPanel.add(txtMiddleName, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        dataPanel.add(new JLabel("Apellido paterno:"), gbc);
        gbc.gridx = 1;
        dataPanel.add(txtLastName, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        dataPanel.add(new JLabel("Apellido materno:"), gbc);
        gbc.gridx = 1;
        dataPanel.add(txtFamilyName, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        dataPanel.add(new JLabel("Fecha de nacimiento (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        dataPanel.add(txtBirthday, gbc);

        add(dataPanel, BorderLayout.NORTH);

        // ===== cambiar contraseña =====
        JPanel passPanel = new JPanel(new GridBagLayout());
        passPanel.setBorder(BorderFactory.createTitledBorder("Cambiar contraseña"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(4, 4, 4, 4);
        gbc2.anchor = GridBagConstraints.WEST;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.weightx = 1;

        txtNewPassword = new JPasswordField(20);
        txtConfirmPassword = new JPasswordField(20);

        int r2 = 0;
        gbc2.gridx = 0;
        gbc2.gridy = r2;
        passPanel.add(new JLabel("Nueva contraseña:"), gbc2);
        gbc2.gridx = 1;
        passPanel.add(txtNewPassword, gbc2);

        r2++;
        gbc2.gridx = 0;
        gbc2.gridy = r2;
        passPanel.add(new JLabel("Confirmar contraseña:"), gbc2);
        gbc2.gridx = 1;
        passPanel.add(txtConfirmPassword, gbc2);

        add(passPanel, BorderLayout.CENTER);

        // ===== botones =====
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar cambios");
        actions.add(btnSave);
        add(actions, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> saveChanges());

        // cargar datos
        loadUserData();
    }

    private void loadUserData() {
        txtUsername.setText(currentUser.getUsername());
        txtFirstName.setText(currentUser.getFirstName());
        txtMiddleName.setText(currentUser.getMiddleName());
        txtLastName.setText(currentUser.getLastName());
        txtFamilyName.setText(currentUser.getFamilyName());
        if (currentUser.getBirthday() != null) {
            txtBirthday.setDate(Date.from(currentUser.getBirthday().atStartOfDay(ZoneId.of("America/El_Salvador")).toInstant()));
        }
    }

    private void saveChanges() {
        // 1. actualizar datos básicos
        currentUser.setFirstName(txtFirstName.getText().trim());
        currentUser.setMiddleName(txtMiddleName.getText().trim());
        currentUser.setLastName(txtLastName.getText().trim());
        currentUser.setFamilyName(txtFamilyName.getText().trim());

        try {
            LocalDate ld = LocalDate.ofInstant(txtBirthday.getDate().toInstant(), ZoneId.of("America/El_Salvador"));
            currentUser.setBirthday(ld);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "La fecha de nacimiento no tiene el formato correcto (yyyy-MM-dd)",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // 2. contraseña (opcional)
        String newPass = new String(txtNewPassword.getPassword()).trim();
        String confirmPass = new String(txtConfirmPassword.getPassword()).trim();

        try {
            // primero guardamos datos básicos
            userRepo.save(currentUser);
            // sincronizamos también el session context
            Params.setUser(currentUser);

            // si hay contraseña ingresada, la validamos
            if (!newPass.isEmpty() || !confirmPass.isEmpty()) {
                if (!newPass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(this,
                            "La contraseña y la confirmación no coinciden",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // aquí hasheas
                String hash = PasswordUtil.hash(newPass);
                userRepo.updatePassword(currentUser.getUsername(), hash);

                // limpiamos campos
                txtNewPassword.setText("");
                txtConfirmPassword.setText("");
            }

            JOptionPane.showMessageDialog(this, "Datos actualizados correctamente.");
        } catch (
                Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "No se pudieron guardar los cambios: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

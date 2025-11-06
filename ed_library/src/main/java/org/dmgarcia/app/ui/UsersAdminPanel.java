package org.dmgarcia.app.ui;

import org.dmgarcia.app.model.User;
import org.dmgarcia.app.security.UserRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UsersAdminPanel extends JPanel {
    private JTable tblUsers;
    private DefaultTableModel usersModel;

    private JTextField txtUsername;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JComboBox<String> cbRole;

    private JButton btnNew;
    private JButton btnSave;
    private JButton btnDelete;
    private JButton btnRefresh;

    public UsersAdminPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        usersModel = new DefaultTableModel(
                new Object[]{"Username", "Nombre Completo", "Email", "Rol"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblUsers = new JTable(usersModel);
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblUsers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedUserToForm();
            }
        });

        refreshTable();
        JScrollPane scroll = new JScrollPane(tblUsers);
        add(scroll, BorderLayout.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del usuario"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(new JLabel("Username:"), gbc);

        txtUsername = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(txtUsername, gbc);

        formPanel.add(new JLabel("Nombre Completo:"), gbc);

        txtFullName = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(txtFullName, gbc);

        formPanel.add(new JLabel("Email:"), gbc);

        txtEmail = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(txtEmail, gbc);

        formPanel.add(new JLabel("Rol:"), gbc);

        cbRole = new JComboBox<>(new String[]{"ADMIN", "USER"});
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(cbRole, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNew = new JButton("Nuevo");
        btnSave = new JButton("Guardar");
        btnDelete = new JButton("Eliminar");
        btnRefresh = new JButton("Refrescar");

        btnNew.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> saveUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnRefresh.addActionListener(e -> refreshTable());

        buttonsPanel.add(btnNew);
        buttonsPanel.add(btnSave);
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnRefresh);

        JPanel south = new JPanel(new BorderLayout());
        south.add(formPanel, BorderLayout.CENTER);
        south.add(buttonsPanel, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);
    }

    private void loadSelectedUserToForm() {
        int row = tblUsers.getSelectedRow();
        if (row == -1) return;

        txtUsername.setText((String) usersModel.getValueAt(row, 0));
        txtFullName.setText((String) usersModel.getValueAt(row, 1));
        txtEmail.setText((String) usersModel.getValueAt(row, 2));
        cbRole.setSelectedItem(usersModel.getValueAt(row, 3));
    }

    private void refreshTable() {
        UserRepository ur= new UserRepository();
        List<User> users = ur.listActive();

        for(User u : users){
            usersModel.addRow(new Object[]{u.getUsername(),
                    u.getFirstName(),
                    u.getPasswordHash(),
                    "u.getRoles().stream().findFirst()"});
        }

    }

    private void deleteUser() {
        int selected = tblUsers.getSelectedRow();

        if(selected==-1) return;

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Estas seguro que deseas eliminar este usuario?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if(resp==JOptionPane.YES_OPTION){
            usersModel.removeRow(selected);
            clearForm();
        }
    }

    private void saveUser() {
        String username = txtUsername.getText();
        String fullname = txtFullName.getText();
        String email = txtEmail.getText();
        String role = (String) cbRole.getSelectedItem();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario es obligatorio");
            return;
        }

        int selected = tblUsers.getSelectedRow();
        if (selected == -1) {
            usersModel.addRow(new Object[]{username, fullname, email, role});
        } else {
            usersModel.setValueAt(username, selected, 0);
            usersModel.setValueAt(fullname, selected, 1);
            usersModel.setValueAt(email, selected, 2);
            usersModel.setValueAt(role, selected, 3);
        }
        clearForm();
    }

    private void clearForm() {
        txtUsername.setText("");
        txtFullName.setText("");
        txtEmail.setText("");
        cbRole.setSelectedIndex(0);
        tblUsers.clearSelection();
    }
}

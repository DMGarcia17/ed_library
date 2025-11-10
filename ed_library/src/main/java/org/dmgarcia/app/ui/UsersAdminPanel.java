package org.dmgarcia.app.ui;

import com.toedter.calendar.JDateChooser;
import org.dmgarcia.app.model.Role;
import org.dmgarcia.app.model.User;
import org.dmgarcia.app.security.UserRepository;
import org.dmgarcia.app.service.AuthService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UsersAdminPanel extends JPanel {
    private JTable tblUsers;
    private DefaultTableModel usersModel;

    private JTextField txtUsername;
    private JTextField txtFirstName;
    private JTextField txtMiddleName;
    private JTextField txtLastName;
    private JTextField txtFamilyName;
    private JTextField txtLpu;

    private JDateChooser dcBirthdate;
    private JComboBox<String> cbRole;

    private JButton btnNew;
    private JButton btnSave;
    private JButton btnDelete;
    private JButton btnRefresh;

    private UserRepository userRep;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public UsersAdminPanel() {
        setLayout(new BorderLayout());
        initComponents();

    }

    private void initComponents() {
        userRep = new UserRepository();
        usersModel = new DefaultTableModel(
                new Object[]{"Username", "Nombre Completo", "Ult. Actualización", "Rol"},
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

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(new JLabel("Primer Nombre:"), gbc);

        txtFirstName = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(txtFirstName, gbc);


        gbc.gridx = 2;
        gbc.gridy = 1;

        formPanel.add(new JLabel("Segundo Nombre:"), gbc);

        txtMiddleName = new JTextField();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(txtMiddleName, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;

        formPanel.add(new JLabel("Primer Apellido:"), gbc);

        txtLastName = new JTextField();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(txtLastName, gbc);


        gbc.gridx = 6;
        gbc.gridy = 1;

        formPanel.add(new JLabel("Segundo Apellido:"), gbc);

        txtFamilyName = new JTextField();
        gbc.gridx = 7;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(txtFamilyName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;

        formPanel.add(new JLabel("Ult. Cambio de Contraseña:"), gbc);

        txtLpu = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(txtLpu, gbc);
        txtLpu.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = 3;

        formPanel.add(new JLabel("Rol:"), gbc);

        cbRole = new JComboBox<>(new String[]{"ADMIN", "USER"});
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(cbRole, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;

        formPanel.add(new JLabel("Fecha de Nacimiento:"), gbc);
        dcBirthdate = new JDateChooser();
        dcBirthdate.setDateFormatString("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -13);
        dcBirthdate.setMaxSelectableDate(calendar.getTime());
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        formPanel.add(dcBirthdate, gbc);

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

        Optional<User> u = userRep.findActiveWithRoles((String) usersModel.getValueAt(row, 0));
        if (u.isPresent()){
            User user = u.get();
            txtUsername.setText(user.getUsername());
            txtFirstName.setText(user.getFirstName());
            txtMiddleName.setText(user.getMiddleName());
            txtLastName.setText(user.getLastName());
            txtFamilyName.setText(user.getFamilyName());
            txtLpu.setText(user.getLastUpdatePassword().format(dtf));
            cbRole.setSelectedItem(((Role)user.getRoles().stream().findFirst().get()).getCode().toUpperCase());
            dcBirthdate.setDate(Date.from(user.getBirthday().atStartOfDay(ZoneId.of("America/El_Salvador")).toInstant()));
        }

        txtUsername.setEnabled(false);
    }

    private void refreshTable() {
        UserRepository ur= new UserRepository();
        List<User> users = ur.findAllWithRoles();

        usersModel.setRowCount(0);

        for(User u : users){
            Optional<Role> found = Optional.empty();
            for (Role role : u.getRoles()) {
                found = Optional.of(role);
                break;
            }
            usersModel.addRow(new Object[]{u.getUsername(),
                    u.getFirstName(),
                    u.getLastUpdatePassword().format(dtf),
                    ((Role) found.get()).getCode()});
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
            userRep.softDelete(usersModel.getValueAt(selected, 0).toString());
            usersModel.removeRow(selected);
            clearForm();
        }
    }

    private void saveUser() {
        String username = txtUsername.getText();
        String firstName = txtFirstName.getText();
        String middleName = txtMiddleName.getText();
        String lastName = txtLastName.getText();
        String familyName = txtFamilyName.getText();
        String email = txtLpu.getText();
        String role = (String) cbRole.getSelectedItem();
        LocalDate birthdate = LocalDate.ofInstant(dcBirthdate.getDate().toInstant(), ZoneId.of("America/El_Salvador"));

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario es obligatorio");
            return;
        }

        int selected = tblUsers.getSelectedRow();

        UserRepository ur = new UserRepository();
        Optional<User> u = ur.findActiveByUsername(txtUsername.getText());
        User user;
        if (txtUsername.isEnabled() && u.isPresent()){
            JOptionPane.showMessageDialog(this, "El nombre de usuario ya existe, por favor ingrese uno nuevo");
            return;
        }else if(u.isPresent()){
            user = u.get();
            user.setFirstName(firstName);
            user.setMiddleName(middleName);
            user.setLastName(lastName);
            user.setFamilyName(familyName);
            user.setBirthday(birthdate);

            userRep.save(user);
        } else{
            AuthService auth = new AuthService();
            user = auth.createUser(username, "Prueba123", firstName, middleName, lastName, familyName, Set.of(((String) cbRole.getSelectedItem()).toUpperCase()), birthdate);
        }

        if (selected == -1) {
            usersModel.addRow(new Object[]{username, firstName, email, role});
        } else {
            usersModel.setValueAt(username, selected, 0);
            usersModel.setValueAt(firstName, selected, 1);
            usersModel.setValueAt(email, selected, 2);
            usersModel.setValueAt(role, selected, 3);
        }
        clearForm();
    }

    private void clearForm() {
        txtUsername.setText("");
        txtUsername.setEnabled(true);
        txtFirstName.setText("");
        txtLpu.setText("");
        cbRole.setSelectedIndex(0);
        tblUsers.clearSelection();
        dcBirthdate.setDate(null);
    }
}

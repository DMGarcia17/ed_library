package org.dmgarcia.app.ui;

import org.dmgarcia.app.model.User;
import org.dmgarcia.app.security.UserRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class SelectUserDialog extends JDialog {

    private final UserRepository userRepo;
    private final DefaultTableModel model;
    private final JTable table;
    private User selectedUser;

    public SelectUserDialog(Window owner) {
        super(owner, "Seleccionar usuario", ModalityType.APPLICATION_MODAL);
        this.userRepo = new UserRepository();

        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Buscar");
        top.add(new JLabel("Buscar:"));
        top.add(txtSearch);
        top.add(btnSearch);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Username", "Nombre completo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Seleccionar");
        JButton btnCancel = new JButton("Cancelar");
        bottom.add(btnOk);
        bottom.add(btnCancel);
        add(bottom, BorderLayout.SOUTH);

        // listeners
        btnSearch.addActionListener(e -> loadUsers(txtSearch.getText().trim()));
        btnOk.addActionListener(e -> selectAndClose());
        btnCancel.addActionListener(e -> {
            selectedUser = null;
            dispose();
        });
        // doble click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) selectAndClose();
            }
        });

        loadUsers(""); // carga inicial

        setSize(450, 350);
        setLocationRelativeTo(owner);
    }

    private void loadUsers(String filter) {
        model.setRowCount(0);
        List<User> users;
        if (filter == null || filter.isEmpty()) {
            users = userRepo.listActive(); // usa tu método
        } else {
            users = userRepo.searchByUsernameOrName(filter); // o haz otro método
        }

        for (User u : users) {
            model.addRow(new Object[]{
                    u.getUsername(),
                    u.getFullName() // o getName(), según tu entity
            });
        }
    }

    private void selectAndClose() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario");
            return;
        }
        String username = (String) model.getValueAt(row, 0);
        Optional<User> op =userRepo.findActiveByUsername(username);
        op.ifPresent(user -> selectedUser=user);  // o findByUsername
        dispose();
    }

    public User getSelectedUser() {
        return selectedUser;
    }
}

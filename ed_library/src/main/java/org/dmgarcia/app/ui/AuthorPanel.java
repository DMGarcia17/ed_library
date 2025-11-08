package org.dmgarcia.app.ui;

import org.dmgarcia.app.model.Author;
import org.dmgarcia.app.model.Category;
import org.dmgarcia.app.security.AuthorRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AuthorPanel extends JPanel {
    private JTable tblAuthors;
    private DefaultTableModel authorModel;
    private AuthorRepository authorRep;

    private JTextField txtIdAuthor;
    private JTextField txtAuthor;
    private JTextField txtNationality;
    private JTextArea txtBiography;

    private JButton btnNew;
    private JButton btnSave;
    private JButton btnDelete;
    private JButton btnRefresh;


    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public AuthorPanel() {
        setLayout(new BorderLayout());
        initComponents();

    }

    private void initComponents() {
        authorRep = new AuthorRepository();
        authorModel = new DefaultTableModel(
                new Object[]{"ID", "Autor", "Nacionalidad"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblAuthors = new JTable(authorModel);
        tblAuthors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblAuthors.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCategoryToForm();
            }
        });

        refreshTable();
        JScrollPane scroll = new JScrollPane(tblAuthors);
        add(scroll, BorderLayout.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos de autores"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(new JLabel("ID:"), gbc);

        txtIdAuthor = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(txtIdAuthor, gbc);
        txtIdAuthor.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(new JLabel("Nombre:"), gbc);

        txtAuthor = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(txtAuthor, gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;

        formPanel.add(new JLabel("Nacionalidad:"), gbc);

        txtNationality = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(txtNationality, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;

        formPanel.add(new JLabel("Biografia"), gbc);

        txtBiography = new JTextArea();
        txtBiography.setLineWrap(true);
        txtBiography.setWrapStyleWord(true);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(txtBiography, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNew = new JButton("Nuevo");
        btnSave = new JButton("Guardar");
        btnDelete = new JButton("Eliminar");
        btnRefresh = new JButton("Refrescar");

        btnNew.addActionListener(e -> clearForm());
        btnSave.addActionListener(e -> save());
        btnDelete.addActionListener(e -> delete());
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

    private void refreshTable() {
        List<Author> categories = authorRep.listActive();

        authorModel.setRowCount(0);

        for(Author u : categories){
            authorModel.addRow(new Object[]{u.getIdAuthor(),
                    u.getAuthor(),
                    u.getNationality()});
        }

    }

    private void loadSelectedCategoryToForm() {
        int row = tblAuthors.getSelectedRow();
        if (row == -1) return;

        Optional<Author> c = authorRep.find((Integer) authorModel.getValueAt(row, 0));
        if (c.isPresent()){
            Author author = c.get();
            txtIdAuthor.setText(String.valueOf(author.getIdAuthor()));
            txtAuthor.setText(author.getAuthor());
            txtNationality.setText(author.getNationality());
            txtBiography.setText(author.getBiography());
        }
    }

    private void delete() {
        int selected = tblAuthors.getSelectedRow();

        if(selected==-1) return;

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Estas seguro que deseas eliminar este autor?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if(resp==JOptionPane.YES_OPTION){
            authorRep.softDelete(authorModel.getValueAt(selected, 0).toString());
            authorModel.removeRow(selected);
            clearForm();
        }
    }

    private void save() {
        String id = txtIdAuthor.getText();
        String author = txtAuthor.getText();
        String nationality = txtNationality.getText();
        String biography = txtBiography.getText();

        int selected = tblAuthors.getSelectedRow();

        AuthorRepository ar = new AuthorRepository();
        Author a;
        if(!txtIdAuthor.getText().isEmpty()){
            a = ar.find(Integer.valueOf(txtIdAuthor.getText())).get();
            a.setAuthor(author);
            a.setNationality(nationality);
            a.setBiography(biography);

            authorRep.save(a);
        } else{
            if(txtAuthor.getText().isEmpty() || txtNationality.getText().isEmpty() || txtBiography.getText().isEmpty()){
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos del formulario.");
                return;
            }
            a= new Author(author, nationality, biography);
            authorRep.save(a);
        }

        if (selected == -1) {
            authorModel.addRow(new Object[]{id, author, nationality});
        } else {
            authorModel.setValueAt(id, selected, 0);
            authorModel.setValueAt(author, selected, 1);
            authorModel.setValueAt(nationality, selected, 2);
        }
        clearForm();
    }

    private void clearForm() {
        txtIdAuthor.setText("");
        txtAuthor.setText("");
        txtNationality.setText("");
        txtBiography.setText("");
        tblAuthors.clearSelection();
        refreshTable();
    }
}

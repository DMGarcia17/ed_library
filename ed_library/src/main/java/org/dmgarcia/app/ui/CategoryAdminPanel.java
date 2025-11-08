package org.dmgarcia.app.ui;

import org.dmgarcia.app.model.Category;
import org.dmgarcia.app.model.Role;
import org.dmgarcia.app.security.CategoryRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class CategoryAdminPanel extends JPanel {
    private JTable tblCategory;
    private DefaultTableModel categoryModel;
    private CategoryRepository catRep;

    private JTextField txtIdCategory;
    private JTextField txtCode;
    private JTextField txtCategoryName;

    private JComboBox<Category> cbParent;

    private JButton btnNew;
    private JButton btnSave;
    private JButton btnDelete;
    private JButton btnRefresh;


    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public CategoryAdminPanel() {
        setLayout(new BorderLayout());
        initComponents();

    }

    private void initComponents() {
        catRep = new CategoryRepository();
        categoryModel = new DefaultTableModel(
                new Object[]{"ID", "Code", "Categoría", "Padre"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCategory = new JTable(categoryModel);
        tblCategory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblCategory.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCategoryToForm();
            }
        });

        refreshTable();
        JScrollPane scroll = new JScrollPane(tblCategory);
        add(scroll, BorderLayout.CENTER);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos de categoría"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(new JLabel("ID:"), gbc);

        txtIdCategory = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(txtIdCategory, gbc);
        txtIdCategory.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(new JLabel("Código Categoría:"), gbc);

        txtCode = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(txtCode, gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;

        formPanel.add(new JLabel("Nombre:"), gbc);

        txtCategoryName = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(txtCategoryName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;

        formPanel.add(new JLabel("Rol:"), gbc);

        cbParent = new JComboBox<>();
        cbParent.setRenderer(new CategoryComboRenderer());
        loadRootCategories();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(cbParent, gbc);

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

    private void loadRootCategories() {
        cbParent.removeAllItems();

        cbParent.addItem(null);

        List<Category> roots = catRep.findRootCategories();

        for (Category c : roots) {
            cbParent.addItem(c);
        }
    }

    private void loadSelectedCategoryToForm() {
        int row = tblCategory.getSelectedRow();
        if (row == -1) return;

        Optional<Category> c = catRep.find((Integer) categoryModel.getValueAt(row, 0));
        if (c.isPresent()){
            Category category = c.get();
            txtIdCategory.setText(String.valueOf(category.getId()));
            txtCode.setText(category.getCode());
            txtCategoryName.setText(category.getName());
            //cbParent.setSelectedItem(((Category) category.getParent()));
            Category parent = category.getParent();
            if (parent == null) {
                cbParent.setSelectedItem(null); // "— Sin padre —"
            } else {
                selectParentInCombo(parent.getId());
            }
        }

        txtIdCategory.setEnabled(false);
    }

    private void refreshTable() {
        List<Category> categories = catRep.listActive();

        categoryModel.setRowCount(0);

        for(Category u : categories){
            categoryModel.addRow(new Object[]{u.getId(),
                    u.getCode(),
                    u.getName(),
                    ((u.getParent() != null)?u.getParent().getName():null)});
        }

    }

    private void delete() {
        int selected = tblCategory.getSelectedRow();

        if(selected==-1) return;

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Estas seguro que deseas eliminar esta categoría?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if(resp==JOptionPane.YES_OPTION){
            catRep.softDelete(categoryModel.getValueAt(selected, 0).toString());
            categoryModel.removeRow(selected);
            clearForm();
        }
    }

    private void save() {
        String id = txtIdCategory.getText();
        String code = txtCode.getText();
        String name = txtCategoryName.getText();
        Category cat = (Category) cbParent.getSelectedItem();

        int selected = tblCategory.getSelectedRow();

        CategoryRepository cr = new CategoryRepository();
//        Optional<Category> u = cr.find(Integer.valueOf(txtIdCategory.getText()));
        Category category;
        if(!txtIdCategory.getText().isEmpty()){
            category = cr.find(Integer.valueOf(txtIdCategory.getText())).get();
            category.setCode(code);
            category.setName(name);
            category.setParent(cat);

            catRep.save(category);
        } else{
            category= new Category(code, name, cat);
            catRep.save(category);
        }

        if (selected == -1) {
            categoryModel.addRow(new Object[]{id, code, cat});
        } else {
            categoryModel.setValueAt(id, selected, 0);
            categoryModel.setValueAt(code, selected, 1);
            categoryModel.setValueAt(cat, selected, 2);
        }
        clearForm();
    }

    private void clearForm() {
        txtIdCategory.setText("");
        txtCode.setText("");
        txtCategoryName.setText("");
        cbParent.setSelectedIndex(0);
        tblCategory.clearSelection();
        refreshTable();
    }

    private void selectParentInCombo(Integer parentId) {
        ComboBoxModel<Category> model = cbParent.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Category item = model.getElementAt(i);
            if (item != null && item.getId().equals(parentId)) {
                cbParent.setSelectedIndex(i);
                return;
            }
        }
        // si no lo encontró, lo dejamos en "sin padre"
        cbParent.setSelectedItem(null);
    }

    private static class CategoryComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value == null) {
                setText("— Sin padre —");
            } else if (value instanceof Category c) {
                setText(c.getCode() + " - " + c.getName());
            }
            return this;
        }
    }
}

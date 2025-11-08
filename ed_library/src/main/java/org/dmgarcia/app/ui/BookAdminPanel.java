package org.dmgarcia.app.ui;

import org.dmgarcia.app.model.Author;
import org.dmgarcia.app.model.Book;
import org.dmgarcia.app.model.Category;
import org.dmgarcia.app.security.AuthorRepository;
import org.dmgarcia.app.security.BookRepository;
import org.dmgarcia.app.security.CategoryRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class BookAdminPanel extends JPanel {
    private JTable tblBook;
    private DefaultTableModel bookModel;
    private BookRepository bookRep;

    private JTextField txtIdBook;
    private JTextField txtTitle;
    private JTextArea txtSynopsis;
    private JTextField txtIsbn;

    private JComboBox<Author> cbAuthor;
    private JComboBox<Category> cbCategory;

    private JButton btnNew;
    private JButton btnSave;
    private JButton btnDelete;
    private JButton btnRefresh;


    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public BookAdminPanel() {
        setLayout(new BorderLayout());
        initComponents();

    }

    private void initComponents() {
        bookRep = new BookRepository();
        bookModel = new DefaultTableModel(
                new Object[]{"ID", "Título", "ISBN", "Categoría", "Autor"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblBook = new JTable(bookModel);
        tblBook.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblBook.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedBookToForm();
            }
        });

        refreshTable();
        JScrollPane scroll = new JScrollPane(tblBook);
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

        txtIdBook = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(txtIdBook, gbc);
        txtIdBook.setEnabled(false);

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(new JLabel("Título:"), gbc);

        txtTitle = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(txtTitle, gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;

        formPanel.add(new JLabel("Sinopsis:"), gbc);

        txtSynopsis = new JTextArea();
        txtSynopsis.setLineWrap(true);
        txtSynopsis.setWrapStyleWord(true);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(txtSynopsis, gbc);


        gbc.gridx = 0;
        gbc.gridy = 3;

        formPanel.add(new JLabel("ISBN:"), gbc);

        txtIsbn = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(txtIsbn, gbc);


        gbc.gridx = 0;
        gbc.gridy = 4;

        formPanel.add(new JLabel("Categoría:"), gbc);

        cbCategory = new JComboBox<>();
        cbCategory.setRenderer(new CategoryComboRenderer());
        loadRootCategories();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        formPanel.add(cbCategory, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;

        formPanel.add(new JLabel("Autor:"), gbc);

        cbAuthor = new JComboBox<>();
        cbAuthor.setRenderer(new AuthorComboRenderer());
        loadAuthors();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        formPanel.add(cbAuthor, gbc);

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

    private void loadAuthors() {
        cbAuthor.removeAllItems();

        cbAuthor.addItem(null);

        List<Author> roots = new AuthorRepository().listActive();

        for (Author a : roots) {
            cbAuthor.addItem(a);
        }
    }

    private void loadRootCategories() {
        cbCategory.removeAllItems();

        cbCategory.addItem(null);

        List<Category> roots = new CategoryRepository().listActive();

        for (Category c : roots) {
            cbCategory.addItem(c);
        }
    }

    private void loadSelectedBookToForm() {
        int row = tblBook.getSelectedRow();
        if (row == -1) return;

        Optional<Book> c = bookRep.find((Integer) bookModel.getValueAt(row, 0));
        if (c.isPresent()){
            Book book = c.get();
            txtIdBook.setText(String.valueOf(book.getIdBook()));
            txtTitle.setText(book.getTitle());
            txtSynopsis.setText(book.getSynopsis());
            txtIsbn.setText(book.getIsbn());
            //cbParent.setSelectedItem(((Category) category.getParent()));
            Category category = book.getIdCategory();
            if (category == null) {
                cbCategory.setSelectedItem(null); // "— Sin padre —"
            } else {
                selectParentInCombo(category.getId(), category);
            }
            Author author = book.getIdAuthor();
            if (category == null) {
                cbAuthor.setSelectedItem(null); // "— Sin padre —"
            } else {
                selectParentInCombo(author.getIdAuthor(), author);
            }
        }

        txtIdBook.setEnabled(false);
    }

    private void refreshTable() {
        List<Book> books = bookRep.listActive();

        bookModel.setRowCount(0);

        for(Book u : books){
            bookModel.addRow(new Object[]{u.getIdBook(),
                    u.getTitle(),
                    u.getIsbn(),
                    u.getIdCategory().getName(),
                    u.getIdAuthor().getAuthor()});
        }

    }

    private void delete() {
        int selected = tblBook.getSelectedRow();

        if(selected==-1) return;

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Estas seguro que deseas eliminar esta categoría?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if(resp==JOptionPane.YES_OPTION){
            bookRep.softDelete(bookModel.getValueAt(selected, 0).toString());
            bookModel.removeRow(selected);
            clearForm();
        }
    }

    private void save() {
        String id = txtIdBook.getText();
        String title = txtTitle.getText();
        String synopsis = txtSynopsis.getText();
        String isbn = txtIsbn.getText();
        Category cat = (Category) cbCategory.getSelectedItem();
        Author author = (Author) cbAuthor.getSelectedItem();

        int selected = tblBook.getSelectedRow();

        BookRepository br = new BookRepository();

        Book book;
        if(!txtIdBook.getText().isEmpty()){
            book = br.find(Integer.valueOf(txtIdBook.getText())).get();
            book.setTitle(title);
            book.setSynopsis(synopsis);
            book.setIsbn(isbn);
            book.setIdCategory(cat);
            book.setIdAuthor(author);

            bookRep.save(book);
        } else{
            book= new Book(title, synopsis, isbn, cat, author);
            bookRep.save(book);
        }

        if (selected == -1) {
            bookModel.addRow(new Object[]{id, title, cat});
        } else {
            bookModel.setValueAt(id, selected, 0);
            bookModel.setValueAt(title, selected, 1);
            bookModel.setValueAt(cat, selected, 2);
        }
        clearForm();
    }

    private void clearForm() {
        txtIdBook.setText("");
        txtTitle.setText("");
        txtIsbn.setText("");
        txtSynopsis.setText("");
        cbAuthor.setSelectedIndex(0);
        cbCategory.setSelectedIndex(0);
        tblBook.clearSelection();
        refreshTable();
    }

    private void selectParentInCombo(Integer id, Author c) {
        ComboBoxModel<Author> model = cbAuthor.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Author item = model.getElementAt(i);
            if (item != null && item.getIdAuthor().equals(id)) {
                cbAuthor.setSelectedIndex(i);
                return;
            }
        }
        cbAuthor.setSelectedItem(null);
    }

    private void selectParentInCombo(Integer id, Category c) {
        ComboBoxModel<Category> model = cbCategory.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Category item = model.getElementAt(i);
            if (item != null && item.getId().equals(id)) {
                cbCategory.setSelectedIndex(i);
                return;
            }
        }
        cbCategory.setSelectedItem(null);
    }

    private static class CategoryComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value == null) {
                setText("— Sin Categoría —");
            } else if (value instanceof Category c) {
                setText(c.getCode() + " - " + c.getName());
            }
            return this;
        }
    }

    private static class AuthorComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value == null) {
                setText("— Sin Author —");
            } else if (value instanceof Author a) {
                setText(a.getIdAuthor() + " - " + a.getAuthor());
            }
            return this;
        }
    }
}

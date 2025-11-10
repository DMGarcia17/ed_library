package org.dmgarcia.app.ui;

import org.dmgarcia.app.model.Book;
import org.dmgarcia.app.security.BookRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class SelectBookDialog extends JDialog {

    private final BookRepository bookRepo;
    private final DefaultTableModel model;
    private final JTable table;
    private Book selectedBook;

    public SelectBookDialog(Window owner) {
        super(owner, "Seleccionar libro", ModalityType.APPLICATION_MODAL);
        this.bookRepo = new BookRepository();

        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Buscar");
        top.add(new JLabel("Título:"));
        top.add(txtSearch);
        top.add(btnSearch);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"ID", "Título", "Autor"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
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

        btnSearch.addActionListener(e -> loadBooks(txtSearch.getText().trim()));
        btnOk.addActionListener(e -> selectAndClose());
        btnCancel.addActionListener(e -> {
            selectedBook = null;
            dispose();
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) selectAndClose();
            }
        });

        loadBooks("");

        setSize(500, 350);
        setLocationRelativeTo(owner);
    }

    private void loadBooks(String filter) {
        model.setRowCount(0);
        List<Book> books;
        if (filter == null || filter.isEmpty()) {
            books = bookRepo.listActive();
        } else {
            books = bookRepo.findByTitle(filter);
        }

        for (Book b : books) {
            model.addRow(new Object[]{
                    b.getIdBook(),
                    b.getTitle(),
                    b.getIdAuthor() != null ? b.getIdAuthor().getAuthor() : ""
            });
        }
    }

    private void selectAndClose() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro");
            return;
        }
        Integer idBook = (Integer) model.getValueAt(row, 0);
        Optional<Book> ob =bookRepo.find(idBook);
        ob.ifPresent(book -> selectedBook = book);
        dispose();
    }

    public Book getSelectedBook() {
        return selectedBook;
    }
}

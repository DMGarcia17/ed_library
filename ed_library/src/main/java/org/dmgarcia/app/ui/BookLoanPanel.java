package org.dmgarcia.app.ui;

import org.dmgarcia.app.model.BookLoan;
import org.dmgarcia.app.security.BookLoanRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookLoanPanel extends JPanel {

    private final BookLoanRepository loanRepo;
    private final DefaultTableModel loanModel;
    private final JTable tblLoans;

    private final JComboBox<String> cbStatus;   // Pendientes, Entregados, Todos
    private final JComboBox<String> cbFilterBy; // Ninguno, Usuario, Libro
    private final JTextField txtFilter;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BookLoanPanel() {
        this.loanRepo = new BookLoanRepository();
        setLayout(new BorderLayout());

        // TOP: filtros
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbStatus = new JComboBox<>(new String[]{"Pendientes", "Entregados", "Todos"});
        cbFilterBy = new JComboBox<>(new String[]{"Sin filtro", "Por usuario", "Por libro"});
        txtFilter = new JTextField(15);

        JButton btnApplyFilter = new JButton("Filtrar");

        filters.add(new JLabel("Estado:"));
        filters.add(cbStatus);
        filters.add(new JLabel("Filtro:"));
        filters.add(cbFilterBy);
        filters.add(txtFilter);
        filters.add(btnApplyFilter);

        add(filters, BorderLayout.NORTH);

        // CENTER: tabla
        loanModel = new DefaultTableModel(
                new Object[]{"ID", "Libro", "Usuario", "Fecha préstamo", "Fecha devolución", "Devuelto"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblLoans = new JTable(loanModel);
        tblLoans.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tblLoans), BorderLayout.CENTER);

        // BOTTOM: botones
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNew = new JButton("Nuevo préstamo");
        JButton btnReturn = new JButton("Marcar devuelto");
        JButton btnCancel = new JButton("Cancelar préstamo");
        JButton btnRefresh = new JButton("Refrescar");

        actions.add(btnNew);
        actions.add(btnReturn);
        actions.add(btnCancel);
        actions.add(btnRefresh);

        add(actions, BorderLayout.SOUTH);

        // listeners
        btnApplyFilter.addActionListener(e -> loadData());
        btnRefresh.addActionListener(e -> loadData());
        btnReturn.addActionListener(e -> markSelectedReturned());
        btnCancel.addActionListener(e -> cancelSelected());
        btnNew.addActionListener(e -> createNewLoan());

        // carga inicial
        loadData();
    }

    private void loadData() {
        String status = (String) cbStatus.getSelectedItem();
        String filterBy = (String) cbFilterBy.getSelectedItem();
        String value = txtFilter.getText().trim();

        List<BookLoan> loans;

        // primero por estado
        if ("Pendientes".equals(status)) {
            loans = loanRepo.findActive();
        } else if ("Entregados".equals(status)) {
            loans = loanRepo.findReturned();
        } else {
            loans = loanRepo.findAll();
        }

        // luego filtramos por usuario/libro si se pidió
        if (!"Sin filtro".equals(filterBy) && !value.isEmpty()) {
            if ("Por usuario".equals(filterBy)) {
                loans = loanRepo.findByUser(value);
            } else if ("Por libro".equals(filterBy)) {
                loans = loanRepo.findByBookTitle(value);
            }
        }

        fillTable(loans);
    }

    private void fillTable(List<BookLoan> loans) {
        loanModel.setRowCount(0);
        for (BookLoan bl : loans) {
            String status;

            if(bl.isCanceled()){
                status = "Cancelado";
            } else {
                if(bl.isReturned()){
                    status = "Si";
                }else {
                    status = "No";
                }
            }

            loanModel.addRow(new Object[]{
                    bl.getIdLoan(),
                    bl.getBook() != null ? bl.getBook().getTitle() : "",
                    bl.getUser() != null ? bl.getUser().getFullName() : bl.getUser().getUsername(),
                    bl.getLoanDate() != null ? fmt.format(bl.getLoanDate()) : "",
                    bl.getReturnDate() != null ? fmt.format(bl.getReturnDate()) : "",
                    status
            });
        }
    }

    private Integer getSelectedLoanId() {
        int row = tblLoans.getSelectedRow();
        if (row == -1) return null;
        return (Integer) loanModel.getValueAt(row, 0);
    }

    private void markSelectedReturned() {
        Integer id = getSelectedLoanId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un préstamo primero");
            return;
        }
        loanRepo.markReturned(id);
        loadData();
    }

    private void cancelSelected() {
        Integer id = getSelectedLoanId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un préstamo primero");
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this,
                "¿Seguro que deseas cancelar este préstamo?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            loanRepo.cancel(id);
            loadData();
        }
    }

    private void createNewLoan() {
        Window parent = SwingUtilities.getWindowAncestor(this);

        SelectUserDialog userDlg = new SelectUserDialog(parent);
        userDlg.setVisible(true);
        var user = userDlg.getSelectedUser();
        if (user == null) {
            return;
        }

        SelectBookDialog bookDlg = new SelectBookDialog(parent);
        bookDlg.setVisible(true);
        var book = bookDlg.getSelectedBook();
        if (book == null) {
            return;
        }

        SelectLoanRangeDialog loanRange = new SelectLoanRangeDialog(parent);
        loanRange.setVisible(true);
        var localLoan = loanRange.getLocalLoan();
        var returned = loanRange.getReturned();
        if (localLoan == null) {
            return;
        }

        BookLoan loan = new BookLoan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setLocalLoan(localLoan);
        loan.setReturnDate(returned);
        loan.setReturned(false);

        try {
            loanRepo.save(loan);
            JOptionPane.showMessageDialog(this, "Préstamo registrado.");
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo guardar el préstamo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

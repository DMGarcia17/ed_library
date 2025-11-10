package org.dmgarcia.app.ui;

import org.dmgarcia.app.model.Book;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class SelectLoanRangeDialog extends JDialog {

    private LocalDate localLoan;
    private LocalDate returned;

    public SelectLoanRangeDialog(Window owner) {
        super(owner, "Seleccionar fechas de retorno", ModalityType.APPLICATION_MODAL);

        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(15);
        JButton btnLocal = new JButton("¿Es un prestamó local?");
        JButton btnReturned = new JButton("¿Sacará el libro?");
        top.add(btnLocal);
        top.add(btnReturned);
        add(top, BorderLayout.NORTH);

        btnLocal.addActionListener(e -> {
            localLoan = LocalDate.now();
            returned = LocalDate.now();
            dispose();
        });

        btnReturned.addActionListener(e -> {
            localLoan = LocalDate.now().plusDays(7L);
            returned = LocalDate.now().plusDays(7L);
            dispose();
        });

        setSize(500, 350);
        setLocationRelativeTo(owner);
    }
    public LocalDate getLocalLoan() {
        return localLoan;
    }
    public LocalDate getReturned() {
        return returned;
    }
}

package org.dmgarcia.app.ui;

import org.dmgarcia.app.model.*;
import org.dmgarcia.app.security.AuthorRepository;
import org.dmgarcia.app.security.BookRepository;
import org.dmgarcia.app.security.CategoryRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookImportsPanel extends JPanel {

    private BookRepository bookRepo;
    private CategoryRepository categoryRepo;
    private AuthorRepository authorRepo;

    public BookImportsPanel(){
        setLayout(new BorderLayout());
        initComponents();
        bookRepo = new BookRepository();
        categoryRepo = new CategoryRepository();
        authorRepo = new AuthorRepository();
    }

    private void initComponents() {
        JPanel jp = new JPanel();
        jp.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets= new Insets(4,4,4,4);
        gbc.fill=GridBagConstraints.HORIZONTAL;
        JButton btnImport = new JButton("Importar");
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnImportActionPerformed(e);
            }
        });

        gbc.gridx=0;
        gbc.gridy=0;

        jp.add(new JLabel("Seleccione el archivo que se desea cargar."), gbc);

        gbc.gridx=0;
        gbc.gridy=1;

        jp.add(new JLabel("El archivo debe ser en formato CSV con la estructura:"), gbc);
        gbc.gridx=0;
        gbc.gridy=2;

        jp.add(new JLabel("title ; author ; nationality ; biography ; category_code ; isbn ; synopsis"), gbc);


        gbc.gridx=0;
        gbc.gridy=3;

        jp.add(btnImport, gbc);

        add(jp, BorderLayout.CENTER);
    }

    private void btnImportActionPerformed(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file)) {
                List<ImportError> errors = importBooks(fis);

                if (errors.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Importación completada sin errores.");
                } else {
                    JTable tblErrors = new JTable(new ImportErrorTableModel(errors));
                    JScrollPane sp = new JScrollPane(tblErrors);
                    sp.setPreferredSize(new Dimension(800, 300));
                    JOptionPane.showMessageDialog(this, sp, "Errores en la importación", JOptionPane.WARNING_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al importar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public List<ImportError> importBooks(InputStream csvStream) throws IOException {
        List<ImportError> errors = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(csvStream))) {
            String line;
            int lineNumber = 0;

            // leer encabezado
            line = br.readLine();
            lineNumber++;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                try {
                    String[] parts = line.split(";", -1);
                    if (parts.length < 7) {
                        throw new RuntimeException("Formato inválido, se esperaban 7 columnas.");
                    }

                    String title = parts[0].trim();
                    String authorName = parts[1].trim();
                    String nationality = parts[2].trim();
                    String biography = parts[3].trim();
                    String categoryCode = parts[4].trim();
                    String isbn = parts[5].trim();
                    String synopsis = parts[6].trim();

                    Author author = authorRepo.findByAuthorIgnoreCase(authorName)
                            .orElseGet(() -> {
                                Author a = new Author();
                                a.setAuthor(authorName);
                                a.setNationality(nationality);
                                a.setBiography(biography);
                                return authorRepo.save(a);
                            });

                    Category category = categoryRepo.findByCode(categoryCode)
                            .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + categoryCode));

                    if (bookRepo.findByIsbn(isbn).isPresent()) {
                        throw new RuntimeException("ISBN ya existe: " + isbn);
                    }

                    Book b = new Book();
                    b.setTitle(title);
                    b.setSynopsis(synopsis);
                    b.setIsbn(isbn);
                    b.setIdCategory(category);
                    b.setIdAuthor(author);
                    bookRepo.save(b);

                } catch (Exception ex) {
                    errors.add(new ImportError(lineNumber, line, ex.getMessage()));
                }
            }
        }

        return errors;
    }
}

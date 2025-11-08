package org.dmgarcia.app.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ImportErrorTableModel extends AbstractTableModel {

    private final String[] columns = {"Línea", "Contenido CSV", "Error"};
    private final List<ImportError> data;

    public ImportErrorTableModel(List<ImportError> data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ImportError err = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> err.getLineNumber();
            case 1 -> err.getRawLine();
            case 2 -> err.getMessage();
            default -> "";
        };
    }
}

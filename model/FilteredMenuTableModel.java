package model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class FilteredMenuTableModel extends AbstractTableModel {
    private Menu menu;
    private String kategori;
    private final String[] columnNames;
    private ArrayList<MenuItem> filteredItems;

    public FilteredMenuTableModel(Menu menu, String kategori) {
        this.menu = menu;
        this.kategori = kategori;
        if (kategori.equalsIgnoreCase("Diskon")) {
            this.columnNames = new String[] { "Nama Diskon", "Potongan" };
        } else if (kategori.equalsIgnoreCase("Makanan")) {
            this.columnNames = new String[] { "Nama Makanan", "Harga", "Jenis Makanan" };
        } else {
            this.columnNames = new String[] { "Nama Minuman", "Harga", "Jenis Minuman" };
        }
        this.filteredItems = new ArrayList<>();
        refresh();
    }

    public void refresh() {
        filteredItems.clear();
        for (MenuItem item : menu.getDaftarMenu()) {
            if (item.getKategori().equalsIgnoreCase(kategori)) {
                filteredItems.add(item);
            }
        }
        fireTableDataChanged();
    }

    public MenuItem getItemAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < filteredItems.size()) {
            return filteredItems.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return filteredItems.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MenuItem item = filteredItems.get(rowIndex);
        if (kategori.equalsIgnoreCase("Diskon")) {
            Diskon d = (Diskon) item;
            switch (columnIndex) {
                case 0:
                    return d.getNama();
                case 1:
                    return d.isPersentase() ? d.getDiskon() + "%" : "Rp" + d.getDiskon();
                default:
                    return null;
            }
        } else if (kategori.equalsIgnoreCase("Makanan")) {
            Makanan m = (Makanan) item;
            switch (columnIndex) {
                case 0:
                    return m.getNama();
                case 1:
                    return String.format("Rp%.2f", m.getHarga());
                case 2:
                    return m.getJenisMakanan();
                default:
                    return null;
            }
        } else {
            Minuman min = (Minuman) item;
            switch (columnIndex) {
                case 0:
                    return min.getNama();
                case 1:
                    return String.format("Rp%.2f", min.getHarga());
                case 2:
                    return min.getJenisMinuman();
                default:
                    return null;
            }
        }
    }
}

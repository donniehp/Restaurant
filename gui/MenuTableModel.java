package restaurant.gui;

import restaurant.model.Makanan;
import restaurant.model.Minuman;
import restaurant.model.Diskon;
import restaurant.model.MenuItem;
import restaurant.model.Menu;

import javax.swing.table.AbstractTableModel;

public class MenuTableModel extends AbstractTableModel {
    private Menu menu;
    private final String[] columnNames = {"Nama", "Harga", "Kategori", "Detail"};

    public MenuTableModel(Menu menu) {
        this.menu = menu;
    }

    @Override
    public int getRowCount() {
        return menu.getDaftarMenu().size();
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
        MenuItem item = menu.getDaftarMenu().get(rowIndex);
        switch (columnIndex) {
            case 0: return item.getNama();
            case 1: return item instanceof Diskon ? "-" : String.format("Rp%.2f", item.getHarga());
            case 2: return item.getKategori();
            case 3:
                if (item instanceof Makanan) return ((Makanan) item).getJenisMakanan();
                if (item instanceof Minuman) return ((Minuman) item).getJenisMinuman();
                if (item instanceof Diskon) {
                    Diskon d = (Diskon) item;
                    return d.isPersentase() ? d.getDiskon() + "%" : "Rp" + d.getDiskon();
                }
                return "";
            default: return null;
        }
    }
}
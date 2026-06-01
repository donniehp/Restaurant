package restaurant.gui;

import restaurant.model.*;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.*;

public class MenuPanel extends JPanel {
    private Menu menu;
    private JTable tableMenu;
    private MenuTableModel tableModel;

    private JTextField txtNama, txtHarga, txtExtra;
    private JComboBox<String> comboTipe;
    private JRadioButton rbPersen, rbNominal;
    private ButtonGroup bgTipeDiskon;
    private JLabel lblExtra;

    public MenuPanel(Menu menu) {
        this.menu = menu;
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Tambah Item Baru"));

        inputPanel.add(new JLabel("Tipe:"));
        comboTipe = new JComboBox<>(new String[]{"Makanan", "Minuman", "Diskon"});
        inputPanel.add(comboTipe);

        inputPanel.add(new JLabel("Nama:"));
        txtNama = new JTextField(15);
        inputPanel.add(txtNama);

        inputPanel.add(new JLabel("Harga:"));
        txtHarga = new JTextField(15);
        inputPanel.add(txtHarga);

        lblExtra = new JLabel("Jenis Makanan:");
        inputPanel.add(lblExtra);
        txtExtra = new JTextField(15);
        inputPanel.add(txtExtra);

        JPanel panelRadio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rbPersen = new JRadioButton("Persen", true);
        rbNominal = new JRadioButton("Nominal");
        bgTipeDiskon = new ButtonGroup();
        bgTipeDiskon.add(rbPersen);
        bgTipeDiskon.add(rbNominal);
        panelRadio.add(rbPersen);
        panelRadio.add(rbNominal);
        inputPanel.add(new JLabel("Tipe Diskon:"));
        inputPanel.add(panelRadio);

        JButton btnTambah = new JButton("Tambah Item");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(btnTambah);

        updateInputFields();
        comboTipe.addActionListener(e -> updateInputFields());

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(inputPanel, BorderLayout.CENTER);
        wrapperPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(wrapperPanel, BorderLayout.NORTH);

        tableModel = new MenuTableModel(menu);
        tableMenu = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableMenu);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelTombol = new JPanel();
        JButton btnSimpan = new JButton("Simpan Menu ke File");
        JButton btnHapus = new JButton("Hapus Item Terpilih");

        panelTombol.add(btnSimpan);
        panelTombol.add(btnHapus);
        add(panelTombol, BorderLayout.SOUTH);

        btnTambah.addActionListener(e -> tambahItem());
        btnSimpan.addActionListener(e -> menu.simpanKeFolder("restaurant/data"));
        btnHapus.addActionListener(e -> hapusItem());
    }

    private void updateInputFields() {
        String tipe = (String) comboTipe.getSelectedItem();
        if ("Makanan".equals(tipe)) {
            lblExtra.setText("Jenis Makanan:");
            txtHarga.setEnabled(true);
            rbPersen.setEnabled(false);
            rbNominal.setEnabled(false);
        } else if ("Minuman".equals(tipe)) {
            lblExtra.setText("Jenis Minuman:");
            txtHarga.setEnabled(true);
            rbPersen.setEnabled(false);
            rbNominal.setEnabled(false);
        } else {
            lblExtra.setText("Nilai Diskon:");
            txtHarga.setEnabled(false);
            txtHarga.setText("0");
            rbPersen.setEnabled(true);
            rbNominal.setEnabled(true);
        }
    }

    private void tambahItem() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipe = (String) comboTipe.getSelectedItem();
        try {
            switch (tipe) {
                case "Makanan":
                    double hargaMakanan = Double.parseDouble(txtHarga.getText());
                    String jenisMakanan = txtExtra.getText().trim();
                    if (jenisMakanan.isEmpty()) throw new IllegalArgumentException("Jenis makanan harus diisi.");
                    menu.tambahItem(new Makanan(nama, hargaMakanan, jenisMakanan));
                    break;
                case "Minuman":
                    double hargaMinuman = Double.parseDouble(txtHarga.getText());
                    String jenisMinuman = txtExtra.getText().trim();
                    if (jenisMinuman.isEmpty()) throw new IllegalArgumentException("Jenis minuman harus diisi.");
                    menu.tambahItem(new Minuman(nama, hargaMinuman, jenisMinuman));
                    break;
                case "Diskon":
                    double nilaiDiskon = Double.parseDouble(txtExtra.getText());
                    boolean isPersen = rbPersen.isSelected();
                    menu.tambahItem(new Diskon(nama, nilaiDiskon, isPersen));
                    break;
            }
            tableModel.fireTableDataChanged();
            clearInputFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input angka tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusItem() {
        int selectedRow = tableMenu.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang akan dihapus!");
            return;
        }
        menu.hapusItem(selectedRow);
        tableModel.fireTableDataChanged();
    }

    private void clearInputFields() {
        txtNama.setText("");
        txtHarga.setText("");
        txtExtra.setText("");
    }
}
package gui;

import model.MenuItem;
import model.Menu;
import model.Pesanan;
import model.Diskon;
import model.FilteredMenuTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PesananPanel extends JPanel {
    private Menu menu;
    private Pesanan pesanan;

    // POS Menu Selector UI
    private JTabbedPane tabbedMenu;
    private JTable tableMakanan;
    private JTable tableMinuman;
    private JTable tableDiskon;

    private FilteredMenuTableModel modelMakanan;
    private FilteredMenuTableModel modelMinuman;
    private FilteredMenuTableModel modelDiskon;

    private JSpinner spinnerQty;

    // Active Order UI
    private DefaultListModel<String> listModelPesanan;
    private JList<String> listPesanan;
    private JTextArea textAreaStruk;
    private double lastPembayaran = 0;
    // private JLabel lblTotal;

    public PesananPanel(Menu menu, Pesanan pesanan) {
        this.menu = menu;
        this.pesanan = pesanan;
        setLayout(new BorderLayout());

        // --- LEFT SIDE: Menu Selection ---
        JPanel panelMenuSelection = new JPanel(new BorderLayout());
        panelMenuSelection.setBorder(BorderFactory.createTitledBorder("Pilih Menu"));

        tabbedMenu = new JTabbedPane();

        modelMakanan = new FilteredMenuTableModel(menu, "Makanan");
        tableMakanan = new JTable(modelMakanan);
        tableMakanan.getColumnModel().getColumn(0).setPreferredWidth(180);
        tableMakanan.getColumnModel().getColumn(1).setPreferredWidth(80);
        tableMakanan.getColumnModel().getColumn(2).setPreferredWidth(100);
        JScrollPane scrollMakanan = new JScrollPane(tableMakanan);
        tabbedMenu.addTab("Makanan", scrollMakanan);

        modelMinuman = new FilteredMenuTableModel(menu, "Minuman");
        tableMinuman = new JTable(modelMinuman);
        tableMinuman.getColumnModel().getColumn(0).setPreferredWidth(180);
        tableMinuman.getColumnModel().getColumn(1).setPreferredWidth(80);
        tableMinuman.getColumnModel().getColumn(2).setPreferredWidth(100);
        JScrollPane scrollMinuman = new JScrollPane(tableMinuman);
        tabbedMenu.addTab("Minuman", scrollMinuman);

        modelDiskon = new FilteredMenuTableModel(menu, "Diskon");
        tableDiskon = new JTable(modelDiskon);
        tableDiskon.getColumnModel().getColumn(0).setPreferredWidth(200);
        tableDiskon.getColumnModel().getColumn(1).setPreferredWidth(100);
        JScrollPane scrollDiskon = new JScrollPane(tableDiskon);
        tabbedMenu.addTab("Diskon", scrollDiskon);

        panelMenuSelection.add(tabbedMenu, BorderLayout.CENTER);

        // Menu selection control bar
        JPanel panelMenuControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel lblQty = new JLabel("Qty:");
        lblQty.setFont(new Font("Arial", Font.BOLD, 14));
        panelMenuControls.add(lblQty);

        SpinnerModel modelQty = new SpinnerNumberModel(1, 1, 100, 1);
        spinnerQty = new JSpinner(modelQty);
        spinnerQty.setPreferredSize(new Dimension(70, 30));
        spinnerQty.setFont(new Font("Arial", Font.BOLD, 14));

        // Style the input field of the spinner
        JComponent editor = spinnerQty.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setFont(new Font("Arial", Font.BOLD, 14));
        }
        panelMenuControls.add(spinnerQty);

        JButton btnTambahPesanan = new JButton("Tambah ke Pesanan");
        btnTambahPesanan.setFont(new Font("Arial", Font.BOLD, 14));
        btnTambahPesanan.setPreferredSize(new Dimension(180, 30));
        panelMenuControls.add(btnTambahPesanan);
        panelMenuSelection.add(panelMenuControls, BorderLayout.SOUTH);

        // --- RIGHT SIDE: Order List & Receipt ---
        JPanel panelOrderReceipt = new JPanel(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        listModelPesanan = new DefaultListModel<>();
        listPesanan = new JList<>(listModelPesanan);
        JScrollPane scrollKiri = new JScrollPane(listPesanan);
        scrollKiri.setBorder(BorderFactory.createTitledBorder("Pesanan Saat Ini"));
        splitPane.setLeftComponent(scrollKiri);

        textAreaStruk = new JTextArea(10, 30);
        textAreaStruk.setEditable(false);
        textAreaStruk.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        textAreaStruk.setBackground(new Color(252, 252, 248));
        textAreaStruk.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane scrollKanan = new JScrollPane(textAreaStruk);
        scrollKanan.setBorder(BorderFactory.createTitledBorder("Struk / Riwayat"));
        splitPane.setRightComponent(scrollKanan);

        splitPane.setResizeWeight(0.2);
        splitPane.setDividerLocation(0.25);
        panelOrderReceipt.add(splitPane, BorderLayout.CENTER);

        // --- SOUTH PANEL: Order Actions ---
        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnHapusPesanan = new JButton("Hapus Pesanan Terpilih");
        JButton btnResetPesanan = new JButton("Reset Pesanan");
        JButton btnHitungTotal = new JButton("Hitung Total");
        JButton btnPembayaran = new JButton("Pembayaran");
        JButton btnSimpanStruk = new JButton("Simpan Struk");
        JButton btnCetakStruk = new JButton("Cetak Struk");
        JButton btnLihatRiwayat = new JButton("Lihat Riwayat");
        // lblTotal = new JLabel("Total: Rp0.00");
        // lblTotal.setFont(new Font("Arial", Font.BOLD, 14));

        panelBawah.add(btnHapusPesanan);
        panelBawah.add(btnResetPesanan);
        panelBawah.add(btnHitungTotal);
        panelBawah.add(btnPembayaran);
        panelBawah.add(btnSimpanStruk);
        panelBawah.add(btnCetakStruk);
        panelBawah.add(btnLihatRiwayat);
        // panelBawah.add(lblTotal);
        panelOrderReceipt.add(panelBawah, BorderLayout.SOUTH);

        // --- MAIN SPLIT PANE ---
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelMenuSelection, panelOrderReceipt);
        mainSplit.setDividerLocation(380);
        add(mainSplit, BorderLayout.CENTER);

        // --- EVENT LISTENERS ---
        btnTambahPesanan.addActionListener(e -> tambahPesanan());
        btnHapusPesanan.addActionListener(e -> hapusPesanan());
        btnResetPesanan.addActionListener(e -> {
            pesanan.reset();
            refreshPesananList();
            textAreaStruk.setText("");
            lastPembayaran = 0;
            // lblTotal.setText("Total: Rp0.00");
            spinnerQty.setValue(1);
        });
        btnHitungTotal.addActionListener(e -> hitungDanTampilkanTotal());
        btnSimpanStruk.addActionListener(e -> simpanStruk());
        btnCetakStruk.addActionListener(e -> cetakStrukKePrinter());
        btnLihatRiwayat.addActionListener(e -> lihatRiwayat());
        btnPembayaran.addActionListener(e -> pembayaran());
        // Component Shown Listener to automatically refresh Menu JTables when switching
        // tabs
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshMenuTables();
            }
        });

        // Initialize display
        refreshMenuTables();
        refreshPesananList();
    }

    private void refreshMenuTables() {
        modelMakanan.refresh();
        modelMinuman.refresh();
        modelDiskon.refresh();
    }

    private void refreshPesananList() {
        listModelPesanan.clear();
        for (MenuItem item : pesanan.getItemDipesan()) {
            if (item instanceof Diskon) {
                listModelPesanan.addElement(item.tampilMenu());
            } else {
                listModelPesanan.addElement(String.format("%s (x%d)", item.tampilMenu(), item.getQuantity()));
            }
        }
    }

    private void tambahPesanan() {
        int activeTab = tabbedMenu.getSelectedIndex();
        MenuItem itemDipilih = null;
        if (activeTab == 0) { // Makanan
            int row = tableMakanan.getSelectedRow();
            if (row != -1) {
                itemDipilih = modelMakanan.getItemAt(row);
            }
        } else if (activeTab == 1) { // Minuman
            int row = tableMinuman.getSelectedRow();
            if (row != -1) {
                itemDipilih = modelMinuman.getItemAt(row);
            }
        } else if (activeTab == 2) { // Diskon
            int row = tableDiskon.getSelectedRow();
            if (row != -1) {
                itemDipilih = modelDiskon.getItemAt(row);
            }
        }

        if (itemDipilih == null) {
            JOptionPane.showMessageDialog(this, "Silakan pilih item dari tabel terlebih dahulu.");
            return;
        }

        int qty = (int) spinnerQty.getValue();
        MenuItem copy = menu.copyItem(itemDipilih);
        pesanan.tambahItem(copy, qty);
        refreshPesananList();
        spinnerQty.setValue(1);

        // Clear selection to prevent accidental double additions
        tableMakanan.clearSelection();
        tableMinuman.clearSelection();
        tableDiskon.clearSelection();
    }

    private void hapusPesanan() {
        int index = listPesanan.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan yang akan dihapus.");
            return;
        }
        pesanan.hapusItem(index);
        refreshPesananList();
    }

    private void hitungDanTampilkanTotal() {
        if (pesanan.getItemDipesan().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pesanan kosong!");
            return;
        }

        String struk = pesanan.generateStruk();
        textAreaStruk.setText(struk);
    }

    private void pembayaran() {
        if (pesanan.getItemDipesan().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pesanan kosong! Tidak dapat melakukan pembayaran.");
            return;
        }

        // Hitung total dulu untuk mengetahui minimum pembayaran
        double totalBayar = pesanan.hitungTotalBayar();

        // Dialog input pembayaran cash
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.add(new JLabel(String.format(
                "Total Bayar: Rp %s  —  Masukkan nominal uang cash:",
                pesanan.formatRupiahPublic(totalBayar))));
        JTextField tfCash = new JTextField();
        tfCash.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        panel.add(tfCash);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Pembayaran Cash",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        String input = tfCash.getText().trim().replaceAll("[^0-9]", "");
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nominal pembayaran tidak valid!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double pembayaran = Double.parseDouble(input);
        if (pembayaran < totalBayar) {
            int konfirmasi = JOptionPane.showConfirmDialog(
                    this,
                    String.format("Uang cash (Rp %s) kurang dari total (Rp %s).\nTetap cetak struk?",
                            pesanan.formatRupiahPublic(pembayaran),
                            pesanan.formatRupiahPublic(totalBayar)),
                    "Uang Kurang",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (konfirmasi != JOptionPane.YES_OPTION) return;
        }

        lastPembayaran = pembayaran;
        String struk = pesanan.generateStruk(pembayaran);
        textAreaStruk.setText(struk);
    }

    private void simpanStruk() {
        if (pesanan.getItemDipesan().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pesanan kosong! Tidak dapat menyimpan struk.");
            return;
        }
        // Pastikan sudah ada pembayaran, jika belum, minta dulu
        if (lastPembayaran == 0) {
            hitungDanTampilkanTotal();
            if (lastPembayaran == 0) return; // user batal
        }

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("ddMMyyyy-HHmmss");
        String timestamp = sdf.format(new java.util.Date());
        String filename = "Struk_Pesanan_" + timestamp + ".txt";

        pesanan.simpanStruk(filename, lastPembayaran);
        JOptionPane.showMessageDialog(this, "Struk berhasil disimpan dengan nama:\n" + filename, "Sukses",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cetakStrukKePrinter() {
        if (pesanan.getItemDipesan().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pesanan kosong! Tidak dapat mencetak struk.");
            return;
        }
        if (lastPembayaran == 0) {
            hitungDanTampilkanTotal();
            if (lastPembayaran == 0) return;
        }
        try {
            boolean complete = textAreaStruk.print(
                    new java.text.MessageFormat("Struk Restoran"),
                    new java.text.MessageFormat("Halaman {0}"),
                    true,
                    null,
                    null,
                    true);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Pencetakan selesai!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Pencetakan dibatalkan.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.awt.print.PrinterException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lihatRiwayat() {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setDialogTitle("Pilih Struk untuk Dilihat");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToOpen = fileChooser.getSelectedFile();
            String riwayat = pesanan.muatRiwayat(fileToOpen.getAbsolutePath());
            textAreaStruk.setText(riwayat);
        }
    }
}

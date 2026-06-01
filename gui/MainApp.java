package restaurant.gui;

import restaurant.model.Makanan;
import restaurant.model.Minuman;
import restaurant.model.Diskon;
import restaurant.model.Menu;
import restaurant.model.Pesanan;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private Menu menu;
    private Pesanan pesanan;

    public MainApp() {
        setTitle("Manajemen Restoran - NIKMAT LEZAT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        menu = new Menu();
        pesanan = new Pesanan();

        menu.muatDariFolder("data");

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Pemesanan", new PesananPanel(menu, pesanan));
        tabbedPane.addTab("Manajemen Menu", new MenuPanel(menu));

        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 1) { // Index of "Manajemen Menu" tab
                JPasswordField passwordField = new JPasswordField();
                int option = JOptionPane.showConfirmDialog(
                        this,
                        passwordField,
                        "Masukkan Password Admin : admin123",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    String password = new String(passwordField.getPassword());
                    if ("admin123".equals(password)) {
                        return; // Access granted
                    }
                }

                // Access denied or cancelled
                JOptionPane.showMessageDialog(this, "Password salah atau akses dibatalkan!", "Akses Ditolak",
                        JOptionPane.ERROR_MESSAGE);
                SwingUtilities.invokeLater(() -> tabbedPane.setSelectedIndex(0));
            }
        });

        add(tabbedPane);

        JPanel panelBawah = new JPanel();
        JButton btnKeluar = new JButton("Keluar");
        btnKeluar.addActionListener(e -> System.exit(0));
        panelBawah.add(btnKeluar);
        add(panelBawah, BorderLayout.SOUTH);
    }
}
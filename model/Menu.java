package model;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class Menu {
    private ArrayList<MenuItem> daftarMenu;

    public Menu() {
        daftarMenu = new ArrayList<>();
    }

    public void tambahItem(MenuItem item) {
        daftarMenu.add(item);
    }

    public void hapusItem(int index) {
        if (index >= 0 && index < daftarMenu.size()) {
            daftarMenu.remove(index);
        }
    }

    public ArrayList<MenuItem> getDaftarMenu() {
        return daftarMenu;
    }

    public void ubahItem(int index, MenuItem item) {
        if (index >= 0 && index < daftarMenu.size() && item != null) {
            daftarMenu.set(index, item);
        }
    }

    public MenuItem cariItem(String nama) {
        for (MenuItem item : daftarMenu) {
            if (item.getNama().equalsIgnoreCase(nama)) {
                return item;
            }
        }
        return null;
    }

    public MenuItem copyItem(MenuItem asli) {
        if (asli instanceof Makanan) {
            Makanan m = (Makanan) asli;
            return new Makanan(m.getNama(), m.getHarga(), m.getJenisMakanan());
        } else if (asli instanceof Minuman) {
            Minuman min = (Minuman) asli;
            return new Minuman(min.getNama(), min.getHarga(), min.getJenisMinuman());
        } else if (asli instanceof Diskon) {
            Diskon d = (Diskon) asli;
            return new Diskon(d.getNama(), d.getDiskon(), d.isPersentase());
        }
        return null;
    }

    public void simpanKeFile(String namaFile) {
        // Also write combined menu file plus separate files for makanan, minuman, and diskon
        try (
                PrintWriter writer = new PrintWriter(new FileWriter(namaFile));
                PrintWriter writerMakanan = new PrintWriter(new FileWriter("data/makanan.txt"));
                PrintWriter writerMinuman = new PrintWriter(new FileWriter("data/minuman.txt"));
                PrintWriter writerDiskon = new PrintWriter(new FileWriter("data/diskon.txt"));
        ) {
            for (MenuItem item : daftarMenu) {
                if (item instanceof Makanan) {
                    Makanan m = (Makanan) item;
                    // Format: MAKANAN|Nama|Harga|Jenis
                    String line = "MAKANAN|" + m.getNama() + "|" + m.getHarga() + "|" + m.getJenisMakanan();
                    writer.println(line);
                    writerMakanan.println(line);
                } else if (item instanceof Minuman) {
                    Minuman min = (Minuman) item;
                    // Format: MINUMAN|Nama|Harga|Jenis
                    String line = "MINUMAN|" + min.getNama() + "|" + min.getHarga() + "|" + min.getJenisMinuman();
                    writer.println(line);
                    writerMinuman.println(line);
                } else if (item instanceof Diskon) {
                    Diskon d = (Diskon) item;
                    // Format: DISKON|Nama|Nilai|PERSEN|NOMINAL
                    String tipe = d.isPersentase() ? "PERSEN" : "NOMINAL";
                    String line = "DISKON|" + d.getNama() + "|" + d.getDiskon() + "|" + tipe;
                    writer.println(line);
                    writerDiskon.println(line);
                }
            }
            JOptionPane.showMessageDialog(null, "Menu berhasil disimpan ke " + namaFile + " dan file terpisah: makanan.txt, minuman.txt, diskon.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan menu: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void simpanKeFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (
                PrintWriter writerMakanan = new PrintWriter(new FileWriter(new File(folder, "makanan.txt")));
                PrintWriter writerMinuman = new PrintWriter(new FileWriter(new File(folder, "minuman.txt")));
                PrintWriter writerDiskon = new PrintWriter(new FileWriter(new File(folder, "diskon.txt")))
        ) {
            for (MenuItem item : daftarMenu) {
                if (item instanceof Makanan) {
                    Makanan m = (Makanan) item;
                    String line = "MAKANAN|" + m.getNama() + "|" + m.getHarga() + "|" + m.getJenisMakanan();
                    writerMakanan.println(line);
                } else if (item instanceof Minuman) {
                    Minuman min = (Minuman) item;
                    String line = "MINUMAN|" + min.getNama() + "|" + min.getHarga() + "|" + min.getJenisMinuman();
                    writerMinuman.println(line);
                } else if (item instanceof Diskon) {
                    Diskon d = (Diskon) item;
                    String tipe = d.isPersentase() ? "PERSEN" : "NOMINAL";
                    String line = "DISKON|" + d.getNama() + "|" + d.getDiskon() + "|" + tipe;
                    writerDiskon.println(line);
                }
            }
            JOptionPane.showMessageDialog(null, "Menu berhasil disimpan ke folder " + folderPath + " sebagai makanan.txt, minuman.txt, dan diskon.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan menu ke folder: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void muatDariFile(String namaFile) {
        File file = new File(namaFile);
        if (!file.exists()) {
            return;
        }
        daftarMenu.clear();
        bacaMenuDariFile(file);
    }

    public void muatDariFolder(String folderPath) {
        daftarMenu.clear();

        File makananFile = new File(folderPath, "makanan.txt");
        File minumanFile = new File(folderPath, "minuman.txt");
        File diskonFile = new File(folderPath, "diskon.txt");

        ArrayList<String> missingFiles = new ArrayList<>();
        if (!makananFile.exists()) {
            missingFiles.add(makananFile.getPath());
        }
        if (!minumanFile.exists()) {
            missingFiles.add(minumanFile.getPath());
        }
        if (!diskonFile.exists()) {
            missingFiles.add(diskonFile.getPath());
        }

        if (!missingFiles.isEmpty()) {
            StringBuilder message = new StringBuilder("File data berikut tidak ditemukan:\n");
            for (String missingFile : missingFiles) {
                message.append("- ").append(missingFile).append("\n");
            }
            message.append("\nPastikan file makanan.txt, minuman.txt, dan diskon.txt tersedia di folder ")
                    .append(folderPath)
                    .append(".");
            JOptionPane.showMessageDialog(null, message.toString(), "File Data Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
        }

        bacaMenuDariFile(makananFile);
        bacaMenuDariFile(minumanFile);
        bacaMenuDariFile(diskonFile);

        if (daftarMenu.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Data ditemukan tetapi tidak ada item yang dimuat. Periksa isi file makanan.txt, minuman.txt, dan diskon.txt.",
                    "Data Kosong",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void bacaMenuDariFile(File file) {
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String baris;
            while ((baris = reader.readLine()) != null) {
                // Support both ';' and '|' delimiters for compatibility
                String[] data = baris.split("[;\\|]");
                if (data.length < 4) continue;
                String tipe = data[0].trim();
                String nama = data[1].trim();
                try {
                    if (tipe.equalsIgnoreCase("MAKANAN")) {
                        double harga = Double.parseDouble(data[2].trim());
                        String jenis = data[3].trim();
                        daftarMenu.add(new Makanan(nama, harga, jenis));
                    } else if (tipe.equalsIgnoreCase("MINUMAN")) {
                        double harga = Double.parseDouble(data[2].trim());
                        String jenis = data[3].trim();
                        daftarMenu.add(new Minuman(nama, harga, jenis));
                    } else if (tipe.equalsIgnoreCase("DISKON")) {
                        double diskon = Double.parseDouble(data[2].trim());
                        boolean isPersen = data[3].trim().equalsIgnoreCase("PERSEN");
                        daftarMenu.add(new Diskon(nama, diskon, isPersen));
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Format angka salah pada baris: " + baris);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Gagal memuat menu: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
